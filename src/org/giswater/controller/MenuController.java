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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.MainDao;
import org.giswater.gui.dialog.about.AcknowledgmentDialog;
import org.giswater.gui.dialog.about.LicenseDialog;
import org.giswater.gui.dialog.about.WelcomeDialog;
import org.giswater.gui.dialog.catalog.AbstractCatalogDialog;
import org.giswater.gui.dialog.catalog.ConduitDialog;
import org.giswater.gui.dialog.catalog.CurvesDialog;
import org.giswater.gui.dialog.catalog.MaterialsDialog;
import org.giswater.gui.dialog.catalog.PatternsDialog;
import org.giswater.gui.dialog.catalog.ProjectDialog;
import org.giswater.gui.dialog.catalog.TimeseriesDialog;
import org.giswater.gui.dialog.options.AbstractOptionsDialog;
import org.giswater.gui.dialog.options.ResultCatDialog;
import org.giswater.gui.dialog.options.ResultCatEpanetDialog;
import org.giswater.gui.dialog.options.ResultSelectionDialog;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.DatabasePanel;
import org.giswater.gui.panel.EpaPanel;
import org.giswater.gui.panel.GisPanel;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.model.table.TableModelCurves;
import org.giswater.model.table.TableModelTimeseries;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MenuController {

	private MainFrame view;
	private PropertiesMap prop;
	
	private final String URL_MANUAL = "http://www.giswater.org/Documentation";	
	private final String URL_REFERENCE = "http://www.giswater.org/node/75";
	private final String URL_WEB = "http://www.giswater.org";
	private final String VERSION_CODE = "1.0.144";

	
	public MenuController(MainFrame mainFrame) {
		this.view = mainFrame;
		this.prop = MainDao.getPropertiesFile();
		view.setControl(this);
		Utils.getLogger().info("Giswater version: "+VERSION_CODE);
	}
	

	public void action(String actionCommand) {

		Method method;
		try {
			if (Utils.getLogger() != null) {
				Utils.getLogger().info(actionCommand);
			}
			method = this.getClass().getMethod(actionCommand);
			method.invoke(this);
		} catch (Exception e) {
			if (Utils.getLogger() != null) {
				Utils.logError(e);
			} else {
				Utils.showError(e);
			}
		}

	}
	
	
	// Menu Project
	public void gswNew(){ }

	
	public void gswOpen(){
		gswOpen(true);
	}
	
	public void gswOpen(boolean chooseFile){
		
		String gswPath = "";
		if (chooseFile){
			gswPath = gswChooseFile();
			if (gswPath == "") return;
			MainDao.setGswPath(gswPath);
			prop.put("FILE_GSW", gswPath);			
		}
		else{
			gswPath = MainDao.getGswPath();
		}
            
		if (gswPath == "") return;
		
        // Load .gsw file into memory
		MainDao.loadGswPropertiesFile();
		
		// Update panels
		updateDatabasePanel();
		updateHecrasPanel();
    	EpaPanel epanetPanel = view.epanetFrame.getPanel();
    	updateEpaPanel("EPANET", epanetPanel);
    	EpaPanel swmmPanel = view.swmmFrame.getPanel();        	
    	updateEpaPanel("EPASWMM", swmmPanel);  		
    	updateGisPanel();

        // Update frames and title
    	view.updateFrames();
    	view.updateTitle(gswPath);
		
	}

	
	public void gswSave(){
		view.saveGswFile();
	}

	
	public void gswSaveAs(){
		String gswPath = gswChooseFile(true);
		if (gswPath == "") return;		
    	MainDao.setGswPath(gswPath);
    	view.saveGswFile();
    	view.updateTitle(gswPath);    	
	}

	
    private void updateDatabasePanel(){
    	
    	DatabasePanel dbPanel = view.dbFrame.getPanel();
    	dbPanel.setHost(MainDao.getGswProperties().get("POSTGIS_HOST"));
    	dbPanel.setPort(MainDao.getGswProperties().get("POSTGIS_PORT"));
    	dbPanel.setDatabase(MainDao.getGswProperties().get("POSTGIS_DATABASE"));
    	dbPanel.setUser(MainDao.getGswProperties().get("POSTGIS_USER"));		
    	Boolean remember = Boolean.parseBoolean(MainDao.getGswProperties().get("POSTGIS_REMEMBER"));
        dbPanel.setRemember(remember);
        if (remember){
        	dbPanel.setPassword(Encryption.decrypt(MainDao.getGswProperties().get("POSTGIS_PASSWORD")));        	
        } else{
        	dbPanel.setPassword("");
        }
    	Boolean autoConnect = Boolean.parseBoolean(prop.get("AUTOCONNECT_POSTGIS"));
        Boolean initDb = Boolean.parseBoolean(MainDao.getGswProperties().get("INIT_DB", "false"));	    
        if (initDb){
        	MainDao.initializeDatabase();
        	dbPanel.setDatabase("giswater_ddb");
        }
       	if (autoConnect && remember){
       		MainDao.silenceConnection();
       		MainDao.getGswProperties().put("INIT_DB", "false");
        }
        
        // Update text open/close button
		if (MainDao.isConnected()){
			dbPanel.setConnectionText(Utils.getBundleString("close_connection"));
			view.enableCatalog(true);
		}
		else{
			dbPanel.setConnectionText(Utils.getBundleString("open_connection"));
			view.enableCatalog(false);
		}
		
	}	    
    

    private void updateEpaPanel(String software, EpaPanel epaPanel){
    	epaPanel.setFolderShp(MainDao.getGswProperties().get(software+"_FOLDER_SHP"));
    	epaPanel.setFileInp(MainDao.getGswProperties().get(software+"_FILE_INP"));
    	epaPanel.setFileRpt(MainDao.getGswProperties().get(software+"_FILE_RPT"));
    	epaPanel.setProjectName(MainDao.getGswProperties().get(software+"_PROJECT_NAME"));
    	epaPanel.setSelectedSchema(MainDao.getGswProperties().get(software+"_SCHEMA"));   
		String storage = MainDao.getGswProperties().get(software+"_STORAGE").toUpperCase();
		if (storage.equals("DATABASE")){
			epaPanel.setDatabaseSelected(true);
		}
		else if (storage.equals("DBF")){
			epaPanel.setDbfSelected(true);
		}
		epaPanel.selectSourceType();    	
	}   
    
 
    private void updateHecrasPanel(){
    	HecRasPanel hecRasPanel = view.hecRasFrame.getPanel();
    	hecRasPanel.setFileAsc(MainDao.getGswProperties().get("HECRAS_FILE_ASC"));
    	hecRasPanel.setFileSdf(MainDao.getGswProperties().get("HECRAS_FILE_SDF"));
    	hecRasPanel.setSelectedSchema(MainDao.getGswProperties().get("HECRAS_SCHEMA"));
	}    
    
    
    private void updateGisPanel(){
    	GisPanel gisPanel = view.gisFrame.getPanel();
    	gisPanel.setProjectFolder(MainDao.getGswProperties().get("GIS_FOLDER"));
    	gisPanel.setProjectName(MainDao.getGswProperties().get("GIS_NAME"));
    	gisPanel.setProjectSoftware(MainDao.getGswProperties().get("GIS_SOFTWARE"));
    	gisPanel.setDataStorage(MainDao.getGswProperties().get("GIS_TYPE"));  
    	gisPanel.setSelectedSchema(MainDao.getGswProperties().get("GIS_SCHEMA"));      	
	}       
    
    
	private String gswChooseFile(){
		return gswChooseFile(false);
	}
	
	private String gswChooseFile(boolean save){
		
		String path = "";
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("GSW extension file", "gsw");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("GSW file"));
        if (save){
        	chooser.setApproveButtonText("Save");        
        }
        File fileProp = new File(prop.get("FILE_GSW", System.getProperty("user.home")));	
        chooser.setCurrentDirectory(fileProp.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            path = file.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".gsw";
                file = new File(path);
            }
        }
        
        return path;
        
	}
	
	
	// Menu Software
	public void openSwmm() {
		view.openSwmm();
	}

	public void openEpanet() {
		view.openEpanet();
	}

	public void openHecras() {
		view.openHecras();
	}

	public void showSoftware() {
		view.openSoftware();
	}

	public void showDatabase() {
		view.openDatabase();
	}

	public void showGisProject() {
		view.openGisProject();
	}	
	
	public void openUserManual() {
		Utils.openWeb(URL_MANUAL);
	}
	
	public void openReferenceGuide() {
		Utils.openWeb(URL_REFERENCE);
	}
	
	public void openWeb() {
		Utils.openWeb(URL_WEB);
	}	

	
	public void showProjectId(){
		ResultSet rs = MainDao.getTableResultset("inp_project_id");
		if (rs == null) return;		
		ProjectDialog dialog = new ProjectDialog();
		showCatalog(dialog, rs);
	}	
	
	
	public void showConduit(){
		ResultSet rs = MainDao.getTableResultset("cat_arc");
		if (rs == null) return;		
		ConduitDialog dialog = new ConduitDialog();
		showCatalog(dialog, rs);
	}	
	
	
	public void showMaterials(){
		ResultSet rs = MainDao.getTableResultset("cat_mat");
		if (rs == null) return;
		MaterialsDialog dialog = new MaterialsDialog();
		if (view.swmmFrame.isSelected()){
			dialog.setName("n");
		}
		else{
			dialog.setName("roughness");
		}		
		showCatalog(dialog, rs);
	}	
	
	
	public void showPatterns(){
		ResultSet rs = MainDao.getTableResultset("inp_pattern");
		if (rs == null) return;		
		PatternsDialog dialog = new PatternsDialog();
		dialog.enableType(view.swmmFrame.isSelected());
		showCatalog(dialog, rs);
	}	
	
	
	public void showTimeseries(){
		
		ResultSet rsMain = MainDao.getTableResultset("inp_timser_id");
		ResultSet rsRelated = MainDao.getTableResultset("inp_timeseries");		
		if (rsMain == null || rsRelated == null) return;		
		TimeseriesDialog dialog = new TimeseriesDialog();
		TableModelTimeseries model = new TableModelTimeseries(rsRelated);
		dialog.setTable(model);
		showCatalog(dialog, rsMain);
		
	}	
	
	
	public void showCurves(){
		
		ResultSet rsMain = MainDao.getTableResultset("inp_curve_id");
		ResultSet rsRelated = MainDao.getTableResultset("inp_curve");		
		if (rsMain == null || rsRelated == null) return;		
		CurvesDialog dialog = new CurvesDialog();
		TableModelCurves model = new TableModelCurves(rsRelated);
		dialog.setTable(model);
		showCatalog(dialog, rsMain);
		
	}		

	
	public void scenarioCatalog(){
		
		ResultSet rs = MainDao.getTableResultset("rpt_result_cat");
		if (rs == null) return;		
		String softwareName = MainDao.getSoftwareName();
		AbstractOptionsDialog dialog = null;
		if (softwareName.equals("EPASWMM")){
			dialog = new ResultCatDialog();	
		}
		else{
			dialog = new ResultCatEpanetDialog();
		}
		showOptions(dialog, rs, "result_cat_empty");
		
	}	
	
	
	public void scenarioManagement(){
		
		ResultSet rs = MainDao.getTableResultset("result_selection");
		if (rs == null) return;		
		ResultSelectionDialog dialog = new ResultSelectionDialog();
		showOptions(dialog, rs, "result_selection_empty");
        
	}		
	
	
	private void showCatalog(AbstractCatalogDialog dialog, ResultSet rs){
		
		CatalogController controller = new CatalogController(dialog, rs);
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
	

	private void showOptions(AbstractOptionsDialog dialog, ResultSet rs, String errorMsg){
		
		// Only show form if exists one record
		OptionsController controller = new OptionsController(dialog, rs);
        if (MainDao.getRowCount(rs) != 0){
            controller.moveFirst();
    	    dialog.setModal(true);
    	    dialog.setLocationRelativeTo(null);   
    	    dialog.setVisible(true);	
        }
        else{
        	Utils.showMessage(errorMsg);
        }
	    
	}	
	
	
	// Menu Configuration
	public void sampleEpanet(){
		createSampleSchema("epanet");
	}

	public void sampleEpaswmm(){
		createSampleSchema("epaswmm");
	}

	public void sampleHecras(){
       	createSampleSchema("hecras");
	}
	
	
	private void createSampleSchema(String softwareName){
		
		// Get default SRID. Never ask user
		String sridValue = prop.get("SRID_DEFAULT", "23031");		
		if (sridValue.equals("")) return;

		// Ask confirmation
		String msg = "Schema called 'sample_"+softwareName+"' will be created with SRID 23031.\nDo you wish to continue?";
		int res = Utils.confirmDialog(msg, "Create DB sample");
		if (res != 0) return; 
		
		String schemaName = "sample_"+softwareName;
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		boolean status = true;
		if (softwareName.equals("hecras")){
			status = MainDao.createSchemaHecRas(softwareName, schemaName, sridValue);
		}
		else{
			status = MainDao.createSchema(softwareName, schemaName, sridValue);
		}
		
		// Once schema has been created, load data 
		if (status){
			try {			
				String folderRoot = new File(".").getCanonicalPath() + File.separator;				
				// From sample .sql file					
				String filePath = folderRoot+"samples/sample_"+softwareName+".sql";	 
		    	Utils.getLogger().info("Reading file: "+filePath); 				
		    	String content = Utils.readFile(filePath);
				Utils.logSql(content);		
				boolean result = MainDao.executeSql(content, true);		
				if (!result) return;
				if (softwareName.equals("hecras")){				
					// Trough Load Raster
					String rasterPath = folderRoot+"samples/sample_mdt.asc";	 						
					if (MainDao.loadRaster(schemaName, rasterPath)){
						Utils.showMessage("schema_creation_completed", schemaName);
					}						
				}	
				else{
					Utils.showMessage("schema_creation_completed", schemaName);
				}
			} catch (Exception e) {
	            Utils.showError(e);
			}			
		}
		
		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	
	// Menu About 
	// TODO: i18n
	public void showWelcome() {
		
		String title = "Welcome";
		String info = "Welcome to Giswater, the EPANET, EPA SWMM and HEC-RAS communication tool";
		String info2 = "Please read the documentation and enjoy using the software";
		WelcomeDialog about = new WelcomeDialog(title, info, info2, "Version: " + VERSION_CODE);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
		
	}

	
	public void showLicense() {
		
		String title = "License";
		String info = "This product as a whole is distributed under the GNU General Public License version 3";
		String info1 = "<html><p align=\"justify\">\"This product is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; " + 
				"without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. " +
				"See the GNU General Public License for more details\u201D</p></html>";
		String info2Begin = "<html><p align=\"justify\"><font size='2'>";
		String info2Body = 
				"THIS VERSION IS PROVIDED BY GISWATER ASSOCIATION.<br> " + 
				"Mention of trade names or commercial products does not constitute endorsement or recommendation for use. " + 
				"Although It has been subjected to technical review before being released and although it has made a considerable effort " +
				"to assure that the results obtained are correct, the computer programs are experimental. " + 
				"Therefore the author and TECNICSASSOCIATS are not responsible and assume no " +
				"liability whatsoever for any results or any use made of the results obtained from these programs, nor for any damages " +
				"or litigation that result from the use of these programs for any purpose.";
		String info2End = "</font></p></html>";		
		String info2 = info2Begin + info2Body + info2End;
		String info3 = "View license file";
		LicenseDialog about = new LicenseDialog(title, info, info1, info2, info3);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
		
	}

	
	public void showAcknowledgment() {
		
		String title = "Acknowledgment";
		String info = "Developers, project collaborators, testers and people entrusted are part of Giswater Team";
		String info2 = "<HTML>Thanks to <i>Gemma Garcia, Andreu Rodríguez, Josep Lluís Sala, Roger Erill, Sergi Muñoz,<br>" +
			" Joan Cervan, David Escala, Abel García, Carlos López, Jordi Yetor, Allen Bateman," +
			" Vicente de Medina, Xavier Torret</i> and <i>David Erill</i></HTML>";
		AcknowledgmentDialog about = new AcknowledgmentDialog(title, info, info2);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
		
	}
	

}