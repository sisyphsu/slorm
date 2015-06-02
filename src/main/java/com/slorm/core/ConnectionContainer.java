package com.slorm.core;

import com.slorm.connection.ConnectionWrapper;
import com.slorm.connection.SpringConnectionWrapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
	 * the mapping table of ConnectionWrapper. this property will not be modified at runtime.
	 */
	private static Map<String, ConnectionWrapper> connections;

	static{
		connections = new HashMap<String, ConnectionWrapper>();
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

		if(connections.keySet().size() >= 1){
			connections.put("", (ConnectionWrapper) connections.values().toArray()[0]);
		}else{
			throw new NullPointerException("there isn't available DataSource");
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