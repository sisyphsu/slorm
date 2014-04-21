package com.slorm.core;

import com.slorm.proxy.ReflectUtil;

/**
 * This class means a field mapped to a database column. 
 * 
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Property {
	
	/**
	 * field's name.
	 */
	private String name;
	
	/**
	 * field's java-type.
	 */
	private Class<?> type;
	
	/**
	 * database's column name, it defaults to the same with the name.
	 */
	private String column;
	
	/**
	 * column's sql-type.
	 */
	private int columnType;

	/**
	 * Call the getXxx method on the specific object,
	 * this method will try to transform current field's value in the specific object, 
	 * and then return the value.
	 * @param target specific object
	 * @return value
	 */
	public Object getter(Object target){
		Object result = ReflectUtil.get(target, this.name);
		return DataConverter.getSqlData(result, this.columnType);
	}
	
	/**
	 * Call the setXxx method on the specific object,
	 * this method will try to transform the given value to suitable java-type data firstly, 
	 * and then set into the specific object.
	 * @param target the specific object.
	 * @param value the value to set into the specific object.
	 */
	public void setter(Object target, Object value){
		ReflectUtil.set(target, this.name, DataConverter.getJavaData(value, this.type));
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public int getColumnType() {
		return columnType;
	}

}