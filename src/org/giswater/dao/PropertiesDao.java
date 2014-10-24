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

import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class PropertiesDao {

	private static String configPath;   // rootPath + CONFIG_FOLDER + Properties file
	private static String gswPath;   // Current gsw Project preferences File
	private static String gswDefaultPath;   // Default gsw Project preferences File
	private static String gswTemplatePath;   // Template gsw Project preferences File. Used to create new gsw files
	private static String rootFolder;
    private static PropertiesMap prop = new PropertiesMap();
    private static PropertiesMap gswProp = new PropertiesMap();
    
	private static final String MINOR_VERSION = "1.1";
	private static final String CONFIG_FILE = "giswater";
	private static final String GSW_DEFAULT_FILE = "default_"+MINOR_VERSION+".gsw";
	private static final String GSW_TEMPLATE_FILE = "template_"+MINOR_VERSION+".gsw";
	private static final String CONFIG_FOLDER = "config" + File.separator;

	
	public static String getGswDefaultPath() {
		return gswDefaultPath;
	}
	
	public static String getGswTemplatePath() {
		return gswTemplatePath;
	}
	
	public static String getGswTempPath() {
		
		String tempPath = "";
		boolean exists = false;
		int i = 1;
		do {
			tempPath = rootFolder + CONFIG_FOLDER + "temp_" + i + ".gsw";
			File file = new File(tempPath);
			exists = file.exists();
			i++;
		} while (exists);
			
		return tempPath;
		
	}
	
	public static String getGswPath() {
		return gswPath;
	}
	
	public static void setGswPath(String path) {
		gswPath = path;
	}
	
	
    public static PropertiesMap getPropertiesFile() {
        return prop;
    }
    
    
    public static PropertiesMap getGswProperties() {
        return gswProp;
    }   
    
    
    public static void setGswProperties(PropertiesMap gswProp) {
    	PropertiesDao.gswProp = gswProp;
    }    

    
	public static boolean configIni(String rootFolder) {
		
		PropertiesDao.rootFolder = rootFolder;
		
    	// Load Properties files
    	if (!loadPropertiesFile()) return false;
    	
    	// Get default and template gsw files
    	gswDefaultPath = rootFolder + CONFIG_FOLDER + GSW_DEFAULT_FILE;
    	gswTemplatePath = rootFolder + CONFIG_FOLDER + GSW_TEMPLATE_FILE;
    	
    	// Load last gsw file
    	String gswPath = prop.get("FILE_GSW", "").trim();
    	File gswFile = new File(gswPath);
    	if (!gswFile.exists()) {
        	// Get default gsw path
            gswPath = gswDefaultPath;
        	gswFile = new File(gswPath);  
    		Utils.getLogger().info("Loading default .gsw file: " + gswPath);	        	
        	if (!gswFile.exists()){
                Utils.showError("gsw_default_notfound", gswPath);    
                return false;
        	}
    	}
    	setGswPath(gswPath);
    	
    	return true;
		
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
    
    
    // Load Properties files
    public static boolean loadPropertiesFile() {

    	String configFile = CONFIG_FILE + "_" + MINOR_VERSION + ".properties";
    	configPath = rootFolder + CONFIG_FOLDER + configFile;
    	Utils.getLogger().info("Versioned properties file: "+configPath);  

        // If versioned properties file not exists, try to load default one instead	
        File file = new File(configPath);
        if (!file.exists()) {
        	configFile = CONFIG_FILE + ".properties";
        	configPath = rootFolder + CONFIG_FOLDER + configFile;
        	Utils.getLogger().info("Default properties file: "+configPath);   
            file = new File(configPath);
        }
        
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

    	if (gswPath.equals("")) {
    		gswPath = rootFolder + CONFIG_FOLDER + GSW_DEFAULT_FILE;
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

	
}