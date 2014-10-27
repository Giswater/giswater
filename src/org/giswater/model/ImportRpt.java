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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.util.Utils;


public class ImportRpt extends Model {
	
	private static String insertSql;
	private static int lineNumber;   // Number of lines read or current line to process
	private static ArrayList<String> fileContent;
	private static int totalLines;   // Total number of lines inside .rpt file
	private static ArrayList<String> pollutants;
	private static String firstLine;
	private static String lastTimeHydraulic = "";
	private static boolean continueProcess = true;
	private static boolean abortRptProcess = false;
	private static File fileRpt;
	private static String projectName;
	
	private static LinkedHashMap<Integer, RptTarget> mapRptTargets = null;
	
	// Each rptElem corresponds to one line of a .rpt file
	private static RptToken rptToken = null;
	private static ArrayList<RptToken> rptTokens;	
	private static ArrayList<ArrayList<RptToken>> rptTokensList;	
	
	
	// Import RPT file into Postgis tables
    public static boolean process(File fileRpt, String projectName) {
        
    	Utils.getLogger().info("importRpt");

    	abortRptProcess = false; 
    	ImportRpt.fileRpt = fileRpt;
    	ImportRpt.projectName = projectName;

    	// Ask confirmation to user
        String importRpt = PropertiesDao.getPropertiesFile().get("AUTO_IMPORT_RPT", "false").toLowerCase();
        if (importRpt.equals("false")) {
           	int res = Utils.confirmDialog("import_sure");    		
           	if (res == JOptionPane.NO_OPTION) {
           		return false;
        	}    	
        }  

       	// Check if we want to overwrite previous results
        Boolean overwrite = Boolean.parseBoolean(PropertiesDao.getPropertiesFile().get("IMPORT_OVERWRITE", "false"));
        Utils.getLogger().info("IMPORT_OVERWRITE: " + overwrite);
        
    	// Check if Project Name exists in rpt_result_id
    	boolean exists = false;
    	if (existsProjectName()) {
    		exists = true;
            if (!overwrite) {
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
    	
    	// Initialize targets 
   		initRptTargets();
    	
        // EPASWMM
        if (softwareName.equals("SWMM")) {
        	processEPASWMM(overwrite, exists);
        } 
        // EPANET
        else {
        	processEPANET(overwrite, exists);
        }
    	       
		return true;
		
    }
    

	private static boolean processEPASWMM(Boolean overwrite, Boolean exists) {

        // Iterate over targets
        Iterator<Entry<Integer, RptTarget>> it = mapRptTargets.entrySet().iterator();
        while (it.hasNext()) {
        	insertSql = "";
            Map.Entry<Integer, RptTarget> mapEntry = it.next();
            RptTarget rptTarget = mapEntry.getValue();
            boolean ok = processRptTarget(rptTarget);
			if (abortRptProcess) return false;
            if (!continueProcess) return false;
            postProcessTarget(rptTarget, ok, true, overwrite, exists);
        }

        // Insert into result_selection commiting transaction
   		MainDao.setResultSelect(MainDao.getSchema(), "result_selection", projectName);
   		
   		return true;
		
	}

    
    private static boolean processEPANET(Boolean overwrite, boolean exists) {

        // Iterate over targets
    	String sql = "";
        Iterator<Entry<Integer, RptTarget>> it = mapRptTargets.entrySet().iterator();
        while (it.hasNext()) {
        	
        	insertSql = "";
            Map.Entry<Integer, RptTarget> mapEntry = it.next();
            RptTarget rptTarget = mapEntry.getValue();
            boolean ok = false;
            boolean processTarget = true;
            boolean continueTarget;
            
        	// Target node or arc
    		if (rptTarget.getId() == 40 || rptTarget.getId() == 50) {
    			processTarget = false;
    			continueTarget = true;
    			if (overwrite) {
    				sql = "DELETE FROM "+MainDao.getSchema()+"."+rptTarget.getTable();
    				Utils.logSql(sql);
    				MainDao.executeUpdateSql(sql);
    			}
    			else {
    				if (exists) {
    					sql = "DELETE FROM "+MainDao.getSchema()+"."+rptTarget.getTable() + 
    						" WHERE result_id = '"+projectName+"'";
    					Utils.logSql(sql);
    					MainDao.executeUpdateSql(sql);
    				}
	    		}            			
    			while (continueTarget) {
        			insertSql = "";       
    				ok = processRptTargetEpanet(rptTarget);
    				if (abortRptProcess) return false;
    	        	if (ok) {
    		    		if (!insertSql.equals("")) {
		            		firstLine = firstLine.substring(15, 24).trim(); 
		            		sql = "UPDATE "+MainDao.getSchema()+"."+rptTarget.getTable() + 
		            			" SET time = '"+firstLine+"' WHERE time is null;";
		            		insertSql+= sql;       	
    		            	Utils.logSql(insertSql);
    			    		if (!MainDao.executeUpdateSql(insertSql)) {
    							return false;
    						}
    		    		}
    	        	}
    				continueTarget = (lineNumber > 0);            	        	
    			}
    		}
    		
    		// Target different than node or arc
    		else {
				ok = processRptTargetEpanet(rptTarget);
				if (abortRptProcess) return false;				
    		}
            
            if (!continueProcess) return false;
            postProcessTarget(rptTarget, ok, processTarget, overwrite, exists);
        	
        } // end iterations over targets (while)

        // Insert into result_selection commiting transaction
   		MainDao.setResultSelect(MainDao.getSchema(), "result_selection", projectName);

		return true;
		
	}
    
    
	private static boolean postProcessTarget(RptTarget rptTarget, boolean ok, boolean processTarget, boolean overwrite, boolean exists) {
		
		String sql;
    	if (ok && processTarget) {
			if (overwrite) {
				sql = "DELETE FROM "+MainDao.getSchema()+"."+rptTarget.getTable();
				Utils.logSql(sql);
				MainDao.executeUpdateSql(sql);
			}
			else {
				if (exists) {
					sql = "DELETE FROM "+MainDao.getSchema()+"."+rptTarget.getTable() + 
						" WHERE result_id = '"+projectName+"'";
					Utils.logSql(sql);
					MainDao.executeUpdateSql(sql);
				}
    		} 
    		if (!insertSql.equals("")) {
    			Utils.logSql(insertSql);	            	
	    		boolean status = MainDao.executeUpdateSql(insertSql, true);
	    		if (!status) {
	    			String msg = "Import aborted. Some data values are not valid in current target: ";
	    			msg+= "\n" + rptTarget.getDescription();
	    			msg+= "\nOpen current .log file for more details";
	    			Utils.showError(msg);
	    			return false;
	    		}
    		}
    	} 
    	else {
    		Utils.getLogger().info("Target not found: " + rptTarget.getId() + " - " + rptTarget.getDescription());
    		if (softwareName.equals("EPANET") && rptTarget.getId() == 10) {
    			sql = "INSERT INTO "+MainDao.getSchema()+"."+rptTarget.getTable()+ " (result_id) VALUES ('"+projectName+"')";
    			MainDao.executeUpdateSql(sql, true);
    		}
    	}
		
		return true;
		
	}

	
	private static String readLine(){
		String line = fileContent.get(lineNumber - 1);
		return line;
	}
	
	
	private static boolean initRptTargets() {
    	
        // Get info from rpt_target into memory
        mapRptTargets = new LinkedHashMap<Integer, RptTarget>();
        String sql = "SELECT id, db_table, description, type, title_lines, tokens, parametrized" +
        	" FROM rpt_target" +
        	" WHERE type <> 0" + 
        	" ORDER BY id";
        try {
            ResultSet rs = MainDao.getResultset(connectionDrivers, sql, true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
            	RptTarget rptTarget = new RptTarget(rs);
                mapRptTargets.put(rptTarget.getId(), rptTarget);
            }
            rs.close();  
        } catch (SQLException e) {
            Utils.showError(e, sql);
            return false;
        }    	
        
        // Iterate over targets and get info from rpt_target_fields into memory
        Iterator<Entry<Integer, RptTarget>> it = mapRptTargets.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, RptTarget> mapEntry = it.next();
            RptTarget rptTarget = mapEntry.getValue();
            sql = "SELECT target_id || '_' || db_name as id, target_id, rpt_name, db_name, db_type" +
            	" FROM rpt_target_fields" + 
            	" WHERE target_id = "+rptTarget.getId();
            ResultSet rs = MainDao.getResultset(connectionDrivers, sql, true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            try {
				while (rs.next()) {
					RptTargetField rptTargetField = new RptTargetField(rs);
					rptTarget.addRptTargetField(rptTargetField);
				}
				rs.close();            
			} catch (SQLException e) {
	            Utils.showError(e, sql);
	            return false;
			}
        }
    	return true;
    	
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

		
	// Process current RptTarget (EPASWMM)
	private static boolean processRptTarget(RptTarget rptTarget) {

		// Read lines until rpt.getDescription() is found		
		boolean found = false;
		String line;
		String aux;
		
		Utils.getLogger().info("Target: "+rptTarget.getId()+ " - "+rptTarget.getDescription());
		
//		if (rptTarget.getId() == 40) {
//			System.out.println(rptTarget.getId());
//		}
		
		// Read lines until rpt.getDescription() is found		
		while (!found) {
			if (lineNumber >= totalLines) {
				lineNumber = 0;
				return false;
			}
			lineNumber++;				
			line = readLine();
			if (line.length() >= rptTarget.getDescription().length()) {
				aux = line.substring(0, rptTarget.getDescription().length()).toLowerCase();
				if (aux.equals(rptTarget.getDescription().toLowerCase())) {
					found = true;
					Utils.getLogger().info("Target line number: "+lineNumber);						
				}
			}
		}
		
        if (rptTarget.getType() == 3 || rptTarget.getType() == 5 || rptTarget.getType() == 6) {
        	getPollutants(rptTarget);
        }
		
		// Jump number of lines specified in rpt.getTitleLines()
		for (int i = 1; i <= rptTarget.getTitleLines(); i++) {
			lineNumber++;
			line = readLine();
			// Check if we have reached next Target
			if (line.contains("No ")) {
				return false;
			}
		}		
		
		// Get Database fields related to this Target
		if (rptTarget.getColumnCount() == 0) {
			String sql = "SELECT * FROM "+MainDao.getSchema()+"."+rptTarget.getTable();
			try {
		        ResultSet rs = MainDao.getResultset(sql);
		        ResultSetMetaData rsmd = rs.getMetaData();	
		        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
		        	rptTarget.addColumnName(rsmd.getColumnName(i));
		        	rptTarget.addColumnType(rsmd.getColumnType(i));
		        }
		        rs.close();
			} catch (SQLException e) {
				Utils.showError(e, sql);
			}
		}
			
		// Read following lines until blank line is found
		rptTokensList = new ArrayList<ArrayList<RptToken>>();		
		rptTokens = new ArrayList<RptToken>();	

		if (rptTarget.getId() == 10) {
			parseLinesAnalysis(rptTarget);
			processTokensAnalysis(rptTarget);
		}
		else if (rptTarget.getType() == 2 && rptTarget.getId() != 10) {
			continueProcess = parseLines(rptTarget);
			processTokensCheck(rptTarget);
		}
		else if (rptTarget.getType() == 3) {
			continueProcess = parseLines(rptTarget);
			processTokens3(rptTarget);
		}
		else {
			continueProcess = parseLines(rptTarget);
		}
		
		return true;
		
	}	
	
	
	private static boolean processTokensCheck(RptTarget rptTarget) {
		
		boolean result;
		if (rptTarget.getParametrized() == 0) {
			result = processTokens(rptTarget);
		}
		else {
			result = processTokensParametrized(rptTarget);
		}
		return result;
		
	}
	
	
	// EPASWMM
	private static boolean parseLinesAnalysis(RptTarget rptTarget) {

		boolean result = true;
		rptTokens = new ArrayList<RptToken>();			
		boolean blankLine = false;		
		while (!blankLine) {
			lineNumber++;
			String line = readLine();
			blankLine = (line.length() == 0);
			if (!blankLine) {
				Scanner scanner = new Scanner(line);
				parseLine2(scanner, rptTarget, true);						
			}
			if (abortRptProcess) {
				return false;
			}
		}		
		
		return result;
		
	}


	// EPASWMM
	private static void getPollutants(RptTarget rpt) {
		
		String line = "";
		int jumpLines;
		if (rpt.getType() == 3) {			
			lineNumber--;
			line = readLine();		
		} 
		else {
			jumpLines = (rpt.getType() == 5) ? 4 : 5;
			for (int i = 1; i <= jumpLines; i++) {		
				lineNumber++;					
				line = readLine();
			}
		}
		
		boolean blankLine = (line.length() == 0);
		if (!blankLine) {
			Scanner scanner = new Scanner(line);
			if (rpt.getType() == 3 || rpt.getType() == 6) {
				int jumpScanner	= (rpt.getType() == 3) ? 1 : 4;
				for (int i = 0; i < jumpScanner; i++) {
					scanner.next();						
				}
			}
			// Get pollutant name
			parseLine1(scanner);		
			pollutants = new ArrayList<String>();	
			for (int i = 0; i < rptTokens.size(); i++) {
				pollutants.add(rptTokens.get(i).getRptValue());
			}
		}		
		
	}
	
	
	// Parse all lines
	private static boolean parseLines(RptTarget rpt) {
		
		boolean result = true;
		rptTokens = new ArrayList<RptToken>();			
		boolean blankLine = false;		
		while (!blankLine) {
			lineNumber++;
			String line = readLine();
			blankLine = (line.length() == 0);
			if (!blankLine) {
				Scanner scanner = new Scanner(line);
				if (rpt.getType() == 1) {
					parseLine1(scanner);
					result = processTokensCheck(rpt);
					if (!result) return false;
				}		
				else if (rpt.getType() == 2) {			
					parseLine2(scanner, rpt, true);
				}
				else if (rpt.getType() == 3) {	
					rptTokens = new ArrayList<RptToken>();	
					parseLine2(scanner, rpt, false);
					rptTokensList.add(rptTokens);
				}					
				else if (rpt.getType() == 4) {					
					parseLineType4(scanner);
					processTokensCheck(rpt);							
				}	
				else if (rpt.getType() == 5) {					
					parseLine1(scanner);
					processTokens5(rpt);						
				}				
				else if (rpt.getType() == 6) {					
					parseLine1(scanner);
					processTokens6(rpt);						
				}			
				else if (rpt.getType() == 7) {					
					parseLine1(scanner);
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
	private static void parseLine1(Scanner scanner) {
		
		rptTokens = new ArrayList<RptToken>();	
		String rptValue = "";
		while (scanner.hasNext()) {
			rptValue = scanner.next();
			RptToken rptToken = new RptToken("", rptValue);
			rptTokens.add(rptToken);	
		}
		
	}	
	
	
	private static void parseLineType4(Scanner scanner) {
		
		rptTokens = new ArrayList<RptToken>();	
		String rptValue = "";
		while (scanner.hasNext()) {
			rptValue+= " "+scanner.next();
		}
		rptToken = new RptToken("", rptValue.trim());
		rptTokens.add(rptToken);	
		
	}
	
	
	// Parse values of current line that contains ".." in it
	private static void parseLine2(Scanner scanner, RptTarget rptTarget, boolean together) {
		
		String rptName = "";
		String rptValue = "";
		String token;
		boolean valueFound = false;
		int numTokens = 0;
		while (scanner.hasNext()) {
			token = scanner.next();
			if (token.contains("..")) {
				valueFound = true;
			}
			// TODO: EPANET Number of Junctions...
			if (valueFound && !token.contains("..")) {
				numTokens++;
				if (numTokens <= rptTarget.getTokens()) {
					if (together) {
						rptValue+= token + " ";
					}
					else {
						rptName = rptName.substring(0, rptName.length() - 1);
						rptToken = new RptToken(rptName, token);
						rptTokens.add(rptToken);						
					}
				}
			}
			if (!valueFound && !token.contains("..")) {
				rptName+= token.toLowerCase() + "_";
			}
		}
		
		if (valueFound && together) {
			rptName = rptName.substring(0, rptName.length() - 1);
			rptToken = new RptToken(rptName, rptValue.trim());
			rptTokens.add(rptToken);	
		}
		
	}
	
	
	private static boolean processTokens(RptTarget rptTarget) {

		String fields = "result_id, ";
		String values = "'"+projectName+"', ";
		
		if (softwareName.equals("EPANET")) {
			if (rptTokens.size() < 2) {
				Utils.logError("Line not valid");
				return true;
			}
		}
		
    	for (int i=0; i<rptTokens.size(); i++) {        	
    		RptToken rptToken = rptTokens.get(i);
			int j = i+3;
			String value = rptToken.getRptValue();
			Integer dbType;
			switch (rptTarget.getColumnType(j)) {
			case Types.NUMERIC:
			case Types.DOUBLE:
			case Types.INTEGER:
				dbType = 1;
				break;									
			default:
				dbType = 0;
				break;
			}
			
			String result = processField(rptTarget.getColumnName(j), value, dbType);
			if (result.equals("-1")) {
				abortRptProcess = true;
				return false;
			}
			values+= result+", ";
			
			fields+= rptTarget.getColumnName(j) + ", ";
		}
	
        // Build SQL Insert sentence
    	insertSql+= buildSql(rptTarget, fields, values);
		
		return true;
		
	}
	
	
	private static boolean processTokensParametrized(RptTarget rptTarget) {

		String fields = "result_id, ";
		String values = "'"+projectName+"', ";
		
		if (softwareName.equals("EPANET")) {
			if (rptTokens.size() < 2) {
				Utils.logError("Line not valid");
				return true;
			}
		}
		
		// Set ResultSet for rpt destination table
        String sqlDest = "SELECT * FROM "+MainDao.getSchema()+"."+rptTarget.getTable();
        ResultSet rsDest = MainDao.getResultset(sqlDest);
        
        // Iterate over rptTokens
    	for (int i=0; i<rptTokens.size(); i++) {        	
    		RptToken rptToken = rptTokens.get(i);
        	// Search if that RptTargetField exists in the Database
            String sql = "SELECT db_name, db_type" +
            	" FROM rpt_target_fields" +
            	" WHERE target_id = "+rptTarget.getId()+" AND lower(rpt_name) = '"+rptToken.getRptName()+"'";
            ResultSet rs = MainDao.getResultset(connectionDrivers, sql, true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            try {
				if (rs.next()) {
					// Check if that field exists in rpt destination table
					String fieldName = rs.getString("db_name");
					boolean exists = MainDao.checkColumn(rsDest, fieldName);
					if (exists) {
						fields+= fieldName + ", ";
						String rptValue = rptToken.getRptValue();
						Integer dbType = rs.getInt("db_type");
						String result = processField(fieldName, rptValue, dbType);
						if (result.equals("-1")) {
							return false;
						}
						values+= result+", ";
					}
					else {
						Utils.getLogger().info("Field not exists in table '"+rptTarget.getTable()+"': "+fieldName);	
					}
				}
				else {
	        		Utils.getLogger().info("Target field not found in driver database: "+rptToken.getRptName());					
				}
				rs.close();            
			} catch (SQLException e) {
	            Utils.showError(e, sql);
	            return false;
			}
        }
	
        // Build SQL Insert sentence
    	insertSql+= buildSql(rptTarget, fields, values);
		
		return true;
		
	}


	private static void processTokens3(RptTarget rptTarget) {

		// Iterate over pollutants
		for (int i=0; i<pollutants.size(); i++) {
			String fields = "result_id, poll_id, ";
			String values = "'"+projectName+"', '"+pollutants.get(i)+"', ";
			// Iterate over fields
			for (int j=0; j<rptTokensList.size(); j++) {
				String value = rptTokensList.get(j).get(i).getRptValue();
				values += "'"+value+"', ";
				fields += rptTarget.getColumnName(j + 4) + ", ";        	
			}
	        // Build SQL Insert sentence
	    	insertSql+= buildSql(rptTarget, fields, values);			
        }
		
	}		
		
	
	private static void processTokens5(RptTarget rptTarget) {

		String fields = "result_id, subc_id, poll_id, value";
		String fixedValues = "'"+projectName+"', '"+rptTokens.get(0).getRptValue()+ "', ";
		
		// No permetre tipus String (peta a Subcatchment)
		try {
			Integer.parseInt(rptTokens.get(0).getRptValue());
		} catch (NumberFormatException e) {
			return;
		}
		
		// Iterate over pollutants
		if (rptTokens.size() > pollutants.size()) {
			for (int i=0; i<pollutants.size(); i++) {
				Double units = Double.valueOf(rptTokens.get(i+1).getRptValue());
				String values = fixedValues + "'"+pollutants.get(i)+"', "+units;
				String sql = "INSERT INTO "+MainDao.getSchema()+"."+rptTarget.getTable()+" ("+fields+") VALUES ("+values+");\n";
				insertSql+= sql;		        
			}
		}
		
	}	

	
	// Outfall Loading Summary (only)
	private static void processTokens6(RptTarget rptTarget) {
		
		String fields = "result_id, node_id, flow_freq, avg_flow, max_flow, total_vol";
		String fields2 = "result_id, node_id, poll_id, value";		
		String fixedValues = "'"+projectName+"', '"+rptTokens.get(0).getRptValue()+ "', ";
	
		// If found separator o resume line skip them
		if (rptTokens.size() < 5 || rptTokens.get(0).getRptValue().equals("System")) {
			return;
		}
		
		// Iterate over first 5 fields
		String values = fixedValues;
		for (int j=1; j<5; j++) {
			values+= rptTokens.get(j).getRptValue() + ", ";
		}
		values = values.substring(0, values.length() - 2);		
		String sql = "INSERT INTO "+MainDao.getSchema()+"."+rptTarget.getTable()+" ("+fields+") VALUES ("+values+");\n";
		insertSql += sql;				
		
		// Table name hardcoded. Not present in sqlite database
		for (int i = 0; i < pollutants.size(); i++) {
			int j = i+5;
			Double units = Double.valueOf(rptTokens.get(j).getRptValue());
			values = fixedValues + "'"+pollutants.get(i)+"', "+units;
			sql = "DELETE FROM "+MainDao.getSchema()+".rpt_outfallload_sum " +
				"WHERE result_id = '"+projectName+"' AND node_id = '"+rptTokens.get(0).getRptValue()+"' AND poll_id = '"+pollutants.get(i)+ "';\n";
			insertSql+= sql;	
			sql = "INSERT INTO "+MainDao.getSchema()+".rpt_outfallload_sum ("+fields2+") VALUES ("+values+");\n";
			insertSql+= sql;		        
		}
		
	}
	
	
	private static boolean processTokensAnalysis(RptTarget rptTarget) {

		String fields = "result_id, ";
		String values = "'"+projectName+"', ";
        
		// Set ResultSet for rpt destination table
		String sqlDest = "SELECT * FROM "+MainDao.getSchema()+"."+rptTarget.getTable();
        ResultSet rsDest = MainDao.getResultset(sqlDest);
        
        // Iterate over rptTokens
    	for (int i=0; i<rptTokens.size(); i++) {        	
    		RptToken rptToken = rptTokens.get(i);
        	// Search if that RptTargetField exists in the Database
    		String sql = "SELECT db_name, db_type" +
            	" FROM rpt_target_fields" +
            	" WHERE target_id = "+rptTarget.getId()+" AND lower(rpt_name) = '"+rptToken.getRptName()+"'";
            ResultSet rs = MainDao.getResultset(connectionDrivers, sql, true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            try {
				if (rs.next()) {
					// Check if that field exists in rpt destination table
					String fieldName = rs.getString("db_name");
					boolean exists = MainDao.checkColumn(rsDest, fieldName);
					if (exists) {
						fields+= fieldName + ", ";
						String rptValue = rptToken.getRptValue();
						Integer dbType = rs.getInt("db_type");
						String result = processField(fieldName, rptValue, dbType);
						if (result.equals("-1")) {
							return false;
						}
						values+= result+", ";
					}
					else {
						Utils.getLogger().info("Field not exists in table '"+rptTarget.getTable()+"': "+fieldName);	
					}
				}
				else {
	        		Utils.getLogger().info("Target field not found in driver database: "+rptToken.getRptName());					
				}
				rs.close();            
			} catch (SQLException e) {
	            Utils.showError(e, sql);
	            return false;
			}
        }
	
        // Build SQL Insert sentence
    	insertSql+= buildSql(rptTarget, fields, values);
		
		return true;
		
	}

	
	private static String processField(String fieldName, String rptValue, Integer dbType) {
		
		String result;
		if (dbType != 0) {
			boolean ok = Utils.isNumeric(rptValue);
			if (!ok) {
				String msg = "An error ocurred in line number: "+lineNumber;
				msg+= "\nField "+fieldName+ " does not contain a valid numeric value: "+rptValue;
				msg+= "\nImport process will be aborted";
				Utils.showError(msg);
				result = "-1";
			}
			result = rptValue;
		}				
		else {
			result = "'"+rptValue+"'";	
		}
		return result;
		
	}


	private static String buildSql(RptTarget rptTarget, String fields, String values) {
		fields = fields.substring(0, fields.length() - 2);
		values = values.substring(0, values.length() - 2);
		String sql = "INSERT INTO "+MainDao.getSchema()+"."+rptTarget.getTable()+" ("+fields+") VALUES ("+values+");\n";
		return sql;
	}
	
	
	// EPANET
	private static boolean processRptTargetEpanet(RptTarget rptTarget) {

		boolean found = false;
		String line = "";
		String aux;
		
		Utils.getLogger().info("Target: "+rptTarget.getId()+" - "+rptTarget.getDescription());

		// Read lines until rpt.getDescription() is found			
		while (!found) {
			if (lineNumber >= totalLines) {
				lineNumber = 0;
				return false;
			}
			lineNumber++;				
			line = readLine();
			if (line.length() >= rptTarget.getDescription().length()) {
				aux = line.substring(0, rptTarget.getDescription().length()).toLowerCase();
				if (aux.equals(rptTarget.getDescription().toLowerCase())) {
					found = true;
					firstLine = line;						
					Utils.getLogger().info("Target line number: " + lineNumber);						
				}
			}
		}
		
		int jumpLines = rptTarget.getTitleLines();
		String fieldsList = "*";
		// Target node
		if (rptTarget.getId() == 40) {
			
			jumpLines = 2;
			fieldsList = "id, result_id, node_id";
			for (int i=1; i<=2; i++) {
				lineNumber++;
				line = readLine();
			}
			Scanner scanner = new Scanner(line);
			parseLine1(scanner);		
			for (int i=0; i<rptTokens.size(); i++) {
				String column = rptTokens.get(i).getRptValue().toLowerCase();
				if (column.equals("node")) {
					// Just ignore them
				}
				else if (column.equals("pressure")) {
					fieldsList+= ", press";
				}	
				// Quality
				else if (column.equals("quality") || column.equals("chemical")
					|| column.equals("%_factor") || column.equals("age")) {
					fieldsList+= ", quality";
				}
				// Elevation, Demand, Head 
				else if (column.equals("elevation") || column.equals("demand")
					|| column.equals("head") || column.equals("quality")) {
					fieldsList+= ", "+column;
				}
			}
			fieldsList+= ", other, time";
			
		}
		
		// Target arc
		else if (rptTarget.getId() == 50) {
			
			jumpLines = 2;
			fieldsList = "id, result_id, arc_id";
			for (int i=1; i<=2; i++) {
				lineNumber++;
				line = readLine();
			}
			Scanner scanner = new Scanner(line);
			parseLine1(scanner);		
			for (int i=0; i<rptTokens.size(); i++) {
				String column = rptTokens.get(i).getRptValue().toLowerCase();
				if (column.equals("node") || column.equals("status")) {
					// Just ignore them
				}
				else if (column.equals("velocity") || column.equals("velocityunit")) {
					fieldsList+= ", vel";
				}	
				else if (column.equals("f-factor")) {
					fieldsList+= ", ffactor";
				}	
				// Length, Diameter, Flow, Headloss, Setting, Reaction
				else if (column.equals("length") || column.equals("diameter")
					|| column.equals("flow") || column.equals("headloss") 
					|| column.equals("setting") || column.equals("reaction")) {
					fieldsList+= ", "+column;
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
		if (rptTarget.getColumnCount() == 0) {
			String sql = "SELECT "+fieldsList+" FROM "+MainDao.getSchema()+"."+rptTarget.getTable();
			try {
				ResultSet rs = MainDao.getResultset(sql);
				ResultSetMetaData rsmd = rs.getMetaData();	
		        for (int i=1; i<=rsmd.getColumnCount(); i++){
		        	rptTarget.addColumnName(rsmd.getColumnName(i));
		        	rptTarget.addColumnType(rsmd.getColumnType(i));
		        }
		        rs.close();
			} catch (SQLException e) {
				Utils.showError(e, sql);
			}
		}
		
		// Read following lines until blank line is found
		rptTokensList = new ArrayList<ArrayList<RptToken>>();
		// Target Hydraulic Status
		if (rptTarget.getId() == 20) {
			parseLinesHydraulic(rptTarget);
		}
		else {
			parseLines(rptTarget);
			// Target Input Data File
			if (rptTarget.getId() == 10) {
				processTokensInputData(rptTarget);
			}
		}
		
		return true;
		
	}	

	
	// EPANET: Parse all lines of Hydraulic Status Target
	private static void parseLinesHydraulic(RptTarget rptTarget) {
			
		rptTokens = new ArrayList<RptToken>();		
		boolean blankLine = false;		
		int numBlankLines = 0;
		while (numBlankLines < 2) {
			try {
				lineNumber++;
				String line = readLine();
				blankLine = (line.length()==0);
				if (!blankLine) {
					Scanner scanner = new Scanner(line);
					rptTokens = parseLineHydraulic(scanner);
					processTokensHydraulic(rptTarget);
					numBlankLines = 0;					
				}
				else {
					numBlankLines++;
				}
			} catch (Exception e) {
				Utils.logError(e);
			}
		} 
		
	}
	
	
	// EPANET: Parse values of current line
	private static ArrayList<RptToken> parseLineHydraulic(Scanner scanner) {
		
		ArrayList<RptToken> tokens = new ArrayList<RptToken>();	
		String token = "";
		String first = "";
		boolean firstWord = true;
		while (scanner.hasNext()) {
			if (firstWord) {
				first = scanner.next();
				firstWord = false;
			}
			else {
				token+= " " + scanner.next();
			}
		}
		RptToken rptFirst = new RptToken("first", first.trim());
		RptToken rptToken = new RptToken("token", token.trim());
		tokens.add(rptFirst);
		tokens.add(rptToken);
		
		return tokens;
		
	}	
	
	
	// EPANET
	private static void processTokensHydraulic(RptTarget rptTarget) {

		String fields = "result_id, time, text";
		String values = "'"+projectName+"', ";
        String time;
        String text = "";
        time = rptTokens.get(0).getRptValue();
        time = rptTokens.get(0).getRptValue().substring(0, time.length() - 1);
        if (time.substring(0, 7).toLowerCase().equals("warning")){
        	text = rptTokens.get(0).getRptValue() + " ";
        	time = lastTimeHydraulic;
        } else{
        	lastTimeHydraulic = time;
        }
        text += rptTokens.get(1).getRptValue();
		values += "'"+time+"', '"+text+"'";
		String sql = "INSERT INTO "+MainDao.getSchema()+"."+rptTarget.getTable()+" ("+fields+") VALUES ("+values+");\n";
		insertSql += sql;
		
	}
	
	
	// EPANET
	private static boolean processTokensInputData(RptTarget rptTarget) {

		// Number of fields without water quality parameters
		final int NORMAL_NUM_FIELDS = 19;
		Boolean existWaterFields = false;
		String fields = "result_id, ";
		String values = "'"+projectName+"', ";
		String sql = "SELECT * FROM "+MainDao.getSchema()+"."+rptTarget.getTable();
		
		try {
			
	        PreparedStatement ps = MainDao.getConnectionPostgis().prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();
	        ResultSetMetaData rsmd = rs.getMetaData();	
	        int m = 2;
	        if (rptTokens.size() > NORMAL_NUM_FIELDS){
	        	existWaterFields = true;
	        	m = 0;
	        }
	        //System.out.println(rsmd.getColumnCount());    // 24
        	if (rptTokens.size() < rsmd.getColumnCount() - 6) {
        		Utils.logError("Line not valid");
        		return true;
        	}
	        rs.close();
	        
	        int k = 0;
	        for (int j=3; j<rsmd.getColumnCount() - m; j++) {
	        	
	        	int i = j-3;
	        	int columnIndex = j-k;
        		if (existWaterFields) {
        			if (i == 14) {
        				columnIndex = rsmd.getColumnCount() - 1;
        				k++;
        			}
        			else if (i == 15) {
        				columnIndex = rsmd.getColumnCount();
        				k++;
        			}
        		} 
        		String fieldName = rsmd.getColumnName(columnIndex);
        		fields+= fieldName + ", ";
        		String rptValue = rptTokens.get(i).getRptValue();		
    			Integer dbType;
    			switch (rptTarget.getColumnType(columnIndex)) {
    			case Types.NUMERIC:
    			case Types.DOUBLE:
    			case Types.INTEGER:
    				dbType = 1;
    				break;									
    			default:
    				dbType = 0;
    				break;
    			}
    			
    			String result = processField(rptTarget.getColumnName(j), rptValue, dbType);
    			if (result.equals("-1")) {
    				abortRptProcess = true;
    				return false;
    			}
    			values+= result+", ";
        		
	        }
	        
		} catch (SQLException e) {
			Utils.showError(e, sql);
		}
	
        // Build SQL Insert sentence
    	insertSql+= buildSql(rptTarget, fields, values);
		
		return true;
		
	}	
	
	
}