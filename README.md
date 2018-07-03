# 基于springboot，mybatis，druid的双数据源整合的starter
## 介绍
只需配置数据库连接信息即可实现数据库读写分离，master数据源负责写数据，slave数据源负责读数据。默认为master数据源，使用注解@ReadOnly实现数据源切换为slave数据源，同时函数执行完后会换回master数据源。

## 使用
### 1.引入starter
将代码拉到本地编译：mvn install -Dmaven.test.skip=true.完成后在自己项目pom文件添加依赖。

        <dependency>
            <groupId>com.suruns.multiple.mybatis</groupId>
            <artifactId>multiple-mybatis-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
### 2.配置数据库信息
在springboot配置文件配置数据库信息，如下所示：
```
# 读数据源 
master.datasource.url = jdbc:mysql://localhost:3306/test_write?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
master.datasource.username = root
master.datasource.password = 123456
master.datasource.driver-class-name = com.mysql.jdbc.Driver

#写数据源
slave.datasource.url = jdbc:mysql://localhost:3306/test_read?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
slave.datasource.username = root
slave.datasource.password = 123456
slave.datasource.driver-class-name = com.mysql.jdbc.Driver
```
### 3.编写mapper
```
@Mapper
@Component
public interface UserMapper {

    @Select("select * from name")
    List<Name> findAllNames();

    @Insert("insert into name(name) values(#{name})")
    int insertNames(String name);
}
```
### 4.添加mapper扫描，以及测试函数
```
@SpringBootApplication
@RestController
@MapperScan(basePackages = "com.xxx.dao")
public class DubboServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboServerApplication.class, args);
    }
    @Autowired
    UserMapper mapper;

    @RequestMapping("names/list")
    @ReadOnly
    public List<Name> find(){
        return mapper.findAllNames();
    }
    @RequestMapping("names/insert")
    public int insert(String name){
        return mapper.insertNames(name);
    }
}
```
