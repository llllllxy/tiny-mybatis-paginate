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
    ORACLE_12C("oracle12c", Oracle12cDialect.class),
    /**
     * DB2
     */
    DB2("db2", Db2Dialect.class),
    /**
     * H2
     */
    H2("h2", PostgresDialect.class),
    /**
     * HSQL
     */
    HSQL("hsql", PostgresDialect.class),
    /**
     * SQLITE
     */
    SQLITE("sqlite", PostgresDialect.class),
    /**
     * POSTGRE
     */
    POSTGRE_SQL("postgresql", PostgresDialect.class),
    /**
     * SQLSERVER
     */
    SQLSERVER("sqlserver", SqlServerDialect.class),
    /**
     * SqlServer 2005 数据库
     */
    SQLSERVER_2005("sqlserver_2005", SqlServer2005Dialect.class),
    /**
     * DM
     */
    DM("dm", OracleDialect.class),
    /**
     * xugu
     */
    XUGU("xugu", MySqlDialect.class),
    /**
     * Kingbase
     */
    KINGBASE_ES("kingbasees", PostgresDialect.class),
    /**
     * Phoenix
     */
    PHOENIX("phoenix", PostgresDialect.class),
    /**
     * Gauss
     */
    GAUSS("gauss", OracleDialect.class),
    /**
     * 华为云GaussDB数据库
     */
    GAUSS_DB("gaussDB", GaussDBDialect.class),
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
    GBASE_8S("gbase-8s", GBase8sDialect.class),
    /**
     * GBase8sPG
     */
    GBASE8S_PG("gbase8s-pg", PostgresDialect.class),
    /**
     * GBase8s
     */
    @Deprecated
    GBASE_INFORMIX("gbase 8s", GBase8sDialect.class),
    /**
     * gbasedbt
     */
    @Deprecated
    GBASEDBT("gbasedbt", GBase8sDialect.class),
    /**
     * GBase8c
     */
    GBASE_8C("gbase-8c", PostgresDialect.class),
    /**
     * Oscar
     */
    OSCAR("oscar", MySqlDialect.class),
    /**
     * Sybase
     */
    SYBASE("sybase", SybaseDialect.class),
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
    DERBY("derby", Oracle12cDialect.class),
    /**
     * HighGo
     */
    HIGH_GO("highgo", PostgresDialect.class),
    /**
     * CUBRID
     */
    CUBRID("cubrid", MySqlDialect.class),
    /**
     * SUNDB
     */
    SUNDB("sundb", MySqlDialect.class),
    /**
     * GOLDILOCKS
     */
    GOLDILOCKS("goldilocks", MySqlDialect.class),
    /**
     * CSIIDB
     */
    CSIIDB("csiidb", MySqlDialect.class),
    /**
     * SAP_HANA
     */
    SAP_HANA("hana", PostgresDialect.class),
    /**
     * Impala
     */
    IMPALA("impala", PostgresDialect.class),
    /**
     * Vertica
     */
    VERTICA("vertica", PostgresDialect.class),
    /**
     * 东方国信 xcloud
     */
    XCloud("xcloud", XCloudDialect.class),
    /**
     * redshift
     */
    REDSHIFT("redshift", PostgresDialect.class),
    /**
     * openGauss
     */
    OPENGAUSS("openGauss", PostgresDialect.class),
    /**
     * TDengine
     */
    TDENGINE("TDengine", PostgresDialect.class),
    /**
     * Informix
     */
    INFORMIX("informix", InforMixDialect.class),
    /**
     * sinodb
     */
    SINODB("sinodb", GBase8sDialect.class),
    /**
     * uxdb
     */
    UXDB("uxdb", PostgresDialect.class),
    /**
     * greenplum
     */
    GREENPLUM("greenplum", PostgresDialect.class),
    /**
     * trino
     */
    TRINO("trino", TrinoDialect.class),
    /**
     * lealone
     */
    LEALONE("lealone", PostgresDialect.class),
    /**
     * presto
     */
    PRESTO("Presto", TrinoDialect.class),
    /**
     * goldendb
     */
    GOLDENDB("goldendb", MySqlDialect.class),
    /**
     * yasdb
     */
    YASDB("yasdb", MySqlDialect.class),
    /**
     * vastbase
     */
    VASTBASE("vastbase", PostgresDialect.class),
    /**
     * duckdb
     */
    DUCKDB("duckdb", PostgresDialect.class),
    /**
     * hive2
     */
    HIVE2("hive2", PostgresDialect.class),
    /**
     * UNKNOWN DB
     */
    OTHER("other", PostgresDialect.class);

    /**
     * 数据库名称
     */
    private final String name;

    /**
     * 实现类
     */
    private final Class<? extends Dialect> value;

    DialectEnum(String name, Class<? extends Dialect> value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取数据库方言实现类
     *
     * @return 数据库方言实现类
     */
    public Class<? extends Dialect> getValue() {
        return value;
    }

    /**
     * 根据数据库名称获取对应的方言实现类
     *
     * @param name 数据库名称
     * @return 方言实现类
     */
    public static Class<? extends Dialect> getDialect(String name) {
        DialectEnum[] enums = values();
        for (DialectEnum item : enums) {
            if (item.name.equalsIgnoreCase(name)) {
                return item.value;
            }
        }
        return OTHER.value;
    }
}
