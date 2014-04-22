package com.slorm.test.model;

import com.slorm.SlormDao;
import com.slorm.annocation.Table;

import java.util.Date;

/**
 * The model used for testing.
 *
 * create table name(id int primary key, name varchar(32)， password varchar(64), 'datetime' datetime);
 *
 * Created by sulin on 14-4-22.
 */
@Table(tableName = "user")
public class User extends SlormDao<User> {

	private Integer id;

	private String name;

	private String password;

	private Date datetime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

}