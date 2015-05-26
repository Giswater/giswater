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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.giswater.util.Utils;


public class ConfigDao {

	private static Connection connectionConfig;   // config.sqlite
    private static Connection connectionDrivers;  // waterSoftware drivers
    private static String inpFolder;   // appPath + "inp"
    
	private static final String CONFIG_DB = "config.sqlite";
	
    
	public static String getInpFolder() {
		return inpFolder;
	}	
	
	public static void setInpFolder(String inpFolder) {
		ConfigDao.inpFolder = inpFolder;
	}
	
	public static Connection getConnectionDrivers() {
		return connectionDrivers;
	}
	
    // Connect to Config sqlite Database
    public static boolean setConnectionConfig() {

        try {
            Class.forName("org.sqlite.JDBC");
            String filePath = inpFolder + CONFIG_DB;
            File file = new File(filePath);
            if (file.exists()) {
            	connectionConfig = DriverManager.getConnection("jdbc:sqlite:" + filePath);
                return true;
            }
			Utils.showError("inp_error_notfound", filePath);
			return false;
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
            String filePath = inpFolder + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                connectionDrivers = DriverManager.getConnection("jdbc:sqlite:" + filePath);
                return true;
            }
			Utils.showError("inp_error_notfound", filePath);
			return false;
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
	
	
	public static String getExeName(String id) {
		
		String sql = "SELECT exe_name FROM postgis_software WHERE id = '"+id+"'";
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
	
	
    public static Vector<String> getAvailableVersions(String type, String software) {

        Vector<String> vector = new Vector<String>();
        String sql = "SELECT id" +
        	" FROM "+type+"_software" +  
        	" WHERE available = 1 AND software_name = '"+software+"'" +
        	" ORDER BY id ASC";            
		try {
            Statement stmt = connectionConfig.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	vector.add(rs.getString("id"));
	        }
	        rs.close();  
	        stmt.close();
		} catch (SQLException e) {
            Utils.showError(e, sql);
		}            
		return vector;
    	
    }
    
    
	public static String replaceSpatialParameters(String schemaSrid, String content) {
		
		String aux = content;
        String sql = "SELECT parameters, srs_id, srid, auth_name || ':' || auth_id as auth_id, description," +
        	" projection_acronym, ellipsoid_acronym, is_geo" + 
        	" FROM srs WHERE srid = '"+schemaSrid+"'"; 
        try {
            Statement stmt = connectionConfig.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
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
            stmt.close();
        } catch (SQLException e) {
        	Utils.showError(e, sql);
        }		
		return aux;
		
	}	
		
	
}