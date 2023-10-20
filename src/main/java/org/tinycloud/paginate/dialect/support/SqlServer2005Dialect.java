package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;
import org.tinycloud.paginate.utils.StrUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlServer2005Dialect extends AbstractDialect {
    private static final Pattern pattern = Pattern.compile("\\((.)*order by(.)*\\)");

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

        StringBuilder pagingBuilder = new StringBuilder();
        String orderby = this.getOrderByPart(originalSql);
        String distinctStr = "";
        String loweredString = originalSql.toLowerCase();
        String sqlPartString = originalSql;
        if (loweredString.trim().startsWith("select")) {
            int index = 6;
            if (loweredString.startsWith("select distinct")) {
                distinctStr = "DISTINCT ";
                index = 15;
            }
            sqlPartString = originalSql.substring(index);
        }

        pagingBuilder.append(sqlPartString);
        if (StrUtils.isBlank(orderby)) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }

        long firstParam = offset + 1L;
        long secondParam = offset + limit;
        String sql = "WITH selectTemp AS (SELECT " + distinctStr + "TOP 100 PERCENT  ROW_NUMBER() OVER (" + orderby + ") as __row_number__, " + pagingBuilder + ") SELECT * FROM selectTemp WHERE __row_number__ BETWEEN " + firstParam + " AND " + secondParam + " ORDER BY __row_number__";
        return sql;


    }

    public String getOrderByPart(String sql) {
        String order_by = "order by";
        int lastIndex = sql.toLowerCase().lastIndexOf(order_by);
        if (lastIndex == -1) {
            return "";
        } else {
            Matcher matcher = pattern.matcher(sql);
            if (!matcher.find()) {
                return sql.substring(lastIndex);
            } else {
                int end = matcher.end();
                return lastIndex < end ? "" : sql.substring(lastIndex);
            }
        }
    }
}
