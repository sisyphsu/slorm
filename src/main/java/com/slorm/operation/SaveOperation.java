package com.slorm.operation;

import com.slorm.core.Property;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示一个Save操作
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class SaveOperation {
	
	/**
	 * 此保存操作对应的数据库表名
	 */
	private String tableName;

	/**
	 * 主键列
	 */
	private Property id;
	
	/**
	 * 需要保存的字段列
	 */
	private List<Property> columns;
	
	/**
	 * 操作对应的数据库连接引用
	 */
	private Connection conn;
	
	/**
	 * 保存操作目标对象
	 */
	private Object target;
	
	/**
	 * 设置id
	 * @param id id字段
	 */
	public void setId(Property id){
		this.id = id;
	}
	
	/**
	 * 设置字段列
	 * @param column 字段名
	 */
	public void addColumn(Property column){
		if(this.columns == null)
			this.columns = new ArrayList<Property>();
		this.columns.add(column);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Property getId() {
		return id;
	}

	public List<Property> getColumns() {
		return columns;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTarget() {
		return target;
	}
	
}