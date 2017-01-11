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
package org.giswater.controller;

import org.giswater.dao.MainDao;
import org.giswater.gui.panel.DevToolboxPanel;
import org.giswater.task.DbToFilesTask;
import org.giswater.task.FilesToDbTask;


public class DevToolboxController extends AbstractController {

	private DevToolboxPanel view;
	
	
	public DevToolboxController(DevToolboxPanel panel) {
		this.view = panel;	
	    view.setController(this);          	
	}

	
	public void filesToDb() {
		
		// Get current schema names
		String currentSchemaName = MainDao.getSchema();
		if (currentSchemaName.equals("")) return;		
		
		// Execute task: FilesToDbTask
		FilesToDbTask task = new FilesToDbTask(MainDao.getWaterSoftware(), currentSchemaName, currentSchemaName, "mainOptions");
        task.setPanel(view);		
        task.addPropertyChangeListener(this);
        task.execute();
		
	}
	
	
	public void dbToFiles() {
		
		// Get current schema names
		String currentSchemaName = MainDao.getSchema();
		if (currentSchemaName.equals("")) return;		
		
		// Execute task: CopyFunctionsSchema
		DbToFilesTask task = new DbToFilesTask(MainDao.getWaterSoftware(), currentSchemaName, currentSchemaName);
        task.setPanel(view);
        task.addPropertyChangeListener(this);
        task.execute();
		
	}	 
	

	
	public void customFilesToDb() {
		
		// Get current schema names
		String currentSchemaName = MainDao.getSchema();
		if (currentSchemaName.equals("")) return;		
		
		// Execute task: FilesToDbTask
		FilesToDbTask task = new FilesToDbTask(MainDao.getWaterSoftware(), currentSchemaName, currentSchemaName, "customOptions");
        task.setPanel(view);		
        task.addPropertyChangeListener(this);
        task.execute();
		
	}	
	
	
	public void closePanel() {
		view.getFrame().setVisible(false);
	}	   
	
	
	public void checkAll() {
		selectChecks(view.chkCheckAll.isSelected());
	}
	
	
	private void selectChecks(boolean isSelected) {
		view.chkFk.setSelected(isSelected);
		view.chkFunctions.setSelected(isSelected);
		view.chkTriggers.setSelected(isSelected);
		view.chkViews.setSelected(isSelected);
		view.chkRules.setSelected(isSelected);
		view.chkValueDefault.setSelected(isSelected);
	}
	
	
	public void customCheckAll() {
		selectCustomChecks(view.chkCustomCheckAll.isSelected());
	}
	
	
	private void selectCustomChecks(boolean isSelected) {
		view.chkCustomFk.setSelected(isSelected);
		view.chkCustomFunctions.setSelected(isSelected);
		view.chkCustomTriggers.setSelected(isSelected);
		view.chkCustomViews.setSelected(isSelected);
		view.chkCustomRules.setSelected(isSelected);
		view.chkCustomValueDefault.setSelected(isSelected);
		view.chkCustomOther.setSelected(isSelected);
		view.chkCustomRoles.setSelected(isSelected);
	}

    
}