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
package org.giswater.dao;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.giswater.gui.MainClass;
import org.giswater.util.Utils;


public class ExecuteDao extends MainDao {
	
	private static final String PORTABLE_FOLDER = "portable" + File.separator;
	private static final String PORTABLE_FILE = "bin" + File.separator + "pg_ctl.exe";
	

	public static boolean executeDump(String schema, String sqlPath) {

		// Set bin folder
		if (!MainDao.setBinFolder()) {
			Utils.showError(Utils.getBundleString("ExecuteDao.bin_not_found")+binFolder+Utils.getBundleString("ExecuteDao.admin_location")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		
		// Check pgPass file and insert param if necessary
		if (!getConnectionParameters()) return false;
		String param = host+":"+port+":"+db+":"+user+":"+password;
		checkPgPass(param);
		
		// Set content of .bat file
		String aux= "\""+binFolder+"pg_dump.exe\" -U "+user+" -h "+host+" -p "+port+" -w -n "+schema+" -F plain --inserts -v -f \""+sqlPath+"\" "+db;
		aux+= "\nexit";			
		Utils.getLogger().info(aux);

        // Fill and execute .bat File
		String batPath = sqlPath.replace(".sql", ".bat");
		File batFile = new File(batPath);        
		if (!Utils.fillFile(batFile, aux)) {
			return false;    		
		}
		if (!Utils.openFile(batFile.getAbsolutePath())) {
			return false;
		}
		
		MainClass.mdi.showMessage(Utils.getBundleString("ExecuteDao.project_saved")+sqlPath); //$NON-NLS-1$
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		batFile.delete();
		
		return true;
			
	}	
	
	
	public static boolean executeRestore(String sqlPath) {
		
		// Set bin folder
		if (!MainDao.setBinFolder()) {
			Utils.showError(Utils.getBundleString("ExecuteDao.bin_not_found2")+binFolder+Utils.getBundleString("ExecuteDao.admin_location2")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		
		// Read file in order to get schema_name
		// Check if that schema already exists in Database
		String schemaName = getSchemaName(sqlPath);
		boolean exists = checkSchema(schemaName);
		if (exists) {
			// Get backup schema name
			boolean existsBackup = false;
			String backupName = schemaName+"_backup";
			int i = 0;
			do {
				if (i > 0) {
					backupName = schemaName+"_backup"+i;
				}
				existsBackup = checkSchema(backupName);
				i++;
			} while (existsBackup);
			String msg = Utils.getBundleString("ExecuteDao.the_project")+schemaName+Utils.getBundleString("ExecuteDao.already_exists") + //$NON-NLS-1$ //$NON-NLS-2$
				Utils.getBundleString("ExecuteDao.rename_automatically")+backupName+Utils.getBundleString("ExecuteDao.before_restoring") + //$NON-NLS-1$ //$NON-NLS-2$
				Utils.getBundleString("ExecuteDao.would_continue"); //$NON-NLS-1$
			int answer = Utils.showYesNoDialog(msg);
			if (answer != JOptionPane.YES_OPTION) return false;
			// Rename current schema
			String sql = "ALTER SCHEMA "+schemaName+" RENAME TO "+backupName;
			if (!MainDao.executeUpdateSql(sql, true)) {
				return false;
			} 
		}
		
		// Check pgPass file and insert param if necessary
		if (!getConnectionParameters()) return false;
		String param = host+":"+port+":"+db+":"+user+":"+password;
		checkPgPass(param);
		
		// Set content of .bat file
		String aux = "chcp 1252>NUL\n";
		aux+= "\""+binFolder+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+" -f \""+sqlPath+"\" > \""+Utils.getLogFolder()+"restore.log\"";
		aux+= "\nexit";			
		Utils.getLogger().info(aux);

        // Fill and execute .bat File
		String batPath = sqlPath.replace(".sql", ".bat");
		File batFile = new File(batPath);        
		if (!Utils.fillFile(batFile, aux)){
			return false;    		
		}
		if (!Utils.openFile(batFile.getAbsolutePath())){
			return false;
		}
//		Utils.showMessage("Restoring project data...");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		batFile.delete();
		
		return true;
			
	}	
	
	
	public static void executePostgisService(String service) {
		
		String folder = giswaterUsersFolder + PORTABLE_FOLDER;
		String path = folder + PORTABLE_FILE;		
		File file = new File(path);
		if (!file.exists()) {
			Utils.logError("Postgis service not found: "+path);
			return;
		}
		String data = folder + "data";
		
		// Set content of .vbs file
		String aux = "Set wshShell = CreateObject(\"WScript.Shell\")";
		aux+= "\nwshShell.Run \""+path+" start -D "+data+"\", 0, False";
		Utils.getLogger().info(aux);
		aux+= "\nSet wshShell = Nothing";

        // Fill and execute .vbs File	
		File vbsFile = new File(Utils.getLogFolder() + "hide.vbs");        
		Utils.fillFile(vbsFile, aux);    		
		Utils.openFile(vbsFile.getAbsolutePath());
		
	}
	
	
	public static boolean executeScript(String scriptPath, String batPath) {

		// Set bin folder
		if (!MainDao.setBinFolder()) {
			Utils.showError(Utils.getBundleString("ExecuteDao.bin_not_found3")+binFolder+Utils.getBundleString("ExecuteDao.admin_location3")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		
		// Set content of .bat file
		if (!getConnectionParameters()) return false;
		String aux= "\""+binFolder+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+ " -f "+scriptPath;
		aux+= "\nexit";		
		Utils.getLogger().info(aux);

        // Fill and execute .bat File	
		File batFile = new File(batPath);        
		Utils.fillFile(batFile, aux);    		
		Utils.openFile(batFile.getAbsolutePath());
		
		return true;
			
	}	
	
	
	public static void checkPgPass(String param) {

		// Get AppData folder
		String pgPassFolder = System.getProperty("user.home") + "/AppData/Roaming/postgresql";
		File folder = new File(pgPassFolder);
		if (!folder.exists()) {
			Utils.logError("pgPass folder not exists", pgPassFolder);
			return;
		}
		File file = new File(pgPassFolder + "/pgpass.conf");
		if (!file.exists()) {
			Utils.logError("pgPass file not exists", file.getAbsolutePath());
			return;
		}
		
		// Read contents of file looking for param
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			long fileLength = raf.length();
			boolean found = false;
			while (raf.getFilePointer() < fileLength && !found) {
				String line = raf.readLine().trim().toLowerCase();	
				if (line.equals(param.toLowerCase())){
					found = true;
				}
			}
			if (!found) {
				raf.writeBytes("\n"+param);
			}
			raf.close();
		} catch (Exception e) {
			Utils.logError(e);
		} 

	}
	
	
	private static String getSchemaName(String filePath) {
		
		// Read file until we found pattern: CREATE SCHEMA <schema_name>;
		String schemaName = "";
		ArrayList<String> fileContent = Utils.fileToArray(filePath);
		for (String line : fileContent){
			if (line.length() > 15) {
				String pattern = line.substring(0, 13).toUpperCase();
				if (pattern.equals("CREATE SCHEMA")){
					schemaName = line.substring(14, line.length() - 1);
					return schemaName;
				}
			}
		}
		return schemaName;
		
	}

	
}