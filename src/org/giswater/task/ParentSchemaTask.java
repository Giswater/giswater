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
package org.giswater.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.SwingWorker;

import org.giswater.controller.ProjectPreferencesController;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.util.Utils;


public class ParentSchemaTask extends SwingWorker<Void, Void> {
	
	protected ProjectPreferencesPanel parentPanel;
	protected ProjectPreferencesController controller;
	protected String waterSoftware;
	protected String schemaName;
	protected String sridValue;	
	protected String softwareAcronym;
	protected String locale;
	protected String currentSchemaName;	
	protected boolean status;
	
	protected final String FILE_PATTERN_FK = "_fk";	
	protected final String FILE_PATTERN_FCT = "fct";	
	protected final String FILE_PATTERN_TRG = "trg";	
	protected final String FILE_PATTERN_VIEW = "_view";	
	
	
	public ParentSchemaTask() {	}
	
	public ParentSchemaTask(String waterSoftware, String schemaName, String sridValue) {
		
		this.waterSoftware = waterSoftware;
		this.schemaName = schemaName;
		this.sridValue = sridValue;
    	if (waterSoftware.toUpperCase().equals("EPANET")) {
    		softwareAcronym = "ws";
    	}
    	else if (waterSoftware.toUpperCase().equals("EPASWMM")) {
    		softwareAcronym = "ud";
    	}
    	else {
    		softwareAcronym = "hecras";
    	}
		locale = PropertiesDao.getPropertiesFile().get("LANGUAGE", "en");    	
    	
	}
	
	
	public ParentSchemaTask(String waterSoftware, String schemaName) {
		this(waterSoftware, schemaName, "");
	}
	
	
	public void setParentPanel(ProjectPreferencesPanel parentPanel) {
		this.parentPanel = parentPanel;
	}
	
	public void setController(ProjectPreferencesController controller) {
		this.controller = controller;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public boolean processFile(String filePath) throws IOException {
		
		// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter. __USER__ for PostGIS user
		String content = Utils.readFile(filePath);
		if (content.equals("")) return false;	    
		content = content.replace("SCHEMA_NAME", schemaName);
		content = content.replace("SRID_VALUE", sridValue);
		content = content.replace("__USER__", PropertiesDao.getGswProperties().get("POSTGIS_USER"));					
		Utils.logSql(content);
		return MainDao.executeSql(content, false, filePath);		
		
	}
	
	
	public boolean processFolder(String folderPath) {
		return processFolder(folderPath, "");
	}
	
	
	public boolean processFolder(String folderPath, String filePattern) {
		
		boolean status = true;
		String filePath = "";
		
		try {		
			File folderRoot = new File(folderPath);
			File[] files = folderRoot.listFiles();
			if (files == null) {
				Utils.logError("Folder not found or without files: "+folderPath);				
				return false;
			}
			Arrays.sort(files);
			for (File file : files) {			
				filePath = file.getPath();
				// If parameter 'filePattern' is set:
				// Process only files that contains that filePattern in its filename
				if (!filePattern.equals("")) {
					if (filePath.contains(filePattern)) {
						Utils.getLogger().info("Processing file: "+filePath);
						status = processFile(filePath);
						if (!status) return false;
					}
				} 
				else {
					Utils.getLogger().info("Processing file: "+filePath);
					status = processFile(filePath);
					if (!status) return false;
				}
			}
		} catch (FileNotFoundException e) {
			Utils.showError("inp_error_notfound", filePath);
			status = false;
		} catch (IOException e) {
			Utils.showError(e, filePath);
			status = false;			
		}		
		return status;
		
	}	
	
	
	protected boolean copyFunctions(String softwareAcronym, String filePattern) {
		
		boolean status = true;
		String filePath = "";
		
		try {
			
			String folderRootPath = new File(".").getCanonicalPath()+File.separator+"sql"+File.separator;
			String folderPath = "";
			
			// Process selected software folder
			folderPath = folderRootPath+softwareAcronym+File.separator;
			if (!processFolder(folderPath, filePattern)) return false;
			
			// Process 'utils' folder
			folderPath = folderRootPath+"utils"+File.separator;
			if (!processFolder(folderPath, filePattern)) return false;
			
		} catch (FileNotFoundException e) {
			Utils.showError("inp_error_notfound", filePath);
			status = false;
		} catch (IOException e) {
			Utils.showError(e, filePath);
			status = false;			
		}
		
		return status;
		
	}	
		
	
	
    @Override
    public Void doInBackground() {     	
		return null;
    }

    
    public void done() { }

    
}