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
import javax.swing.SwingWorker;

import org.giswater.controller.MenuController;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.frame.MainFrame;
import org.giswater.util.Utils;


public class CreateExampleSchemaTask extends SwingWorker<Void, Void> {
	
	private MainFrame mainFrame;
	private MenuController controller;
	private String waterSoftware;
	private String schemaName;
	private String sridValue;
	private boolean status;
	
	
	public CreateExampleSchemaTask(String waterSoftware, String schemaName, String sridValue) {
		this.waterSoftware = waterSoftware;
		this.schemaName = schemaName;
		this.sridValue = sridValue;
	}
	
	public void setParentPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public void setController(MenuController controller) {
		this.controller = controller;	
	}

	
	private boolean loadRaster(String schemaName, String rasterPath, String rasterName) {
		
		String srid = MainDao.getTableSrid(schemaName, "banks").toString();
		String logFolder = Utils.getLogFolder();
		String fileSql = logFolder + rasterName.replace(".tif", ".sql");
		
		// Check if DTM table already exists
		if (MainDao.checkTableHasData(schemaName, "mdt")) {
			String msg = Utils.getBundleString("CreateExampleSchemaTask.dtm_already_loaded"); //$NON-NLS-1$
			int res = Utils.showYesNoDialog(msg);
			if (res != JOptionPane.YES_OPTION) return false;	
		}
		
		// Set content of .bat file
		String bin = MainDao.getBinFolder();
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
		
		// Check if schema already exists
		if (MainDao.checkSchema(schemaName)) {
			String msg = Utils.getBundleString("CreateExampleSchemaTask.project")+schemaName+Utils.getBundleString("CreateExampleSchemaTask.overwrite_it"); //$NON-NLS-1$ //$NON-NLS-2$
			int res = Utils.showYesNoDialog(mainFrame, msg, Utils.getBundleString("CreateExampleSchemaTask.create_example")); //$NON-NLS-1$
			if (res != JOptionPane.YES_OPTION) return null; 
			MainDao.deleteSchema(schemaName);
		}
		
		// Set wait cursor
		mainFrame.ppFrame.getPanel().enableControlsText(false);
		mainFrame.setCursorFrames(new Cursor(Cursor.WAIT_CURSOR));
		
    	// Create schema
		status = CreateSchemaTask.createSchema(waterSoftware, schemaName, sridValue);
		if (status) {
			MainDao.setSchema(schemaName);
			if (MainDao.updateSchema()) {
				String sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date)" +
					" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+waterSoftware+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";
				Utils.getLogger().info(sql);
				MainDao.executeSql(sql, false);
				// Once schema has been created, load data 
				try {			
					String folderRoot = new File(".").getCanonicalPath() + File.separator;				
					// From sample .sql file					
					String filePath = folderRoot+"samples"+File.separator+schemaName+".sql";	 
			    	Utils.getLogger().info(Utils.getBundleString("CreateExampleSchemaTask.reading_file")+filePath); 				 //$NON-NLS-1$
			    	String content = Utils.readFile(filePath);
					Utils.logSql(content);		
					// Last SQL script. So commit all process
					boolean result = MainDao.executeSql(content, true);		
					if (!result) {
						status = false;
						MainDao.deleteSchema(schemaName);
						return null;
					}
					if (waterSoftware.equals("hecras")) {				
						// Trough Load Raster
						String rasterName = "sample_dtm.tif";	 						
						String rasterPath = folderRoot+"samples"+File.separator+rasterName;	 						
						if (loadRaster(schemaName, rasterPath, rasterName)) {
							String msg = Utils.getBundleString("schema_creation_completed") + ": " + schemaName;
							MainClass.mdi.showMessage(msg);
						}						
					}	
					else {
						String msg = Utils.getBundleString("schema_creation_completed") + ": " + schemaName;
						MainClass.mdi.showMessage(msg);
					}
				} catch (Exception e) {
					status = false;					
					MainDao.deleteSchema(schemaName);
		            Utils.showError(e);
				}
			}
			else {
				status = false;
				MainDao.deleteSchema(schemaName);
				MainClass.mdi.showError(Utils.getBundleString("CreateExampleSchemaTask.error_update_project")); //$NON-NLS-1$
			}		
		}
		else {
			status = false;
			MainDao.deleteSchema(schemaName);
			MainClass.mdi.showError(Utils.getBundleString("CreateExampleSchemaTask.error_create_project")); //$NON-NLS-1$
		}

		// Refresh view
		mainFrame.ppFrame.getPanel().enableControlsText(true);
		mainFrame.setCursorFrames(new Cursor(Cursor.DEFAULT_CURSOR));
		controller.gswEdit();
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