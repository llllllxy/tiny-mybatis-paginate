package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;

/**
 * <p>
 * 数据库方言-Oracle
 * </p>
 *
 * @author liuxingyu01
 * @since 2023-10-18
 **/
public class OracleDialect extends AbstractDialect {

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
        StringBuilder sql = new StringBuilder("SELECT * FROM ( SELECT TMP_TB.*, ROWNUM ROW_ID FROM ( ");

        sql.append(oldSQL);
        long pageStart = (pageNo - 1L) * pageSize + 1L;
        long pageEnd = pageNo * pageSize;
        sql.append(" ) TMP_TB WHERE ROWNUM <=  ").append(pageEnd).append(" ) WHERE ROW_ID >= ")
                .append(pageStart);

        return sql.toString();
    }
}
