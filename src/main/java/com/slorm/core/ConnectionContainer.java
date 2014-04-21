package com.slorm.core;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.slorm.connection.ConnectionWrapper;
import com.slorm.connection.NativeConnectionWrapper;
import com.slorm.connection.SpringConnectionWrapper;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Connection manager container. it accepts the DataSources initialized from the properties configuration file 
 * or the DataSources seek from Spring container.
 * 
 * This class will first seek the named properties file that contain the configuration message, if that file exists,
 * this class will initialize all the DataSources and generate all the ConnectionWrapper. if not, this class will try
 * to seek the DataSources from Springframework, if all fails, this class will throw a RuntimeException.
 *
 * After initializing, this class will generate the ConnectionWrappers from the DataSources. the ConnectionWrapper is 
 * used for JDBC operating, details of the specific Connection management strategy is in the class ConnectionWrapper.
 * 
 * @author sulin
 * @date 2012-4-25
 */
public final class ConnectionContainer {

	/**
	 * Configuration properties file name
	 */
	private static final String propsFileName = "magicotm.properties";
	
	/**
	 * the mapping table of ConnectionWrapper. this property will not be modified at runtime.
	 */
	private static Map<String, ConnectionWrapper> connections;
	
	static{
		String classesPath = SQLContainer.class.getClassLoader().getResource("").toString().substring(5);
		connections = new HashMap<String, ConnectionWrapper>();
		try {
			Properties props = new Properties();
			props.load(new FileReader(new File(classesPath, propsFileName)));
			Map<String, ComboPooledDataSource> dataSources = new HashMap<String, ComboPooledDataSource>();
			for (Object _key : props.keySet()) {
				String key = _key.toString();
				if (!key.matches("dataSource\\.\\w+\\.\\w+"))
					continue;
				key = key.replace("dataSource\\.", ""); // dataSource.property
				ComboPooledDataSource ds = dataSources.get(key.substring(0, key.indexOf(".")));
				if(ds == null)
					ds = new ComboPooledDataSource();
				try {
					setProp(ds, key.substring(key.indexOf(".")+1), props.get(_key).toString());
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
				dataSources.put(key.substring(0, key.indexOf(".")), ds);
			}
			for(String name : dataSources.keySet()){
				try {
					connections.put(name, new NativeConnectionWrapper(dataSources.get(name)));
				} catch (SQLException e) {
					throw new RuntimeException("exception occurs when initialize the DataSources", e);
				}
			}
		} catch (FileNotFoundException e) {
			try{
				Map<String, DataSource> dataSources = org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext().getBeansOfType(DataSource.class);
				if(dataSources==null || dataSources.keySet().isEmpty())
					throw new RuntimeException("Get none DataSource from springframework!");
				for(String name : dataSources.keySet()){
					try {
						connections.put(name, new SpringConnectionWrapper(dataSources.get(name)));
					} catch (SQLException sqle) {
						throw new RuntimeException("exception occurs when initialize the DataSources", sqle);
					}
				}
			}catch(Exception ee){
				
				throw new RuntimeException("Cann't get DataSource from springframework !", ee);
			}
		} catch (IOException e) {
			throw new RuntimeException("Exception occurs when reading " + propsFileName, e);
		}
		
		if(connections.keySet().size() >= 1){
			connections.put("", (ConnectionWrapper) connections.values().toArray()[0]);
		}else{
			throw new NullPointerException("there isn't available DataSource");
		}
	}
	
	private static void setProp(ComboPooledDataSource cpds, String key, String value) throws PropertyVetoException {
		// necessary
		if (key.endsWith(".driverClass")) {
			cpds.setDriverClass(value);
		} else if (key.endsWith(".user")) {
			cpds.setUser(value);
		} else if (key.endsWith(".password")) {
			cpds.setPassword(value);
		} else if (key.endsWith(".url")) {
			cpds.setJdbcUrl(value);
		}
		// basic configuration
		else if (key.endsWith(".acquireIncrement")) {
			cpds.setAcquireIncrement(Integer.parseInt(value));
		} else if (key.endsWith(".initialPoolSize")) {
			cpds.setInitialPoolSize(Integer.parseInt(value));
		} else if (key.endsWith(".maxPoolSize")) {
			cpds.setMaxPoolSize(Integer.parseInt(value));
		} else if (key.endsWith(".maxIdleTime")) {
			cpds.setMaxIdleTime(Integer.parseInt(value));
		} else if (key.endsWith(".minPoolSize")) {
			cpds.setMinPoolSize(Integer.parseInt(value));
		}
		// other
		else if (key.endsWith(".maxConnectionAge")) {
			cpds.setMaxConnectionAge(Integer.parseInt(value));
		} else if (key.endsWith(".maxIdleTime")) {
			cpds.setMaxIdleTime(Integer.parseInt(value));
		} else if (key.endsWith(".maxIdleTimeExcessConnections")) {
			cpds.setMaxIdleTimeExcessConnections(Integer.parseInt(value));
		} else if (key.endsWith(".automaticTestTable")) {
			cpds.setAutomaticTestTable(value);
		} else if (key.endsWith(".connectionTesterClassName")) {
			cpds.setConnectionTesterClassName(value);
		} else if (key.endsWith(".idleConnectionTestPeriod")) {
			cpds.setIdleConnectionTestPeriod(Integer.parseInt(value));
		} else if (key.endsWith(".preferredTestQuery")) {
			cpds.setPreferredTestQuery(value);
		} else if (key.endsWith(".testConnectionOnCheckin")) {
			cpds.setTestConnectionOnCheckin(Boolean.parseBoolean(value));
		} else if (key.endsWith(".testConnectionOnCheckout")) {
			cpds.setTestConnectionOnCheckout(Boolean.parseBoolean(value));
		} else if (key.endsWith(".maxStatements")) {
			cpds.setMaxStatements(Integer.parseInt(value));
		} else if (key.endsWith(".maxStatementsPerConnection")) {
			cpds.setMaxStatementsPerConnection(Integer.parseInt(value));
		} else if (key.endsWith(".acquireRetryAttempts")) {
			cpds.setAcquireRetryAttempts(Integer.parseInt(value));
		} else if (key.endsWith(".acquireRetryDelay")) {
			cpds.setAcquireRetryDelay(Integer.parseInt(value));
		} else if (key.endsWith(".breakAfterAcquireFailure")) {
			cpds.setBreakAfterAcquireFailure(Boolean.parseBoolean(value));
		} else if (key.endsWith(".connectionCustomizerClassName")) {
			cpds.setConnectionCustomizerClassName(value);
		} else if (key.endsWith(".autoCommitOnClose")) {
			cpds.setAutoCommitOnClose(Boolean.parseBoolean(value));
		} else if (key.endsWith(".forceIgnoreUnresolvedTransactions")) {
			cpds.setForceIgnoreUnresolvedTransactions(Boolean.parseBoolean(value));
		} else if (key.endsWith(".debugUnreturnedConnectionStackTraces")) {
			cpds.setDebugUnreturnedConnectionStackTraces(Boolean.parseBoolean(value));
		} else if (key.endsWith(".unreturnedConnectionTimeout")) {
			cpds.setUnreturnedConnectionTimeout(Integer.parseInt(value));
		} else if (key.endsWith(".checkoutTimeout")) {
			cpds.setCheckoutTimeout(Integer.parseInt(value));
		} else if (key.endsWith(".factoryClassLocation")) {
			cpds.setFactoryClassLocation(value);
		} else if (key.endsWith(".maxAdministrativeTaskTime")) {
			cpds.setMaxAdministrativeTaskTime(Integer.parseInt(value));
		} else if (key.endsWith(".numHelperThreads")) {
			cpds.setNumHelperThreads(Integer.parseInt(value));
		} else if (key.endsWith(".usesTraditionalReflectiveProxies")) {
			cpds.setUsesTraditionalReflectiveProxies(Boolean.parseBoolean(value));
		}
	}
	
	/**
	 * According to the specific DataSource's name, seek a suitable Connection used for JDBC operating.
	 * if the specific dataSourceName is null, then return the first ConnectionWrapper stored, if it cann't seek any Connection, return null.
	 * 
	 * @param dataSourceName the name of the DataSource.
	 * @return the Connection seek or null.
	 */
	public static Connection getConnection(String dataSourceName){
		if(dataSourceName == null)
			dataSourceName = "";
		return connections.get(dataSourceName);
	}
	
	/**
	 * Release the Connection, accurate to say, release the thread-Connection.
	 * @param dataSourceName the name of the DataSource.
	 * @throws java.sql.SQLException
	 */
	public static void releaseConnection(String dataSourceName) throws SQLException{
		if(dataSourceName == null)
			dataSourceName = "";
		ConnectionWrapper cw = connections.get(dataSourceName);
		cw.close();
	}
	
}