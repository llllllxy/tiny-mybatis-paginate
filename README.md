<h1 align="center">tiny-mybatis-paginate</h1>

<p align="center">
	<a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">
		<img src="https://img.shields.io/badge/license-Apache%202-green.svg" />
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-8+-blue.svg" />
	</a>
    <a href='https://gitee.com/leisureLXY/tiny-mybatis-paginate'>
        <img src='https://gitee.com/leisureLXY/tiny-mybatis-paginate/badge/star.svg?theme=dark' alt='star' />
    </a>
    <br/>
</p>

## 1、简介
`tiny-mybatis-paginate`是一个`MyBatis`的辅助分页插件，基于`MyBatis`拦截器(Interceptor)开发的，使用简单，安全可靠，性能优异。

## 2、支持的数据库
- **MySQL**
- **MariaDb**
- **ORACLE**
- **DB2**
- **PostgreSql**
- **Hsql**
- **sqlite**
- **DM**
- **sqlserver**
- **gauss**
- **gbase**
- **oceanbase**
- **highgo**
- **hana**
- **h2**
- **openGauss**
- **Derby**
- **oscar**

## 2、快速开始
### 引入依赖
```xml
    <dependency>
        <groupId>org.tinycloud</groupId>
        <artifactId>tiny-jdbc-boot-starter</artifactId>
        <version>1.4.4</version>
    </dependency>
```

### 在传统的`Spring`项目内使用
##### 使用mybatis-config.xml 配置插件
如果项目中配置了`mybatis-config.xml`，则可以添加如下代码到配置中：
```
<plugins>
    <plugin interceptor="org.tinycloud.paginate.MyBatisPaginateInterceptor">
        <!-- 配置数据库方言，不配置的话会根据jdbcUrl自动适配 -->
        <property name="dialect" value="mysql"/>
	</plugin>
</plugins>
```
##### 使用application.xml 配置插件
如果不使用`mybatis-config.xml`配置文件配置插件，也可以通过`SqlSessionFactoryBean`的插件列表来完成集成，如下配置：
```
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
  <!-- 配置插件列表 -->
  <property name="plugins">
    <array>
      <bean class="org.tinycloud.paginate.MyBatisPaginateInterceptor">
        <property name="properties">
          <!-- 配置数据库方言，不配置的话会根据jdbcUrl自动适配 -->
          <value>
            dialect=mysql
          </value>
        </property>
      </bean>
    </array>
  </property>
</bean>
```

### 在`SpringBoot`项目内使用
在项目中增加`MybatisInterceptorConfig`配置类即可，代码如下：
```java
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.tinycloud.paginate.MyBatisPaginateInterceptor;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

@Configuration
public class MybatisInterceptorConfig {
    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    // 只执行一次
    @PostConstruct
    public void addDefaultTimeInterceptor() {
        // Mybatis分页拦截器
        MyBatisPaginateInterceptor pageInterceptor = new MyBatisPaginateInterceptor();
        Properties pageProperties = new Properties();
        /*
         * 默认值为 false。设置为 true 时，允许在运行时根据多数据源自动识别对应方言的分页 （不支持自动选择sqlserver2012，只能使用sqlserver）
         */
        pageProperties.setProperty("dialect", "mysql");
        pageInterceptor.setProperties(pageProperties);

        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();

            // 最后添加的会更早执行
            configuration.addInterceptor(pageInterceptor);
        }
    }
}
```

## 3、使用方法
```java
// pageNumber pageSize模式
Page<SysLoginLog> page = PaginateRequest.of(1, 10).request(() -> loginLogMapper.pageList(param));

或者
// offset limit模式
Page<SysLoginLog> page = PaginateRequest.in(0, 10).request(() -> loginLogMapper.pageList(param));
```

- `of`  配置分页的`当前页码`以及`每页条数`
- `in`  配置分页的`偏移量`以及`每页条数`
- `request` 该方法需要传递一个业务逻辑方法，也就是你需要执行分页的方法

#### Page对象内容
- `records` 分页后的数据列表，具体的返回值可以使用`Page<T>`泛型接收
- `pages` 总页数
- `total` 总记录数
- `pageNum` 当前页码
- `pageSize` 每页条数
- `hasNextPage` 是否存在下一页，`true`：存在，`false`：不存在
- `hasPreviousPage` 是否存在上一页，`true`：存在，`false`：不存在
- `isFirstPage` 是否为首页，`true`：首页，`false`：非首页
- `isLastPage` 是否为末页，`true`：末页，`false`：非末页
