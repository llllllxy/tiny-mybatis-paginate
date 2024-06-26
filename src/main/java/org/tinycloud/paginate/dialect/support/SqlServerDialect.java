package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;


/**
 * <p>
 * 数据库方言-SqlServer2012版本
 * </p>
 *
 * @author liuxingyu01
 * @since 2023-10-18
 **/
public class SqlServerDialect extends AbstractDialect {

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
        StringBuilder sql = new StringBuilder();

        long offset = (pageNo - 1L) * pageSize;
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
