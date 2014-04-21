package com.slorm.expression;

/**
 * 表示SQL中的一系列 <b>and</b>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class And implements Expression{

	/**
	 * 一系列的<b>且</b>
	 */
	private Expression[] expressions;

	public Expression[] getExpressions() {
		return expressions;
	}
	
	/**
	 * 构造方法
	 * @param expressions
	 */
	public And(Expression...expressions) {
		this.expressions = expressions;
	}
	
}