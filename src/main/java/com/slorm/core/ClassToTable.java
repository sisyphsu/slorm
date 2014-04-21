package com.slorm.core;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaBean&Table映射类。
 * 
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class ClassToTable {
	
	/**
	 * 此表对应的数据源
	 */
	private String dataSource;
	
	/**
	 * 此Java类对应的表名
	 */
	private String tableName;
	
	/**
	 * 此映射类对应的JavaBean
	 */
	private Class<?> clazz;
	
	/**
	 * 此类对应表的主键
	 */
	private Property id;
	
	/**
	 * 此Java类中属性与表中列的映射关系列表
	 */
	private List<Property> props = new ArrayList<Property>();
	
	/**
	 * reference.
	 */
	private List<Reference> quotes = new ArrayList<Reference>();
	
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Property> getProps() {
		return props;
	}

	public void setProps(List<Property> props) {
		this.props = props;
	}

	public void setId(Property id) {
		this.id = id;
	}

	public Property getId() {
		return id;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getDataSource() {
		return dataSource;
	}

	public List<Reference> getQuotes() {
		return quotes;
	}

	public void setQuotes(List<Reference> quotes) {
		this.quotes = quotes;
	}

}