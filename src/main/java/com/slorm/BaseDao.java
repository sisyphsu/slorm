package com.slorm;

import com.slorm.core.*;
import com.slorm.handler.BaseHandler;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DA操作基类, 此基类提供了一系列用于JDBC操作的扩展方法<br>
 * <h5>使用说明</h5>
 * 此基类提供了三个<b>对象</b>级别的增(<b>$save()</b>)删(<b>$delete()</b>)改(<b>$update()</b>)操作. 除此之外您还可通过<b>$extDao()</b>方法获取到可用于进行<b>类</b>级别增删改查操作的ExtDao对象。<br>
 * 所谓<b>对象</b>级别是指：提供的方法是针对当前对象（数据表中的某一行）进行JDBC操作<br>
 * 所谓<b>类</b>级别是指：提供的方法是针对当前类的所有对象（数据表中的所有行）进行JDBC操作<br>
 * <br>
 * <h5>配置说明</h5>
 * 所有需要借助此基类扩展JDBC操作的Model类都必须直接或间接继承此类。继承之后如果您的Model结构与数据表结构完全吻合则不需要再进行任何配置。<br>
 * <b>默认数据表名与Model类名相同，列名与字段名相同，数据类型自动匹配。默认使用系统中的唯一数据源，如果数据源不存在或不唯一，则抛出运行期异常。<b><br>
 * 如果需要特别指定，可以使用<b>MapInfo注解</b>配置表名和数据源名，这些配置都不是必须存在的，您也可以只配置其中的类名或者数据源名。<br>
 * 除此之外，你也可以使用<b>Column注解</b>配置列名、列数据类型、是否是主键（默认遇见的第一个字段为主键）。需要映射至数据库的字段都必须有getter、setter，若没有则不会被映射。<br>
 * 如果你想指定某个字段不与数据库关联，则在此字段上添加<b>UnColumn注解</b>即可！<br>
 * <br>
 * <h5>自动进行的数据转换</h5>
 * <b>Java类型转换为SQL类型</b>：如果您显式的通过<b>Column注解</b>配置了SQL类型，则此插件会<b>尝试</b>自动将Java类型转换为SQL类型。<br>
 * <b>SQL类型转换为Java类型</b>：此转换是在select操作之后进行的，此转换会<b>尝试</b>将从数据库中查询到的SQL类型转换为Model类中字段类型。<br>
 * <br>
 * @author sulin
 * @date 2012-4-17 上午09:28:17
 */
@SuppressWarnings("unchecked")
public abstract class BaseDao<T> {

	public Serializable $save(){
		return BaseHandler.save(this);
	}
	
	public List<Serializable> $saveAll(List<T> ts){
		List<Serializable> result = new ArrayList<Serializable>();
		for(T t : ts)
			result.add(BaseHandler.save(t));
		return result;
	}
	
	public long $delete(){
		return BaseHandler.delete(this.getClass(), null, this);
	}
	
	public long $deleteAll(List<T> ts){
		long result = 0l;
		for(T t : ts){
			result += BaseHandler.delete(this.getClass(), null, t);
		}
		return result;
	}

	public long $update(){
		if(MapContainer.getCCT(this.getClass()).getId().getter(this) == null)
			throw new NullPointerException("The primary key mustn't be null !");
		return BaseHandler.update(null, this);
	}
	
	public long $updateAll(List<T> ts){
		ClassToTable ctt = MapContainer.getCCT(this.getClass());
		for(T t : ts)
			Assert.isNotNull(ctt.getId().getter(t), "The primary key of the object to update mustn't be null !");
		long result = 0l;
		for(T t : ts)
			result += BaseHandler.update(null, t);
		return result;
	}
	
	public boolean $load(){
		ClassToTable ctt = MapContainer.getCCT(this.getClass());
		Object t = BaseHandler.get(this.getClass(), null, this, (String[])null);
		if(t != null){
			for(Property p : ctt.getProps()){
				p.setter(this, p.getter(t));
			}
			return true;
		}
		return false;
	}

	public boolean $load(String fields){
		fields = SQLContainer.filterSQL(fields, this.getClass());
		ClassToTable ctt = MapContainer.getCCT(this.getClass());
		Object t = BaseHandler.get(this.getClass(), null, this, _$getFields(fields));
		if(t != null){
			for(Property p : ctt.getProps())
				p.setter(this, p.getter(t));
			return true;
		}
		return false;
	}
	
	public T $get(){
		return (T) BaseHandler.get(this.getClass(), null, this, (String[])null);
	}
	
	public T $get(String fields){
		fields = SQLContainer.filterSQL(fields, this.getClass());
		return (T) BaseHandler.get(this.getClass(), null, this, _$getFields(fields));
	}
	
	public List<T> $list(){
		return (List<T>) BaseHandler.select(this.getClass(), null, this, -1, -1, (String[])null);
	}
	
	public List<T> $list(String fields){
		fields = SQLContainer.filterSQL(fields, this.getClass());
		return (List<T>) BaseHandler.select(this.getClass(), null, this, -1, -1, _$getFields(fields));
	}

	public List<T> $page(int from, int size){
		return (List<T>) BaseHandler.select(this.getClass(), null, this, from, size, (String[])null);
	}

	public List<T> $page(int from, int size, String fields){
		fields = SQLContainer.filterSQL(fields, this.getClass());
		return (List<T>) BaseHandler.select(this.getClass(), null, this, from, size, _$getFields(fields));
	}
	
	public List<T> $selectBySQL(String sql, Object[] params, int[] types){
		sql = SQLContainer.filterSQL(sql, this.getClass());
		return (List<T>) BaseHandler.selectBySQL(this.getClass(), sql, params, types);
	}
	
	public ResultSet $nativeSQL(String sql, Object[] params, int[] types){
		sql = SQLContainer.filterSQL(sql, this.getClass());
		return BaseHandler.nativeSQL(getClass(), sql, params, types);
	}
	
	public Restriction<T> $createRestriction(){
		return new Restriction<T>((Class<T>) this.getClass());
	}
	
	public Connection $getConnection(){
		ClassToTable ctt = MapContainer.getCCT(this.getClass());
		return ConnectionContainer.getConnection(ctt.getDataSource());
	}
	
	public void $releaseConnection(){
		ClassToTable ctt = MapContainer.getCCT(this.getClass());
		try {
			ConnectionContainer.releaseConnection(ctt.getDataSource());
		} catch (SQLException e) {
			throw new RuntimeException("exception occurs during closing the thread-connection.", e);
		}
	}
	
	private String[] _$getFields(String columns){
		Assert.isNotNull(columns);
		ClassToTable ctt = MapContainer.getCCT(this.getClass());
		List<String> fields = new ArrayList<String>();
		String[] columnsArray = columns.split(",");
		if(columnsArray == null){
			for(Property p : ctt.getProps()){
				if(p.getName().equals(columns.trim()))
					fields.add(columns.trim());
			}
			throw new RuntimeException("Unknow field : "+columns.trim());
		}else{
			X : for(String temp : columnsArray){
				for(Property p : ctt.getProps()){
					if(p.getName().equals(temp.trim())){
						if(fields.contains(temp.trim())){
							throw new RuntimeException("Duplicate field : "+temp.trim());
						}
						fields.add(temp.trim());
						continue X;
					}
				}
				throw new RuntimeException("Unknow field : "+temp.trim());
			}
		}
		
		return fields.toArray(new String[]{});
	}
	
}
