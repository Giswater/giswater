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
	
	
	public CreateSchemaTask(String waterSoftware, String schemaName, String sridValue) {
		super(waterSoftware, schemaName, sridValue);
	}
	
	
	public void setController(NewProjectController controller){
		this.newProjectController = controller;
	}
	
	
	public void setParams(String title, String author, String date) {
		this.title = title;
		this.author = author;
		this.date = date;	
	}
	
	
	public boolean createSchema(String softwareAcronym) {
		
		boolean status = true;
		
		try {
			
			String folderRootPath = new File(".").getCanonicalPath()+File.separator+"sql"+File.separator;
			String folderPath = "";
			
			// Process selected software folder
			folderPath = folderRootPath+softwareAcronym+File.separator;
			if (!processFolder(folderPath)) return false;
			
			// Process 'utils' folder
			folderPath = folderRootPath+"utils"+File.separator;
			if (!processFolder(folderPath)) return false;
			
			// Process language folders: parameter 'softwareAcronym' and 'utils'
			String folderLocale = folderRootPath+"i18n"+File.separator+locale+File.separator;		
			folderPath = folderLocale+softwareAcronym+File.separator;
			if (!processFolder(folderPath)) return false;
			folderPath = folderLocale+"utils"+File.separator;
			if (!processFolder(folderPath)) return false;
			
		} catch (FileNotFoundException e) {
			Utils.showError(e);
			status = false;
		} catch (IOException e) {
			Utils.showError(e);
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
		newProjectController.closeProject();
    	Utils.setPanelEnabled(parentPanel, false);
    	
    	// Create schema of selected software
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