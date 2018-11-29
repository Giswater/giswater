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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.giswater.controller.ProjectPreferencesController;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class ParentSchemaTask extends SwingWorker<Void, Void> {
	
	protected ProjectPreferencesPanel parentPanel;
	protected ProjectPreferencesController controller;
	protected String waterSoftware;
	protected String schemaName;
	protected String sridValue;	
	protected String locale;
	protected String currentSchemaName;	
	protected boolean status;
	protected PropertiesMap prop;		
	
	protected String folderRootPath;
	protected String folderSoftware;
	protected String folderLocale;
	protected String folderLocaleEn;
	protected String folderUtils;
	protected String folderUpdates;
	protected String folderFct;
	protected String folderTrg;
	protected String folderFctUtils;
	protected String folderViews;	
	
	protected final String FILE_PATTERN_FK = "fk";	
	protected final String FILE_PATTERN_DDL = "ddl";	
	protected final String FILE_PATTERN_DML = "dml";	
	protected final String FILE_PATTERN_FCT = "fct";
	protected final String FILE_PATTERN_TRG = "trg";	
	protected final String FILE_PATTERN_VIEW = "view";	
	protected final String FILE_PATTERN_RULES = "rules";	
	protected final String FILE_PATTERN_VDEFAULT = "vdefault";	
	protected final String FILE_PATTERN_OTHER = "other";
	protected final String FILE_PATTERN_ROLES = "roles";
	
	protected final String FILE_PATTERN_FCT_GW = "fct_gw";	
	protected final String FILE_PATTERN_FCT_OM = "fct_om";	
	protected final String FILE_PATTERN_FCT_SMW = "fct_smw";
	protected final String FILE_PATTERN_FCT_UTIL = "fct_util";
	
	protected final String BYTE_ORDER_MARK = "\uFEFF";
	protected final Integer UPDATE_FIRST_VERSION = 31100;	
	
	
	public ParentSchemaTask() {	}
	
	public ParentSchemaTask(String waterSoftware, String schemaName, String sridValue) {
		
		this.waterSoftware = waterSoftware;
		if (waterSoftware.equals("EPANET")) {
			this.waterSoftware = "ws";
		}
		else if (waterSoftware.equals("EPASWMM")) {
			this.waterSoftware = "ud";
		}
		this.schemaName = schemaName;
		this.sridValue = sridValue;
		this.folderRootPath = Utils.getAppPath()+"sql"+File.separator;
    	setProperties();
    	
	}
	
	
	public ParentSchemaTask(String waterSoftware, String schemaName) {
		this(waterSoftware, schemaName, MainDao.getSrid(schemaName));
	}
	
	protected void setProperties() {
		this.prop = PropertiesDao.getPropertiesFile();
		this.locale = this.prop.get("LANGUAGE", "").toLowerCase();
		String folderPath = folderRootPath+waterSoftware+"_export_fct";
		this.folderFct = this.prop.get("FOLDER_FCT", folderPath);
		this.folderFctUtils = this.prop.get("FOLDER_FCT_UTILS", folderPath);
		folderPath = folderRootPath+waterSoftware+"_export_trg";		
		this.folderTrg = this.prop.get("FOLDER_TRG", folderPath);
		folderPath = folderRootPath+waterSoftware+"_export_view";
		this.folderViews = this.prop.get("FOLDER_VIEWS", folderPath);
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
		
		String fileExtension = FilenameUtils.getExtension(filePath);	
		if (!fileExtension.equals("sql")) {
			Utils.getLogger().info("Not a SQL file: "+filePath);				
			return true;
		}
		
		// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter. __USER__ for PostGIS user
		Utils.getLogger().info("Processing file: "+filePath);		
		String content = Utils.readFile(filePath);
		if (content.equals("")) return false;
		content = content.replace(BYTE_ORDER_MARK, "");
		content = content.replace("SCHEMA_NAME", schemaName);
		content = content.replace("SRID_VALUE", sridValue);
		content = content.replace("__USER__", PropertiesDao.getGswProperties().get("POSTGIS_USER"));					
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
				return true;
			}
			Arrays.sort(files);
			for (File file : files) {			
				filePath = file.getPath();
				// If parameter 'filePattern' is set:
				// Process only files that contains that filePattern in its filename
				if (file.isFile()) {
					if (!filePattern.equals("")) {
						if (filePath.contains(filePattern)) {
							status = processFile(filePath);
							if (!status) return false;
						}
					} 
					else {
						status = processFile(filePath);
						if (!status) return false;
					}
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
	
	
	protected boolean copyFunctions(String waterSoftware, String filePattern) {
				
		// Get execution path		
		String folderRootPath = Utils.getAppPath()+File.separator+"sql"+File.separator;
		
		// Process selected software folder
		String folderPath = folderRootPath+waterSoftware+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;
		
		// Process 'fct' folder
		folderPath = folderRootPath+waterSoftware+File.separator+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;
		
		// Process 'view' folder
		folderPath = folderRootPath+waterSoftware+File.separator+FILE_PATTERN_VIEW+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;				
		
		// Process 'trg' folder
		folderPath = folderRootPath+waterSoftware+File.separator+FILE_PATTERN_TRG+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;	
		
		// Process 'fk' folder
		folderPath = folderRootPath+waterSoftware+File.separator+FILE_PATTERN_FK+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;	
		
		// Process 'rules' folder
		folderPath = folderRootPath+waterSoftware+File.separator+FILE_PATTERN_RULES+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;	
		
		// Process 'utils' folder
		folderPath = folderRootPath+"utils"+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;
		
		// Process 'utils/fct' folder
		folderPath = folderRootPath+"utils"+File.separator+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;
		
		// Process 'utils/view' folder
		folderPath = folderRootPath+"utils"+File.separator+FILE_PATTERN_VIEW+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;		
		
		// Process 'utils/trg' folder
		folderPath = folderRootPath+"utils"+File.separator+FILE_PATTERN_TRG+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;	
		
		// Process 'utils/fk' folder
		folderPath = folderRootPath+"utils"+File.separator+FILE_PATTERN_FK+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;	
		
		// Process 'utils/rules' folder
		folderPath = folderRootPath+"utils"+File.separator+FILE_PATTERN_RULES+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;	
		
		return true;
		
	}	
	
	
	protected boolean copyCustomFunctions(String folderRootPath, String filePattern) {
		
		String folderPath = "";
		
		// Process selected software folder
		folderPath = folderRootPath+File.separator+waterSoftware+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;	
		
		// Process 'fct' folder
		folderPath = folderRootPath+File.separator+waterSoftware+File.separator+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;
		
		// Process 'trg' folder
		folderPath = folderRootPath+File.separator+waterSoftware+File.separator+FILE_PATTERN_TRG+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;				

		// Process 'view' folder
		folderPath = folderRootPath+File.separator+waterSoftware+File.separator+FILE_PATTERN_VIEW+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;		
		
		// Process 'utils' folder
		folderPath = folderRootPath+File.separator+"utils"+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;
		
		// Process 'utils/fct' folder
		folderPath = folderRootPath+File.separator+"utils"+File.separator+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;
		
		// Process 'utils/trg' folder
		folderPath = folderRootPath+File.separator+"utils"+File.separator+FILE_PATTERN_TRG+File.separator;
		if (!processFolder(folderPath, filePattern)) return false;			
		
		return true;
		
	}	
	

	protected void emptyFolder(String folderPath) {
		
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		try {
			FileUtils.cleanDirectory(folder);
		} catch (Exception e) {
			Utils.logError(e);
		}     		
		
	}
	
	
	// Iterate over all files inside selected folder
	public boolean processUpdateFolder(String folderPath) {
		
		boolean status = true;
		
		File folderRoot = new File(folderPath);
		File[] files = folderRoot.listFiles();
		if (files == null || files.length == 0) {
			Utils.logError("Folder not found or without files: "+folderPath);				
			return true;
		}		
		Arrays.sort(files);
		for (File file : files) {
	    	String content;
			try {
				String fileExtension = FilenameUtils.getExtension(file.getAbsolutePath());	
				if (fileExtension.equals("sql")) {
					Utils.getLogger().info("Executing file: "+file.getAbsolutePath());
					content = Utils.readFile(file.getAbsolutePath());
					content = content.replace("SCHEMA_NAME", schemaName);
					content = content.replace("SRID_VALUE", MainDao.getSrid(schemaName));					
					status = MainDao.executeSql(content, false);
					// Abort process if one script fails
					if (!status) return false;
				}
				else {
					Utils.getLogger().info("Not a SQL file: "+file.getAbsolutePath());				
				}
			} catch (IOException e) {
				Utils.logError(e);
			}
		}	
		
		return status;
		
	}
	
	
    @Override
    public Void doInBackground() {     	
		return null;
    }

    
    public void done() { }

    
}