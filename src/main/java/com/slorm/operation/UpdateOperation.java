package com.slorm.operation;

import com.slorm.core.Property;
import com.slorm.core.Restriction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示一个Update操作。
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class UpdateOperation {

	/**
	 * 操作表名
	 */
	private String tableName;
	
	/**
	 * 操作限制条件
	 */
	private Restriction<?> restriction;
	
	/**
	 * 操作对应的数据库连接
	 */
	private Connection conn;

	/**
	 * 更新列字段
	 */
	private List<Property> columns = new ArrayList<Property>();
	
	/**
	 * 更新目标对象
	 */
	private Object target;
	
	/**
	 * 添加更新参数
	 * @param column
	 */
	public void addColumn(Property column){
		this.columns.add(column);
	}
	
	public String getTableName() {
		return tableName;
	}

	public Restriction<?> getRestriction() {
		return restriction;
	}

	public Connection getConn() {
		return conn;
	}

	public List<Property> getColumns() {
		return columns;
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

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTarget() {
		return target;
	}
	
}