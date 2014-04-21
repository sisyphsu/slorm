package com.slorm.handler.impl;

import com.slorm.core.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * 针对Oracle数据库的已解析过的Restriction
 * @author sulin 2012-4-5
 * @version 1.0
 */
final class ParsedRestriction{
	
	/**
	 * 解析生成的SQL语句
	 */
	private String sql;
	
	/**
	 * SQL语句中涉及的字段列
	 */
	private List<Property> columns = new ArrayList<Property>();
	
	/**
	 * SQL语句中涉及的字段列的值
	 */
	private List<Object> columnValues = new ArrayList<Object>();

	/**
	 * 添加参数
	 * @param prop
	 * @param o
	 */
	public void addColumn(Property prop, Object o){
		this.columns.add(prop);
		this.columnValues.add(o);
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public List<Property> getColumns() {
		return columns;
	}

	public List<Object> getColumnValues() {
		return columnValues;
	}
	
}