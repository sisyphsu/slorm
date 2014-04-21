package com.slorm.operation;

import com.slorm.core.Property;
import com.slorm.core.Restriction;

import java.sql.Connection;

/**
 * SQL function query.
 * @author sulin
 * @date 2012-7-5
 */
public class FunctionOperation {

	/**
	 * SQL: avg(distinct, column)
	 */
	public static int AVG = 101;
	
	/**
	 * SQL: count(distinct, column)
	 */
	public static int COUNT = 102;
	
	/**
	 *  SQL: min(distinct, column)
	 */
	public static int MIN = 103;
	
	/**
	 *  SQL: max(distinct, column)
	 */
	public static int MAX = 104;
	
	/**
	 *  SQL: sum(distinct, column)
	 */
	public static int SUM = 105;
	
	/**
	 * function type
	 */
	private int function;
	
	/**
	 * It is a distinct function query, or not.<br>
	 * true: 'distinct' will be added to SQL expression. 
	 */
	private boolean distinct;
	
	/**
	 * The column that will be used in the SQL function.
	 */
	private Property property;
	
	/**
	 * The table's name that appeared in the SQL expression.
	 */
	private String tableName;
	
	/**
	 * The restriction will be used in the SQL query.
	 */
	private Restriction<?> restriction;
	
	/**
	 * The database connection will be used in the SQL query.
	 */
	private Connection conn;

	public int getFunction() {
		return function;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public String getTableName() {
		return tableName;
	}

	public Restriction<?> getRestriction() {
		return restriction;
	}

	public Connection getConn() {
		return conn;
	}

	public void setFunction(int function) {
		this.function = function;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setRestriction(Restriction<?> restriction) {
		this.restriction = restriction;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public Property getProperty() {
		return property;
	}
	
}