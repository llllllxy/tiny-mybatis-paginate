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
    long getPageNumber();

    /**
     * 每页条数
     *
     * @return 每页条数
     */
    long getPageSize();

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
    long getLimit();

    /**
     * 当前页码结束位置
     *
     * @return 分页结束位置
     */
    long getEndRow();

    /**
     * 请求分页并且返回分页响应实体实例
     *
     * @param logicFunction 业务方法函数
     * @param <T>           泛型类型
     * @return 分页响应对象
     */
    <T> Page<T> request(LogicFunction logicFunction);
}
