package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementid, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        return(List<E>) simpleExecutor.query(configuration, mappedStatement, params);
    }

    @Override
    public <T> T selectOne(String statementid, Object... params) throws Exception {
        List<Object> objects = selectList(statementid, params);
        if (objects.size() == 1) {
            return (T)objects.get(0);
        } else if (objects.size() == 0) {
            return null;
        }
        throw new RuntimeException("返回结果过多");

    }

    @Override
    public boolean insertOne(String statementid, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        try {
            simpleExecutor.update(configuration, mappedStatement, params);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateOne(String statementid, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        try {
            simpleExecutor.update(configuration, mappedStatement, params);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteOne(String statementid, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        try {
            simpleExecutor.update(configuration, mappedStatement, params);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        //动态代理，生成一个代理类
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;
                Type genericReturnType = method.getGenericReturnType();
                if (methodName.contains("insert")) {
                    return insertOne(statementId, args);
                } else if (methodName.contains("update")) {
                    return updateOne(statementId, args);
                } else if (methodName.contains("delete")) {
                    return deleteOne(statementId, args);
                } else if (methodName.contains("find")) {
                    if (genericReturnType instanceof ParameterizedType) {
                        List<Object> objects = selectList(statementId, args);
                        return objects;
                    }
                }
                return selectOne(statementId, args);
            }
        });
        return (T) proxyInstance;
    }
}
