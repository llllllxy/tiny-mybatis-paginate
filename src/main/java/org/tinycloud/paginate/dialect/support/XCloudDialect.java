package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;

public class XCloudDialect extends AbstractDialect {

    /**
     * 分页查询适配
     *
     * @param oldSQL 原始sql
     * @param page   分页参数对象
     * @return 处理过后的sql
     */
    @Override
    public String getPageSql(String oldSQL, Page<?> page) {
        long pageNo = page.getPageNum();
        long pageSize = page.getPageSize();
        StringBuilder sql = new StringBuilder(oldSQL);

        long offset = (pageNo - 1L) * pageSize;
        long limit = pageSize;
        sql.append(" LIMIT ");
        if (offset != 0) {
            sql.append(" ( ").append(offset + 1L).append(",").append(offset + limit).append(" ) ");
        } else {
            sql.append(limit);
        }
        return sql.toString();
    }
}
