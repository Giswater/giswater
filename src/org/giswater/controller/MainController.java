/*
 * This file is part of Giswater
 * Copyright (C) 2013PrincesaMonoayaM-2009s Associats
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

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.MainDao;
import org.giswater.gui.dialog.options.AbstractOptionsDialog;
import org.giswater.gui.dialog.options.OptionsDialog;
import org.giswater.gui.dialog.options.OptionsEpanetDialog;
import org.giswater.gui.dialog.options.RaingageDialog;
import org.giswater.gui.dialog.options.ReportDialog;
import org.giswater.gui.dialog.options.ReportEpanetDialog;
import org.giswater.gui.dialog.options.TimesDialog;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.EpaPanel;
import org.giswater.gui.panel.SectorSelectionPanel;
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
    private MainFrame mainFrame;
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
    	fileInp = new File(prop.get("FILE_INP", userHomeFolder));
		if (fileInp.exists()) {
			view.setFileInp(fileInp.getAbsolutePath());
		}
		fileRpt = new File(prop.get("FILE_RPT", userHomeFolder));
		if (fileRpt.exists()) {
			view.setFileRpt(fileRpt.getAbsolutePath());
		}    	
		projectName = prop.get("PROJECT_NAME", "");
		view.setProjectName(projectName);
		
		String gisType = prop.get("GIS_TYPE", "");
		if (gisType.equals("DATABASE")){
			view.setDatabaseSelected(true);
		}
		else if (gisType.equals("DBF")){
			view.setDbfSelected(true);
		}
		selectSourceType(false);
			
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
	
	
	// DBF only
	public void chooseFolderShp() {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(Utils.getBundleString("folder_shp"));
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
		selectSourceType(true);
	}
	
	
	private void checkCatalogTables(String schemaName){
		mainFrame.enableProjectId(MainDao.checkTable(schemaName, "inp_project_id"));
		mainFrame.enableConduit(MainDao.checkTable(schemaName, "cat_arc"));
		mainFrame.enableMaterials(MainDao.checkTable(schemaName, "cat_mat"));
		mainFrame.enablePatterns(MainDao.checkTable(schemaName, "inp_pattern"));
		mainFrame.enableTimeseries(MainDao.checkTable(schemaName, "inp_timser_id"));
		mainFrame.enableCurves(MainDao.checkTable(schemaName, "inp_curve_id"));		
	}
	
	
	private void checkOptionsTables(String schemaName){
		mainFrame.enableResultCat(MainDao.checkTable(schemaName, "rpt_result_cat"));
		mainFrame.enableResultSelection(MainDao.checkTable(schemaName, "result_selection"));
	}
	
	
	public void selectSourceType(boolean askQuestion){

		dbSelected = view.getOptDatabaseSelected();
		// Database selected
		if (dbSelected){
			// Check if we already are connected
			if (MainDao.isConnected()){
				mainFrame.enableCatalog(true);
				view.enableControlsDbf(false);
				view.enableControlsDatabase(true);
				view.enableAccept(true);
				view.setSchema(MainDao.getSchemas());
				view.setSoftware(MainDao.getAvailableVersions("postgis", software));
				// Check Catalog tables
				checkCatalogTables(view.getSchema());
			} 
			else{
				if (askQuestion){
		            // Ask if user wants to connect to Database
		            int answer = Utils.confirmDialog("open_database_connection");
		            if (answer == JOptionPane.YES_OPTION){
						mainFrame.openDatabase();
		            }
				} else{
					mainFrame.openDatabase();
				}
				mainFrame.enableCatalog(false);
				view.enableControlsDbf(false);
				view.enableControlsDatabase(false);
				view.enableAccept(false);
				view.setSchema(null);				
			}
			schemaChanged();
			prop.put("GIS_TYPE", "DATABASE");
		}
		// DBF selected
		else{
			mainFrame.enableCatalog(false);
			view.enableControlsDbf(true);			
			view.enableControlsDatabase(false);
			view.enableAccept(true);
			view.setSoftware(MainDao.getAvailableVersions("dbf", software));
			prop.put("GIS_TYPE", "DBF");
		}
		
	}
	
	
	public void schemaTest(String schemaName){
		view.setSelectedSchema(schemaName);
	}
	
	
	public void schemaChanged(){
		MainDao.setSoftwareName(software);		
		if (MainDao.isConnected()){
			String schemaName = view.getSchema();
			MainDao.setSchema(view.getSchema());
			checkCatalogTables(schemaName);
			checkOptionsTables(schemaName);
		}
	}

		
	public void showSectorSelection(){
		SectorSelectionPanel panel = new SectorSelectionPanel(view.getSchema());
        JDialog dialog = Utils.openDialogForm(panel, 350, 280);
		ImageIcon image = new ImageIcon("images/imago.png");        
        dialog.setIconImage(image.getImage());
        dialog.setVisible(true);
	}	
	
	
	public void showInpOptions(){
		ResultSet rs = MainDao.getTableResultset("inp_options");
		if (rs == null) return;
		OptionsDialog dialog = new OptionsDialog();
		showOptions(dialog, rs);
	}
	
	
	public void showInpOptionsEpanet(){
		ResultSet rs = MainDao.getTableResultset("inp_options");
		if (rs == null) return;		
		OptionsEpanetDialog dialog = new OptionsEpanetDialog();
		showOptions(dialog, rs);
	}

	
	public void showRaingage(){
		ResultSet rs = MainDao.getTableResultset("raingage");
		if (rs == null) return;		
		RaingageDialog dialog = new RaingageDialog();
		showOptions(dialog, rs);	
	}	
	
	
	public void showTimesValues(){
		ResultSet rs = MainDao.getTableResultset("inp_times");
		if (rs == null) return;		
		TimesDialog dialog = new TimesDialog();
		showOptions(dialog, rs);
	}	
	
	
	public void showReport(){
		ResultSet rs = MainDao.getTableResultset("inp_report");
		if (rs == null) return;		
		ReportDialog dialog = new ReportDialog();
		showOptions(dialog, rs);
	}	
	
	
	public void showReportEpanet(){
		ResultSet rs = MainDao.getTableResultset("inp_report");
		if (rs == null) return;		
		ReportEpanetDialog dialog = new ReportEpanetDialog();
		showOptions(dialog, rs);
	}	
	
	
	private void showOptions(AbstractOptionsDialog dialog, ResultSet rs){
		
		OptionsController controller = new OptionsController(dialog, rs);
        if (MainDao.getRowCount(rs) == 0){
            controller.create();
        }
        else{
            controller.moveFirst();
        }
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);   
        dialog.setVisible(true);	
        
	}
	
		

    public void chooseFileInp() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("INP extension file", "inp");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_inp"));
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
        chooser.setDialogTitle(Utils.getBundleString("file_rpt"));
        File file = new File(prop.get("FILE_RPT", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileRpt = chooser.getSelectedFile();
            String path = fileRpt.getAbsolutePath();
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
            Utils.showError("select_option");
            return;
        }

        // Get schema from view
        String schema = view.getSchema();
        if (schema.equals("")){
            Utils.showError("any_schema_selected");
            return;
        }
        MainDao.setSchema(schema);
        
        // Get software version from view
        String softwareId = view.getSoftware();
        if (softwareId.equals("")){
            Utils.showError("any_software_selected");
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
                Utils.showError("file_inp_not_selected");
                return;
            }                
            continueExec = ModelPostgis.processAll(fileInp);
        }

        // Run SWMM
        if (execChecked && continueExec) {
            if (!getFileInp()) {
                Utils.showError("file_inp_not_selected");
                return;
            }            
            if (!getFileRpt()) {
                Utils.showError("file_rpt_not_selected");
                return;
            }                  
            continueExec = ModelPostgis.execSWMM(fileInp, fileRpt);
        }

        // Import RPT to Postgis
        if (importChecked && continueExec) {
            if (!getFileRpt()) {
                Utils.showError("file_rpt_not_selected");
                return;
            }            
            projectName = view.getProjectName();
            if (projectName.equals("")){
                Utils.showError("project_name");
            } 
            else{
           		continueExec = ModelPostgis.importRpt(fileRpt, projectName);
            	Model.closeFile();
            	if (!continueExec){
					MainDao.rollback();
            	}
            }
        }
        
    }
    
    
	public void executeDbf() {

		if (!readyShp) {
			Utils.showError("dir_shp_not_selected");
			return;
		}

        boolean continueExec = true;
        
        // Which checks are selected?
        exportChecked = view.isExportChecked();
        execChecked = view.isExecChecked();
        importChecked = view.isImportChecked();        
        
        if (!exportChecked && !execChecked && !importChecked){
            Utils.showError("select_option");
            return;
        }
        
        // Get software version from view
		String id = view.getSoftware();
        if (id.equals("")){
            Utils.showError("any_software_selected");
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
		String templatePath = MainDao.getFolderConfig()+version+".inp";
		File fileTemplate = new File(templatePath);
		if (!fileTemplate.exists()) {
			Utils.showError("inp_error_notfound", templatePath);				
			return;
		}

		// Check if all necessary files exist
		if (!ModelDbf.checkFiles(dirShp.getAbsolutePath())) {
			return;
		}

		// Process all shapes and output to INP file
        if (!getFileInp()) {
            Utils.showError("file_inp_not_selected");
            return;
        }    
        
        // Export to INP
        if (exportChecked) {
            if (!getFileInp()) {
                Utils.showError("file_inp_not_selected");
                return;
            }                
            continueExec = ModelDbf.processAll(fileInp);
        }

        // Run SWMM
        if (execChecked && continueExec) {
            if (!getFileInp()) {
                Utils.showError("file_inp_not_selected");
                return;
            }            
            if (!getFileRpt()) {
                Utils.showError("file_rpt_not_selected");
                return;
            }                  
            continueExec = ModelPostgis.execSWMM(fileInp, fileRpt);
        }

        // Import RPT to Postgis
        if (importChecked && continueExec) {
            if (!getFileRpt()) {
                Utils.showError("file_rpt_not_selected");
                return;
            }            
            projectName = view.getProjectName();
            if (projectName.equals("")){
                Utils.showError("project_name");
            } 
            else{
           		continueExec = ModelPostgis.importRpt(fileRpt, projectName);
            	Model.closeFile();
            	if (!continueExec){
					MainDao.rollback();
            	}
            }
        }        

	}    
    
	
	public void setSoftware() {
		view.setSoftware(MainDao.getAvailableVersions("postgis", software));
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
		
		String softwareName = view.getSoftwareName();
		
		// Ask user to set SRID?
		String defaultSrid = prop.get("SRID_DEFAULT", "23030");		
		String sridValue = getUserSrid(defaultSrid);

		if (!sridValue.equals("")){
			Integer srid;
			try{
				srid = Integer.parseInt(sridValue);
			} catch (NumberFormatException e){
				Utils.showError("error_srid");
				return;
			}
			if (!sridValue.equals(defaultSrid)){
				prop.put("SRID_DEFAULT", sridValue);
				MainDao.savePropertiesFile();
			}
			boolean isSridOk = MainDao.checkSrid(srid);
			if (!isSridOk && srid != 0){
				String msg = "SRID "+srid+" " +Utils.getBundleString("srid_not_found")+"\n" +
					Utils.getBundleString("srid_valid");			
				Utils.showError(msg);
				return;
			}
			view.setCursor(new Cursor(Cursor.WAIT_CURSOR));		
			boolean status = MainDao.createSchema(softwareName, schemaName, sridValue);
			if (status && defaultSchemaName.equals("")){
				Utils.showMessage("schema_creation_completed");
			}
			else if (status && !defaultSchemaName.equals("")){
				Utils.showMessage("schema_truncate_completed");
			}
			view.setSchema(MainDao.getSchemas());		
			view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	
	public void deleteSchema(){
		
		String schemaName = view.getSchema();
		String msg = Utils.getBundleString("delete_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(msg);        
        if (res == 0){
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	MainDao.deleteSchema(schemaName);
        	view.setSchema(MainDao.getSchemas());
    		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    		Utils.showMessage("schema_deleted", "");
        }
        
	}		
		
	
	public void deleteData(){
		
		String schemaName = view.getSchema();
		String msg = Utils.getBundleString("empty_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(msg);        
        if (res == 0){
        	MainDao.deleteSchema(schemaName);
    		createSchema(schemaName);
        }
		
	}
	
	
}