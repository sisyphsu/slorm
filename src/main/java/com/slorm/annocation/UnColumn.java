package com.slorm.annocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置指定字段不需要被收录入数据库映射中。
 * @author sulin
 * @date 2012-4-27 上午09:24:03
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnColumn {
	
}
