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
import java.io.IOException;

import javax.swing.JOptionPane;

import org.giswater.controller.NewProjectController;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.util.Utils;


public class CreateSchemaTask extends ParentSchemaTask {
	
	private NewProjectController newProjectController;
	private String title;
	private String author;
	private String date;
	private Boolean enableConstraints = true;
	
	
	public CreateSchemaTask(String waterSoftware, String schemaName, String sridValue) {
		super(waterSoftware, schemaName, sridValue);
	}
	
	
	public void setController(NewProjectController controller){
		this.newProjectController = controller;
	}
	
	
	public void setParams(String title, String author, String date, Boolean enableConstraints) {
		this.title = title;
		this.author = author;
		this.date = date;	
		this.enableConstraints = enableConstraints;	
	}
	
	
	public boolean createSchema(String softwareAcronym) {
		
		boolean status = true;
			
		this.folderSoftware = folderRootPath+softwareAcronym+File.separator;
		this.folderLocale = folderRootPath+"i18n"+File.separator+locale+File.separator;
		this.folderUtils = folderRootPath+"utils"+File.separator;
		this.folderUpdates = folderRootPath+"updates"+File.separator;
		String folderPath = "";
		
		// Process folder 'utils/ddl'
		folderPath = folderUtils+FILE_PATTERN_DDL+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		// Process folder '<waterSoftware>/ddl'
		folderPath = folderSoftware+FILE_PATTERN_DDL+File.separator;
		if (!processFolder(folderPath)) return false;
		
		// Process folder 'updates/<waterSoftware>' folder
		folderPath = folderUpdates+waterSoftware+File.separator;
		if (!processUpdateFolder(folderPath)) return false;
		
		// Process folder 'updates/utils' folder
		folderPath = folderUpdates+"utils"+File.separator;
		if (!processUpdateFolder(folderPath)) return false;
		
		// Process folder 'i18n/<locale>/<waterSoftware>'
		folderPath = folderLocale+softwareAcronym+File.separator;
		if (!processFolder(folderPath)) return false;
		
		// Process folder 'i18n/<locale>/utils'
		folderPath = folderLocale+"utils"+File.separator;
		if (!processFolder(folderPath)) return false;
		
		// Process folder 'updates/i18n/<locale>/<watersoftware>'
		folderPath = folderUpdates+"i18n"+File.separator+locale+File.separator+softwareAcronym+File.separator;
		if (!processUpdateFolder(folderPath)) return false;			
		
		// Process folder '<waterSoftware>/fct' folder
		folderPath = folderSoftware+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath)) return false;
		
		// Process folder '<waterSoftware>/view' folder
		folderPath = folderSoftware+FILE_PATTERN_VIEW+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		if (enableConstraints) {	
			// Process folder '<waterSoftware>/trg' folder
			folderPath = folderSoftware+FILE_PATTERN_TRG+File.separator;
			if (!processFolder(folderPath)) return false;
		}

		// Process folder '<waterSoftware>/dml' folder
		folderPath = folderSoftware+FILE_PATTERN_DML+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		if (enableConstraints) {
			// Process folder '<waterSoftware>/fk' folder
			folderPath = folderSoftware+FILE_PATTERN_FK+File.separator;
			if (!processFolder(folderPath)) return false;		
			
			// Process folder '<waterSoftware>/rules' folder
			folderPath = folderSoftware+FILE_PATTERN_RULES+File.separator;
			if (!processFolder(folderPath)) return false;	
		}
		
		// Process folder 'utils/fct' folder
		folderPath = folderUtils+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath)) return false;			
		
		// Process folder 'utils/view' folder
		folderPath = folderUtils+FILE_PATTERN_VIEW+File.separator;
		if (!processFolder(folderPath)) return false;
		
		if (enableConstraints) {			
			// Process folder 'utils/trg' folder
			folderPath = folderUtils+FILE_PATTERN_TRG+File.separator;
			if (!processFolder(folderPath)) return false;	
		}
		
		// Process folder 'utils/dml' folder
		folderPath = folderUtils+FILE_PATTERN_DML+File.separator;
		if (!processFolder(folderPath)) return false;		
		
		if (enableConstraints) {		
			// Process folder 'utils/fk' folder
			folderPath = folderUtils+FILE_PATTERN_FK+File.separator;
			if (!processFolder(folderPath)) return false;		
			
			// Process folder 'utils/rules' folder
			folderPath = folderUtils+FILE_PATTERN_RULES+File.separator;
			if (!processFolder(folderPath)) return false;		
		}
			
		return status;
		
	}
	
	
	private boolean loadBase(String softwareAcronym) {
		
		String folderPath = "";
		String filePath = "";
		
		// Process folder 'utils/ddl'
		folderPath = folderUtils+FILE_PATTERN_DDL+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		// Process folder 'utils/dml'
		folderPath = folderUtils+FILE_PATTERN_DML+File.separator;
		if (!processFolder(folderPath)) return false;		
		
		// Process folder 'utils/fct' folder
		folderPath = folderUtils+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath)) return false;			
		
		// Process folder 'utils/ftrg' folder
		folderPath = folderUtils+FILE_PATTERN_FTRG+File.separator;
		if (!processFolder(folderPath)) return false;
		
		// Process folder '<waterSoftware>/ddl'
		folderPath = folderSoftware+FILE_PATTERN_DDL+File.separator;
		if (!processFolder(folderPath)) return false;
		
		// Process folder '<waterSoftware>/ddlrule'
		folderPath = folderSoftware+FILE_PATTERN_DDLRULE+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		// Process folder '<waterSoftware>/dml' folder
		folderPath = folderSoftware+FILE_PATTERN_DML+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		// Process folder '<waterSoftware>/tablect' folder
		folderPath = folderSoftware+FILE_PATTERN_TABLECT+File.separator;
		if (!processFolder(folderPath)) return false;		

		// Process folder '<waterSoftware>/fct' folder
		folderPath = folderSoftware+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath)) return false;
		
		// Process folder '<waterSoftware>/ftrg folder
		folderPath = folderSoftware+FILE_PATTERN_FTRG+File.separator;
		if (!processFolder(folderPath)) return false;		
		
		// Process folder 'utils/tablect' folder
		folderPath = folderUtils+FILE_PATTERN_TABLECT+File.separator;
		if (!processFolder(folderPath)) return false;
		
		// Process folder 'utils/ddlrule' folder
		folderPath = folderUtils+FILE_PATTERN_DDLRULE+File.separator;
		if (!processFolder(folderPath)) return false;		
		
		// Process file 'i18n/<locale>/<waterSoftware>.sql'
		filePath = folderLocale+softwareAcronym+".sql";
		try {
			File file = new File(filePath);
			if (!file.isFile()){
				filePath = folderLocaleEn+softwareAcronym+".sql";				
			}
			if (!processFile(filePath)) return false;
		} catch (IOException e) {
			Utils.showError(e, filePath);
			status = false;	
		}		
		
		// Process file 'i18n/<locale>/utils.sql'
		filePath = folderLocale+"utils.sql";
		try {
			File file = new File(filePath);
			if (!file.isFile()){
				filePath = folderLocaleEn+"utils.sql";				
			}
			if (!processFile(filePath)) return false;
		} catch (IOException e) {
			Utils.showError(e, filePath);
			status = false;	
		}			
		
		return true;
		
	}
	
	
	private boolean loadViews(String softwareAcronym) {
		
		String folderPath = "";
		
		// Process folder '<waterSoftware>/ddlview'
		folderPath = folderSoftware+FILE_PATTERN_DDLVIEW+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		// Process folder 'utils/ddlview'
		folderPath = folderUtils+FILE_PATTERN_DDLVIEW+File.separator;
		if (!processFolder(folderPath)) return false;			
		
		return true;
		
	}
	
	
	private boolean loadTrg(String softwareAcronym) {
		
		String folderPath = "";
		
		// Process folder 'utils/trg'
		folderPath = folderUtils+FILE_PATTERN_TRG+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		// Process folder '<waterSoftware>/trg'
		folderPath = folderSoftware+FILE_PATTERN_TRG+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		return true;
		
	}
	
	
	private boolean processUpdates(String softwareAcronymn, Integer versionFrom, Integer versionTo){
		
		boolean status = true;

		String folderUpdatePath = "";
		String folderPath = "";
		
		for (int i = versionFrom; i <= versionTo; i++) {
			
			folderUpdatePath = folderRootPath+"updates"+File.separator+"31"+File.separator+i+File.separator;
			File folder = new File(folderUpdatePath);
			if (folder.exists()){			
				Utils.getLogger().info("Processing updates folder: "+folderUpdatePath);
				
				// Process folder 'utils' folder
				folderPath = folderUpdatePath+"utils"+File.separator;
				if (!processUpdateFolder(folderPath)) return false;		
				
				// Process folder '<waterSoftware>' folder
				folderPath = folderUpdatePath+softwareAcronymn+File.separator;
				if (!processUpdateFolder(folderPath)) return false;	
				
				// Process folder 'i18n/<locale>/<waterSoftware>.sql'
				folderPath = folderUpdatePath+"i18n"+File.separator+locale+File.separator;
				folder = new File(folderPath);
				if (!folder.exists()){
					folderPath = folderUpdatePath+"i18n"+File.separator+"en"+File.separator;				
				}
				if (!processUpdateFolder(folderPath)) return false;				
			}
			else {
				Utils.getLogger().info("Folder not found: "+folderUpdatePath);				
			}
			
		}
			
		return status;
		
	}	
	
	
	private boolean loadApi(String softwareAcronym) {
		
		String folderPath = "";
		
		// Process folder 'api/ftrg'
		folderPath = folderApi+FILE_PATTERN_FTRG+File.separator;
		if (!processFolder(folderPath)) return false;	
		
		// Process folder 'api/fct'
		folderPath = folderApi+FILE_PATTERN_FCT+File.separator;
		if (!processFolder(folderPath)) return false;			
		
		return true;
		
	}
	
	
	public boolean createSchemaNew(String softwareAcronym) {
		
		boolean status = true;
			
		this.folderSoftware = folderRootPath+softwareAcronym+File.separator;
		this.folderLocale = folderRootPath+"i18n"+File.separator+locale+File.separator;
		this.folderLocaleEn = folderRootPath+"i18n"+File.separator+"en"+File.separator;		
		this.folderUtils = folderRootPath+"utils"+File.separator;
		this.folderUpdates = folderRootPath+"updates"+File.separator;
		this.folderApi = folderRootPath+"api"+File.separator;		
		
		String giswaterVersion = MainDao.getGiswaterVersion().replace(".", "");
		Integer giswaterVersionInt = Integer.valueOf(giswaterVersion);		
		
		loadBase(softwareAcronym);
		processUpdates(softwareAcronym, 31100, 31100);			
		loadViews(softwareAcronym);
		loadTrg(softwareAcronym);
		processUpdates(softwareAcronym, 31101, giswaterVersionInt);	
		loadApi(softwareAcronym);
			
		return status;
		
	}
	
	
    @Override
    public Void doInBackground() { 
		
		setProgress(1);
		
		// Check if schema already exists
		if (MainDao.checkSchema(schemaName)) {
			String msg = Utils.getBundleString("CreateSchemaTask.project")+schemaName+Utils.getBundleString("CreateSchemaTask.overwrite_it"); //$NON-NLS-1$ //$NON-NLS-2$
			int res = Utils.showYesNoDialog(parentPanel, msg, Utils.getBundleString("CreateSchemaTask.project_create")); //$NON-NLS-1$
			if (res != JOptionPane.YES_OPTION) return null; 
			MainDao.deleteSchema(schemaName);
		}
		
    	// Close view and disable parent view
		newProjectController.closeProject();
    	Utils.setPanelEnabled(parentPanel, false);
    	
    	// Create schema of selected software
    	MainDao.setSchema(schemaName);
		status = createSchemaNew(waterSoftware);	
		if (status) {
			// Insert information into table inp_project_id and version				
			String sql = "INSERT INTO "+schemaName+".inp_project_id VALUES ('"+title+"', '"+author+"', '"+date+"')";
			Utils.logInfo(sql);
			MainDao.executeSql(sql, false);		
			MainDao.insertVersion(true);
		}
		else {
			MainDao.deleteSchema(schemaName);
		}
		
		// Refresh view
    	Utils.setPanelEnabled(parentPanel, true);
		parentPanel.setSchemaModel(MainDao.getSchemas(waterSoftware));	
		parentPanel.setSelectedSchema(schemaName);
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage("schema_creation_completed");
    	}
    	else {
    		MainClass.mdi.showError(Utils.getBundleString("CreateSchemaTask.project_not_created")); //$NON-NLS-1$
    	}
		
    }

    
}