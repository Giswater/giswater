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
package org.giswater.gui.frame;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.giswater.controller.ConfigController;
import org.giswater.controller.EpaSoftController;
import org.giswater.controller.HecRasController;
import org.giswater.controller.MenuController;
import org.giswater.controller.ProjectPreferencesController;
import org.giswater.dao.MainDao;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.GisPanel;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MainFrame extends JFrame implements ActionListener{
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private static final long serialVersionUID = -6630818426483107558L;
	private MenuController menuController;
	private PropertiesMap prop;
	private String versionCode;
	
    private JDesktopPane desktopPane;
    
    private JMenu mnProject;
	private JMenuItem mntmOpenProject;
	private JMenuItem mntmSaveProject;
	private JSeparator separator;
	private JMenuItem mntmOpenPreferences;
	private JMenuItem mntmSavePreferences;
	private JMenuItem mntmSaveAsPreferences;
	
	private JMenu mnGisProject;	
	
	private JMenu mnConfiguration;
	private JMenuItem mntmSoftware;
	private JMenuItem mntmSampleEpanet;
	private JMenuItem mntmSampleEpaswmm;
	private JMenuItem mntmSampleHecras;
	private JMenuItem mntmDatabaseAdministrator;	
	
	private JMenu mnAbout;
	private JMenuItem mntmWelcome;		
	private JMenuItem mntmLicense;
	private JMenuItem mntmAgreements;
	private JMenuItem mntmUserManual;	
	private JMenuItem mntmReferenceGuide;	
	private JMenuItem mntmWeb;
	private JMenuItem mntmCheckUpdates;
	
	private JMenu mnNewVersionAvailable;
	private JMenuItem mntmDownload;

	public EpaSoftFrame epaSoftFrame;
	public HecRasFrame hecRasFrame;
	public ProjectPreferencesFrame ppFrame;
	public ConfigFrame configFrame;
	public GisFrame gisFrame;

	
	/**
	 * @wbp.parser.constructor
	 */
	public MainFrame(boolean isConnected, String versionCode) {
		this(isConnected, versionCode, false, "");
	}
	
	
	public MainFrame(boolean isConnected, String versionCode, boolean newVersion, String ftpVersion) {
		
		this.versionCode = versionCode;
		initConfig();
		setNewVersionVisible(newVersion, ftpVersion);
		try {
			initFrames();
			hecRasFrame.getPanel().enableButtons(isConnected);
		} catch (PropertyVetoException e) {
            Utils.logError(e.getMessage());
		}
		
	}

	
	public void setNewVersionVisible(boolean newVersion, String ftpVersion) {
		mnNewVersionAvailable.setVisible(newVersion);
		String msg = "Download version v"+ftpVersion;
		mntmDownload.setText(msg);
	}


	public void setControl(MenuController menuController) {
		this.menuController = menuController;
	}	
	
	
	private void initConfig(){

		ImageIcon image = new ImageIcon("images/imago.png");
		setIconImage(image.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		prop = MainDao.getPropertiesFile();
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnProject = new JMenu(BUNDLE.getString("MainFrame.mnProject.text")); //$NON-NLS-1$
		menuBar.add(mnProject);
		
		separator = new JSeparator();
		mnProject.add(separator);
		
		JMenuItem mntmNewPreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmNewProjectPreferences.text")); //$NON-NLS-1$
		mnProject.add(mntmNewPreferences);
		
		mntmOpenPreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmOpen.text")); //$NON-NLS-1$
		mntmOpenPreferences.setActionCommand("gswOpen");
		mnProject.add(mntmOpenPreferences);
		
		mntmSavePreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmSave.text")); //$NON-NLS-1$
		mntmSavePreferences.setActionCommand("gswSave");
		mnProject.add(mntmSavePreferences);
		
		mntmSaveAsPreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmSaveAs.text")); //$NON-NLS-1$
		mntmSaveAsPreferences.setActionCommand("gswSaveAs");
		mnProject.add(mntmSaveAsPreferences);
		
		JMenuItem mntmEditPreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmEditProjectPreferences.text")); //$NON-NLS-1$
		mnProject.add(mntmEditPreferences);
		
		JSeparator separator_1 = new JSeparator();
		mnProject.add(separator_1);
		
		mntmOpenProject = new JMenuItem(BUNDLE.getString("MainFrame.mntmOpenProject.text")); //$NON-NLS-1$
		mntmOpenProject.setActionCommand("openProject");
		mnProject.add(mntmOpenProject);
		
		mntmSaveProject = new JMenuItem(BUNDLE.getString("MainFrame.mntmSaveProject.text")); //$NON-NLS-1$
		mntmSaveProject.setActionCommand("saveProject");
		mnProject.add(mntmSaveProject);
		
		mnGisProject = new JMenu(BUNDLE.getString("MainFrame.mnGisProject.text"));
		menuBar.add(mnGisProject);
		
		mntmSampleEpanet = new JMenuItem(BUNDLE.getString("MainFrame.mntmNewMenuItem.text"));
		mnGisProject.add(mntmSampleEpanet);
		mntmSampleEpanet.setActionCommand("sampleEpanet");
		
		mntmSampleEpaswmm = new JMenuItem(BUNDLE.getString("MainFrame.mntmCreateEpaswmmSample.text"));
		mnGisProject.add(mntmSampleEpaswmm);
		mntmSampleEpaswmm.setActionCommand("sampleEpaswmm");
		
		mntmSampleHecras = new JMenuItem(BUNDLE.getString("MainFrame.mntmCreateHecrasSample.text"));
		mnGisProject.add(mntmSampleHecras);
		mntmSampleHecras.setActionCommand("sampleHecras");
		
		JMenu mnData = new JMenu(BUNDLE.getString("MainFrame.mnData.text")); //$NON-NLS-1$
		menuBar.add(mnData);
		
		mntmDatabaseAdministrator = new JMenuItem(BUNDLE.getString("MainFrame.mntmDatabaseAdministrator.text"));
		mnData.add(mntmDatabaseAdministrator);
		mntmDatabaseAdministrator.setActionCommand("openDatabaseAdmin");
		
		mnConfiguration = new JMenu(BUNDLE.getString("MainFrame.mnConfiguration.text")); //$NON-NLS-1$
		menuBar.add(mnConfiguration);
		
		mntmSoftware = new JMenuItem(BUNDLE.getString("MainFrame.mntmSoftwareConfiguration.text"));
		mntmSoftware.setActionCommand("showSoftware");
		mnConfiguration.add(mntmSoftware);
		
		mnAbout = new JMenu(BUNDLE.getString("MainFrame.mnAbout.text")); //$NON-NLS-1$
		menuBar.add(mnAbout);
		
		mntmWelcome = new JMenuItem(BUNDLE.getString("MainFrame.mntmWelcome.text")); //$NON-NLS-1$
		mnAbout.add(mntmWelcome);
		mntmWelcome.setActionCommand("showWelcome");
		
		mntmLicense = new JMenuItem(BUNDLE.getString("MainFrame.mntmLicense.text")); //$NON-NLS-1$
		mntmLicense.setActionCommand("showLicense");
		mnAbout.add(mntmLicense);
		
		mntmUserManual = new JMenuItem(BUNDLE.getString("MainFrame.mntmHelp.text")); //$NON-NLS-1$
		mnAbout.add(mntmUserManual);
		mntmUserManual.setActionCommand("openUserManual");
		
		mntmReferenceGuide = new JMenuItem(BUNDLE.getString("MainFrame.mntmReferenceGuide.text")); //$NON-NLS-1$
		mntmReferenceGuide.setHorizontalAlignment(SwingConstants.TRAILING);
		mntmReferenceGuide.setActionCommand("openReferenceGuide");
		mnAbout.add(mntmReferenceGuide);
		
		mntmWeb = new JMenuItem(BUNDLE.getString("MainFrame.mntmWebPage.text")); //$NON-NLS-1$
		mntmWeb.setActionCommand("openWeb");
		mnAbout.add(mntmWeb);
		
		mntmAgreements = new JMenuItem(BUNDLE.getString("MainFrame.mntmAgreements.text")); //$NON-NLS-1$
		mntmAgreements.setActionCommand("showAcknowledgment");
		mnAbout.add(mntmAgreements);
		
		mntmCheckUpdates = new JMenuItem(BUNDLE.getString("MainFrame.mntmCheckUpdates.text")); //$NON-NLS-1$
		mntmCheckUpdates.setActionCommand(BUNDLE.getString("MainFrame.mntmCheckUpdates.actionCommand_1")); //$NON-NLS-1$
		mnAbout.add(mntmCheckUpdates);
		
		String path = Utils.getAppPath() + "images/download_16.png";
		final ImageIcon iconImage = new ImageIcon(path);
		mnNewVersionAvailable = new JMenu(BUNDLE.getString("MainFrame.mnNewVersionAvailable.text"));
		mnNewVersionAvailable.setActionCommand("downloadNewVersion"); //$NON-NLS-1$
		mnNewVersionAvailable.setVisible(false);
		mnNewVersionAvailable.setIcon(iconImage);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(mnNewVersionAvailable);
		
		mntmDownload = new JMenuItem();
		mntmDownload.setActionCommand("downloadNewVersion");
		mnNewVersionAvailable.add(mntmDownload);
		
		desktopPane = new JDesktopPane();
		desktopPane.setVisible(true);
		desktopPane.setBackground(Color.LIGHT_GRAY);
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(desktopPane)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                    .addGap(1, 1, 1)
        ));
        
		setupListeners();
		
		this.addWindowListener(new WindowAdapter() {
			 @Override
			 public void windowClosing(WindowEvent e) {
			     closeApp();
			 }
		 });	
		
	}
	
	
	@SuppressWarnings("unused")
	private void initFrames() throws PropertyVetoException{

        // Create and Add frames to main Panel
        epaSoftFrame = new EpaSoftFrame();
        hecRasFrame = new HecRasFrame();
        ppFrame = new ProjectPreferencesFrame();
        configFrame = new ConfigFrame();
        gisFrame = new GisFrame();
        gisFrame.setLocation(175, 80);
        
        desktopPane.add(epaSoftFrame);
        desktopPane.add(hecRasFrame);     
        desktopPane.add(ppFrame);            
        desktopPane.add(configFrame);
        desktopPane.add(gisFrame);
        
        // TODO: Set specific configuration
//		swmmFrame.setTitle("EPA SWMM");
//		swmmFrame.getPanel().setDesignButton("Raingage", "showRaingage");
//		swmmFrame.getPanel().setOptionsButton("Options", "showInpOptions");
//		swmmFrame.getPanel().setReportButton("Report options", "showReport");
//		epanetFrame.setTitle("EPANET");
//		epanetFrame.getPanel().setDesignButton("Times values", "showTimesValues");
//		epanetFrame.getPanel().setOptionsButton("Options", "showInpOptionsEpanet");
//		epanetFrame.getPanel().setReportButton("Report options", "showReportEpanet");

        // Get info from properties
		getMainParams("MAIN");

        // Define one controller per panel           
		new HecRasController(hecRasFrame.getPanel());
		new ProjectPreferencesController(ppFrame.getPanel(), this);
		new ConfigController(configFrame.getPanel());
        EpaSoftController mcEpaSof = new EpaSoftController(epaSoftFrame.getPanel(), this, "");
        
        boolean overwrite = Boolean.parseBoolean(prop.get("IMPORT_OVERWRITE", "false"));
		
	}
	
	
	public void updateTitle(String path){
		String title = BUNDLE.getString("MainFrame.this.title");
		if (versionCode != null){
			title+= " v"+versionCode;
		}
		title+= " - " + path;
		setTitle(title);
	}
	
	
	// TODO:
	public void updateFrames(){
		
		try {
			getFrameParams(ppFrame, "PP");
			getFrameParams(configFrame, "CONFIG");			
			getFrameParams(epaSoftFrame, "EPASOFT");
			getFrameParams(hecRasFrame, "HECRAS");
		} catch (PropertyVetoException e) {
			Utils.logError(e);
		}           
	        
		// Only one frame remains visible	
		
	}
	
	
	private void getFrameParams (JInternalFrame frame, String prefix) throws PropertyVetoException{

        int x, y;
        boolean visible;
        x = Integer.parseInt(MainDao.getGswProperties().get(prefix + "_X", "0"));
        y = Integer.parseInt(MainDao.getGswProperties().get(prefix + "_Y", "0"));
        visible = Boolean.parseBoolean(MainDao.getGswProperties().get(prefix + "_VISIBLE", "false"));
        frame.setLocation(x, y);
        frame.setVisible(visible);
		
	}
	
	
	private void putFrameParams (JInternalFrame frame, String prefix) throws PropertyVetoException{
		MainDao.getGswProperties().put(prefix + "_X", frame.getX());
		MainDao.getGswProperties().put(prefix + "_Y", frame.getY());
		MainDao.getGswProperties().put(prefix + "_VISIBLE", frame.isVisible());
		MainDao.getGswProperties().put(prefix + "_SELECTED", frame.isSelected());
	}
	
	
	private void getMainParams (String prefix) throws PropertyVetoException{

        int x, y, width, height;
        boolean maximized;
        x = Integer.parseInt(prop.get(prefix + "_X", "200"));
        y = Integer.parseInt(prop.get(prefix + "_Y", "50"));
        width = Integer.parseInt(prop.get(prefix + "_WIDTH", "800"));
        height = Integer.parseInt(prop.get(prefix + "_HEIGHT", "600"));
        maximized = Boolean.parseBoolean(prop.get(prefix + "_MAXIMIZED", "false"));
        this.setLocation(x, y);
        this.setSize(width, height);
        
        if (maximized){
        	this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
                
	}
	
	
	private void putMainParams (String prefix) throws PropertyVetoException{
		boolean maximized = (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
		prop.put(prefix + "_MAXIMIZED", maximized);		
		prop.put(prefix + "_X", this.getX());
		prop.put(prefix + "_Y", this.getY());
		prop.put(prefix + "_WIDTH", this.getWidth());
		prop.put(prefix + "_HEIGHT", this.getHeight());
		MainDao.savePropertiesFile(); 
	}	
	
	
	public void putEpaSoftParams(){
		
		EpaSoftPanel epaSoftPanel = epaSoftFrame.getPanel();
    	MainDao.getGswProperties().put("FILE_INP", epaSoftPanel.getFileInp());
    	MainDao.getGswProperties().put("FILE_RPT", epaSoftPanel.getFileRpt());
    	MainDao.getGswProperties().put("PROJECT_NAME", epaSoftPanel.getProjectName());   
    	
	}    
    
    public void putHecrasParams(){
    	HecRasPanel hecRasPanel = hecRasFrame.getPanel();
    	MainDao.getGswProperties().put("HECRAS_FILE_ASC", hecRasPanel.getFileAsc());
    	MainDao.getGswProperties().put("HECRAS_FILE_SDF", hecRasPanel.getFileSdf());
    	MainDao.getGswProperties().put("HECRAS_SCHEMA", hecRasPanel.getSelectedSchema());
	}	
    
    public void putDatabaseParams(){
    	
    	ProjectPreferencesPanel ppPanel = ppFrame.getPanel();
    	MainDao.getGswProperties().put("FOLDER_SHP", ppPanel.getFolderShp());    	
    	MainDao.getGswProperties().put("SCHEMA", ppPanel.getSelectedSchema());
    	if (ppPanel.getOptDatabaseSelected()){
    		MainDao.getGswProperties().put("STORAGE", "DATABASE");
    	}
    	else if (ppPanel.getOptDbfSelected()){
    		MainDao.getGswProperties().put("STORAGE", "DBF");
    	}
    	else{
    		MainDao.getGswProperties().put("STORAGE", "");
    	}
    	
    	//PropertiesMap gswProp = MainDao.getGswProperties();
    	MainDao.getGswProperties().put("POSTGIS_HOST", ppPanel.getHost());
    	MainDao.getGswProperties().put("POSTGIS_PORT", ppPanel.getPort());
    	MainDao.getGswProperties().put("POSTGIS_DATABASE", ppPanel.getDatabase());
    	MainDao.getGswProperties().put("POSTGIS_USER", ppPanel.getUser());
    	MainDao.getGswProperties().put("POSTGIS_PASSWORD", Encryption.encrypt(ppPanel.getPassword()));
    	MainDao.getGswProperties().put("POSTGIS_REMEMBER", ppPanel.getRemember().toString());
    	MainDao.getGswProperties().put("POSTGIS_DATA", "");
    	MainDao.getGswProperties().put("POSTGIS_BIN", "");
    	
	}	   
    
    public void putGisParams(){
    	
    	GisPanel gisPanel = gisFrame.getPanel();
    	MainDao.getGswProperties().put("GIS_FOLDER", gisPanel.getProjectFolder());
    	MainDao.getGswProperties().put("GIS_NAME", gisPanel.getProjectName());
    	MainDao.getGswProperties().put("GIS_SOFTWARE", gisPanel.getProjectSoftware());
    	MainDao.getGswProperties().put("GIS_TYPE", gisPanel.getDataStorage());
    	MainDao.getGswProperties().put("GIS_SCHEMA", gisPanel.getSelectedSchema());
    	
	}	
    
	
    // TODO: 
	public void saveGswFile(){

		// Update FILE_GSW parameter 
		prop.put("FILE_GSW", MainDao.getGswPath());
    	
		// Get EPANET and SWMM parameters
//    	EpaPanel epanetPanel = epanetFrame.getPanel();
//    	putEpaParams("EPANET", epanetPanel);
//    	EpaPanel swmmPanel = swmmFrame.getPanel();        	
//    	putEpaParams("EPASWMM", swmmPanel);      
    	
		// Get HECRAS parameters
		putHecrasParams();		
		
		// Get Database parameters
		putDatabaseParams();		
    	
    	// Get GIS parameters
    	putGisParams();
    	
    	MainDao.saveGswPropertiesFile();
        
	}	
	
	
	public void closeApp(){
	
        try {
	        putFrameParams(epaSoftFrame, "");
	        putFrameParams(hecRasFrame, "HECRAS");
	        putFrameParams(ppFrame, "PP");        
	        putFrameParams(configFrame, "CONFIG");	
	        putMainParams("MAIN");
	        saveGswFile();  
	    	Utils.getLogger().info("Application closed");	        
		} catch (PropertyVetoException e) {
            Utils.logError(e.getMessage());			
		}
		
	}
	
	
	private void setupListeners(){
		
		mntmOpenProject.addActionListener(this);
		mntmSaveProject.addActionListener(this);
		mntmOpenPreferences.addActionListener(this);
		mntmSavePreferences.addActionListener(this);
		mntmSaveAsPreferences.addActionListener(this);
		mntmSoftware.addActionListener(this);
		mntmSampleEpanet.addActionListener(this);
		mntmSampleEpaswmm.addActionListener(this);
		mntmSampleHecras.addActionListener(this);	
		mntmDatabaseAdministrator.addActionListener(this);		
		
		mntmWelcome.addActionListener(this);
		mntmLicense.addActionListener(this);		
		mntmAgreements.addActionListener(this);		
		mntmUserManual.addActionListener(this);
		mntmReferenceGuide.addActionListener(this);		
		mntmWeb.addActionListener(this);
		mntmCheckUpdates.addActionListener(this);
		
		mntmDownload.addActionListener(this);
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		menuController.action(e.getActionCommand());
	}


	public void openHecras() {
		manageFrames(hecRasFrame);
	}	
	
	public void openProjectPreferences() {
		manageFrames(ppFrame);
	}	

	public void openSoftware() {
		manageFrames(configFrame);
	}
	
	public void openGisProject() {
		manageFrames(gisFrame);
		gisFrame.setGisExtension("qgs");
		gisFrame.setGisTitle(Utils.getBundleString("gis_panel_qgis"));
		try {
			gisFrame.setMaximum(false);
		} catch (PropertyVetoException e) {
            Utils.logError(e);
		}		
	}	
	
	public void updateEpaFrames(){
		//epaSoftFrame.getPanel().selectSourceType();
	}
	
	public void enableCatalog(boolean enable) {
		
		mntmOpenProject.setEnabled(enable);
		mntmSaveProject.setEnabled(enable);
		mntmSampleEpanet.setEnabled(enable);
		mntmSampleEpaswmm.setEnabled(enable);
		mntmSampleHecras.setEnabled(enable);
		
	}
	
	
    private void manageFrames(JInternalFrame frame) {
    	
        try {   	
            frame.setMaximum(true);
            frame.setVisible(true); 
            frame.setMaximum(true);            
        } catch (PropertyVetoException e) {
            Utils.logError(e);
        }
        
    }


	public void setCursorFrames(Cursor cursor) {
		
		epaSoftFrame.getPanel().setCursor(cursor);
		hecRasFrame.getPanel().setCursor(cursor);
		configFrame.getPanel().setCursor(cursor);
		gisFrame.getPanel().setCursor(cursor);
		this.setCursor(cursor);
		
	}
	
	
}