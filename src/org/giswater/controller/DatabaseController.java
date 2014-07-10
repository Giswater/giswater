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
import java.lang.reflect.Method;

import org.giswater.dao.MainDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.DatabasePanel;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class DatabaseController {

	private DatabasePanel view;
    private PropertiesMap gswProp;
	public MainFrame mainFrame;
	
	
	public DatabaseController(DatabasePanel dbPanel, MainFrame mf) {
		this.mainFrame = mf;
		this.view = dbPanel;	
        this.gswProp = MainDao.getGswProperties();
	    view.setControl(this);        
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
			view.enableControls(true);			
		}
		else{
			if (openConnection()){
				mainFrame.enableCatalog(true);
				view.enableControls(false);
			}
		}
		mainFrame.updateEpaFrames();
		
	}	
	
	
	private void closeConnection(){
		
		view.setConnectionText(Utils.getBundleString("open_connection"));
		mainFrame.hecRasFrame.getPanel().enableButtons(false);
		MainDao.closeConnectionPostgis();
		Utils.showMessage(view, "connection_closed");			
		
	}
	
	
	private boolean openConnection(){
		
		String host, port, db, user, password;
		
		// Get parameteres connection from view
		host = view.getHost();		
		port = view.getPort();
		db = view.getDatabase();
		user = view.getUser();
		password = view.getPassword();	
		
		// Try to connect to Database
		boolean isConnected = MainDao.setConnectionPostgis(host, port, db, user, password);
		MainDao.setConnected(isConnected);
		
		if (isConnected){
			gswProp.put("POSTGIS_HOST", host);
			gswProp.put("POSTGIS_PORT", port);
			gswProp.put("POSTGIS_DATABASE", db);
			gswProp.put("POSTGIS_USER", user);
			// Save encrypted password
			if (view.getRemember()){
				gswProp.put("POSTGIS_PASSWORD", Encryption.encrypt(password));
			} else{
				gswProp.put("POSTGIS_PASSWORD", "");
			}
			
			// Get Postgis data and bin Folder
	    	String dataPath = MainDao.getDataDirectory();
	        gswProp.put("POSTGIS_DATA", dataPath);
	        File dataFolder = new File(dataPath);
	        String binPath = dataFolder.getParent() + File.separator + "bin";
	        gswProp.put("POSTGIS_BIN", binPath);
	        Utils.getLogger().info("Connection successful");
	        Utils.getLogger().info("Postgre data directory: " + dataPath);	
	    	Utils.getLogger().info("Postgre version: " + MainDao.checkPostgreVersion());
        	String postgisVersion = MainDao.checkPostgisVersion();	        
        	if (postgisVersion.equals("")){
        		// Enable Postgis to current Database
        		String sql = "CREATE EXTENSION postgis; CREATE EXTENSION postgis_topology;";
        		MainDao.executeUpdateSql(sql, true, false);			  	
        	}
        	else{
        		Utils.getLogger().info("Postgis version: " + postgisVersion);
        	}
	    	
			view.setConnectionText(Utils.getBundleString("close_connection"));
			Utils.showMessage(view, "connection_opened");
			
			// Hecras panel
			mainFrame.hecRasFrame.getPanel().setSchemaModel(MainDao.getSchemas("HECRAS"));
			mainFrame.hecRasFrame.getPanel().enableButtons(true);
			
			// TODO: Update pg_pass.conf
			// updatePgPass();
		} 
		else{
			mainFrame.hecRasFrame.getPanel().setSchemaModel(null);
		}
		
		return isConnected;
		
	}	
	
	
}