package com.suruns.multiple.mybatis.db;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MultipleDataSource {

    @Bean
    AopDataSource aopDataSource(){
        return new AopDataSource();
    }

    @Autowired
    private Environment env;

//    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource masterDataSource() {
        return getDataSource("master");
    }
//    @Bean(initMethod = "init",destroyMethod = "close")
    public DataSource slaveDataSource() {
        return getDataSource("slave");
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(){
        Map<Object,Object> targetDataSource = new HashMap<>();
        targetDataSource.put(DataSourceType.Master,masterDataSource());
        targetDataSource.put(DataSourceType.Slave,slaveDataSource());
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSource);
        dataSource.setDefaultTargetDataSource(masterDataSource());
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource ds) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(ds);
        //使用xml方式则使用下面的配置
//        sqlSessionFactoryBean.setTypeAliasesPackage(env.getProperty("mybatis.typeAliasesPackage"));
//        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().
//                getResources(env.getProperty("mybatis.mapperLocations")));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }

    private DataSource getDataSource(String type){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty(type + ".datasource.url"));
        dataSource.setUsername(env.getProperty(type + ".datasource.username"));//用户名
        dataSource.setPassword(env.getProperty(type + ".datasource.password"));//密码
        dataSource.setDriverClassName(env.getProperty(type + ".datasource.driver-class-name"));
        dataSource.setInitialSize(2);//初始化时建立物理连接的个数
        dataSource.setMaxActive(20);//最大连接池数量
        dataSource.setMinIdle(0);//最小连接池数量
        dataSource.setMaxWait(60000);//获取连接时最大等待时间，单位毫秒。
        dataSource.setValidationQuery("SELECT 1");//用来检测连接是否有效的sql
        dataSource.setTestOnBorrow(false);//申请连接时执行validationQuery检测连接是否有效
        dataSource.setTestWhileIdle(true);//建议配置为true，不影响性能，并且保证安全性。
        dataSource.setPoolPreparedStatements(false);//是否缓存preparedStatement，也就是PSCache
        return dataSource;
    }
}
