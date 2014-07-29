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

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;

import org.giswater.dao.MainDao;
import org.giswater.gui.panel.ProjectPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.model.TableModelSrid;
import org.giswater.util.Utils;


public class NewProjectController extends AbstractController{

	private ProjectPanel view;
	private String software;
	private TableModelSrid model;
	private TableColumnModel tcm;
	private ProjectPreferencesPanel parentPanel;

    
    public NewProjectController(ProjectPanel view) {
    	this.view = view;	
    	this.software = MainDao.getSoftwareName();
	}
   
	
	public void initModel(){
		
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
		if (tcm.getColumnCount() > 0){
			tcm.getColumn(0).setMaxWidth(50);   
			tcm.getColumn(1).setMaxWidth(40);   
		}
        
	}
	
		
	public void checkedType() {

		String filterType = "";
		Boolean isGeo = view.isGeoSelected();
		Boolean isProj = view.isProjSelected();
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
	
	
	public void updateTableModel() {
		updateTableModel("");
	}
	
	
	public void updateTableModel(String filterType) {
		
		String sql = "SELECT substr(srtext, 1, 6) as \"Type\", srid as \"SRID\", substr(split_part(srtext, ',', 1), 9) as \"Description\"";			
		sql+= " FROM public.spatial_ref_sys";
		String filter = view.getFilter();
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
		view.setTableModel(model);    			
		
	}
	
	
	private String validateName(String schemaName){
		
		String validate;
		validate = schemaName.trim().toLowerCase();
		validate = validate.replace(" ", "_");
		validate = validate.replaceAll("[^\\p{ASCII}]", "");
		return validate;
		
	}
	
	
	public void acceptProject(){
		
		// SRID
		String sridValue = view.getSrid();
		if (sridValue.equals("-1")){
			Utils.showMessage(view, Utils.getBundleString("srid_select"));
			return;
		}
		
		// Project Name
		String schemaName = view.getName();
		if (schemaName.equals("")){
			Utils.showMessage(view, Utils.getBundleString("enter_schema_name"));
			return;
		}
		schemaName = validateName(schemaName);
		if (schemaName.equals("")){
			Utils.showError(view, "schema_valid_name");
			return;
		}
		
		// Project Title, Author and Date
		String title = view.getTitle();
		if (title.equals("")){
			Utils.showMessage(view, Utils.getBundleString("enter_schema_title"));
			return;
		}
		String author = view.getAuthor();
		String date = view.getDate();
		
		// Save properties
		MainDao.getGswProperties().put("SRID_USER", sridValue);
		MainDao.savePropertiesFile();
		
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	  
		
		boolean status = MainDao.createSchema(software, schemaName, sridValue);	
		if (status){
			MainDao.setSchema(schemaName);
			if (MainDao.updateSchema()){
				String sql = "INSERT INTO "+schemaName+".inp_project_id VALUES ('"+title+"', '"+author+"', '"+date+"')";
				Utils.getLogger().info(sql);
				MainDao.executeSql(sql, false);
				sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date)" +
					" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+software+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";
				Utils.getLogger().info(sql);
				// Last SQL script. So commit all process
				MainDao.executeSql(sql, true);
				Utils.showMessage(view, "schema_creation_completed");
			}
			else{
				MainDao.rollbackSchema(schemaName);
				Utils.logError("Error updateSchema. Schema could not be created");
			}
		}
		else{
			MainDao.rollbackSchema(schemaName);
			Utils.logError("Error createSchema. Schema could not be created");
		}
		
		// Refresh view
		parentPanel.setSchemaModel(MainDao.getSchemas(software));	
		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		closeProject();
		
	}
	
	
	public void closeProject(){
		view.getDialog().dispose();
	}


	public void setParentPanel(ProjectPreferencesPanel ppPanel) {
		parentPanel = ppPanel;
	}
	
	
	// TODO:
	public void loadData(){
		
	}
	
	
    public void chooseFileLoadInp() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("INP extension file", "inp");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_load_inp"));
        File file = new File(MainDao.getGswProperties().get("FILE_INP_LOAD", MainDao.getRootFolder()));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileLoadInp = chooser.getSelectedFile();
            String path = fileLoadInp.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".inp";
                fileLoadInp = new File(path);
            }
            view.setFileLoadInp(fileLoadInp.getAbsolutePath());            
        }

    }
	
	
}