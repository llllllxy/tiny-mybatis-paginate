package org.tinycloud.paginate;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.tinycloud.paginate.dialect.Dialect;
import org.tinycloud.paginate.utils.DialectUtils;
import org.tinycloud.paginate.utils.PageRequestHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


/**
 * 封装mybatis的自动化分页拦截器插件
 * 拦截org.apache.ibatis.executor.Executor接口的两个query方法来完成自动化分页
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class MyBatisPaginateInterceptor implements Interceptor {
    /**
     * 项目配置数据库方言的类名全限定名
     */
    private String dialect;

    /**
     * 缓存数据库方言对象到内存
     */
    private Dialect dialectCache;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 默认行绑定不执行分页
        if (PageRequestHolder.getPageLocal() == null) {
            return invocation.proceed();
        }

        // 获取Executor对象query方法的参数列表
        final Object[] args = invocation.getArgs();
        // MappedStatement对象实例
        MappedStatement statement = (MappedStatement) args[0];
        Object parameterObject = null;
        if (args.length > 1) {
            parameterObject = args[1];
        }
        BoundSql boundSql = statement.getBoundSql(parameterObject);

        if (dialectCache == null) {
            dialectCache = DialectUtils.newInstance(statement, this.dialect);
        }
        Page<?> page = PageRequestHolder.getPageLocal();

        int count = executeCount(boundSql, statement);
        page.setTotal(count);

        // 重新执行新的sql
        Object result = executePage(page, invocation);
        page.setRecords((Collection) result);

        return result;
    }


    private Object executePage(Page<?> page, Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = statement.getBoundSql(parameterObject);
        MappedStatement newStatement = newMappedStatement(statement, new BoundSqlSqlSource(boundSql));
        MetaObject msObject = MetaObject.forObject(newStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(),
                new DefaultReflectorFactory());
        String sql = boundSql.getSql().trim();
        String pageSql = dialectCache.getPageSql(sql, page);
        msObject.setValue("sqlSource.boundSql.sql", pageSql);
        args[0] = newStatement;
        return invocation.proceed();
    }

    /**
     * 克隆以获取新的MappedStatement
     *
     * @param ms           旧的MappedStatement
     * @param newSqlSource 新的newSqlSource
     * @return 新的MappedStatement
     */
    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    private int executeCount(BoundSql boundSql, MappedStatement mappedStatement) throws SQLException {
        Object parameterObject = boundSql.getParameterObject();
        String sql = boundSql.getSql().trim();
        String countSql = dialectCache.getCountSql(sql);

        //获取相关配置
        Configuration config = mappedStatement.getConfiguration();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int totalRecord = 0;
        try {
            connection = config.getEnvironment().getDataSource().getConnection();
            preparedStatement = connection.prepareStatement(countSql);
            this.setParameters(preparedStatement, mappedStatement, boundSql, parameterObject);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalRecord = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return totalRecord;
    }


    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);

            for (int i = 0; i < parameterMappings.size(); ++i) {
                ParameterMapping parameterMapping = (ParameterMapping) parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    Object value;
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith("__frch_") && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }

                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        // log.error(this.getClass().getName() + "(177):There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }

                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }


    /**
     * 插件执行方法
     *
     * @param target 目标对象
     * @return 执行后结果
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


    /**
     * 设置插件的自定义属性
     *
     * @param properties 属性对象
     */
    @Override
    public void setProperties(Properties properties) {
        // 设置传递的数据库方言
        this.dialect = properties.getProperty("dialect");
    }


    /**
     * 新的SqlSource需要实现
     */
    class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
