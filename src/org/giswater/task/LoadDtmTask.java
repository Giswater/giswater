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


public class LoadDtmTask extends SwingWorker<Void, Void> {
	
	private String schemaName;
	private String rasterPath;
	private String rasterName;
	private boolean status;
	
	
	public LoadDtmTask(String schemaName, String rasterPath, String rasterName) {
		this.schemaName = schemaName;
		this.rasterPath = rasterPath;
		this.rasterName = rasterName;
	}
	
	
	private boolean loadRaster() {
		
		String srid = MainDao.getTableSrid(schemaName, "mdt").toString();
		String logFolder = Utils.getLogFolder();
		String fileSql = logFolder + rasterName.replace(".asc", ".sql");
		
		// Check if DTM table already exists
		if (MainDao.checkTableHasData(schemaName, "mdt")) {
			String msg = "DTM table already loaded. Do you want to overwrite it?";
			int res = Utils.confirmDialog(msg);
			if (res != 0) {
				return false;
			}			
		}
		
		// Set content of .bat file
		String bin = MainDao.getBin();
		String user = MainDao.getUser();
		String host = MainDao.getHost();
		String port = MainDao.getPort();
		String db = MainDao.getDb();
		String aux = "\""+bin+"raster2pgsql\" -d -s "+srid+" -I -C -M \""+rasterPath+"\" -F -t 100x100 "+schemaName+".mdt > \""+fileSql+"\"";
		aux+= "\n";
		aux+= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+" -c \"drop table if exists "+schemaName+".mdt\";";
		aux+= "\n";		
		aux+= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+" -f \""+fileSql+"\" > \""+logFolder+"raster2pgsql.log\"";
		aux+= "\ndel " + fileSql;
		aux+= "\nexit";				
		Utils.getLogger().info(aux);

        // Fill and execute .bat File	
		File batFile = new File(logFolder + "raster2pgsql.bat");        
		Utils.fillFile(batFile, aux);    		
		Utils.openFile(batFile.getAbsolutePath());
		
    	return true;
    	
	}
	
	
    @Override
    public Void doInBackground() { 
		
    	setProgress(1);
    	status = loadRaster();
		return null;
    	
    }

    
    public void done() {
    	
    	if (status) {
    		MainClass.mdi.showMessage("Please, wait until processing window closes...", 20000);
    	}
    	else {
    		MainClass.mdi.showError("DTM file could not be loaded");
    	}
    	//MainClass.mdi.setProgressBarEnd();
		
    }

    
}