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
	
	
	
	public static boolean createSchema(String softwareName, String schemaName, String srid) {
		
		boolean status = false;
		String sql = "CREATE schema "+schemaName;
		if (!MainDao.executeUpdateSql(sql, false, true)){
			MainDao.rollback();
			return status;	
		}
		String filePath = "";
		String content = "";
    	
		try {

	    	String folderRoot = new File(".").getCanonicalPath()+File.separator;			
			filePath = folderRoot+"sql"+File.separator+softwareName+".sql";
	    	content = Utils.readFile(filePath);
			
	    	// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter
			content = content.replace("SCHEMA_NAME", schemaName);
			content = content.replace("SRID_VALUE", srid);
			Utils.logSql(content);

			if (MainDao.executeSql(content, false)) {
				filePath = folderRoot+"sql"+File.separator+softwareName+"_value_domain.sql";
		    	content = Utils.readFile(filePath);
				content = content.replace("SCHEMA_NAME", schemaName);		   
				Utils.logSql(content);
				if (MainDao.executeSql(content, false)) {
					filePath = folderRoot+"sql"+File.separator+softwareName+"_value_default.sql";
			    	content = Utils.readFile(filePath);
					content = content.replace("SCHEMA_NAME", schemaName);
					Utils.logSql(content);
					if (MainDao.executeSql(content, false)) {
						filePath = folderRoot+"sql"+File.separator+softwareName+"_functrigger.sql";
				    	content = Utils.readFile(filePath);
						content = content.replace("SCHEMA_NAME", schemaName);
						Utils.logSql(content);
						status = MainDao.executeSql(content, false);
					}					
				}
			}
			
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", filePath);
        } catch (IOException e) {
            Utils.showError(e, filePath);
        }
		return status;
		
	}
	
	
	public static boolean createSchemaHecRas(String softwareName, String schemaName, String srid) {
		
		boolean status = false;
		String filePath = "";
		try {		
			filePath = Utils.getAppPath()+"sql"+File.separator+softwareName+".sql";
			String content = Utils.readFile(filePath);
			if (content.equals("")) return false;
	    	// Replace SCHEMA_NAME for schemaName parameter. __USER__ for user
			content = content.replace("SCHEMA_NAME", schemaName);
			content = content.replace("SRID_VALUE", srid);			
			content = content.replace("__USER__", PropertiesDao.getGswProperties().get("POSTGIS_USER"));		
			Utils.logSql(content);
			status = MainDao.executeUpdateSql(content, false, true);
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", filePath);
        } catch (IOException e) {
            Utils.showError(e, filePath);
		}
		return status;
		
	}
	
	
    @Override
    public Void doInBackground() { 
		
		setProgress(1);
		
		// Check if schema already exists
		if (MainDao.checkSchema(schemaName)) {
			String msg = "Project '"+schemaName+"' already exists.\nDo you want to overwrite it?";
			int res = Utils.showYesNoDialog(parentPanel, msg, "Create Project");
			if (res != JOptionPane.YES_OPTION) return null; 
			MainDao.deleteSchema(schemaName);
		}
		
    	// Close view and disable parent view
		controller.closeProject();
    	Utils.setPanelEnabled(parentPanel, false);
    	
		if (waterSoftware.equals("HECRAS")) {
			status = createSchemaHecRas(waterSoftware, schemaName, sridValue);	
		}
		else {
			status = createSchema(waterSoftware, schemaName, sridValue);	
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
		parentPanel.setSelectedSchema(schemaName);
		
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