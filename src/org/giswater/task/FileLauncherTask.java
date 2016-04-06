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

import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.util.Utils;


public class FileLauncherTask extends SwingWorker<Void, Void> {
	
	private String content;
	private String schemaName;
	private String sridValue;
	private boolean status;
	
	
	public FileLauncherTask(String content, String schemaName, String sridValue) {
		this.content = content;
		this.schemaName = schemaName;
		this.sridValue = sridValue;		
	}
	
		
    @Override
    public Void doInBackground() { 
		
		setProgress(1);
			
		// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter
		status = true;
		content = content.replace("SCHEMA_NAME", schemaName);
		content = content.replace("SRID_VALUE", sridValue);
		Utils.logSql(content);
		Exception e = MainDao.executeSql(content, false, 0);
		if (e != null) {
			status = false;
			Utils.showError(Utils.getBundleString("MenuController.errors_found"), e.getMessage()); //$NON-NLS-1$
		}
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
			MainClass.mdi.showMessage(Utils.getBundleString("MenuController.file_executed_successfully")); //$NON-NLS-1$
    	}
		
    }

    
}