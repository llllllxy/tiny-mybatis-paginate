package org.tinycloud.paginate.utils;

import org.tinycloud.paginate.dialect.Dialect;
import org.tinycloud.paginate.dialect.DialectEnum;
import org.tinycloud.paginate.exception.PaginateException;
import org.apache.ibatis.mapping.MappedStatement;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class DialectUtils {
    private DialectUtils() {
    }

    /**
     * 动态获取方言实例
     *
     * @param dialect   方言实现类
     * @param statement mappedStatement对象
     * @return 获取数据库方言
     */
    public static Dialect newInstance(MappedStatement statement, String dialect) {
        try {
            // 如果没有传递枚举参数
            // 则自动根据数据库
            if (dialect == null) {
                return getDialectEnum(statement).getValue().newInstance();
            }
            // 反射获取方言实现实例
            return (Dialect) DialectEnum.getDialect(dialect).newInstance();
        }
        // 遇到异常后抛出方言暂未支持异常
        catch (Exception e) {
            throw new PaginateException("数据库方言暂未支持!");
        }
    }

    /**
     * 获取当前配置的 DbType
     */
    public static DialectEnum getDialectEnum(MappedStatement statement) {
        // 获取数据源
        DataSource dataSource = statement.getConfiguration().getEnvironment().getDataSource();
        String jdbcUrl = getJdbcUrl(dataSource);
        if (!StrUtils.isEmpty(jdbcUrl)) {
            return parseDialectEnum(jdbcUrl);
        }
        throw new IllegalStateException("Can not get dataSource jdbcUrl: " + dataSource.getClass().getName());
    }


    /**
     * 获取当前配置的 DbType
     */
    public static DialectEnum getDialectEnum(DataSource dataSource) {
        String jdbcUrl = getJdbcUrl(dataSource);
        if (!StrUtils.isEmpty(jdbcUrl)) {
            return parseDialectEnum(jdbcUrl);
        }
        throw new IllegalStateException("Can not get dataSource jdbcUrl: " + dataSource.getClass().getName());
    }

    /**
     * 通过数据源中获取 jdbc 的 url 配置
     * 符合 HikariCP, druid, c3p0, DBCP, BEECP 数据源框架 以及 MyBatis UnpooledDataSource 的获取规则
     *
     * @return jdbc url 配置
     */
    public static String getJdbcUrl(DataSource dataSource) {
        String[] methodNames = new String[]{"getUrl", "getJdbcUrl"};
        for (String methodName : methodNames) {
            try {
                Method method = dataSource.getClass().getMethod(methodName);
                return (String) method.invoke(dataSource);
            } catch (Exception e) {
                //ignore
            }
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return connection.getMetaData().getURL();
        } catch (Exception e) {
            throw new PaginateException("Can not get the dataSource jdbcUrl!");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { //ignore
                }
            }
        }
    }


    /**
     * 参考 druid  和 MyBatis-plus 的 JdbcUtils
     *
     * @param jdbcUrl jdbcURL
     * @return 返回数据库类型
     */
    public static DialectEnum parseDialectEnum(String jdbcUrl) {
        jdbcUrl = jdbcUrl.toLowerCase();
        if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return DialectEnum.MYSQL;
        } else if (jdbcUrl.contains(":mariadb:")) {
            return DialectEnum.MARIADB;
        } else if (jdbcUrl.contains(":oracle:")) {
            return DialectEnum.ORACLE;
        } else if (jdbcUrl.contains(":sqlserver2012:")) {
            return DialectEnum.SQLSERVER;
        } else if (jdbcUrl.contains(":sqlserver:") || jdbcUrl.contains(":microsoft:")) {
            return DialectEnum.SQLSERVER_2005;
        } else if (jdbcUrl.contains(":postgresql:")) {
            return DialectEnum.POSTGRE_SQL;
        } else if (jdbcUrl.contains(":hsqldb:")) {
            return DialectEnum.HSQL;
        } else if (jdbcUrl.contains(":db2:")) {
            return DialectEnum.DB2;
        } else if (jdbcUrl.contains(":sqlite:")) {
            return DialectEnum.SQLITE;
        } else if (jdbcUrl.contains(":h2:")) {
            return DialectEnum.H2;
        } else if (isMatchedRegex(":dm\\d*:", jdbcUrl)) {
            return DialectEnum.DM;
        } else if (jdbcUrl.contains(":xugu:")) {
            return DialectEnum.XUGU;
        } else if (isMatchedRegex(":kingbase\\d*:", jdbcUrl)) {
            return DialectEnum.KINGBASE_ES;
        } else if (jdbcUrl.contains(":phoenix:")) {
            return DialectEnum.PHOENIX;
        } else if (jdbcUrl.contains(":zenith:")) {
            return DialectEnum.GAUSS;
        } else if (jdbcUrl.contains(":gbase:")) {
            return DialectEnum.GBASE;
        } else if (jdbcUrl.contains(":gbasedbt-sqli:") || jdbcUrl.contains(":informix-sqli:")) {
            return DialectEnum.GBASE_8S;
        } else if (jdbcUrl.contains(":ch:") || jdbcUrl.contains(":clickhouse:")) {
            return DialectEnum.CLICK_HOUSE;
        } else if (jdbcUrl.contains(":oscar:")) {
            return DialectEnum.OSCAR;
        } else if (jdbcUrl.contains(":sybase:")) {
            return DialectEnum.SYBASE;
        } else if (jdbcUrl.contains(":oceanbase:")) {
            return DialectEnum.OCEAN_BASE;
        } else if (jdbcUrl.contains(":highgo:")) {
            return DialectEnum.HIGH_GO;
        } else if (jdbcUrl.contains(":cubrid:")) {
            return DialectEnum.CUBRID;
        } else if (jdbcUrl.contains(":goldilocks:")) {
            return DialectEnum.GOLDILOCKS;
        } else if (jdbcUrl.contains(":csiidb:")) {
            return DialectEnum.CSIIDB;
        } else if (jdbcUrl.contains(":sap:")) {
            return DialectEnum.SAP_HANA;
        } else if (jdbcUrl.contains(":impala:")) {
            return DialectEnum.IMPALA;
        } else if (jdbcUrl.contains(":vertica:")) {
            return DialectEnum.VERTICA;
        } else if (jdbcUrl.contains(":xcloud:")) {
            return DialectEnum.XCloud;
        } else if (jdbcUrl.contains(":firebirdsql:")) {
            return DialectEnum.FIREBIRD;
        } else if (jdbcUrl.contains(":redshift:")) {
            return DialectEnum.REDSHIFT;
        } else if (jdbcUrl.contains(":opengauss:")) {
            return DialectEnum.OPENGAUSS;
        } else if (jdbcUrl.contains(":taos:") || jdbcUrl.contains(":taos-rs:")) {
            return DialectEnum.TDENGINE;
        } else if (jdbcUrl.contains(":informix")) {
            return DialectEnum.INFORMIX;
        } else if (jdbcUrl.contains(":sinodb")) {
            return DialectEnum.SINODB;
        } else if (jdbcUrl.contains(":uxdb:")) {
            return DialectEnum.UXDB;
        } else if (jdbcUrl.contains(":greenplum:")) {
            return DialectEnum.GREENPLUM;
        } else {
            return DialectEnum.OTHER;
        }
    }

    /**
     * 正则匹配，验证成功返回 true，验证失败返回 false
     */
    public static boolean isMatchedRegex(String regex, String jdbcUrl) {
        if (null == jdbcUrl) {
            return false;
        }
        return Pattern.compile(regex).matcher(jdbcUrl).find();
    }
}
