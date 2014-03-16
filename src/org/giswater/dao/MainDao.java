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
 *   David Erill <derill@giswater.org>
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
	private static String softwareName;   // EPASWMM or EPANET
    private static String schema;
    private static boolean isConnected = false;
    private static String folderConfig;
	private static String appPath;	
	private static String configPath;
	private static String gswPath;
    private static PropertiesMap prop = new PropertiesMap();
    private static PropertiesMap gswProp = new PropertiesMap();
    
    private static final String PROJECT_FOLDER = "giswater" + File.separator;
	private static final String CONFIG_FOLDER = "config" + File.separator;
	private static final String CONFIG_FILE = "giswater.properties";
	private static final String CONFIG_DB = "config.sqlite";
	private static final String INIT_DB = "giswater_ddb";
	private static final String PORTABLE_FOLDER = "portable" + File.separator;
	private static final String PORTABLE_FILE = "bin" + File.separator + "pg_ctl.exe";
	private static final String GSW_FILE = "default.gsw";
	private static final String INIT_GISWATER_DDB = "init_giswater_ddb.sql";	
	private static final String TABLE_EPANET = "inp_demand";		
	private static final String TABLE_EPASWMM = "inp_divider";	
	private static final String TABLE_HECRAS = "banks";		
	
	
	public static String getGswPath(){
		return gswPath;
	}
	
	public static void setGswPath(String path) {
		gswPath = path;
	}
	
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
	
	
    // Sets initial configuration files
    public static boolean configIni() {

    	Utils.getLogger().info("Application started");
    	
    	// Load Properties files
    	if (!loadPropertiesFile()) return false;
    	
    	// Load last gsw file
    	String gswPath = prop.get("FILE_GSW", "").trim();
    	File gswFile = new File(gswPath);
    	if (!gswFile.exists()){
            Utils.logError("inp_error_notfound", gswPath);
        	// Get default gsw path
            gswPath = System.getProperty("user.home") + File.separator + PROJECT_FOLDER + CONFIG_FOLDER + GSW_FILE;
        	gswFile = new File(gswPath);  
        	if (!gswFile.exists()){
                Utils.showError("gsw_default_notfound", gswPath);    
                return false;
        	}
    	}
    	MainDao.setGswPath(gswPath);
    	loadGswPropertiesFile();
    	
    	// Log SQL?
    	Utils.setSqlLog(prop.get("SQL_LOG", "false"));
    	
        // Get INP folder
        folderConfig = prop.get("FOLDER_CONFIG");
        folderConfig = appPath + folderConfig + File.separator;

    	// Set Config DB connection
        if (!setConnectionConfig(CONFIG_DB)){
        	return false;
        }
        
        // Start Postgis portable
    	executePostgisService("start");
    	       
        return true;

    }
       
    
	public static void executePostgisService(String service){
		
		String folder  = System.getProperty("user.home") + File.separator + PROJECT_FOLDER + PORTABLE_FOLDER;
		String path = folder + PORTABLE_FILE;		
		File file = new File(path);
		if (!file.exists()){
			Utils.logError("Postgis service not found: "+path);
			return;
		}
		String data = folder + "data";
		String exec = "start \"PostgreSQL server running\" \""+path+"\" "+service+" -D \""+data+"\"";
		Utils.execService(exec);
		
	}
    
		
	public static boolean silenceConnection(){
		
		String host, port, db, user, password;
		Boolean status = true;
		
		// Get parameteres connection from properties file
		host = gswProp.get("POSTGIS_HOST", "localhost");		
		port = gswProp.get("POSTGIS_PORT", "5431");
		db = gswProp.get("POSTGIS_DATABASE", "postgres");
		user = gswProp.get("POSTGIS_USER", "postgres");
		password = gswProp.get("POSTGIS_PASSWORD");		
		password = Encryption.decrypt(password);
		password = (password == null) ? "" : password;
		
		if (host.equals("") || port.equals("") || db.equals("") || user.equals("")){
			Utils.getLogger().info("Autoconnection not possible. Check parameters in properties file");
			return false;
		}
		
		int count = 0;
		do{
			count++;
			Utils.getLogger().info("Trying to connect: " + count);
			isConnected = setConnectionPostgis(host, port, db, user, password, false);
		} while (!isConnected && count < 5);
		
		if (isConnected){
			// Get Postgis data and bin folder
	    	String dataPath = MainDao.getDataDirectory();
	    	gswProp.put("POSTGIS_DATA", dataPath);
	        File dataFolder = new File(dataPath);
	        String binPath = dataFolder.getParent() + File.separator + "bin";
	    	gswProp.put("POSTGIS_BIN", binPath);	
			Utils.getLogger().info("Autoconnection successful");
	    	Utils.getLogger().info("Postgis data directory: " + dataPath);		    	
	    	Utils.getLogger().info("Postgre version: " + MainDao.checkPostgreVersion());
        	String postgisVersion = MainDao.checkPostgisVersion();	        
        	if (postgisVersion.equals("")){
				// Enable Postgis to current Database
				String sql = "CREATE EXTENSION postgis; CREATE EXTENSION postgis_topology;";
				executeUpdateSql(sql, true, false);			  	
        	}
        	else{
        		Utils.getLogger().info("Postgis version: " + postgisVersion);
        	}
		}
		else{
			Utils.getLogger().info("Autoconnection error");			
			status = false;	
		}
		
		return status;
		
	}	    
    
	
	public static boolean initializeDatabase(){
		
		String host, port, db, user, password;
		Boolean status = true;
		
		// Get parameteres connection from properties file
		host = gswProp.get("POSTGIS_HOST", "localhost");		
		port = gswProp.get("POSTGIS_PORT", "5431");
		db = gswProp.get("POSTGIS_DATABASE", "postgres");
		user = gswProp.get("POSTGIS_USER", "postgres");
		password = gswProp.get("POSTGIS_PASSWORD");		
		password = Encryption.decrypt(password);
		password = (password == null) ? "" : password;
		
		if (host.equals("") || port.equals("") || db.equals("") || user.equals("")){
			Utils.getLogger().info("Autoconnection not possible. Check parameters in properties file");
			return false;
		}
		
		int count = 0;
		do{
			count++;
			Utils.getLogger().info("Trying to connect: " + count);
			isConnected = setConnectionPostgis(host, port, db, user, password, false);
		} while (!isConnected && count < 5);
		
		if (isConnected){
	    	String dataPath = MainDao.getDataDirectory();
	    	gswProp.put("POSTGIS_DATA", dataPath);
	        File dataFolder = new File(dataPath);
	        String binPath = dataFolder.getParent() + File.separator + "bin";
	    	gswProp.put("POSTGIS_BIN", binPath);	
			if (!MainDao.checkDatabase(INIT_DB)){
				Utils.getLogger().info("initDatabase");
				initDatabase();
			} 
			// Close current connection in order to connect later to default Database just created
			closeConnectionPostgis();			
		}
		else{
			Utils.getLogger().info("Autoconnection error");			
			status = false;	
		}
		
		return status;
		
	}	 	
    
	
	private static void initDatabase(){
		
		String bin = gswProp.getProperty("POSTGIS_BIN", "");
		File file = new File(bin);
		if (!file.exists()){
			Utils.showError("postgis_not_found", bin);
			return;			
		}
		bin+= File.separator;
		
		// Execute script that creates working Database
		String filePath = Utils.getAppPath() + "sql" + File.separator + INIT_GISWATER_DDB;
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
		
		// Update gsw properties file
		gswProp.setProperty("POSTGIS_DATABASE", INIT_DB);

//		// Close current connection in order to connect later to default Database just created
//		closeConnectionPostgis();

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
    
    
    public static PropertiesMap getGswProperties() {
        return gswProp;
    }    


    public static void savePropertiesFile() {

        File file = new File(configPath);
        try {
        	FileOutputStream fos = new FileOutputStream(file);
            prop.store(fos, true);
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", file.getPath());
        } catch (IOException e) {
            Utils.showError("inp_error_io", file.getPath());
        }

    }
    

    public static void saveGswPropertiesFile() {

        File file = new File(gswPath);
        try {
        	FileOutputStream fos = new FileOutputStream(file);
            gswProp.store(fos, true);
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", file.getPath());
        } catch (IOException e) {
            Utils.showError("inp_error_io", file.getPath());
        }

    }    
    
    
    // Load Properties and gsw default files
    public static boolean loadPropertiesFile() {

    	appPath = Utils.getAppPath();
        configPath = System.getProperty("user.home") + File.separator + PROJECT_FOLDER + CONFIG_FOLDER + CONFIG_FILE;
    	Utils.getLogger().info("Properties file: "+configPath);        

        File file = new File(configPath);
        try {
        	prop.load(new FileInputStream(file));      
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", configPath);
            return false;
        } catch (IOException e) {
            Utils.showError("inp_error_io", configPath);
            return false;
        }
        return (prop != null);

    }    
    
    
    public static boolean loadGswPropertiesFile() {

    	if (gswPath.equals("")){
    		gswPath = System.getProperty("user.home") + File.separator + PROJECT_FOLDER + CONFIG_FOLDER + GSW_FILE;
    	}
    	Utils.getLogger().info("Loading gsw file: "+gswPath);        

        File file = new File(gswPath);
        try {
        	gswProp.load(new FileInputStream(file));      
        } catch (FileNotFoundException e) {
            Utils.logError("inp_error_notfound", gswPath);
            return false;
        } catch (IOException e) {
            Utils.showError("inp_error_io", gswPath);
            return false;
        }
        return (gswProp != null);

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
		return setConnectionPostgis(host, port, db, user, password, true);
	}
	
    public static boolean setConnectionPostgis(String host, String port, String db, String user, String password, boolean showError) {
    	
        String connectionString = "jdbc:postgresql://"+host+":"+port+"/"+db+"?user="+user+"&password="+password;
        try {
            connectionPostgis = DriverManager.getConnection(connectionString);
        } catch (SQLException e) {
            try {
                connectionPostgis = DriverManager.getConnection(connectionString);
            } catch (SQLException e1) {
            	if (showError){
            		Utils.showError(e1);
            	} else{
            		Utils.logError(e1);
            	}
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
	
	
    // Check if the table has data
	public static boolean checkTableHasData(String schemaName, String tableName) {
		
		if (!checkTable(schemaName, tableName)) return false;
        String sql = "SELECT count(*) FROM "+schemaName+"."+tableName;
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()){
            	return (rs.getInt(1) != 0);
            }
            return false;
        } catch (SQLException e) {
        	Utils.showError(e, sql);
            return false;
        }
        
    }	
	
	
	private static boolean checkQuery(String sql){
		boolean check = false;
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            check = rs.next();
            rs.close();
            stat.close();
        } catch (SQLException e) {
        	Utils.showError(e);
        }		
        return check;
	}
	
	
	private static String stringQuery(String sql){
    	String value = "";
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);        	
			if (rs.next()){
				value = rs.getString(1);
			}
	        rs.close();	
	        stat.close();
		} catch (SQLException e) {
        	Utils.logError(e.getMessage());
		}
        return value;
	}	
	
	
    // Check if the table exists
	public static boolean checkTable(String tableName) {
        String sql = "SELECT * FROM pg_tables" +
        	" WHERE lower(tablename) = '" + tableName + "'";
        return checkQuery(sql);
    }	
	
	
    // Check if the table exists
	public static boolean checkTable(String schemaName, String tableName) {
        String sql = "SELECT * FROM pg_tables" +
        	" WHERE lower(schemaname) = '"+schemaName+"' AND lower(tablename) = '"+tableName+"'";
        return checkQuery(sql);
    }	
    
    
    // Check if the view exists
    public static boolean checkView(String viewName) {
        String sql = "SELECT * FROM pg_views" +
        	" WHERE lower(viewname) = '"+viewName+"'";
        return checkQuery(sql);
    }    
    
    
    // Check if the view exists
    public static boolean checkView(String schemaName, String viewName) {
        String sql = "SELECT * FROM pg_views" +
        	" WHERE lower(schemaname) = '"+schemaName+"' AND lower(viewname) = '"+viewName+"'";
        return checkQuery(sql);
    }        
    
    
    // Check if database exists
    public static boolean checkDatabase(String dbName) {
        String sql = "SELECT 1 FROM pg_database WHERE datname = '"+dbName+"'";
        return checkQuery(sql);
    } 
    
    
    public static String checkPostgreVersion(){
        String sql = "SELECT version()";
        return stringQuery(sql);
    }
    
    public static String checkPostgisVersion(){
        String sql = "SELECT PostGIS_full_version()";
        return stringQuery(sql);
    }
    
    
    // Check if the selected srid exists in spatial_ref_sys
	public static boolean checkSrid(Integer srid) {
        String sql = "SELECT srid FROM public.spatial_ref_sys WHERE srid = "+srid;
        return checkQuery(sql);
    }    
    
    private static boolean checkSoftwareSchema(String software, String schemaName){
    	
    	String tableName = "";
        if (software.equals("EPANET")){
        	tableName = TABLE_EPANET;
        }
        else if (software.equals("EPASWMM") || software.equals("EPA SWMM")){
        	tableName = TABLE_EPASWMM;
        }
        else if (software.equals("HECRAS") || software.equals("HEC-RAS")){
        	tableName = TABLE_HECRAS;
        }
        return checkTable(schemaName, tableName);

    }
    
	public static Vector<String> getSchemas(){
		return getSchemas("");
	}
    
	public static Vector<String> getSchemas(String software){

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
	            	String schemaName = rs.getString(1);
	            	if (!software.equals("")){
	            		// Add current schema only if "belongs" to software we're working on
	            		if (checkSoftwareSchema(software, schemaName)){
	            			vector.add(schemaName);
	            		}
	            	}
	            	else{
	            		vector.add(schemaName);
	            	}
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
	
	
	private static ResultSet getResultset(Connection connection, String sql, boolean showError){
		
        ResultSet rs = null;        
        try {
        	connection.setAutoCommit(true);
        	Statement stat = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stat.executeQuery(sql);
        } catch (SQLException e) {
			if (showError){
				Utils.showError(e, sql);
			} else{
				Utils.logError(e, sql);
			}
        }
        return rs;   
        
	}

	
	public static ResultSet getResultset(String sql){
		return getResultset(connectionPostgis, sql, true);
	}
	
	
	public static ResultSet getTableResultset(Connection connection, String table, String fields) {
		
		String sql;
		if (schema == null){
			sql = "SELECT "+fields+" FROM "+table;
		} 
		else{
			sql = "SELECT "+fields+" FROM "+schema+"."+table;
		}
        return getResultset(connection, sql, true);
        
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
		executeUpdateSql(sql, true);
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
		if (!executeUpdateSql(sql, true, true)){
			rollback();
			return status;	
		}
		String filePath = "";
		String content = "";
    	
		try {

	    	String folderRoot = new File(".").getCanonicalPath()+File.separator;			
			filePath = folderRoot+"sql"+File.separator+softwareName+".sql";
	    	content = Utils.readFile(filePath);
			
	    	// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter
			content = content.replace("SCHEMA_NAME", schemaName);
			content = content.replace("SRID_VALUE", srid);
			Utils.logSql(content);
			
			if (executeSql(content, true)){
			
				filePath = folderRoot+"sql"+File.separator+softwareName+"_value_domain.sql";
		    	content = Utils.readFile(filePath);
				content = content.replace("SCHEMA_NAME", schemaName);		   
				Utils.logSql(content);
				if (executeUpdateSql(content, true)){
					filePath = folderRoot+"sql"+File.separator+softwareName+"_functrigger.sql";
			    	content = Utils.readFile(filePath);
					content = content.replace("SCHEMA_NAME", schemaName);
					Utils.logSql(content);
					status = executeUpdateSql(content, true);
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
	    } catch (SQLException e) {
	    	Utils.logError(e);
	    } finally {
	        try {
	            resultSet.beforeFirst();
	        } catch (SQLException e) {
	            Utils.logError(e);
	        }
	    }
	    return 0;
	    
	}	
	
	
	// Gis functions
	public static Integer getTableSrid(String schema, String table) {
		
		Integer srid = 0;
		String sql = "SELECT srid FROM public.geometry_columns"+
			" WHERE f_table_schema = '"+schema+"' AND f_table_name = '"+table+"'";
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()){
            	srid = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
        	Utils.showError(e, sql);
        }
        return srid;
            
	}
	
	
	public static String replaceSpatialParameters(String schemaSrid, String content) {
		
		String aux = content;
        String sql = "SELECT parameters, srs_id, srid, auth_name || ':' || auth_id as auth_id, description," +
        	" projection_acronym, ellipsoid_acronym, is_geo" + 
        	" FROM srs WHERE srid = '"+schemaSrid+"'"; 
        try {
            Statement stat = connectionConfig.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()){
            	aux = aux.replace("__PROJ4__", rs.getString(1));
            	aux = aux.replace("__SRSID__", rs.getString(2));
            	aux = aux.replace("__SRID__", rs.getString(3));
            	aux = aux.replace("__AUTHID__", rs.getString(4));
            	aux = aux.replace("__DESCRIPTION__", rs.getString(5));
            	aux = aux.replace("__PROJECTIONACRONYM__", rs.getString(6));
            	aux = aux.replace("__ELLIPSOIDACRONYM__", rs.getString(7));
            	String geo = "false";
            	if (rs.getInt(8) != 0){
            		geo = "true";
            	}
            	aux = aux.replace("__GEOGRAPHICFLAG__", geo);
            }
            rs.close();
        } catch (SQLException e) {
        	Utils.showError(e, sql);
        }		
		return aux;
		
	}	
	
	
	public static String replaceExtentParameters(String software, String schemaName, String content) {
		
		String aux = content;
		String tableName;
		String geomName;
		if (software.equals("HECRAS")){
			tableName = "xscutlines";
			geomName = "geom";
		}
		else{
			tableName = "node";
			geomName = "the_geom";
		}
		String sql = "SELECT ST_XMax(gometries) AS xmax, ST_XMin(gometries) AS xmin," +
			" ST_YMax(gometries) AS ymax, ST_YMin(gometries) AS ymin" +
			" FROM (SELECT ST_Collect("+geomName+") AS gometries FROM "+schemaName+"."+tableName+") AS foo";
        try {
            Statement stat = connectionPostgis.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()){
            	aux = aux.replace("__XMIN__", (rs.getString(2) == null) ? "-1.555992" : rs.getString(2));
            	aux = aux.replace("__YMIN__", (rs.getString(4) == null) ? "-1.000000" : rs.getString(4));            	
            	aux = aux.replace("__XMAX__", (rs.getString(1) == null) ? "1.555992" : rs.getString(1));
            	aux = aux.replace("__YMAX__", (rs.getString(3) == null) ? "1.000000" : rs.getString(3));
            }
            rs.close();
        } catch (SQLException e) {
        	Utils.showError(e, sql);
        } catch (NullPointerException e) {
        	Utils.logError(e, sql);
        }			
		return aux;
		
	}		
		
	
	// hecRas functions
	public static boolean createSchemaHecRas(String softwareName, String schemaName, String srid) {
		
		boolean status = false;
		String filePath = "";
		try {		
			filePath = Utils.getAppPath()+"sql"+File.separator+softwareName+".sql";
			String content = Utils.readFile(filePath);
			if (content.equals("")) return false;
	    	// Replace SCHEMA_NAME for schemaName parameter. __USER__ for user
			content = content.replace("SCHEMA_NAME", schemaName);
			content = content.replace("SRID_VALUE", srid);			
			content = content.replace("__USER__", gswProp.get("POSTGIS_USER"));		
			Utils.logSql(content);
			status = executeUpdateSql(content, true, true);
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", filePath);
        } catch (IOException e) {
            Utils.showError(e, filePath);
		}
		return status;
		
	}
	
	
	public static boolean executeScript(String scriptPath, String batPath) {

		String aux;
		String bin, host, port, db, user;
		
		bin = gswProp.getProperty("POSTGIS_BIN", "");
		host = gswProp.getProperty("POSTGIS_HOST", "localhost");
		port = gswProp.getProperty("POSTGIS_PORT", "5431");
		db = gswProp.getProperty("POSTGIS_DATABASE", "giswater");
		user = gswProp.getProperty("POSTGIS_USER", "postgres");
		
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
		String bin, host, port, db, user, srid;
		
		fileSql = raster.replace(".asc", ".sql");
		bin = gswProp.getProperty("POSTGIS_BIN", "");
		host = gswProp.getProperty("POSTGIS_HOST", "localhost");
		port = gswProp.getProperty("POSTGIS_PORT", "5431");
		db = gswProp.getProperty("POSTGIS_DATABASE", "giswater");
		user = gswProp.getProperty("POSTGIS_USER", "postgres");
		srid = gswProp.get("SRID_USER");			
		logFolder = Utils.getLogFolder();
		
		File file = new File(bin);
		if (!file.exists()){
			Utils.showError("postgis_not_found", bin);
			return false;			
		}
		bin+= File.separator;
		
		// Check if mdt table already exists
		if (MainDao.checkTableHasData(schemaName, "mdt")){
			String msg = "MDT table already loaded. Do you want to overwrite it?";
			int res = Utils.confirmDialog(msg);
			if (res != 0){
				return false;
			}			
		}
		
		// Set content of .bat file
		aux = "\""+bin+"raster2pgsql\" -d -s "+srid+" -I -C -M \""+raster+"\" -F -t 100x100 "+schemaName+".mdt > \""+fileSql+"\"";
		aux+= "\n";
		aux+= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+" -c \"drop table if exists "+schemaName+".mdt\";";
		aux+= "\n";		
		aux+= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+" -f \""+fileSql+"\" > \""+logFolder+"raster2pgsql.log\"";
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