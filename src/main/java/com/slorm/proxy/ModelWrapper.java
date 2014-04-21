package com.slorm.proxy;

/**
 * Model反射操作抽象类，它提供了JavaBean的反射操作API。用于代替低效的Java Reflection API！<br/>
 * 这些操作都是使用动态字节码实现的，运行性能和常规操作完全相同！！！
 * @author sulin
 * @date 2012-4-11 上午07:52:43
 */
public abstract class ModelWrapper{
	
	/**
	 * getXxx操作
	 * @param target 目标Model
	 * @param propertyName Model字段
	 * @return
	 */
	public abstract Object get(Object target, String propertyName);

	/**
	 * setXxx操作
	 * @param target 目标Model
	 * @param propertyName Model字段
	 * @param propertyValue
	 */
	public abstract void set(Object target, String propertyName, Object propertyValue);
	
	/**
	 * 构造对象，代替Class.newInstance()操作
	 * @return
	 */
	public abstract Object newInstance();
	
}