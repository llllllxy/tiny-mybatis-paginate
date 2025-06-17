package org.tinycloud.paginate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象，支持pageNum-pageSize模式
 *
 * @author liuxingyu01
 * @since 2023-10-10 9:17
 **/
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（pageNo = offset / limit + 1;）
     */
    private long pageNum;

    /**
     * 分页大小（等价于limit）
     */
    private long pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 分页后的数据
     */
    private List<T> records;

    public Page() {

    }

    public Page(long pageNum, long pageSize) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public Page(List<T> records, long total, long pageNum, long pageSize) {
        this.records = (records == null ? new ArrayList<T>() : records);
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.pages = (total + pageSize - 1) / pageSize;
    }

    public long getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPages() {
        return this.pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<T> getRecords() {
        return this.records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
        this.pages = (total + this.pageSize - 1L) / this.pageSize;
    }

    /**
     * 是否存在下一页
     *
     * @return true：存在，false：不存在
     */
    public boolean hasNextPage() {
        return this.getPages() > this.getPageNum();
    }

    /**
     * 是否存在上一页
     *
     * @return true：存在上一页，false：不存在
     */
    public boolean hasPreviousPage() {
        return this.getPageNum() > 1L;
    }

    /**
     * 是否为首页
     *
     * @return true：首页，false：非首页
     */
    public boolean firstPage() {
        return this.getPageNum() == 1L;
    }

    /**
     * 是否为末页
     *
     * @return true：为末页，false：非末页
     */
    public boolean lastPage() {
        return this.getPages() == this.getPageNum();
    }

    @Override
    public String toString() {
        return "Page {pageNum=" + pageNum + ", pageSize=" + pageSize + ", total=" + total + ", pages=" + pages
                + ", records=" + records + "}";
    }
}
