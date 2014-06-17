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

import java.awt.Cursor;
import java.io.File;
import java.lang.reflect.Method;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.MainDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class HecRasController{

	private HecRasPanel view;
    private PropertiesMap prop;
    private PropertiesMap gswProp;
    private File fileSdf;
    private File fileAsc;    
    private String userHomeFolder;
	public MainFrame mainFrame;

    
    public HecRasController(HecRasPanel view, MainFrame mf) {
    	
    	this.mainFrame = mf;
    	this.view = view;	
        this.prop = MainDao.getPropertiesFile();
        this.gswProp = MainDao.getGswProperties();
    	this.userHomeFolder = System.getProperty("user.home");
	    view.setControl(this);         	
    	setDefaultValues();
    	    	
	}

    
    private void setDefaultValues(){
    	
    	fileSdf = new File(gswProp.getProperty("FILE_SDF", userHomeFolder));
    	view.setFileSdf(fileSdf.getAbsolutePath());
    	fileAsc = new File(gswProp.getProperty("FILE_ASC", userHomeFolder));
		if (fileAsc.exists()) {
			view.setFileAsc(fileAsc.getAbsolutePath());
		}	
		view.setSchemaModel(MainDao.getSchemas("HECRAS"));
		
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
	
	
	public void closePanel(){
		view.getFrame().setVisible(false);
	}
	
	
	public void openDatabase(){
		mainFrame.openDatabase();
	}
	
	
	public void isConnected(){

		// Check if we already are connected
		if (MainDao.isConnected()){
			view.enableButtons(true);
			view.setSchemaModel(MainDao.getSchemas("HECRAS"));
	    	view.setSelectedSchema(MainDao.getGswProperties().get("HECRAS_SCHEMA"));			
		} 
		else{
			view.enableButtons(false);
			view.setSchemaModel(null);				
		}
		//mainFrame.enableCatalog(false);
		
	}	
	
	
	public void schemaChanged(){
		MainDao.setSchema(view.getSelectedSchema());
	}
	
	
    public void chooseFileSdf() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("SDF extension file", "sdf");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_sdf"));
        File file = new File(gswProp.getProperty("FILE_SDF", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
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
        File file = new File(gswProp.getProperty("FILE_ASC", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
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
        gswProp.put("FILE_SDF", fileSdf.getAbsolutePath());
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
        gswProp.put("FILE_ASC", fileAsc.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }
    
    
    // Clear gisras schema info    
    public void clearData(){
    	    	
		String schemaName = view.getSelectedSchema();    	
		String msg = Utils.getBundleString("empty_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(msg);  
        if (res == 0){
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	if (MainDao.clearData(schemaName)){
        		Utils.showMessage(view, "data_cleared");
        	}
        }    	
    	
    }

    
	private String getUserSrid(String defaultSrid){
		
		String sridValue = "";
		Boolean sridQuestion = Boolean.parseBoolean(prop.get("SRID_QUESTION"));
		if (sridQuestion){
			sridValue = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_srid"), defaultSrid);
			if (sridValue == null){
				return "";
			}
		}
		else{
			sridValue = defaultSrid;
		}
		return sridValue.trim();
		
	}
	
	
	public void createSchema(){
		createSchema("");
	}	
	
    
	public void createSchema(String defaultSchemaName){
		
		String schemaName = defaultSchemaName;
		if (defaultSchemaName.equals("")){
			schemaName = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_schema_name"), "schema_name");
			if (schemaName == null){
				return;
			}
			schemaName = schemaName.trim().toLowerCase();
			if (schemaName.equals("")){
				Utils.showError("schema_valid_name");
				return;
			}
		}
		
		// Ask user to set SRID?
		String defaultSrid = prop.get("SRID_DEFAULT", "25831");		
		String sridValue = getUserSrid(defaultSrid);

		if (sridValue.equals("")){
			return;
		}
		Integer srid;
		try{
			srid = Integer.parseInt(sridValue);
		} catch (NumberFormatException e){
			Utils.showError("error_srid");
			return;
		}
		MainDao.getGswProperties().put("SRID_USER", sridValue);
		MainDao.savePropertiesFile();

		boolean isSridOk = MainDao.checkSrid(srid);
		if (!isSridOk && srid != 0){
			String msg = "SRID "+srid+" " +Utils.getBundleString("srid_not_found")+"\n" +
				Utils.getBundleString("srid_valid");			
			Utils.showError(msg);
			return;
		}
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));		
		boolean status = MainDao.createSchemaHecRas("hecras", schemaName, sridValue);
		if (status && defaultSchemaName.equals("")){
			Utils.showMessage(view, "schema_creation_completed");
		}
		else if (status && !defaultSchemaName.equals("")){
			Utils.showMessage(view, "schema_truncate_completed");
		}
		view.setSchemaModel(MainDao.getSchemas("HECRAS"));	
		schemaChanged();
		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
    
	
	public void deleteSchema(){
		
		String schemaName = view.getSelectedSchema();
		String msg = Utils.getBundleString("delete_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(msg);        
        if (res == 0){
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	MainDao.deleteSchema(schemaName);
        	view.setSchemaModel(MainDao.getSchemas("HECRAS"));
    		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    		Utils.showMessage(view, "schema_deleted", "");
        }
        
	}	
    
    
    public void loadRaster(){
    	
		// Check ASC file is set
		if (!getFileAsc()) {
			Utils.showError("file_asc_not_selected");
			return;
		}
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	
		String schemaName = view.getSelectedSchema();
		String filePath = fileAsc.getAbsolutePath();
		String fileName = fileAsc.getName();
    	MainDao.loadRaster(schemaName, filePath, fileName);  	
        	
    }
    
    
    // Create HEC-RAS file    
	public void exportSdf() {
   	
		// Check SDF file is set
		if (!getFileSdf()) {
			Utils.showError("file_sdf_not_selected");
			return;
		}

		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        
		String schemaName = view.getSelectedSchema();
		String fileName = fileSdf.getName();
		MainDao.createSdfFile(schemaName, fileName);
		
		// Copy file from Postgis Data Directory to folder specified by the user
		String auxIn, auxOut;
		String folderIn = gswProp.getProperty("POSTGIS_DATA");
		auxIn = folderIn + File.separator + fileName;
		auxOut = fileSdf.getAbsolutePath();
		boolean ok = Utils.copyFile(auxIn, auxOut);
		if (!ok){
			Utils.showError("sdf_error");
		}
		else{
			Utils.showMessage(view, "sdf_ok", fileSdf.getAbsolutePath());
		}
		
	}
    
	
}