package com.slorm.annocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;

/**
 * 字段配置
 * @author sulin
 * @date 2012-4-4 下午02:40:48
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	
	/**
	 * 字段列对应数据库表的列名称
	 */
	String columnName() default "";
	
	/**
	 * SQL数据类型, 默认为Types.OTHER
	 */
	int columnType() default Types.OTHER;
	
	/**
	 * 是否是ID
	 */
	boolean isID() default false;
	
}