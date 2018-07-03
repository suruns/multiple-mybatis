package com.suruns.multiple.mybatis.db;

public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();

    public static void setDataSourceType(DataSourceType type) {
        contextHolder.set(type);
    }
    public static DataSourceType getDataSourceType() {
        return contextHolder.get();
    }
}
