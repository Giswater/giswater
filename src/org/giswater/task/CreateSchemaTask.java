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

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.giswater.controller.NewProjectController;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.util.Utils;


public class CreateSchemaTask extends SwingWorker<Void, Void> {
	
	private ProjectPreferencesPanel parentPanel;
	private NewProjectController controller;
	private String waterSoftware;
	private String schemaName;
	private String sridValue;
	private String title;
	private String author;
	private String date;
	private boolean status;
	
	
	public CreateSchemaTask(String waterSoftware, String schemaName, String sridValue) {
		this.waterSoftware = waterSoftware;
		this.schemaName = schemaName;
		this.sridValue = sridValue;
	}
	
	public void setParentPanel(ProjectPreferencesPanel parentPanel){
		this.parentPanel = parentPanel;
	}
	
	public void setController(NewProjectController controller){
		this.controller = controller;
	}
	
	public void setParams(String title, String author, String date) {
		this.title = title;
		this.author = author;
		this.date = date;	
	}
	
	
	public boolean processFile(String filePath) throws IOException {
		
		// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter. __USER__ for PostGIS user
		String content = Utils.readFile(filePath);
		if (content.equals("")) return false;	    
		content = content.replace("SCHEMA_NAME", schemaName);
		content = content.replace("SRID_VALUE", sridValue);
		content = content.replace("__USER__", PropertiesDao.getGswProperties().get("POSTGIS_USER"));					
		Utils.logSql(content);
		return MainDao.executeSql(content, false);		
		
	}
	
	
	public boolean processFolder(String folderPath) {
		
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
				Utils.getLogger().info("Processing file: "+filePath);
				status = processFile(filePath);
				if (!status) return false;
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
	
	
	public boolean createSchema(String softwareAcronym) {
		
		boolean status = true;
		String filePath = "";
		
		try {
			// Process selected software folder
			String folderRootPath = new File(".").getCanonicalPath()+File.separator+"sql"+File.separator+softwareAcronym+File.separator;
			if (!processFolder(folderRootPath)) return false;
			// Process 'utils' folder
			String folderUtilsPath = new File(".").getCanonicalPath()+File.separator+"sql"+File.separator+"utils"+File.separator;
			if (!processFolder(folderUtilsPath)) return false;
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
		
		setProgress(1);
		
		// Check if schema already exists
		if (MainDao.checkSchema(schemaName)) {
			String msg = Utils.getBundleString("CreateSchemaTask.project")+schemaName+Utils.getBundleString("CreateSchemaTask.overwrite_it"); //$NON-NLS-1$ //$NON-NLS-2$
			int res = Utils.showYesNoDialog(parentPanel, msg, Utils.getBundleString("CreateSchemaTask.project_create")); //$NON-NLS-1$
			if (res != JOptionPane.YES_OPTION) return null; 
			MainDao.deleteSchema(schemaName);
		}
		
    	// Close view and disable parent view
		controller.closeProject();
    	Utils.setPanelEnabled(parentPanel, false);
    	
    	// Create schema of selected software
    	String softwareAcronym = null;
    	if (waterSoftware.equals("EPANET")) {
    		softwareAcronym = "ws";
    	}
    	else if (waterSoftware.equals("EPASWMM")) {
    		softwareAcronym = "ud";
    	}
		status = createSchema(softwareAcronym);	
		if (status) {
			MainDao.setSchema(schemaName);
			if (MainDao.updateSchema()) {
				String sql = "INSERT INTO "+schemaName+".inp_project_id VALUES ('"+title+"', '"+author+"', '"+date+"')";
				Utils.logInfo(sql);
				MainDao.executeSql(sql, false);
				sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date)" +
					" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+waterSoftware+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";
				Utils.logInfo(sql);
				// Last SQL script. So commit all process
				MainDao.executeSql(sql, true);
			}
			else {
				MainDao.deleteSchema(schemaName);
				status = false;
			}
		}
		else {
			MainDao.deleteSchema(schemaName);
			status = false;
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
    		if (waterSoftware.equals("HECRAS")) {
    			parentPanel.getController().enableHecras(true);
    		}
    	}
    	else {
    		MainClass.mdi.showError(Utils.getBundleString("CreateSchemaTask.project_not_created")); //$NON-NLS-1$
    	}
		
    }

    
}