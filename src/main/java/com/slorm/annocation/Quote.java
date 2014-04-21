package com.slorm.annocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this Annotation means the mapping query, the query will be auto-executed.
 * @author sulin
 * @version 1.0
 */
@Target(value=ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Quote {

	/**
	 * a piece of SQL statement, it will be used for mapping query. 
	 * it may like: <b>target.xxx=this.xxxx && target.xxx<>'1' or target.xxx like '%this.xxx%'</b>. 
	 * <b>target</b> means the name of query object, <b>this</b> means current class, 
	 * and all <b>this.xxxx</b> will be replaced by <b>?</b>. 
	 */
	String value();

}