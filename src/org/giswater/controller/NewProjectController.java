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

import java.io.File;
import java.sql.ResultSet;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.panel.ProjectPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.model.table.TableModelSrid;
import org.giswater.task.CreateSchemaTask;
import org.giswater.util.Utils;


public class NewProjectController extends AbstractController {

	private ProjectPanel view;
	private String waterSoftware;
	private TableModelSrid model;
	private TableColumnModel tcm;
	private ProjectPreferencesPanel parentPanel;

    
    public NewProjectController(ProjectPanel view) {
    	this.view = view;	
    	this.waterSoftware = MainDao.getWaterSoftware();
	}
   
	
	public void initModel() {
		
        model = new TableModelSrid();
        JTable table = view.getTable();
        model.setTable(table);   
        tcm = table.getColumnModel();     
        
		String sql = "SELECT substr(srtext, 1, 6) as \"Type\", srid as \"SRID\", substr(split_part(srtext, ',', 1), 9) as \"Description\"";		
		sql+= " FROM public.spatial_ref_sys";
		sql+= " ORDER BY substr(srtext, 1, 6), srid";
		ResultSet rs = MainDao.getResultset(sql);
		model.setRs(rs);
		view.setTableModel(model);    
		// Rendering just first time
		if (tcm.getColumnCount() > 0) {
			tcm.getColumn(0).setMaxWidth(50);   
			tcm.getColumn(1).setMaxWidth(40);   
		}
        
	}
	
	
	public void updateTableModel() {
		
		String sql = "SELECT substr(srtext, 1, 6) as \"Type\", srid as \"SRID\", substr(split_part(srtext, ',', 1), 9) as \"Description\"";			
		sql+= " FROM public.spatial_ref_sys";
		String filter = view.getFilter();
		
		// SRID: Select only PROJCS
		if (!filter.equals("")) {
			sql+= " WHERE (cast(srid as varchar) like '%"+filter+"%' OR split_part(srtext, ',', 1) like '%"+filter+"%')";
		} 
		String filterType = "substr(srtext, 1, 6) = 'PROJCS'";
		if (filter.equals("")) {
			sql+= " WHERE ";
		}
		else{
			sql+= " AND ";
		}
		sql+= "("+filterType+")";
		
		sql+= " ORDER BY substr(srtext, 1, 6), srid";
		ResultSet rs = MainDao.getResultset(sql);
		model.setRs(rs);
		view.setTableModel(model);    			
		
	}
	
	
	private String validateName(String schemaName) {
		
		String validate = schemaName.trim().toLowerCase();
		validate = validate.replace(" ", "_");
		validate = validate.replace("-", "_");
		validate = validate.replaceAll("[^\\p{ASCII}]", "");
		return validate;
		
	}
	
	
	public void acceptProject() {
		
		// SRID
		String sridValue = view.getSrid();
		if (sridValue.equals("-1")) {
			Utils.showMessage(view, Utils.getBundleString("srid_select"));
			return;
		}
		
		// Get Project name and validate it
		String schemaName = view.getName();
		if (schemaName.equals("")) {
			Utils.showError(view, Utils.getBundleString("enter_schema_name"));
			return;
		}
		if (Utils.isInteger(schemaName.substring(0, 1))) {
			Utils.showError(view, Utils.getBundleString("NewProjectController.invalid_project")); //$NON-NLS-1$
			return;
		}
		schemaName = validateName(schemaName);
		if (schemaName.equals("")) {
			Utils.showError(view, Utils.getBundleString("NewProjectController.invalid_name"));
			return;
		}
		
		// Project Title, Author and Date
		String title = view.getTitle();
		if (title.equals("")) {
			Utils.showMessage(view, Utils.getBundleString("enter_schema_title"));
			return;
		}
		String author = view.getAuthor();
		String date = view.getDate();
		
		Boolean enableConstraints = view.isConstraintsEnabled();
		
		// Execute task: CreateSchema
		CreateSchemaTask task = new CreateSchemaTask(waterSoftware, schemaName, sridValue);
        task.setParams(title, author, date, enableConstraints);
        task.setController(this);
        task.setParentPanel(parentPanel);
        task.addPropertyChangeListener(this);
        task.execute();
		
	}
	
	
	public void closeProject() {
		view.getDialog().dispose();
	}


	public void setParentPanel(ProjectPreferencesPanel ppPanel) {
		parentPanel = ppPanel;
	}
	
	
    public void chooseFile() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("NewProjectController.select_file"));
        File file = new File(PropertiesDao.getGswProperties().get("FILE_INP", MainDao.getGiswaterUsersFolder()));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileImport = chooser.getSelectedFile();
            view.setFileImport(fileImport.getAbsolutePath());            
        }

    }


	public void enableImportData() {
		view.enableImportData(true);
	}
	
	
	// TODO: When Import Data check is enabled
	public void importData() {
		
	}

	
}