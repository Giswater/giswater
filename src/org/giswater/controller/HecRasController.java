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

import java.awt.Cursor;
import java.io.File;
import java.lang.reflect.Method;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.MainDao;
import org.giswater.gui.MainFrame;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class HecRasController{

	private HecRasPanel view;
    private PropertiesMap prop;
    private File fileSdf;
    private File fileAsc;    
    private String projectName;
    private String userHomeFolder;
	public MainFrame mainFrame;

    
    public HecRasController(HecRasPanel view, MainFrame mf) {
    	
    	this.mainFrame = mf;
    	this.view = view;	
        this.prop = MainDao.getPropertiesFile();
    	this.userHomeFolder = System.getProperty("user.home");
	    view.setControl(this);         	
    	setDefaultValues();
    	    	
	}

    
    private void setDefaultValues(){
    	
    	fileSdf = new File(prop.getProperty("FILE_SDF", userHomeFolder));
    	view.setFileSdf(fileSdf.getAbsolutePath());
    	fileAsc = new File(prop.getProperty("FILE_ASC", userHomeFolder));
		if (fileAsc.exists()) {
			view.setFileAsc(fileAsc.getAbsolutePath());
		}	
		projectName = prop.getProperty("PROJECT_NAME");
		view.setNewSchemaName(projectName);
		
    }
   

	public void action(String actionCommand) {
		
		Method method;
		try {
			if (Utils.getLogger() != null){
				Utils.getLogger().info(actionCommand);
			}
			method = this.getClass().getMethod(actionCommand);
			method.invoke(this);	
			view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));			
		} catch (Exception e) {
			view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			if (Utils.getLogger() != null){			
				Utils.logError(e);
			} else{
				Utils.showError(e);
			}
		}
		
	}	
	
	
	public void openDatabase(){
		mainFrame.openDatabase();
	}
	
	
	public void isConnected(){

		// Check if we already are connected
		if (MainDao.isConnected){
			view.enableButtons(true);
			view.setSchema(MainDao.getSchemas());
		} 
		else{
			view.enableButtons(false);
			view.setSchema(null);				
		}
		
	}	
	
	
	public void schemaChanged(){
		MainDao.setSchema(view.getSchema());
	}
	
	
    public void chooseFileSdf() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("SDF extension file", "sdf");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_sdf"));
        File file = new File(prop.getProperty("FILE_SDF", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileSdf = chooser.getSelectedFile();
            String path = fileSdf.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".sdf";
                fileSdf = new File(path);
            }
            view.setFileSdf(fileSdf.getAbsolutePath());            
        }

    }
    
    
    public void chooseFileAsc() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("ASC extension file", "asc");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_asc"));
        File file = new File(prop.getProperty("FILE_ASC", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	fileAsc = chooser.getSelectedFile();
            String path = fileAsc.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".asc";
                fileAsc = new File(path);
            }
            view.setFileAsc(fileAsc.getAbsolutePath());            
        }

    }    
    

    private boolean getFileSdf(){
    	
        String path = view.getFileSdf();
        if (path.equals("")){
            return false;        	
        }
        if (path.lastIndexOf(".") == -1) {
            path += ".sdf";
        }
        fileSdf = new File(path);        
        prop.put("FILE_SDF", fileSdf.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }
    
    
    private boolean getFileAsc(){
    	
        String path = view.getFileAsc();
        if (path.equals("")){
            return false;        	
        }
        if (path.lastIndexOf(".") == -1) {
            path += ".asc";
        }
        fileAsc = new File(path);        
        prop.put("FILE_ASC", fileAsc.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }
    
    
    // Clear gisras schema info    
    public void clearData(){
    	    	
        int res = JOptionPane.showConfirmDialog(view, "clear_data?", "gisRAS", JOptionPane.YES_NO_OPTION);
        if (res == 0){
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	if (MainDao.clearData()){
        		Utils.showMessage("data_cleared");
        	}
        }    	
    	
    }

 
    public void saveCase(){
    	
    	String schemaName = view.getNewSchemaName();        
    	if (schemaName.equals("")){
    		Utils.showError("enter_schema_name");
    		view.focusSchemaName();
    		return;
    	}
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
    	MainDao.saveCase(schemaName);
    	view.setSchema(MainDao.getSchemas());
    	Utils.showMessage("case_saved", schemaName);    	
        	
    }

    
    public void loadCase(){
    	
    	String schemaName = view.getSchema();
        if (schemaName.equals("")){
            Utils.showError("any_schema_selected");
            return;
        } 
		int res = Utils.confirmDialog("load_schema?");        
        if (res == 0){        
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	if (MainDao.loadCase(schemaName)){
        		Utils.showMessage("case_loaded", schemaName);
        	}
        }
        	
    }
    
    
    public void deleteCase(){
    	
    	String schemaName = view.getSchema();
        if (schemaName.equals("")){
            Utils.showError("any_schema_selected");
            return;
        }          	
        else if (schemaName.toLowerCase().equals("gisras") || schemaName.toLowerCase().equals("original")){
            Utils.showError("delete_cannot", schemaName);
            return;
        }
		int res = Utils.confirmDialog("delete_schema?");      
        if (res == 0){
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	MainDao.deleteCase(schemaName);
        	view.setSchema(MainDao.getSchemas());
        	Utils.showMessage("case_deleted", schemaName);
        }   
        	
    }    
    
    
    public void loadRaster(){
    	
		// Comprobamos que se ha especificado fichero .asc
		if (!getFileAsc()) {
			Utils.showError("asc_file");
			return;
		}
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
		String fileName = fileAsc.getAbsolutePath();
    	if (MainDao.loadRaster(fileName)){  	
    		//Utils.showMessage("Raster loaded", fileName, "gisRAS");
    	}
        	
    }
    
    
    // Create HEC-RAS file    
	public void exportSdf() {
   	
		// Comprobamos que se ha especificado fichero .sdf
		if (!getFileSdf()) {
			Utils.showError("sdf_set");
			return;
		}

		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
		String fileName = fileSdf.getName();
		MainDao.createSdfFile(fileName);
		
		// Copiamos fichero de la carpeta de Postgis a la carpeta especificada por el usuario
		String auxIn, auxOut;
		String folderIn = prop.getProperty("POSTGIS_DATA");
		auxIn = folderIn + File.separator + fileName;
		auxOut = fileSdf.getAbsolutePath();
		boolean ok = Utils.copyFile(auxIn, auxOut);
		if (!ok){
			Utils.showError("sdf_error");
		}
		else{
			Utils.showMessage("sdf_ok", fileSdf.getAbsolutePath());
		}
		
	}
    
	
}