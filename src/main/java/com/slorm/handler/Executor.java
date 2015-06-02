package com.slorm.handler;

import com.slorm.operation.*;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作规范，每个数据库操作必须实现此接口。
 * @author sulin
 * @date 2012-4-16 下午02:02:15
 */
public interface Executor {
	
	/**
	 * 保存操作执行方法，save参数中包含保存操作所需的表名、列名列表、主键、数据库连接和保存目标对象。<br>
	 * @param save 保存操作对象
	 * @return 主键值（程序制定或数据库自动生成）
	 */
	public Serializable save(SaveOperation save) throws SQLException;
	
	
	/**
	 * 更新操作执行方法，update参数中包含更新操作所需的表名、列名列表、操作限制条件、数据库连接和更新目标对象。<br>
	 * @param update 更新操作对象
	 * @return 更新的行数
	 */
	public long update(UpdateOperation update) throws SQLException;
	
	
	/**
	 * 删除操作执行方法，delete操作中包含删除操作需要的表名、操作限制条件、数据库连接。<br>
	 * @param delete 删除操作对象
	 * @return 删除的行数
	 */
	public long delete(DeleteOperation delete) throws SQLException;

	
	/**
	 * 查询操作执行方法，select操作中包含查询操作需要的表名、操作限制条件、数据库连接、查询列数组<br>
	 * @param select 查询操作对象
	 * @return 查询到的离线结果集
	 */
	public List<Map<String, Object>> select(SelectOperation select) throws SQLException;
	
	
	/**
	 * 根据指定SQL语句的查询操作执行方法，如果types为null，则操作使用setObject。<br>
	 * @param sqlSelect     sql查询
	 * @return 查询到的离线结果集
	 */
	public List<Map<String, Object>> selectBySQL(SQLSelectOperation sqlSelect) throws SQLException;
	
	
	/**
	 * 执行SQL函数查询，如avg、count、min、max等等<br>
	 * @return
	 * @throws java.sql.SQLException
	 */
	public Object function(FunctionOperation function) throws SQLException;
	
}