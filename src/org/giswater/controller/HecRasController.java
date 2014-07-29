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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;

import org.giswater.dao.MainDao;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.gui.panel.ProjectPanel;
import org.giswater.model.TableModelSrid;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class HecRasController extends AbstractController{

	private HecRasPanel view;
    private PropertiesMap prop;
    private PropertiesMap gswProp;
    private File fileSdf;
    private File fileAsc;    
    private String userHomeFolder;
	private String software;
	
	private TableModelSrid model;
	private TableColumnModel tcm;
	private ProjectPanel projectPanel;
	private JDialog projectDialog;

    
    public HecRasController(HecRasPanel view) {
    	
    	this.view = view;	
        this.prop = MainDao.getPropertiesFile();
        this.gswProp = MainDao.getGswProperties();
    	this.userHomeFolder = System.getProperty("user.home");
    	this.software = "HECRAS";
	    view.setControl(this);         	
    	setDefaultValues();
    	    	
	}

    
    private void setDefaultValues(){
    	
    	fileSdf = new File(gswProp.getProperty("HECRAS_FILE_SDF", userHomeFolder));
    	view.setFileSdf(fileSdf.getAbsolutePath());
    	fileAsc = new File(gswProp.getProperty("HECRAS_FILE_ASC", userHomeFolder));
		if (fileAsc.exists()) {
			view.setFileAsc(fileAsc.getAbsolutePath());
		}	
		view.setSchemaModel(MainDao.getSchemas("HECRAS"));
		
    }
	
	
	public void closePanel(){
		view.getFrame().setVisible(false);
	}
	
	
	public void isConnected(){

		// Check if we already are connected
		if (MainDao.isConnected()){
			view.enableButtons(true);
			view.setSchemaModel(MainDao.getSchemas("HECRAS"));
	    	view.setSelectedSchema(MainDao.getGswProperties().get("HECRAS_SCHEMA"));			
		} 
		else{
			view.enableButtons(false);
			view.setSchemaModel(null);				
		}
		//mainFrame.enableCatalog(false);
		
	}	
	
	
	public void schemaChanged(){
		
		if (MainDao.isConnected()){
			String schemaName = view.getSelectedSchema();
			MainDao.setSchema(schemaName);
		}
		
	}
	
	
    public void chooseFileSdf() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("SDF extension file", "sdf");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_sdf"));
        File file = new File(gswProp.getProperty("FILE_SDF", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileSdf = chooser.getSelectedFile();
            String path = fileSdf.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".sdf";
                fileSdf = new File(path);
            }
            view.setFileSdf(fileSdf.getAbsolutePath());            
        }

    }
    
    
    public void chooseFileAsc() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("ASC extension file", "asc");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_asc"));
        File file = new File(gswProp.getProperty("HECRAS_FILE_ASC", userHomeFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	fileAsc = chooser.getSelectedFile();
            String path = fileAsc.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".asc";
                fileAsc = new File(path);
            }
            view.setFileAsc(fileAsc.getAbsolutePath());            
        }

    }    
    

    private boolean getFileSdf(){
    	
        String path = view.getFileSdf();
        if (path.equals("")){
            return false;        	
        }
        if (path.lastIndexOf(".") == -1) {
            path += ".sdf";
        }
        fileSdf = new File(path);        
        gswProp.put("FILE_SDF", fileSdf.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }
    
    
    private boolean getFileAsc(){
    	
        String path = view.getFileAsc();
        if (path.equals("")){
            return false;        	
        }
        if (path.lastIndexOf(".") == -1) {
            path += ".asc";
        }
        fileAsc = new File(path);        
        gswProp.put("HECRAS_FILE_ASC", fileAsc.getAbsolutePath());
        MainDao.savePropertiesFile();
        return true;    
        
    }
    
    
    // Clear gisras schema info    
    public void clearData(){
    	    	
		String schemaName = view.getSelectedSchema();    	
		String msg = Utils.getBundleString("empty_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(msg);  
        if (res == 0){
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	if (MainDao.clearData(schemaName)){
        		Utils.showMessage(view, "data_cleared");
        	}
        }    	
    	
    }

    
	private String getUserSrid(String defaultSrid){
		
		String sridValue = "";
		Boolean sridQuestion = Boolean.parseBoolean(prop.get("SRID_QUESTION"));
		if (sridQuestion){
			sridValue = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_srid"), defaultSrid);
			if (sridValue == null){
				return "";
			}
		}
		else{
			sridValue = defaultSrid;
		}
		return sridValue.trim();
		
	}
	
	
	private String validateName(String schemaName){
		
		String validate;
		validate = schemaName.trim().toLowerCase();
		validate = validate.replace(" ", "_");
		validate = validate.replaceAll("[^\\p{ASCII}]", "");
		return validate;
		
	}
	
	
	public void createSchema(){
		createSchemaAssistant();
	}	
	
	
	private void createSchemaAssistant() {
		
		String defaultSrid = prop.get("SRID_DEFAULT", "25831");		
		ProjectPanel panel = new ProjectPanel(defaultSrid);
		panel.setHecRasController(this);
        initModel(panel);
        updateTableModel();
        projectDialog = Utils.openDialogForm(panel, view, "Create Project", 420, 480);
        projectDialog.setVisible(true);
		
	}
	
    
	public void createSchema(String defaultSchemaName){
		
		String schemaName = defaultSchemaName;
		if (defaultSchemaName.equals("")){
			schemaName = JOptionPane.showInputDialog(view, Utils.getBundleString("enter_schema_name"), "schema_name");
			if (schemaName == null){
				return;
			}
			schemaName = schemaName.trim().toLowerCase();
			if (schemaName.equals("")){
				Utils.showError("schema_valid_name");
				return;
			}
		}
		
		// Ask user to set SRID?
		String defaultSrid = prop.get("SRID_DEFAULT", "25831");		
		String sridValue = getUserSrid(defaultSrid);

		if (sridValue.equals("")){
			return;
		}
		Integer srid;
		try{
			srid = Integer.parseInt(sridValue);
		} catch (NumberFormatException e){
			Utils.showError("error_srid");
			return;
		}
		MainDao.getGswProperties().put("SRID_USER", sridValue);
		MainDao.savePropertiesFile();

		boolean isSridOk = MainDao.checkSrid(srid);
		if (!isSridOk && srid != 0){
			String msg = "SRID "+srid+" " +Utils.getBundleString("srid_not_found")+"\n" +
				Utils.getBundleString("srid_valid");			
			Utils.showError(msg);
			return;
		}
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));		
		boolean status = MainDao.createSchemaHecRas("hecras", schemaName, sridValue);
		if (status && defaultSchemaName.equals("")){
			Utils.showMessage(view, "schema_creation_completed");
		}
		else if (status && !defaultSchemaName.equals("")){
			Utils.showMessage(view, "schema_truncate_completed");
		}
		view.setSchemaModel(MainDao.getSchemas("HECRAS"));	
		schemaChanged();
		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
    
	
	public void deleteSchema(){
		
		String schemaName = view.getSelectedSchema();
		String msg = Utils.getBundleString("delete_schema_name") + "\n" + schemaName;
		int res = Utils.confirmDialog(msg);        
        if (res == 0){
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	MainDao.deleteSchema(schemaName);
        	view.setSchemaModel(MainDao.getSchemas("HECRAS"));
        	schemaName = view.getSelectedSchema();
        	MainDao.setSchema(schemaName);
    		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    		Utils.showMessage(view, "schema_deleted", "");
        }
        
	}	
    
	
	private void initModel(ProjectPanel panel){
		
		this.projectPanel = panel;
        model = new TableModelSrid();
        JTable table = projectPanel.getTable();
        model.setTable(table);   
        tcm = table.getColumnModel();     
        
		String sql = "SELECT substr(srtext, 1, 6) as \"Type\", srid as \"SRID\", substr(split_part(srtext, ',', 1), 9) as \"Description\"";		
		sql+= " FROM public.spatial_ref_sys";
		sql+= " ORDER BY substr(srtext, 1, 6), srid";
		Utils.getLogger().info(sql);
		ResultSet rs = MainDao.getResultset(sql);
		model.setRs(rs);
		projectPanel.setTableModel(model);    
		// Rendering just first time
		if (tcm.getColumnCount() > 0){
			tcm.getColumn(0).setMaxWidth(50);   
			tcm.getColumn(1).setMaxWidth(40);   
		}
        
	}
	
	
	public void updateTableModel() {
		updateTableModel("");
	}
	
	
	public void updateTableModel(String filterType) {
		
		String sql = "SELECT substr(srtext, 1, 6) as \"Type\", srid as \"SRID\", substr(split_part(srtext, ',', 1), 9) as \"Description\"";			
		sql+= " FROM public.spatial_ref_sys";
		String filter = projectPanel.getFilter();
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
		projectPanel.setTableModel(model);    			
		Utils.getLogger().info(sql);
		
	}
	
	
	public void acceptProject(){
		
		// SRID
		String sridValue = projectPanel.getSrid();
		if (sridValue.equals("-1")){
			Utils.showMessage(projectPanel, Utils.getBundleString("srid_select"));
			return;
		}
		
		// Project Name
		String schemaName = projectPanel.getName();
		if (schemaName.equals("")){
			Utils.showMessage(projectPanel, Utils.getBundleString("enter_schema_name"));
			return;
		}
		schemaName = validateName(schemaName);
		if (schemaName.equals("")){
			Utils.showError(view, "schema_valid_name");
			return;
		}
		
		// Project Title, Author and Date
		String title = projectPanel.getTitle();
		if (title.equals("")){
			Utils.showMessage(projectPanel, Utils.getBundleString("enter_schema_title"));
			return;
		}
		String author = projectPanel.getAuthor();
		String date = projectPanel.getDate();
		
		// Save properties
		MainDao.getGswProperties().put("SRID_USER", sridValue);
		MainDao.savePropertiesFile();
		
    	view.enableControlsText(false);
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	  
		
		boolean status = MainDao.createSchemaHecRas(software, schemaName, sridValue);	
		if (status){
			MainDao.setSchema(schemaName);
			String sql = "INSERT INTO "+schemaName+".inp_project_id VALUES ('"+title+"', '"+author+"', '"+date+"')";
			Utils.getLogger().info(sql);
			MainDao.executeSql(sql, true);
			sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date)" +
				" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+software+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";
			Utils.getLogger().info(sql);
			MainDao.executeSql(sql, true);
			Utils.showMessage(view, "schema_creation_completed");
		}
		
		// Update view
		view.setSchemaModel(MainDao.getSchemas(software));	
		schemaChanged();
		view.enableControlsText(true);
		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
		closeProject();
		
	}
	
	
	public void closeProject(){
		projectDialog.dispose();
	}	
	
    
    public void loadRaster(){
    	
		// Check ASC file is set
		if (!getFileAsc()) {
			Utils.showError("file_asc_not_selected");
			return;
		}
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	
		String schemaName = view.getSelectedSchema();
		String filePath = fileAsc.getAbsolutePath();
		String fileName = fileAsc.getName();
    	MainDao.loadRaster(schemaName, filePath, fileName);  	
        	
    }
    
    
    // Create HEC-RAS file    
	public void exportSdf() {
   	
		// Check SDF file is set
		if (!getFileSdf()) {
			Utils.showError("file_sdf_not_selected");
			return;
		}

		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        
		String schemaName = view.getSelectedSchema();
		String fileName = fileSdf.getName();
		MainDao.createSdfFile(schemaName, fileName);
		
		// Copy file from Postgis Data Directory to folder specified by the user
		String auxIn, auxOut;
		String folderIn = gswProp.getProperty("POSTGIS_DATA");
		auxIn = folderIn + File.separator + fileName;
		auxOut = fileSdf.getAbsolutePath();
		boolean ok = Utils.copyFile(auxIn, auxOut);
		if (!ok){
			Utils.showError("sdf_error");
		}
		else{
			Utils.showMessage(view, "sdf_ok", fileSdf.getAbsolutePath());
		}
		
	}
    
	
}