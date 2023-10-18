package org.tinycloud.paginate.utils;

import org.tinycloud.paginate.Page;

public class PageRequestHolder {
    /**
     * 分页请求对象多线程threadLocal
     */
    private static final ThreadLocal<Page<?>> PAGE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取threadLocal内存放的page分页响应对象
     *
     * @return page 分页响应对应
     */
    public static Page<?> getPageLocal() {
        return PAGE_THREAD_LOCAL.get();
    }

    /**
     * 删除threadLocal内存放的page分页响应对象
     */
    public static void removePageLocal() {
        PAGE_THREAD_LOCAL.remove();
    }

    /**
     * 设置threadLocal内存放的page分页响应对象
     *
     * @param page 分页响应对象实例
     */
    public static void setPageLocal(Page<?> page) {
        PAGE_THREAD_LOCAL.set(page);
    }

}
