#SLORM
This is a very amazing java ORM framework.

------
## First Of All
Assume that you have a class **User**，and you need to write some **User** in database，sometimes，you also need to read or rewrite them。what is the easiest way to do this?

**JDBC? Hibernate? IBatis?** 

Now i will show you a new method：
```Java
User user = new User();
// initialize user......blah
user.$save();
// the user has been automatically saved in database now ~
user.setName("new name");
user.$update();
// the user's name has been automatically updated now ~
user.$delete();
// the user has been automatically deleted ~
```

Is it amazing?

------
##Introduce

