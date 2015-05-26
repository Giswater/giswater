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

import javax.swing.SwingWorker;

import org.giswater.controller.ProjectPreferencesController;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.util.Utils;


public class CopySchemaTask extends SwingWorker<Void, Void> {
	
	private ProjectPreferencesPanel parentPanel;
	private ProjectPreferencesController controller;
	private String schemaName;
	private String newSchemaName;
	private boolean status;
	
	
	public CopySchemaTask(String schemaName, String newSchemaName) {
		this.schemaName = schemaName;
		this.newSchemaName = newSchemaName;
	}
	
	public void setParentPanel(ProjectPreferencesPanel parentPanel) {
		this.parentPanel = parentPanel;
	}
	
	public void setController(ProjectPreferencesController controller) {
		this.controller = controller;
	}
	
	
    @Override
    public Void doInBackground() { 
		
    	setProgress(1);
    	
    	// Disable view
    	Utils.setPanelEnabled(parentPanel, false);

		String sql = "SELECT "+schemaName+".clone_schema('"+schemaName+"', '"+newSchemaName+"')";
		Utils.logSql(sql);
		MainClass.mdi.showMessage(Utils.getBundleString("copy_schema_process"), true);		
		status = MainDao.executeSql(sql, true);
		if (status){
			// Now we have to execute functrigger.sql
			try {
				String folderRoot = new File(".").getCanonicalPath()+File.separator;
				String filePath = folderRoot+"sql"+File.separator+MainDao.getWaterSoftware()+"_functrigger.sql";
				String content = Utils.readFile(filePath);
				content = content.replace("SCHEMA_NAME", newSchemaName);
				Utils.logSql(content);
				if (MainDao.executeSql(content, true)){
					controller.selectSourceType(false);
				}
			} catch (IOException e) {
				Utils.logError(e);
				status = false;
			}	
		}
		
		// Refresh view
    	Utils.setPanelEnabled(parentPanel, true);
    	parentPanel.setSelectedSchema(newSchemaName);
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage(Utils.getBundleString("project_copied_successfuly"));    		
    	}
    	else {
    		MainClass.mdi.showError(Utils.getBundleString("project_not_copied"));
    	}
		
    }

    
}