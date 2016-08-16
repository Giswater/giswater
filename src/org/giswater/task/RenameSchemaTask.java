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
import org.giswater.util.Utils;


public class RenameSchemaTask extends ParentSchemaTask {
	
	
	public RenameSchemaTask(String waterSoftware, String currentSchemaName, String schemaName) {
		super(waterSoftware, schemaName);
		this.currentSchemaName = currentSchemaName;
	}
	
		
    @Override
    public Void doInBackground() { 
		
    	setProgress(1);
    	
		String sql = "ALTER SCHEMA "+currentSchemaName+" RENAME TO "+schemaName;
		Utils.logSql(sql);
		if (MainDao.executeUpdateSql(sql, true)) {
			// Rename schema 'audit' (if exists)
			if (MainDao.checkSchema(currentSchemaName+"_audit")) {
				sql = "ALTER SCHEMA "+currentSchemaName+"_audit RENAME TO "+schemaName+"_audit";
				Utils.logSql(sql);
				MainDao.executeUpdateSql(sql, true);	
			}
			// Execute SQL's that its name contains '_fct_' (corresponding to functions)
			status = renameFunctions(this.softwareAcronym);
			
			// Refresh view
			controller.selectSourceType(false);
			Utils.setPanelEnabled(parentPanel, true);	
			parentPanel.setSelectedSchema(schemaName);
		}
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage(Utils.getBundleString("project_renamed_ok"));    		
    	}
    	else {
    		MainClass.mdi.showError(Utils.getBundleString("project_not_copied"));
    	}
		
    }

    
}