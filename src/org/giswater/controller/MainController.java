/*
 * This file is part of INPcom
 * Copyright (C) 2012  Tecnics Associats
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.MainDao;
import org.giswater.gui.MainFrame;
import org.giswater.gui.dialog.OptionsDialog;
import org.giswater.gui.dialog.RaingageDialog;
import org.giswater.gui.dialog.TimesValuesDialog;
import org.giswater.gui.panel.EpaPanel;
import org.giswater.gui.panel.TableWindowPanel;
import org.giswater.model.Model;
import org.giswater.model.ModelDbf;
import org.giswater.model.ModelPostgis;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MainController{

	private EpaPanel view;
    private PropertiesMap prop;
    private File fileInp;
    private File fileRpt;
    private String projectName;
    private boolean exportChecked;
    private boolean execChecked;
    private boolean importChecked;
    
    private String userHomeFolder;
    private ResourceBundle bundleText;
    
	public MainFrame mainFrame;
	private String software;
	
	// DBF only
	private File dirShp;
	private boolean readyShp = false;
	private boolean dbSelected = false;

    
    public MainController(EpaPanel view, MainFrame mf, String software) {
    	
    	this.mainFrame = mf;
    	this.view = view;	
        this.prop = MainDao.getPropertiesFile();
        this.software = software;
	    view.setControl(this);        
    	
    	userHomeFolder = System.getProperty("user.home");
    	this.bundleText = Utils.getBundleText();
    	
    	// Set default values
    	setDefaultValues();
    	    	
	}

    
    private void setDefaultValues(){
    	
    	// DBF
		dirShp = new File(prop.get("FOLDER_SHP", userHomeFolder));
		if (dirShp.exists()) {
			view.setFolderShp(dirShp.getAbsolutePath());
			readyShp = true;
		}
		
    	fileInp = new File(prop.get("FILE_INP"));
		if (fileInp.exists()) {
			view.setFileInp(fileInp.getAbsolutePath());
		}
		fileRpt = new File(prop.get("FILE_RPT"));
		if (fileRpt.exists()) {
			view.setFileRpt(fileRpt.getAbsolutePath());
		}    	
		projectName = prop.get("PROJECT_NAME");
		view.setProjectName(projectName);
			
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
				Utils.logError(e, actionCommand);
			} else{
				Utils.showError(e, actionCommand);
			}
		}
		
	}	
	
	
	// DBF only
	public void chooseFolderShp() {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(bundleText.getString("folder_shp"));
		File file = new File(prop.get("FOLDER_SHP", userHomeFolder));
		chooser.setCurrentDirectory(file);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			dirShp = chooser.getSelectedFile();
			view.setFolderShp(dirShp.getAbsolutePath());
			prop.put("FOLDER_SHP", dirShp.getAbsolutePath());
			MainDao.savePropertiesFile();
			readyShp = true;
		}

	}
	
	
	// Press DBF or Database option
	public void selectSourceType(){

		dbSelected = view.getOptDatabaseSelected();
		if (dbSelected){
			// Check if we already are connected
			if (MainDao.isConnected){
				view.setSchema(MainDao.getSchemas());
				view.setSoftware(MainDao.getAvailableVersions("postgis", software));
				view.enableAccept(true);
			} 
			else{
				mainFrame.openDatabase();
				view.enableAccept(false);
				view.setSchema(null);				
			}
		} else{
			view.setSoftware(MainDao.getAvailableVersions("dbf", software));
			view.enableAccept(true);
		}
		
		// Update view
		view.selectSourceType();
		
	}
	
	
//	public void dialogDatabase(){
//		//DatabaseController inp = new DatabaseController(dialog);
//		DatabaseFrame frame = new DatabaseFrame();
//		
//		frame.setModal(true);
//		frame.setLocationRelativeTo(null);   
//		frame.setVisible(true);		
//	}

	
	public void schemaChanged(){
		MainDao.setSchema(view.getSchema());
	}
	
	
	public void showOptions(){
		ResultSet rs = MainDao.getTableResultset("inp_options");
		OptionsDialog dialog = new OptionsDialog();
		OptionsController inp = new OptionsController(dialog, rs);
		inp.setComponents();
		dialog.setModal(true);
		dialog.setLocationRelativeTo(null);   
		dialog.setVisible(true);		
	}
	

	public void showCatchment(){
		TableWindowPanel tableWindow = new TableWindowPanel();
        JDialog dialog = Utils.openDialogForm(tableWindow, 350, 280);
		ImageIcon image = new ImageIcon("images/imago.png");        
        dialog.setIconImage(image.getImage());
        dialog.setVisible(true);
	}	
	
	
	public void showRaingage(){
		ResultSet rs = MainDao.getTableResultset("raingage");
		RaingageDialog dialog = new RaingageDialog();
		RaingageController inp = new RaingageController(dialog, rs);
		inp.setComponents();
		dialog.setModal(true);
		dialog.setLocationRelativeTo(null);   
		dialog.setVisible(true);		        
	}	
	
	
	public void showTimesValues(){
		ResultSet rs = MainDao.getTableResultset("inp_times");
		TimesValuesDialog dialog = new TimesValuesDialog();
		TimesValuesController inp = new TimesValuesController(dialog, rs);
		inp.setComponents();
		dialog.setModal(true);
		dialog.setLocationRelativeTo(null);   
		dialog.setVisible(true);		        
	}	
		

    public void chooseFileInp() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("INP extension file", "inp");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(bundleText.getString("file_inp"));
        File file = new File(prop.get("FILE_INP", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileInp = chooser.getSelectedFile();
            String path = fileInp.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".inp";
                fileInp = new File(path);
            }
            view.setFileInp(fileInp.getAbsolutePath());            
        }

    }


    public void chooseFileRpt() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("RPT extension file", "rpt");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(bundleText.getString("file_rpt"));
        File file = new File(prop.get("FILE_RPT", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileRpt = chooser.getSelectedFile();
            String path = fileRpt.getAbsolutePath();
            System.out.println(path.lastIndexOf("."));
            if (path.lastIndexOf(".") == -1) {
                path += ".rpt";
                fileRpt = new File(path);
            }
            view.setFileRpt(fileRpt.getAbsolutePath());
        }

    }
    
    
    private boolean getFileInp(){
    	
        String path = view.getFileInp();
        if (path.equals("")){
            return false;        	
        }
        if (path.lastIndexOf(".") == -1) {
            path += ".inp";
        }
        fileInp = new File(path);        
        prop.put("FILE_INP", fileInp.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }

    
    private boolean getFileRpt(){
    	
        String path = view.getFileRpt();
        if (path.equals("")){
            return false;        	
        }
        if (path.lastIndexOf(".") == -1) {
            path += ".rpt";
        }
        fileRpt = new File(path);        
        prop.put("FILE_RPT", fileRpt.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }
    
    
    public void execute(){
    
    	if (dbSelected){
    		executePostgis();
    	} else{
    		executeDbf();
    	}
    
    }

    
    public void executePostgis() {

        boolean continueExec = true;
        
        // Which checks are selected?
        exportChecked = view.isExportChecked();
        execChecked = view.isExecChecked();
        importChecked = view.isImportChecked();        
        
        if (!exportChecked && !execChecked && !importChecked){
            Utils.showError("select_option", "", "inp_descr");
            return;
        }

        // Get schema from view
        String schema = view.getSchema();
        if (schema.equals("")){
            Utils.showError("Any schema selected", "", "inp_descr");
            return;
        }
        MainDao.setSchema(schema);
        
        // Get software version from view
        String softwareId = view.getSoftware();
        if (softwareId.equals("")){
            Utils.showError("Any software version selected", "", "inp_descr");
            return;
        }
        String version = MainDao.getSoftwareVersion("postgis", softwareId);
        Model.setSoftwareVersion(version);
        
		// Get Sqlite Database			
		String sqlitePath = version + ".sqlite";
		if (!Model.setConnectionDrivers(sqlitePath)){
			return;
		}        
        
        view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        // Export to INP
        if (exportChecked) {
            if (!getFileInp()) {
                Utils.showError("file_inp_not_selected", "", "inp_descr");
                return;
            }                
            continueExec = ModelPostgis.processAll(fileInp);
        }

        // Run SWMM
        if (execChecked && continueExec) {
            if (!getFileInp()) {
                Utils.showError("file_inp_not_selected", "", "inp_descr");
                return;
            }            
            if (!getFileRpt()) {
                Utils.showError("file_rpt_not_selected", "", "inp_descr");
                return;
            }                  
            continueExec = ModelPostgis.execSWMM(fileInp, fileRpt);
        }

        // Import RPT to Postgis
        if (importChecked && continueExec) {
            if (!getFileRpt()) {
                Utils.showError("file_rpt_not_selected", "", "inp_descr");
                return;
            }            
            projectName = view.getProjectName();
            if (projectName.equals("")){
                Utils.showError("project_name", "", "inp_descr");
            } 
            else{
           		continueExec = ModelPostgis.importRpt(fileRpt, projectName);
            	Model.closeFile();
            	if (!continueExec){
            		try {
						MainDao.rollback();
					} catch (SQLException e) {
	    	            Utils.showError(e);
					}
            	}
            }
        }
        
    }
    
    
	public void executeDbf() {

		if (!readyShp) {
			Utils.showError("dir_shp_not_selected", "", "inp_descr");
			return;
		}

        boolean continueExec = true;
        
        // Which checks are selected?
        exportChecked = view.isExportChecked();
        execChecked = view.isExecChecked();
        importChecked = view.isImportChecked();        
        
        if (!exportChecked && !execChecked && !importChecked){
            Utils.showError("select_option", "", "inp_descr");
            return;
        }
        
        // Get software version from view
		String id = view.getSoftware();
        if (id.equals("")){
            Utils.showError("Any software version selected", "", "inp_descr");
            return;
        }
        String version = MainDao.getSoftwareVersion("dbf", id);
        Model.setSoftwareVersion(version);
		
		// Get Sqlite Database			
		String sqlitePath = version + ".sqlite";
		if (!Model.setConnectionDrivers(sqlitePath)){
			return;
		}
		
		// Get INP template file
		String templatePath = MainDao.folderConfig + version + ".inp";
		File fileTemplate = new File(templatePath);
		if (!fileTemplate.exists()) {
			Utils.showError("inp_error_notfound", templatePath, "inp_descr");				
			return;
		}

		// Check if all necessary files exist
		if (!ModelDbf.checkFiles(dirShp.getAbsolutePath())) {
			return;
		}

		// Process all shapes and output to INP file
        if (!getFileInp()) {
            Utils.showError("file_inp_not_selected", "", "inp_descr");
            return;
        }    
        
        // Export to INP
        if (exportChecked) {
            if (!getFileInp()) {
                Utils.showError("file_inp_not_selected", "", "inp_descr");
                return;
            }                
            continueExec = ModelDbf.processAll(fileInp);
        }

        // Run SWMM
        if (execChecked && continueExec) {
            if (!getFileInp()) {
                Utils.showError("file_inp_not_selected", "", "inp_descr");
                return;
            }            
            if (!getFileRpt()) {
                Utils.showError("file_rpt_not_selected", "", "inp_descr");
                return;
            }                  
            continueExec = ModelPostgis.execSWMM(fileInp, fileRpt);
        }

        // Import RPT to Postgis
        if (importChecked && continueExec) {
            if (!getFileRpt()) {
                Utils.showError("file_rpt_not_selected", "", "inp_descr");
                return;
            }            
            projectName = view.getProjectName();
            if (projectName.equals("")){
                Utils.showError("project_name", "", "inp_descr");
            } 
            else{
           		continueExec = ModelPostgis.importRpt(fileRpt, projectName);
            	Model.closeFile();
            	if (!continueExec){
            		try {
						MainDao.rollback();
					} catch (SQLException e) {
	    	            Utils.showError(e);
					}
            	}
            }
        }        

	}    
    
	
	public void setSoftware() {
		view.setSoftware(MainDao.getAvailableVersions("postgis", software));
	}
	
	
}