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
import java.sql.ResultSet;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;

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
import org.giswater.gui.panel.ProjectPanel;
import org.giswater.gui.panel.SectorSelectionPanel;
import org.giswater.model.Model;
import org.giswater.model.ModelDbf;
import org.giswater.model.ModelPostgis;
import org.giswater.model.TableModelSrid;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MainController{

	private EpaPanel view;
    private PropertiesMap prop;
    private PropertiesMap gswProp;
    private File fileInp;
    private File fileRpt;
    private String projectName;
    private boolean exportChecked;
    private boolean execChecked;
    private boolean importChecked;
    
    private String usersFolder;
    private MainFrame mainFrame;
	private String software;
	
	private TableModelSrid model;
	private TableColumnModel tcm;
	private ProjectPanel projectPanel;
	private JDialog projectDialog;
	
	// DBF only
	private File dirShp;
	private boolean readyShp = false;
	private boolean dbSelected = false;

    
    public MainController(EpaPanel view, MainFrame mf, String software) {
    	
    	this.mainFrame = mf;
    	this.view = view;	
        this.prop = MainDao.getPropertiesFile();
        this.gswProp = MainDao.getGswProperties();
        this.software = software;
	    view.setController(this);        
    	usersFolder = MainDao.getUsersPath(); 	
    	
	}
   

	public void action(String actionCommand) {
		
		Method method;
		try {
			if (Utils.getLogger() != null){
				if (!actionCommand.equals("schemaChanged")){
					Utils.getLogger().info(actionCommand);
				}
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
	
	
	// DBF only
	public void chooseFolderShp() {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(Utils.getBundleString("folder_shp"));
		File file = new File(gswProp.get(software+"_FOLDER_SHP", usersFolder));
		chooser.setCurrentDirectory(file);
		int returnVal = chooser.showOpenDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			dirShp = chooser.getSelectedFile();
			view.setFolderShp(dirShp.getAbsolutePath());
			gswProp.put(software+"_FOLDER_SHP", dirShp.getAbsolutePath());
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
	
	
	public void enableCatalog(boolean enable){
		mainFrame.enableCatalog(enable);
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
				view.setSchemaModel(MainDao.getSchemas(software));
		    	view.setSelectedSchema(MainDao.getGswProperties().get(software+"_SCHEMA"));						
				view.setSoftware(MainDao.getAvailableVersions("postgis", software));
				// Check Catalog tables
				checkCatalogTables(view.getSelectedSchema());
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
				view.setSchemaModel(null);				
			}
			schemaChanged();
		}
		// DBF selected
		else{
			mainFrame.enableCatalog(false);
			view.enableControlsDbf(true);			
			view.enableControlsDatabase(false);
			view.enableAccept(true);
			view.setSoftware(MainDao.getAvailableVersions("dbf", software));
		}
		
	}
	
	
	public void schemaTest(String schemaName){
		view.setSelectedSchema(schemaName);
	}
	
	
	public void schemaChanged(){
		
		MainDao.setSoftwareName(software);		
		if (MainDao.isConnected()){
			String schemaName = view.getSelectedSchema();
			MainDao.setSchema(schemaName);
			checkCatalogTables(schemaName);
			checkOptionsTables(schemaName);
		}
		
	}

	
	public void isConnected(){

		// Check if we already are connected
		if (MainDao.isConnected()){
			view.setSchemaModel(MainDao.getSchemas(software));
			String gswSchema = MainDao.getGswProperties().get(software+"_SCHEMA").trim();
			if (!gswSchema.equals("")){
				view.setSelectedSchema(gswSchema);	
			}
			else{
				schemaChanged();
			}
		} 
		else{
			view.setSchemaModel(null);				
		}
		enableCatalog(MainDao.isConnected());
		
	}	
	
		
	public void showSectorSelection(){
		SectorSelectionPanel panel = new SectorSelectionPanel();
        JDialog dialog = Utils.openDialogForm(panel, view, "Sector Selection", 380, 280);
        //JDialog dialog = Utils.openDialogForm(panel, view, "Sector Selection");
        panel.setParent(dialog);
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
        
		if (dialog instanceof RaingageDialog){
			controller.changeRaingageType();
		}
        
	}
	
		

    public void chooseFileInp() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("INP extension file", "inp");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_inp"));
        File file = new File(gswProp.get(software+"_FILE_INP", usersFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
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
        File file = new File(gswProp.get(software+"_FILE_RPT", usersFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
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
        gswProp.put(software+"_FILE_INP", fileInp.getAbsolutePath());
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
        gswProp.put(software+"_FILE_RPT", fileRpt.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }
    
    
    public void execute(){
       	
    	view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	if (dbSelected){
    		executePostgis();
    	} else{
    		executeDbf();
    	}
    	view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));    	
    
    }

    
    public void executePostgis() {

        boolean continueExec = true;
        
        // Which checks are selected?
        exportChecked = view.isExportChecked();
        execChecked = view.isExecChecked();
        importChecked = view.isImportChecked();        
        
        if (!exportChecked && !execChecked && !importChecked){
            Utils.showError(view, "select_option");
            return;
        }

        // Get schema from view
        String schema = view.getSelectedSchema();
        if (schema.equals("")){
            Utils.showError(view, "any_schema_selected");
            return;
        }
        MainDao.setSchema(schema);
        
        // Get software version from view
        String softwareId = view.getSoftware();
        if (softwareId.equals("")){
            Utils.showError(view, "any_software_selected");
            return;
        }
        String version = MainDao.getSoftwareVersion("postgis", softwareId);
        Model.setSoftwareVersion(version);
        
		// Get Sqlite Database			
		String sqlitePath = version + ".sqlite";
		if (!Model.setConnectionDrivers(sqlitePath)){
			return;
		}        
        
        // Export to INP
        if (exportChecked) {
            if (!getFileInp()) {
                Utils.showError(view, "file_inp_not_selected");
                return;
            }      
            if (!ModelPostgis.checkSectorSelection()) {
        		int res = Utils.confirmDialog(view, "sector_selection_empty");        
                if (res == 0){            	
                	showSectorSelection();
                }
                return;
            }               
            continueExec = ModelPostgis.processAll(fileInp);
        }

        // Run SWMM
        if (execChecked && continueExec) {
            if (!getFileInp()) {
                Utils.showError(view, "file_inp_not_selected");
                return;
            }            
            if (!getFileRpt()) {
                Utils.showError(view, "file_rpt_not_selected");
                return;
            }                  
            continueExec = ModelPostgis.execEPASOFT(fileInp, fileRpt);
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
        
        // Force refresh schema
        schemaChanged();
        
    }
    
    
	public void executeDbf() {

		if (!readyShp) {
			Utils.showError(view, "dir_shp_not_selected");
			return;
		}

        boolean continueExec = true;
        
        // Which checks are selected?
        exportChecked = view.isExportChecked();
        execChecked = view.isExecChecked();
        importChecked = view.isImportChecked();        
        
        if (!exportChecked && !execChecked && !importChecked){
            Utils.showError(view, "select_option");
            return;
        }
        
        // Get software version from view
		String id = view.getSoftware();
        if (id.equals("")){
            Utils.showError(view, "any_software_selected");
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
			Utils.showError(view, "inp_error_notfound", templatePath);				
			return;
		}

		// Check if all necessary files exist
		if (!ModelDbf.checkFiles(dirShp.getAbsolutePath())) {
			return;
		}

		// Process all shapes and output to INP file
        if (!getFileInp()) {
            Utils.showError(view, "file_inp_not_selected");
            return;
        }    
        
        // Export to INP
        if (exportChecked) {
            if (!getFileInp()) {
                Utils.showError(view, "file_inp_not_selected");
                return;
            }                
            continueExec = ModelDbf.processAll(fileInp);
        }

        // Run SWMM
        if (execChecked && continueExec) {
            if (!getFileInp()) {
                Utils.showError(view, "file_inp_not_selected");
                return;
            }            
            if (!getFileRpt()) {
                Utils.showError(view, "file_rpt_not_selected");
                return;
            }                  
            continueExec = ModelPostgis.execEPASOFT(fileInp, fileRpt);
        }

        // Import RPT to Postgis
        if (importChecked && continueExec) {
            if (!getFileRpt()) {
                Utils.showError(view, "file_rpt_not_selected");
                return;
            }            
            projectName = view.getProjectName();
            if (projectName.equals("")){
                Utils.showError(view, "project_name");
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
		return sridValue.trim().toLowerCase();
		
	}
	
	
	private String validateName(String schemaName){
		
		String validate;
		validate = schemaName.trim().toLowerCase();
		validate = validate.replace(" ", "_");
		validate = validate.replaceAll("[^\\p{ASCII}]", "");
		return validate;
		
	}
	
	
	public void createSchema(){
		createSchemaAssistant();
	}
	
	
	private void createSchemaAssistant() {
		
		String defaultSrid = prop.get("SRID_DEFAULT", "25831");		
		ProjectPanel panel = new ProjectPanel(defaultSrid);
		panel.setController(this);
        initModel(panel);
        updateTableModel();
        projectDialog = Utils.openDialogForm(panel, view, "Create Project", 420, 480);
        projectDialog.setVisible(true);
		
	}


	public void createSchema(String defaultSchemaName, String defaultSridSchema){
		
		String schemaName = defaultSchemaName;
		if (defaultSchemaName.equals("")){
			schemaName = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_schema_name"), "schema_name");
			if (schemaName == null){
				return;
			}
			schemaName = validateName(schemaName);
			if (schemaName.equals("")){
				Utils.showError(view, "schema_valid_name");
				return;
			}
		}
		
		String softwareName = view.getSoftwareName();
		
		String sridValue = "";
		if (defaultSridSchema.equals("")){
			String defaultSrid = prop.get("SRID_DEFAULT", "25831");		
			sridValue = getUserSrid(defaultSrid);
		}
		else{
			sridValue = defaultSridSchema;
		}
		if (sridValue.equals("")){
			return;
		}
		Integer srid;
		try{
			srid = Integer.parseInt(sridValue);
		} catch (NumberFormatException e){
			Utils.showError(view, "error_srid");
			return;
		}	
		MainDao.getGswProperties().put("SRID_USER", sridValue);
		MainDao.savePropertiesFile();
		boolean isSridOk = MainDao.checkSrid(srid);
		if (!isSridOk && srid != 0){
			String msg = "SRID "+srid+" " +Utils.getBundleString("srid_not_found")+"\n" +
				Utils.getBundleString("srid_valid");			
			Utils.showError(view, msg);
			return;
		}
		
		// Set wait cursor
    	view.enableControlsText(false);
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	  
		
		boolean status = MainDao.createSchema(softwareName, schemaName, sridValue);	
		if (status && defaultSchemaName.equals("")){
			Utils.showMessage(view, "schema_creation_completed");
		}
		else if (status && !defaultSchemaName.equals("")){
			Utils.showMessage(view, "schema_truncate_completed");
		}
		view.setSchemaModel(MainDao.getSchemas(software));	
		schemaChanged();
		
		view.enableControlsText(true);
		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));			
		
	}
	
	
	public void deleteSchema(){
		
		String schemaName = view.getSelectedSchema();
		String msg = Utils.getBundleString("delete_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(view, msg);        
        if (res == 0){     
        	view.requestFocusInWindow();
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	MainDao.deleteSchema(schemaName);
        	view.setSchemaModel(MainDao.getSchemas(software));
    		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
    		Utils.showMessage(view, "schema_deleted", "");
        }
        
	}		
		
	
	public void deleteData(){
		
		String schemaName = view.getSelectedSchema();
		String msg = Utils.getBundleString("empty_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(view, msg);        
        if (res == 0){
        	// Get SRID before delete schema
			String table = "arc";
			if (software.equals("HECRAS")){
				table = "banks";
			}
			String schemaSrid = MainDao.getTableSrid(schemaName, table).toString();            	
        	MainDao.deleteSchema(schemaName);
    		createSchema(schemaName, schemaSrid);
        }
		
	}
	
	
	public void initModel(ProjectPanel panel){
		
		this.projectPanel = panel;
        model = new TableModelSrid();
        JTable table = projectPanel.getTable();
        model.setTable(table);   
        tcm = table.getColumnModel();     
        
		String sql = "SELECT substr(srtext, 1, 6) as \"Type\", srid as \"SRID\", substr(split_part(srtext, ',', 1), 9) as \"Description\"";		
		sql+= " FROM public.spatial_ref_sys";
		sql+= " ORDER BY substr(srtext, 1, 6), srid";
		Utils.getLogger().info(sql);
		ResultSet rs = MainDao.getResultset(sql);
		model.setRs(rs);
		projectPanel.setTableModel(model);    
		// Rendering just first time
		if (tcm.getColumnCount() > 0){
			tcm.getColumn(0).setMaxWidth(50);   
			tcm.getColumn(1).setMaxWidth(40);   
		}
        
	}
	
	
	public void updateTableModel() {
		updateTableModel("");
	}
	
	
	public void updateTableModel(String filterType) {
		
		String sql = "SELECT substr(srtext, 1, 6) as \"Type\", srid as \"SRID\", substr(split_part(srtext, ',', 1), 9) as \"Description\"";			
		sql+= " FROM public.spatial_ref_sys";
		String filter = projectPanel.getFilter();
		if (!filter.equals("")){
			sql+= " WHERE (cast(srid as varchar) like '%"+filter+"%' OR split_part(srtext, ',', 1) like '%"+filter+"%')";
		} 
		if (!filterType.equals("")){
			if (filter.equals("")){
				sql+= " WHERE ";
			}
			else{
				sql+= " AND ";
			}
			sql+= "("+filterType+")";
		} 
		
		sql+= " ORDER BY substr(srtext, 1, 6), srid";
		ResultSet rs = MainDao.getResultset(sql);
		model.setRs(rs);
		projectPanel.setTableModel(model);    			
		Utils.getLogger().info(sql);
		
	}
	
	
	public void checkedType() {

		String filterType = "";
		Boolean isGeo = projectPanel.isGeoSelected();
		Boolean isProj = projectPanel.isProjSelected();
		if (!isGeo && !isProj){
			Utils.showMessage("You have to select at least one Type: GEOGCS or PROJCS");
			return;
		}
		if (isGeo){
			filterType = "substr(srtext, 1, 6) = 'GEOGCS'";
		}
		if (isProj){
			if (!filterType.equals("")){
				filterType+= " OR ";
			}
			filterType+= "substr(srtext, 1, 6) = 'PROJCS'";
		}
		updateTableModel(filterType);
		
	}
	
	
	public void acceptProject(){
		
		// SRID
		String sridValue = projectPanel.getSrid();
		if (sridValue.equals("-1")){
			Utils.showMessage(projectPanel, Utils.getBundleString("srid_select"));
			return;
		}
		
		// Project Name
		String schemaName = projectPanel.getName();
		if (schemaName.equals("")){
			Utils.showMessage(projectPanel, Utils.getBundleString("enter_schema_name"));
			return;
		}
		schemaName = validateName(schemaName);
		if (schemaName.equals("")){
			Utils.showError(view, "schema_valid_name");
			return;
		}
		
		// Project Title, Author and Date
		String title = projectPanel.getTitle();
		if (title.equals("")){
			Utils.showMessage(projectPanel, Utils.getBundleString("enter_schema_title"));
			return;
		}
		String author = projectPanel.getAuthor();
		String date = projectPanel.getDate();
		
		// Save properties
		MainDao.getGswProperties().put("SRID_USER", sridValue);
		MainDao.savePropertiesFile();
		
    	view.enableControlsText(false);
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	  
		
		String softwareName = view.getSoftwareName();
		boolean status = MainDao.createSchema(softwareName, schemaName, sridValue);	
		if (status){
			MainDao.setSchema(schemaName);
			String sql = "INSERT INTO "+schemaName+".inp_project_id VALUES ('"+title+"', '"+author+"', '"+date+"')";
			Utils.getLogger().info(sql);
			MainDao.executeSql(sql, true);
			sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date)" +
				" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+software+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";
			Utils.getLogger().info(sql);
			MainDao.executeSql(sql, true);
			Utils.showMessage(view, "schema_creation_completed");
		}
		
		// Update view
		view.setSchemaModel(MainDao.getSchemas(software));	
		schemaChanged();
		view.enableControlsText(true);
		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		closeProject();
		
	}
	
	
	public void closeProject(){
		projectDialog.dispose();
	}
	
	
}