package org.tinycloud.paginate.dialect;

import org.tinycloud.paginate.Page;


/**
 * <p>
 *    数据库方言接口声明
 * </p>
 *
 * @author liuxingyu01
 * @since 2023-10-18
 **/
public interface Dialect {

    /**
     * 获取分页sql
     *
     * @param sql  原始sql
     * @param page 分页响应对象实例
     * @return 获取分页sql
     */
    String getPageSql(String sql, Page<?> page);

    /**
     * 获取总条数sql
     *
     * @param sql 原始sql
     * @return 获取查询总数sql
     */
    String getCountSql(String sql);
}
