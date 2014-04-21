package com.slorm.exception;

/**
 * 表示查询结果集中出现了不能识别的列，这种情况一般都是因为sql语句中使用了as
 * @author sulin 2012-4-14
 * @version 1.0
 */
public class UnknownColumnException extends RuntimeException{

	private static final long serialVersionUID = -6807161442593568278L;

	public UnknownColumnException(){
		super();
	}
	
	public UnknownColumnException(String message){
		super(message);
	}
	
	public UnknownColumnException(Throwable e){
		super(e);
	}
	
	public UnknownColumnException(String message, Throwable e){
		super(message, e);
	}
	
}