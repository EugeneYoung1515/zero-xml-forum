# zero-xml-forum
零XML配置的论坛

基于《精通Spring 4.x 企业应用开发实战》实战项目(SSH,Spring、Spring MVC、Hibernate)修改的，零XML配置的论坛，主要提供如下原项目没有的特色：

使用Gradle替代Maven，使用JavaConfig替代Spring的XML配置，使用@EnableWebMvc结合WebMvcConfigurerAdapter替代Spring MVC的XML配置，使用WebApplicationInitializer替代web.xml，实现零XML配置。(forum1)

使用Shiro框架进行认证操作。(forum1、forum2)

使用js操作DOM、提交表单、监听DOM事件。利用隐藏的表单参数或AJAX构造GET和POST以外的HTTP方法。Restful风格的URL，但仍然使用视图，部分页面功能通过AJAX与后端收发JSON实现。(forum1、forum2)

使用单元测试框架组合(Spring Test+Junit 4+DBUnit+Mockito)，展示简单的单元测试和集成测试例子。(forum1)



使用spring Boot进行配置，实现零XML配置。(forum2)

使用Spring Data JPA替换Hibernate，利用到了Spring Data JPA增删改查和分页查询。(forum2)。

简单展示Spring Boot单元测试相关注解的使用。(forum2)

## 5月20日更新
- 使用Redis替代PostgreSql，参考《Redis实战》第一章的思路，选择合适的数据结构和命令。
- Dao层利用Jackson的ObjectMapper类的方法，进行Map、Json和Bean间的转换。或者使用反射编写工具类进行Map和Bean间的转换。在Service层处理多对一关系。
- 也利用反射实现RedisTemplate不支持的Redis命令。

- 使用Shiro进行认证和授权操作。使用Shiro管理session，基于Redis实现分布式session，并尝试降低Shiro读取和更新Redis中的session的频率。