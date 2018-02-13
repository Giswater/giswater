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
package org.giswater.task;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.giswater.controller.EpaSoftController;
import org.giswater.dao.ConfigDao;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.model.ExecuteEpa;
import org.giswater.model.ExportToInp;
import org.giswater.model.ImportRpt;
import org.giswater.model.Model;
import org.giswater.util.Utils;


public class ExecuteTask extends SwingWorker<Void, Void> {
	
	private boolean status;
	private ProjectPreferencesPanel ppPanel;
	private EpaSoftPanel view;
	private EpaSoftController controller;
	private boolean onlyCheck;
	
	
    @Override
    public Void doInBackground() {
    	return executePostgis();       
    }

    
    private Void executePostgis() {
    	
        boolean continueExec = true;
        status = false;
        
        setProgress(1);

        // Which checks are selected?
        boolean exportSelected = view.isExportSelected();
        boolean execSelected = view.isExecSelected();
        boolean importSelected = view.isImportSelected();          
        
        if (!exportSelected && !execSelected && !importSelected) {
            MainClass.mdi.showError("select_option");
            return null;
        }

        // Get schema from Project Preferences view
        String schema = ppPanel.getSelectedSchema();
        if (schema.equals("")) {
        	MainClass.mdi.showError("any_schema_selected");
            return null;
        }
        MainDao.setSchema(schema);
        
        // Get software version from Project Preferences view
        String softwareId = ppPanel.getVersionSoftware();
        if (softwareId.equals("")) {
        	MainClass.mdi.showError("any_software_selected");
            return null;
        }
        String version = ConfigDao.getSoftwareVersion(softwareId);
        Model.setSoftwareVersion(version);
        
		// Get Sqlite Database			
		String sqlitePath = version + ".sqlite";
		if (!Model.setConnectionDrivers(sqlitePath)) {
			return null;
		}        
		
		// Get INP and RPT files
		File fileInp = controller.getFileInp();
		File fileRpt = controller.getFileRpt();
		
		// If EPASWMM 2D and Execute EPA software are selected, check if raster file 'topo.asc' exists
		if (softwareId.equals("EPASWMM_51006_2D") && execSelected) {
            if (fileInp == null) {
            	MainClass.mdi.showError("file_inp_not_selected");
                return null;
            } 
            String rasterPath = fileInp.getParentFile().getAbsolutePath() + File.separator + "topo.asc";
            File rasterFile = new File(rasterPath);
            if (!rasterFile.exists()) {
            	String msg = Utils.getBundleString("ExecuteTask.cannot_2d_mode") + //$NON-NLS-1$
            		Utils.getBundleString("ExecuteTask.raster_not_found") + rasterPath; //$NON-NLS-1$
            	Utils.showError(view, msg);
            	return null;
            }
		}
		
		// Check if we're working with EPASWMM version 5.1
        boolean isVersion51 = false;
        if (!ppPanel.getVersionSoftware().equals("EPASWMM_50022")) {
        	isVersion51 = true;
        }		
		
        // Export to INP
        if (exportSelected) {
            if (fileInp == null) {
            	MainClass.mdi.showError("file_inp_not_selected");
                return null;
            }      
            if (!ExportToInp.checkSectorSelection()) {
        		int res = Utils.showYesNoDialog(view, "sector_selection_empty");        
                if (res == JOptionPane.YES_NO_OPTION) {            	
                	controller.showSectorSelection();
                }
                return null;
            }   
            String resultName = view.getResultName();
            if (resultName.equals("")) {
                Utils.showError("project_name");
                return null;
            }       
            boolean selected = view.isSubcatchmentsSelected();
            continueExec = ExportToInp.process(fileInp, selected, isVersion51, resultName, onlyCheck);
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
            continueExec = ExecuteEpa.process(fileInp, fileRpt);
        }

        // Import RPT to Postgis
        if (importSelected && continueExec) {
            if (fileRpt == null) {
                Utils.showError("file_rpt_not_selected");
                return null;
            }            
            String resultName = view.getResultName();
            if (resultName.equals("")) {
                Utils.showError("project_name");
            } 
            else {
           		continueExec = ImportRpt.process(fileRpt, resultName);
            	Model.closeFile();
            	if (!continueExec) {
					MainDao.rollback();
            	}
            }
        }
        
        MainDao.commit();
        status = true;
        return null;
    	
    }
        
    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage("process_end");
    	}
    	if (MainClass.function != null) {
    		System.exit(0);
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

	public void setOnlyCheck(boolean onlyCheck) {
		this.onlyCheck = onlyCheck;
	}
   
    
}