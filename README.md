#SLORM
This is a very amazing java ORM framework.

##介绍

你是否像我一样，对繁琐的数据库CURD操作感觉厌倦？

本文将向你呈现一个崭新的ORM框架Slorm，它可以帮助你从无穷无尽的CURD中解脱出来。

在接下来的讲解之前，我们先通过如下几行代码，对Slorm的**“极简精神”**了解一二。

**1、新建Java对象，并初始化：**
```java
User user = new User();
user.setName("hello world");
```
**2、将该对象存储入数据库中：**
```java
user.$save();   // 返回数据库自增主键 1
```
**3、更新该对象，并提交修改至数据库：**
```java
user.setName("new user name");
user.$update();
```
**4、从数据库中查询/加载该对象：**
```java
User user = new User();
user.setId(1);
user.$load();
```
**5、从数据库中删除该对象：**
```java
user.$delete();
```

##快速入门

接下来以maven为例，讲解如何使用slorm。

*备注：由于maven中央库审核条件比较苛刻，Slorm目前在OSC的第三方库中，使用前需要添加OSC的第三方资源库。*

**1、添加maven依赖：**
```xml
<dependency>
    <groupId>slorm</groupId>
    <artifactId>slorm</artifactId>
    <version>1.1.1</version>
</dependency>
```
**2、配置数据源：**

将数据源配置在Spring容器中即可，Slorm会在运行时主动到Spring中搜索数据源，但这只限于web应用。

普通J2SE程序使用Slorm的话，需要在classpath中添加配置文件**slorm.properties**：
```java
dataSource.dataSourceName.driverClass=org.gjt.mm.mysql.Driver
dataSource.dataSourceName.user=blah
dataSource.dataSourceName.password=blah
dataSource.dataSourceName.url=blah
```
Slorm也支持多数据源配置，只要将dataSourceName修改为其他名字即可。

**3、编写POJO：**

Slorm使用继承方式来增强POJO功能，因此你的POJO需要继承SlormDao。
```java
public class User extends SlormDao<User> {

	private Integer id;
	private String name;
    /*  getter and setter  */

}
```

**4、使用该POJO进行CURD操作：**

此时，User类就可以非常随意地进行CURD操作了。

```java
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

Slorm支持注解方式自定义POJO与数据库的映射关系，例：

```java
@Table(tableName = "user", dataSource = "test")
public class User extends SlormDao<User> {

	@Column(isID = true)
	private Integer id;

	private String name;

	@Column(columnName = "pass")
	private String password;

	@UnColumn
	private String dont_mapping;

	@Quote("target.friend_id = this.id")
	private User friend;

}
```
注解说明：
* **Table**：手动配置POJO对应的表名、数据源名。
* **Column**：配置字段对应的列名、数据类型、是否为主键。
* **UnColumn**：强制Slorm忽略该字段。
* **Quote**：实现POJO间的关联映射，参数为映射描述。

Slorm也支持SQL片段配置、原生SQL查询、懒加载、关联映射等增强特性。

Slorm本身并没有封装事务操作，但它可以利用Spring的事务管理功能实现事务控制。

Slorm也支持编程式复杂批量操作，如批量删除、批量修改、分页查询、指定列名查询等。

除此之外，Slorm也暴露出了connection接口，应用层可以获取底层数据库连接进行任何操作。

***开发中：自动创建数据表、自动添加数据列功能，元数据自动修复功能。***

Slorm接口一览（*为区分于普通方法，DML方法均以美元符号$为始*）：
* **$save / $saveAll**：保存数据
* **$delete / $deleteAll**：删除数据，可以按主键删除，也可以按example删除。
* **$update / $updateAll**：更新数据，可以按主键更新，也可以按example更新。
* **$load**：加载数据，select的特殊实现，需要指定主键。
* **$get**：按example查询单行记录，可指定被查询列名。
* **$list**：按example查询多行记录，可指定被查询列名。
* **$page**：按example分页查询，可指定被查询列名。
* **$selectBySQL**：SQL片段查询，只需要编写where子句，支持SQL片段配置、引用等。
* **$nativeSQL**：原生SQL查询，支持SQL配置、引用。
* **$createRestriction**：创建函数式查询工具。
* **$getConnection**：获取底层数据库连接。
* **$releaseConnection**：释放底层数据库连接。

## 备注

Slorm于2012年8月完成，并且已经在多个项目中使用，但它并不是很城市，但应付中小型应用绰绰有余。

Slorm的目的是：
* 在易用的前提上，尽可能的减少学习成本。
* 在极简的前提上，提供不逊于Hibernate和IBatis的功能。