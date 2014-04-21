package com.slorm.core;

import com.slorm.expression.*;
import com.slorm.handler.BaseHandler;
import com.slorm.operation.FunctionOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Support programmatic SQL operation.<br>
 * The character '$' in front of the method means that method is used for SQL operating.
 * 
 * 2013-1-14 update ------------
 * Add 'reset' method. 
 * -------------
 * 
 * @author sulin 2012-4-5
 * @update sulin 2013-1-14
 * @version 1.1
 */
public final class Restriction<T> {

	private Class<T> clazz;

	private List<Expression> expressions = new ArrayList<Expression>();

	/**
	 * The unique construtor
	 * 
	 * @param clazz
	 *            javaType(POJO type)
	 */
	public Restriction(Class<T> clazz) {
		Assert.isNotNull(clazz);
		this.clazz = clazz;
	}

	/**
	 * Reset current restriction, clear all limit expressions. <br>
	 */
	public void reset(){
		this.expressions.clear();
	}
	
	/**
	 * Add a 'between' restriction<br>
	 * SQL: Between ? and ?
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param from
	 *            the first parameter
	 * @param to
	 *            the second parameter
	 * @return an SQL statement fragment
	 */
	public Expression between(String propertyName, Object from, Object to) {
		Expression expression = new Between(_getColumnName(propertyName), from, to);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'equal' restriction<br>
	 * SQL: name=?
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param value
	 *            value
	 * @return an SQL statement fragment
	 */
	public Expression equal(String propertyName, Object value) {
		Expression expression = new Compare(Compare.EQUAL, _getColumnName(propertyName), value);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'not equal' restriction<br>
	 * SQL: name<>?
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param value
	 *            value
	 * @return an SQL statement fragment
	 */
	public Expression notEqual(String propertyName, Object value) {
		Expression expression = new Compare(Compare.NOT_EQUAL, _getColumnName(propertyName), value);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'not less' restriction<br>
	 * SQL: name>=?
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param value
	 *            value
	 * @return an SQL statement fragment
	 */
	public Expression greatEqual(String propertyName, Object value) {
		Expression expression = new Compare(Compare.LARGE_EQUAL, _getColumnName(propertyName), value);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'greater' restriction<br>
	 * SQL: name>?
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param value
	 *            value
	 * @return an SQL statement fragment
	 */
	public Expression great(String propertyName, Object value) {
		Expression expression = new Compare(Compare.LARGE, _getColumnName(propertyName), value);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'not greater' restriction<br>
	 * SQL: name<=?
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param value
	 *            value
	 * @return an SQL statement fragment
	 */
	public Expression lowEqual(String propertyName, Object value) {
		Expression expression = new Compare(Compare.SMAILL_EQUAL, _getColumnName(propertyName), value);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'less' restriction<br>
	 * SQL: name<?
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param value
	 *            value
	 * @return an SQL statement fragment
	 */
	public Expression low(String propertyName, Object value) {
		Expression expression = new Compare(Compare.SMAILL, _getColumnName(propertyName), value);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'is null' restriction<br>
	 * SQL: name is null
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @return an SQL statement fragment
	 */
	public Expression isNULL(String propertyName) {
		Expression expression = new IsNULL(true, _getColumnName(propertyName));
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'is not null' restriction<br>
	 * SQL: name is not null
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @return an SQL statement fragment
	 */
	public Expression isNotNULL(String propertyName) {
		Expression expression = new IsNULL(false, _getColumnName(propertyName));
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'like' restriction<br>
	 * SQL: name like ?
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param value
	 *            value
	 * @return an SQL statement fragment
	 */
	public Expression like(String propertyName, Object value) {
		Expression expression = new Like(_getColumnName(propertyName), value);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'in' restricton<br>
	 * SQL: name in ?,?,?...
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @param value
	 *            value array
	 * @return an SQL statement fragment
	 */
	public Expression in(String propertyName, Object... value) {
		Expression expression = new In(_getColumnName(propertyName), value);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'order by asc' restriction<br>
	 * SQL: order by ? asc
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @return an SQL statement fragment
	 */
	public Expression asc(String propertyName) {
		Expression expression = new Order(true, _getColumnName(propertyName));
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'order by desc' restriction<br>
	 * SQL: order by ? desc
	 * 
	 * @param propertyName
	 *            property's name of POJO
	 * @return an SQL statement fragment
	 */
	public Expression desc(String propertyName) {
		Expression expression = new Order(false, _getColumnName(propertyName));
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add a 'page limit' restriction<br>
	 * SQL: limit ?, ?
	 * 
	 * @param from
	 *            start row, from 0.
	 * @param pageSize
	 *            page size
	 * @return an SQL statement fragment
	 */
	public Expression limit(int from, int pageSize) {
		Expression expression = new Limit(from, pageSize);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add some 'or' restrictions<br>
	 * SQL: Expression or Expression or Expression...
	 * 
	 * @param expressions some SQL statement fragments
	 * @return an SQL statement fragment
	 */
	public Expression or(Expression... expressions) {
		for (Expression e : expressions)
			this.expressions.remove(e);
		Expression expression = new Or(expressions);
		this.expressions.add(expression);
		return expression;
	}

	/**
	 * Add some 'and' restrictions<br>
	 * SQL: Expression and Expression and Expression...
	 * 
	 * @param expressions some SQL statement fragments
	 * @return an SQL statement fragment
	 */
	public Expression and(Expression... expressions) {
		for (Expression e : expressions)
			this.expressions.remove(e);
		Expression expression = new And(expressions);
		this.expressions.add(expression);
		return expression;
	}

	/********************************** Operating ******************************/
	/**
	 * Delete all the rows meet this restriction in the database. <br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @return Number of the rows deleted.
	 */
	public long $delete() {
		return BaseHandler.delete(clazz, this, null);
	}

	/**
	 * Update all the rows meet this restriction in the database to the specific instance [t]. <br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param t
	 *            update to what.
	 * @return Number of the rows updated.
	 */
	public long $update(T t) {
		return BaseHandler.update(this, t);
	}

	/**
	 * Select the unique Model meet this restriction in the database, if it doesn't exist, return null.
	 * if it isn't unique, a NotUniqueResultException will be thrown out. <br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @return The unique Model selected.
	 */
	public T $get() {
		return BaseHandler.get(clazz, this, null, (String[]) null);
	}

	/**
	 * Select the named fields of the unique Model meet this restriction in the database. <br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param fields
	 *            named fields's example : "field1, field2, field3, field4"
	 * @return The unique model selected
	 */
	public T $getColumns(String fields){
		fields = SQLContainer.filterSQL(fields, clazz);
		return BaseHandler.get(clazz, this, null, this._getFields(fields));
	}
	
	/**
	 * Select all fields of the Models meet this restriction in the database.<br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @return The models selected
	 */
	public List<T> $list() {
		return BaseHandler.select(clazz, this, null, -1, -1, (String[]) null);
	}

	/**
	 * Select the named fields of the Models meet this restriction in the database. <br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param fields
	 *            named fields's example : "field1, field2, field3, field4"
	 * @return The models selected
	 */
	public List<T> $listColumns(String fields) {
		fields = SQLContainer.filterSQL(fields, clazz);
		return BaseHandler.select(clazz, this, null, -1, -1, (String[]) _getFields(fields));
	}

	/**
	 * 分页查询.<br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @return The models selected
	 */
	public List<T> $page(int from, int size) {
		return BaseHandler.select(clazz, this, null, from, size, (String[]) null);
	}

	/**
	 * 分页查询. <br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param fields
	 *            named fields's example : "field1, field2, field3, field4"
	 * @return The models selected
	 */
	public List<T> $pageColumns(int from, int size, String fields) {
		fields = SQLContainer.filterSQL(fields, clazz);
		return BaseHandler.select(clazz, this, null, from, size, (String[]) _getFields(fields));
	}
	
	/**
	 * Select the top 'count' rows from the models meet this restriction in the database.<br>
	 * @param count
	 *            The number of records to select.
	 * @return the top 'count' records.
	 */
	public List<T> $top(int count){
		this.limit(0, count);
		return this.$list();
	}
	
	/**
	 * Select the average value of the named field of the models meet this restriction in the database.<br>
	 * SQL Function: avg([distinct] column)<br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param distinct
	 *            add 'distinct' in the SQL function avg()?
	 * @param field
	 *            property's name of POJO
	 * @return the average value.
	 */
	public Object $avg(boolean distinct, String field){
		return BaseHandler.function(clazz, distinct, FunctionOperation.AVG, field, this);
	}

	/**
	 * Select the sum value of the named field of the models meet this restriction in the database.<br>
	 * SQL Function: sum([distinct] column)<br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param distinct
	 *            add 'distinct' in the SQL function sum()?
	 * @param field
	 *            property's name of POJO
	 * @return the sum value.
	 */
	public Object $sum(boolean distinct, String field){
		return BaseHandler.function(clazz, distinct, FunctionOperation.SUM, field, this);
	}
	
	/**
	 * Select the minimum value of the named field of the models meet this restriction in the database. <br>
	 * SQL Function: min(column)<br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param field
	 *            property's name of POJO
	 * @return the minimum value.
	 */
	public Object $min(String field){
		return BaseHandler.function(clazz, false, FunctionOperation.MIN, field, this);
	}
	
	/**
	 * Select the maximum value of the named field of the models meet this restriction in the database. <br>
	 * SQL Function: max(column)<br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param field
	 *            property's name of POJO
	 * @return the maximum value.
	 */
	public Object $max(String field){
		return BaseHandler.function(clazz, false, FunctionOperation.MAX, field, this);
	}
	
	/**
	 * Select the amount of the row of the named not-null field meet this restriction in the database. <br>
	 * SQL Function: count([distinct] column/*)<br>
	 * <b><i>Maybe a SQLException occurs</i></b>
	 * 
	 * @param distinct
	 *            add 'distinct' in the SQL function count()?
	 * @param field
	 *            property's name of POJO, if 'field' is null, count(*) will be executed
	 * @return the rows count
	 */
	public long $count(boolean distinct, String field){
		return (Long) BaseHandler.function(clazz, false, FunctionOperation.COUNT, field, this);
	}
	
	/*****************************Assistent Methods*******************************/
	protected List<Expression> getExpressions() {
		return expressions;
	}
	
	// 获取指定字段名或列名对应的Property对象
	private Property _getColumnName(String propertyName) {
		List<Property> props = MapContainer.getCCT(clazz).getProps();
		for (Property p : props) {
			if (p.getName().equals(propertyName))
				return p;
		}

		throw new RuntimeException("[" + propertyName + "] wasn't mapped!!!");
	}

	// 从指定字符串中提取Field数组
	private String[] _getFields(String columns) {
		Assert.isNotNull(columns);
		ClassToTable ctt = MapContainer.getCCT(this.clazz);
		List<String> fields = new ArrayList<String>();
		String[] columnsArray = columns.split(",");
		if (columnsArray == null) {
			for (Property p : ctt.getProps()) {
				if (p.getName().equals(columns.trim()))
					fields.add(columns.trim());
			}
			throw new RuntimeException("Unknow field : " + columns.trim());
		} else {
			X: for (String temp : columnsArray) {
				for (Property p : ctt.getProps()) {
					if (p.getName().equals(temp.trim())) {
						if (fields.contains(temp.trim())) {
							throw new RuntimeException("Duplicate field : " + temp.trim());
						}
						fields.add(temp.trim());
						continue X;
					}
				}
				throw new RuntimeException("Unknow field : " + temp.trim());
			}
		}

		return fields.toArray(new String[] {});
	}

}