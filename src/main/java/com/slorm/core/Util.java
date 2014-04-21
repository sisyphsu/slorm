package com.slorm.core;

import com.slorm.expression.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Util{
	
	/**
	 * 使用指定数组构造List
	 * @param <T>
	 * @param t
	 * @return
	 */
	public final static <T> List<T> toList(T... t){
		List<T> result = new ArrayList<T>(t.length);
		for(T temp : t)
			result.add(temp);
		return result;
	}
	
	/**
	 * 获取指定Restriction中的表达式列表
	 * @param r
	 * @return
	 */
	public final static List<Expression> getExpressions(Restriction<?> r) {
		return r.getExpressions();
	}

}