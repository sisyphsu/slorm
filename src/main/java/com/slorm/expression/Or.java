package com.slorm.expression;

/**
 * 表示SQL中的 一系列<b>or</b>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Or implements Expression{

	/**
	 * 一系列的<b>或</b>
	 */
	private Expression[] expressions;

	/**
	 * 构造方法
	 * @param expressions
	 */
	public Or(Expression...expressions) {
		this.expressions = expressions;
	}
	
	public Expression[] getExpressions() {
		return expressions;
	}
	
}