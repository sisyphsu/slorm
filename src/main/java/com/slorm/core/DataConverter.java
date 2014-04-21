package com.slorm.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Types;
import java.util.Date;
import java.util.Random;

/**
 * 数据类型映射工具
 * 此工具类完成SQL数据类型与常规Java数据类型之间的映射匹配与转换。
 * 以下为SQL类型与Java类型之间的直接映射与兼容映射
 * 
 * sql类型			setXXX类型				接受java类型
 * ARRAY 			setArray				Array
 * BIGINT			setLong					Long
 * BINARY			setBytes				byte[]
 * BIT				setBoolean				Boolean
 * BLOB				setBlob(InputStream)	InputStream,File,Blob,byte[]
 * BOOLEAN			setBoolean				Boolean
 * CHAR				setString				String,Character
 * CLOB				setClob(InputStream)	Reader,File,String,Clob,InputStream
 * DATALINK			setURL					URL,String
 * DATE				setDate(java.sql.Date)	java.sql.Date,Long,java.util.Date
 * NUMERIC			setBigDecimal			BigDecimal,char[],byte[],double,float,int,long,String,BigInteger
 * DECIMAL			setBigDecimal			BigDecimal,char[],byte[],double,float,int,long,String,BigInteger
 * DISTINCT			不支持			
 * DOUBLE			setDouble				Double
 * FLOAT			setFloat				Float
 * INTEGER			setInt					Integer
 * JAVA_OBJECT		不支持
 * NCHAR			setNString				String
 * NVARCHAR			setNString				String
 * LONGNVARCHAR		setNString				String
 * LONGVARBINARY	setBinaryStream			InputStream,byte[]
 * LONGVARCHAR		setCharacterStream		InputStream,Reader,String
 * NCLOB			setNClob(Reader)		Reader,File,String,NClob,InputStream
 * NULL				不支持
 * OTHER			不支持
 * REAL				setFloat				Float
 * REF				setRef					java.sql.Ref
 * ROWID			setRowId				java.sql.RowId
 * SMALLINT			setShort				Short
 * SQLXML			setSQLXML				java.sql.SQLXML
 * STRUCT			不支持
 * TIME				setTime					Time,Long,Date
 * TIMESTAMP		setTimestamp			Timestamp,Long,Date
 * TINYINT			setByte					Byte
 * VARBINARY		setBytes				byte[]
 * VARCHAR			setString				String
 * 
 * @author sulin
 * @date 2012-4-19 下午04:16:18
 */
public final class DataConverter {

	/**
	 * 此方法根据给定的sql数据类型和java数据类型判断此两种数据类型是否互相匹配<br/>
	 * 如果不匹配则返回不匹配描述字符串，否则返回null。
	 * <i>此方法暂时不用</i>
	 * @param sqlType sql数据类型，取自java.sql.Types
	 * @param javaType java数据类型
	 * @return null或不匹配原因描述字符串
	 */
	public static String verify(int sqlType, Class<?> javaType){
		switch(sqlType){
		case Types.ARRAY:
			if(!Array.class.isAssignableFrom(javaType))
				return "SQL type ARRAY can only be mapped to java.sql.Array!";
			else
				return null;
		case Types.BIGINT:
			if(!Long.class.isAssignableFrom(javaType))
				return "SQL type BIGINT can only be mapped to java.lang.Long!";
			else
				return null;
		case Types.BINARY:
			if(!byte[].class.isAssignableFrom(javaType))
				return "SQL type BINARY can only be mapped to byte[]!";
			else
				return null;
		case Types.BIT:
			if(!Boolean.class.isAssignableFrom(javaType))
				return "SQL type BIT can only be mapped to java.lang.Boolean!";
			else
				return null;
		case Types.BLOB:
			if(!InputStream.class.isAssignableFrom(javaType)
					&& !File.class.isAssignableFrom(javaType)
					&& !Blob.class.isAssignableFrom(javaType)
					&& !byte[].class.isAssignableFrom(javaType))
				return "SQL type BLOB can only be mapped to java.io.InputStream, java.io.File, java.sql.Blob, byte[]!";
			else
				return null;
		case Types.BOOLEAN:
			if(!Boolean.class.isAssignableFrom(javaType))
				return "SQL type BOOLEAN can only be mapped to java.lang.Boolean!";
			else
				return null;
		case Types.CHAR:
			if(!String.class.isAssignableFrom(javaType)
					&& !javaType.isAssignableFrom(Character.class))
				return "SQL type CHAR can only be mapped to java.lang.String, java.lang.Character!";
			else
				return null;
		case Types.CLOB:
			if(!File.class.isAssignableFrom(javaType)
					&& !String.class.isAssignableFrom(javaType)
					&& !Clob.class.isAssignableFrom(javaType)
					&& !InputStream.class.isAssignableFrom(javaType)
					&& !Reader.class.isAssignableFrom(javaType))
				return "SQL type CLOB can only be mapped to java.io.Reader, java.io.File, java.lang.String, java.sql.Clob, java.io.InputStream!";
			else
				return null;
		case Types.DATALINK:
			if(!URL.class.isAssignableFrom(javaType)
					&& !String.class.isAssignableFrom(javaType))
				return "SQL type DATALINK can only be mapped to java.net.URL, java.lang.String!";
			else
				return null;
		case Types.DATE:
			if(!Long.class.isAssignableFrom(javaType)
					&& !Date.class.isAssignableFrom(javaType))
				return "SQL type DATE can only be mapped to java.lang.Long, java.util.Date!";
			else
				return null;
		case Types.DECIMAL:
			if(!BigDecimal.class.isAssignableFrom(javaType)
					&& !char[].class.isAssignableFrom(javaType)
					&& !byte[].class.isAssignableFrom(javaType)
					&& !Double.class.isAssignableFrom(javaType)
					&& !Float.class.isAssignableFrom(javaType)
					&& !Integer.class.isAssignableFrom(javaType)
					&& !Long.class.isAssignableFrom(javaType)
					&& !String.class.isAssignableFrom(javaType)
					&& !BigInteger.class.isAssignableFrom(javaType))
				return "SQL type DECIMAL can only be mapped to java.math.BigDecimal, char[], byte[], java.lang.Double, java.lang.Float, java.lang.Integer, java.lang.Long, java.lang.String, java.math.BigInteger!";
			else
				return null;
		case Types.DISTINCT:
			return "Unsupported SQL type DISTINCT";
		case Types.DOUBLE:
			if(!Double.class.isAssignableFrom(javaType))
				return "SQL type DOUBLE can only be mapped to java.lang.Double!";
			else
				return null;
		case Types.FLOAT:
			if(!Float.class.isAssignableFrom(javaType))
				return "SQL type FLOAT can only be mapped to java.lang.Float!";
			else
				return null;
		case Types.INTEGER:
			if(!Integer.class.isAssignableFrom(javaType))
				return "SQL type INTEGER can only be mapped to java.lang.Integer!";
			else
				return null;
		case Types.JAVA_OBJECT:
			return "Unsupported SQL type JAVA_OBJECT";
		case Types.LONGNVARCHAR:
			if(!String.class.isAssignableFrom(javaType))
				return "SQL type LONGNVARCHAR can only be mapped to java.lang.String!";
			else
				return null;
		case Types.LONGVARBINARY:
			if(!InputStream.class.isAssignableFrom(javaType)
					&& !byte[].class.isAssignableFrom(javaType))
				return "SQL type LONGVARBINARY can only be mapped to java.io.InputStream, byte[]!";
			else
				return null;
		case Types.LONGVARCHAR:
			if(!InputStream.class.isAssignableFrom(javaType)
					&& !String.class.isAssignableFrom(javaType)
					&& !Reader.class.isAssignableFrom(javaType))
				return "SQL type LONGVARCHAR can only be mapped to java.io.InputStream, java.lang.String, java.io.Reader!";
			else
				return null;
		case Types.NCHAR:
			if(!String.class.isAssignableFrom(javaType))
				return "SQL type NCHAR can only be mapped to java.lang.String!";
			else
				return null;
		case Types.NCLOB:
			if(!Reader.class.isAssignableFrom(javaType)
					&& !File.class.isAssignableFrom(javaType)
					&& !String.class.isAssignableFrom(javaType)
					&& !NClob.class.isAssignableFrom(javaType)
					&& !InputStream.class.isAssignableFrom(javaType))
				return "SQL type NCLOB can only be mapped to java.io.Reader, java.io.File, java.lang.String, java.sql.NClob, java.io.InputStream!";
			else
				return null;
		case Types.NULL:
			return "Unsupported SQL type NULL!";
		case Types.NUMERIC:
			if(!BigDecimal.class.isAssignableFrom(javaType)
					&& !char[].class.isAssignableFrom(javaType)
					&& !byte[].class.isAssignableFrom(javaType)
					&& !Double.class.isAssignableFrom(javaType)
					&& !Float.class.isAssignableFrom(javaType)
					&& !Integer.class.isAssignableFrom(javaType)
					&& !Long.class.isAssignableFrom(javaType)
					&& !String.class.isAssignableFrom(javaType)
					&& !BigInteger.class.isAssignableFrom(javaType))
				return "SQL type NUMERIC can only be mapped to java.math.BigDecimal, char[], byte[], java.lang.Double, java.lang.Float, java.lang.Integer, java.lang.Long, java.lang.String, java.math.BigInteger!";
			else
				return null;
		case Types.NVARCHAR:
			if(!String.class.isAssignableFrom(javaType))
				return "SQL type NVARCHAR can only be mapped to java.lang.String!";
			else
				return null;
		case Types.OTHER:
			return null;
		case Types.REAL:
			if(!Float.class.isAssignableFrom(javaType))
				return "SQL type REAL can only be mapped to java.lang.Float!";
			else
				return null;
		case Types.REF:
			if(!Ref.class.isAssignableFrom(javaType))
				return "SQL type REF can only be mapped to java.sql.Ref!";
			else
				return null;
		case Types.ROWID:
			if(!RowId.class.isAssignableFrom(javaType))
				return "SQL type ROWID can only be mapped to java.sql.RowId!";
			else
				return null;
		case Types.SMALLINT:
			if(!Short.class.isAssignableFrom(javaType))
				return "SQL type SMALLINT can only be mapped to java.lang.Short!";
			else
				return null;
		case Types.SQLXML:
			if(!SQLXML.class.isAssignableFrom(javaType))
				return "SQL type SQLXML can only be mapped to java.sql.SQLXML!";
			else
				return null;
		case Types.STRUCT:
			return "Unsupported SQL type STRUCT!";
		case Types.TIME:
			if(!Long.class.isAssignableFrom(javaType)
					&& !Date.class.isAssignableFrom(javaType))
				return "SQL type TIME can only be mapped to java.lang.Long, java.util.Date!";
			else
				return null;
		case Types.TIMESTAMP:
			if(!Long.class.isAssignableFrom(javaType)
					&& !Date.class.isAssignableFrom(javaType))
				return "SQL type TIMESTAMP can only be mapped to java.lang.Long, java.util.Date!";
			else
				return null;
		case Types.TINYINT:
			if(!Byte.class.isAssignableFrom(javaType))
				return "SQL type TINYINT can only be mapped to java.lang.Byte!";
			else
				return null;
		case Types.VARBINARY:
			if(!byte[].class.isAssignableFrom(javaType))
				return "SQL type VARBINARY can only be mapped to byte[]!";
			else
				return null;
		case Types.VARCHAR:
			if(!String.class.isAssignableFrom(javaType))
				return "SQL type VARCHAR can only be mapped to java.lang.String!";
			else
				return null;
		default:
			return "ERROR SQL TYPE number : " + sqlType;
		}
	}
	
	/**
	 * 根据给定的java数据类型获取到默认映射的sql数据类型
	 * 如果没有可以映射的sql数据类型，则返回Types.OTHER
	 * 此方法貌似没用，因为这个默认SQL类型其实无关紧要的。直接对无配置参数进行setObject更方便！
	 * @param javaType java数据类型
	 * @return sql数据类型或Types.OTHER
	 */
	public static int getDefaultSqlType(Class<?> javaType){
		return Types.OTHER;
	}

	/**
	 * 根据给定的java对象获取最合适的可用于sql操作的java对象，具体映射关系见类注释
	 * 如果无法转换或者给定的o类型无法识别则直接返回o！
	 * @param o java数据对象
	 * @param sqlType sql数据类型
	 * @return 用于sql操作的java对象
	 */
	public static Object getSqlData(Object o, int sqlType){
		switch(sqlType){
		case Types.BLOB:
			if(File.class.isAssignableFrom(o.getClass())){
				try {
					return new FileInputStream((File)o);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			}else if(Blob.class.isAssignableFrom(o.getClass())){
				try {
					return ((Blob)o).getBinaryStream();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}else if(byte[].class.isAssignableFrom(o.getClass())){
				return new ByteArrayInputStream((byte[])o);
			}
		case Types.CHAR:
			if(Character.class.isAssignableFrom(o.getClass())){
				return String.valueOf((Character)o);
			}
		case Types.CLOB:
			if(File.class.isAssignableFrom(o.getClass())){
				try {
					return new FileInputStream((File)o);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			}else if(String.class.isAssignableFrom(o.getClass())){
				return new ByteArrayInputStream(((String)o).getBytes());
			}else if(Clob.class.isAssignableFrom(o.getClass())){
				try {
					return ((Clob)o).getAsciiStream();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}else if(InputStream.class.isAssignableFrom(o.getClass())){
				return (InputStream)o;
			}
		case Types.DATALINK:
			if(String.class.isAssignableFrom(o.getClass())){
				try {
					return new URL((String)o);
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}
		case Types.DATE:
			if(Long.class.isAssignableFrom(o.getClass())){
				return new java.sql.Date((Long)o);
			}else if(java.sql.Date.class.isAssignableFrom(o.getClass())){
				return o;
			}else if(Date.class.isAssignableFrom(o.getClass())){
				return new java.sql.Date(((Date)o).getTime());
			}
		case Types.NUMERIC:
		case Types.DECIMAL:
			if(char[].class.isAssignableFrom(o.getClass())){
				return new BigDecimal((char[])o);
			}else if(byte[].class.isAssignableFrom(o.getClass())){
				return new BigDecimal(new BigInteger((byte[])o));
			}else if(Double.class.isAssignableFrom(o.getClass())){
				return new BigDecimal((Double)o);
			}else if(Float.class.isAssignableFrom(o.getClass())){
				return new BigDecimal((Float)o);
			}else if(Integer.class.isAssignableFrom(o.getClass())){
				return new BigDecimal((Integer)o);
			}else if(Long.class.isAssignableFrom(o.getClass())){
				return new BigDecimal((Long)o);
			}else if(String.class.isAssignableFrom(o.getClass())){
				return new BigDecimal((String)o);
			}else if(BigInteger.class.isAssignableFrom(o.getClass())){
				return new BigDecimal((BigInteger)o);
			}
		case Types.LONGVARBINARY:
			if(byte[].class.isAssignableFrom(o.getClass()))
				return new ByteArrayInputStream((byte[])o);;
		case Types.LONGVARCHAR:
			if(InputStream.class.isAssignableFrom(o.getClass())){
				return new InputStreamReader((InputStream)o);
			}else if(String.class.isAssignableFrom(o.getClass())){
				return new StringReader((String)o);
			}
		case Types.NCLOB:
			if(File.class.isAssignableFrom(o.getClass())){
				try {
					return new FileReader((File)o);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			}else if(String.class.isAssignableFrom(o.getClass())){
				return new StringReader((String)o);
			}else if(NClob.class.isAssignableFrom(o.getClass())){
				try {
					return ((NClob)o).getCharacterStream();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}else if(InputStream.class.isAssignableFrom(o.getClass())){
				return new InputStreamReader((InputStream)o);
			}
		case Types.TIME:
			if(Long.class.isAssignableFrom(o.getClass())){
				return new Time((Long)o);
			}else if(Time.class.isAssignableFrom(o.getClass())){
				return o;
			}else if(Date.class.isAssignableFrom(o.getClass())){
				return new Time(((Date)o).getTime());
			}
		case Types.TIMESTAMP:
			if(Long.class.isAssignableFrom(o.getClass())){
				return new java.sql.Timestamp((Long)o);
			}else if(java.sql.Timestamp.class.isAssignableFrom(o.getClass())){
				return o;
			}else if(Date.class.isAssignableFrom(o.getClass())){
				return new java.sql.Timestamp(((Date)o).getTime());
			}
		}
		return o;
	}
	
	/**
	 * 根据给定的sql对象获取并转换为指定java数据类型，如果不需要转换或者不能转换则直接返回原始数据
	 * 此方法只实现了部分转换，可能有新的添加。
	 * @param o sql数据对象
	 * @param javaType java数据类型
	 * @return 转换得到的java数据对象
	 */
	public static Object getJavaData(Object o, Class<?> javaType){
		if(o == null)
			return null;
		Random random = new Random(System.currentTimeMillis());
		if(InputStream.class.isAssignableFrom(o.getClass())){
			//File,byte[],Reader,String
			if(File.class.isAssignableFrom(javaType)){
				try {
					File file = File.createTempFile("lxl", null);
					FileOutputStream fos = new FileOutputStream(file);
					InputStream is = (InputStream)o;
					byte[] buf = new byte[1024];
					while(is.read(buf) > 0)
						fos.write(buf);
					is.close();
					fos.flush();
					fos.close();
					return file;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}else if(byte[].class.isAssignableFrom(javaType)){
				try {
					InputStream is = (InputStream)o;
					byte[] buf = new byte[is.available()];
					if(is.read(buf) > 0)
						throw new RuntimeException("The InputStream is blocked... but it should be a non-block stream!");
					is.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}else if(Reader.class.isAssignableFrom(javaType)){
				return new InputStreamReader((InputStream)o);
			}else if(String.class.isAssignableFrom(javaType)){
				try {
					InputStreamReader reader = new InputStreamReader((InputStream)o);
					StringBuilder str = new StringBuilder();
					char[] cs = new char[1024];
					while(reader.read(cs) > -1)
						str.append(cs);
					reader.close();
					return str.toString();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}else if(BigDecimal.class.isAssignableFrom(o.getClass())){
			// BigDecimal,char[],byte[],double,float,int,long,String,BigInteger
			if(char[].class.isAssignableFrom(javaType)){
				return ((BigDecimal)o).toString().toCharArray();
			}else if(byte[].class.isAssignableFrom(javaType)){
				return ((BigDecimal)o).toBigInteger().toByteArray();
			}else if(Double.class.isAssignableFrom(javaType)){
				return ((BigDecimal)o).doubleValue();
			}else if(Float.class.isAssignableFrom(javaType)){
				return ((BigDecimal)o).floatValue();
			}else if(Integer.class.isAssignableFrom(javaType)){
				return ((BigDecimal)o).intValue();
			}else if(Long.class.isAssignableFrom(javaType)){
				return ((BigDecimal)o).longValue();
			}else if(String.class.isAssignableFrom(javaType)){
				return ((BigDecimal)o).toString();
			}else if(BigInteger.class.isAssignableFrom(javaType)){
				return ((BigDecimal)o).toBigInteger();
			}
		}else if(Blob.class.isAssignableFrom(o.getClass())){
			// InputStream,File,byte[]
			if(InputStream.class.isAssignableFrom(javaType)){
				try {
					return ((Blob)o).getBinaryStream();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}else if(File.class.isAssignableFrom(javaType)){
				try {
					File file = File.createTempFile("lxl", null);
					FileOutputStream fos = new FileOutputStream(file);
					InputStream is = ((Blob)o).getBinaryStream();
					byte[] buf = new byte[1024];
					while(is.read(buf) > 0)
						fos.write(buf);
					is.close();
					fos.flush();
					fos.close();
					return file;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else if(byte[].class.isAssignableFrom(javaType)){
				try {
					InputStream is = ((Blob)o).getBinaryStream();
					byte[] buf = new byte[is.available()];
					if(is.read(buf) > 0)
						throw new RuntimeException("The InputStream is blocked... it should be non-block stream!");
					is.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}else if(Boolean.class.isAssignableFrom(o.getClass())){
			
		}else if(Byte.class.isAssignableFrom(o.getClass())){
			
		}else if(byte[].class.isAssignableFrom(o.getClass())){
			// InputStream,File
			if(InputStream.class.isAssignableFrom(javaType)){
				return new ByteArrayInputStream((byte[])o);
			}else if(File.class.isAssignableFrom(javaType)){
				try {
					File file = File.createTempFile("lxl", null);
					FileOutputStream fos = new FileOutputStream(file);
					InputStream is = new ByteArrayInputStream((byte[])o);
					byte[] buf = new byte[1024];
					while(is.read(buf) > 0)
						fos.write(buf);
					is.close();
					fos.flush();
					fos.close();
					return file;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}else if(Reader.class.isAssignableFrom(o.getClass())){
			//File,String
			if(File.class.isAssignableFrom(javaType)){
				try {
					File file = File.createTempFile("lxl"+System.currentTimeMillis()+random.nextInt(), null);
					FileWriter writer = new FileWriter(file);
					Reader reader = (Reader)o;
					char[] buf = new char[1024];
					while(reader.read(buf) > -1)
						writer.write(buf);
					reader.close();
					writer.flush();
					writer.close();
					return file;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}else if(String.class.isAssignableFrom(javaType)){
				try {
					StringBuilder sb = new StringBuilder();
					Reader reader = (Reader)o;
					char[] buf = new char[1024];
					while(reader.read(buf) > -1)
						sb.append(buf);
					reader.close();
					return sb.toString();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}else if(Clob.class.isAssignableFrom(o.getClass())){
			// Reader,File,String,InputStream
			Clob c = (Clob)o;
			if(Reader.class.isAssignableFrom(javaType)){
				try {
					return c.getCharacterStream();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}else if(File.class.isAssignableFrom(javaType)){
				try {
					File file = File.createTempFile("lxl"+System.currentTimeMillis()+random.nextInt(), null);
					FileWriter writer = new FileWriter(file);
					Reader reader = c.getCharacterStream();
					char[] buf = new char[1024];
					while(reader.read(buf) > 0)
						writer.write(buf);
					reader.close();
					writer.flush();
					writer.close();
					return file;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else if(String.class.isAssignableFrom(javaType)){
				try {
					StringBuilder sb = new StringBuilder();
					Reader reader = c.getCharacterStream();
					char[] buf = new char[1024];
					while(reader.read(buf) > 0)
						sb.append(buf);
					reader.close();
					return sb.toString();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else if(InputStream.class.isAssignableFrom(javaType)){
				try {
					return c.getAsciiStream();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}else if(Date.class.isAssignableFrom(o.getClass())){
			// java.sql.Date, java.sql.Time, java.sql.Timestamp, java.lang.Long
			Date d = (Date)o;
			if(Time.class.isAssignableFrom(javaType)){
				return new Time(d.getTime());
			}else if(java.sql.Timestamp.class.isAssignableFrom(javaType)){
				return new java.sql.Timestamp(d.getTime());
			}else if(java.sql.Date.class.isAssignableFrom(javaType)){
				return new java.sql.Date(d.getTime());
			}
		}else if(Double.class.isAssignableFrom(o.getClass())){
			
		}else if(Integer.class.isAssignableFrom(o.getClass())){
			
		}else if(Float.class.isAssignableFrom(o.getClass())){
			
		}else if(Long.class.isAssignableFrom(o.getClass())){
			
		}else if(NClob.class.isAssignableFrom(o.getClass())){
			// Reader,File,String,InputStream
			NClob c = (NClob)o;
			if(Reader.class.isAssignableFrom(javaType)){
				try {
					return c.getCharacterStream();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}else if(File.class.isAssignableFrom(javaType)){
				try {
					File file = File.createTempFile("lxl", null);
					FileWriter writer = new FileWriter(file);
					Reader reader = c.getCharacterStream();
					char[] buf = new char[1024];
					while(reader.read(buf) > 0)
						writer.write(buf);
					reader.close();
					writer.flush();
					writer.close();
					return file;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else if(String.class.isAssignableFrom(javaType)){
				try {
					StringBuilder sb = new StringBuilder();
					Reader reader = c.getCharacterStream();
					char[] buf = new char[1024];
					while(reader.read(buf) > 0)
						sb.append(buf);
					reader.close();
					return sb.toString();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else if(InputStream.class.isAssignableFrom(javaType)){
				try {
					return c.getAsciiStream();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}else if(String.class.isAssignableFrom(o.getClass())){
			// 不会是MySQL的JDBC驱动默认通过getObject获取到的数据都是String吧
			if(Integer.class.isAssignableFrom(javaType)){
				return Integer.parseInt((String)o);
			}else if(Short.class.isAssignableFrom(javaType)){
				return Short.parseShort((String)o);
			}else if(Long.class.isAssignableFrom(javaType)){
				return Long.parseLong((String)o);
			}else if(Boolean.class.isAssignableFrom(javaType)){
				return Boolean.parseBoolean((String)o);
			}else if(Double.class.isAssignableFrom(javaType)){
				return Double.parseDouble((String)o);
			}else if(Float.class.isAssignableFrom(javaType)){
				return Float.parseFloat((String)o);
			}else if(Byte.class.isAssignableFrom(javaType)){
				return Byte.parseByte((String)o);
			}else if(byte[].class.isAssignableFrom(javaType)){
				return ((String)o).getBytes();
			}else if(Character.class.isAssignableFrom(javaType)){
				return ((String)o).charAt(0);
			}else if(char[].class.isAssignableFrom(javaType)){
				return ((String)o).toCharArray();
			}else if(Date.class.isAssignableFrom(javaType)){
				Date result = null;
				long time = Long.parseLong((String)o);
				try{
					result = new java.sql.Date(time);
				}catch(IllegalArgumentException e1){
					try{
						result = new Time(time);
					}catch(IllegalArgumentException e2){
						try{
							result = new java.sql.Timestamp(time);
						}catch(IllegalArgumentException e3){
							throw new RuntimeException("Cann't get Date from " + o);
						}
					}
				}
				return result;
			}
		}else if(Ref.class.isAssignableFrom(o.getClass())){
			
		}else if(RowId.class.isAssignableFrom(o.getClass())){
			
		}else if(Short.class.isAssignableFrom(o.getClass())){
			
		}else if(SQLXML.class.isAssignableFrom(o.getClass())){
			
		}else if(URL.class.isAssignableFrom(o.getClass())){
			URL url = (URL)o;
			if(String.class.isAssignableFrom(javaType))
				return url.toString();
		}
		// Basic data type
		if(javaType.equals(Long.class)){
			if(o.getClass().equals(Integer.class)){
				return (long)(Integer)o;
			}else if(o.getClass().equals(Short.class)){
				return (long)(Short)o;
			}else if(o.getClass().equals(Byte.class)){
				return (long)((Byte)o);
			}else if(o.getClass().equals(String.class)){
				return Long.valueOf((String)o);
			}
		}else if(javaType.equals(Integer.class)){
			if(o.getClass().equals(Short.class)){
				return (int)(Short)o;
			}else if(o.getClass().equals(Byte.class)){
				return (int)(Byte)o;
			}else if(o.getClass().equals(String.class)){
				return Integer.valueOf((String)o);
			}
		}else if(javaType.equals(Short.class)){
			if(o.getClass().equals(Byte.class)){
				return (short)(Byte)o;
			}else if(o.getClass().equals(String.class)){
				return Short.valueOf((String)o);
			}
		}else if(javaType.equals(Byte.class)){
			if(o.getClass().equals(String.class)){
				return Byte.valueOf((String)o);
			}
		}else if(javaType.equals(Double.class)){
			if(o.getClass().equals(Float.class)){
				return (double)(Float)o;
			}else if(o.getClass().equals(String.class)){
				return Double.valueOf((String)o);
			}
		}
		
		return o;
	}
	
}