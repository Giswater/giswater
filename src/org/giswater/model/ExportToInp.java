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
package org.giswater.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.JOptionPane;

import org.giswater.dao.ConfigDao;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.util.Utils;


public class ExportToInp extends Model {

	private static final String PROJECT_TABLE = "v_inp_project_id";
	private static final String OPTIONS_TABLE = "v_inp_options";
	private static final String REPORTS_TABLE = "v_inp_report";
	private static final String REPORTS_TABLE2 = "inp_report";
	private static final String TIMES_TABLE = "v_inp_times";
	private static final String PATTERNS_TABLE = "inp_pattern";
	private static final String SELECTOR_SECTOR_TABLE = "inp_selector_sector";
	private static final Integer DEFAULT_SPACE = 23;
	private static final String TABLE_CAT_RESULT = "rpt_cat_result";	

	private static boolean isVersion51;
	

    public static boolean checkSectorSelection() {
		
		boolean result = false;
		String sql = "SELECT COUNT(*) FROM "+MainDao.getSchema()+"."+SELECTOR_SECTOR_TABLE;
		PreparedStatement ps;
		try {
			ps = MainDao.getConnectionPostgis().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = (rs.getInt(1) > 0);
			}
		} catch (SQLException e) {
			Utils.showError(e, sql);
		}
		return result;
		
	}    
    
    
    // Execute SQL function gw_fct_pg2epa()
    private static void executePg2Epa(String resultName, boolean onlyCheck) {
    	
    	if (MainDao.checkFunction(MainDao.getSchema(), "gw_fct_pg2epa")) {    	
	    	Utils.getLogger().info("Execution function 'gw_fct_pg2epa()'");
	    	String sql = "SELECT "+MainDao.getSchema()+".gw_fct_pg2epa('"+resultName+"', "+onlyCheck+");";
	    	String result = MainDao.queryToString(sql);		
	    	Utils.getLogger().info("Result function 'gw_fct_pg2epa()': "+result);
    	}
    	else {
	    	Utils.getLogger().info("Doesn't exist function 'gw_fct_pg2epa()'");    		
    	}
    	
    }
	
    
	private static boolean existsProjectName(String resultName) {
		
		String sql = "SELECT * FROM "+MainDao.getSchema()+"."+TABLE_CAT_RESULT+
			" WHERE result_id = '"+resultName+"'";
		try {
			PreparedStatement ps = MainDao.getConnectionPostgis().prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();
	        return rs.next();
		} catch (SQLException e) {
            Utils.showError(e, sql);
			return false;
		}
		
	}
	
	
    // Export to INP 
    public static boolean process(File fileInp, boolean isSubcatchmentSelected, boolean isVersion51, 
    	String resultName, boolean onlyCheck) {

        Utils.getLogger().info("exportINP");
        String sql = "";
        ExportToInp.isVersion51 = isVersion51;
        
       	// Check if we want to overwrite previous results
        Boolean overwriteResult = Boolean.parseBoolean(PropertiesDao.getPropertiesFile().get("OVERWRITE_RESULT", "false"));
        Utils.getLogger().info("OVERWRITE_RESULT: "+overwriteResult);
        
    	// Check if Project Name exists in rpt_result_id
    	if (existsProjectName(resultName)) {
            if (!overwriteResult) {
            	int res = Utils.showYesNoDialog("project_exists");    		
            	if (res == JOptionPane.NO_OPTION) return false;
            }
    	}
    	
        try {
            
            // Overwrite INP file if already exists?
            if (fileInp.exists()) {
                String owInp = PropertiesDao.getPropertiesFile().get("OVERWRITE_INP", "true").toLowerCase();
	            if (owInp.equals("false")) {
	                String msg = Utils.getBundleString("ExportToInp.file_already_exists") +
	                	fileInp.getAbsolutePath()+Utils.getBundleString("ExportToInp.overwrite");
	            	int res = Utils.showYesNoDialog(msg);             
	            	if (res == JOptionPane.NO_OPTION) return false;
	            }  
            }
            
            // Get INP template File
            String templatePath = ConfigDao.getInpFolder()+softwareVersion+".inp";
            File fileTemplate = new File(templatePath);
            if (!fileTemplate.exists()) {
            	Utils.showMessage("inp_error_notfound", fileTemplate.getAbsolutePath());
            	return false;
            }
            
            // Execute SQL function gw_fct_pg2epa('result_id') 
            executePg2Epa(resultName, onlyCheck);
                
            // Open template and output file
            rat = new RandomAccessFile(fileTemplate, "r");
            raf = new RandomAccessFile(fileInp, "rw");
            raf.setLength(0);

            // Get content of target table
            sql = "SELECT target.id as target_id, target.name as target_name, lines, "
            	+ "main.id as main_id, main.dbase_table as table_name "
        		+ "FROM inp_target as target " 
        		+ "INNER JOIN inp_table as main ON target.table_id = main.id";             
            Statement stat = connectionDrivers.createStatement();            
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
            	String msg = "INP target: "+rs.getInt("target_id")+" - "+rs.getString("table_name")+" - "+rs.getInt("lines");
            	Utils.getLogger().info(msg);
            	if (rs.getString("table_name").equals(OPTIONS_TABLE) || 
            		rs.getString("table_name").equals(REPORTS_TABLE) || rs.getString("table_name").equals(REPORTS_TABLE2) ||
            		rs.getString("table_name").equals(TIMES_TABLE)) {
            		processTarget2(rs.getInt("target_id"), rs.getString("table_name"), rs.getInt("lines"));
            	}
            	else if (rs.getString("table_name").equals(PROJECT_TABLE)) {
                		processTargetTitle(rs.getInt("target_id"), rs.getString("table_name"), rs.getInt("lines"), resultName);
                	}            	
            	else {
            		processTarget(rs.getInt("target_id"), rs.getString("table_name"), rs.getInt("lines"));
            	}
            }
            rs.close();
            
            // Subcatchment function
            if (isSubcatchmentSelected) {
            	sql = "SET search_path TO '"+MainDao.getSchema()+"', public";
            	MainDao.executeSql(sql);
            	Utils.getLogger().info("Process subcatchments");    	
                // Get content of target table
            	String functionName = "gw_fct_pg2epa_dump_subcatch";
            	if (!MainDao.checkFunction(MainDao.getSchema(), functionName)) {
            		functionName = "gw_fct_dump_subcatchments";
            	}
            	sql = "SELECT "+MainDao.getSchema()+"."+functionName+"();";            
            	rs = MainDao.getResultset(sql);
                while (rs.next()) {                	
                	raf.writeBytes(rs.getString(functionName));
                	raf.writeBytes("\r\n");
                }            	
            }
            
            rat.close();
            raf.close();

            // Open INP file?
            String openInp = PropertiesDao.getPropertiesFile().get("OPEN_INP").toLowerCase();
            if (openInp.equals("always")) {
            	Utils.openFile(fileInp.getAbsolutePath());
            }
            else if (openInp.equals("ask")) {
                String msg = Utils.getBundleString("inp_end") + "\n" + fileInp.getAbsolutePath() 
                	+ "\n" + Utils.getBundleString("view_file");
            	int res = Utils.showYesNoDialog(msg);             
            	if (res == JOptionPane.YES_OPTION) {
                   	Utils.openFile(fileInp.getAbsolutePath());
                }   
            }                            
            return true;

        } catch (IOException e) {
            Utils.showError(e);
            return false;
        } catch (SQLException e) {
            Utils.showError(e, sql);
            return false;
        }

    }


	// Process target specified by id parameter
    private static void processTarget(int id, String tableName, int lines) throws IOException, SQLException {

        // Go to the first line of the target
        for (int i = 1; i <= lines; i++) {
            String line = rat.readLine().trim();
            raf.writeBytes(line + "\r\n");
        }

        // If table is null or doesn't exit then exit function
        if (!MainDao.checkTable(MainDao.getSchema(), tableName) && !MainDao.checkView(MainDao.getSchema(), tableName)) {
        	Utils.getLogger().info("Table or view doesn't exist: " + tableName);
            return;
        }

        // Get data of the specified Postgis table
        lMapDades = getTableData(tableName);
        if (lMapDades.isEmpty()) {
        	Utils.getLogger().info("Table or view empty: " + tableName);
            return;
        }

        // Get table columns to write into this target
        mHeader = new LinkedHashMap<String, Integer>();
        String sql = "SELECT name, space" +
        	" FROM inp_target_fields" + 
        	" WHERE target_id = "+id+ 
        	" ORDER BY pos";
        Statement stat = connectionDrivers.createStatement();
        ResultSet rs = stat.executeQuery(sql);
        while (rs.next()) {
            mHeader.put(rs.getString("name").trim().toLowerCase(), rs.getInt("space"));
        }
        rs.close();

        ListIterator<LinkedHashMap<String, String>> itData = lMapDades.listIterator();
        LinkedHashMap<String, String> rowData;   // Current Postgis row data
        String sKey;
        int size, sizeId;
        Set<String> set = mHeader.keySet();
        Iterator<String> itKey = set.iterator();        
        
        if (tableName.equals(PATTERNS_TABLE)){
	        // Iterate over Postgis table
	        while (itData.hasNext()) {
	            rowData = itData.next();
	            itKey = set.iterator();
	            // First element: id
                String sKeyId = (String) itKey.next();
                sKeyId = sKeyId.toLowerCase();
                sizeId = mHeader.get(sKeyId);
	            parseField(rowData, sKeyId, sizeId);
	            // Every Postgis row fills 4 lines -> 6 factors per line	            
	            int i = 0;
	            while (itKey.hasNext()) {
	            	// Iterate over fields specified in table target_fields
	                sKey = (String) itKey.next();
	                sKey = sKey.toLowerCase();
	                size = mHeader.get(sKey);
		            parseField(rowData, sKey, size);
	            	i++;
	            	if (i%6 == 0 && i%24 != 0){
	    	            raf.writeBytes("\r\n");
	    	            parseField(rowData, sKeyId, sizeId);		    	            
	            	}
				}
	            raf.writeBytes("\r\n");
	        }
        }
        
        else {
	        // Iterate over Postgis table
	        while (itData.hasNext()) {
	            rowData = itData.next();
	            itKey = set.iterator();
	            // Iterate over fields specified in table target_fields
	            while (itKey.hasNext()) {
	                sKey = (String) itKey.next();
	                sKey = sKey.toLowerCase();
	                size = mHeader.get(sKey);
		            parseField(rowData, sKey, size);	
	            }
	            raf.writeBytes("\r\n");
	        }
        }
        
    }

    
    private static void parseField(LinkedHashMap<String, String> rowData, String sKey, int size) throws IOException {

        // Write to the output file if the field exists in Postgis table
        if (rowData.containsKey(sKey)) {
            String sValor = (String) rowData.get(sKey);
            raf.writeBytes(sValor);
            // Complete spaces with empty values
            for (int j = sValor.length(); j <= size; j++) {
                raf.writeBytes(" ");
            }
        } // If key doesn't exist write empty spaces
        else {
            for (int j = 0; j <= size; j++) {
                raf.writeBytes(" ");
            }
        }
        if (size == 0) {
        	raf.writeBytes(" ");
        }        
        
	}


	// Process target options or target report
    private static void processTarget2(int id, String tableName, int lines) throws IOException, SQLException {

        // Go to the first line of the target
        for (int i = 1; i <= lines; i++) {
            String line = rat.readLine().trim();
            raf.writeBytes(line + "\r\n");
        }

        // If table is null or doesn't exit then exit function
        if (!MainDao.checkTable(MainDao.getSchema(), tableName) && !MainDao.checkView(MainDao.getSchema(), tableName)) {
            return;
        }

        // Get data of the specified Postgis table
        ArrayList<LinkedHashMap<String, String>> options = getTableData(tableName);
        if (options.isEmpty()) {
        	Utils.getLogger().info("Empty table: " + tableName);        	
            return;
        }

        ListIterator<LinkedHashMap<String, String>> it = options.listIterator();
        LinkedHashMap<String, String> m;   // Current Postgis row data
        String sValor = null;
        int size = DEFAULT_SPACE;
        
        // Iterate over Postgis table (only one element)
        while (it.hasNext()) {
            m = it.next();
            Set<String> set = m.keySet();
            Iterator<String> itKey = set.iterator();
            // Iterate over fields and write 
            while (itKey.hasNext()) {
                // Write to the output file (one per row)
            	String sKey = (String) itKey.next();
            	// If we are working with EPASWMM version < 5.1, not process these fields
            	if ((!ExportToInp.isVersion51) && 
            		(sKey.equals("max_trials") || sKey.equals("head_tolerance") 
            		|| sKey.equals("sys_flow_tol") || sKey.equals("lat_flow_tol"))) {
            		// Ignore them	
            	}
            	else {
	                sValor = (String) m.get(sKey);
	                raf.writeBytes(sKey.toUpperCase());
	                // Complete spaces with empty values
	                for (int j = sKey.length(); j <= size; j++) {
	                    raf.writeBytes(" ");
	                }
	                raf.writeBytes(sValor);
	                raf.writeBytes("\r\n");   
            	}
            }
        }

    }    

    
	// Process target title
    private static void processTargetTitle(int id, String tableName, int lines, String resultName) 
    	throws IOException, SQLException {

        // Go to the first line of the target
        for (int i = 1; i <= lines; i++) {
            String line = rat.readLine().trim();
            raf.writeBytes(line + "\r\n");
        }

        // If table is null or doesn't exit then exit function
        if (!MainDao.checkTable(MainDao.getSchema(), tableName) && !MainDao.checkView(MainDao.getSchema(), tableName)) {
        	Utils.getLogger().info("Table or view doesn't exist: " + tableName);
            return;
        }

        // Get project title from database
        String sql = "SELECT title FROM "+MainDao.getSchema()+"."+tableName;
        String projectId = MainDao.queryToString(sql);
        raf.writeBytes(";Project name: "+projectId+"\r\n");
        raf.writeBytes(";Result name: "+resultName+"\r\n");

    }   
    
    
    // Read content of the table saved it in an Array
    private static ArrayList<LinkedHashMap<String, String>> getTableData(String tableName) {

    	LinkedHashMap<String, String> mDades;
        ArrayList<LinkedHashMap<String, String>> mAux = new ArrayList<LinkedHashMap<String, String>>();
        String sql = "SELECT * FROM "+MainDao.getSchema()+"."+tableName;
        try {
            ResultSet rs = MainDao.getResultset(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            // Get columns name
            int fields = rsmd.getColumnCount();
            String[] columns = new String[fields];
            for (int i = 0; i < fields; i++) {
                columns[i] = rsmd.getColumnName(i + 1);
            }
            String value;
            while (rs.next()) {
                mDades = new LinkedHashMap<String, String>();
                for (int i = 0; i < fields; i++) {
                    Object o = rs.getObject(i + 1);
                    if (o != null) {
                        value = o.toString();
                        mDades.put(columns[i], value);
                    }
                }
                mAux.add(mDades);
            }
            rs.close();
        } catch (SQLException e) {
        	Utils.showError(e, sql);
        }
        return mAux;

    }
    
    
}