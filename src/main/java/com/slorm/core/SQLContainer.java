package com.slorm.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 此类为<b>命名SQL</b>语句容器，这些SQL语句都来源于XML配置文件中。
 * 此SQL容器为每个类优先读取"类名.xml"中的SQL语句，其次到"*GlobalSQL.xml"中寻找命名SQL语句。
 * 因此写<b>命名SQL</b>语句时，优先将SQL语句写在"类名.xml"中，若有复杂SQL语句涉及多个表，可以写在一个全局"*SQL.xml"文件中。
 * 配置文件内xml文档格式为：
 * <sql name='sqlName'>
 * 		sqlContent
 * </sql>
 * 此文档节点无位置限制，只要文档中有sql节点，则此sql语句都会被保存起来。
 * 
 * ${sqlPieceName }会被自动替换。
 * 
 * @author sulin
 * @date 2012-4-25 下午08:08:41
 */
public final class SQLContainer {

	/**
	 * 命名SQL语句映射表：<SQL语句名称>:<SQL语句>
	 * 如果读取自非SQL.xml后缀的文件，则key命名为"文件名&SQL语句名"
	 * 如果读取自SQL.xml后缀的文件，则key命名为"SQL&SQL语句名"
	 */
	private static Map<String, String> sqlMaps = new HashMap<String, String>();
	
	static{
		String classesPath = SQLContainer.class.getClassLoader().getResource("").toString().substring(5);
		List<File> xmlFiles = scanXML(new File(classesPath));
		DocumentBuilder db = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("read named xml failed : " + e);
		}
		if(db != null){
			for(File xmlFile : xmlFiles){
				try {
					String prefix = null;
					if(xmlFile.getName().endsWith("GlobalSQL.xml")){
						prefix = "SQL&";
					}else{
						prefix = xmlFile.getName().toLowerCase().substring(0, xmlFile.getName().toLowerCase().indexOf(".xml")) + "&";
					}
					scanSQL(db.parse(xmlFile), prefix);
				} catch (Exception e) {
					// 读取失败，放弃，继续下一个文件
					continue;
				}
			}
		}
		// replace ${sqlPieceName }
		boolean temp = true;
		Pattern pattern = Pattern.compile("(\\$\\{[a-zA-Z_\\$][a-zA-Z0-9_\\$]*[ ]*\\})");
		while(temp){
			temp = false;
			for(String key : sqlMaps.keySet()){
				Matcher matcher = pattern.matcher(sqlMaps.get(key));
				while(matcher.find()){
					String ref = matcher.group(); // ${... }
					String name = ref.substring(2, ref.lastIndexOf("}")).trim(); // ...
					String fileName = key.substring(0, key.indexOf("&")); // SQL/fileName
					if(fileName.equals("SQL")){
						if(sqlMaps.get("SQL&"+name) != null){
							sqlMaps.put(key, sqlMaps.get(key).replace(ref, sqlMaps.get("SQL&"+name)));
							temp = true;
						}
					}else{
						if(sqlMaps.get(fileName+"&"+name) != null){
							sqlMaps.put(key, sqlMaps.get(key).replace(ref, sqlMaps.get(fileName+"&"+name)));
							temp = true;
						}else if(sqlMaps.get("SQL&"+name) != null){
							sqlMaps.put(key, sqlMaps.get(key).replace(ref, sqlMaps.get("SQL&"+name)));
							temp = true;
						}
					}
				}
			}
		}
		
	}
	
	// 从指定目录中搜索xml文件
	private static List<File> scanXML(File directory){
		List<File> result = new ArrayList<File>();
		File[] files = directory.listFiles();
		if(files != null){
			for(File file : files){
				if(file.isDirectory()){
					result.addAll(scanXML(file));
				}else{
					if(file.getName().endsWith(".xml"))
						result.add(file);
				}
			}
		}
		return result;
	}
	
	// 从指定xml文件中搜索命名SQL语句
	private static void scanSQL(Document document, String prefix){
		NodeList sqlNodeList = document.getElementsByTagName("sql");
		for(int i=0; i<sqlNodeList.getLength(); i++){
			Node sqlNode = sqlNodeList.item(i);
			if(sqlNode.getChildNodes().getLength()==1 && sqlNode.getFirstChild().getNodeName().equals("#text")){
				if(sqlNode.getAttributes().getNamedItem("name") == null)
					continue;
				String sqlName = sqlNode.getAttributes().getNamedItem("name").getNodeValue();
				String sqlContent = sqlNode.getFirstChild().getNodeValue().trim();
				sqlMaps.put(prefix+sqlName, sqlContent);
			}
		}
	}
	
	/**
	 * 根据指定sql名称获取SQL语句，如果获取不到则返回null<br/>
	 * 此方法首先以"类名&SQL语句名"搜索SQL语句，如果找不到则再以"SQL&SQL语句名"搜索SQL语句
	 * @param sqlName sql名称
	 * @param clazz 搜索此命名SQL语句的Java类
	 * @return 搜索到的SQL语句，如果搜索不到则返回null
	 */
	public static String getSQL(String sqlName, Class<?> clazz){
		String sql = null;
		if(clazz != null)
			sql = sqlMaps.get(clazz.getSimpleName().toLowerCase() + "&" + sqlName);
		if(sql == null)
			sql = sqlMaps.get("SQL&" + sqlName);
		return sql;
	}
	
	/**
	 * filter the specific SQL statement, and replace all the ${... } automatically. 
	 * @param sql The specific SQL statement.
	 * @param clazz priority class to use.
	 * @return
	 */
	public static String filterSQL(String sql, Class<?> clazz){
		boolean temp = true;
		Pattern pattern = Pattern.compile("(\\$\\{[a-zA-Z_\\$][a-zA-Z0-9_\\$]*[ ]*\\})");
		while(temp){
			temp = false;
			Matcher matcher = pattern.matcher(sql);
			while(matcher.find()){
				String ref = matcher.group(); // ${... }
				String name = ref.substring(2, ref.lastIndexOf("}")).trim(); // ...
				if(clazz != null){
					if(sqlMaps.get(clazz.getSimpleName()+"&"+name) != null){
						sql = sql.replace(ref, sqlMaps.get(clazz.getSimpleName()+"&"+name));
						temp = true;
					}else if(sqlMaps.get("SQL&"+name) != null){
						sql = sql.replace(ref, sqlMaps.get("SQL&"+name));
						temp = true;
					}
				}else if(sqlMaps.get("SQL&"+name) != null){
					sql = sql.replace(ref, sqlMaps.get("SQL&"+name));
					temp = true;
				}
			}
		}
		for(String s : sqlMaps.keySet()){
			System.out.println(s + " : " + sqlMaps.get(s));
		}
		
		return sql;
	}
	
}