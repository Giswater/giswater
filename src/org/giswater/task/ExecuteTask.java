package org.giswater.task;

import java.io.File;

import javax.swing.SwingWorker;

import org.giswater.controller.EpaSoftController;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.model.Model;
import org.giswater.model.ModelDbf;
import org.giswater.model.ModelPostgis;
import org.giswater.util.Utils;


public class ExecuteTask extends SwingWorker<Void, Void> {
	
	private boolean status;
	private ProjectPreferencesPanel ppPanel;
	private EpaSoftPanel view;
	private EpaSoftController controller;
	private boolean dbSelected;
	
	
    @Override
    public Void doInBackground() {
    	
    	if (dbSelected) {
    		return executePostgis();
    	}
		return executeDbf();
        
    }

    
    private Void executePostgis() {
    	
        boolean continueExec = true;
        status = false;
        
        setProgress(1);

        // Which checks are selected?
        boolean exportSelected = view.isExportSelected();
        boolean execSelected = view.isExecSelected();
        boolean importSelected = view.isImportSelected();          
        
        if (!exportSelected && !execSelected && !importSelected){
            MainClass.mdi.showError("select_option");
            return null;
        }

        // Get schema from Project Preferences view
        String schema = ppPanel.getSelectedSchema();
        if (schema.equals("")){
        	MainClass.mdi.showError("any_schema_selected");
            return null;
        }
        MainDao.setSchema(schema);
        
        // Get software version from Project Preferences view
        String softwareId = ppPanel.getVersionSoftware();
        if (softwareId.equals("")){
        	MainClass.mdi.showError("any_software_selected");
            return null;
        }
        String version = MainDao.getSoftwareVersion("postgis", softwareId);
        Model.setSoftwareVersion(version);
        
		// Get Sqlite Database			
		String sqlitePath = version + ".sqlite";
		if (!Model.setConnectionDrivers(sqlitePath)){
			return null;
		}        
        
		// Get INP and RPT files
		File fileInp = controller.getFileInp();
		File fileRpt = controller.getFileRpt();
		
        // Export to INP
        if (exportSelected) {
            if (fileInp == null) {
            	MainClass.mdi.showError("file_inp_not_selected");
                return null;
            }      
            if (!ModelPostgis.checkSectorSelection()) {
        		int res = Utils.confirmDialog(view, "sector_selection_empty");        
                if (res == 0){            	
                	controller.showSectorSelection();
                }
                return null;
            }               
            boolean selected = view.isSubcatchmentsSelected();
            continueExec = ModelPostgis.processAll(fileInp, selected);
        }

        // Run SWMM
        if (execSelected && continueExec) {
            if (fileInp == null) {
                Utils.showError(view, "file_inp_not_selected");
                return null;
            }            
            if (fileRpt == null) {
                Utils.showError(view, "file_rpt_not_selected");
                return null;
            }                  
            continueExec = ModelPostgis.execEPASOFT(fileInp, fileRpt);
        }

        // Import RPT to Postgis
        if (importSelected && continueExec) {
            if (fileRpt == null) {
                Utils.showError("file_rpt_not_selected");
                return null;
            }            
            String projectName = view.getProjectName();
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
        
        status = true;
        return null;
    	
    }
    
    
    private Void executeDbf() {
    	
		String pathFolderShp = ppPanel.getFolderShp();
		File folderShp = new File(pathFolderShp);
		if (!folderShp.exists()){
			MainClass.mdi.showError("Selected data folder not exists. Please set a valid one");
			return null;
		}
		MainDao.getGswProperties().put("FOLDER_SHP", pathFolderShp);
		MainDao.savePropertiesFile();
        
        // Which checks are selected?
        boolean exportSelected = view.isExportSelected();
        boolean execSelected = view.isExecSelected();
        boolean importSelected = view.isImportSelected();        
        
        if (!exportSelected && !execSelected && !importSelected){
        	MainClass.mdi.showError("select_option");
            return null;
        }
        
        // Get software version from Project Preferences view
		String id = ppPanel.getVersionSoftware();
        if (id.equals("")){
        	MainClass.mdi.showError("any_software_selected");
            return null;
        }
        String version = MainDao.getSoftwareVersion("dbf", id);
        Model.setSoftwareVersion(version);
		
		// Get Sqlite Database			
		String sqlitePath = version + ".sqlite";
		if (!Model.setConnectionDrivers(sqlitePath)){
			return null;
		}
		
		// Check if all necessary files exist
		if (!ModelDbf.checkFiles(pathFolderShp)) {
			return null;
		}
		
		// Get INP template file
		String templatePath = MainDao.getInpFolder()+version+".inp";
		File fileTemplate = new File(templatePath);
		if (!fileTemplate.exists()) {
			MainClass.mdi.showError("inp_error_notfound", templatePath);				
			return null;
		}

		// Process all shapes and output to INP file
		File fileInp = controller.getFileInp();
    	File fileRpt = controller.getFileRpt();
        
        boolean continueExec = true;
        // Export to INP
        if (exportSelected) {
            if (fileInp == null) {
            	MainClass.mdi.showError("file_inp_not_selected");
                return null;
            }   
            continueExec = ModelDbf.processAll(fileInp);
        }

        // Run SWMM
        if (execSelected && continueExec) {
            if (fileInp == null) {
            	MainClass.mdi.showError("file_inp_not_selected");
                return null;
            }   
            if (fileRpt == null) {
                Utils.showError(view, "file_rpt_not_selected");
                return null;
            }                  
            continueExec = ModelPostgis.execEPASOFT(fileInp, fileRpt);
        }

        // Import RPT to Postgis
        if (importSelected && continueExec) {
            if (fileRpt == null) {
            	MainClass.mdi.showError("file_rpt_not_selected");
                return null;
            }              
            String projectName = view.getProjectName();
            if (projectName.equals("")){
            	MainClass.mdi.showError("project_name");
            } 
            else{
           		continueExec = ModelPostgis.importRpt(fileRpt, projectName);
            	Model.closeFile();
            	if (!continueExec){
					MainDao.rollback();
            	}
            }
        }     
        
    	status = true;
    	return null;
    	
    }
    
    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage("process_end");
    	}
		
    }


	public void setController(EpaSoftController controller) {
		this.controller = controller;
	}

	public void setParentPanel(EpaSoftPanel view) {
		this.view = view;		
	}
	
	public void setProjectPreferencesPanel(ProjectPreferencesPanel ppPanel) {
		this.ppPanel = ppPanel;		
	}


	public void setDbSelected(boolean optDatabaseSelected) {
		this.dbSelected = optDatabaseSelected;		
	}
    
    
}