package com.slorm.annocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表&类映射信息
 * @author sulin 2012-4-5
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	
	/**
	 * 数据源名称，当有多个数据源时使用此配置
	 * @return
	 */
	String dataSource() default "";
	
	/**
	 * 表名称，如果为null则使用类简名
	 * @return
	 */
	String tableName() default "";
	
}