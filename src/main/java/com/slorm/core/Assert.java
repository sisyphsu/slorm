package com.slorm.core;

import java.util.Collection;

/**
 * 断言。此类为最终类且线程安全。<br/>
 * 这个类的方法看起来挺蛋疼的，算是程序中的自我安慰吧
 * 
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class Assert {
	
	/**
	 * 断言指定对象不为空
	 * @param o
	 */
	public final static void isNotNull(Object o){
		if(o == null)
			throw new NullPointerException();
		if(o instanceof Collection<?>){
			if(((Collection<?>)o).isEmpty())
				throw new IllegalArgumentException("Collection cann't be empty");
		}
	}
	
	/**
	 * 断言指定对象不为空
	 * @param o
	 */
	public final static void isNotNull(Object o, String message){
		if(o == null)
			throw new NullPointerException(message);
		if(o instanceof Collection<?>){
			if(((Collection<?>)o).isEmpty())
				throw new IllegalArgumentException("Collection cann't be empty and " + message);
		}
	}
	
}