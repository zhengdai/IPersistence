package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.util.List;

public interface Executor {
    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;
    void update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;
}
