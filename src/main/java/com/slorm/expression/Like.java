package com.slorm.expression;

import com.slorm.core.Property;

/**
 * 表示SQL中的 <b>Like</b>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Like implements Expression{

	/**
	 * 字段列
	 */
	private Property prop;
	
	/**
	 * 参数
	 */
	private Object param;

	/**
	 * 构造方法
	 * @param prop
	 * @param param
	 */
	public Like(Property prop, Object param) {
		this.param = param;
		this.prop = prop;
	}
	
	public Property getProp() {
		return prop;
	}

	public Object getParam() {
		return param;
	}
	
}