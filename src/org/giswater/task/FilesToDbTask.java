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

import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.panel.DevToolboxPanel;
import org.giswater.util.Utils;


public class FilesToDbTask extends ParentSchemaTask {
	
	
	private DevToolboxPanel panel;


	public FilesToDbTask(String waterSoftware, String currentSchemaName, String schemaName) {
		super(waterSoftware, schemaName);
		this.currentSchemaName = currentSchemaName;
	}
	

	public void setPanel(DevToolboxPanel view) {
		this.panel = view;		
	}
	
	
    @Override
    public Void doInBackground() { 
		
    	status = true;
    	
    	setProgress(1);
    	
    	// Disable view
    	Utils.setPanelEnabled(panel, false);
    	
		// Execute SQL's that its name contains '_fk' (corresponding to Foreign Keys)
		//status = copyFunctions(this.softwareAcronym, FILE_PATTERN_FK);
		//if (!status) return null;	
		// Execute SQL's that its name contains 'view' (corresponding to views)
		//status = copyFunctions(this.softwareAcronym, FILE_PATTERN_VIEW);
		//if (!status) return null;	
    	
		// Execute SQL's that its name contains 'fct' (corresponding to functions)
		status = copyFunctions(this.softwareAcronym, FILE_PATTERN_FCT);
		if (!status) return null;	
		// Execute SQL's that its name contains 'trg' (corresponding to trigger functions)
		status = copyFunctions(this.softwareAcronym, FILE_PATTERN_TRG);	
		if (!status) return null;	
		
		// Last SQL script. So commit all process
		String sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date) VALUES ('"+
			MainDao.getGiswaterVersion()+"', '"+waterSoftware+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";			
		Utils.logInfo(sql);
		MainDao.executeSql(sql, true);
		MainDao.resetSchemaVersion();			
		
		// Refresh view	
    	Utils.setPanelEnabled(panel, true);
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage(Utils.getBundleString("DbToFiles.success")); 
    		MainClass.mdi.updateConnectionInfo();    		
    	}
    	else {
    		MainClass.mdi.showError(Utils.getBundleString("DbToFiles.success"));
    	}
		
    }

    
}