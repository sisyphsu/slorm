package com.slorm.operation;

import java.sql.Connection;

/**
 * 根据SQL语句进行的查询操作
 * @author sulin
 * 2012-4-17 上午08:57:55
 */
public class SQLSelectOperation {

	/**
	 * sql语句
	 */
	private String sql;
	
	/**
	 * 预处理参数
	 */
	private Object[] params;
	
	/**
	 * 预处理参数SQL数据类型
	 */
	private int[] types;
	
	/**
	 * 查询操作用到的数据库连接
	 */
	private Connection conn;
	

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	public int[] getTypes() {
		return types;
	}

	public Connection getConn() {
		return conn;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public void setTypes(int[] types) {
		this.types = types;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
}