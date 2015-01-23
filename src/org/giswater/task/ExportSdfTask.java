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

import javax.swing.SwingWorker;

import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.util.Utils;


public class ExportSdfTask extends SwingWorker<Void, Void> {
	
	private String schemaName;
	private File fileSdf;
	private boolean MA;
	private boolean IA;
	private boolean Levees;
	private boolean BO;
	private boolean Manning;
	private Integer result;
	
	
	public ExportSdfTask(String schemaName, File fileSdf, boolean maSelected, boolean iaSelected, boolean leveesSelected, boolean boSelected, boolean manningSelected) {
		this.schemaName = schemaName;
		this.fileSdf = fileSdf;
		this.MA = maSelected;
		this.IA = iaSelected;
		this.Levees = leveesSelected;
		this.BO = boSelected;
		this.Manning = manningSelected;
	}


	private Integer createSdfFile() {
			
		String pathSdf = fileSdf.getAbsolutePath();
		String sql = "SELECT "+schemaName+".gr_export_geo('"+pathSdf+"', "+MA+", "+IA+", "+Levees+", "+BO+", "+Manning+");";
		Utils.logSql(sql);
		return Integer.parseInt(MainDao.queryToString(sql));
	        
	}
	
	
    @Override
    public Void doInBackground() { 
		
    	setProgress(1);
    	result = createSdfFile();
		return null;
    	
    }

    
    public void done() {
    	
		if (result == 0) {
			MainClass.mdi.showMessage("sdf_ok");
		}
		else {
			// TODO: Get error from table and show to the user
			String msg = MainDao.getErrorMessage();
			MainClass.mdi.showError(msg);
		}
		
    }

    
}