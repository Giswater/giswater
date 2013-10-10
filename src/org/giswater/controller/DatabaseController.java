/*
 * This file is part of gisWater
 * Copyright (C) 2012  Tecnics Associats
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

import java.awt.Cursor;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import org.giswater.dao.MainDao;
import org.giswater.gui.MainFrame;
import org.giswater.gui.panel.DatabasePanel;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class DatabaseController {

	private DatabasePanel view;
    private PropertiesMap prop;
	public MainFrame mainFrame;
	
	
	public DatabaseController(DatabasePanel dbPanel, MainFrame mf) {
		
		this.mainFrame = mf;
		this.view = dbPanel;	
        this.prop = MainDao.getPropertiesFile();
	    view.setControl(this);        

    	setDefaultValues();    	
    	
	}
	
	
    private void setDefaultValues(){
    	
		// Get parameters connection 
		view.setHost(prop.get("POSTGIS_HOST"));
		view.setPort(prop.get("POSTGIS_PORT"));
		view.setDatabase(prop.get("POSTGIS_DATABASE"));
		view.setUser(prop.get("POSTGIS_USER"));
		view.setPassword(Encryption.decrypt(prop.get("POSTGIS_PASSWORD")));		
		
		if (MainDao.isConnected){
			view.setConnectionText(Utils.getBundleString("close_connection"));
		}
		else{
			view.setConnectionText(Utils.getBundleString("open_connection"));
		}
	
    }
   
    
	
	public void action(String actionCommand) {
		
		Method method;
		try {
			if (Utils.getLogger() != null){
				Utils.getLogger().info(actionCommand);
			}
			method = this.getClass().getMethod(actionCommand);
			method.invoke(this);	
		} catch (Exception e) {
			if (Utils.getLogger() != null){			
				Utils.logError(e, actionCommand);
			} else{
				Utils.showError(e, actionCommand);
			}
		}
		
	}	
	
	
	public void createSchema(){
		
		Integer driver = view.getDriver();
		String schemaName = JOptionPane.showInputDialog(this.view, Utils.getBundleString("enter_schema_name"), "schema_name");
		if (schemaName == null){
			return;
		}
		schemaName = schemaName.trim().toLowerCase();
		if (schemaName.equals("")){
			Utils.showError(Utils.getBundleString("schema_valid_name"), "", "gisWater");
			return;
		}
		
		// Get default SRID from properties
		String defaultSrid = prop.get("SRID", "23030");
		String sridValue = JOptionPane.showInputDialog(this.view, Utils.getBundleString("enter_srid"), defaultSrid);
		if (sridValue == null){
			return;
		}		
		sridValue = sridValue.trim();
		if (!sridValue.equals("")){
			Integer srid;
			try{
				srid = Integer.parseInt(sridValue);
			} catch (NumberFormatException e){
				Utils.showError(Utils.getBundleString("error_srid"), "", "gisWater");
				return;
			}
			if (!sridValue.equals(defaultSrid)){
				prop.put("SRID", sridValue);
				MainDao.savePropertiesFile();
			}
			boolean isSridOk = MainDao.checkSrid(srid);
			if (!isSridOk){
				String msg = "SRID " + srid + " " + Utils.getBundleString("srid_not_found") + "\n" +
					Utils.getBundleString("srid_valid");			
				Utils.showError(msg, "", "gisWater");
				return;
			}
			view.setCursor(new Cursor(Cursor.WAIT_CURSOR));		
			MainDao.createSchema(schemaName.trim(), sridValue.trim(), driver);
			view.setSchemaResult(MainDao.getSchemas());		
			view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	
	public void deleteSchema(){
		
		String schemaName = view.getSchemaResult();
        int res = JOptionPane.showConfirmDialog(this.view, Utils.getBundleString("delete_schema_name") + "\n" + schemaName, 
        	"gisWater", JOptionPane.YES_NO_OPTION);
        if (res == 0){
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));	        	
        	MainDao.deleteSchema(schemaName);
        	view.setSchemaResult(MainDao.getSchemas());
    		view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    		Utils.showMessage(Utils.getBundleString("schema_deleted"), "", "gisWater");
        }
        
	}	
	
	
	public void testConnection(){
		
		if (MainDao.isConnected){
			MainDao.closeConnectionPostgis();
			view.setConnectionText(Utils.getBundleString("open_connection"));
			Utils.showMessage(Utils.getBundleString("connection_closed"), "", "gisWater");			
		}
		else{
			openConnection();
		}
		
	}	
	
	
	private void openConnection(){
		
		String host, port, db, user, password;
		
		// Get parameteres connection from view
		host = view.getHost();		
		port = view.getPort();
		db = view.getDatabase();
		user = view.getUser();
		password = view.getPassword();		
		MainDao.isConnected  = MainDao.setConnectionPostgis(host, port, db, user, password);
		
		if (MainDao.isConnected){
			view.setSchemaResult(MainDao.getSchemas());
			prop.put("POSTGIS_HOST", host);
			prop.put("POSTGIS_PORT", port);
			prop.put("POSTGIS_DATABASE", db);
			prop.put("POSTGIS_USER", user);
			// Save encrypted password
			if (view.getRemember()){
				prop.put("POSTGIS_PASSWORD", Encryption.encrypt(password));
			} else{
				prop.put("POSTGIS_PASSWORD", "");
			}
			// Save properties file
			MainDao.savePropertiesFile();
			view.setConnectionText(Utils.getBundleString("close_connection"));
			Utils.showMessage(Utils.getBundleString("connection_opened"), "", "gisWater");	
			view.enableButtons(true);
			MainDao.setSchema(view.getSchemaResult());
		} 
		else{
			view.enableButtons(false);
			view.setSchemaResult(null);			
		}
		
	}		
	
	
}