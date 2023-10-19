package org.tinycloud.paginate.request;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.utils.LogicFunction;
import org.tinycloud.paginate.utils.PageRequestHolder;

/**
 * 分页请求对象
 */
public class PaginateRequest extends AbstractPaginateRequest {

    /**
     * 构造函数初始化分页请求对象
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页条数
     */
    private PaginateRequest(int pageNumber, int pageSize) {
        super(pageNumber, pageSize);
    }

    /**
     * 对外提供的初始化分页请求方法
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页条数
     * @return 获取分页请求对象实例
     */
    public static Paginate of(int pageNumber, int pageSize) {
        return new PaginateRequest(pageNumber, pageSize);
    }

    /**
     * 对外提供的初始化分页请求方法
     *
     * @param offset 当前偏移量
     * @param limit  每页条数
     * @return 获取分页请求对象实例
     */
    public static Paginate in(int offset, int limit) {
        int pageSize = limit;
        int pageNumber = offset / limit + 1;
        return new PaginateRequest(pageNumber, pageSize);
    }

    /**
     * 执行请求分页方法
     *
     * @param logicFunction 业务逻辑查询方法
     * @param <T>           泛型参数
     * @return 执行分页请求
     */
    @Override
    public <T> Page<T> request(LogicFunction logicFunction) {
        try {
            // 业务方法执行
            logicFunction.invoke();
            // 从threadLocal里获取最终分页结果
            Page<T> page = (Page<T>) PageRequestHolder.getPageLocal();
            return page;
        } finally {
            // 清除threadLocal，用完必须清除，要不然的话，会内存泄露
            PageRequestHolder.removePageLocal();
        }
    }
}
