package com.slorm.core;

import com.slorm.BaseDao;
import com.slorm.proxy.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class means a reference.
 * @author sulin
 * @version 1.0
 */
public final class Reference{
	
	/**
	 * the field's name
	 */
	private String name;
	
	/**
	 * this java-type of the class contains this Reference.
	 */
	private Class<?> type;
	
	/**
	 * the field's java-type
	 */
	private Class<?> targetType;
	
	/**
	 * the field's real java-type 
	 */
	private Class<?> realTargetType;
	
	/**
	 * description string, a piece of sql statement
	 * examples: target.xxx=this.xxx || target.xxx>'000' 
	 */
	private String sql;

	/**
	 * all the "this.xxx" in the sqlPiece.
	 */
	private List<String> thisName = new ArrayList<String>();

	/**
	 * all the "this.xxx"'s sql-type in the sqlPiece.
	 */
	private List<Integer> thisType = new ArrayList<Integer>();

	/**
	 * Initialize this Reference, this method should be invoked after the Class has been parsed.
	 */
	public void initialize(){
		// 处理targetType,因为此时它有可能是List,Set等等类型,需要提取出泛型类型
		if(BaseDao.class.isAssignableFrom(targetType)){
			this.realTargetType = this.targetType;
		}else{
			if(List.class.equals(targetType) || Set.class.equals(targetType)){
				try {
					Field field = this.type.getDeclaredField(this.name);
					this.realTargetType = (Class<?>)(((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
				} catch (Exception e) {
					throw new RuntimeException("Cann't realize the Quote-type : " + this.targetType);
				}
			}else{
				throw new RuntimeException("Cann't realize the Quote-type : " + this.targetType);
			}
		}
		// parse the sql piece and create a new sql statement.
		ClassToTable ctt = MapContainer.getCCT(type);
		this.sql = this.sql.trim();
		this.sql = this.sql.replaceAll("target\\.", "");
		this.sql = this.sql.replaceAll(this.name+"\\.", "");
		Matcher m = Pattern.compile("(this\\.[a-zA-Z_\\$][0-9a-zA-Z_\\$]*)").matcher(this.sql);
		while(m.find()){
			String name = m.group().replace("this.", "");
			this.thisName.add(name);
		}
		this.sql = this.sql.replaceAll("this\\.[a-zA-Z_\\$][0-9a-zA-Z_\\$]*", "?");
		if(!"".equals(this.sql))
			this.sql = "SELECT * FROM `" + MapContainer.getCCT(this.realTargetType).getTableName() + "` WHERE " + this.sql;
		else
			this.sql = "SELECT * FROM `" + MapContainer.getCCT(this.realTargetType).getTableName() + ctt.getTableName(); // 卧槽，忘了这个代码是什么意思。
		
		// validate the this.field that appears in the sql statement.
		for(String field : this.thisName){
			boolean flag = false;
			for(Property p : ctt.getProps()){
				if(field.equals(p.getName()) || field.equals(p.getColumn())){
					this.thisType.add(p.getColumnType());
					flag = true;
					break;
				}
			}
			if(!flag)
				throw new RuntimeException("Unknow field or column ["+field+"] in the class ["+ctt.getClazz().getName()+"]!");
		}
	}

	public Class<?> getRealTargetType() {
		return realTargetType;
	}

	public void setRealTargetType(Class<?> realTargetType) {
		this.realTargetType = realTargetType;
	}

	public void setThisType(List<Integer> thisType) {
		this.thisType = thisType;
	}

	/**
	 * Call the setXxx method on the specific object,
	 * this method will try to transform the given value to suitable java-type data firstly, 
	 * and then set into the specific object.
	 * @param target the specific object.
	 * @param value the value to set into the specific object.
	 */
	public<T> void setter(Object target, List<T> value){
		if(BaseDao.class.isAssignableFrom(this.targetType)){
			ReflectUtil.set(target, this.name, value.get(0));
		}else if(List.class.equals(this.targetType)){
			ReflectUtil.set(target, this.name, value);
		}else if(Set.class.equals(this.targetType)){
			ReflectUtil.set(target, this.name, new HashSet<T>(value));
		}else{
			throw new RuntimeException("Unable to complete the proxy type : ["+this.targetType.getName()+"]");
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getSql() {
		return sql;
	}
	
	public void setSql(String sqlPiece) {
		this.sql = sqlPiece;
	}

	public List<String> getThisName() {
		return thisName;
	}

	public void setThisName(List<String> thiss) {
		this.thisName = thiss;
	}

	public List<Integer> getThisType() {
		return thisType;
	}

	public void setTargetType(Class<?> targetType) {
		this.targetType = targetType;
	}

	public Class<?> getTargetType() {
		return targetType;
	}

}