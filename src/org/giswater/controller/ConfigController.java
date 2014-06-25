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

import java.io.File;
import java.lang.reflect.Method;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.MainDao;
import org.giswater.gui.panel.ConfigPanel;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class ConfigController {

	private ConfigPanel view;
    private PropertiesMap prop;
	
	
	public ConfigController(ConfigPanel dbPanel) {
		this.view = dbPanel;	
        this.prop = MainDao.getPropertiesFile();
	    view.setController(this);        
    	setDefaultValues();    	
	}
	
	
    private void setDefaultValues(){
    	
		view.setDbAdminFile(prop.get("FILE_DBADMIN"));
		view.setAutoConnect(prop.get("AUTOCONNECT_POSTGIS"));
		view.setAutoStart(prop.get("AUTOSTART_POSTGIS"));
		view.setOpenInp(prop.get("OPEN_INP"));
		view.setOpenRpt(prop.get("OPEN_RPT"));
		view.setOverwriteInp(prop.get("OVERWRITE_INP"));
		view.setOverwriteRpt(prop.get("OVERWRITE_RPT"));
		view.setSqlLog(prop.get("SQL_LOG"));
		view.setSridQuestion(prop.get("SRID_QUESTION"));
		view.setLoadRaster(prop.get("LOAD_RASTER"));
		view.setCheckUpdates(prop.get("AUTO_CHECK_UPDATES", "false"));
        String aux = prop.get("LOG_FOLDER_SIZE", "10");
        Integer size = 10;
        try{
	        size = Integer.parseInt(aux);
        }
        catch (NumberFormatException e){
        	String msg = "Value of parameter LOG_FOLDER_SIZE is not valid. It must be a number";
        	Utils.logError(msg);
        }        
		view.setLogFolderSize(size);
		
    }
       
	
	public void action(String actionCommand) {
		
		Method method;
		try {
			if (Utils.getLogger() != null){
				Utils.getLogger().info(actionCommand);
			}
			method = this.getClass().getMethod(actionCommand);
			method.invoke(this);	
		} catch (Exception e) {
			if (Utils.getLogger() != null){			
				Utils.logError(e);
			} else{
				Utils.showError(e);
			}
		}
		
	}	
	
	
	public void configAccept(){
		
		// Update properties file getting parameteres from view	 
		prop.put("FILE_DBADMIN", view.getDgAdminFile());
		prop.put("AUTOCONNECT_POSTGIS", view.getAutoConnect().toString());
		prop.put("AUTOSTART_POSTGIS", view.getAutoStart().toString());		
		prop.put("OPEN_INP", view.getOpenInp());	
		prop.put("OPEN_RPT", view.getOpenRpt());		
		prop.put("SQL_LOG", view.getSqlLog());				
		prop.put("SRID_QUESTION", view.getSridQuestion());		
		prop.put("LOAD_RASTER", view.getLoadRaster());		
		prop.put("AUTO_CHECK_UPDATES", view.getCheckUpdates());		
		prop.put("LOG_FOLDER_SIZE", view.getLogFolderSize());		
		prop.put("OVERWRITE_INP", view.getOverwriteInp());		
		prop.put("OVERWRITE_RPT", view.getOverwriteRpt());		

		// Close frame
		view.getFrame().setVisible(false);
		
	}	
	
	

    public void chooseFileDbAdmin() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("EXE extension file", "exe");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_dbadmin"));
        File fileProp = new File(prop.get("FILE_DBADMIN", System.getProperty("user.home")));	
        chooser.setCurrentDirectory(fileProp.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".exe";
                file = new File(path);
            }
            view.setDbAdminFile(file.getAbsolutePath());            
        }

    }       
    
    
    public void openLogFolder() {
    	Utils.openFile(Utils.getLogFolder());
    }
	
    
}