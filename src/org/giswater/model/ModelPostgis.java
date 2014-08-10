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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.giswater.dao.MainDao;
import org.giswater.util.Utils;


public class ModelPostgis extends Model {

    private static String insertSql;
	private static ArrayList<String> tokens;
	private static ArrayList<ArrayList<String>> tokensList;	
	private static File fileRpt;
	private static String projectName;
	private static int lineNumber;   // Number of lines read or current line to process
	private static int totalLines;   // Total number of lines inside .rpt file
	private static ArrayList<String> pollutants;
	private static ArrayList<String> fileContent;
	
	private static String firstLine;
	private static String lastTimeHydraulic = "";
	private static boolean continueProcess = true;
	private static boolean abortRptProcess = false;
	
	private static final String OPTIONS_TABLE = "v_inp_options";
	private static final String REPORTS_TABLE = "v_inp_report";
	private static final String REPORTS_TABLE2 = "inp_report";
	private static final String TIMES_TABLE = "v_inp_times";
	private static final String PATTERNS_TABLE = "inp_pattern";
	private static final Integer DEFAULT_SPACE = 23;


    // Read content of the table saved it in an Array
    private static ArrayList<LinkedHashMap<String, String>> getTableData(String tableName) {

    	LinkedHashMap<String, String> mDades;
        ArrayList<LinkedHashMap<String, String>> mAux = new ArrayList<LinkedHashMap<String, String>>();
        String sql = "SELECT * FROM " + MainDao.getSchema() + "." +  tableName;
        PreparedStatement ps;
        try {
            ps = MainDao.getConnectionPostgis().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
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


    
	public static boolean checkSectorSelection() {
		
		boolean result = false;
		String sql = "SELECT COUNT(*) FROM "+MainDao.getSchema()+".sector_selection";
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
	
	
    // Main procedure
    public static boolean processAll(File fileInp) {
    	return processAll(fileInp, false);
    }
    
    public static boolean processAll(File fileInp, boolean isSubcatchmentSelected) {

        Utils.getLogger().info("exportINP");

		iniProperties = MainDao.getPropertiesFile();         
    	String sql = "";
   	
        try {
        	
            // If not set, get default INP output file
            if (fileInp == null) {
                String sFile = iniProperties.get("DEFAULT_INP");
                sFile = MainDao.getInpFolder() + sFile;
                fileInp = new File(sFile);
            }
            
            // Overwrite INP file if already exists?
            if (fileInp.exists()){
                String owInp = MainDao.getPropertiesFile().get("OVERWRITE_INP", "true").toLowerCase();
	            if (owInp.equals("false")) {
	                String msg = "Selected file already exists:\n"+fileInp.getAbsolutePath()+"\nDo you want to overwrite it?";
	            	int res = Utils.confirmDialog(msg);             
	            	if (res == JOptionPane.NO_OPTION){
	                   	return false;
	                }   
	            }  
            }
            
            // Get INP template File
            String templatePath = MainDao.getInpFolder() + softwareVersion + ".inp";
            File fileTemplate = new File(templatePath);
            if (!fileTemplate.exists()) {
            	Utils.showMessage("inp_error_notfound", fileTemplate.getAbsolutePath());
            	return false;
            }
                
            // Open template and output file
            rat = new RandomAccessFile(fileTemplate, "r");
            raf = new RandomAccessFile(fileInp, "rw");
            raf.setLength(0);

            // Get content of target table
            sql = "SELECT target.id as target_id, target.name as target_name, lines, main.id as main_id, main.dbase_table as table_name "
        		+ "FROM inp_target as target " 
        		+ "INNER JOIN inp_table as main ON target.table_id = main.id";             
            Statement stat = connectionDrivers.createStatement();            
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
            	Utils.getLogger().info("INP target: " + rs.getInt("target_id") + " - " + rs.getString("table_name") + " - " + rs.getInt("lines"));
            	if (rs.getString("table_name").equals(OPTIONS_TABLE) || 
            		rs.getString("table_name").equals(REPORTS_TABLE) || rs.getString("table_name").equals(REPORTS_TABLE2) ||
            		rs.getString("table_name").equals(TIMES_TABLE)){
            		processTarget2(rs.getInt("target_id"), rs.getString("table_name"), rs.getInt("lines"));
            	}
            	else{
            		processTarget(rs.getInt("target_id"), rs.getString("table_name"), rs.getInt("lines"));
            	}
            }
            rs.close();
            
            // Subcatchment function
            if (isSubcatchmentSelected){
            	Utils.getLogger().info("Process subcatchments");
            	
            	stat = MainDao.getConnectionPostgis().createStatement();
            	
                // Get content of target table
            	sql = "SELECT " + MainDao.getSchema() + ".gw_dump_subcatchments();";            
                rs = stat.executeQuery(sql);
                
                while (rs.next()) {                	
                	raf.writeBytes(rs.getString("gw_dump_subcatchments"));
                	raf.writeBytes("\r\n");
                }            	
         
            }
            
            rat.close();
            raf.close();

            // Open INP file?
            String openInp = MainDao.getPropertiesFile().get("OPEN_INP").toLowerCase();
            if (openInp.equals("always")){
            	Utils.openFile(fileInp.getAbsolutePath());
            }
            else if (openInp.equals("ask")){
                String msg = Utils.getBundleString("inp_end") + "\n" + fileInp.getAbsolutePath() + "\n" + Utils.getBundleString("view_file");
            	int res = Utils.confirmDialog(msg);             
            	if (res == JOptionPane.YES_OPTION){
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
        String sql = "SELECT name, space FROM inp_target_fields" + 
        	" WHERE target_id = " + id + " ORDER BY pos";
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
        
        else{
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
        if (size == 0){
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
        if (!MainDao.checkTable(tableName) && !MainDao.checkView(tableName)) {
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

    
    // Exec SWMM
    public static boolean execEPASOFT(File fileInp, File fileRpt) {

        Utils.getLogger().info("execEPASOFT");
        
		iniProperties = MainDao.getPropertiesFile(); 
		String exeCmd = iniProperties.get("DEFAULT_FILE_" + softwareName, "");
        File exeFile = new File(exeCmd);
        
		// If file doesn't exists, append application path (path was relative)
		if (!exeFile.exists()){
			exeCmd = Utils.getAppPath() + exeCmd;	
	        exeFile = new File(exeCmd);			
		}
        
        // Check anyway if exists
		if (!exeFile.exists()){
			String msg = Utils.getBundleString("software_not_found") + " " + exeCmd + "\n" + 
				Utils.getBundleString("software_path");
			exeCmd = JOptionPane.showInputDialog(null, msg);
            exeFile = new File(exeCmd);
    		if (!exeFile.exists()){            
    			Utils.showError("inp_error_notfound", exeCmd);
    			return false;
    		}
    		iniProperties.put("DEFAULT_FILE_" + softwareName, exeCmd);
    		MainDao.savePropertiesFile();
		}

        if (!fileInp.exists()){
			Utils.showError("inp_error_notfound", fileInp.getAbsolutePath());     
			return false;
        }
        
        // Overwrite RPT file if already exists?
        if (fileRpt.exists()){
            String owRpt = MainDao.getPropertiesFile().get("OVERWRITE_RPT", "true").toLowerCase();
            if (owRpt.equals("false")){
                String msg = "Selected file already exists:\n"+fileRpt.getAbsolutePath()+"\nDo you want to overwrite it?";
            	int res = Utils.confirmDialog(msg);             
            	if (res == JOptionPane.NO_OPTION){
                   	return false;
                }   
            }  
        }
        
        String sFile = fileRpt.getAbsolutePath().replace(".rpt", ".out");
        File fileOut = new File(sFile);

        // Create command
        exeCmd = "\"" + exeCmd + "\"";
        exeCmd += " \"" + fileInp.getAbsolutePath() + "\" \"" + fileRpt.getAbsolutePath() + "\" \"" + fileOut.getAbsolutePath() + "\"";

        // Ending message
        Utils.getLogger().info(exeCmd);            

        // Exec process
		try {
			Process p = Runtime.getRuntime().exec(exeCmd);
	        p.waitFor();			
	        p.destroy();
		} catch (IOException e) {
			Utils.showError("inp_error_io", exeCmd);
			return false;
		} catch (InterruptedException e) {
			Utils.showError("inp_error_io", exeCmd);
			return false;
		}

        // Open RPT file
        String openFile = MainDao.getPropertiesFile().get("OPEN_RPT").toLowerCase();
        if (openFile.equals("always")){
        	Utils.openFile(fileRpt.getAbsolutePath());
        }
        else if (openFile.equals("ask")) {    
            String msg = Utils.getBundleString("inp_end") + "\n" + fileRpt.getAbsolutePath() + "\n" + Utils.getBundleString("view_file");
        	int res = Utils.confirmDialog(msg);             
        	if (res == JOptionPane.YES_OPTION) {
               	Utils.openFile(fileRpt.getAbsolutePath());
            }   
        }                            
        return true;

    }


    // Import RPT file into Postgis tables
    public static boolean importRpt(File fileRpt, String projectName) {
        
    	Utils.getLogger().info("importRpt");

    	abortRptProcess = false;
		iniProperties = MainDao.getPropertiesFile();   
    	ModelPostgis.fileRpt = fileRpt;
    	ModelPostgis.projectName = projectName;

    	// Ask confirmation to user
        String importRpt = MainDao.getPropertiesFile().get("AUTO_IMPORT_RPT", "false").toLowerCase();
        if (importRpt.equals("false")) {
           	int res = Utils.confirmDialog("import_sure");    		
           	if (res == JOptionPane.NO_OPTION) {
           		return false;
        	}    	
        }  

       	// Check if we want to overwrite previous results
        Boolean overwrite = Boolean.parseBoolean(iniProperties.get("IMPORT_OVERWRITE", "false"));
        Utils.getLogger().info("IMPORT_OVERWRITE: " + overwrite);
        
    	// Check if Project Name exists in rpt_result_id
    	boolean exists = false;
    	if (existsProjectName()) {
    		exists = true;
            if (!overwrite){
            	int res = Utils.confirmDialog("project_exists");    		
            	if (res == JOptionPane.NO_OPTION) {
            		return false;
            	}
            }
    	}
    	
		// Open RPT file
    	if (!openRptFile()) {
    		return false;
    	}
    	Utils.getLogger().info("Getting contents of .rpt file...");
    	if (!getRptContent()) {
    		return false;
    	}
    	Utils.getLogger().info("Getting contents completed");
    	
        // Get info from rpt_target into memory
        TreeMap<Integer, RptTarget> targets = new TreeMap<Integer, RptTarget>();
        String sql = "SELECT id, db_table, description, type, title_lines, tokens, dbf_table" +
        	" FROM rpt_target" +
        	" WHERE type <> 0 ORDER BY id";
        try {
            Statement stat = connectionDrivers.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                RptTarget rpt = new RptTarget(rs);
                targets.put(rpt.getId(), rpt);
            }
            rs.close();
            MainDao.getConnectionPostgis().setAutoCommit(false);       
        } catch (SQLException e) {
            Utils.showError(e, sql);
            return false;
        }
        
        // Iterate over targets
        Iterator<Entry<Integer, RptTarget>> it = targets.entrySet().iterator();
        while (it.hasNext()) {
        	
        	insertSql = "";
            Map.Entry<Integer, RptTarget> mapEntry = it.next();
            RptTarget rpt = mapEntry.getValue();
            boolean ok = false;
            boolean processTarget = true;
            boolean continueTarget;
            if (softwareName.equals("SWMM")) {
            	ok = processRpt(rpt);
				if (abortRptProcess) {
					return false;
				}
            } 
            else {
            	// Target node or arc
        		if (rpt.getId() >= 40) {
        			processTarget = false;
        			continueTarget = true;
        			if (overwrite) {
        				sql = "DELETE FROM "+MainDao.getSchema()+"."+rpt.getTable();
        				Utils.logSql(sql);
        				MainDao.executeUpdateSql(sql);
        			}
        			else {
        				if (exists) {
        					sql = "DELETE FROM "+MainDao.getSchema()+"."+rpt.getTable() + 
        						" WHERE result_id = '"+projectName+"'";
        					Utils.logSql(sql);
        					MainDao.executeUpdateSql(sql);
        				}
		    		}            			
        			while (continueTarget) {
            			insertSql = "";            				
        				ok = processRptEpanet(rpt);
        				if (abortRptProcess) {
        					return false;
        				}
        	        	if (ok) {
        		    		if (!insertSql.equals("")) {
        		            	if (softwareName.equals("EPANET") && rpt.getId() >= 40) {
        		            		firstLine = firstLine.substring(15, 24).trim(); 
        		            		sql = "UPDATE "+MainDao.getSchema()+"."+rpt.getTable() + 
        		            			" SET time = '"+firstLine+"' WHERE time is null;";
        		            		insertSql+= sql;
        		            	}          	
        		            	Utils.logSql(insertSql);
        			    		if (!MainDao.executeUpdateSql(insertSql)) {
        							return false;
        						}
        		    		}
        	        	}
        				continueTarget = (lineNumber > 0);            	        	
        			}
        			MainDao.commit();
        		}
        		// Target different than node or arc
        		else {
    				ok = processRptEpanet(rpt);
    				if (abortRptProcess) {
    					return false;
    				}    				
        		}
            }
            
            if (!continueProcess) {
            	return false;
            }
            
        	if (ok && processTarget) {
    			if (overwrite) {
    				sql = "DELETE FROM "+MainDao.getSchema()+"."+rpt.getTable();
    				Utils.logSql(sql);
    				MainDao.executeUpdateSql(sql);
    			}
    			else {
    				if (exists) {
    					sql = "DELETE FROM "+MainDao.getSchema()+"."+rpt.getTable() + 
    						" WHERE result_id = '"+projectName+"'";
    					Utils.logSql(sql);
    					MainDao.executeUpdateSql(sql);
    				}
	    		} 
	    		if (!insertSql.equals("")) {
	    			Utils.logSql(insertSql);	            	
		    		boolean status = MainDao.executeUpdateSql(insertSql, true);
		    		if (!status) {
		    			// TODO: i18n
		    			String msg = "Import aborted. Some data values are not valid in current target: ";
		    			msg+= "\n" + rpt.getDescription();
		    			msg+= "\nOpen current .log file for more details";
		    			Utils.showError(msg);
		    			return false;
		    		}
	    		}
        	} 
        	else {
        		Utils.getLogger().info("Target not found: " + rpt.getId() + " - " + rpt.getDescription());
        		if (softwareName.equals("EPANET") && rpt.getId() == 10) {
        			sql = "INSERT INTO "+MainDao.getSchema()+"."+rpt.getTable()+ " (result_id) VALUES ('"+projectName+"')";
        			MainDao.executeUpdateSql(sql, true);
        		}
        	}
        	
        } // end iterations over targets (while)

        // Insert into result_selection commiting transaction
   		MainDao.setResultSelect(MainDao.getSchema(), "result_selection", projectName);
        
        // Ending message
        Utils.showMessage("import_end");                

		return true;
		
    }


	private static boolean openRptFile() {
		
		Boolean ok = true;
		try {
			rat = new RandomAccessFile(fileRpt, "r");
			lineNumber = 0;
		} catch (FileNotFoundException e) {
			Utils.showError("inp_error_notfound", fileRpt.getAbsolutePath());
			ok = false;
		}	
		return ok;
		
	}

	
	private static boolean getRptContent() {
		
		fileContent = new ArrayList<String>();
		Boolean ok = true;
		String line;
		try {
			long fileLength = rat.length();
			lineNumber = 0;
			while (rat.getFilePointer() < fileLength) {
				lineNumber++;				
				line = rat.readLine().trim();	
				fileContent.add(line);
			}
			totalLines = lineNumber;
			lineNumber = 0;
		} catch (IOException e) {
			Utils.showError(e);
			ok = false;
		}	
		return ok;
		
	}


	private static boolean existsProjectName() {
		
		String sql = "SELECT * FROM "+MainDao.getSchema()+".rpt_result_cat " +
			" WHERE result_id = '"+projectName+"'";
		try {
			PreparedStatement ps = MainDao.getConnectionPostgis().prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();
	        return rs.next();
		} catch (SQLException e) {
            Utils.showError(e, sql);
			return false;
		}
		
	}

		
	private static boolean processRpt(RptTarget rpt) {

		// Read lines until rpt.getDescription() is found		
		boolean found = false;
		String line;
		String aux;
		
		Utils.getLogger().info("Target: " + rpt.getId() + " - " + rpt.getDescription());
		
		// Read lines until rpt.getDescription() is found		
		while (!found){
			if (lineNumber >= totalLines) {
				lineNumber = 0;
				return false;
			}
			lineNumber++;				
			line = readLine();
			if (line.length() >= rpt.getDescription().length()) {
				aux = line.substring(0, rpt.getDescription().length()).toLowerCase();
				if (aux.equals(rpt.getDescription().toLowerCase())) {
					found = true;
					Utils.getLogger().info("Target line number: " + lineNumber);						
				}
			}
		}
		
        if (rpt.getType() == 3 || rpt.getType() == 5 || rpt.getType() == 6) {
        	getPollutants(rpt);
        }
		
		// Jump number of lines specified in rpt.getTitleLines()
		for (int i = 1; i <= rpt.getTitleLines(); i++) {
			lineNumber++;
			line = readLine();
			// Check if we have reached next Target
			if (line.contains("No ")) {
				return false;
			}
		}		
		
		// Get Database fields related to this Target
		if (rpt.getColumnCount() == 0) {
			String sql = "SELECT * FROM "+MainDao.getSchema()+"."+rpt.getTable();
	        PreparedStatement ps;
			try {
				ps = MainDao.getConnectionPostgis().prepareStatement(sql);
		        ResultSet rs = ps.executeQuery();
		        ResultSetMetaData rsmd = rs.getMetaData();	
		        for (int i=1; i <= rsmd.getColumnCount(); i++) {
		        	rpt.addColumnName(rsmd.getColumnName(i));
		        	rpt.addColumnType(rsmd.getColumnType(i));
		        }
		        rs.close();
			} catch (SQLException e) {
				Utils.showError(e, sql);
			}
		}
		
		// Read following lines until blank line is found
		tokensList = new ArrayList<ArrayList<String>>();		
		continueProcess = parseLines(rpt);
		if (rpt.getType() == 2 && rpt.getId() != 10) {
			processTokens(rpt);
		}
		else if (rpt.getType() == 2 && rpt.getId() == 10) {
			processTokensAnalysis(rpt);
		}
		else if (rpt.getType() == 3) {
			processTokens3(rpt);
		}
		
		return true;
		
	}	
	

	private static void getPollutants(RptTarget rpt) {
		
		String line = "";
		int jumpLines;
		if (rpt.getType() == 3) {			
			lineNumber--;
			line = readLine();		
		} 
		else{
			jumpLines = (rpt.getType() == 5) ? 4 : 5;
			for (int i = 1; i <= jumpLines; i++) {		
				lineNumber++;					
				line = readLine();
			}
		}
		
		boolean blankLine = (line.length() == 0);
		if (!blankLine){
			Scanner scanner = new Scanner(line);
			if (rpt.getType() == 3 || rpt.getType() == 6) {
				int jumpScanner	= (rpt.getType() == 3) ? 1 : 4;
				for (int i = 0; i < jumpScanner; i++) {
					scanner.next();						
				}
			}
			// Get pollutant name
			parseLine1(scanner, false);		
			pollutants = new ArrayList<String>();	
			for (int i = 0; i < tokens.size(); i++) {
				pollutants.add(tokens.get(i));
			}
		}		
		
	}
	
	
	// Parse all lines
	private static boolean parseLines(RptTarget rpt) {
		
		boolean result = true;
		tokens = new ArrayList<String>();			
		boolean blankLine = false;		
		while (!blankLine) {
			lineNumber++;
			String line = readLine();
			blankLine = (line.length() == 0);
			if (!blankLine){
				Scanner scanner = new Scanner(line);
				if (rpt.getType() == 1) {
					parseLine1(scanner, false);
					result = processTokens(rpt);
					if (!result){
						return false;
					}
				}		
				else if (rpt.getType() == 2) {					
					parseLine2(scanner, rpt, true);
				}
				else if (rpt.getType() == 3) {	
					tokens = new ArrayList<String>();
					parseLine2(scanner, rpt, false);
					tokensList.add(tokens);
				}					
				else if (rpt.getType() == 4) {					
					parseLine1(scanner, true);
					processTokens(rpt);							
				}	
				else if (rpt.getType() == 5) {					
					tokens = new ArrayList<String>();
					parseLine1(scanner, false);
					processTokens5(rpt);						
				}				
				else if (rpt.getType() == 6) {					
					tokens = new ArrayList<String>();
					parseLine1(scanner, false);
					processTokens6(rpt);						
				}			
				else if (rpt.getType() == 7) {					
					tokens = new ArrayList<String>();
					parseLine1(scanner, false);
					processTokens6(rpt);						
				}							
			}
			if (abortRptProcess) {
				return false;
			}
		}		
		
		return result;
		
	}
	

	// Parse values of current line
	private static void parseLine1(Scanner scanner, boolean together) {
		
		tokens = new ArrayList<String>();	
		String token = "";
		while (scanner.hasNext()) {
			if (together){
				token += " " + scanner.next();
			}
			else{
				token = scanner.next();
				tokens.add(token);
			}
		}
		if (together){
			tokens.add(token.trim());
		}
		
	}	
	
	
	// Parse values of current line that contains ".." in it
	private static void parseLine2(Scanner scanner, RptTarget rpt, boolean together) {
		
		String token;
		boolean valid = false;
		String aux = "";
		int numTokens = 0;
		while (scanner.hasNext()) {
			token = scanner.next();
			if (valid == true){
				numTokens++;
				if (numTokens <= rpt.getTokens()) {
					if (together){
						aux += token + " ";
					}
					else{
						tokens.add(token);						
					}
				}
			}
			if (token.contains("..")) {
				valid = true;
			}
		}
		if (valid == true && together) {
			tokens.add(aux.trim());			
		}
		
	}
	
	
	private static boolean processTokens(RptTarget rpt) {

		String fields = "result_id, ";
		String values = "'"+projectName+"', ";
		if (softwareName.equals("EPANET")) {
			if (tokens.size() < 2){
				Utils.logError("Line not valid");
				return true;
			}
		}
		int j;
		for (int i=0; i<tokens.size(); i++) {
			j = i + 3;
			switch (rpt.getColumnType(j)) {
			case Types.NUMERIC:
			case Types.DOUBLE:
			case Types.INTEGER:
				boolean ok = Utils.isNumeric(tokens.get(i));
				if (!ok) {
					// TODO: i18n
					String msg = "An error ocurred in line number: "+lineNumber;
					msg+= "\nField "+rpt.getColumnName(j)+ " does not contain a valid numeric value: "+tokens.get(i);
					msg+= "\nImport process will be aborted";
					Utils.showError(msg);
					abortRptProcess = true;
					return false;
				}
				values += tokens.get(i) + ", ";
				break;					
			case Types.VARCHAR:
				values += "'" + tokens.get(i) + "', ";
				break;					
			default:
				values += "'" + tokens.get(i) + "', ";
				break;
			}
			fields += rpt.getColumnName(j) + ", ";
		}
	
		fields = fields.substring(0, fields.length() - 2);
		values = values.substring(0, values.length() - 2);
		String sql = "INSERT INTO "+MainDao.getSchema()+"."+rpt.getTable()+" ("+fields+") VALUES ("+values+");\n";
		insertSql += sql;
		
		return true;
		
	}
	
	
	private static void processTokens3(RptTarget rpt) {

		// Iterate over pollutants
		for (int i=0; i<pollutants.size(); i++) {
			String fields = "result_id, poll_id, ";
			String values = "'"+projectName+"', '"+pollutants.get(i)+"', ";
			// Iterate over fields
			for (int j=0; j<tokensList.size(); j++) {
				String value = tokensList.get(j).get(i);
	        	switch (rpt.getColumnType(j + 4)) {
				case Types.NUMERIC:
				case Types.DOUBLE:
				case Types.INTEGER:
					values += value + ", ";
					break;					
				case Types.VARCHAR:
					values += "'" + value + "', ";
					break;					
				default:
					values += "'" + value + "', ";
					break;
				}
				fields += rpt.getColumnName(j + 4) + ", ";        	
			}
			fields = fields.substring(0, fields.length() - 2);
			values = values.substring(0, values.length() - 2);
			String sql = "INSERT INTO "+MainDao.getSchema()+"."+rpt.getTable()+" ("+fields+") VALUES ("+values+");\n";
			insertSql += sql;				
        }
		
	}		
		
	
	private static void processTokens5(RptTarget rpt) {

		String fields = "result_id, subc_id, poll_id, value";
		String fixedValues = "'"+projectName+"', '"+tokens.get(0)+ "', ";
		String sql;
		String values;
		Double units;
		
		// No permetre tipus String (peta a Subcatchment)
		try {
			Integer.parseInt(tokens.get(0));
		} catch (NumberFormatException e){
			return;
		}
		
		// Iterate over pollutants
		if (tokens.size() > pollutants.size()) {
			for (int i=0; i<pollutants.size(); i++) {
				units = Double.valueOf(tokens.get(i + 1));
				values = fixedValues + "'"+pollutants.get(i)+"', "+units;
				sql = "INSERT INTO "+MainDao.getSchema()+"."+rpt.getTable()+" ("+fields+") VALUES ("+values+");\n";
				insertSql += sql;		        
			}
		}
		
	}	

	
	private static void processTokens6(RptTarget rpt) {
		
		String fields = "result_id, node_id, flow_freq, avg_flow, max_flow, total_vol";
		String fields2 = "result_id, node_id, poll_id, value";		
		String fixedValues = "'"+projectName+"', '"+tokens.get(0)+ "', ";
		String values;
		String sql;
		Double units;
	
		// If found separator o resume line skip them
		if (tokens.size() < 5 || tokens.get(0).equals("System")) {
			return;
		}
		
		// Iterate over first 5 fields
		values = fixedValues;
		for (int j = 1; j < 5; j++) {
			values += tokens.get(j) + ", ";
		}
		values = values.substring(0, values.length() - 2);		
		sql = "INSERT INTO "+MainDao.getSchema()+"."+rpt.getTable()+" ("+fields+") VALUES ("+values+");\n";
		insertSql += sql;				
		
		// Iterate over pollutants
		for (int i = 0; i < pollutants.size(); i++) {
			int j = i + 5;
			units = Double.valueOf(tokens.get(j));
			values = fixedValues + "'"+pollutants.get(i)+ "', "+units;
			sql = "DELETE FROM "+MainDao.getSchema()+".rpt_outfallload_sum " +
				"WHERE result_id = '"+projectName+"' AND node_id = '"+tokens.get(0)+"' AND poll_id = '"+pollutants.get(i)+ "';\n";
			insertSql += sql;	
			sql = "INSERT INTO "+MainDao.getSchema()+".rpt_outfallload_sum ("+fields2+") VALUES ("+values+");\n";
			insertSql += sql;		        
		}
		
	}
	
	
	private static boolean processTokensAnalysis(RptTarget rpt) {

		final int NORMAL_NUM_FIELDS = 16;
		final String[] fieldArray = new String[] {"infil_m", "wet_tstep", "dry_tstep"};
		final Set<String> fieldList = new HashSet<String>(Arrays.asList(fieldArray));
		
		String fields = "result_id, ";
		String values = "'"+projectName+"', ";
		String sql = "SELECT * FROM "+MainDao.getSchema()+"."+rpt.getTable();
		try {
	        PreparedStatement ps = MainDao.getConnectionPostgis().prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();
	        ResultSetMetaData rsmd = rs.getMetaData();	
	        rs.close();
	        int k = 0;
	        int size = tokens.size();   
	        for (int j=3; j<rsmd.getColumnCount(); j++){
	        	int i = j - 3 - k;
        		// Check if we have to process this field
	        	String fieldName = rsmd.getColumnName(j);
	        	Utils.getLogger().info(fieldName+": "+tokens.get(i));
        		if (size < NORMAL_NUM_FIELDS && fieldList.contains(fieldName)){
        			k++;
        			continue;
        		}
       			fields+= rsmd.getColumnName(j) + ", ";
        		switch (rsmd.getColumnType(j)) {
				case Types.NUMERIC:
				case Types.DOUBLE:
				case Types.INTEGER:
					boolean ok = Utils.isNumeric(tokens.get(i));
					if (!ok){
						// TODO: i18n
						String msg = "An error ocurred in line number: "+lineNumber;
						msg+= "\nField "+rsmd.getColumnName(j)+ " does not contain a valid numeric value: "+tokens.get(i);
						msg+= "\nImport process will be aborted";
						Utils.showError(msg);
						return false;
					}
					values+= tokens.get(i) + ", ";
					break;					
				case Types.VARCHAR:
					values+= "'" + tokens.get(i) + "', ";
					break;					
				default:
					values+= "'" + tokens.get(i) + "', ";
					break;
				}
	        }
		} catch (SQLException e) {
			Utils.showError(e, sql);
		}
	
		fields = fields.substring(0, fields.length() - 2);
		values = values.substring(0, values.length() - 2);
		sql = "INSERT INTO "+MainDao.getSchema()+"."+rpt.getTable()+" ("+fields+") VALUES ("+values+");\n";
		insertSql += sql;
		
		return true;
		
	}

	
	// Epanet
	private static boolean processRptEpanet(RptTarget rpt) {

		boolean found = false;
		String line = "";
		String aux;
		
		Utils.getLogger().info("Target: "+rpt.getId()+" - "+rpt.getDescription());

		// Read lines until rpt.getDescription() is found			
		while (!found){
			if (lineNumber >= totalLines){
				lineNumber = 0;
				return false;
			}
			lineNumber++;				
			line = readLine();
			if (line.length() >= rpt.getDescription().length()){
				aux = line.substring(0, rpt.getDescription().length()).toLowerCase();
				if (aux.equals(rpt.getDescription().toLowerCase())){
					found = true;
					firstLine = line;						
					Utils.getLogger().info("Target line number: " + lineNumber);						
				}
			}
		}
		
		int jumpLines = rpt.getTitleLines();
		String fieldsList = "*";
		// Target node
		if (rpt.getId() == 40){
			jumpLines = 2;
			fieldsList = "id, result_id, node_id";
			for (int i=1; i<=2; i++) {
				lineNumber++;
				line = readLine();
			}
			Scanner scanner = new Scanner(line);
			parseLine1(scanner, false);		
			for (String column : tokens) {
				if (column.toLowerCase().equals("node")){
					// Just ignore them
				}
				else if (column.toLowerCase().equals("pressure")){
					fieldsList+= ", press";
				}	
				// Elevation, Demand, Head, Quality
				else if (column.toLowerCase().equals("elevation") || column.toLowerCase().equals("demand")
					|| column.toLowerCase().equals("head") || column.toLowerCase().equals("quality")) {
					fieldsList+= ", "+column.toLowerCase();
				}
			}
			fieldsList+= ", other, time";
		}
		
		// Target arc
		else if (rpt.getId() == 50){
			jumpLines = 2;
			fieldsList = "id, result_id, arc_id";
			for (int i=1; i<=2; i++) {
				lineNumber++;
				line = readLine();
			}
			Scanner scanner = new Scanner(line);
			parseLine1(scanner, false);		
			for (String column : tokens) {
				if (column.toLowerCase().equals("node") || column.toLowerCase().equals("status")){
					// Just ignore them
				}
				else if (column.toLowerCase().equals("velocity") || column.toLowerCase().equals("velocityunit")){
					fieldsList+= ", vel";
				}	
				else if (column.toLowerCase().equals("f-factor")){
					fieldsList+= ", ffactor";
				}	
				// Length, Diameter, Flow, Headloss, Setting, Reaction
				else if (column.toLowerCase().equals("length") || column.toLowerCase().equals("diameter")
					|| column.toLowerCase().equals("flow") || column.toLowerCase().equals("headloss") 
					|| column.toLowerCase().equals("setting") || column.toLowerCase().equals("reaction")) {
					fieldsList+= ", "+column.toLowerCase();
				}
			}
			fieldsList+= ", other, time";			
		}
		
		// Jump number of lines specified in rpt.getTitleLines()
		for (int i = 1; i <= jumpLines; i++) {
			lineNumber++;
			line = readLine();
			// Check if we have reached next Target
			if (line.contains("No ")) {
				return false;
			}
		}		
		
		// Get Database fields related to this Target
		if (rpt.getColumnCount() == 0){
			String sql = "SELECT "+fieldsList+" FROM "+MainDao.getSchema()+"."+rpt.getTable();
			try {
				ResultSet rs = MainDao.getResultset(sql);
				ResultSetMetaData rsmd = rs.getMetaData();	
		        for (int i = 1; i <= rsmd.getColumnCount(); i++){
		        	rpt.addColumnName(rsmd.getColumnName(i));
		        	rpt.addColumnType(rsmd.getColumnType(i));
		        }
		        rs.close();
			} catch (SQLException e) {
				Utils.showError(e, sql);
			}
		}
		
		// Read following lines until blank line is found
		tokensList = new ArrayList<ArrayList<String>>();
		// Target Hydraulic Status
		if (rpt.getId() == 20) {
			parseLinesHydraulic(rpt);
		}
		else {
			parseLines(rpt);
			// Target Input Data File
			if (rpt.getId() == 10) {
				processTokensInputData(rpt);
			}
		}
		
		return true;
		
	}	

	
	// Parse all lines of Hydraulic Target
	private static void parseLinesHydraulic(RptTarget rpt) {
			
		boolean blankLine = false;		
		int numBlankLines = 0;
		while (numBlankLines < 2){
			try {
				lineNumber++;
				String line = readLine();
				blankLine = (line.length()==0);
				if (!blankLine){
					Scanner scanner = new Scanner(line);
					tokens = parseLineHydraulic(scanner);
					processTokensHydraulic(rpt);
					numBlankLines = 0;					
				}
				else{
					numBlankLines++;
				}
			} catch (Exception e) {
				Utils.logError(e);
			}
		} 
		
	}
	
	
	// Parse values of current line
	private static ArrayList<String> parseLineHydraulic(Scanner scanner) {
		
		ArrayList<String> tokens = new ArrayList<String>();	
		String token = "";
		String first = "";
		boolean firstWord = true;
		while (scanner.hasNext()){
			if (firstWord){
				first = scanner.next();
				firstWord = false;
			}
			else{
				token += " " + scanner.next();
			}
		}
		tokens.add(first.trim());
		tokens.add(token.trim());
		
		return tokens;
		
	}	
	
	
	private static void processTokensHydraulic(RptTarget rpt) {

		String fields = "result_id, time, text";
		String values = "'"+projectName+"', ";
        String time;
        String text = "";
        time = tokens.get(0);
        time = tokens.get(0).substring(0, time.length() - 1);
        if (time.substring(0, 7).toLowerCase().equals("warning")){
        	text = tokens.get(0) + " ";
        	time = lastTimeHydraulic;
        } else{
        	lastTimeHydraulic = time;
        }
        text += tokens.get(1);
		values += "'"+time+"', '"+text+"'";
		String sql = "INSERT INTO " + MainDao.getSchema() + "." + rpt.getTable() + " (" + fields + ") VALUES (" + values + ");\n";
		insertSql += sql;
		
	}
	
	
	private static boolean processTokensInputData(RptTarget rpt) {

		// Number of fields without water quality parameters
		final int NORMAL_NUM_FIELDS = 19;
		Boolean existWaterFields = false;
		String fields = "result_id, ";
		String values = "'"+projectName+"', ";
		String sql = "SELECT * FROM "+MainDao.getSchema()+"."+rpt.getTable();
		
		try {
			
	        PreparedStatement ps = MainDao.getConnectionPostgis().prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();
	        ResultSetMetaData rsmd = rs.getMetaData();	
	        int m = 2;
	        if (tokens.size() > NORMAL_NUM_FIELDS){
	        	existWaterFields = true;
	        	m = 0;
	        }
	        //System.out.println(rsmd.getColumnCount());    // 24
        	if (tokens.size() < rsmd.getColumnCount() - 6){
        		Utils.logError("Line not valid");
        		return true;
        	}
	        rs.close();
	        
	        int k = 0;
	        for (int j=3; j<rsmd.getColumnCount() - m; j++){
	        	int i = j - 3;
	        	int columnIndex = j - k;
        		if (existWaterFields){
        			if (i == 14){
        				columnIndex = rsmd.getColumnCount() - 1;
        				k++;
        			}
        			else if (i == 15){
        				columnIndex = rsmd.getColumnCount();
        				k++;
        			}
        		} 
        		String fieldName = rsmd.getColumnName(columnIndex);
        		fields+= fieldName + ", ";
        		Utils.getLogger().info(fieldName+": "+tokens.get(i));
        		switch (rsmd.getColumnType(columnIndex)) {
				case Types.NUMERIC:
				case Types.DOUBLE:
				case Types.INTEGER:
					boolean ok = Utils.isNumeric(tokens.get(i));
					if (!ok){
						// TODO: i18n
						String msg = "An error ocurred in line number: "+lineNumber;
						msg+= "\nField "+rsmd.getColumnName(columnIndex)+ " does not contain a valid numeric value: "+tokens.get(i);
						msg+= "\nImport process will be aborted";
						Utils.showError(msg);
						abortRptProcess = true;
						return false;
					}
					values += tokens.get(i) + ", ";
					break;					
				case Types.VARCHAR:
					values += "'" + tokens.get(i) + "', ";
					break;					
				default:
					values += "'" + tokens.get(i) + "', ";
					break;
				}
	        }
	        
		} catch (SQLException e) {
			Utils.showError(e, sql);
		}
	
		fields = fields.substring(0, fields.length() - 2);
		values = values.substring(0, values.length() - 2);
		sql = "INSERT INTO "+MainDao.getSchema()+"."+rpt.getTable()+" ("+fields+") VALUES ("+values+");\n";
		insertSql += sql;
		
		return true;
		
	}	
	
	
	private static String readLine(){
		String line = fileContent.get(lineNumber - 1);
		return line;
	}
	

}