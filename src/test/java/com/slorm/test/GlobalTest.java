package com.slorm.test;

import com.slorm.core.Restriction;
import com.slorm.test.model.User;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * This is used for testing all orm-operation function.
 *
 * Created by sulin on 14-4-22.
 */
public class GlobalTest {

	@Test
	public void saveTest(){
		User user = new User();
		user.setId((int) System.currentTimeMillis());
		user.setName("hello");
		user.setPassword("world");
		user.setDatetime(new Date());
		user.$save();
	}

	@Test
	public void loadTest(){
		User user = new User();
		user.setId(123);
		user.$load();
		System.out.println(user.getDatetime());
	}

	@Test
	public void selectTest(){
		User example = new User();
		example.setName("hello");
		example.$list();
		List<User> list = example.$page(0, 10);
		System.out.println(list.size());
	}

	@Test
	public void updateTest(){
		User user = new User();
		user.setId(123);
		user.setPassword("newPassword");
		System.out.println(user.$update());
	}

	@Test
	public void updateAllTest(){
		User example = new User();
		example.setName("helloAgain");
		Restriction<User> res = new Restriction<User>(User.class);
		res.equal("name", "hello");
		System.out.println(res.$update(example));
	}

	@Test
	public void delete(){
		User user = new User();
		user.setId(123);
		System.out.println(user.$delete());
	}

}