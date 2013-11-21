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
 *   David Erill <daviderill79@gmail.com>
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
	    view.setControl(this);        
    	setDefaultValues();    	
    	
	}
	
	
    private void setDefaultValues(){
		view.setPostgisBinFolder(prop.get("FOLDER_BIN"));
		view.setSwmmFile(prop.get("FILE_SWMM"));
		view.setEpanetFile(prop.get("FILE_EPANET"));
		view.setAutoConnect(prop.get("AUTOCONNECT_POSTGIS"));
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
		
		String postgis, swmm, epanet;
		Boolean isChecked;
		
		// Get parameteres from view
		postgis = view.getPostgisBinFolder();		
		swmm = view.getSwmmFile();
		epanet = view.getEpanetFile();
		isChecked = view.getAutoConnect();
		
		// Update properties file
		prop.put("FOLDER_BIN", postgis);
		prop.put("FILE_SWMM", swmm);
		prop.put("FILE_EPANET", epanet);
		prop.put("AUTOCONNECT_POSTGIS", isChecked.toString());
		MainDao.savePropertiesFile();
		
		//view.close();
		
	}	
	
	
	public void chooseFolderPostgis() {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(Utils.getBundleString("folder_bin"));
		File file = new File(prop.get("FOLDER_BIN", System.getProperty("user.home")));
		chooser.setCurrentDirectory(file);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = chooser.getSelectedFile();
			view.setPostgisBinFolder(folder.getAbsolutePath());
		}

	}
	
	
    public void chooseFileSwmm() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("EXE extension file", "exe");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_swmm"));
        File fileProp = new File(prop.get("FILE_SWMM", System.getProperty("user.home")));	
        chooser.setCurrentDirectory(fileProp.getParentFile());
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".inp";
                file = new File(path);
            }
            view.setSwmmFile(file.getAbsolutePath());            
        }

    }
	
	
    public void chooseFileEpanet() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("EXE extension file", "exe");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_epanet"));
        File fileProp = new File(prop.get("FILE_EPANET", System.getProperty("user.home")));	
        chooser.setCurrentDirectory(fileProp.getParentFile());
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".inp";
                file = new File(path);
            }
            view.setEpanetFile(file.getAbsolutePath());            
        }

    }    
	
    
}