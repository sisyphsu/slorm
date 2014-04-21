package com.slorm.exception;

/**
 * 表示查询结果集不唯一的异常。
 * @author sulin 2012-4-13
 * @version 1.0
 */
public class NotUniqueResultException extends RuntimeException{

	private static final long serialVersionUID = 7674062215030477410L;
	
	public NotUniqueResultException() {
		super();
	}
	
	public NotUniqueResultException(String message){
		super(message);
	}
	
	public NotUniqueResultException(Throwable e){
		super(e);
	}
	
	public NotUniqueResultException(String message, Throwable e){
		super(message, e);
	}
	
}
