package com.slorm.handler;

import com.slorm.connection.ConnectionWrapper;
import com.slorm.core.*;
import com.slorm.exception.NotUniqueResultException;
import com.slorm.handler.impl.MysqlExecutorImpl;
import com.slorm.operation.*;
import com.slorm.proxy.ReflectUtil;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基本的数据持久层操作中转处理器,它负责根据调用者的实际情况组装Operation对象.具体的数据库读写操作由执行器(Executor)完成！
 * 此类是一个最终类、工具类、线程安全类。
 * @author sulin
 * @date 2012-4-16 下午01:44:45
 */
@SuppressWarnings("unchecked")
public final class BaseHandler {
	
	/**
	 * 执行器映射表,因为不同的数据库需要采用不同的SQL语句生成规则,因此需要针对不同的DB生成独有的Executor.<br/>
	 * 此映射表是以<b>数据库版本名:数据操作执行器</b>进行映射的。
	 */
	private final static ConcurrentMap<String, Executor> executors = new ConcurrentHashMap<String, Executor>();
	
	/**
	 * 数据保存操作处理
	 * @param o 需要保存的对象
	 * @return 此对象对应的数据库主键
	 */
	public static Serializable save(Object o){
		Assert.isNotNull(o, "the object to save mustn't be null !");
		ClassToTable ctt = MapContainer.getCCT(o.getClass());
		
		SaveOperation save = new SaveOperation();
		save.setConn(ConnectionContainer.getConnection(ctt.getDataSource()));
		save.setTableName(ctt.getTableName());
		save.setId(ctt.getId());
		for(Property p : ctt.getProps()){
			if(p.getter(o)!=null)
				save.addColumn(p);
		}
		save.setTarget(o);
		
		try {
			return getExecutor(save.getConn()).save(save);
		} catch (SQLException e) {
			throw new RuntimeException("Exception occurs during executing JDBC INSERT ! ", e);
		} finally {
            try {
                save.getConn().close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
	
	/**
	 * 数据删除操作处理。
	 * @param clazz 
	 * @param r 删除操作约束条件
	 * @param o 删除对象，此对象中所有非空字段都会被添加入r中
	 * @return 删除的行数
	 */
	public static<T> long delete(Class<T> clazz, Restriction<T> r, Object o){
		if(r==null && o==null)
			throw new NullPointerException("All the arguements is null, i cann't decide how to delete ! ");
		ClassToTable ctt = MapContainer.getCCT(clazz);
		if(r == null)
			r = new Restriction<T>(clazz);
		if(o != null){
			for(Property p : ctt.getProps()){
				Object value = p.getter(o);
				if(value != null)
					r.equal(p.getName(), value);
			}
		}
		
		DeleteOperation delete = new DeleteOperation();
		delete.setTableName(ctt.getTableName());
		delete.setRestriction(r);
		delete.setConn(ConnectionContainer.getConnection(ctt.getDataSource()));
		try {
			return getExecutor(delete.getConn()).delete(delete);
		} catch (SQLException e) {
			throw new RuntimeException("Exception occurs during JDBC DELETE ! ", e);
		} finally {
            try {
                delete.getConn().close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
	
	/**
	 * 数据更新操作处理。
	 * @param r 更新操作约束条件，如果为空则表示只更新本身，此时主键必须不为空
	 * @param o 更新为什么样（此对象的null字段为空）
	 * @return 更新的行数
	 */
	public static<T> long update(Restriction<T> r, T o){
		Assert.isNotNull(o, "The object used for update mustn't be null !");
		
		ClassToTable ctt = MapContainer.getCCT(o.getClass());
		Assert.isNotNull(ctt);
		if(r==null){
			Object id = ctt.getId().getter(o);
			if(id == null)
				throw new IllegalArgumentException("The primary key of the object used for update mustn't be null !");
			r = new Restriction<T>((Class<T>)o.getClass());
			r.equal(ctt.getId().getName(), id);
		}
		
		UpdateOperation update = new UpdateOperation();
		update.setTableName(ctt.getTableName());
		update.setRestriction(r);
		update.setTarget(o);
		update.setConn(ConnectionContainer.getConnection(ctt.getDataSource()));
		for(Property p : ctt.getProps()){
			if(p.equals(ctt.getId()))
				continue;
			if(p.getter(o)!=null)
				update.addColumn(p);
		}
		
		try {
			return getExecutor(update.getConn()).update(update);
		} catch (SQLException e) {
			throw new RuntimeException("Exception occurs during JDBC UPDATE ! ", e);
		} finally {
            try {
                update.getConn().close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
	
	/**
	 * 数据单一查询操作处理。如果查询到的对象不单一，则抛出NotUniqueResultException异常！！！
	 * @param <T> 泛型
	 * @param clazz 操作类
	 * @param r 查询操作限制条件，可为null
	 * @param o 查询例子（此对象中每个非空字段都会被添加入查询条件中），可为null
	 * @param columns 查询列，如果为null表示查询所有列
	 * @return 查询到的对象
	 */
	public static<T> T get(Class<T> clazz, Restriction<T> r, Object o, String...columns){
		if(r == null)
			r = new Restriction<T>(clazz);
		
		List<T> result = select(clazz, r, o, 0, 1, columns);
		
		if(result==null || result.isEmpty())
			return null;
		if(result.size()==1)
			return result.get(0);
		throw new NotUniqueResultException();
	}

	/**
	 * 数据查询操作处理。
	 * @param <T> 泛型
	 * @param clazz 操作类
	 * @param r 查询操作限制条件，可为null
	 * @param o 查询例子（此对象中每个非空字段都会被添加入查询条件中），可为null
	 * @param from 分页参数，分页起始索引，如果为0表示不分页
	 * @param size 分页参数，分页页面大小，如果为0表示不分页
	 * @param columns 查询列，如果为null表示查询所有列
	 * @return 查询到的对象
	 */
	public static<T> List<T> select(Class<T> clazz, Restriction<T> r, Object o, int from, int size, String...columns){
		ClassToTable ctt = MapContainer.getCCT(clazz);
		if(r == null)
			r = new Restriction<T>(clazz);
		if(o != null){
			for(Property p : ctt.getProps()){
				Object value = p.getter(o);
				if(value != null)
					r.equal(p.getName(), value);
			}
		}
		if(from>=0 && size>=0)
			r.limit(from, size);
		
		SelectOperation select = new SelectOperation();
		select.setTableName(ctt.getTableName());
		select.setConn(ConnectionContainer.getConnection(ctt.getDataSource()));
		select.setRestriction(r);
		if(columns != null){
			for(Property p : ctt.getProps()){
				for(String column : columns)
					if(p.getName().equals(column))
						select.addColumn(p);
			}
		}else{
			select.setColumns(ctt.getProps());
		}
		
		List<T> result;
		try {
			result = parseResultSet(getExecutor(select.getConn()).select(select), clazz);
		} catch (SQLException e) {
			throw new RuntimeException("Exception occurs during JDBC SELECT ! ", e);
		} finally {
            try {
                select.getConn().close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

		if(result==null || result.isEmpty())
			return null;
		return result;
	}
	
	
	/**
	 * 根据指定的sql语句进行的查询操作处理
	 * @param <T> 泛型
	 * @param sql sql语句
	 * @param params sql语句中需要的预处理参数
	 * @param types 预处理参数的SQL类型
	 * @return 查询到的对象
	 */
	public static<T> List<T> selectBySQL(Class<T> clazz, String sql, Object[] params, int[] types){
		return parseResultSet(nativeSQL(clazz, sql, params, types), clazz);
	}
	
	/**
	 * 根据指定的SQL语句进行查询操作处理
	 * @param clazz 此参数用于获取数据库连接
	 * @param sql sql语句
	 * @param params 预处理参数
	 * @param types 预处理参数SQL数据类型
	 * @return 查询到的离线结果集
	 */
	public static List<Map<String, Object>> nativeSQL(Class<?> clazz, String sql, Object[] params, int[] types){
		ClassToTable ctt = MapContainer.getCCT(clazz);
		SQLSelectOperation sqlSelect = new SQLSelectOperation();
		sqlSelect.setConn(ConnectionContainer.getConnection(ctt.getDataSource()));
		sqlSelect.setParams(params);
		sqlSelect.setSql(sql);
		sqlSelect.setTypes(types);
		
		try {
			return getExecutor(sqlSelect.getConn()).selectBySQL(sqlSelect);
		} catch (SQLException e) {
			throw new RuntimeException("Exception occurs during JDBC SELECT by SQL ! ", e);
		} finally {
            try {
                sqlSelect.getConn().close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
	
	/**
	 * 根据指定SQL函数进行查询
	 * @param <T>
	 * @param clazz
	 * @param distinct
	 * @param type
	 * @param field
	 * @param r
	 * @return
	 */
	public static<T> Object function(Class<T> clazz, boolean distinct, int type, String field, Restriction<T> r){
		ClassToTable ctt = MapContainer.getCCT(clazz);
		FunctionOperation function = new FunctionOperation();
		function.setConn(ConnectionContainer.getConnection(ctt.getDataSource()));
		function.setDistinct(distinct);
		function.setFunction(type);
		function.setRestriction(r);
		function.setTableName(ctt.getTableName());
		if(field == null)
			function.setProperty(null);
		else
			for(Property p : ctt.getProps()){
				if(p.getName().equals(field))
					function.setProperty(p);
			}
		try {
			return getExecutor(function.getConn()).function(function);
		} catch (SQLException e) {
			throw new RuntimeException("Exception occurs during JDBC SELECT by FUNCTION ! ", e);
		} finally {
            try {
                function.getConn().close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
	
	/**
	 * 离线结果集解析方法，根据指定java类型clazz，从结果集中提取出属于此clazz的所有列并组装出新的对象。
	 * @param <T> 泛型
	 * @param rs 离线结果集
	 * @param clazz java类型
	 * @return clazz的实例数组
	 */
	public static<T> List<T> parseResultSet(List<Map<String, Object>> rs, Class<T> clazz){
		ClassToTable ctt = MapContainer.getCCT(clazz);
        List<T> result = new ArrayList<T>();
        for (Map<String, Object> row : rs){
            T item = (T) ReflectUtil.newInstance(clazz);
            for(Property p : ctt.getProps()){
                p.setter(item, row.get(p.getColumn()));
            }
            result.add(item);
        }
        return result;
	}
	
	// 获取执行器
	private static Executor getExecutor(Connection conn) throws SQLException{
		String dbName = conn.getMetaData().getDatabaseProductName().toLowerCase();
		Executor result = executors.get(dbName);
		if(result == null){
			if(dbName.equals("mysql")){
				result = new MysqlExecutorImpl();
			}else if(dbName.equals("oracle")){
				// TODO
			}
			if(result != null)
				result = executors.putIfAbsent(dbName, result);
		}
		return executors.get(dbName);
	}
	
}