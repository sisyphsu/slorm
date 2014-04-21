package com.slorm.core;

import com.slorm.BaseDao;
import com.slorm.annocation.Column;
import com.slorm.annocation.Quote;
import com.slorm.annocation.Table;
import com.slorm.annocation.UnColumn;
import com.slorm.proxy.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Model&Table映射容器,此类为最终类且线程安全!<br>
 * 此类为静态的容器工具类,它会将任何一个BaseDao子类解析并根据默认映射策略和注解元素生成JavaBean-->DBTable之间的映射.<br>
 * <pre>
 * 映射策略:
 * 		类名-->表名, 如果需要特殊配置,可使用@Table(tableName='...')进行显式指定.
 * 		字段名-->列名, 如果需要特殊配置,可使用@Column(columnName='...')进行显式指定. 需要注意的是:所有需要映射的字段都必须有getter,setter方法,否则此字段会被忽略!
 * 		默认数据源-->数据源,如果需要特殊配置,可使用@Table(dataSource='...')进行显式指定. 默认数据源即是指当前系统中唯一存在的数据源,如果系统中没有设定数据源或者有多个数据源则无法配置默认数据源!
 * 		数据格式. 默认情况下,程序内部直接使用setObject和getObject进行JDBC操作,你可以使用@Column(columnType=0)指定,此处的类型值必须是Types.XXXX!
 * 		第一个字段-->主键, 如果需要特殊配置,可使用@Column(isID=true)进行显式指定.
 * 		不需要映射字段. 如果不需要对某些列进行自动映射, 可以使用@UnColumn注解进行显式指定.
 * </pre>
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class MapContainer {

	private static final ConcurrentMap<String, ClassToTable> maps = new ConcurrentHashMap<String, ClassToTable>();

	/**
	 * 获取给定类所对应的ClassToTable实例
	 * @param c 指定类
	 * @return
	 */
	public static ClassToTable getCCT(Class<?> c) {
		Assert.isNotNull(c,"the Class mustn't be null !"); // impossible
		String className = null;
		if(c.getName().endsWith(ReflectUtil.PROXYSUFFIX)){
			className = c.getName().replace(ReflectUtil.PROXYSUFFIX, "");
		}else{
			className = c.getName();
		}
		ClassToTable ctt = maps.get(className);
		if (ctt == null) {
			parseClass(c);
			ctt = maps.get(className);
		}

		return ctt;
	}

	// 解析类并保存起来
	private static void parseClass(Class<?> c) {
		if(c.equals(BaseDao.class) || !BaseDao.class.isAssignableFrom(c))
			throw new RuntimeException("the class["+c.getName()+"] must be a subClass of "+BaseDao.class.getSimpleName());
		
		ClassToTable ctt = new ClassToTable();
		List<Method> ms = Util.toList(c.getDeclaredMethods());
		List<Field> fs = Util.toList(c.getDeclaredFields());
		ctt = new ClassToTable();
		ctt.setClazz(c);

		Table mi = c.getAnnotation(Table.class);
		if (mi != null) {
			ctt.setDataSource(mi.dataSource());
			if (mi.tableName()!=null) 
				ctt.setTableName(mi.tableName());
		}else{
			ctt.setDataSource("");
		}
		if(ctt.getTableName() == null)
			ctt.setTableName(c.getSimpleName().toLowerCase());

		for (Field f : fs) {
			if(f.getAnnotation(UnColumn.class) != null)
				continue;
			
			String propertyName = f.getName();
			String p = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			String getter = "get" + p;
			String setter = "set" + p;
			for (Method m : ms) {
				if (m.getName().equals(getter))
					getter = null;
				if (m.getName().equals(setter))
					setter = null;
			}
			if (getter != null || setter != null)
				continue;
			
			Quote q = f.getAnnotation(Quote.class);
			if(q == null){
				Property prop = new Property();
				prop.setName(propertyName);
				prop.setType(f.getType());
				Column column = f.getAnnotation(Column.class);
				if (column != null) {
					prop.setColumnType(column.columnType());
					if(column.columnName()!=null && !column.columnName().equals(""))
						prop.setColumn(column.columnName().trim());
					else
						prop.setColumn(propertyName);
					if(column.isID())
						ctt.setId(prop);
				}else{
					prop.setColumnType(Types.OTHER);
					prop.setColumn(propertyName);
					if(ctt.getId() == null)
						ctt.setId(prop);
				}
				ctt.getProps().add(prop);
			}else{
				Reference ref = new Reference();
				ref.setName(propertyName);
				ref.setType(c);
				ref.setSql(q.value());
				ref.setTargetType(f.getType());
				ctt.getQuotes().add(ref);
			}
		}

		// 必须先保存，否则会死循环。。。
		maps.putIfAbsent(c.getName(), ctt);
		
		if(ctt.getQuotes()!=null && !ctt.getQuotes().isEmpty())
			for(Reference ref : ctt.getQuotes())
				ref.initialize();
	}

}