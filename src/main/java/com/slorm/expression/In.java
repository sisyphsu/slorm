package com.slorm.expression;

import com.slorm.core.Property;

/**
 * 表示SQL中的 <b>IN(...)</b>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class In implements Expression{
	
	/**
	 * 字段列名
	 */
	private Property prop;
	
	/**
	 * 参数数组
	 */
	private Object[] params;

	/**
	 * 构造方法
	 * @param prop
	 * @param params
	 */
	public In(Property prop, Object...params) {
		this.params = params;
		this.prop = prop;
	}
	
	public Property getProp() {
		return prop;
	}

	public Object[] getParams() {
		return params;
	}
	
}