package com.slorm.handler.impl;

import com.slorm.core.*;
import com.slorm.handler.Executor;
import com.slorm.operation.*;
import com.sun.rowset.CachedRowSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.*;
import java.util.List;

/**
 * Mysql数据库的增删改查操作执行器. <br>
 * 此方法内部使用setObject方法，参见<a href='http://brull.iteye.com/blog/194664'>JDBC setObject非主流性能报告</a><br>
 * @author sulin
 * @date 2012-4-18 上午09:39:47
 */
public class MysqlExecutorImpl implements Executor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MysqlExecutorImpl.class);

	/**
	 * 是否打印sql语句
	 */
	private boolean isShowSQL = false;
	
	/**
	 * 保存操作执行方法
	 */
	public Serializable save(SaveOperation save) throws SQLException{
		Serializable id = null;
		// 开始Insert
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append('`' + save.getTableName() + '`').append('(');
		List<Property> props = save.getColumns();
		for (int i=0, size=props.size(); i<size; i++) {
			if(i != 0)
				sb.append(", ");
			sb.append('`' + props.get(i).getColumn() + '`');
		}
		sb.append(") VALUES (");
		for (int i=0, size=props.size(); i<size; i++) {
			if(i != 0)
				sb.append(", ");
			sb.append('?');
		}
		sb.append(')');

		LOGGER.debug("EXECUTE SAVE: {}", sb);
		PreparedStatement ps = save.getConn().prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
		// 装配参数
		for(int i=0, size=save.getColumns().size(); i<size; i++){
			Property p = save.getColumns().get(i);
			Object o = p.getter(save.getTarget());
			try{
				if(p.getColumnType() == Types.BLOB){
					ps.setBinaryStream(i+1, (InputStream)o, ((InputStream)o).available());
				}else if(p.getColumnType() == Types.CLOB){
					ps.setAsciiStream(i+1, (InputStream)o, ((InputStream)o).available());
				}else{
					ps.setObject(i+1, o);
				}
			}catch(IOException e){
				throw new RuntimeException("Exception occurs when write big data.", e);
			}
		}
		// 执行保存并获取ID值
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if(rs.next())
			id = (Serializable) rs.getObject(1);
		if(id == null){
			ClassToTable ctt = MapContainer.getCCT(save.getTarget().getClass());
			id = (Serializable) ctt.getId().getter(save.getTarget());
		}
		rs.close();
		ps.close();
		
		return id;
	}

	/**
	 * 更新操作执行方法
	 */
	public long update(UpdateOperation update) throws SQLException {
		Assert.isNotNull(update);
		ParsedRestriction temp = MysqlRestrictionParser.parseRestriction(update.getRestriction());
		List<Property> columns = update.getColumns();
		// 组装SQL语句
		StringBuilder sb = new StringBuilder("UPDATE ");
		sb.append('`'+update.getTableName()+'`').append(" SET ");
		for(int i=0, size=columns.size(); i<size; i++){
			if(i!=0)
				sb.append(", ");
			sb.append('`' + columns.get(i).getColumn() + '`').append("=?");
		}
		sb.append(temp.getSql());
		LOGGER.debug("EXECUTE UPDATE: {}", sb);
		PreparedStatement ps = update.getConn().prepareStatement(sb.toString());
		// 设置更新参数
		int index = 0, size = 0;
		for(index=0, size=columns.size(); index<size; index++){
			Property p = columns.get(index);
			Object o = p.getter(update.getTarget());
			try{
				if(p.getColumnType() == Types.BLOB){
					ps.setBinaryStream(index+1, (InputStream)o, ((InputStream)o).available());
				}else if(p.getColumnType() == Types.CLOB){
					ps.setAsciiStream(index+1, (InputStream)o, ((InputStream)o).available());
				}else{
					ps.setObject(index+1, o);
				}
			}catch(IOException e){
				throw new RuntimeException("Exception occurs when write big data.", e);
			}
		}
		// 设置查询参数
		size=temp.getColumnValues().size();
		for(int i=0; i<size; i++){
			Property p = temp.getColumns().get(i);
			Object o = temp.getColumnValues().get(i);
			if(p.getColumnType() == Types.OTHER){
				ps.setObject(++index, o);
			}else{
				ps.setObject(++index, o, p.getColumnType());
			}
		}
		int lines = ps.executeUpdate();
		ps.close();
		LOGGER.debug("EXECUTE UPDATE: rows={}", lines);

		return lines;
	}

	/**
	 * 删除操作执行方法
	 */
	public long delete(DeleteOperation delete) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ").append('`' + delete.getTableName() + '`');
		ParsedRestriction pr = MysqlRestrictionParser.parseRestriction(delete.getRestriction());
		sql.append(pr.getSql());
		LOGGER.debug("EXECUTE DELETE: {}", sql);
		PreparedStatement ps = delete.getConn().prepareStatement(sql.toString());
		for(int i=0,size=pr.getColumns().size(); i<size; i++){
			Property p = pr.getColumns().get(i);
			Object o = pr.getColumnValues().get(i);
			if(p.getColumnType() == Types.OTHER){
				ps.setObject(i+1, o);
			}else{
				ps.setObject(i+1, o, p.getColumnType());
			}
		}
		int lines = ps.executeUpdate();
		ps.close();
		LOGGER.debug("EXECUTE DELETE: rows={}", lines);

		return lines;
	}

	/**
	 * 查询操作执行方法
	 */
	public ResultSet select(SelectOperation select) throws SQLException {
		// 解析限定条件
		ParsedRestriction temp = MysqlRestrictionParser.parseRestriction(select.getRestriction());
		List<Property> columns = select.getColumns();
		// 构造SQL语句
		StringBuilder sb = new StringBuilder("SELECT ");
		for(int i=0,size=columns.size(); i<size; i++){
			if(i!=0)
				sb.append(',');
			sb.append('`' + columns.get(i).getColumn() + '`');
		}
		sb.append(" FROM ").append('`' + select.getTableName() + '`').append(temp.getSql());
		// 预处理
		if(isShowSQL)
			System.out.println(sb.toString());
		PreparedStatement ps = select.getConn().prepareStatement(sb.toString());
		for(int i=0,size=temp.getColumns().size(); i<size; i++){
			Object o = temp.getColumnValues().get(i);
			Property p = temp.getColumns().get(i);
			if(p == null){
				ps.setObject(i+1, o);
			}else{
				if(p.getColumnType() == Types.OTHER){
					ps.setObject(i+1, o);
				}else{
					ps.setObject(i+1, o, p.getColumnType());
				}
			}
		}
		// 执行查询并返回离线结果集
		CachedRowSetImpl result = new CachedRowSetImpl();
		ResultSet rs = ps.executeQuery();
		result.populate(rs);
		rs.close();
		ps.close();
		if(isShowSQL)
			System.out.println("select rows : " + result.size());
		
		return result;
	}

	/**
	 * SQL查询操作执行方法
	 */
	public ResultSet selectBySQL(SQLSelectOperation sqlSelect) throws SQLException {
		Assert.isNotNull(sqlSelect);
		if(isShowSQL)
			System.out.println(sqlSelect.getSql());
		PreparedStatement ps = sqlSelect.getConn().prepareStatement(sqlSelect.getSql());
		if(sqlSelect.getParams()!=null){
			if(sqlSelect.getTypes()!=null && sqlSelect.getTypes().length!=sqlSelect.getParams().length){
				throw new IllegalArgumentException("the length of two arguements is not same!");
			}else if(sqlSelect.getTypes()!=null){
				for(int i=0,size=sqlSelect.getParams().length; i<size; i++){
					if(sqlSelect.getTypes()[i] != Types.OTHER)
						ps.setObject(i+1, sqlSelect.getParams()[i],sqlSelect.getTypes()[i]);
					else
						ps.setObject(i+1, sqlSelect.getParams()[i]);
				}
			}else{
				for(int i=0,size=sqlSelect.getParams().length; i<size; i++){
					ps.setObject(i+1, sqlSelect.getParams()[i]);
				}
			}
		}
		// 执行查询并返回离线结果集
		CachedRowSetImpl result = new CachedRowSetImpl();
		ResultSet rs = ps.executeQuery();
		result.populate(rs);
		rs.close();
		ps.close();
		if(isShowSQL)
			System.out.println("select rows : " + result.size());
		
		return result;
	}

	/**
	 * 执行SQL函数
	 */
	public Object function(FunctionOperation function) throws SQLException {
		Assert.isNotNull(function);
		ParsedRestriction pr = MysqlRestrictionParser.parseRestriction(function.getRestriction());
		StringBuilder sb = new StringBuilder("select ");
		if(function.getFunction() == FunctionOperation.AVG)
			sb.append("avg(");
		else if(function.getFunction() == FunctionOperation.COUNT)
			sb.append("count(");
		else if(function.getFunction() == FunctionOperation.MAX)
			sb.append("max(");
		else if(function.getFunction() == FunctionOperation.MIN)
			sb.append("min(");
		else if(function.getFunction() == FunctionOperation.MAX)
			sb.append("max(");
		else if(function.getFunction() == FunctionOperation.SUM)
			sb.append("sum(");
		else
			throw new IllegalArgumentException("the function type ["+function.getFunction()+"] is unknown.");
		if(function.isDistinct())
			sb.append("distinct ");
		if(function.getProperty() == null)
			sb.append('*');
		else
			sb.append('`' + function.getProperty().getColumn() + '`');
		sb.append(')');
		sb.append(" from ").append('`' + function.getTableName() + '`');
		sb.append(pr.getSql());
		if(isShowSQL)
			System.out.println(sb.toString());
		PreparedStatement ps = function.getConn().prepareStatement(sb.toString());
		for(int i=0,size=pr.getColumns().size(); i<size; i++){
			Object o = pr.getColumnValues().get(i);
			ps.setObject(i+1, o);
		}
		
		Object result = null;
		ResultSet rs = ps.executeQuery();
		if(rs.next())
			result = rs.getObject(1);
		rs.close();
		ps.close();

		if(function.getFunction() == FunctionOperation.AVG)
			result = DataConverter.getJavaData(result, function.getProperty().getType());
		else if(function.getFunction() == FunctionOperation.COUNT)
			result = DataConverter.getJavaData(result, Long.class);
		else if(function.getFunction() == FunctionOperation.MAX)
			result = DataConverter.getJavaData(result, function.getProperty().getType());
		else if(function.getFunction() == FunctionOperation.MIN)
			result = DataConverter.getJavaData(result, function.getProperty().getType());
		else if(function.getFunction() == FunctionOperation.MAX)
			result = DataConverter.getJavaData(result, function.getProperty().getType());
		
		return result;
	}
	
}