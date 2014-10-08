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
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.MainDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class HecRasController extends AbstractController{

	private HecRasPanel view;
	private MainFrame mainFrame;
    private PropertiesMap gswProp;
    private File fileSdf;
    private File fileAsc;    
    private String userHomeFolder;

    
    public HecRasController(HecRasPanel view, MainFrame mainFrame) {
    	
    	this.view = view;	
    	this.mainFrame = mainFrame;
        this.gswProp = MainDao.getGswProperties();
    	this.userHomeFolder = System.getProperty("user.home");
	    view.setControl(this);         	
    	setDefaultValues();
    	    	
	}

    
    private void setDefaultValues(){
    	
    	fileSdf = new File(gswProp.getProperty("HECRAS_FILE_SDF", userHomeFolder));
    	view.setFileSdf(fileSdf.getAbsolutePath());
    	fileAsc = new File(gswProp.getProperty("HECRAS_FILE_ASC", userHomeFolder));
		if (fileAsc.exists()) {
			view.setFileAsc(fileAsc.getAbsolutePath());
		}	
		
    }
	
	
	public void closePanel(){
		view.getFrame().setVisible(false);
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
        File file = new File(gswProp.getProperty("HECRAS_FILE_ASC", userHomeFolder));	
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
        gswProp.put("HECRAS_FILE_ASC", fileAsc.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }

    
    public void loadRaster(){
    	
		// Check ASC file is set
		if (!getFileAsc()) {
			Utils.showError("file_asc_not_selected");
			return;
		}
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	
		String schemaName = MainDao.getSchema();
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
		String schemaName = MainDao.getSchema();
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
	
	
    public void gswEdit() {

        try {
        	mainFrame.ppFrame.setVisible(true);
        	mainFrame.ppFrame.setMaximum(true);
		} catch (PropertyVetoException e) {
			Utils.logError(e);
		}
        
    }
    
	
}