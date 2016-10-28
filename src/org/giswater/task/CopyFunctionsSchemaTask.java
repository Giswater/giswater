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

import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.util.Utils;


public class CopyFunctionsSchemaTask extends ParentSchemaTask {
	
	private String folderRoot;


	public CopyFunctionsSchemaTask(String waterSoftware, String currentSchemaName, String schemaName) {
		super(waterSoftware, schemaName);
		this.currentSchemaName = currentSchemaName;
		this.folderRoot = Utils.getAppPath()+File.separator+"model";				
	}
	
	
    @Override
    public Void doInBackground() { 
		
    	setProgress(1);
    	
    	// Disable view
    	Utils.setPanelEnabled(parentPanel, false);
    	
    	// Set path
    	status = true;
		String folderPath = this.folderRoot+File.separator+"fct";
		if (!processFolder(folderPath, FILE_PATTERN_FCT)) return null;
		
		folderPath = this.folderRoot+File.separator+"trg";
		if (!processFolder(folderPath, FILE_PATTERN_TRG)) return null;
		
		String sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date) VALUES ('"+
			MainDao.getGiswaterVersion()+"', '"+waterSoftware+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";			
		Utils.logInfo(sql);
		// Last SQL script. So commit all process
		MainDao.executeSql(sql, true);
		MainDao.resetSchemaVersion();			
		
		// Refresh view
		controller.selectSourceType(false);			
    	Utils.setPanelEnabled(parentPanel, true);
    	parentPanel.setSelectedSchema(schemaName);
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage(Utils.getBundleString("MainDao.project_updated")); 
    		MainClass.mdi.updateConnectionInfo();    		
    	}
    	else {
    		MainClass.mdi.showError(Utils.getBundleString("MainDao.project_not_updated"));
    	}
		
    }

    
}