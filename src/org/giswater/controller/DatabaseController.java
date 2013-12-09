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

import org.giswater.dao.MainDao;
import org.giswater.gui.frame.MainFrame;
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
		
		if (MainDao.isConnected()){
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
				Utils.logError(e);
			} else{
				Utils.showError(e);
			}
		}
		
	}	
	
	
	public void testConnection(){
	
		if (MainDao.isConnected()){
			closeConnection();
			mainFrame.enableCatalog(false);
		}
		else{
			openConnection();
			mainFrame.enableCatalog(true);
		}
		
	}	
	
	
	private void closeConnection(){
		
		view.setConnectionText(Utils.getBundleString("open_connection"));
		mainFrame.hecRasFrame.getPanel().enableButtons(false);
		MainDao.closeConnectionPostgis();
		Utils.showMessage("connection_closed");			
		
	}
	
	
	private void openConnection(){
		
		String host, port, db, user, password;
		
		// Get parameteres connection from view
		host = view.getHost();		
		port = view.getPort();
		db = view.getDatabase();
		user = view.getUser();
		password = view.getPassword();		
		MainDao.setConnected(MainDao.setConnectionPostgis(host, port, db, user, password));
		
		if (MainDao.isConnected()){
			//view.setSchemaResult(MainDao.getSchemas());
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
	    	String folder = MainDao.getDataDirectory();
	    	Utils.getLogger().info("Postgis data directory: " + folder);
	        prop.put("POSTGIS_DATA", folder);
			view.setConnectionText(Utils.getBundleString("close_connection"));
			Utils.showMessage("connection_opened");	
			//MainDao.setSchema(view.getSchemaResult());
			mainFrame.hecRasFrame.getPanel().enableButtons(true);
		} 
		else{
			closeConnection();
		}
		
	}		
	
	
}