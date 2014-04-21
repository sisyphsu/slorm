package com.slorm.expression;

import com.slorm.core.Property;

/**
 * 表示SQL中的<b>Between...and...</b>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Between implements Expression{

	/**
	 * 字段列
	 */
	private Property prop;
	
	/**
	 * between起始点
	 */
	private Object from;
	
	/**
	 * between终止点
	 */
	private Object to;

	/**
	 * 构造方法
	 * @param prop
	 * @param from
	 * @param to
	 */
	public Between(Property prop, Object from, Object to) {
		this.prop = prop;
		this.from = from;
		this.to = to;
	}

	public Property getProp() {
		return prop;
	}

	public Object getFrom() {
		return from;
	}

	public Object getTo() {
		return to;
	}
	
}