package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;

public class Oracle12cDialect extends AbstractDialect {
    /**
     * 分页查询适配
     *
     * @param oldSQL 原始sql
     * @param page   分页参数对象
     * @return 处理过后的sql
     */
    @Override
    public String getPageSql(String oldSQL, Page<?> page) {
        long pageNum = page.getPageNum();
        long pageSize = page.getPageSize();
        StringBuilder sql = new StringBuilder();

        long offset = (pageNum - 1L) * pageSize;
        long limit = pageSize;
        sql.append(oldSQL);
        sql.append(" OFFSET ");
        sql.append(offset);
        sql.append(" ROWS FETCH NEXT ");
        sql.append(limit);
        sql.append(" ROWS ONLY");

        return sql.toString();
    }
}
