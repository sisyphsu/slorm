#SLORM
This is a very amazing java ORM framework.

##介绍

你是否像我一样，对繁琐的数据库CURD操作感觉厌倦？

本文将向你呈现一个崭新的ORM框架，该框架可以以“润物细无声”的方式，帮助你从无穷无尽的CURD中解脱出来。

在接下来的讲解之前，我们先通过如下几行代码，对Slorm的“极简精神”了解一二。

1、新建Java对象，并初始化：
```java
User user = new User();
user.setName("hello world");
```
2、将该对象存储入数据库中：
```java
user.$save();   // 返回数据库自增主键 1
```
3、更新该对象，并提交修改至数据库：
```java
user.setName("new user name");
user.$update();
```
4、查询/加载该对象：
```java
User user = new User();
user.setId(1);
user.$load();
```
5、删除该对象：
```java
user.$delete();
```

##快速入门

接下来以maven为例，讲解如何使用slorm。

*备注：由于maven中央库的提交审核条件太多，因此Slorm目前在OSC的第三方库中存储，使用前需要先添加OSC的第三方资源库。*

1、添加maven依赖：
```xml
<dependency>
    <groupId>slorm</groupId>
    <artifactId>slorm</artifactId>
    <version>1.1.1</version>
</dependency>
```
2、配置数据源：

将数据源配置在Spring容器中即可，Slorm会在运行时主动到Spring中搜索数据源，但这只限于web应用。

普通J2SE程序使用Slorm的话，需要在classpath中添加配置文件slorm.properties：
```
dataSource.dataSourceName.driverClass=org.gjt.mm.mysql.Driver
dataSource.dataSourceName.user=blah
dataSource.dataSourceName.password=blah
dataSource.dataSourceName.url=blah
```
Slorm也支持配置多个数据源，只要将dataSourceName修改为其他名字即可。

3、编写POJO：

Slorm使用继承方式来增强POJO功能，因此你的POJO需要继承SlormDao。
```
public class User extends SlormDao<User> {
	private Integer id;
	private String name;
    /*  getter and setter  */
}
```

4、使用该POJO进行CURD操作：

此时，User类就可以像**介绍**中那样进行CURD操作了。

```
User user = new User();
user.setName("hello world");
user.$save();   // 返回数据库自增主键 1
user.setName("new user name");
user.$update();
user = new User();
user.setId(1);
user.$load();
user.$delete();
```

##高级特性

Slorm支持注解方式自定义POJO与数据库的映射关系，如下：

```
@Table(tableName = "user", dataSource = "test")
public class User extends SlormDao<User> {

	@Column(isID = true)
	private Integer id;

	private String name;

	@Column(columnName = "pass")
	private String password;

	@UnColumn
	private String dont_mapping;

	@Quote("target.user_id = this.id")
	private User friend;

}
```
说明：
* Table注解：手动配置POJO对应的表名、数据源名（Slorm支持多数据源）
* Column注解：配置字段对应的列名、数据类型、是否为主键
* UnColumn注解：强制Slorm忽略该字段
* Quote注解：实现POJO间的关联映射，参数为映射描述

Slorm也支持SQL片段配置、原生SQL查询、懒加载、关联映射等增强特性。

Slorm也支持编程式复杂批量操作，如批量删除、批量修改、分页查询、指定列名查询等。

Slorm本身并没有封装事务操作，但它可以利用Spring的事务管理功能实现事务控制。

除此之外，Slorm也暴露出了获取connection的api，应用层可以使用该connection做任何未实现功能。

*开发中：自动创建数据表、自动添加数据列功能，元数据自动修复功能。*

## 备注

Slorm于2012年8月完成，并且已经在多个项目中使用，但它尚未成熟。

Slorm的目的是，在极简的前提上，提供不逊于Hibernate和IBatis的功能。