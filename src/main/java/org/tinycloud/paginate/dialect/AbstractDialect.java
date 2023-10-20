package org.tinycloud.paginate.dialect;

import org.tinycloud.paginate.Page;


/**
 * <p>
 * 数据库方言-抽象类实现
 * </p>
 *
 * @author liuxingyu01
 * @since 2023-10-18
 **/
public abstract class AbstractDialect implements Dialect {

    /**
     * 获取查询总条数sql
     *
     * @param oldSQL 原始sql
     * @return 查询总条数count sql
     */
    @Override
    public String getCountSql(String oldSQL) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(0) FROM ( ");
        sql.append(oldSQL);
        sql.append(" ) TEMP_COUNT");
        return sql.toString();
    }
}
