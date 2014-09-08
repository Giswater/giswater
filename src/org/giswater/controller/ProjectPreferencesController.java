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

import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.giswater.dao.MainDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.ProjectPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.task.CopySchemaTask;
import org.giswater.util.Encryption;
import org.giswater.util.Utils;


public class ProjectPreferencesController extends AbstractController {

	private ProjectPreferencesPanel view;
	private MainFrame mainFrame;
	private EpaSoftPanel epaSoftPanel;
    private String usersFolder;
	private String waterSoftware;
	private JDialog projectDialog;


	public ProjectPreferencesController(ProjectPreferencesPanel ppPanel, MainFrame mf) {
		
		this.view = ppPanel;	
		this.mainFrame = mf;
		this.epaSoftPanel = mainFrame.epaSoftFrame.getPanel();
    	this.usersFolder = MainDao.getRootFolder(); 
	    view.setController(this);    
	    
	}
	
	
	public void changeSoftware() {
		
		// Update software version 
		waterSoftware = view.getWaterSoftware();
		view.setVersionSoftware(MainDao.getAvailableVersions("postgis", waterSoftware));
		view.setInfo("");
		
		// Get schemas from selected water software
		selectSourceType();
		
		// Customize buttons and title
		// customizePanel();

	}
	
	
	private void customizePanel() {
		
		if (waterSoftware.equals("HECRAS")) {
			mainFrame.epaSoftFrame.setVisible(false);
			mainFrame.hecRasFrame.getPanel().enableButtons(MainDao.isConnected());
        	try {
				mainFrame.hecRasFrame.setMaximum(true);
				mainFrame.hecRasFrame.setVisible(true);
			} catch (PropertyVetoException e) {
				Utils.logError(e);
			}			
		}
		else {
			if (waterSoftware.equals("EPASWMM")) {
				epaSoftPanel.setDesignButton("Raingage", "showRaingage");
				epaSoftPanel.setOptionsButton("Options", "showInpOptions");
				epaSoftPanel.setReportButton("Report options", "showReport");
				epaSoftPanel.setSubcatchmentVisible(true);
			}
			else if (waterSoftware.equals("EPANET")) {
				epaSoftPanel.setDesignButton("Times values", "showTimesValues");
				epaSoftPanel.setOptionsButton("Options", "showInpOptionsEpanet");
				epaSoftPanel.setReportButton("Report options", "showReportEpanet");
				epaSoftPanel.setSubcatchmentVisible(false);
			}
			mainFrame.hecRasFrame.setVisible(false);
			mainFrame.epaSoftFrame.setTitle(waterSoftware);
	        boolean maximized = Boolean.parseBoolean(MainDao.getGswProperties().get("EPASOFT_MAXIMIZED", "true"));
	        if (maximized) {
	        	try {
					mainFrame.epaSoftFrame.setMaximum(maximized);
					mainFrame.epaSoftFrame.setVisible(true);
				} catch (PropertyVetoException e) {
					Utils.logError(e);
				}
	        }			
		}
		mainFrame.updateConnectionInfo();
		
	}
	
	private boolean checkPreferences() {
		
		view.setInfo("");
		if (waterSoftware.equals("")){
			view.setInfo("You have to select Water Software");
			return false;
		}
		return true;
		
	}
	
	
	public boolean applyPreferences() {
		
		// Check if everything is set
		if (!checkPreferences()){
			view.getFrame().setVisible(true);
			mainFrame.hecRasFrame.setVisible(false);
			mainFrame.epaSoftFrame.setVisible(false);	
			return false;
		}
			
		// Update Project preferences parameters
		mainFrame.putProjectPreferencecsParams();		
		//MainDao.saveGswPropertiesFile();
		
		// Customize buttons and title
		customizePanel();
		
		// Check schema version
		MainDao.checkSchemaVersion();
		
		return true;
		
	}
	
	
	public void acceptPreferences() {
		if (applyPreferences()){
			closePreferences();	
		}
	}
	
	
	public void closePreferences() {
		view.getFrame().setVisible(false);	
	}


	// DBF configuration
	public void chooseFolderShp() {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(Utils.getBundleString("folder_shp"));
		File file = new File(MainDao.getGswProperties().get(waterSoftware+"_FOLDER_SHP", usersFolder));
		chooser.setCurrentDirectory(file);
		int returnVal = chooser.showOpenDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dirShp = chooser.getSelectedFile();
			view.setFolderShp(dirShp.getAbsolutePath());
		}

	}
	
	
	// Database configuration
	private void checkDataManagerTables(String schemaName) {
		epaSoftPanel.enableConduit(MainDao.checkTable(schemaName, "cat_arc"));
		epaSoftPanel.enableMaterials(MainDao.checkTable(schemaName, "cat_mat"));
		epaSoftPanel.enableHydrologyCat(MainDao.checkTable(schemaName, "cat_hydrology"));	
		epaSoftPanel.enablePatterns(MainDao.checkTable(schemaName, "inp_pattern"));
		epaSoftPanel.enableTimeseries(MainDao.checkTable(schemaName, "inp_timser_id"));
		epaSoftPanel.enableCurves(MainDao.checkTable(schemaName, "inp_curve_id"));	
	}
	
	
	private void checkPostprocessTables(String schemaName) {
		epaSoftPanel.enableResultCatalog(MainDao.checkTable(schemaName, "rpt_result_cat"));
		epaSoftPanel.enableResultSelector(MainDao.checkTable(schemaName, "result_selection"));
	}
	
	
	public void testConnection() {
	
		if (MainDao.isConnected()){
			closeConnection();
			mainFrame.enableMenuDatabase(false);
			view.enableConnectionParameters(true);			
		}
		else{
			if (openConnection()){
				mainFrame.enableMenuDatabase(true);
				view.enableConnectionParameters(false);
			}
		}
		selectSourceType();
		
		// Update Status Bar
		mainFrame.updateConnectionInfo();
		
	}	
	
	
	private void closeConnection() {
		
		view.setConnectionText(Utils.getBundleString("open_connection"));
		mainFrame.hecRasFrame.getPanel().enableButtons(false);
		MainDao.closeConnectionPostgis();
		Utils.showMessage(view, "connection_closed");			
		
	}
	
	
	private boolean openConnection() {
		
		String host, port, db, user, password;
		
		// Get parameteres connection from view
		host = view.getHost();		
		port = view.getPort();
		db = view.getDatabase();
		user = view.getUser();
		password = view.getPassword();	
		
		// Try to connect to Database
		boolean isConnected = MainDao.setConnectionPostgis(host, port, db, user, password);
		MainDao.setConnected(isConnected);
		
		if (isConnected){
			MainDao.getGswProperties().put("POSTGIS_HOST", host);
			MainDao.getGswProperties().put("POSTGIS_PORT", port);
			MainDao.getGswProperties().put("POSTGIS_DATABASE", db);
			MainDao.getGswProperties().put("POSTGIS_USER", user);
			// Save encrypted password
			if (view.getRemember()){
				MainDao.getGswProperties().put("POSTGIS_PASSWORD", Encryption.encrypt(password));
			} else{
				MainDao.getGswProperties().put("POSTGIS_PASSWORD", "");
			}
			
			// Get Postgis data and bin Folder
	    	String dataPath = MainDao.getDataDirectory();
	    	MainDao.getGswProperties().put("POSTGIS_DATA", dataPath);
	        File dataFolder = new File(dataPath);
	        String binPath = dataFolder.getParent() + File.separator + "bin";
	        MainDao.getGswProperties().put("POSTGIS_BIN", binPath);
	        Utils.getLogger().info("Connection successful");
	        Utils.getLogger().info("Postgre data directory: " + dataPath);	
	    	Utils.getLogger().info("Postgre version: " + MainDao.checkPostgreVersion());
        	String postgisVersion = MainDao.checkPostgisVersion();	        
        	if (postgisVersion.equals("")){
        		// Enable Postgis to current Database
        		String sql = "CREATE EXTENSION postgis; CREATE EXTENSION postgis_topology;";
        		MainDao.executeUpdateSql(sql, true, false);			  	
        	}
        	else{
        		Utils.getLogger().info("Postgis version: " + postgisVersion);
        	}
	    	
			view.setConnectionText(Utils.getBundleString("close_connection"));
			Utils.showMessage(view, "connection_opened");
			
			// Hecras form
			mainFrame.hecRasFrame.getPanel().enableButtons(true);

		} 

		return isConnected;
		
	}	
	
	
	public void selectSourceType() {

		// Database selected
		if (view.getOptDatabaseSelected()) {
			view.enableConnectionParameters(true);
			view.enableDbfStorage(false);
			epaSoftPanel.enableRunAndImport(true);
			// Check if we already are connected
			if (MainDao.isConnected()) {
				mainFrame.hecRasFrame.getPanel().enableButtons(true);
				mainFrame.enableMenuDatabase(true);
				view.enableProjectManagement(true);
				view.setVersionSoftware(MainDao.getAvailableVersions("postgis", waterSoftware));
				Vector<String> schemaList = MainDao.getSchemas(waterSoftware);
				if (schemaList != null && schemaList.size() > 0){
					setSchema(schemaList.get(0));
				} else {
					setSchema("");
				}
				boolean enabled = view.setSchemaModel(schemaList);
				view.setSelectedSchema(MainDao.getGswProperties().get("SCHEMA"));						
				epaSoftPanel.enablePreprocess(enabled);
				epaSoftPanel.enableAccept(enabled);
			} 
			else {
				mainFrame.hecRasFrame.getPanel().enableButtons(false);
				mainFrame.enableMenuDatabase(false);
				view.enableProjectManagement(false);
				view.setSchemaModel(null);	
				epaSoftPanel.enableDatabaseButtons(false);
				epaSoftPanel.enableAccept(false);
			}
		}
		
		// DBF selected
		else {
			view.enableConnectionParameters(false);
			view.enableProjectManagement(false);
			view.enableDbfStorage(true);		
			epaSoftPanel.enableRunAndImport(false);
			mainFrame.hecRasFrame.getPanel().enableButtons(false);
			mainFrame.enableMenuDatabase(false);
			view.setVersionSoftware(MainDao.getAvailableVersions("dbf", waterSoftware));
			epaSoftPanel.enableDatabaseButtons(false);
			epaSoftPanel.enableAccept(true);
		}
		
	}
	
	
	public void isConnected() {

		// Check if we already are connected
		if (MainDao.isConnected()){
			view.setSchemaModel(MainDao.getSchemas(waterSoftware));
			String gswSchema = MainDao.getGswProperties().get("SCHEMA").trim();
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
		mainFrame.enableMenuDatabase(MainDao.isConnected());
		
	}	
	
	
	public void schemaChanged() {
		
		MainDao.setWaterSoftware(waterSoftware);		
		if (MainDao.isConnected()){
			setSchema(view.getSelectedSchema());
		}
		
	}
	
	
	private void setSchema(String schemaName) {
		MainDao.setSchema(schemaName);
		checkDataManagerTables(schemaName);
		checkPostprocessTables(schemaName);
	}
	
	
	public void schemaTest(String schemaName) {
		view.setSelectedSchema(schemaName);
	}
	
	
	public void setWaterSoftware(String waterSoftware) {
		this.waterSoftware = waterSoftware;
	}
	
	public void setVersionSoftware() {
		view.setVersionSoftware(MainDao.getAvailableVersions("postgis", waterSoftware));
	}
	
	
	// Project Management
	private String validateName(String schemaName) {
		
		String validate;
		validate = schemaName.trim().toLowerCase();
		validate = validate.replace(" ", "_");
		validate = validate.replaceAll("[^\\p{ASCII}]", "");
		return validate;
		
	}
	
	
	public void createSchemaAssistant() {
		
		String defaultSrid = MainDao.getPropertiesFile().get("SRID_DEFAULT", "25831");		
		ProjectPanel projectPanel = new ProjectPanel(defaultSrid);
		NewProjectController npController = new NewProjectController(projectPanel);
		projectPanel.setController(npController);
		npController.setParentPanel(view);
		npController.initModel();
		npController.updateTableModel();
		
		// Open New Project dialog
        projectDialog = Utils.openDialogForm(projectPanel, view, "Create Project", 420, 480);
        projectPanel.setParent(projectDialog);
        projectDialog.setVisible(true);
		
	}
	
	
	public void deleteSchema() {
		
		String schemaName = view.getSelectedSchema();
		String msg = Utils.getBundleString("delete_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(view, msg);        
        if (res == 0){     
        	view.requestFocusInWindow();     	
        	MainDao.deleteSchema(schemaName);
    		Vector<String> schemaList = MainDao.getSchemas(waterSoftware);
    		boolean enabled = view.setSchemaModel(schemaList);
    		epaSoftPanel.enablePreprocess(enabled);
        	setSchema(view.getSelectedSchema());
        	mainFrame.showMessage("schema_deleted");
        }
        
	}		
	
	
	public void renameSchema() {
		
		String schemaName = view.getSelectedSchema();
		if (schemaName.equals("")) return;
		
		String newSchemaName = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_schema_name"), schemaName);
		if (newSchemaName == null){
			return;
		}
		newSchemaName = validateName(newSchemaName);
		if (newSchemaName.equals("")){
        	mainFrame.showError("schema_valid_name");
			return;
		}
		String sql = "ALTER SCHEMA "+schemaName+" RENAME TO "+newSchemaName;
		Utils.logSql(sql);
		if (MainDao.executeUpdateSql(sql, true)){
			selectSourceType();
        	mainFrame.showMessage("Project renamed successfuly");
		}
		
	}
	
	
	public void copySchema() {
		
		String schemaName = view.getSelectedSchema();
		if (schemaName.equals("")) return;
		
		String newSchemaName = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_schema_name"), schemaName);
		if (newSchemaName == null){
			return;
		}
		newSchemaName = validateName(newSchemaName);
		if (newSchemaName.equals("")){
			Utils.showError(view, "schema_valid_name");
			return;
		}

		// Execute task: CreateSchema
		CopySchemaTask task = new CopySchemaTask(schemaName, newSchemaName);
        task.setController(this);
        task.setParentPanel(view);
        task.addPropertyChangeListener(this);
        task.execute();
        		
	}
		

	public void createGisProject(){
		mainFrame.gisFrame.setVisible(true);
	}
    
		
}