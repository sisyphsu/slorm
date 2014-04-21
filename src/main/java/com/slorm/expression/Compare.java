package com.slorm.expression;

import com.slorm.core.Property;

/**
 * 表示SQL中的<b>=、<>、>=、>、<、<=</b>六种算数运算符
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Compare implements Expression{

	/**
	 * 等于
	 */
	public final static int EQUAL = 1;

	/**
	 * 不等于
	 */
	public final static int NOT_EQUAL = 2;
	
	/**
	 * 大于
	 */
	public final static int LARGE = 3;
	
	/**
	 * 小于
	 */
	public final static int SMAILL = 4;
	
	/**
	 * 大于等于
	 */
	public final static int LARGE_EQUAL = 5;
	
	/**
	 * 小于等于
	 */
	public final static int SMAILL_EQUAL = 6;
	
	/**
	 * 算数关系，必须为1-6
	 */
	private int type;
	
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
	 * @param type
	 * @param prop
	 * @param param
	 */
	public Compare(int type, Property prop, Object param) {
		this.type = type;
		this.prop = prop;
		this.param = param;
	}
	
	public int getType() {
		return type;
	}

	public Property getProp() {
		return prop;
	}

	public Object getParam() {
		return param;
	}
	
}