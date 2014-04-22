#SLORM
This is a very amazing java ORM framework.

##Introduce
Assume that you have a class **User**，and you need to write some **User** in database，sometimes，you also need to read or rewrite them。what is the easiest way to do this?

**JDBC? Hibernate? IBatis?** 

Now i will show you a new method：
```java
User user = new User();
// initialize user......blah
user.$save();
// the user has been automatically saved in database now ~
/* edit user */
user.$update();
// the user's name has been automatically updated now ~
user.$delete();
// the user has been automatically deleted ~
```

Is it amazing?

##最简单的ORM框架
在介绍章节中，你应该能够对Slorm的“极简”特性了解一二，那么接下来就让我着重介绍一下使用Slorm的每个细节步骤。
###一、将Slorm添加入工程中
###二、配置数据源
如果你使用Spring管理数据源，并且当前工程为WEB项目，那么就你就不需要针对Slorm做任何配置，Slorm会主动从Spring中搜寻合适的数据源。
如果不符合上述条件，那么你就需要手动配置一下Slorm的数据源。手动配置Slorm数据源也是非常简单的，在CLASSPATH中添加一个slorm.properties文件即可。以下是slorm.properties文件样例：
```
dataSource.dataSourceName1.driverClass=org.gjt.mm.mysql.Driver
dataSource.dataSourceName1.user=blah
dataSource.dataSourceName1.password=blah
dataSource.dataSourceName1.url=blah
dataSource.dataSourceName2.driverClass=org.gjt.mm.mysql.Driver
dataSource.dataSourceName2.user=blah
dataSource.dataSourceName2.password=blah
dataSource.dataSourceName2.url=blah
```
OK，数据源已经配置完成了。
###三、编写POJO
Slorm使用继承的方式来增强POJO功能，因此你的POJO需要继承SlormDao。
除此之外，你可能还需要为POJO添加上注解，标明该类对应的数据表和数据源，当然你也可以不添加注解，那么Slorm就会采用默认策略来处理该类。
```
@Table(tableName = "user", dataSource="dataSourceName")
public class User extends SlormDao<User> {

	private Integer id;

	private String name;

	private String password;

	private Date datetime;

    @UnColumn
    private String dont_mapping;

    /*  getter and setter  */

}
```
除以上示例之外，Slorm也提供数据类型注解、映射注解、SQL片断注解等等。Slorm会在使用简单的前提上，提供不逊于Hibernate/IBatis的功能。
###四、使用Slorm
