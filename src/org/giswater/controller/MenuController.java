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
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;
import org.giswater.dao.ExecuteDao;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.dialog.about.AcknowledgmentDialog;
import org.giswater.gui.dialog.about.LicenseDialog;
import org.giswater.gui.dialog.about.WelcomeDialog;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.DownloadPanel;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.task.BackupProjectTask;
import org.giswater.task.CreateExampleSchemaTask;
import org.giswater.task.FileLauncherTask;
import org.giswater.task.RestoreProjectTask;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;
import org.giswater.util.UtilsFTP;


public class MenuController extends AbstractController {

	private MainFrame mainFrame;
	private PropertiesMap prop;
	private String versionCode;
	private UtilsFTP ftp;
	private String action;
	private boolean openingApp;   // True when opening application. False otherwise
	
	private final String URL_MANUAL = "http://www.giswater.org/Documentation";	
	private final String URL_REFERENCE = "https://vimeo.com/giswater";
	private final String URL_WEB = "https://www.giswater.org/community";
	private final String UPDATE_FILE = "giswater_stand-alone_update_";

	
	public MenuController(MainFrame mainFrame, String versionCode, UtilsFTP ftp) {
		this.mainFrame = mainFrame;
		this.prop = PropertiesDao.getPropertiesFile();
		this.versionCode = versionCode;
		this.ftp = ftp;
		mainFrame.setControl(this);	
	}
	
	
	public boolean getOpeningApp() {
		return openingApp;
	}
	
	
	// Execute all tasks of 'File manager' panel in background
	public boolean executeFileManager(String gswFilePath) {
		
		if (!gswFilePath.equals("")) {
			gswOpenFile(gswFilePath);
		}
		else {
			gswOpen(false);	
		}
		
		// Select all checkboxes and execute all processes
		EpaSoftController epaSoftController = this.mainFrame.epaSoftController;
		epaSoftController.checkFileManager(true);
		epaSoftController.execute();

		return true;
		
	}
	
	

	// Menu File
	public void openProject() { 
		
		// Select .sql to restore
		String sqlPath = chooseSqlFile(false);
		if (sqlPath.equals("")) {
			return;
		}
		
		// Restore contents of .sql file into current Database
		boolean ok = ExecuteDao.executeRestore(sqlPath);		
		
		if (ok) {
			// Execute task RestoreProject
			File batFile = ExecuteDao.getBatFile();
			RestoreProjectTask task = new RestoreProjectTask(batFile);
	        task.setParentPanel(mainFrame);
	        task.addPropertyChangeListener(this);
	        task.execute();			
		}		
		
	}
	
	
	public void saveProject() { 
		
		String schema = MainDao.getSchema();
		if (schema == null) {
			String msg = Utils.getBundleString("MenuController.project_not_selected_backup"); //$NON-NLS-1$
			MainClass.mdi.showMessage(msg);
			return;
		}
		String sqlPath = chooseSqlFile(true);
		if (sqlPath.equals("")) {
			return;
		}
		
		// Backup current schema into selected file
		boolean ok = ExecuteDao.executeDump(schema, sqlPath);
		
		if (ok) {
			// Execute task BackupProject
			File batFile = ExecuteDao.getBatFile();
			BackupProjectTask task = new BackupProjectTask(batFile);
	        task.setParentPanel(mainFrame);
	        task.addPropertyChangeListener(this);
	        task.execute();			
		}			
		
	}

	
    private String chooseSqlFile(boolean save) {

    	String path = "";
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("SQL extension file", "sql");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("MenuController.file_sql"));
        File file = new File(PropertiesDao.getLastSqlPath());
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal;
        if (save) { 	
        	returnVal = chooser.showSaveDialog(mainFrame);
        }
        else {
        	returnVal = chooser.showOpenDialog(mainFrame);
        }
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileSql = chooser.getSelectedFile();
            path = fileSql.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".sql";
                fileSql = new File(path);
            }        
            PropertiesDao.setLastSqlPath(path);
        }
        return path;

    }

    
    public void exit() {
    	exit(true);
    }
    
    public void exit(Boolean askQuestion) {
    	
    	if (askQuestion) {
        	String msg = Utils.getBundleString("MenuController.save_project_preferences"); //$NON-NLS-1$
	    	int answer = Utils.showYesNoCancelDialog(mainFrame, msg);
	    	if (answer == JOptionPane.CANCEL_OPTION) return;
	    	if (answer == JOptionPane.YES_OPTION) {
	        	Utils.getLogger().info("Project preferences saved");	    		
	    		gswSave();
	    	}
    	}
    	else {
    		gswSave();
    	}
    	Utils.getLogger().info("Application closed");
    	System.exit(0);
    	
    }
    
    
    private void openFrame(JInternalFrame frame) {

        try {
        	frame.setVisible(true);
       		frame.setMaximum(true);
		} catch (PropertyVetoException e) {
			Utils.logError(e);
		}
    	
    }
	
    
    // Project preferences
    public void gswNew() {
    	
    	action = "new";
    	
    	// Get template path name
    	String gswTemplatePath = PropertiesDao.getGswTemplatePath();

    	// Get path of gsw file to create
		File gswFile = gswChooseFile(true);
		if (gswFile == null) return;		
		String gswNewPath = gswFile.getAbsolutePath();

    	// Copy contents from template gsw file to new gsw file and open it
    	boolean ok = Utils.copyFile(gswTemplatePath, gswNewPath);
    	if (ok) {
    		PropertiesDao.setGswPath(gswNewPath);
	    	gswOpen(false, false);
    	}
    	else {
    		Utils.logError("Error creating new gsw file");
    	}
    	
    }
    
    
    public void gswEdit() {
    	action = "edit";
		openFrame(mainFrame.ppFrame);
    }
    
    
	public void gswOpen() {
		gswOpen(true, true);
	}
	
	public void gswOpen(boolean chooseFile) {
    	action = "open";
    	openingApp = !chooseFile;    	
		gswOpen(chooseFile, true);
		openingApp = false;    	
	}
	
	
	public void gswOpen(boolean chooseFile, boolean acceptPreferences) {
		
		File gswFile = null;
		String gswPath = "";
		String gswName = "";
		if (chooseFile) {
			gswFile = gswChooseFile();
			if (gswFile == null) return;		
			gswPath = gswFile.getAbsolutePath();
			gswName = gswFile.getName();
			PropertiesDao.setGswPath(gswPath);
			prop.put("FILE_GSW", gswPath);		
			MainDao.closeConnectionPostgis();
		}
		else {
			gswPath = PropertiesDao.getGswPath();
			gswFile = new File(gswPath);
			gswName = gswFile.getName();
		}
		if (gswPath == "") return;
		
		// Load .gsw file into memory
		PropertiesDao.loadGswPropertiesFile();
		if (action.equals("new")) {
			PropertiesDao.loadGswPropertiesFile();
			PropertiesDao.getGswProperties().put("SOFTWARE", "");
			PropertiesDao.getGswProperties().put("VERSION", "");
			PropertiesDao.getGswProperties().put("STORAGE", "");
			PropertiesDao.getGswProperties().put("SCHEMA", "");
		}
		
		// Update application title
		mainFrame.updateTitle(gswName);
		
		// Update frames position and panels
		mainFrame.updateFrames();
		updateHecrasPanel();
		updateEpaSoftPanel();  		
		if (updateProjectPreferencesPanel()) {
			if (acceptPreferences) {
				ProjectPreferencesController ppController = mainFrame.ppFrame.getPanel().getController();
				ppController.openEpaSoft();
			}
		}
    	
    	if (action.equals("new")) {
    		openFrame(mainFrame.ppFrame);
    	}
		
	}
	
	
	public void gswOpenFile(String gswPath) {
		
		action = "open";
		if (gswPath == "") return;
		File gswFile = new File(gswPath);
		if (!gswFile.exists()) {
			mainFrame.showError("File not found:", gswPath, 10000);
			return;
		}
		String fileExtension = FilenameUtils.getExtension(gswPath);	
		if (!fileExtension.equals("gsw")) {
			mainFrame.showError("File extension not valid:", gswPath, 10000);
			return;			
		}
		String gswName = gswFile.getName();
		
		// Set PropertiesDao settings
		PropertiesDao.setGswPath(gswPath);
		prop.put("FILE_GSW", gswPath);		
		
		// Load .gsw file into memory
		PropertiesDao.loadGswPropertiesFile();
		
		// Update application title
		mainFrame.updateTitle(gswName);
		
		// Update frames position and panels
		mainFrame.updateFrames();
		updateHecrasPanel();
		updateEpaSoftPanel();  		
		if (updateProjectPreferencesPanel()) {
			ProjectPreferencesController ppController = mainFrame.ppFrame.getPanel().getController();
			ppController.openPreferences();
		}
		
	}
	
	
	public void gswSave() {
		mainFrame.saveGswFile();
	}
	
	public void gswSaveAs() {
		
		File gswFile = gswChooseFile(true);
		if (gswFile == null) return;		
		String gswPath = gswFile.getAbsolutePath();
		String gswName = gswFile.getName();
		PropertiesDao.setGswPath(gswPath);
    	mainFrame.saveGswFile();
    	mainFrame.updateTitle(gswName);  
    	
	}
	
	
	private File gswChooseFile() {
		return gswChooseFile(false);
	}
	
	private File gswChooseFile(boolean save) {
		
		String path = "";
		File file = null;
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("GSW extension file", "gsw");
        chooser.setFileFilter(filter);          
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("MenuController.gsw_file"));
        File fileProp = new File(prop.get("FILE_GSW", Utils.getLogFolder()));	
        chooser.setCurrentDirectory(fileProp.getParentFile());
        int returnVal;
        if (save) { 	
        	returnVal = chooser.showSaveDialog(mainFrame);
        }
        else {
        	returnVal = chooser.showOpenDialog(mainFrame);
        }
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            path = file.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".gsw";
                file = new File(path);
            }
        }
        
        return file;
        
	}

	
	private boolean updateDatabaseParams(ProjectPreferencesPanel ppPanel) {
		
		// Panel Database
		ppPanel.setHost(PropertiesDao.getGswProperties().get("POSTGIS_HOST"));
		ppPanel.setPort(PropertiesDao.getGswProperties().get("POSTGIS_PORT"));
		ppPanel.setDatabase(PropertiesDao.getGswProperties().get("POSTGIS_DATABASE"));
		ppPanel.setUser(PropertiesDao.getGswProperties().get("POSTGIS_USER"));		
		Boolean useSsl = Boolean.parseBoolean(PropertiesDao.getGswProperties().get("POSTGIS_USESSL"));
		ppPanel.selectUseSsl(useSsl);
		Boolean remember = Boolean.parseBoolean(PropertiesDao.getGswProperties().get("POSTGIS_REMEMBER"));
		ppPanel.selectRemember(remember);
		if (remember) {
			ppPanel.setPassword(Encryption.decrypt(PropertiesDao.getGswProperties().get("POSTGIS_PASSWORD")));        	
		} else {
			ppPanel.setPassword("");
		}
		
        // Initialize Database?   
        if (!MainDao.initializeDatabase()) {
			ppPanel.setConnectionText(Utils.getBundleString("open_connection"));
			ppPanel.enableConnectionParameters(true);			
			mainFrame.enableMenuDatabase(false);
        	return false;
        }
        
        // Autoconnect?
        Boolean autoConnect = Boolean.parseBoolean(prop.get("AUTOCONNECT_POSTGIS"));
       	if (autoConnect && remember) {
       		MainDao.silenceConnection();
       		ppPanel.setDatabase(MainDao.getDb());
       	}
        
        // Update text open/close button
		if (MainDao.isConnected()) {
			ppPanel.setConnectionText(Utils.getBundleString("close_connection"));				
			mainFrame.enableMenuDatabase(true);
		}
		else {
			ppPanel.setConnectionText(Utils.getBundleString("open_connection"));	
			mainFrame.enableMenuDatabase(false);
		}
		ppPanel.setSelectedSchema(PropertiesDao.getGswProperties().get("SCHEMA"));
		
		// Update Status Bar
		mainFrame.updateConnectionInfo();
		
		return true;
		
	}
	
	
    private boolean updateProjectPreferencesPanel() {
    	
    	ProjectPreferencesPanel ppPanel = mainFrame.ppFrame.getPanel();
    	
    	// Panel Water Software
    	String waterSoftware = PropertiesDao.getGswProperties().get("SOFTWARE").toUpperCase();
    	ppPanel.setWaterSoftware(waterSoftware);
    	MainDao.setWaterSoftware(waterSoftware);
		
		// Panel Database
		if (updateDatabaseParams(ppPanel)) {
			ppPanel.selectSourceType(true); 
			ppPanel.setVersionSoftware(PropertiesDao.getGswProperties().get("VERSION"));
			return true;
		}
		// If error open configuration file
		else {
			ppPanel.selectSourceType(true);
			openFrame(mainFrame.configFrame);
			return false;
		}
		
	}	    
    

    private void updateEpaSoftPanel() {
    	
    	EpaSoftPanel epaSoftPanel = mainFrame.epaSoftFrame.getPanel();
    	epaSoftPanel.setFileInp(PropertiesDao.getGswProperties().get("FILE_INP"));
    	epaSoftPanel.setFileRpt(PropertiesDao.getGswProperties().get("FILE_RPT"));
    	epaSoftPanel.setProjectName(PropertiesDao.getGswProperties().get("PROJECT_NAME"));
		
	}   
    
 
    private void updateHecrasPanel() {
    	
    	HecRasPanel hecRasPanel = mainFrame.hecRasFrame.getPanel();
    	hecRasPanel.setFileAsc(PropertiesDao.getGswProperties().get("HECRAS_FILE_ASC"));
    	hecRasPanel.setFileSdf(PropertiesDao.getGswProperties().get("HECRAS_FILE_SDF"));
    	
	}    
     
    
	// Menu Project example
	public void exampleEpanet() {
		MainDao.setWaterSoftware("EPANET");
		createExampleSchema("epanet");
	}

	public void exampleEpaswmm() {
		MainDao.setWaterSoftware("EPASWMM");
		createExampleSchema("epaswmm");
	}
	
	public void exampleEpaswmm2D() {
		MainDao.setWaterSoftware("EPASWMM");
		createExampleSchema("epaswmm", "_2d");
	}

	public void exampleHecras() {
		MainDao.setWaterSoftware("HECRAS");
       	createExampleSchema("hecras");
	}
	
	
	private void createExampleSchema(String waterSoftware) {
		createExampleSchema(waterSoftware, "");
	}
	
	private void createExampleSchema(String waterSoftware, String suffix) {
        
		// Get SRID
		String sridValue = "25831";		
		
		// Ask confirmation
		String schemaName = "sample_"+waterSoftware+suffix;
		String msg = Utils.getBundleString("MenuController.project_name")+schemaName;
		msg+= Utils.getBundleString("MenuController.created_srid")+sridValue;
		msg+= Utils.getBundleString("MenuController.wish_continue")+Utils.getBundleString("MenuController.disclaimer"); 
		int res = Utils.showYesNoDialog(mainFrame, msg, Utils.getBundleString("MenuController.create_example_project"));
		if (res != JOptionPane.YES_OPTION) return; 
		
		// Execute task: CreateSchema
		CreateExampleSchemaTask task = new CreateExampleSchemaTask(waterSoftware, schemaName, sridValue);
		task.setMenuController(this);
        task.setMainFrame(mainFrame);
        task.addPropertyChangeListener(this);
        task.execute();
				
	}

	
	
	// Menu Data 
	public void openDatabaseAdmin() {
		
		String path = prop.get("FILE_DBADMIN");
		File file = new File(path);
		if (!file.exists()) {
			// Maybe path is relative, so make it absolute and check it again
			path = MainDao.getGiswaterUsersFolder() + path;
			file = new File(path);
			if (!file.exists()) {
				Utils.showMessage(mainFrame, Utils.getBundleString("MenuController.file_not_found") + file.getAbsolutePath()); //$NON-NLS-1$
				return;
			}
		}
		Utils.openFile(path);
		
	}
	
	
	public void executeSqlFile() {
		
		// Get selected schema
		String schema = MainDao.getSchema();
		if (schema == null || schema.equals("")) {
			String msg = Utils.getBundleString("MenuController.project_not_selected_backup2"); //$NON-NLS-1$
			MainClass.mdi.showMessage(msg);
			return;
		}
		
		// Get SQL file to execute
		String filePath = chooseSqlFile(false);
		if (filePath.equals("")) return;
		
		// Get schema SRID
		String srid = MainDao.getSrid(schema);
		
		// Ask for confirmation
		int answer = Utils.showYesNoDialog(mainFrame, "MenuController.file_launcher_warning");
		if (answer == JOptionPane.NO_OPTION) return;
		
		try {
			
			// Copy file to users folder
			String tempPath = Utils.getLogFolder()+"temp.sql";
			Utils.copyFile(filePath, tempPath);
			
			// Search if we have _PARAM in the file
			// If found, ask user to input param value. Replace _PARAM_ with this value
			String msgAbort = Utils.getBundleString("MenuController.abort_process");				
	    	String content = Utils.readFile(tempPath);
	    	if (content.contains("_PARAM")) {
	    		// Get parameters list
	    		HashMap<String, String> mapParams = new HashMap<String, String>();
	    		int i = 0;
	    		int offset = 0;
	    		while (offset > -1) {
	    			offset = content.indexOf("_PARAM", i);
	    			if (offset > -1) {
	    				int aux = content.indexOf("_", offset + 1);
	    				String paramName = content.substring(offset, aux + 1);
	    				// If is a new one, ask user for its value
	    				if (!mapParams.containsKey(paramName)) {
	    					String msg = Utils.getBundleString("MenuController.parameter_found")+" '"+paramName+"' "+Utils.getBundleString("MenuController.parameter_found2");
	    					String value = null;
	    					while (value == null || value.equals("")) {
	    						value = Utils.showInputDialog(mainFrame, msg);
		    					if (value == null) {
		    						answer = Utils.showYesNoDialog(mainFrame, msgAbort);	  
		    						if (answer == JOptionPane.YES_OPTION) return;	    						
		    					}
	    					}
	    					content = content.replace(paramName, value);
	    					mapParams.put(paramName, value);
	    				}
	    			}
	    		}
	    	}
	    	
			// Execute task: FileLauncher
			FileLauncherTask task = new FileLauncherTask(content, schema, srid);
	        task.addPropertyChangeListener(this);
	        task.execute();
	        
        } catch (IOException e) {
            Utils.showError(e, filePath);
        }
		
	}
	
	
	public void showDevToolbox() {
		mainFrame.openDevToolbox();
	}	
	
	
	
	// Menu Configuration 
	public void showSoftware() {
		mainFrame.openSoftware();
	}
	
	
	
	// Menu About 
	public void showWelcome() {
		
		String title = Utils.getBundleString("MenuController.welcome"); //$NON-NLS-1$
		String info = Utils.getBundleString("MenuController.welcome_giswater"); //$NON-NLS-1$
		String info2 = Utils.getBundleString("MenuController.read_documentation"); //$NON-NLS-1$
		WelcomeDialog about = new WelcomeDialog(title, info, info2, versionCode);
		about.setModal(true);
		about.setLocationRelativeTo(mainFrame);
		about.setVisible(true);
		
	}

	
	public void showLicense() {
		
		String title = Utils.getBundleString("MenuController.license");
		String info = "This product as a whole is distributed under the GNU General Public License version 3";
		String info1 = "<html><p align=\"justify\">\"This product is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; " + 
				"without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. " +
				"See the GNU General Public License for more details\u201D</p></html>";
		String info2Begin = "<html><p align=\"justify\"><font size='2'>";
		String info2Body = 
				"THIS VERSION IS PROVIDED BY GISWATER ASSOCIATION.<br> " + 
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
		about.setLocationRelativeTo(mainFrame);
		about.setVisible(true);
		
	}

	
	public void showAcknowledgment() {
		
		String title = Utils.getBundleString("MenuController.acknowledgments"); //$NON-NLS-1$
		String info = Utils.getBundleString("MenuController.giswater_team"); //$NON-NLS-1$
		String info2 = Utils.getBundleString("MenuController.team_names") + //$NON-NLS-1$
			Utils.getBundleString("MenuController.team_names1") + //$NON-NLS-1$
			Utils.getBundleString("MenuController.team_names2"); //$NON-NLS-1$
		AcknowledgmentDialog about = new AcknowledgmentDialog(title, info, info2);
		about.setModal(true);
		about.setLocationRelativeTo(mainFrame);
		about.setVisible(true);
		
	}
	
	
	public void openUserManual() {
		Utils.openWeb(URL_MANUAL);
	}
	
	public void openReferenceGuide() {
		Utils.openWeb(URL_REFERENCE);
	}
	
	public void openWeb() {
		Utils.openWeb(URL_WEB);
	}	
	
	public void checkUpdates() {
		
		// Check if new version is available
		Integer majorVersion = Integer.parseInt(versionCode.substring(0, 1));
		Integer minorVersion = Integer.parseInt(versionCode.substring(2, 3));
		Integer buildVersion = Integer.parseInt(versionCode.substring(4));
		if (ftp == null) {
			ftp = new UtilsFTP();
		}
		boolean newVersion = ftp.checkVersion(majorVersion, minorVersion, buildVersion);
		String ftpVersion = ftp.getFtpVersion();
		mainFrame.setNewVersionVisible(newVersion, ftpVersion);
		if (!newVersion) {
			Utils.showMessage(mainFrame, Utils.getBundleString("MenuController.update")); //$NON-NLS-1$
		}
		
	}
	
	
	
	// Download new version
	public void downloadNewVersion() {
		
		Utils.getLogger().info("Downloading last version...");
		
		if (ftp == null) return;
		
		String ftpVersion = ftp.getFtpVersion();
		String remoteName = UPDATE_FILE+ftpVersion+".exe";
		// Choose file to download
		mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		String localePath = chooseFileSetup(remoteName);
		if (!localePath.equals("")) {
			DownloadPanel panel = new DownloadPanel(remoteName, localePath, ftp);
	        JDialog downloadDialog = Utils.openDialogForm(panel, mainFrame, Utils.getBundleString("MenuController.download_process"), 290, 135); //$NON-NLS-1$
	        downloadDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); 
	        downloadDialog.setVisible(true);
			mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	
    private String chooseFileSetup(String fileName) {

    	String path = "";
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("EXE extension file", "exe");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_exe"));
        File file = new File(usersFolder+fileName);	
        chooser.setCurrentDirectory(file);
        chooser.setSelectedFile(file);
        int returnVal = chooser.showOpenDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileSql = chooser.getSelectedFile();
            path = fileSql.getAbsolutePath();
            if (path.lastIndexOf(".") == -1) {
                path += ".exe";
                fileSql = new File(path);
            }        
        }
        return path;

    }
        

}