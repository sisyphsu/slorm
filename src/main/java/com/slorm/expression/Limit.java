package com.slorm.expression;

/**
 * 表示SQL中的<b>分页</b>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Limit implements Expression{

	/**
	 * 起始行
	 */
	private int from;
	
	/**
	 * 最多行数
	 */
	private int pageSize;
	
	/**
	 * 构造方法
	 * @param from
	 * @param pageSize
	 */
	public Limit(int from, int pageSize) {
		this.from = from;
		this.pageSize = pageSize;
	}

	
	public int getFrom() {
		return from;
	}

	public int getPageSize() {
		return pageSize;
	}
	
}