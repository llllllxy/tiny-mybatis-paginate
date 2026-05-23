package org.tinycloud.paginate;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinycloud.paginate.dialect.Dialect;
import org.tinycloud.paginate.utils.DialectUtils;
import org.tinycloud.paginate.utils.PageRequestHolder;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private static final Logger logger = LoggerFactory.getLogger(MyBatisPaginateInterceptor.class);


    /**
     * 项目配置数据库方言的类名全限定名
     */
    private String dialect;

    /**
     * 是否运行时动态识别数据库方言，默认关闭
     */
    private boolean openRuntimeDbType = false;

    /**
     * 缓存数据库方言对象到内存
     */
    private Dialect dialectImpl;

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

        Dialect currentDialect = this.getDialect(statement);
        Page<?> page = PageRequestHolder.getPageLocal();

        // 执行查询总记录数的sql
        Executor executor = (Executor) invocation.getTarget();
        long count = executeCount(boundSql, statement, currentDialect, executor);
        page.setTotal(count);
        if (count <= 0L) {
            List<?> emptyRecords = Collections.emptyList();
            page.setRecords((List) emptyRecords);
            return emptyRecords;
        }

        // 执行分页查询的sql
        Object result = executePage(page, invocation, currentDialect);
        page.setRecords((List) result);

        return result;
    }

    /**
     * 获取当前查询使用的数据库方言
     *
     * @param statement MappedStatement
     * @return 数据库方言实现
     */
    private Dialect getDialect(MappedStatement statement) {
        if (this.openRuntimeDbType) {
            try {
                return DialectUtils.newInstance(statement, null);
            } catch (RuntimeException e) {
                if (this.dialect != null && !this.dialect.isEmpty()) {
                    return DialectUtils.newInstance(statement, this.dialect);
                }
                throw e;
            }
        }
        if (this.dialectImpl == null) {
            this.dialectImpl = DialectUtils.newInstance(statement, this.dialect);
        }
        return this.dialectImpl;
    }

    /**
     * 执行分页查询
     *
     * @param page        分页参数对象
     * @param invocation  Invocation
     * @param dialectImpl 数据库方言实现
     * @return 执行结果
     * @throws Throwable 异常
     */
    private Object executePage(Page<?> page, Invocation invocation, Dialect dialectImpl) throws Throwable {
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameterObject = null;
        if (args.length > 1) {
            parameterObject = args[1];
        }
        BoundSql boundSql = statement.getBoundSql(parameterObject);
        MappedStatement newStatement = newMappedStatement(statement, new BoundSqlSqlSource(boundSql));
        MetaObject msObject = MetaObject.forObject(newStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(),
                new DefaultReflectorFactory());
        String sql = boundSql.getSql().trim();
        // 根据数据库方言，生成对应的分页sql
        String pageSql = dialectImpl.getPageSql(sql, page);
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

    /**
     * 执行总记录数查询
     *
     * @param boundSql        boundSql
     * @param mappedStatement MappedStatement
     * @param dialectImpl     数据库方言实现
     * @param executor        MyBatis执行器
     * @return totalRecord 总记录数
     */
    private long executeCount(BoundSql boundSql, MappedStatement mappedStatement, Dialect dialectImpl, Executor executor) throws SQLException {
        Object parameterObject = boundSql.getParameterObject();
        String sql = boundSql.getSql().trim();
        String countSql = dialectImpl.getCountSql(sql);

        long totalRecord = 0L;
        try {
            Configuration configuration = mappedStatement.getConfiguration();
            BoundSql countBoundSql = newCountBoundSql(configuration, boundSql, countSql);
            MappedStatement countStatement = newCountMappedStatement(mappedStatement);
            CacheKey countKey = executor.createCacheKey(countStatement, parameterObject, RowBounds.DEFAULT, countBoundSql);
            List<Object> countResult = executor.query(countStatement, parameterObject, RowBounds.DEFAULT, null, countKey, countBoundSql);
            if (countResult != null && !countResult.isEmpty()) {
                Object count = countResult.get(0);
                if (count instanceof Number) {
                    totalRecord = ((Number) count).longValue();
                }
            }
        } catch (SQLException e) {
            logger.error(this.getClass().getName() + "executeCount SQLException: of statement " + mappedStatement.getId(), e);
        }
        return totalRecord;
    }

    /**
     * 创建总记录数查询使用的BoundSql
     *
     * @param configuration MyBatis配置对象
     * @param boundSql      原始BoundSql
     * @param countSql      总记录数SQL
     * @return 总记录数查询使用的BoundSql
     */
    @SuppressWarnings("unchecked")
    private BoundSql newCountBoundSql(Configuration configuration, BoundSql boundSql, String countSql) {
        BoundSql countBoundSql = new BoundSql(configuration, countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        MetaObject metaObject = MetaObject.forObject(boundSql, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(),
                new DefaultReflectorFactory());
        Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("additionalParameters");
        for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
            countBoundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
        }
        return countBoundSql;
    }

    /**
     * 创建总记录数查询使用的MappedStatement
     *
     * @param ms 原始MappedStatement
     * @return 总记录数查询使用的MappedStatement
     */
    private MappedStatement newCountMappedStatement(MappedStatement ms) {
        Configuration configuration = ms.getConfiguration();
        ResultMap resultMap = new ResultMap.Builder(configuration, ms.getId() + "_COUNT_RESULT", Long.class,
                Collections.<ResultMapping>emptyList()).build();
        MappedStatement.Builder builder =
                new MappedStatement.Builder(configuration, ms.getId() + "_COUNT", ms.getSqlSource(), SqlCommandType.SELECT);
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(Collections.singletonList(resultMap));
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(false);
        builder.useCache(ms.isUseCache());
        return builder.build();
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
        // 设置是否运行时动态识别数据库方言
        this.openRuntimeDbType = Boolean.parseBoolean(properties.getProperty("openRuntimeDbType", "false"));
    }

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
