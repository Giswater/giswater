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
import java.sql.ResultSet;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.MainDao;
import org.giswater.gui.dialog.catalog.AbstractCatalogDialog;
import org.giswater.gui.dialog.catalog.ArcCatalogDialog;
import org.giswater.gui.dialog.catalog.CurvesDialog;
import org.giswater.gui.dialog.catalog.HydrologyCatalogDialog;
import org.giswater.gui.dialog.catalog.MaterialsDialog;
import org.giswater.gui.dialog.catalog.PatternsDialog;
import org.giswater.gui.dialog.catalog.ProjectDialog;
import org.giswater.gui.dialog.catalog.TimeseriesDialog;
import org.giswater.gui.dialog.options.AbstractOptionsDialog;
import org.giswater.gui.dialog.options.OptionsDialog;
import org.giswater.gui.dialog.options.OptionsEpanetDialog;
import org.giswater.gui.dialog.options.RaingageDialog;
import org.giswater.gui.dialog.options.ReportDialog;
import org.giswater.gui.dialog.options.ReportEpanetDialog;
import org.giswater.gui.dialog.options.ResultCatDialog;
import org.giswater.gui.dialog.options.ResultCatEpanetDialog;
import org.giswater.gui.dialog.options.ResultSelectionDialog;
import org.giswater.gui.dialog.options.TimesDialog;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.gui.panel.SectorSelectionPanel;
import org.giswater.model.Model;
import org.giswater.model.ModelDbf;
import org.giswater.model.ModelPostgis;
import org.giswater.model.table.TableModelCurves;
import org.giswater.model.table.TableModelTimeseries;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class EpaSoftController extends AbstractController{

	private EpaSoftPanel view;
	private ProjectPreferencesPanel ppPanel;
	private MainFrame mainFrame;
    private PropertiesMap gswProp;
    private File fileInp;
    private File fileRpt;
    private String projectName;
    private boolean exportChecked;
    private boolean execChecked;
    private boolean importChecked; 
	
	private File dirShp;
	private boolean readyShp = false;

    
    public EpaSoftController(EpaSoftPanel view, MainFrame mf) {
    	
    	this.view = view;	
    	this.mainFrame = mf;
    	this.ppPanel = mainFrame.ppFrame.getPanel();
        this.gswProp = MainDao.getGswProperties();
	    view.setController(this);        
    	
	}
	
	
	public void showSectorSelection(){
		SectorSelectionPanel panel = new SectorSelectionPanel();
        JDialog dialog = Utils.openDialogForm(panel, view, "Sector Selection", 380, 280);
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
        File file = new File(gswProp.get("FILE_INP", usersFolder));	
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
        File file = new File(gswProp.get("FILE_RPT", usersFolder));	
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
        gswProp.put("FILE_INP", fileInp.getAbsolutePath());
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
        gswProp.put("FILE_RPT", fileRpt.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }
    
    
    // TODO:
    public void execute(){
       	
    	view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	Boolean dbSelected = ppPanel.getOptDatabaseSelected();
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

        // Get schema from Project Preferences view
        String schema = ppPanel.getSelectedSchema();
        if (schema.equals("")){
            Utils.showError(view, "any_schema_selected");
            return;
        }
        MainDao.setSchema(schema);
        
        // Get software version from Project Preferences view
        String softwareId = ppPanel.getVersionSoftware();
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
        
        // Get software version from Project Preferences view
		String id = ppPanel.getVersionSoftware();
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
		String templatePath = MainDao.getInpFolder()+version+".inp";
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
    

	
	// Data Manager
	public void showProjectId(){
		ResultSet rs = MainDao.getTableResultset("inp_project_id");
		if (rs == null) return;		
		ProjectDialog dialog = new ProjectDialog();
		showCatalog(dialog, rs);
	}	
	
	
	public void showArcCatalog(){
		ResultSet rs = MainDao.getTableResultset("cat_arc");
		if (rs == null) return;		
		ArcCatalogDialog dialog = new ArcCatalogDialog();
		showCatalog(dialog, rs);
	}	
	
	
	public void showHydrologyCatalog(){
		ResultSet rs = MainDao.getTableResultset("cat_hydrology");
		if (rs == null) return;		
		HydrologyCatalogDialog dialog = new HydrologyCatalogDialog();
		showCatalog(dialog, rs);
	}	
	
	
	public void showMaterialCatalog(){
		
		ResultSet rs = MainDao.getTableResultset("cat_mat");
		if (rs == null) return;
		MaterialsDialog dialog = new MaterialsDialog();
		if (ppPanel.getWaterSoftware().equals("EPASWMM")){
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
		if (ppPanel.getWaterSoftware().equals("EPASWMM")){
			dialog.enableType(true);
		} else{
			dialog.enableType(false);
		}
		showCatalog(dialog, rs);
		
	}	
	
	
	public void showTimeseries(){
		
		ResultSet rsMain = MainDao.getTableResultset("inp_timser_id", "*", "id");
		ResultSet rsRelated = MainDao.getTableResultset("inp_timeseries", "*", "id");		
		if (rsMain == null || rsRelated == null) return;		
		TimeseriesDialog dialog = new TimeseriesDialog();
		TableModelTimeseries model = new TableModelTimeseries(rsRelated);
		dialog.setTable(model);
		showCatalog(dialog, rsMain);
		
	}	
	
	
	public void showCurves(){
		
		ResultSet rsMain = MainDao.getTableResultset("inp_curve_id", "*", "id");
		ResultSet rsRelated = MainDao.getTableResultset("inp_curve", "*", "id");		
		if (rsMain == null || rsRelated == null) return;		
		CurvesDialog dialog = new CurvesDialog();
		TableModelCurves model = new TableModelCurves(rsRelated);
		dialog.setTable(model);
		showCatalog(dialog, rsMain);
		
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
	
	
	
	// Analysis
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
        	Utils.showMessage(view, errorMsg);
        }
	    
	}


	// TODO: Update view content when frame is activated or...
	public void updateView() {
		
		// Get parameters from current gsw file
		String software = gswProp.get("SOFTWARE");
		view.setTitle(software);
		String storage = gswProp.get("STORAGE");
		if (storage.equals("DBF")){
			view.enableDatabaseButtons(false);
		}
		else{
			view.enableDatabaseButtons(true);
		}
				
	}	
	
	
	public void closePanel(){
		view.getFrame().setVisible(false);
	}
	
	
}