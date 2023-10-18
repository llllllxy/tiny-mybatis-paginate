package org.tinycloud.paginate.utils;

/**
 * 业务逻辑执行方法接口定义
 * 提供给PageRequest作为参数使用
 */
public interface LogicFunction {
    /**
     * 查询逻辑执行方法
     */
    void invoke();
}
