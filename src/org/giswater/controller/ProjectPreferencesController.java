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
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.giswater.dao.ConfigDao;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.gui.panel.ProjectPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.task.CopySchemaTask;
import org.giswater.task.DeleteSchemaTask;
import org.giswater.util.Encryption;
import org.giswater.util.Utils;
import org.giswater.util.UtilsFTP;


public class ProjectPreferencesController extends AbstractController {

	private static final Integer PROJECT_DIALOG_WIDTH = 420; 
	private static final Integer PROJECT_DIALOG_HEIGHT = 480; 
	private static final Integer FIRST_VERSION_RENAME_AVAILABLE = 11146; 
	private static final Integer FIRST_VERSION_COPY_AVAILABLE = 11101; 
	
	private ProjectPreferencesPanel view;
	private MainFrame mainFrame;
	private EpaSoftPanel epaSoftPanel;
	private HecRasPanel hecRasPanel;
	private String waterSoftware;


	public ProjectPreferencesController(ProjectPreferencesPanel ppPanel, MainFrame mf) {
		
		this.view = ppPanel;	
		this.mainFrame = mf;
		this.epaSoftPanel = mainFrame.epaSoftFrame.getPanel();
		this.hecRasPanel = mainFrame.hecRasFrame.getPanel();
	    view.setController(this);    
	    
	}
	
	
	public void changeSoftware() {
		
		// Update software version 
		setWaterSoftware(view.getWaterSoftware());
		MainDao.setWaterSoftware(waterSoftware);
		view.setVersionSoftwareModel(ConfigDao.getAvailableVersions("postgis", waterSoftware));
		view.setInfo("");
		
		// Get schemas from selected water software
		selectSourceType();
		
		// Get related data
		getProjectData();
		Integer schemaVersion = MainDao.getSchemaVersion();
		view.enableRename(schemaVersion >= FIRST_VERSION_RENAME_AVAILABLE);
		view.enableCopy(schemaVersion >= FIRST_VERSION_COPY_AVAILABLE);

	}
	
	
	private void customizePanel() {
		
		// HECRAS
		if (waterSoftware.equals("HECRAS")) {
			mainFrame.epaSoftFrame.setVisible(false);
        	try {
				mainFrame.hecRasFrame.setMaximum(true);
				mainFrame.hecRasFrame.setVisible(true);
			} catch (PropertyVetoException e) {
				Utils.logError(e);
			}			
		}
		
		// EPASWMM or EPANET
		else {
			if (waterSoftware.equals("EPASWMM")) {
//				epaSoftPanel.setButton4(Utils.getBundleString("ProjectPreferencesController.timeseries"), "showTimeseries"); //$NON-NLS-1$
//				epaSoftPanel.setButton5(Utils.getBundleString("ProjectPreferencesController.arc_catalog"), "showArcCatalog"); //$NON-NLS-1$
//				epaSoftPanel.setButton6(Utils.getBundleString("ProjectPreferencesController.hydrologic_catalog"), "showHydrologyCatalog"); //$NON-NLS-1$
				epaSoftPanel.setOptionsButton(Utils.getBundleString("ProjectPreferencesController.options"), "showInpOptions"); //$NON-NLS-1$
				epaSoftPanel.setDesignButton(Utils.getBundleString("ProjectPreferencesController.raingage"), "showRaingage"); //$NON-NLS-1$
				epaSoftPanel.setReportButton(Utils.getBundleString("ProjectPreferencesController.report_options"), "showReport"); //$NON-NLS-1$
				epaSoftPanel.setSubcatchmentVisible(true);
				if (view.getVersionSoftware().equals("EPASWMM_51006_2D")) {
					epaSoftPanel.setSubcatchmentSelected(true);
					epaSoftPanel.setSubcatchmentEnabled(false);
					// Show warning message when 2D is selected
					if (!mainFrame.getController().getOpeningApp()) {
						String msg = Utils.getBundleString("ProjectPreferencesController.warning_2d"); //$NON-NLS-1$
						Utils.showMessage(msg);
					}
				}
				else {
					epaSoftPanel.setSubcatchmentSelected(false);
					epaSoftPanel.setSubcatchmentEnabled(true);
				}
				epaSoftPanel.exportSelected();
			}
			else if (waterSoftware.equals("EPANET")) {
//				epaSoftPanel.setButton4(Utils.getBundleString("ProjectPreferencesController.emitters"), "showEmitter"); //$NON-NLS-1$
//				epaSoftPanel.setButton5(Utils.getBundleString("ProjectPreferencesController.demands"), "showDemands"); //$NON-NLS-1$
//				epaSoftPanel.setButton6(Utils.getBundleString("ProjectPreferencesController.rules"), "showRules"); //$NON-NLS-1$
				epaSoftPanel.setOptionsButton(Utils.getBundleString("ProjectPreferencesController.options"), "showInpOptionsEpanet"); //$NON-NLS-1$
				epaSoftPanel.setDesignButton(Utils.getBundleString("ProjectPreferencesController.times_values"), "showTimesValues"); //$NON-NLS-1$
				epaSoftPanel.setReportButton(Utils.getBundleString("ProjectPreferencesController.report_options"), "showReportEpanet"); //$NON-NLS-1$
				epaSoftPanel.setSubcatchmentVisible(false);
				epaSoftPanel.setSubcatchmentSelected(false);
			}
			mainFrame.hecRasFrame.setVisible(false);
			mainFrame.epaSoftFrame.setTitle(waterSoftware);
        	try {
				mainFrame.epaSoftFrame.setMaximum(true);
				mainFrame.epaSoftFrame.setVisible(true);
			} catch (PropertyVetoException e) {
				Utils.logError(e);
			}
		}
		
		mainFrame.updateConnectionInfo();
		
	}
	
	
	private boolean checkPreferences() {
		
		view.setInfo("");
		// Check if we have selected a water software
		if (waterSoftware.equals("")) {
			view.setWaterSoftware("EPASWMM");
			changeSoftware();		
			return false;
		}
		// Check if we have an schema selected
		if (view.getSelectedSchema().equals("")) {
			mainFrame.showError(Utils.getBundleString("ProjectPreferencesController.select_project")); //$NON-NLS-1$
			return false;
		}
		return true;
		
	}
	
	
	public boolean applyPreferences() {
		
		// Check if everything is set
		if (!checkPreferences()) {
			view.getFrame().setVisible(true);
			mainFrame.hecRasFrame.setVisible(false);
			mainFrame.epaSoftFrame.setVisible(false);	
			return false;
		}
			
		// Update Project preferences parameters
		mainFrame.putProjectPreferencesParams();		
		
		// Customize buttons and title
		customizePanel();
		
		// Check schema version
		MainDao.checkSchemaVersion();
		schemaChanged();
		
		return true;
		
	}
	
	
	// TODO: Older acceptPreferences. Now has to open EpaSoftPanel
	public void openEpaSoft() {
		if (applyPreferences()) {
			closePreferences();	
		}
	}
	
	
	public void closePreferences() {
		view.getFrame().setVisible(false);	
	}
	
	
	public void openProjectManager() {
		mainFrame.openProjectManager();
	}	
	
	
	// Database configuration
//	private void checkDataManagerTables(String schemaName) {
//		
//		epaSoftPanel.enableMaterials(MainDao.checkTable(schemaName, "cat_mat"));
//		epaSoftPanel.enableCurves(MainDao.checkTable(schemaName, "inp_curve_id"));	
//		epaSoftPanel.enablePatterns(MainDao.checkTable(schemaName, "inp_pattern"));
//		epaSoftPanel.enableControls(MainDao.checkTable(schemaName, "inp_controls"));	
//		epaSoftPanel.enableProjectData(MainDao.checkTable(schemaName, "inp_project_id"));	
//		
//		if (waterSoftware.equals("EPASWMM")) {
//			epaSoftPanel.enableTimeseries(MainDao.checkTable(schemaName, "inp_timser_id"));
//			epaSoftPanel.enableArcCat(MainDao.checkTable(schemaName, "cat_arc"));
//			epaSoftPanel.enableHydrologyCat(MainDao.checkTable(schemaName, "cat_hydrology"));	
//		}
//		else if (waterSoftware.equals("EPANET")) {
//			epaSoftPanel.enableTimeseries(MainDao.checkTable(schemaName, "inp_emitter"));
//			epaSoftPanel.enableArcCat(MainDao.checkTable(schemaName, "inp_demand"));
//			epaSoftPanel.enableHydrologyCat(MainDao.checkTable(schemaName, "inp_rules"));	
//		}
//		
//	}
	
	
	private void checkPostprocessTables(String schemaName) {
		epaSoftPanel.enableResultCatalog(MainDao.checkTable(schemaName, "rpt_result_cat"));
		epaSoftPanel.enableResultSelector(MainDao.checkTable(schemaName, "result_selection"));
	}
	
	
	public void testConnection() {
		
		if (MainDao.isConnected()) {
			closeConnection();
			mainFrame.enableMenuDatabase(false);		
		}
		else {
			if (openConnection()) {
				mainFrame.enableMenuDatabase(true);	
			}
		}
		selectSourceType(false);
		view.enableDbControls(!MainDao.isConnected());		
		
		// Update Status Bar
		mainFrame.updateConnectionInfo();
		
	}	
	
	
	private void closeConnection() {
		
		view.setConnectionText(Utils.getBundleString("open_connection"));
		mainFrame.hecRasFrame.getPanel().enableControls(false);
		MainDao.closeConnectionPostgis();
		mainFrame.showMessage("connection_closed");			
		view.enableDbControls(true);			
		
	}
	
	
	private boolean openConnection() {
		
		// Get parameteres connection from view
		String host = view.getHost();		
		String port = view.getPort();
		String db = view.getDatabase();
		String user = view.getUser();
		String password = view.getPassword();	
		Boolean useSsl = view.isUseSslSelected();	
		
		// Check parameters
		if (host.equals("") || port.equals("") || db.equals("") || user.equals("")) {
			Utils.showError(Utils.getBundleString("ProjectPreferencesController.connection_not_possible")); //$NON-NLS-1$
			return false;
		}
		Utils.getLogger().info("host:"+host+" - port:"+port+" - db:"+db+" - user:"+user);
		
		// Check if Internet is available
		if (!host.equals("localhost") && !host.equals("127.0.0.1")) {
			if (!UtilsFTP.isInternetReachable()) {
				Utils.showError(Utils.getBundleString("ProjectPreferencesController.internet_unavailable"));	 //$NON-NLS-1$
				return false;
			}
		}
		
		// Try to connect to Database
		boolean isConnected = MainDao.setConnectionPostgis(host, port, db, user, password, useSsl, true);
		MainDao.setConnected(isConnected);
		
		if (isConnected) {
			PropertiesDao.getGswProperties().put("POSTGIS_HOST", host);
			PropertiesDao.getGswProperties().put("POSTGIS_PORT", port);
			PropertiesDao.getGswProperties().put("POSTGIS_DATABASE", db);
			PropertiesDao.getGswProperties().put("POSTGIS_USER", user);
			// Save encrypted password
			if (view.isRememberSelected()) {
				PropertiesDao.getGswProperties().put("POSTGIS_PASSWORD", Encryption.encrypt(password));
			} else {
				PropertiesDao.getGswProperties().put("POSTGIS_PASSWORD", "");
			}
			MainDao.setConnectionParams(host, port, db, user, password);
			
			// Get Postgis data and bin Folder
	    	String dataPath = MainDao.getDataDirectory();
	    	PropertiesDao.getGswProperties().put("POSTGIS_DATA", dataPath);
	        Utils.getLogger().info("Connection successful");
	        Utils.getLogger().info("Postgre data directory: " + dataPath);	
	    	Utils.getLogger().info("Postgre version: " + MainDao.checkPostgreVersion());
        	String postgisVersion = MainDao.checkPostgisVersion();	        
        	if (postgisVersion.equals("")) {
        		// Enable Postgis to current Database
        		String sql = "CREATE EXTENSION postgis; CREATE EXTENSION postgis_topology;";
        		MainDao.executeUpdateSql(sql, true, false);			  	
        	}
        	else {
        		Utils.getLogger().info("Postgis version: " + postgisVersion);
        	}
	    	
			view.setConnectionText(Utils.getBundleString("close_connection"));
			mainFrame.showMessage("connection_opened");
			
			// Hecras form
			mainFrame.hecRasFrame.getPanel().enableControls(true);

		} 

		view.enableDbControls(!isConnected);	
		
		return isConnected;
		
	}	
	
	
	public void selectSourceType() {
		selectSourceType(true);
	}
	
	public void selectSourceType(boolean loadVersionSoftwareModel) {

		view.enableConnectionParameters(true);
		epaSoftPanel.enableRunAndImport(true);
		// Check if we already are connected
		if (MainDao.isConnected()) {
			mainFrame.enableMenuDatabase(true);
			view.enableProjectManagement(true);
			if (loadVersionSoftwareModel) {
				view.setVersionSoftwareModel(ConfigDao.getAvailableVersions("postgis", waterSoftware));
			}
			enableWaterSoftwareControls();
		} 
		else {
			mainFrame.enableMenuDatabase(false);
			view.enableProjectManagement(false);
			view.setSchemaModel(null);	
			epaSoftPanel.enableDatabaseButtons(false);
			epaSoftPanel.enableAccept(false);
			hecRasPanel.enableControls(false);
		}
		view.enableDbControls(!MainDao.isConnected());			
		mainFrame.updateConnectionInfo();
				
	}
	
	
	public void enableWaterSoftwareControls() {
		
		Vector<String> schemaList = MainDao.getSchemas(waterSoftware);
		if (schemaList != null && schemaList.size() > 0) {
			setSchema(schemaList.get(0));
		} else {
			setSchema("");
		}
		boolean enabled = view.setSchemaModel(schemaList);
		view.setSelectedSchema(PropertiesDao.getGswProperties().get("SCHEMA"));						
		epaSoftPanel.enablePreprocess(enabled);
		epaSoftPanel.enableAccept(enabled);
		hecRasPanel.enableControls(enabled);
		
	}
	
	
	public void enableHecras(boolean enabled) {
		hecRasPanel.enableControls(!view.getSelectedSchema().equals(""));		
	}
	
	
	public void isConnected() {

		// Check if we already are connected
		if (MainDao.isConnected()) {
			view.setSchemaModel(MainDao.getSchemas(waterSoftware));
			String gswSchema = PropertiesDao.getGswProperties().get("SCHEMA").trim();
			if (!gswSchema.equals("")) {
				view.setSelectedSchema(gswSchema);	
			}
			else {
				schemaChanged();
			}
		} 
		else {
			view.setSchemaModel(null);				
		}
		mainFrame.enableMenuDatabase(MainDao.isConnected());
		
	}	
	
	
	public void schemaChanged() {
		
		MainDao.setWaterSoftware(waterSoftware);		
		if (MainDao.isConnected()) {
			setSchema(view.getSelectedSchema());
			mainFrame.updateConnectionInfo();
			getProjectData();
			Integer schemaVersion = MainDao.getSchemaVersion();
			view.enableRename(schemaVersion >= FIRST_VERSION_RENAME_AVAILABLE);
			view.enableCopy(schemaVersion >= FIRST_VERSION_COPY_AVAILABLE);	
		}
		
	}
	
	
	public void getProjectData() {
		
		view.updateProjectData("", "", "");
		if (MainDao.getSchema().equals("")) return;
		String sql = "SELECT title, author, date FROM "+MainDao.getSchema()+".inp_project_id";
		Vector<Vector<String>> vector_container = MainDao.queryToVector(sql);
		if (vector_container.size() > 0) {
			Vector<String> vector = vector_container.get(0);
			view.updateProjectData(vector.get(0), vector.get(1), vector.get(2));
		}
		
	}
	
	
	public void setSchema(String schemaName) {
		MainDao.setSchema(schemaName);
		//checkDataManagerTables(schemaName);
		checkPostprocessTables(schemaName);
	}
	
	
	public void schemaTest(String schemaName) {
		view.setSelectedSchema(schemaName);
	}
	
	public void enablePreprocess(boolean enabled) {
		epaSoftPanel.enablePreprocess(enabled);
	}
	
	public void setWaterSoftware(String waterSoftware) {
		this.waterSoftware = waterSoftware;
	}
	
	public void setVersionSoftware() {
		view.setVersionSoftwareModel(ConfigDao.getAvailableVersions("postgis", waterSoftware));
	}
	
	
	// Project Management
	private String validateName(String schemaName) {
		
		String validate = schemaName.trim().toLowerCase();
		validate = validate.replace(" ", "_");
		validate = validate.replaceAll("[^\\p{ASCII}]", "");
		return validate;
		
	}
	
	
	public void createSchemaAssistant() {
		
		String defaultSrid = PropertiesDao.getPropertiesFile().get("SRID_DEFAULT", "25831");		
		ProjectPanel projectPanel = new ProjectPanel(defaultSrid);
		NewProjectController npController = new NewProjectController(projectPanel);
		projectPanel.setController(npController);
		npController.setParentPanel(view);
		npController.initModel();
		npController.updateTableModel();
		npController.enableImportData();
		
		// Open New Project dialog
        JDialog projectDialog = Utils.openDialogForm(projectPanel, view, Utils.getBundleString("ProjectPreferencesController.create_project"), PROJECT_DIALOG_WIDTH, PROJECT_DIALOG_HEIGHT); //$NON-NLS-1$
        projectPanel.setParent(projectDialog);
        projectDialog.setVisible(true);
		
	}
	
	
	public void deleteSchema() {
		
		String schemaName = view.getSelectedSchema();
		String msg = Utils.getBundleString("delete_schema_name") + "\n" + schemaName;
		int res = Utils.showYesNoDialog(view, msg);        
        if (res == 0) {     
    		// Execute task: DeleteSchema
    		DeleteSchemaTask task = new DeleteSchemaTask(waterSoftware, schemaName);
            task.setController(this);
            task.setParentPanel(view);
            task.addPropertyChangeListener(this);
            task.execute();
        }
        
	}		
	
	
	public void renameSchema() {
		
		String schemaName = view.getSelectedSchema();
		if (schemaName.equals("")) return;
		
		String newSchemaName = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_schema_name"), schemaName);
		if (newSchemaName == null) {
			return;
		}
		newSchemaName = validateName(newSchemaName);
		if (newSchemaName.equals("")) {
        	mainFrame.showError("schema_valid_name");
			return;
		}
		String sql = "ALTER SCHEMA "+schemaName+" RENAME TO "+newSchemaName;
		Utils.logSql(sql);
		if (MainDao.executeUpdateSql(sql, true)) {
			if (MainDao.checkSchema(schemaName+"_audit")) {
				sql = "ALTER SCHEMA "+schemaName+"_audit RENAME TO "+newSchemaName+"_audit";
				Utils.logSql(sql);
				MainDao.executeUpdateSql(sql, true);	
			}
			selectSourceType(false);
	    	view.setSelectedSchema(newSchemaName);
	    	mainFrame.showMessage(Utils.getBundleString("project_renamed_ok"));
		}
		
	}
	
	
	public void copySchema() {
		
		String schemaName = view.getSelectedSchema();
		if (schemaName.equals("")) return;
		
		String newSchemaName = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_schema_name"), schemaName);
		if (newSchemaName == null) {
			return;
		}
		newSchemaName = validateName(newSchemaName);
		if (newSchemaName.equals("")) {
			mainFrame.showError("schema_valid_name");
			return;
		}

		// Execute task: CopySchema
		CopySchemaTask task = new CopySchemaTask(schemaName, newSchemaName);
        task.setController(this);
        task.setParentPanel(view);
        task.addPropertyChangeListener(this);
        task.execute();
        		
	}
		
	
}