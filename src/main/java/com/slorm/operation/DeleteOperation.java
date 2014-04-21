package com.slorm.operation;

import com.slorm.core.Restriction;

import java.sql.Connection;

/**
 * 表示一个Delete操作，可以是单一删除、也可以是批量删除<br/>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class DeleteOperation {
	
	/**
	 * 删除操作相对的表名
	 */
	private String tableName;
	
	/**
	 * 删除操作的约束条件
	 */
	private Restriction<?> restriction;
	
	/**
	 * 操作用到的数据库连接
	 */
	private Connection conn;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Restriction<?> getRestriction() {
		return restriction;
	}

	public void setRestriction(Restriction<?> restriction) {
		this.restriction = restriction;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}
	
}