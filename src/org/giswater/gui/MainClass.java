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
package org.giswater.gui;

import javax.swing.JFrame;

import org.giswater.controller.MenuController;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.util.Utils;
import org.giswater.util.UtilsFTP;


public class MainClass {

	public static MainFrame mdi;
	private final static String CURRENT_VERSION = "3.0.100";
	private static String gswFilePath = null;
	public static String function = null;   //  ["ed_giswater_jar" | "mg_go2epa_express"]
	

	public static void main(String[] args) {
		
		// Check if a parameter exists
		if (args.length == 2) {
			gswFilePath = args[0];
			function = args[1];
		}		

		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {		

				// Initial configuration
				String versionCode = MainClass.class.getPackage().getImplementationVersion();
				String msg = "Application started";
				if (versionCode == null) {
					versionCode = CURRENT_VERSION;
				}
				msg+= "\nVersion "+versionCode;
				Utils.getLogger().info(msg);				
				if (!MainDao.configIni(versionCode)) return;

				// Check if new version is available
				boolean newVersion = false;
				String ftpVersion = "";
				UtilsFTP ftp = null;
				String aux = PropertiesDao.getPropertiesFile().get("AUTO_CHECK_UPDATES", "false");
				Boolean autoCheck = Boolean.parseBoolean(aux);
				if (autoCheck) {
					Integer majorVersion = Integer.parseInt(versionCode.substring(0, 1));
					Integer minorVersion = Integer.parseInt(versionCode.substring(2, 3));
					Integer buildVersion = Integer.parseInt(versionCode.substring(4));
					ftp = new UtilsFTP();
					newVersion = ftp.checkVersion(majorVersion, minorVersion, buildVersion);
					ftpVersion = ftp.getFtpVersion();
				}

				// Create MainFrame and Menu controller
				mdi = new MainFrame(MainDao.isConnected(), versionCode, newVersion, ftpVersion);            	
				MenuController menuController = new MenuController(mdi, versionCode, ftp);            	

				// By default open last gsw
				if (function == null) {
					menuController.gswOpen(false);							
				}
				else {						
					Utils.getLogger().info(function);						
					if (function.equals("ed_giswater_jar")) {
						if (gswFilePath != null && !gswFilePath.equals("")) {				
							menuController.gswOpenFile(gswFilePath);
						}
						else {				
							menuController.gswOpen(false);					
						}
					}
					else if (function.equals("mg_go2epa_express")) {
						mdi.setExtendedState(JFrame.ICONIFIED);
						mdi.setExtendedState(mdi.getExtendedState() | JFrame.ICONIFIED);						
						menuController.executeFileManager(gswFilePath);					
					}
				}

			}
		});

	}


}