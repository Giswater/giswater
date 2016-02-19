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

import java.sql.ResultSet;

import javax.swing.JDialog;

import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.dialog.catalog.AbstractCatalogDialog;
import org.giswater.gui.dialog.catalog.ArcCatalogDialog;
import org.giswater.gui.dialog.catalog.ControlsDialog;
import org.giswater.gui.dialog.catalog.CurvesDialog;
import org.giswater.gui.dialog.catalog.DemandDialog;
import org.giswater.gui.dialog.catalog.EmitterDialog;
import org.giswater.gui.dialog.catalog.HydrologyCatalogDialog;
import org.giswater.gui.dialog.catalog.MaterialsDialog;
import org.giswater.gui.dialog.catalog.PatternsDialog;
import org.giswater.gui.dialog.catalog.ProjectDialog;
import org.giswater.gui.dialog.catalog.TimeseriesDialog;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.GisPanel;
import org.giswater.gui.panel.ProjectManagerPanel;
import org.giswater.model.table.TableModelCurves;
import org.giswater.model.table.TableModelTimeseries;
import org.giswater.util.Utils;


public class ProjectManagerController extends AbstractController {

	private ProjectManagerPanel view;
	private MainFrame mainFrame;
	
	private static final Integer GIS_DIALOG_WIDTH = 420; 
	private static final Integer GIS_DIALOG_HEIGHT = 245; 	


	public ProjectManagerController(ProjectManagerPanel pmPanel, MainFrame mf) {
		
		this.view = pmPanel;	
		this.mainFrame = mf;
    	this.usersFolder = MainDao.getGiswaterUsersFolder(); 
	    view.setController(this);    
	    
	}
	
	
	public void openProjectPreferences() {	
		mainFrame.openProjectPreferences();		
	}
	
	
	public void openEpaSoft() {	
		mainFrame.openEpaSoft();		
	}
		

	public void createGisProject() {
		
		GisPanel gisPanel = new GisPanel();
		JDialog gisDialog = 
			Utils.openDialogForm(gisPanel, view, Utils.getBundleString("ProjectPreferencesController.create_gis"), GIS_DIALOG_WIDTH, GIS_DIALOG_HEIGHT); //$NON-NLS-1$
		gisPanel.setParent(gisDialog);
        gisDialog.setVisible(true);
        
	}
	

	public void showProjectData() {
		ResultSet rs = MainDao.getTableResultset("inp_project_id");
		if (rs == null) return;		
		ProjectDialog dialog = new ProjectDialog();
		showCatalog(dialog, rs);
	}	
	
	
	public void showArcCatalog() {
		ResultSet rs = MainDao.getTableResultset("cat_arc");
		if (rs == null) return;		
		ArcCatalogDialog dialog = new ArcCatalogDialog();
		showCatalog(dialog, rs);
	}	
	
	
	public void showHydrologyCatalog() {
		ResultSet rs = MainDao.getTableResultset("cat_hydrology");
		if (rs == null) return;		
		HydrologyCatalogDialog dialog = new HydrologyCatalogDialog();
		showCatalog(dialog, rs);
	}	
	
	
	public void showMaterialCatalog() {
		
		ResultSet rs = MainDao.getTableResultset("cat_mat");
		if (rs == null) return;
		MaterialsDialog dialog = new MaterialsDialog();
		if (PropertiesDao.getWaterSoftware().equals("EPASWMM")){
			dialog.setOther(Utils.getBundleString("EpaSoftController.n"), "n");
		}
		else{
			dialog.setOther(Utils.getBundleString("EpaSoftController.roughness"), "roughness");
		}		
		showCatalog(dialog, rs);
		
	}	
	
	
	public void showPatterns() {
		
		ResultSet rs = MainDao.getTableResultset("inp_pattern");
		if (rs == null) return;		
		PatternsDialog dialog = new PatternsDialog();
		if (PropertiesDao.getWaterSoftware().equals("EPASWMM")) {
			dialog.enableType(true);
		} else {
			dialog.enableType(false);
		}
		showCatalog(dialog, rs);
		
	}	
	
	
	public void showTimeseries() {
		
		ResultSet rsMain = MainDao.getTableResultset("inp_timser_id", "*", "id");
		ResultSet rsRelated = MainDao.getTableResultset("inp_timeseries", "*", "id");		
		if (rsMain == null || rsRelated == null) return;		
		TimeseriesDialog dialog = new TimeseriesDialog();
		TableModelTimeseries model = new TableModelTimeseries(rsRelated);
		dialog.setTable(model);
		showCatalog(dialog, rsMain);
		
	}	
	
	
	public void showCurves() {
		
		ResultSet rsMain = MainDao.getTableResultset("inp_curve_id", "*", "id");
		ResultSet rsRelated = MainDao.getTableResultset("inp_curve", "*", "id");		
		if (rsMain == null || rsRelated == null) return;		
		CurvesDialog dialog = new CurvesDialog();
		TableModelCurves model = new TableModelCurves(rsRelated);
		dialog.setTable(model);
		showCatalog(dialog, rsMain);
		
	}		
	
	
	public void showEmitter() {
		ResultSet rs = MainDao.getTableResultset("inp_emitter");
		if (rs == null) return;		
		EmitterDialog dialog = new EmitterDialog();
		showCatalog(dialog, rs);
	}
	
	public void showDemands() {
		ResultSet rs = MainDao.getTableResultset("inp_demand");
		if (rs == null) return;		
		DemandDialog dialog = new DemandDialog();
		showCatalog(dialog, rs);		
	}
	
	public void showRules() {
		ResultSet rs = MainDao.getTableResultset("inp_rules");
		if (rs == null) return;		
		ControlsDialog dialog = new ControlsDialog();
		dialog.setTitle(Utils.getBundleString("EpaSoftController.table_rules"));
		showCatalog(dialog, rs);
	}
	
	public void showControls() {
		ResultSet rs = MainDao.getTableResultset("inp_controls");
		if (rs == null) return;		
		ControlsDialog dialog = new ControlsDialog();
		dialog.setTitle(Utils.getBundleString("EpaSoftController.table_controls")); //$NON-NLS-1$
		showCatalog(dialog, rs);
	}
	
	
	private void showCatalog(AbstractCatalogDialog dialog, ResultSet rs) {
		
		CatalogController controller = new CatalogController(dialog, rs);
        if (MainDao.getNumberOfRows(rs) == 0) {
            controller.create();
        }
        else {
            controller.moveFirst();
        }		
		dialog.setModal(true);
		dialog.setLocationRelativeTo(null);   
		dialog.setVisible(true);		
		
	}		
	
	
}