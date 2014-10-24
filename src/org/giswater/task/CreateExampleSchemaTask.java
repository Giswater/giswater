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

import java.awt.Cursor;
import java.io.File;

import javax.swing.SwingWorker;

import org.giswater.controller.MenuController;
import org.giswater.dao.HecRasDao;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.frame.MainFrame;
import org.giswater.util.Utils;


public class CreateExampleSchemaTask extends SwingWorker<Void, Void> {
	
	private MainFrame mainFrame;
	private MenuController controller;
	private String waterSoftware;
	private String schemaName;
	private String sridValue;
	private boolean status;
	
	
	public CreateExampleSchemaTask(String waterSoftware, String schemaName, String sridValue) {
		this.waterSoftware = waterSoftware;
		this.schemaName = schemaName;
		this.sridValue = sridValue;
	}
	
	public void setParentPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public void setController(MenuController controller) {
		this.controller = controller;	
	}

	
    @Override
    public Void doInBackground() { 
		
		setProgress(1);
		
		// Check if schema already exists
		if (MainDao.checkSchema(schemaName)) {
			String msg = "Project '"+schemaName+"' already exists.\nDo you want to overwrite it?";
			int res = Utils.confirmDialog(mainFrame, msg, "Create example project");
			if (res != 0) return null; 
			MainDao.deleteSchema(schemaName);
		}
		
		// Set wait cursor
		mainFrame.ppFrame.getPanel().enableControlsText(false);
		mainFrame.setCursorFrames(new Cursor(Cursor.WAIT_CURSOR));
		
		if (waterSoftware.equals("hecras")) {
			status = HecRasDao.createSchemaHecRas(waterSoftware, schemaName, sridValue);
		}
		else {
			status = MainDao.createSchema(waterSoftware, schemaName, sridValue);
		}
		if (status) {
			MainDao.setSchema(schemaName);
			if (MainDao.updateSchema()) {
				String sql = "INSERT INTO "+schemaName+".inp_project_id VALUES ('example "+waterSoftware+"', 'giswater software', '')";
				Utils.getLogger().info(sql);
				MainDao.executeSql(sql, false);
				sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date)" +
					" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+waterSoftware+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";
				Utils.getLogger().info(sql);
				MainDao.executeSql(sql, false);
				// Once schema has been created, load data 
				try {			
					String folderRoot = new File(".").getCanonicalPath() + File.separator;				
					// From sample .sql file					
					String filePath = folderRoot+"samples/sample_"+waterSoftware+".sql";	 
			    	Utils.getLogger().info("Reading file: "+filePath); 				
			    	String content = Utils.readFile(filePath);
					Utils.logSql(content);		
					// Last SQL script. So commit all process
					boolean result = MainDao.executeSql(content, true);		
					if (!result) {
						MainDao.deleteSchema(schemaName);
						return null;
					}
					if (waterSoftware.equals("hecras")) {				
						// Trough Load Raster
						String rasterName = "sample_mdt.asc";	 						
						String rasterPath = folderRoot+"samples/"+rasterName;	 						
						if (HecRasDao.loadRaster(schemaName, rasterPath, rasterName)) {
							String msg = Utils.getBundleString("schema_creation_completed") + ": " + schemaName;
							MainClass.mdi.showMessage(msg);
						}						
					}	
					else {
						String msg = Utils.getBundleString("schema_creation_completed") + ": " + schemaName;
						MainClass.mdi.showMessage(msg);
					}
				} catch (Exception e) {
					MainDao.deleteSchema(schemaName);
		            Utils.showError(e);
				}
			}
			else {
				MainDao.deleteSchema(schemaName);
				Utils.logError("Error updateSchema. Schema could not be created");
			}		
		}
		else {
			MainDao.deleteSchema(schemaName);
			Utils.logError("Error createSchema. Schema could not be created");
		}

		// Refresh view
		mainFrame.ppFrame.getPanel().enableControlsText(true);
		mainFrame.setCursorFrames(new Cursor(Cursor.DEFAULT_CURSOR));
		controller.gswEdit();
		mainFrame.updateEpaFrames();
		
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