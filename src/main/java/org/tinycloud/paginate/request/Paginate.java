package org.tinycloud.paginate.request;


import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.utils.LogicFunction;

/**
 * 分页构造接口定义
 */
public interface Paginate {
    /**
     * 当前页码
     *
     * @return 当前页码
     */
    int getPageNumber();

    /**
     * 每页条数
     *
     * @return 每页条数
     */
    int getPageSize();

    /**
     * 当前页开始位置
     *
     * @return 分页开始位置
     */
    long getOffset();

    /**
     * 当前页开始位置
     *
     * @return 分页开始位置
     */
    int getLimit();

    /**
     * 当前页码结束位置
     *
     * @return 分页结束位置
     */
    long getEndRow();

    /**
     * 下一页
     *
     * @return 下一页分页请求对象实例
     */
    Paginate next();

    /**
     * 上一页
     *
     * @return 上一页分页请求对象实例
     */
    Paginate previous();

    /**
     * 第一页
     *
     * @return 首页分页请求对象实例
     */
    Paginate first();

    /**
     * 请求分页并且返回分页响应实体实例
     *
     * @param logicFunction 业务方法函数
     * @param <T>           泛型类型
     * @return 分页响应对象
     */
    <T> Page<T> request(LogicFunction logicFunction);
}
