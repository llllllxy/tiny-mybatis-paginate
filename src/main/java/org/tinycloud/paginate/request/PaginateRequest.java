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
     * 执行请求分页方法
     *
     * @param logicFunction 业务逻辑查询方法
     * @param <T>           泛型参数
     * @return 执行分页请求
     */
    @Override
    public <T> Page<T> request(LogicFunction logicFunction) {
        // 业务方法执行
        logicFunction.invoke();

        /*
         * 获取threadLocal分页请求对象
         * 注意：该行代码需要再执行完成业务方法后执行，原因是业务方法在执行时拦截器需要对threadLocal内的page对象作出修改
         */
        Page<T> page = (Page<T>) PageRequestHolder.getPageLocal();

        // 删除threadLocal分页请求对象
        PageRequestHolder.removePageLocal();
        return page;
    }

    /**
     * 获取下一页分页对象
     *
     * @return 获取下一页分页请求实例
     */
    @Override
    public Paginate next() {
        return of(pageNumber + 1, pageSize);
    }

    /**
     * 获取上一页分页对象
     *
     * @return 获取上一页分页请求实例
     */
    @Override
    public Paginate previous() {
        return pageNumber == 1 ? this : of(pageNumber - 1, pageSize);
    }

    /**
     * 获取首页分页对象
     *
     * @return 获取首页分页请求实例
     */
    @Override
    public Paginate first() {
        return of(1, pageSize);
    }
}
