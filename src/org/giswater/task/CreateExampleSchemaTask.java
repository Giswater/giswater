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

import javax.swing.JOptionPane;

import org.giswater.controller.MenuController;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.frame.MainFrame;
import org.giswater.util.Utils;


public class CreateExampleSchemaTask extends ParentSchemaTask {
	
	private MainFrame mainFrame;
	private MenuController menuController;

	
	public CreateExampleSchemaTask(String waterSoftware, String schemaName, String sridValue) {
		super(waterSoftware, schemaName, sridValue);
	}
	
	
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;		
	}
	
	public void setMenuController(MenuController menuController) {
		this.menuController = menuController;		
	}
	
	
    @Override
    public Void doInBackground() { 
		
		setProgress(1);
		
		// Check if schema already exists
		if (MainDao.checkSchema(schemaName)) {
			String msg = Utils.getBundleString("CreateExampleSchemaTask.project")+schemaName+Utils.getBundleString("CreateExampleSchemaTask.overwrite_it");
			int res = Utils.showYesNoDialog(mainFrame, msg, Utils.getBundleString("CreateExampleSchemaTask.create_example"));
			if (res != JOptionPane.YES_OPTION) return null; 
			MainDao.deleteSchema(schemaName);
		}
		
		// Set wait cursor
		mainFrame.ppFrame.getPanel().enableControlsText(false);
		mainFrame.setCursorFrames(new Cursor(Cursor.WAIT_CURSOR));
		
    	// Create schema of selected software
    	CreateSchemaTask cst = new CreateSchemaTask(waterSoftware, schemaName, sridValue);
    	// Locale must be set to 'EN'
    	cst.setLocale("EN");
		status = cst.createSchema(waterSoftware);	
		if (status) {
			MainDao.setSchema(schemaName);
			if (MainDao.updateSchema()) {
				// Insert information into table version
				insertVersion(false);
				// Once schema has been created, load example data 
				try {
					String folderRoot = Utils.getAppPath();
					String folderPath = folderRoot+"sql"+File.separator+"example"+File.separator+waterSoftware+File.separator;
					status = processFolder(folderPath);
					if (status) {
						MainDao.commit();
						String msg = Utils.getBundleString("schema_creation_completed") + ": " + schemaName;
						MainClass.mdi.showMessage(msg);
					}
					else {
						MainDao.rollback();
						MainClass.mdi.showError(Utils.getBundleString("CreateExampleSchemaTask.error_create_project")); //$NON-NLS-1$						
					}
				} catch (Exception e) {
					status = false;
					MainDao.rollback();
		            Utils.showError(e);
				}
			}
			else {
				status = false;
				MainDao.rollback();
				MainClass.mdi.showError(Utils.getBundleString("CreateExampleSchemaTask.error_update_project")); //$NON-NLS-1$
			}		
		}
		else {
			status = false;
			MainDao.rollback();
			MainClass.mdi.showError(Utils.getBundleString("CreateExampleSchemaTask.error_create_project")); //$NON-NLS-1$
		}

		// Refresh view
		mainFrame.ppFrame.getPanel().enableControlsText(true);
		mainFrame.setCursorFrames(new Cursor(Cursor.DEFAULT_CURSOR));
		menuController.gswEdit();
		mainFrame.updateEpaFrames();
		mainFrame.ppFrame.getPanel().setSelectedSchema(schemaName);
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage("schema_creation_completed");
    	}
    	else {
    		MainClass.mdi.showError(Utils.getBundleString("CreateExampleSchemaTask.error_create_project2")); //$NON-NLS-1$
    	}
		
    }
    
    
}