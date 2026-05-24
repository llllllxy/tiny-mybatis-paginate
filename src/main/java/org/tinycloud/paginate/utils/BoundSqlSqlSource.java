package org.tinycloud.paginate.utils;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * BoundSql包装SqlSource实现类
 */
public class BoundSqlSqlSource implements SqlSource {

    private final BoundSql boundSql;

    /**
     * 构造BoundSql包装SqlSource
     *
     * @param boundSql 原始BoundSql
     */
    public BoundSqlSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    /**
     * 获取BoundSql对象
     *
     * @param parameterObject 参数对象
     * @return BoundSql对象
     */
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return boundSql;
    }
}
