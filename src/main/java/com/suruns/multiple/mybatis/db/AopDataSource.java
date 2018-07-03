package com.suruns.multiple.mybatis.db;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AopDataSource {
    @Pointcut("@annotation(com.suruns.multiple.mybatis.annotation.ReadOnly)")
    private void readOnly(){}

    @Around("readOnly()")
    public Object around(ProceedingJoinPoint p) throws Throwable {
        DataSourceContextHolder.setDataSourceType(DataSourceType.Slave);
        Object result = p.proceed();
        DataSourceContextHolder.setDataSourceType(DataSourceType.Master);
        return result;
    }
}
