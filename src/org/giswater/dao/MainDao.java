/*
 * This file is part of Giswater
 * Copyright (C) 2013 Tecnics Associats
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author:
 *   David Erill <daviderill79@gmail.com>
 */
package org.giswater.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MainDao {
	
	private static Connection connectionConfig;   // SQLite
    private static Connection connectionDrivers;  // SQLite 
    private static Connection connectionPostgis;  // Postgis
	private static String softwareName;   // SWMM or EPANET
    private static String schema;
    private static boolean isConnected = false;
    private static String folderConfig;
	private static String appPath;	
	private static String configPath;
    private static PropertiesMap prop = new PropertiesMap();
    
	private static final String CONFIG_FOLDER = "config";
	private static final String CONFIG_FILE = "inp.properties";
	private static final String CONFIG_DB = "config.sqlite";
	private static final String INIT_DB = "giswater_ddb";
	private static final String PORTABLE_FOLDER = "portable";
	private static final String PORTABLE_START = "start_service.bat";
	private static final String PORTABLE_STOP = "stop_service.bat";
	
	
	public static Connection getConnectionDrivers() {
		return connectionDrivers;
	}

	public static Connection getConnectionPostgis() {
		return connectionPostgis;
	}	

	public static String getSoftwareName() {
		return softwareName;
	}	
	
	public static void setSoftwareName(String param) {
		softwareName = param;
	}
	
	public static boolean isConnected() {
		return isConnected;
	}	
	
	public static void setConnected(boolean connected) {
		isConnected = connected;
	}	
	
	public static String getSchema() {
		return schema;
	}
	
	public static void setSchema(String param) {
		schema = param;
	}

	public static String getFolderConfig() {
		return folderConfig;
	}	
	
	public static String getConfigPath() {
		return configPath;
	}		
	
	
    // Sets initial configuration files
    public static boolean configIni() {

    	Utils.getLogger().info("Application started");
    	
    	if (!enabledPropertiesFile()){
    		return false;
    	}
    	
    	// Log SQL?
    	Utils.setSqlLog(prop.get("SQL_LOG", "false"));
    	
        // Get INP folder
        folderConfig = prop.get("FOLDER_CONFIG");
        folderConfig = appPath + folderConfig + File.separator;

    	// Set Config DB connection
        if (!setConnectionConfig(CONFIG_DB)){
        	return false;
        }
        
        // Start Postgis portable?
        Boolean autoStart = Boolean.parseBoolean(prop.get("POSTGIS_AUTOSTART", "true"));
        if (autoStart){
        	startPostgisService();
        }
        
        // Set Postgis connection
        Boolean autoConnect = Boolean.parseBoolean(prop.get("AUTOCONNECT_POSTGIS"));
        if (autoConnect){
        	if (silenceConnection()){
        		// If we're connecting for the first time
        		if (!MainDao.checkDatabase(INIT_DB)){
        			Utils.getLogger().info("initDatabase");
        			initDatabase();
        		}
        	}
        }
        
        return true;

    }
       
    
	public static void startPostgisService(){
		
		String path = Utils.getAppPath() + PORTABLE_FOLDER + File.separator + PORTABLE_START;
		File file = new File(path);
		if (!file.exists()){
			Utils.logError("Start service .bat not found: " + path);
			return;
		}
		execService(path);

	}
    
	
	public static void stopPostgisService(){

		String path = Utils.getAppPath() + PORTABLE_FOLDER + File.separator + PORTABLE_STOP;
		File file = new File(path);
		if (!file.exists()){
			Utils.logError("Stop service .bat not found: " + path);
			return;
		}
		execService(path);

	}
	
	
	private static void execService(String path){
		
		path = "\"" + path + "\"";
		try {
			Runtime.getRuntime().exec(path);
		} catch (IOException e) {
			Utils.logError(e);
		}		
		
	}
	
	
	public static boolean silenceConnection(){
		
		String host, port, db, user, password;
		
		// Get parameteres connection from properties file
		host = prop.get("POSTGIS_HOST", "localhost");		
		port = prop.get("POSTGIS_PORT", "5431");
		db = prop.get("POSTGIS_DATABASE", "postgres");
		user = prop.get("POSTGIS_USER", "postgres");
		password = prop.get("POSTGIS_PASSWORD");		
		password = Encryption.decrypt(password);
		password = (password == null) ? "" : password;
		
		if (host.equals("") || port.equals("") || db.equals("") || user.equals("") || password.equals("")){
			Utils.getLogger().info("Autoconnection not possible. Check parameters in inp.properties");
			return false;
		}
		
		int count = 0;
		do{
			count++;
			Utils.getLogger().info("Trying to connect: " + count);
			isConnected = setConnectionPostgis(host, port, db, user, password);
		} while (!isConnected && count < 5);
		
		//isConnected = setConnectionPostgis(host, port, db, user, password);
		if (isConnected){
			// Get Postgis data and bin folder
	    	String dataPath = MainDao.getDataDirectory();
	    	prop.put("POSTGIS_DATA", dataPath);
	        File dataFolder = new File(dataPath);
	        String binPath = dataFolder.getParent() + File.separator + "bin";
	    	prop.put("POSTGIS_BIN", binPath);	
			Utils.getLogger().info("Autoconnection successful");
	    	Utils.getLogger().info("Postgis data directory: " + dataPath);		    	
	    	//Utils.getLogger().info("Postgis bin directory: " + binPath);
			return true;
		}
		else{
			Utils.getLogger().info("Autoconnection error");			
			return false;
		}
		
	}	    
    
    
	public static void initDatabase(){
		
		String bin = prop.getProperty("POSTGIS_BIN", "");
		File file = new File(bin);
		if (!file.exists()){
			Utils.showError("postgis_not_found", bin);
			return;			
		}
		bin+= File.separator;
		
		// Execute script that creates working Database
		String filePath =  Utils.getAppPath() + "sql/init_giswater_ddb.sql";
    	String content;
		try {
			content = Utils.readFile(filePath);
			Utils.logSql(content);
			if (!executeUpdateSql(content, true, false)){
				return;
			}				
		} catch (IOException e) {
			Utils.logError(e);
			return;
		}
		
		// Close current connection in order to connect to default Database just created
		prop.setProperty("POSTGIS_DATABASE", INIT_DB);
		closeConnectionPostgis();
		silenceConnection();
		
		// Enable Postgis to Database
		String sql = "CREATE EXTENSION postgis; CREATE EXTENSION postgis_topology;";
		MainDao.executeUpdateSql(sql, true, false);

	}
	
	
	public static void rollback(){
		try {
			connectionPostgis.rollback();
		} catch (SQLException e) {
            Utils.showError(e);
		}
	}	

	
    public static PropertiesMap getPropertiesFile() {
        return prop;
    }


    public static void savePropertiesFile() {

        File iniFile = new File(configPath);
        try {
        	FileOutputStream fos = new FileOutputStream(iniFile);
            prop.store(fos, true);
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", iniFile.getPath());
        } catch (IOException e) {
            Utils.showError("inp_error_io", iniFile.getPath());
        }

    }
    
    
    // Get Properties Files
    public static boolean enabledPropertiesFile() {

    	appPath = Utils.getAppPath();
        configPath = appPath + CONFIG_FOLDER + File.separator + CONFIG_FILE;
        File fileIni = new File(configPath);
        try {
        	prop.load(new FileInputStream(fileIni));      
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", configPath);
            return false;
        } catch (IOException e) {
            Utils.showError("inp_error_io", configPath);
            return false;
        }
        return (prop != null);

    }    
	
    
    // Connect to Config sqlite Database
    public static boolean setConnectionConfig(String fileName) {

        try {
            Class.forName("org.sqlite.JDBC");
            String filePath = folderConfig + fileName;
            File file = new File(filePath);
            if (file.exists()) {
            	connectionConfig = DriverManager.getConnection("jdbc:sqlite:" + filePath);
                return true;
            } else {
                Utils.showError("inp_error_notfound", filePath);
                return false;
            }
        } catch (SQLException e) {
            Utils.showError("inp_error_connection", e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            Utils.showError("inp_error_connection", "ClassNotFoundException");
            return false;
        }

    }
    
    
    // Connect to sqlite drivers database
    public static boolean setConnectionDrivers(String fileName) {

        try {
            Class.forName("org.sqlite.JDBC");
            String filePath = folderConfig + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                connectionDrivers = DriverManager.getConnection("jdbc:sqlite:" + filePath);
                return true;
            } else {
                Utils.showError("inp_error_notfound", filePath);
                return false;
            }
        } catch (SQLException e) {
            Utils.showError("inp_error_connection", e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            Utils.showError("inp_error_connection", "ClassNotFoundException");
            return false;
        }

    }  
    
    
	public static String getSoftwareVersion(String type, String id) {
		
        String sql = "SELECT software_version FROM "+type+"_software WHERE id = '"+id+"'";
        String result = "";
        try {
            Statement stat = connectionConfig.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()) {
            	 result = rs.getString(1);
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
        	Utils.showError(e, sql);
        }
        return result;   
        
	}
	
	
    public static boolean setConnectionPostgis(String host, String port, String db, String user, String password) {
    	
        String connectionString = "jdbc:postgresql://"+host+":"+port+"/"+db+"?user="+user+"&password="+ password;
        try {
            connectionPostgis = DriverManager.getConnection(connectionString);
        } catch (SQLException e) {
            try {
                connectionPostgis = DriverManager.getConnection(connectionString);
            } catch (SQLException e1) {
                Utils.logError(e1);
                return false;
            }   		
        }
        return true;
        
    }	
    
    
    public static void closeConnectionPostgis(){
    	try {
			connectionPostgis.close();
			isConnected = false;
		} catch (SQLException e) {
            Utils.showError(e);
		}
    }

    
	public static boolean executeUpdateSql(String sql) {
		return executeUpdateSql(sql, false);
	}	
	
    
	public static boolean executeUpdateSql(String sql, boolean commit) {
		return executeUpdateSql(sql, commit, false);
	}
	
	
	public static boolean executeUpdateSql(String sql, boolean commit, boolean showError) {
		
		try {
			Statement ps = connectionPostgis.createStatement();
	        ps.executeUpdate(sql);
	        if (commit && !connectionPostgis.getAutoCommit()){
	        	connectionPostgis.commit();
	        }
			return true;
		} catch (SQLException e) {
			if (showError){
				Utils.showError(e, sql);
			} else{
				Utils.logError(e, sql);
			}
			return false;
		}
		
	}	
	
	
	public static boolean executeSql(String sql) {
		return executeSql(sql, false);
	}	
	
	
	public static boolean executeSql(String sql, boolean commit) {
		try {
			Statement ps = connectionPostgis.createStatement();
	        ps.execute(sql);
			if (commit && !connectionPostgis.getAutoCommit()){
	        	connectionPostgis.commit();
	        }			
			return true;
		} catch (SQLException e) {
			Utils.showError(e, sql);
			return false;
		}
	}		
	
	
    // Check if the column exists in ResultSet	
	public static boolean checkColumn(ResultSet rs, String columnName) {
		
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
		    int columns = rsmd.getColumnCount();
		    for (int x = 1; x <= columns; x++) {
		        if (columnName.equals(rsmd.getColumnName(x))) {
		            return true;
		        }
		    }			
		} catch (SQLException e) {
        	Utils.showError(e);
            return false;
		}
	    return false;
	    
	}	
	
	
    // Check if the table exists
	public static boolean checkTable(String tableName) {
		
        String sql = "SELECT * FROM pg_tables" +
        	" WHERE lower(tablename) = '" + tableName + "'";
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            return (rs.next());
        } catch (SQLException e) {
        	Utils.showError(e);
            return false;
        }
        
    }
	
	
    // Check if the table exists
	public static boolean checkTable(String schemaName, String tableName) {
		
        String sql = "SELECT * FROM pg_tables" +
        	" WHERE lower(schemaname) = '"+schemaName+"' AND lower(tablename) = '"+tableName+"'";
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            return (rs.next());
        } catch (SQLException e) {
        	Utils.showError(e, sql);
            return false;
        }
        
    }	
    
    
    // Check if the view exists
    public static boolean checkView(String viewName) {
    	
        String sql = "SELECT * FROM pg_views" +
        	" WHERE lower(viewname) = '"+viewName+"'";
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            return (rs.next());
        } catch (SQLException e) {
        	Utils.showError(e, sql);
            return false;
        }
        
    }    
    
    
    // Check if the view exists
    public static boolean checkView(String schemaName, String viewName) {
    	
        String sql = "SELECT * FROM pg_views" +
        	" WHERE lower(schemaname) = '"+schemaName+"' AND lower(viewname) = '"+viewName+"'";
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            return (rs.next());
        } catch (SQLException e) {
        	Utils.showError(e, sql);
            return false;
        }
        
    }        
    
    
    // Check if database exists
    public static boolean checkDatabase(String dbName) {
    	
        String sql = "SELECT 1 FROM pg_database WHERE datname = '"+dbName+"'";
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            return (rs.next());
        } catch (SQLException e) {
        	Utils.showError(e, sql);
            return false;
        }
        
    } 
    

    
    // Check if the selected srid exists in spatial_ref_sys
	public static boolean checkSrid(Integer srid) {
		
        String sql = "SELECT srid FROM spatial_ref_sys WHERE srid = "+srid;
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            return (rs.next());
        } catch (SQLException e) {
        	Utils.showError(e, sql);
            return false;
        }
        
    }    
    
    
	public static Vector<String> getSchemas(){

        String sql = "SELECT schema_name FROM information_schema.schemata " +
        	"WHERE schema_name <> 'information_schema' AND schema_name !~ E'^pg_' " +
        	"AND schema_name <> 'drivers' AND schema_name <> 'public' AND schema_name <> 'topology' " +
        	"ORDER BY schema_name";
        Vector<String> vector = new Vector<String>();
        if (isConnected()){
	        try {
	    		connectionPostgis.setAutoCommit(false);        	
	            Statement stat = connectionPostgis.createStatement();
	            ResultSet rs = stat.executeQuery(sql);
	            while (rs.next()) {
	            	vector.add(rs.getString(1));
	            }
	            rs.close();
	    		return vector;	            
	        } catch (SQLException e) {
	            Utils.showError(e, sql);
	            return vector;
		    } catch (NullPointerException e) {
		        Utils.logError(e);
		        return vector;
		    }
        }
        else{
        	return vector;
        }
		
	}
	
	
	private static ResultSet getResultset(Connection connection, String sql){
		
        ResultSet rs = null;        
        try {
        	connection.setAutoCommit(true);
        	Statement stat = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stat.executeQuery(sql);
        } catch (SQLException e) {
        	Utils.showError(e, sql);
        }
        return rs;   
        
	}

	
	public static ResultSet getResultset(String sql){
		return getResultset(connectionPostgis, sql);
	}
	
	
	public static ResultSet getTableResultset(Connection connection, String table, String fields) {
		
		String sql;
		if (schema == null){
			sql = "SELECT "+fields+" FROM "+table;
		} 
		else{
			sql = "SELECT "+fields+" FROM "+schema+"."+table;
		}
        return getResultset(connection, sql);
        
	}
	
	
	public static ResultSet getTableResultset(String table, String fields) {
		return getTableResultset(connectionPostgis, table, fields);
	}
	
	public static ResultSet getTableResultset(String table) {
		return getTableResultset(connectionPostgis, table, "*");
	}
	
	
	public static ResultSet getRaingageResultset(String table) {
        String sql = "SELECT rg_id, form_type, intvl, scf, rgage_type, timser_id, fname, sta, units" +
        	" FROM "+schema+"." + table;
        return getResultset(sql);
	}	

	
    public static Vector<String> getAvailableVersions(String type, String software){

        Vector<String> vector = new Vector<String>();
        String sql = "SELECT id" +
        	" FROM "+type+"_software" +  
        	" WHERE available = 1 AND software_name = '"+software+"'" +
        	" ORDER BY id DESC";            
		try {
			Statement stat = connectionConfig.createStatement();
	        ResultSet rs = stat.executeQuery(sql);
	        while (rs.next()) {
	        	vector.add(rs.getString("id"));
	        }
	        rs.close();   
            stat.close();	        
		} catch (SQLException e) {
            Utils.showError(e, sql);
		}            
		return vector;
    	
    }


	public static Vector<String> getTable(String table, String schemaParam, boolean addBlank, String fields) {
        
        Vector<String> vector = new Vector<String>();
        
        if (addBlank){
        	vector.add("");
        }
		if (schemaParam == null){
			schemaParam = schema;
		}
		if (!checkTable(schemaParam, table)) {
			return vector;
		}
		String sql = "SELECT "+fields+" FROM "+schemaParam+"."+table;
		try {
			Statement stat = connectionPostgis.createStatement();
	        ResultSet rs = stat.executeQuery(sql);
	        while (rs.next()) {
	        	vector.add(rs.getString(1));
	        }
	        stat.close();
		} catch (SQLException e) {
            Utils.showError(e, sql);
		}            
		return vector;
		
	}	
	
	
	public static Vector<String> getTable(String table, String schemaParam) {
		return getTable(table, schemaParam, false, "*");
	}
	
	
	public static void setResultSelect(String schema, String table, String result) {
		String sql = "DELETE FROM "+schema+"."+table;
		Utils.logSql(sql);
		executeUpdateSql(sql);
		sql = "INSERT INTO "+schema+"."+table+" VALUES ('"+result+"')";
		executeUpdateSql(sql);
		Utils.logSql(sql);
	}
	

	public static void deleteSchema(String schemaName) {
		String sql = "DROP schema IF EXISTS "+schemaName+" CASCADE;";
		executeUpdateSql(sql, true);		
		sql = "DELETE FROM public.geometry_columns WHERE f_table_schema = '"+schemaName+"'";
		executeUpdateSql(sql, true);			
	}

	
	public static boolean createSchema(String softwareName, String schemaName, String srid) {
		
		boolean status = false;
		String sql = "CREATE schema "+schemaName;
		if (!executeUpdateSql(sql, true)){
			rollback();
			return status;	
		}
		String filePath = "";
		String content = "";
    	
		try {

	    	String folderRoot = new File(".").getCanonicalPath() + File.separator;			
			filePath = folderRoot + "sql/"+softwareName+".sql";
	    	content = Utils.readFile(filePath);
			
	    	// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter
			content = content.replace("SCHEMA_NAME", schemaName);
			content = content.replace("SRID_VALUE", srid);
			Utils.logSql(content);
			
			if (executeSql(content, true)){
			
				filePath = folderRoot + "sql/"+softwareName+"_value_domain.sql";
		    	content = Utils.readFile(filePath);
				content = content.replace("SCHEMA_NAME", schemaName);		   
				Utils.logSql(content);
		    	
				if (executeUpdateSql(content, true)){
					filePath = folderRoot + "sql/"+softwareName+"_functrigger.sql";
			    	content = Utils.readFile(filePath);
					content = content.replace("SCHEMA_NAME", schemaName);
					Utils.logSql(content);
					if (executeUpdateSql(content, true)){
						status = true;
					}
				}
		    	
			}
			
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", filePath);
        } catch (IOException e) {
            Utils.showError(e, filePath);
        }
		return status;
		
	}

		
	public static int getRowCount(ResultSet resultSet) {
		
	    if (resultSet == null) {
	        return 0;
	    }
	    try {
	        resultSet.last();
	        return resultSet.getRow();
	    } catch (SQLException exp) {
	        exp.printStackTrace();
	    } finally {
	        try {
	            resultSet.beforeFirst();
	        } catch (SQLException exp) {
	            exp.printStackTrace();
	        }
	    }
	    return 0;
	    
	}	
	
	
	// hecRas functions
	public static boolean createSchemaHecRas(String softwareName, String schemaName, String srid) {
		
		String filePath = "";
		try {		
			filePath = Utils.getAppPath() + "sql" + File.separator + softwareName + ".sql";
			String destPath = Utils.getLogFolder() + softwareName + "_" + schemaName + ".sql";
			String batPath = Utils.getLogFolder() + softwareName + "_" + schemaName + ".bat";
			String content = Utils.readFile(filePath);
	    	// Replace SCHEMA_NAME for schemaName parameter. __USER__ for user
	    	String user = prop.get("POSTGIS_USER");
			content = content.replace("SCHEMA_NAME", schemaName);
			content = content.replace("__USER__", user);		
			Utils.logSql(content);
			Utils.fillFile(new File(destPath), content);
			executeScript(destPath, batPath);
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", filePath);
        } catch (IOException e) {
            Utils.showError(e, filePath);
		}
		return true;
		
	}
	
	
	public static boolean executeScript(String scriptPath, String batPath) {

		String aux;
		String bin, host, port, db, user;
		
		bin = prop.getProperty("POSTGIS_BIN", "");
		host = prop.getProperty("POSTGIS_HOST", "localhost");
		port = prop.getProperty("POSTGIS_PORT", "5431");
		db = prop.getProperty("POSTGIS_DATABASE", "giswater");
		user = prop.getProperty("POSTGIS_USER", "postgres");
		
		File file = new File(bin);
		if (!file.exists()){
			Utils.showError("postgis_not_found", bin);
			return false;			
		}
		bin+= File.separator;
		
		// Set content of .bat file
		aux= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+ " -f "+scriptPath;
		aux+= "\nexit";		
		Utils.getLogger().info(aux);

        // Fill and execute .bat File	
		File batFile = new File(batPath);        
		Utils.fillFile(batFile, aux);    		
		Utils.openFile(batFile.getAbsolutePath());
		
		return true;
			
	}	
	
	
	public static boolean createSdfFile(String schemaName, String fileName) {
		String sql = "SELECT "+schemaName+".gr_export_geo('"+fileName+"');";
		Utils.logSql(sql);
		return executeSql(sql, true);	
	}
	
	
	public static boolean clearData(String schemaName){
		String sql = "SELECT "+schemaName+".gr_clear();";
		Utils.logSql(sql);
		return executeSql(sql, true);	
	}


	public static String getDataDirectory() {
		
		String sql = "SELECT setting FROM pg_settings WHERE name = 'data_directory'";
		String folder = "";
		try {
			Statement stat = connectionPostgis.createStatement();
	        ResultSet rs = stat.executeQuery(sql);
	        if (rs.next()) {
	        	folder = rs.getString(1);
	        }
	        stat.close();
		} catch (SQLException e) {
            Utils.showError(e, sql);
		}    		
		return folder;
		
	}


	public static boolean loadRaster(String schemaName, String raster) {

		String fileSql, aux, logFolder;
		String bin, host, port, db, user;
		
		fileSql = raster.replace(".asc", ".sql");
		bin = prop.getProperty("POSTGIS_BIN", "");
		host = prop.getProperty("POSTGIS_HOST", "localhost");
		port = prop.getProperty("POSTGIS_PORT", "5431");
		db = prop.getProperty("POSTGIS_DATABASE", "giswater");
		user = prop.getProperty("POSTGIS_USER", "postgres");
		logFolder = Utils.getLogFolder();
		
		File file = new File(bin);
		if (!file.exists()){
			Utils.showError("postgis_not_found", bin);
			return false;			
		}
		bin+= File.separator;
		
		// Set content of .bat file
		aux = "\""+bin+"raster2pgsql\" -d -s 0 -I -C -M "+raster+" -F -t 100x100 "+schemaName+".mdt > "+fileSql;
		aux+= "\n";
		aux+= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+ " -c \"drop table if exists "+schemaName+".mdt\";";
		aux+= "\n";		
		aux+= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+ " -f "+fileSql+" > "+logFolder+"raster2pgsql.log";
		aux+= "\ndel " + fileSql;
		aux+= "\nexit";		
		Utils.getLogger().info(aux);

        // Fill and execute .bat File	
		File batFile = new File(logFolder + "raster2pgsql.bat");        
		Utils.fillFile(batFile, aux);    		
		Utils.openFile(batFile.getAbsolutePath());
		
		return true;
			
	}


}