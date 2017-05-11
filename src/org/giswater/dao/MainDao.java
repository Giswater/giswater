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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.giswater.gui.MainClass;
import org.giswater.util.Encryption;
import org.giswater.util.Utils;
import org.giswater.util.UtilsFTP;


public class MainDao {

	protected static String host;
	protected static String port;
	protected static String db;
	protected static String user;
	protected static String password;
	protected static Boolean useSsl;
	protected static String binFolder;
	protected static String giswaterUsersFolder;   // UsersFolder + ROOT_FOLDER
	
    private static Connection connectionPostgis;
	private static String waterSoftware;   // [EPASWMM | EPANET]
	private static String softwareAcronym;   // [ud | ws]
    private static String schema;   // Current selected schema
    private static boolean isConnected = false;
    private static String updatesFolder;   // appPath + "sql/updates"
	private static String giswaterVersion;
	private static String postgreVersion;
	private static String postgisVersion;
	private static String exeMode = "";   // If we are executing in a IDE we can set to "versionToIsLast" or "isLastToVersion"
	private static HashMap<String, Integer> schemaMap;   // <schemaName, schemaVersion>
	private static HashMap<String, Integer> updateMap;   // <folderPath, lastUpdateScript>
    
    private static final String FOLDER_NAME = "giswater" + File.separator;
	private static final String INIT_DB = "giswater_ddb";
	private static final String DEFAULT_DB = "postgres";
	private static final String INIT_GISWATER_DDB = "init_giswater_ddb.sql";	
	private static final String TABLE_EPANET = "inp_demand";		
	private static final String TABLE_EPASWMM = "inp_divider";		
	private static final Integer NUMBER_OF_ATTEMPTS = 2;		
	
	
	public static String getDb() {
		return db;
	}
	
	public static String getHost() {
		return host;
	}
	
	public static String getPort() {
		return port;
	}
	
	public static String getUser() {
		return user;
	}
	
	public static String getPassword() {
		return password;
	}
	
	public static void setConnectionParams(String host, String port, String db, String user, String password) {
		MainDao.host = host;
		MainDao.port = port;
		MainDao.db = db;
		MainDao.user = user;
		MainDao.password = password;
	}
	
	public static Connection getConnectionPostgis() {
		return connectionPostgis;
	}	

	public static String getWaterSoftware() {
		return waterSoftware;
	}	
	
	public static void setWaterSoftware(String param) {
		waterSoftware = param;
    	if (waterSoftware.toUpperCase().equals("EPANET")) {
    		softwareAcronym = "ws";
    	}
    	else if (waterSoftware.toUpperCase().equals("EPASWMM")) {
    		softwareAcronym = "ud";
    	}	
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

	public static String getGiswaterUsersFolder() {
		return giswaterUsersFolder;
	}	
	
	public static String getGiswaterVersion() {
		return giswaterVersion;
	}
	
	public static String getPostgreVersion() {
		return postgreVersion;
	}	
	
	public static String getPostgisVersion() {
		return postgisVersion;
	}	
	
	public static void setExeMode(String value) {
		exeMode = value;
	}	
	
	
    // Sets initial configuration files
    public static boolean configIni(String versionCode) {
    	
    	// Giswater version
    	giswaterVersion = versionCode;
    	
        // Set Giswater users folder path
        giswaterUsersFolder = System.getProperty("user.home") + File.separator + FOLDER_NAME;
        
        // Properties files configuration
        if (!PropertiesDao.configIni(giswaterUsersFolder)) {
        	return false;
        }
         	
        // Set Locale
        setLocale();     
    	
        // Get inp and updates folder
        String inpFolder = Utils.getAppPath()+"inp"+File.separator;
        ConfigDao.setInpFolder(inpFolder);
        updatesFolder = Utils.getAppPath()+"sql"+File.separator+"updates"+File.separator;
        Utils.logInfo("SQL updates folder: " +updatesFolder);
        if (exeMode.equals("versionToIsLast")) {
        	replaceVersionToIsLast();
        }
        else if (exeMode.equals("isLastToVersion")) {
        	replaceIsLastToVersion();
        }        

    	// Set Config DB connection
        if (!ConfigDao.setConnectionConfig()) {
        	return false;
        }
        
        // Check log folder size
        String aux = PropertiesDao.getPropertiesFile().get("LOG_FOLDER_SIZE", "10");
        try{
	        Double warningSize = Double.parseDouble(aux);
	        double size = FileUtils.sizeOfDirectory(new File(Utils.getLogFolder()));
	        double sizeMb = Math.round((size / 1048576) * 100.0) / 100.0;
	        if (sizeMb > warningSize) {
	            Utils.getLogger().info("Log folder size is: " + sizeMb + " Mb");         	
	        	String msg = Utils.getBundleString("MainDao.log_size") + sizeMb + Utils.getBundleString("MainDao.perform_maintenance"); //$NON-NLS-1$ //$NON-NLS-2$
	        	int answer = Utils.showYesNoDialog(msg);
		        if (answer == JOptionPane.YES_OPTION) {
		        	Utils.openFile(Utils.getLogFolder());
		        }
	        }
        }
        catch (NumberFormatException e) {
        	String msg = Utils.getBundleString("MainDao.log_size_invalid"); //$NON-NLS-1$
        	Utils.logError(msg);
        }
       
        return true;

    }
       
    
	private static void setLocale() {
		
		Locale locale = new Locale("en", "EN");
        String language = PropertiesDao.getPropertiesFile().get("LANGUAGE", "en").toLowerCase();
		if (language.equals("es")) {
			locale = new Locale("es", "ES");
		}
		else if (language.equals("ca")) {
			locale = new Locale("ca", "CA");
		}
		else if (language.equals("pt")) {
			locale = new Locale("pt", "PT");
		}
		else if (language.equals("pt_BR")) {
			locale = new Locale("pt", "BR");
		}	
		Utils.setLocale(locale);
		
	}

	
	public static boolean setBinFolder() {

		boolean result = false;
		String dbAdminPath = PropertiesDao.getPropertiesFile().get("FILE_DBADMIN", "");
		if (!dbAdminPath.equals("")) {
			File file = new File(dbAdminPath);
			binFolder = file.getParent();
			File folder = new File(binFolder);
			if (!folder.exists()) {
				// If path is relative, make it absolute and check it again
				if (!file.isAbsolute()) {
					Utils.getLogger().info("dbAdminFile path not exists: "+dbAdminPath);
					String absolutePath = giswaterUsersFolder + dbAdminPath;
					file = new File(absolutePath);
					binFolder = file.getParent() + File.separator;
					result = file.exists();
				}
				else {
					result = false;
				}
			} 
			else {
				binFolder = file.getParent() + File.separator;
				result = true;
			}
		} 
		
		return result;
		
	}
	
    
	protected static boolean getConnectionParameters() {
		
		// Get connection parameteres from properties file
		host = PropertiesDao.getGswProperties().get("POSTGIS_HOST", "127.0.0.1");		
		port = PropertiesDao.getGswProperties().get("POSTGIS_PORT", "5431");
		db = PropertiesDao.getGswProperties().get("POSTGIS_DATABASE", "postgres");
		user = PropertiesDao.getGswProperties().get("POSTGIS_USER", "postgres");
		password = PropertiesDao.getGswProperties().get("POSTGIS_PASSWORD");		
		password = Encryption.decrypt(password);
		password = (password == null) ? "" : password;
		useSsl = Boolean.parseBoolean(PropertiesDao.getGswProperties().get("POSTGIS_USESSL"));		
		
		return true;
		
	}
	
	
	private static boolean commonSteps() {
		
		if (isConnected) return true;
		
		// Get parameteres connection from properties file
		if (!getConnectionParameters()) return false;
		
		// Check parameters
		if (host.equals("") || port.equals("") || db.equals("") || user.equals("")) {
			Utils.getLogger().info("Connection not possible. Check parameters in properties file");
			return false;
		}
		Utils.getLogger().info("host:"+host+" - port:"+port+" - db:"+db+" - user:"+user+" - useSsl:"+useSsl);
		
		// Check if Internet is available
		if (!host.equals("localhost") && !host.equals("127.0.0.1")) {
			if (!UtilsFTP.isInternetReachable()) {
				Utils.showError(Utils.getBundleString("MainDao.internet_unavailable"));	 //$NON-NLS-1$
				return false;
			}		
		}
		
		int count = 0;
		do {
			count++;
			Utils.getLogger().info("Trying to connect: " + count);
			isConnected = setConnectionPostgis(host, port, db, user, password, useSsl, false);
		} while (!isConnected && count < NUMBER_OF_ATTEMPTS);
		
		// Try to connect to the default database if we couldn't connect previously
		if (!isConnected) {
			Utils.getLogger().info("Connection not possible. Trying to connect to 'postgres' database instead");
			db = DEFAULT_DB;
			count = 0;
			do {
				count++;
				Utils.getLogger().info("Trying to connect to default Database: " + count);
				isConnected = setConnectionPostgis(host, port, db, user, password, useSsl, false);
			} while (!isConnected && count < NUMBER_OF_ATTEMPTS);
		}

		if (isConnected) {
			// Get Postgis data
	    	String dataPath = MainDao.getDataDirectory();
	    	PropertiesDao.getGswProperties().put("POSTGIS_DATA", dataPath);
			Utils.getLogger().info("Connection successful");
	    	Utils.getLogger().info("Postgre data directory: " + dataPath);	
	    	checkPostgis();
		}
		
		return isConnected;
		
	}
	
	
	private static boolean checkPostgis() {
		
		// Check Postgre and Postgis versions
		postgreVersion = MainDao.checkPostgreVersion();	        
		postgisVersion = MainDao.checkPostgisVersion();	        
		Utils.getLogger().info("Postgre version: " + postgreVersion);
		if (postgisVersion.equals("")){
			// Enable Postgis to current Database
			String sql = "CREATE EXTENSION postgis; CREATE EXTENSION postgis_topology;";
			executeUpdateSql(sql, true, false);			  	
		}
		else {
			Utils.getLogger().info("Postgis version: " + postgisVersion);
		}
		return true;
		
	}
	
		
	public static boolean silenceConnection() {
		
		if (!isConnected) {
			commonSteps();
		}
		
		if (isConnected) {    	
			return checkPostgis();
		}

		Utils.getLogger().info("Autoconnection error");		
		return false;
		
	}	    
    
	
	public static boolean initializeDatabase() {
		
		if (isConnected) return true;
		
		commonSteps();
		if (isConnected) {
			if (db.equals(DEFAULT_DB)) {
				if (!MainDao.checkDatabase(INIT_DB)) {
					Utils.getLogger().info("Creating database... " + INIT_DB);
					if (!initDatabase()) {
						return false;
					}
					PropertiesDao.getGswProperties().put("POSTGIS_DATABASE", INIT_DB);
					// Close current connection in order to connect later to Database just created: giswater_ddb
					closeConnectionPostgis();	
					return true;
				} 
			} 
			return true;
		}
		
		Utils.getLogger().info("initializeDatabase: Autoconnection error");			
		return false;
		
	}	 	
    
	
	private static boolean initDatabase() {
		
		// Execute script that creates working Database
		String filePath = Utils.getAppPath() + "sql" + File.separator + INIT_GISWATER_DDB;
    	String content;
		try {
			content = Utils.readFile(filePath);
			executeUpdateSql(content, true, false);
		} catch (IOException e) {
			Utils.logError(e);
			return false;
		}
		return true;

	}
	
	
	
	public static void commit() {
		try {
			connectionPostgis.commit();
		} catch (SQLException e) {
            Utils.logError(e);
		}
	}	
	
	
	public static void rollback() {
		try {
			connectionPostgis.rollback();
		} catch (SQLException e) {
            Utils.logError(e);
		}
	}	

	
    public static boolean setConnectionPostgis(String host, String port, String db, String user, String password, boolean useSsl, boolean showError) {
    	
    	schemaMap = new HashMap<String, Integer>();
        String connectionString = "jdbc:postgresql://"+host+":"+port+"/"+db+"?user="+user+"&password="+password;
        if (useSsl) {
        	connectionString+= "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        }
        try {
            connectionPostgis = DriverManager.getConnection(connectionString);
			connectionPostgis.setAutoCommit(false);            
        } catch (SQLException e) {
            try {
                connectionPostgis = DriverManager.getConnection(connectionString);
				connectionPostgis.setAutoCommit(false);                
            } catch (SQLException e1) {
            	if (showError) {
            		Utils.showError(e1.getMessage());
            	} else {
            		Utils.logError(e1.getMessage());
            	}
                return false;
            }   		
        }
        return true;
        
    }	
    
    
    public static void closeConnectionPostgis(){
    	try {
    		isConnected = false;
    		if (connectionPostgis != null) {
    			connectionPostgis.close();
    		}
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
			Statement stmt = connectionPostgis.createStatement();
	        stmt.executeUpdate(sql);
	        if (commit && !connectionPostgis.getAutoCommit()){
	        	commit();
	        }
			return true;
		} catch (SQLException e) {
			if (showError){
				Utils.showError(e, sql);
			} else{
				Utils.logError(e, sql);
			}
			rollback();
			return false;
		}
		
	}	
	
	
	public static boolean executeSql(String sql) {
		return executeSql(sql, false, "");
	}	
	
	public static boolean executeSql(String sql, boolean commit) {
		return executeSql(sql, commit, "");
	}	
	
	public static boolean executeSql(String sql, boolean commit, String context) {
		
		try {
			Statement stmt = connectionPostgis.createStatement();
	        stmt.execute(sql);
			if (commit && !connectionPostgis.getAutoCommit()) {
	        	commit();
	        }			
			return true;
		} catch (SQLException e) {
			Utils.showSQLError(e, context);
			rollback();
			return false;
		}
		
	}		
	
	public static Exception executeSql(String sql, boolean commit, Integer dummy) {
		
		try {
			Statement stmt = connectionPostgis.createStatement();
	        stmt.execute(sql);
			if (commit && !connectionPostgis.getAutoCommit()) {
	        	commit();
	        }			
			return null;
		} catch (SQLException e) {
			rollback();
			return e;
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
    		ResultSet rs = getResultset(sql);
            if (rs.next()) {
            	return (rs.getInt(1) != 0);
            }
            return false;
        } catch (SQLException e) {
        	Utils.showError(e, sql);
            return false;
        }
        
    }	
	
	
	// Return true if query returns at least one record
	private static boolean checkQuery(String sql) {
		
		boolean check = false;
        try {
    		ResultSet rs = getResultset(sql);
            check = rs.next();
            rs.close();
        } catch (SQLException e) {
        	Utils.showError(e);
        	rollback();
        }		
        return check;
        
	}
	
	
	// Execute query and returns it as a String
	public static String queryToString(String sql) {
		return queryToString(sql, true);
	}
	
	public static String queryToString(String sql, boolean showError) {
		
    	String value = "";
        try {
    		ResultSet rs = getResultset(sql, showError);      	
    		if (rs != null) {
				if (rs.next()) {
					value = rs.getString(1);
				}
				rs.close();	
    		}
		} catch (SQLException e) {
        	Utils.logError(e.getMessage());
        	rollback();        	
		}
        return value;
        
	}	
	
	
    // Check if the table exists
	public static boolean checkTable(String tableName) {
        String sql = "SELECT * FROM pg_tables" +
        	" WHERE lower(tablename) = '" + tableName + "'";
        return checkQuery(sql);
    }	
	
	
    // Check if the table exists in the selected schema
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
    
    
    // Check if the view exists in the selected schema
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
    
    // Check if schema exists
    public static boolean checkSchema(String schemaName) {
    	String sql = "SELECT schema_name FROM information_schema.schemata WHERE schema_name = '"+schemaName+"'";
    	return checkQuery(sql);
    } 
    
    // Check if function exists
    public static boolean checkFunction(String schemaName, String functionName) {
    	String sql = "SELECT routine_name FROM information_schema.routines" +
    		" WHERE lower(routine_schema) = '"+schemaName+"' AND lower(routine_name) = '"+functionName+"'";
    	return checkQuery(sql);
    } 
    
    // Get PostgreSQL version
    public static String checkPostgreVersion() {
        String sql = "SELECT version()";
        return queryToString(sql);
    }
    
    // Get Postgis version
    public static String checkPostgisVersion() {
        String sql = "SELECT PostGIS_full_version()";
        return queryToString(sql, false);
    }
    
    
    private static boolean checkSoftwareSchema(String software, String schemaName) {
    	
    	String tableName = "";
    	software = software.toUpperCase().trim();
        if (software.equals("EPANET") || software.equals("WS")) {
        	tableName = TABLE_EPANET;
        }
        else if (software.equals("EPASWMM") || software.equals("EPA SWMM") || software.equals("UD")) {
        	tableName = TABLE_EPASWMM;
        }
        return checkTable(schemaName, tableName);

    }
    
	public static Vector<String> getSchemas() {
		return getSchemas("");
	}
    
	public static Vector<String> getSchemas(String software) {

        String sql = "SELECT DISTINCT(table_schema) " +
        	"FROM information_schema.tables " +
        	"WHERE table_schema <> 'information_schema' AND table_schema !~ E'^pg_' " +
        	"AND table_schema <> 'drivers' AND table_schema <> 'public' AND table_schema <> 'topology' " +
        	"ORDER BY table_schema";      
        Vector<String> vector = new Vector<String>();
        if (isConnected()) {
	        try {
	    		ResultSet rs = getResultset(sql);
	    		if (rs == null) return vector;
	            while (rs.next()) {
	            	String schemaName = rs.getString(1);
	            	if (!software.equals("")) {
	            		// Add current schema only if "belongs" to software we're working on
	            		if (checkSoftwareSchema(software, schemaName)) {
	            			vector.add(schemaName);
	            		}
	            	}
	            	else {
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
		return vector;
		
	}
	
	
	public static ResultSet getResultset(Connection connection, String sql, boolean showError, int type, int concurrency) {
		
        ResultSet rs = null;        
        try {
        	Statement stat = connection.createStatement(type, concurrency);
            rs = stat.executeQuery(sql);
        } catch (SQLException e) {
			if (showError) {
				Utils.showError(e, sql);
			} 
			else {
				Utils.logError(e, sql);
			}
        	rollback();			
        }
        return rs;   
        
	}
	
	public static ResultSet getResultset(Connection connection, String sql, boolean showError) {
		return getResultset(connection, sql, showError, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	}
	
	public static ResultSet getResultset(String sql, boolean showError) {
		return getResultset(connectionPostgis, sql, showError);
	}
	
	public static ResultSet getResultset(String sql) {
		return getResultset(connectionPostgis, sql, true);
	}
	
	public static ResultSet getTableResultset(Connection connection, String table, 
		String fields, String fieldOrderBy) {
		
		String sql;
		if (schema == null) {
			sql = "SELECT "+fields+" FROM "+table;
		} 
		else {
			sql = "SELECT "+fields+" FROM "+schema+"."+table;
		}
		if (fieldOrderBy != "") {
			sql+= " ORDER BY "+fieldOrderBy;
		}
        return getResultset(connection, sql, true);
        
	}
	
	
	public static ResultSet getTableResultset(String table, String fields, String fieldOrderBy) {
		return getTableResultset(connectionPostgis, table, fields, fieldOrderBy);
	}	
	
	public static ResultSet getTableResultset(String table, String fields) {
		return getTableResultset(connectionPostgis, table, fields, "");
	}
	
	public static ResultSet getTableResultset(String table) {
		return getTableResultset(connectionPostgis, table, "*", "");
	}
	
	
	public static ResultSet getRaingageResultset(String table) {
        String sql = "SELECT rg_id, form_type, intvl, scf, rgage_type, timser_id, fname, sta, units" +
        	" FROM "+schema+"." + table;
        return getResultset(sql);
	}


	public static Vector<String> getTable(String table, String schemaParam) {
		return getTable(table, schemaParam, false, "*");
	}
	
	public static Vector<String> getTable(String tableName, String schemaName, boolean addBlank, String fields) {
        
        Vector<String> vector = new Vector<String>();
        
        if (addBlank) {
        	vector.add("");
        }
		if (schemaName == null) {
			schemaName = schema;
		}
		if (!checkTable(schemaName, tableName)) {
			return vector;
		}
		String sql = "SELECT "+fields+" FROM "+schemaName+"."+tableName;
		try {
    		ResultSet rs = getResultset(sql);
	        while (rs.next()) {
	        	vector.add(rs.getString(1));
	        }
		} catch (SQLException e) {
            Utils.showError(e, sql);
        	rollback();            
		}            
		return vector;
		
	}	
	
	public static Vector<Vector<String>> queryToVector(String sql) {
        
		Vector<Vector<String>> vector_container = new Vector<Vector<String>>();
		try {
    		ResultSet rs = getResultset(sql);
    		ResultSetMetaData rsmd = rs.getMetaData();
	        while (rs.next()) {
	        	Vector<String> vector = new Vector<String>();
	        	for (int i = 1; i <= rsmd.getColumnCount(); i++) {
	        		vector.add(rs.getString(i));
	        	}
	        	vector_container.add(vector);
	        }
		} catch (SQLException e) {
            Utils.showError(e, sql);
        	rollback();            
		}            
		return vector_container;
		
	}	
	
	
	public static void setResultSelect(String schema, String table, String result) {
		String sql = "DELETE FROM "+schema+"."+table;
		sql+= " WHERE cur_user = current_user";
		executeUpdateSql(sql);
		sql = "INSERT INTO "+schema+"."+table+" (result_id, cur_user) VALUES ('"+result+"', current_user)";
		executeUpdateSql(sql, true);
	}
	

	public static void deleteSchema(String schemaName) {
		String sql = "DROP schema IF EXISTS "+schemaName+" CASCADE;";
		if (!executeUpdateSql(sql, true, true)) return;
		sql = "DROP schema IF EXISTS "+schemaName+"_audit CASCADE;";
		if (!executeUpdateSql(sql, true, true)) return;		
		sql = "DELETE FROM public.geometry_columns WHERE f_table_schema = '"+schemaName+"'";
		executeUpdateSql(sql, true, true);		
	}
	
	
	public static int getNumberOfRows(ResultSet rs) {
		
		if (rs == null) {
			return 0;
		}
	    try {
	    	if (rs.getType() == ResultSet.TYPE_FORWARD_ONLY) {
	    		System.out.println("FORWARD");
	    		return 0;
	    	}
	        rs.last();
	        return rs.getRow();
	    } catch (SQLException e) {
	    	Utils.logError(e);
	    } finally {
	        try {
	            rs.beforeFirst();
	        } catch (SQLException e) {
	            Utils.logError(e);
	        	rollback();	            
	        }
	    }
	    return 0;
	    
	}	
	
	
	public static String getDataDirectory() {
		
		String sql = "SELECT setting FROM pg_settings WHERE name = 'data_directory'";
		String folder = "";
		try {
    		ResultSet rs = getResultset(sql);
	        if (rs.next()) {
	        	folder = rs.getString(1);
	        }
		} catch (SQLException e) {
            Utils.showError(e, sql);
		}    		
		return folder;
		
	}
	
	
	public static void resetSchemaVersion() {
		schemaMap.remove(schema);
	}
	
	
	public static Integer getSchemaVersion() {
		
		Integer schemaVersion = -1;
		if (schemaMap.containsKey(schema)) {
			schemaVersion = schemaMap.get(schema);
		} 
		else {
			String sql = "SELECT giswater FROM "+schema+".version ORDER BY giswater DESC";
			if (checkTable(schema, "version")) {
				String aux = queryToString(sql);
				schemaVersion = Utils.parseInt(aux.replace(".", ""));				
			}
			schemaMap.put(schema, schemaVersion);
		}
		return schemaVersion;
		
	}
	
	
	public static boolean insertVersion(boolean commit) {
		
		String language = PropertiesDao.getPropertiesFile().get("LANGUAGE", "en").toLowerCase();
		String sridValue = MainDao.getSrid(schema);
		String sql = "INSERT INTO "+schema+".version (giswater, wsoftware, postgres, postgis, date, language, epsg)" +
			" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+softwareAcronym.toUpperCase()+"', '"+MainDao.getPostgreVersion()+"', '" +
			MainDao.getPostgisVersion()+"', now(), '"+language+"', "+sridValue+")";
        Utils.logInfo(sql);
		return MainDao.executeSql(sql, commit);	

	}	
	
	
	// Called when we apply or accept changes in Project Preferences form
	public static boolean checkSchemaVersion() {
		
		if (schema == null || schema.equals("")) return false;
		
		Integer schemaVersion = getSchemaVersion();
		Integer updateVersion = updateMap.get(softwareAcronym);
		Utils.getLogger().info("Project '"+schema+"' ("+schemaVersion+")");
		Utils.getLogger().info("Update version: ("+updateVersion+")");
		if (updateVersion == null || updateVersion == -1) return false;
		
		// Get project_update value from Properties file
		String projectUpd = PropertiesDao.getPropertiesFile().get("PROJECT_UPDATE", "ask");
		if (updateVersion > schemaVersion && !projectUpd.equals("never")) {
			if (projectUpd.equals("ask")) {
				String msg = Utils.getBundleString("MainDao.would_like_update")+schema+Utils.getBundleString("MainDao.current_version"); //$NON-NLS-1$ //$NON-NLS-2$
				msg+= Utils.getBundleString("MainDao.advisable_backup"); //$NON-NLS-1$
				msg+= "\nUpdate from project version "+schemaVersion+" to "+updateVersion;
				int answer = Utils.showYesNoDialog(msg, Utils.getBundleString("MainDao.update_project")); //$NON-NLS-1$
				if (answer == JOptionPane.NO_OPTION) {
					Utils.getLogger().info("User chose not to update");
					return false;
				}
			}
			if (schemaVersion == -1) {
				String sql = "CREATE TABLE IF NOT EXISTS "+schema+".version (" +
					" id SERIAL, giswater varchar(16), wsoftware varchar(16), postgres varchar(512)," +
					" postgis varchar(512),	date timestamp(6) DEFAULT now(), CONSTRAINT version_pkey PRIMARY KEY (id))";
				executeSql(sql, true);
			}
			if (updateSchema(schemaVersion)) {
				schemaMap.remove(schema);
				insertVersion(true);
				return true;
			}
			else {
				MainClass.mdi.showError(Utils.getBundleString("MainDao.project_not_updated")); //$NON-NLS-1$
			}
		}
		
		return false;
		
	}
	
	
	private static void replaceVersionToIsLast() {
		
		// Get last update script version from every software
		versionToIsLast("ws");
		versionToIsLast("ud");
		versionToIsLast("utils");
		versionToIsLast("i18n"+File.separator+"ca");
		versionToIsLast("i18n"+File.separator+"en");
		versionToIsLast("i18n"+File.separator+"es");
		versionToIsLast("i18n"+File.separator+"pt_br");		
		
	}
	
	
	private static void versionToIsLast(String folderPath) {
		
		String folder = updatesFolder+folderPath+File.separator;
		File[] files = new File(folder).listFiles();
		if (files != null && files.length > 0) {
			Arrays.sort(files);
			// Get only last file of selected folder
			String fileName = files[files.length-1].getName();
			String newFileName = fileName;
			boolean renameFile = false;
			// Replace @giswaterVersion.sql to is_last.sql
			Integer fileVersion = Utils.parseInt(giswaterVersion.replace(".", ""));
			if (fileName.equals(fileVersion+".sql")) {
				renameFile = true;
				newFileName = fileName.replace(fileVersion.toString(), "is_last");		
			}
			if (renameFile) {
				if (!Utils.renameFile(folder+fileName, folder+newFileName)) {
					Utils.logError("Rename could not be executed");
				}
			}
		}
		
	}	

		
	private static void replaceIsLastToVersion() {
		
		Utils.logInfo("Searching and replacing 'is_last.sql' files");
		// Get last update script version from every software
    	updateMap = new HashMap<String, Integer>();
    	isLastToVersion("ws");
    	isLastToVersion("ud");
    	isLastToVersion("utils");
    	isLastToVersion("i18n"+File.separator+"ca");
    	isLastToVersion("i18n"+File.separator+"en");
    	isLastToVersion("i18n"+File.separator+"es");
    	isLastToVersion("i18n"+File.separator+"pt_br");
		
	}
	
	
	private static void isLastToVersion(String folderPath) {
		
		String folder = updatesFolder+folderPath+File.separator;
		File[] files = new File(folder).listFiles();
		if (files != null && files.length > 0) {
			Arrays.sort(files);
			// Get only last file of selected folder: is_last.sql
			String fileName = files[files.length-1].getName();
			String newFileName = fileName;
			boolean renameFile = false;
			// Replace is_last.sql to @giswaterVersion.sql
			if (fileName.equals("is_last.sql")) {
				renameFile = true;
				newFileName = fileName.replace("is_last", giswaterVersion);		
			}
			newFileName = newFileName.replace(".", "").replace("sql", "");
			Integer fileVersion = Utils.parseInt(newFileName);
			updateMap.put(folderPath, fileVersion);
			if (renameFile) {
				if (!Utils.renameFile(folder+fileName, folder+newFileName+".sql")) {
					Utils.logError("Rename could not be executed");
				}
			}
		}
		else {
			updateMap.put(folderPath, -1);	
		}
		
	}
	
	
	public static boolean updateSchema(Integer schemaVersion) {
		
		String folderPath = "";
		
		// Process folder updates/<watersoftware>
		folderPath = updatesFolder+softwareAcronym+File.separator;
		if (!processFolder(folderPath, schemaVersion)) return false;
		
		// Process folder updates/utils		
		folderPath = updatesFolder+"utils"+File.separator;
		if (!processFolder(folderPath, schemaVersion)) return false;
		
		// Process folder updates/i18n/<locale>	
		String language = PropertiesDao.getPropertiesFile().get("LANGUAGE", "en").toLowerCase();
		folderPath = updatesFolder+"i18n"+File.separator+language+File.separator;
		if (!processFolder(folderPath, schemaVersion)) return false;		
		
		return true;
		
	}
	
	
	public static boolean processFolder(String folderPath, Integer schemaVersion) {
		
		// Iterate over all files inside selected folder
		File[] files = new File(folderPath).listFiles();
		if (files != null) {
			Arrays.sort(files);
			for (File file : files) {
				String fileName = file.getName().replace(waterSoftware.toLowerCase()+"_", "").replace(".sql", "");
				Integer fileVersion = Utils.parseInt(fileName);			
				if (fileVersion > schemaVersion) {
			    	String content;
					try {
						Utils.getLogger().info("Executing file: "+file.getAbsolutePath());
						content = Utils.readFile(file.getAbsolutePath());
						content = content.replace("SCHEMA_NAME", schema);
						content = content.replace("SRID_VALUE", getSrid(schema));					
						// Abort process if one script fails
						if (!executeSql(content, false)) return false;
					} catch (IOException e) {
						Utils.logError(e);
					}
				}
			}
		}
		
		return true;
		
	}
	

	public static String getSrid(String schemaName) {
		String table = "arc";
		String schemaSrid = MainDao.getTableSrid(schemaName, table).toString();
		return schemaSrid;
	}
	
	
	private static Integer getTableSrid(String schema, String table) {
		
		Integer srid = 0;
		String sql = "SELECT srid FROM public.geometry_columns"+
			" WHERE f_table_schema = '"+schema+"' AND f_table_name = '"+table+"'";
        try {
    		ResultSet rs = getResultset(sql);
            if (rs.next()) {
            	srid = rs.getInt(1);
            }
        } catch (SQLException e) {
        	Utils.showError(e, sql);
        }
        return srid;
            
	}
	
	
	public static String replaceExtentParameters(String schemaName, String content) {
		
		String aux = content;
		String tableName;
		String geomName;

		tableName = "node";
		geomName = "the_geom";
		String sql = "SELECT ST_XMax(gometries) AS xmax, ST_XMin(gometries) AS xmin," +
			" ST_YMax(gometries) AS ymax, ST_YMin(gometries) AS ymin" +
			" FROM (SELECT ST_Collect("+geomName+") AS gometries FROM "+schemaName+"."+tableName+") AS foo";
        try {
    		ResultSet rs = getResultset(sql);
            if (rs.next()) {
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


}