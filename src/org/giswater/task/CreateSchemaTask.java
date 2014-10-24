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

import javax.swing.SwingWorker;

import org.giswater.controller.NewProjectController;
import org.giswater.dao.HecRasDao;
import org.giswater.dao.MainDao;
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
	
	
    @Override
    public Void doInBackground() { 
		
		setProgress(1);
		
		// Check if schema already exists
		if (MainDao.checkSchema(schemaName)) {
			String msg = "Project '"+schemaName+"' already exists.\nDo you want to overwrite it?";
			int res = Utils.confirmDialog(parentPanel, msg, "Create Project");
			if (res != 0) return null; 
			MainDao.deleteSchema(schemaName);
		}
		
    	// Close view and disable parent view
		controller.closeProject();
    	Utils.setPanelEnabled(parentPanel, false);
    	
		if (waterSoftware.equals("HECRAS")) {
			status = HecRasDao.createSchemaHecRas(waterSoftware, schemaName, sridValue);	
		}
		else {
			status = MainDao.createSchema(waterSoftware, schemaName, sridValue);	
		}
		if (status) {
			MainDao.setSchema(schemaName);
			if (MainDao.updateSchema()) {
				String sql = "INSERT INTO "+schemaName+".inp_project_id VALUES ('"+title+"', '"+author+"', '"+date+"')";
				Utils.getLogger().info(sql);
				MainDao.executeSql(sql, false);
				sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date)" +
					" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+waterSoftware+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";
				Utils.getLogger().info(sql);
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
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage("schema_creation_completed");
    	}
    	else {
    		MainClass.mdi.showError("Schema could not be created");
    	}
		
    }

    
}