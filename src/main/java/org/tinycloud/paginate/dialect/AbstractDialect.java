package org.tinycloud.paginate.dialect;

import org.tinycloud.paginate.Page;


/**
 * <p>
 *    数据库方言-抽象类实现
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

    /**
     * 分页查询适配
     *
     * @param oldSQL 原始sql
     * @param page   分页参数对象
     * @return 处理过后的sql
     */
    @Override
    public String getPageSql(String oldSQL, Page<?> page) {
        Integer pageNo = page.getPageNum();
        Integer pageSize = page.getPageSize();
        StringBuilder sql = new StringBuilder(oldSQL);
        if (pageSize > 0) {
            int offset = (pageNo - 1) * pageSize;
            int limit = pageSize;
            if (offset <= 0) {
                sql.append(" limit ").append(limit);
            } else {
                sql.append(" limit ").append(offset).append(",")
                        .append(limit);
            }
        }
        return sql.toString();
    }
}
