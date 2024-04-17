package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InforMixDialect extends AbstractDialect {

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
        long offset = (pageNo - 1L) * pageSize;
        long limit = pageSize;

        StringBuilder sql = new StringBuilder("SELECT");
        sql.append(" SKIP ");
        sql.append(offset);
        sql.append(" FIRST ");
        sql.append(limit);

        // 忽略大小写进行替换
        Pattern pattern = Pattern.compile("SELECT", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(oldSQL);
        return matcher.replaceFirst(sql.toString());
    }
}
