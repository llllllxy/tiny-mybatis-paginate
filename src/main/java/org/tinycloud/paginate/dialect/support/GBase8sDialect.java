package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;

public class GBase8sDialect extends AbstractDialect {
    /**
     * 分页查询适配
     *
     * @param originalSql 原始sql
     * @param page        分页参数对象
     * @return 处理过后的sql
     */
    @Override
    public String getPageSql(String originalSql, Page<?> page) {
        Integer pageNo = page.getPageNum();
        Integer pageSize = page.getPageSize();
        int offset = (pageNo - 1) * pageSize;
        int limit = pageSize;

        StringBuilder sql = (new StringBuilder(originalSql)).insert(6, " SKIP " + offset + " FIRST " + limit);
        return sql.toString();
    }
}
