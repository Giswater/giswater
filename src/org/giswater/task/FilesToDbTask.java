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
import org.giswater.gui.panel.DevToolboxPanel;
import org.giswater.util.Utils;


public class FilesToDbTask extends ParentSchemaTask {
	
	
	private DevToolboxPanel panel;
	private String panelName;
	private String customFolder;


	public FilesToDbTask(String waterSoftware, String currentSchemaName, String schemaName, String panelName) {
		super(waterSoftware, schemaName);
		this.currentSchemaName = currentSchemaName;
		this.panelName = panelName;
	}
	

	public void setPanel(DevToolboxPanel view) {
		this.panel = view;		
	}
	
	
    @Override
    public Void doInBackground() { 
		
    	status = false;
    	setProgress(1);
    	
    	// Disable view
    	Utils.setPanelEnabled(panel, false);
    	
    	if (panelName.equals("mainOptions")) {
    		status = mainOptions();
    		if (status) {
    			// Insert information into table version
    			MainDao.insertVersion(true);
    			MainDao.resetSchemaVersion();			
    		}
    	}
    	else if (panelName.equals("customOptions")) {
    		if (getCustomFolder()) {
	    		status = customOptions();
	    		if (status) {
	    			// Insert information into table version
	    			MainDao.insertVersion(true);
	    			MainDao.resetSchemaVersion();			
	    		}
    		}
    	}
    	
		return null;
    	
    }

    
    private boolean mainOptions() {
		
    	// Execute SQL's that its name contains 'fct' (corresponding to functions)
    	if (panel.chkFunctions.isSelected()) {    
    		status = copyFunctions(this.waterSoftware, FILE_PATTERN_FCT);
    		if (!status) return false;
    	}
    	
    	// Execute SQL's that its name contains 'view' (corresponding to views)
    	if (panel.chkViews.isSelected()) {    
    		status = copyFunctions(this.waterSoftware, FILE_PATTERN_VIEW);
    		if (!status) return false;	
    	}
    	
    	// Execute SQL's that its name contains 'trg' (corresponding to trigger functions)
    	if (panel.chkTriggers.isSelected() || panel.chkViews.isSelected()) {    
    		status = copyFunctions(this.waterSoftware, FILE_PATTERN_TRG);	
    		if (!status) return false;	
    	}
    	
    	// Execute SQL's that its name contains 'fk' (corresponding to foreign keys)
    	if (panel.chkFk.isSelected()) {    
    		status = copyFunctions(this.waterSoftware, FILE_PATTERN_FK);	
    		if (!status) return false;	
    	}
    	
    	// Execute SQL's that its name contains 'rules' (corresponding to rules)
    	if (panel.chkRules.isSelected()) {    
    		status = copyFunctions(this.waterSoftware, FILE_PATTERN_RULES);	
    		if (!status) return false;	
    	}
    	
    	// Execute SQL's that its name contains 'vdefault' (corresponding to default values)
    	if (panel.chkValueDefault.isSelected()) {    
    		status = copyFunctions(this.waterSoftware, FILE_PATTERN_VDEFAULT);	
    		if (!status) return false;	
    	}
    	
    	return true;
		
    }
    
    
    private boolean getCustomFolder() {
    	
    	String sql = "SELECT value"+
    			" FROM "+currentSchemaName+".config_param_text"+
    			" WHERE id = 'custom_giswater_folder'";
    	customFolder = MainDao.queryToString(sql);
    	if (customFolder.equals("")) {
    		String msg = "Parameter 'custom_giswater_folder' not defined in table 'config_param_text'";
    		Utils.showMessage(msg);
    		return false;
    	}
    	File file = new File(customFolder);
    	// If not found, maybe it's because the path is relative to SQL folder...
		if (!file.exists()) {
			String customFolder2 = customFolder;
			customFolder = Utils.getAppPath()+"sql"+File.separator+customFolder+File.separator;
			file = new File(customFolder);
			if (!file.exists()) {
				String msg = "Custom folder not found in:\n"+customFolder2+"\nor\n"+customFolder;
				Utils.showMessage(msg);
				return false;
			}
		}
		return true;
    	
    }
    
    
    private boolean customOptions() {
    	
		// Execute SQL's that its name contains 'fct' (corresponding to functions)
    	if (panel.chkCustomFunctions.isSelected()) {    
			status = copyCustomFunctions(customFolder, FILE_PATTERN_FCT);
			if (!status) return false;
    	}
    	
    	// Execute SQL's that its name contains 'view' (corresponding to views)
    	if (panel.chkCustomViews.isSelected()) {    
    		status = copyCustomFunctions(customFolder, FILE_PATTERN_VIEW);
    		if (!status) return false;	
    	}
		
		// Execute SQL's that its name contains 'trg' (corresponding to trigger functions)
    	if (panel.chkCustomTriggers.isSelected() || panel.chkCustomViews.isSelected()) {    
    		status = copyCustomFunctions(customFolder, FILE_PATTERN_TRG);	
    		if (!status) return false;	
    	}
    	
    	// Execute SQL's that its name contains 'fk' (corresponding to foreign keys)
    	if (panel.chkCustomFk.isSelected()) {    
    		status = copyCustomFunctions(customFolder, FILE_PATTERN_FK);	
    		if (!status) return false;	
    	}
    	
    	// Execute SQL's that its name contains 'rules' (corresponding to rules)
    	if (panel.chkCustomRules.isSelected()) {    
    		status = copyCustomFunctions(customFolder, FILE_PATTERN_RULES);	
    		if (!status) return false;	
    	}
    	
    	// Execute SQL's that its name contains 'vdefault' (corresponding to default values)
    	if (panel.chkCustomValueDefault.isSelected()) {    
    		status = copyCustomFunctions(customFolder, FILE_PATTERN_VDEFAULT);	
    		if (!status) return false;	
    	}

    	// Execute SQL's that its name contains 'other'
    	if (panel.chkCustomOther.isSelected()) {    
    		status = copyCustomFunctions(customFolder, FILE_PATTERN_OTHER);	
    		if (!status) return false;	
    	}
    	    	
    	// Execute SQL's that its name contains 'roles'
    	if (panel.chkCustomRoles.isSelected()) {    
    		status = copyCustomFunctions(customFolder, FILE_PATTERN_ROLES);	
    		if (!status) return false;	
    	}
		return true;
		   	
    }
    
    
    public void done() {
    	
    	Utils.setPanelEnabled(panel, true);    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage(Utils.getBundleString("FilesToDb.success")); 
    		MainClass.mdi.updateConnectionInfo();    		
    	}
    	else {
    		MainClass.mdi.showError(Utils.getBundleString("FilesToDb.error"));
    	}
		
    }

    
}