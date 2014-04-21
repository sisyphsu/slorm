package com.slorm.operation;

import com.slorm.core.Property;
import com.slorm.core.Restriction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示一个Select操作
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class SelectOperation {

	/**
	 * 查询操作对应的数据库表名
	 */
	private String tableName;
	
	/**
	 * 查询操作的约束条件
	 */
	private Restriction<?> restriction;
	
	/**
	 * 查询那些列。
	 */
	private List<Property> columns = new ArrayList<Property>();

	/**
	 * 查询操作用到的数据库连接
	 */
	private Connection conn;

	/**
	 * 添加一个需要查询的行
	 * @param column
	 */
	public void addColumn(Property column){
		this.columns.add(column);
	}
	
	public void setColumns(List<Property> columns) {
		this.columns = columns;
	}

	public String getTableName() {
		return tableName;
	}

	public Restriction<?> getRestriction() {
		return restriction;
	}

	public List<Property> getColumns() {
		return columns;
	}

	public Connection getConn() {
		return conn;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setRestriction(Restriction<?> restriction) {
		this.restriction = restriction;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
}