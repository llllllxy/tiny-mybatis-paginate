package org.tinycloud.paginate.request;


import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.exception.PaginateException;
import org.tinycloud.paginate.utils.PageRequestHolder;

/**
 * 分页请求对象抽象类
 */
public abstract class AbstractPaginateRequest implements Paginate {

    /**
     * 分页当前页码
     */
    protected long pageNumber;

    /**
     * 分页每页条数
     */
    protected long pageSize;

    /**
     * 抽象分页请求对象构造函数
     *
     * @param pageNumber 当前分页页码
     * @param pageSize   当前分页每页条数
     */
    public AbstractPaginateRequest(long pageNumber, long pageSize) {
        if (pageNumber < 1L) {
            throw new PaginateException("The pageNumber cannot be less than 1!");
        }
        if (pageSize < 1L) {
            throw new PaginateException("The pageSize cannot be less than 1!");
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;

        // 构造分页参数对象
        Page<?> page = new Page<>(this.pageNumber, this.pageSize);
        // 写入到threadLocal
        PageRequestHolder.setPageLocal(page);
    }

    /**
     * 获取当前分页页码
     *
     * @return 当前分页页码
     */
    @Override
    public long getPageNumber() {
        return this.pageNumber;
    }

    /**
     * 获取当前分页每页条数
     *
     * @return 每页条数
     */
    @Override
    public long getPageSize() {
        return this.pageSize;
    }

    /**
     * 获取分页开始位置
     *
     * @return 分页开始位置
     */
    @Override
    public long getOffset() {
        return (long) (this.pageNumber - 1) * (long) this.pageSize;
    }

    /**
     * 获取当前分页每页条数
     *
     * @return 每页条数
     */
    @Override
    public long getLimit() {
        return this.pageSize;
    }

    /**
     * 获取分页结束位置
     *
     * @return 分页结束位置
     */
    @Override
    public long getEndRow() {
        return this.pageNumber * this.pageSize;
    }
}
