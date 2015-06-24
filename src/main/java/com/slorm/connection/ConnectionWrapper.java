package com.slorm.connection;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

/**
 * Connection management wrapper, this class designed from <a href='http://www.iteye.com/topic/989201'>threads share</a>.
 * 
 * Every DataSource has its own singleton ConnectionWrapper, 
 * all this ConnectionWrappers should be mapped in the ConnectionContainer as key-value pairs.
 * This class contains a ThreadLocal<Connection> used for storing thread-shared Connection, 
 * and a deamon Connection used for the operation without requirement of transaction,
 * and a read-only Connection used for the operation which only need a short query.
 * 
 * This class is just something like API, there are some different subclasses used for different types of Connection management styles.
 * 
 * @author sulin
 * @version 1.0
 */
public abstract class ConnectionWrapper implements Connection{
	
	/**
	 * All of this wrapper's Connections gain from this DataSource.
	 */
	protected DataSource dataSource;

	/**
	 * Thread Connection, it caches the Connection used by current thread.
	 */
	protected ThreadLocal<Connection> threadConnection;
	
	/**
	 * Deamon Connection, it is thread-safe Connection, when to use this Connection decided by subclass.
	 * this Connection should be pre-gained and stored when it is initialized.
	 */
	protected Connection deamonConnection;

	/**
	 * Read-only Connection, it is dedicated to read-only short query, precisely, it is dedicated to object mapping query.
	 */
	protected Connection readConnection;
	
	/**
	 * Gain a avaliable Connection used for JDBC operating.
	 * @return maybe deamonConnection or ThreadLocal's Connection etc...
	 * @throws java.sql.SQLException
	 */
	protected abstract Connection getConnection() throws SQLException;
	
	/**
	 * Gain the thread Connection, if not exist, create one and store it.
	 * @return the thread Connection, not null.
	 * @throws java.sql.SQLException
	 */
	protected abstract Connection getThreadConnection() throws SQLException;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Check and gain the unique read-only Connection.
	 * @return read-only Connection
	 */
	public synchronized Connection getReadOnlyConnection() throws SQLException{
        return getConnection();
//		if(this.readConnection==null){
//			this.readConnection = dataSource.getConnection();
//			this.readConnection.setAutoCommit(true);
//			this.readConnection.setReadOnly(true);
//		}else{
//			try{
//				if(this.readConnection.isClosed()){ // in normal, it won't happen.
//					this.readConnection = dataSource.getConnection();
//					this.readConnection.setAutoCommit(true);
//					this.readConnection.setReadOnly(true);
//				}
//			}catch(Exception e){
//				try{
//					this.readConnection.close();
//				}catch(Exception no){
//					// ignore......
//				}
//				this.readConnection = dataSource.getConnection();
//				this.readConnection.setAutoCommit(true);
//				this.readConnection.setReadOnly(true);
//				throw new RuntimeException("Unknow SQLException occurs, the deamonConnection was replaced.", e);
//			}
//		}
//		return this.readConnection;
	}
	
	/**
	 * Check and gain the unique deamon Connection.
	 * @return deamon Connection
	 */
	public synchronized Connection getDeamonConnection() throws SQLException{
        return getConnection();
//		if(this.deamonConnection==null){
//			this.deamonConnection = dataSource.getConnection();
//			this.deamonConnection.setAutoCommit(true);
//		}else{
//			try{
//				if(this.deamonConnection.isClosed()){ // in normal, it won't happen.
//					this.deamonConnection = dataSource.getConnection();
//					this.deamonConnection.setAutoCommit(true);
//				}
//			}catch(Exception e){
//				try{
//					this.deamonConnection.close();
//				}catch(Exception no){
//					// ignore......
//				}
//				this.deamonConnection = dataSource.getConnection();
//				this.deamonConnection.setAutoCommit(true);
//				throw new RuntimeException("Unknow SQLException occurs, the deamonConnection was replaced.", e);
//			}
//		}
//		return this.deamonConnection;
	}
	
	//---------------------------------- Connection's native functions
	
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return this.getConnection().isWrapperFor(arg0);
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return this.getConnection().unwrap(arg0);
	}
	
	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void abort(Executor arg0) throws SQLException {
		// jdk1.7 this.getThreadConnection().abort(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void clearWarnings() throws SQLException {
		this.getThreadConnection().clearWarnings();
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void close() throws SQLException{
		Connection conn = getConnection();
		if(conn != null){
			// 线程链接关闭
			conn.close();
			this.threadConnection.remove();
		} else {
			// 守护线程和只读线程关闭
			if(deamonConnection != null)
				deamonConnection.close();
			if(readConnection != null)
				readConnection.close();
		}
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void commit() throws SQLException{
		this.getThreadConnection().commit();
	}

	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		return this.getConnection().createArrayOf(arg0, arg1);
	}

	public Blob createBlob() throws SQLException {
		return this.getConnection().createBlob();
	}

	public Clob createClob() throws SQLException {
		return this.getConnection().createClob();
	}

	public NClob createNClob() throws SQLException {
		return this.getConnection().createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return this.getConnection().createSQLXML();
	}

	public Statement createStatement() throws SQLException {
		return this.getConnection().createStatement();
	}

	public Statement createStatement(int arg0, int arg1) throws SQLException {
		return this.getConnection().createStatement(arg0, arg1);
	}

	public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
		return this.getConnection().createStatement(arg0, arg1, arg2);
	}

	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		return this.getConnection().createStruct(arg0, arg1);
	}

	public boolean getAutoCommit() throws SQLException {
		return this.getConnection().getAutoCommit();
	}

	public String getCatalog() throws SQLException {
		return this.getConnection().getCatalog();
	}

	public Properties getClientInfo() throws SQLException {
		return this.getConnection().getClientInfo();
	}

	public String getClientInfo(String arg0) throws SQLException {
		return this.getConnection().getClientInfo(arg0);
	}

	public int getHoldability() throws SQLException {
		return this.getConnection().getHoldability();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return this.getConnection().getMetaData();
	}

	// jdk1.7 public int getNetworkTimeout() throws SQLException {
		// jdk1.7 return this.getConnection().getNetworkTimeout();
	// jdk1.7 }

	// jdk1.7 public String getSchema() throws SQLException {
	// jdk1.7 	return this.getConnection().getSchema();
	// jdk1.7 }

	public int getTransactionIsolation() throws SQLException {
		return this.getConnection().getTransactionIsolation();
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.getConnection().getTypeMap();
	}

	public SQLWarning getWarnings() throws SQLException {
		return this.getConnection().getWarnings();
	}

	public boolean isClosed() throws SQLException {
		return this.getConnection().isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		return this.getConnection().isReadOnly();
	}

	public boolean isValid(int arg0) throws SQLException {
		return this.getConnection().isValid(arg0);
	}

	public String nativeSQL(String arg0) throws SQLException {
		return this.getConnection().nativeSQL(arg0);
	}

	public CallableStatement prepareCall(String arg0) throws SQLException {
		return this.getConnection().prepareCall(arg0);
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2) throws SQLException {
		return this.getConnection().prepareCall(arg0, arg1, arg2);
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		return this.getConnection().prepareCall(arg0, arg1, arg2, arg3);
	}

	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		return this.getConnection().prepareStatement(arg0);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
		return this.getConnection().prepareStatement(arg0, arg1);
	}

	public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
		return this.getConnection().prepareStatement(arg0, arg1);
	}

	public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
		return this.getConnection().prepareStatement(arg0, arg1);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2) throws SQLException {
		return this.getConnection().prepareStatement(arg0, arg1, arg2);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		return this.getConnection().prepareStatement(arg0, arg1, arg2, arg3);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		this.getThreadConnection().releaseSavepoint(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void rollback() throws SQLException {
		this.getThreadConnection().rollback();
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void rollback(Savepoint arg0) throws SQLException {
		this.getThreadConnection().rollback(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void setAutoCommit(boolean arg0) throws SQLException{
		if(arg0){
			this.getThreadConnection().setAutoCommit(true);
		}else{
			if(this.threadConnection.get() != null)
				this.threadConnection.get().setAutoCommit(false);
		}
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void setCatalog(String arg0) throws SQLException {
		this.getThreadConnection().setCatalog(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		Connection conn = null;
		try {
			conn = this.getThreadConnection();
		} catch (SQLException e) {
			SQLClientInfoException scie = new SQLClientInfoException();
			scie.setNextException(e);
			throw scie;
		}
		conn.setClientInfo(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException {
		Connection conn = null;
		try {
			conn = this.getThreadConnection();
		} catch (SQLException e) {
			SQLClientInfoException scie = new SQLClientInfoException();
			scie.setNextException(e);
			throw scie;
		}
		conn.setClientInfo(arg0, arg1);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void setHoldability(int arg0) throws SQLException {
		this.getThreadConnection().setHoldability(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	// jdk1.7 public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
	// jdk1.7 	this.getThreadConnection().setNetworkTimeout(arg0, arg1);
	// jdk1.7 }

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void setReadOnly(boolean arg0) throws SQLException{
		this.getThreadConnection().setReadOnly(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public Savepoint setSavepoint() throws SQLException {
		return this.getThreadConnection().setSavepoint();
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public Savepoint setSavepoint(String arg0) throws SQLException {
		return this.getThreadConnection().setSavepoint(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	// jdk1.7 public void setSchema(String arg0) throws SQLException {
	// jdk1.7 	this.getThreadConnection().setSchema(arg0);
	// jdk1.7 }

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void setTransactionIsolation(int arg0) throws SQLException {
		this.getThreadConnection().setTransactionIsolation(arg0);
	}

	/**
	 * Being sure that there is a Connection cached in the current thread.
	 */
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		this.getThreadConnection().setTypeMap(arg0);
	}

}