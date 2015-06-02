package com.slorm.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Spring's Connection Wrapper.
 * 
 * All the Connection used in this class are obtained from Springframework's DataSources
 * 
 * @author sulin
 * @version 1.0
 */
public class SpringConnectionWrapper extends ConnectionWrapper{

	/**
	 * Initialize the ConnectionWrapper.
	 * @param dataSource
	 * @throws java.sql.SQLException
	 */
	public SpringConnectionWrapper(DataSource dataSource) throws SQLException {
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
	 * If there is a thread Connection in Spring thread resources, return it directly. if none, return the thread-Connection.
	 * @return The Connection cached in current thread.
	 * @throws java.sql.SQLException
	 */
	protected Connection getThreadConnection() throws SQLException{
		Connection conn = this.dataSource.getConnection();
		return conn;
	}
	
	/**
	 * obtains a Connection used for JDBC operating.
	 * @throws java.sql.SQLException
	 */
	protected Connection getConnection() throws SQLException {
		ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
		if (conHolder != null && conHolder.isSynchronizedWithTransaction()) {
			conHolder.requested();
			return conHolder.getConnection(); // ?????? is it right?
		}
		Connection conn = this.threadConnection.get();
		if(conn == null)
			conn = this.getDeamonConnection();
		return conn;
	}

	/**
	 * obtains a read Connection used for JDBC read-only operating
	 */
	public synchronized Connection getReadOnlyConnection() throws SQLException {
		ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
		if (conHolder != null && conHolder.isSynchronizedWithTransaction()) {
			conHolder.requested();
			return conHolder.getConnection(); // ?????? is it right?
		}
		Connection conn = this.threadConnection.get();
		if(conn != null)
			return conn;
		return super.getReadOnlyConnection();
	}

	/*********************JDBC新加方法**********************/
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