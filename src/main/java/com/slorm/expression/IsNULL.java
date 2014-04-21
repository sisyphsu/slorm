package com.slorm.expression;

import com.slorm.core.Property;

/**
 * 表示SQL中的 <b>IS NULL、IS NOT NULL</b>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class IsNULL implements Expression{

	/**
	 * 是否为空
	 */
	private boolean isNull;
	
	/**
	 * 字段列
	 */
	private Property prop;

	/**
	 * 构造方法
	 * @param isNull
	 * @param prop
	 */
	public IsNULL(boolean isNull, Property prop) {
		this.isNull = isNull;
		this.prop = prop;
	}
	
	public boolean isNull() {
		return isNull;
	}

	public Property getProp() {
		return prop;
	}
	
}
