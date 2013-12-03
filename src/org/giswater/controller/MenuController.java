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
 *   David Erill <daviderill79@gmail.com>
 */
package org.giswater.controller;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import org.giswater.controller.catalog.DefaultCatalogController;
import org.giswater.controller.options.DefaultOptionsController;
import org.giswater.dao.MainDao;
import org.giswater.gui.dialog.about.LicenseDialog;
import org.giswater.gui.dialog.about.VersionDialog;
import org.giswater.gui.dialog.about.WelcomeDialog;
import org.giswater.gui.dialog.catalog.AbstractCatalogDialog;
import org.giswater.gui.dialog.catalog.ConduitDialog;
import org.giswater.gui.dialog.catalog.CurvesDialog;
import org.giswater.gui.dialog.catalog.MaterialsDialog;
import org.giswater.gui.dialog.catalog.PatternsDialog;
import org.giswater.gui.dialog.catalog.TimeseriesDialog;
import org.giswater.gui.dialog.options.AbstractOptionsDialog;
import org.giswater.gui.dialog.options.ResultCatDialog;
import org.giswater.gui.frame.MainFrame;
import org.giswater.model.table.TableModelCurves;
import org.giswater.model.table.TableModelTimeseries;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MenuController {

	private MainFrame view;
	private PropertiesMap prop;

	
	public MenuController(MainFrame mainFrame) {
		this.view = mainFrame;
		this.prop = MainDao.getPropertiesFile();
		view.setControl(this);
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

	public void openHelp() {
		String file = MainDao.getConfigPath();
		Utils.getLogger().info(file);		
		Utils.openFile(file);
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
		//DefaultTableModel model = MainDao.buildTableModel(rsRelated);
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

	
	public void showResultCat(){
		ResultSet rsMain = MainDao.getTableResultset("rpt_result_cat");
		if (rsMain == null) return;		
		ResultCatDialog dialog = new ResultCatDialog();
		showOptions(dialog, rsMain);		
	}	
	
	
	private void showCatalog(AbstractCatalogDialog dialog, ResultSet rs){
		DefaultCatalogController controller = new DefaultCatalogController(dialog, rs);
		controller.moveFirst();
		dialog.setModal(true);
		dialog.setLocationRelativeTo(null);   
		dialog.setVisible(true);		        
	}
	

	private void showOptions(AbstractOptionsDialog dialog, ResultSet rs){
		
		DefaultOptionsController controller = new DefaultOptionsController(dialog, rs);
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
	
	
	// TODO: i18n
	public void showWelcome() {
		
		String title = "Welcome";
		String info = "Welcome to Giswater, the EPANET, EPASWMM and HEC-RAS communication tool";
		String info2 = "Please read the documentation and enjoy using the software";
		WelcomeDialog about = new WelcomeDialog(title, info, info2);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
		
	}

	
	public void showVersion() {
		
		String version = "Giswater version " + prop.get("VERSION_CODE");
		VersionDialog about = new VersionDialog(version);
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
				"This product has been funded wholly or in part by GRUPO DE INVESTIGACION EN TRANSPORTE DE SEDIMENTOS (GITS) de la " + 
				"UNIVERSITAT POLITECNICA DE CATALUNYA (UPC) and TECNICSASSOCIATS, TALLER D'ARQUITECTURA I ENGINYERIA, SL. " + 
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

	
	public void showAgreements() {
		
		String title = "Agreements";
		String info = "Special thanks for his contribution to the project to:";
		String info2 = "Gemma García Ribot & Andrés Rodríguez Valero";
		WelcomeDialog about = new WelcomeDialog(title, info, info2);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
		
	}
	

}