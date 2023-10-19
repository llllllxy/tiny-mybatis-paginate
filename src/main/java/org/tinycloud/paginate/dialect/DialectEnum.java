package org.tinycloud.paginate.dialect;

import org.tinycloud.paginate.dialect.support.*;

/**
 * <p>
 * 数据库方言枚举，配合DialectUtils工具类可快速映射对应的方言实现类
 * </p>
 *
 * @author liuxingyu01
 * @since 2023-10-18
 **/
public enum DialectEnum {
    /**
     * MYSQL
     */
    MYSQL("mysql", MySqlDialect.class),
    /**
     * MARIADB
     */
    MARIADB("mariadb", MySqlDialect.class),
    /**
     * ORACLE
     */
    ORACLE("oracle", OracleDialect.class),
    /**
     * oracle12c
     */
    ORACLE_12C("oracle12c", OracleDialect.class),
    /**
     * DB2
     */
    DB2("db2", Db2Dialect.class),
    /**
     * H2
     */
    H2("h2", HsqlDialect.class),
    /**
     * HSQL
     */
    HSQL("hsql", HsqlDialect.class),
    /**
     * SQLITE
     */
    SQLITE("sqlite", SqlLiteDialect.class),
    /**
     * POSTGRE
     */
    POSTGRE_SQL("postgresql", MySqlDialect.class),
    /**
     * SQLSERVER
     */
    SQLSERVER("sqlserver", SqlServerDialect.class),
    /**
     * SqlServer 2005 数据库
     */
    SQLSERVER_2005("sqlserver_2005", OracleDialect.class),
    /**
     * DM
     */
    DM("dm", OracleDialect.class),
    /**
     * xugu
     */
    XUGU("xugu", HsqlDialect.class),
    /**
     * Kingbase
     */
    KINGBASE_ES("kingbasees", MySqlDialect.class),
    /**
     * Phoenix
     */
    PHOENIX("phoenix", HsqlDialect.class),
    /**
     * Gauss
     */
    GAUSS("gauss", MySqlDialect.class),
    /**
     * ClickHouse
     */
    CLICK_HOUSE("clickhouse", MySqlDialect.class),
    /**
     * GBase
     */
    GBASE("gbase", MySqlDialect.class),
    /**
     * GBase-8s
     */
    GBASE_8S("gbase-8s", MySqlDialect.class),
    /**
     * Oscar
     */
    OSCAR("oscar", MySqlDialect.class),
    /**
     * Sybase
     */
    SYBASE("sybase", MySqlDialect.class),
    /**
     * OceanBase
     */
    OCEAN_BASE("oceanbase", MySqlDialect.class),
    /**
     * Firebird
     */
    FIREBIRD("Firebird", FirebirdDialect.class),
    /**
     * derby
     */
    DERBY("derby", MySqlDialect.class),
    /**
     * HighGo
     */
    HIGH_GO("highgo", HsqlDialect.class),
    /**
     * CUBRID
     */
    CUBRID("cubrid", MySqlDialect.class),

    /**
     * GOLDILOCKS
     */
    GOLDILOCKS("goldilocks", MySqlDialect.class),
    /**
     * CSIIDB
     */
    CSIIDB("csiidb", MySqlDialect.class),
    /**
     * CSIIDB
     */
    SAP_HANA("hana", MySqlDialect.class),
    /**
     * Impala
     */
    IMPALA("impala", HsqlDialect.class),
    /**
     * Vertica
     */
    VERTICA("vertica", MySqlDialect.class),
    /**
     * 东方国信 xcloud
     */
    XCloud("xcloud", MySqlDialect.class),
    /**
     * redshift
     */
    REDSHIFT("redshift", MySqlDialect.class),
    /**
     * openGauss
     */
    OPENGAUSS("openGauss", MySqlDialect.class),
    /**
     * TDengine
     */
    TDENGINE("TDengine", MySqlDialect.class),
    /**
     * Informix
     */
    INFORMIX("informix", InforMixDialect.class),
    /**
     * sinodb
     */
    SINODB("sinodb", MySqlDialect.class),
    /**
     * uxdb
     */
    UXDB("uxdb", MySqlDialect.class),
    /**
     * greenplum
     */
    GREENPLUM("greenplum", PostgresDialect.class),
    /**
     * UNKNOWN DB
     */
    OTHER("other", MySqlDialect.class);

    /**
     * 数据库名称
     */
    private final String name;

    /**
     * 实现类
     */
    private final Class<? extends Dialect> value;

    public String getName() {
        return name;
    }

    public Class<? extends Dialect> getValue() {
        return value;
    }

    DialectEnum(String name, Class<? extends Dialect> value) {
        this.name = name;
        this.value = value;
    }

    public static Class<? extends Dialect> getDialect(String name) {
        DialectEnum[] enums = values();
        for (DialectEnum item : enums) {
            if (item.name.equals(name)) {
                return item.value;
            }
        }
        return MYSQL.value;
    }
}
