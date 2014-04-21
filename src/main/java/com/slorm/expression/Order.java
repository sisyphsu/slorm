package com.slorm.expression;

import com.slorm.core.Property;

/**
 * 表示SQL中的<b>Order by</b>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Order implements Expression{
	
	/**
	 * 是否升序
	 */
	private boolean isAsc;

	/**
	 * 字段列
	 */
	private Property prop;

	/**
	 * 构造方法
	 * @param isAsc
	 * @param prop
	 */
	public Order(boolean isAsc, Property prop) {
		this.isAsc = isAsc;
		this.prop = prop;
	}
	
	public boolean isAsc() {
		return isAsc;
	}

	public Property getProp() {
		return prop;
	}
	
}