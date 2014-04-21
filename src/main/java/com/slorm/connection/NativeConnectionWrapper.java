package com.slorm.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

/**
 * Native's Connection wrapper.
 * 
 * All the connections used in this wrapper are obtained directly from the DataSource.
 * When operating, it should try to obtain a Connection from threadConnection, if null, it directly obtains the deamonConnection and use it to finish operating.
 * When read-only operating, it should try to obtain a Connection from threadConnection, if null, it directly obtains the readConnection and use it to finish operating.
 * 
 * The threadConnection will be put when setAutoCommit() is invoked, and it will be removed when close() is invoked.
 * 
 * All the methods whick will change the status of this Connection should be overwrited, such as setReadOnly() etc...
 * 
 * @author sulin
 * @version 1.0
 */
public class NativeConnectionWrapper extends ConnectionWrapper{

	/**
	 * Initialize the ConnectionWrapper.
	 * @param dataSource
	 * @throws java.sql.SQLException
	 */
	public NativeConnectionWrapper(DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
		this.deamonConnection = dataSource.getConnection();
		this.deamonConnection.setAutoCommit(true);
		this.readConnection = dataSource.getConnection();
		this.readConnection.setAutoCommit(true);
		this.readConnection.setReadOnly(true);
		this.threadConnection = new ThreadLocal<Connection>();
	}
	
	/**
	 * ensure there is a Connection in the current thread, and return it.
	 * @return The Connection cached in current thread.
	 * @throws java.sql.SQLException
	 */
	protected Connection getThreadConnection() throws SQLException{
		Connection conn = this.threadConnection.get();
		if(conn == null){
			conn = this.dataSource.getConnection();
			this.threadConnection.set(conn);
		}
		return conn;
	}
	
	/**
	 * obtains a Connection used for JDBC operating.
	 * @throws java.sql.SQLException
	 */
	protected Connection getConnection() throws SQLException {
		Connection conn = this.threadConnection.get();
		if(conn == null)
			conn = this.getDeamonConnection();
		return conn;
	}
	
	/**
	 * obtains a read Connection for JDBC read-only operating.
	 */
	public synchronized Connection getReadOnlyConnection() throws SQLException {
		Connection conn = this.threadConnection.get();
		if(conn != null)
			return conn;
		return super.getReadOnlyConnection();
	}


	/*********************jdbc新加方法***********************/
	public void setSchema(String schema) throws SQLException {
	}

	public String getSchema() throws SQLException {
		return null;
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
	}

	public int getNetworkTimeout() throws SQLException {
		return 0;
	}

}