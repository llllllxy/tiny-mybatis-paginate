package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;

/**
 * <p>
 * 数据库方言-db2
 * </p>
 *
 * @author liuxingyu01
 * @since 2023-10-18
 **/
public class Db2Dialect extends AbstractDialect {

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
        StringBuilder sql = new StringBuilder("SELECT * FROM ( SELECT B.*, ROWNUMBER() OVER() AS RN FROM ( ");

        sql.append(oldSQL);
        int pageStart = (pageNo - 1) * pageSize + 1;
        int pageEnd = pageStart + pageSize - 1;
        sql.append(" ) AS B ) AS A WHERE A.RN BETWEEN ").append(pageStart).append(" AND ")
                .append(pageEnd);

        return sql.toString();
    }

}
