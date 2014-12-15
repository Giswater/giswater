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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.ConfigController;
import org.giswater.controller.EpaSoftController;
import org.giswater.controller.HecRasController;
import org.giswater.controller.MenuController;
import org.giswater.controller.ProjectPreferencesController;
import org.giswater.dao.ConfigDao;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.GisPanel;
import org.giswater.gui.panel.HecRasPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MainFrame extends JFrame implements ActionListener{
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 

	private static final long serialVersionUID = -6630818426483107558L;
	private MenuController menuController;
	private PropertiesMap prop;
	private String versionCode;
	
    public JDesktopPane desktopPane;
    
    private JMenu mnProject;
	private JMenuItem mntmNewPreferences;
	private JMenuItem mntmOpenPreferences;
	private JMenuItem mntmSavePreferences;
	private JMenuItem mntmSaveAsPreferences;
	private JMenuItem mntmEditPreferences;
	private JSeparator separator;
	private JMenuItem mntmOpenProject;
	private JMenuItem mntmSaveProject;
	private JMenuItem mntmExit;
	
	private JMenu mnProjectExample;	
	
	private JMenu mnConfiguration;
	private JMenuItem mntmSoftware;
	private JMenuItem mntmExampleEpanet;
	private JMenuItem mntmExampleEpaswmm;
	private JMenuItem mntmExampleEpaswmm2D;
	private JMenuItem mntmExampleHecras;
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
	
	private JPanel statusPanel;
	private JLabel lblInfo;
	private JLabel lblProcessInfo;
	private JProgressBar progressInfo;
	
	private ImageIcon iconInfo;
	private ImageIcon iconAlert;
	private ImageIcon iconGreen;
	private ImageIcon iconRed;
	private JMenuItem mntmSqlFileLauncher;

	
	/**
	 * @wbp.parser.constructor
	 */
	public MainFrame(boolean isConnected, String versionCode) {
		this(isConnected, versionCode, false, "");
	}
	
	
	public MainFrame(boolean isConnected, String versionCode, boolean newVersion, String ftpVersion) {
		
		this.versionCode = versionCode;
		initConfig();
		setIcons();
		setNewVersionVisible(newVersion, ftpVersion);
		initFrames();
		
	}


	public void setNewVersionVisible(boolean newVersion, String ftpVersion) {
		mnNewVersionAvailable.setVisible(newVersion);
		String msg = "Download version "+ftpVersion;
		mntmDownload.setText(msg);
	}


	public void setControl(MenuController menuController) {
		this.menuController = menuController;
	}	
	
	
	public MenuController getController() {
		return menuController;
	}
	
	
	private void initConfig(){

		ImageIcon image = new ImageIcon("images/imago.png");
		setIconImage(image.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		prop = PropertiesDao.getPropertiesFile();
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnProject = new JMenu(BUNDLE.getString("MainFrame.mnProject.text")); 
		mnProject.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnProject);
		
		separator = new JSeparator();
		mnProject.add(separator);
		
		mntmNewPreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmNewProjectPreferences.text")); 
		mntmNewPreferences.setMnemonic(KeyEvent.VK_N);
		mntmNewPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mntmNewPreferences.setActionCommand("gswNew"); 
		mnProject.add(mntmNewPreferences);
		
		mntmOpenPreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmOpen.text")); 
		mntmOpenPreferences.setMnemonic(KeyEvent.VK_O);
		mntmOpenPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmOpenPreferences.setActionCommand("gswOpen");
		mnProject.add(mntmOpenPreferences);
		
		mntmSavePreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmSave.text")); 
		mntmSavePreferences.setMnemonic(KeyEvent.VK_S);
		mntmSavePreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmSavePreferences.setActionCommand("gswSave");
		mnProject.add(mntmSavePreferences);
		
		mntmSaveAsPreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmSaveAs.text")); 
		mntmSaveAsPreferences.setMnemonic(KeyEvent.VK_A);
		mntmSaveAsPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmSaveAsPreferences.setActionCommand("gswSaveAs");
		mnProject.add(mntmSaveAsPreferences);
		
	    mntmEditPreferences = new JMenuItem(BUNDLE.getString("MainFrame.mntmEditProjectPreferences.text")); 
		mntmEditPreferences.setActionCommand("gswEdit"); 
		mntmEditPreferences.setMnemonic(KeyEvent.VK_E);
		mntmEditPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		mnProject.add(mntmEditPreferences);
		
		JSeparator separator_1 = new JSeparator();
		mnProject.add(separator_1);
		
		mntmOpenProject = new JMenuItem(BUNDLE.getString("MainFrame.mntmOpenProject.text")); 
		mntmOpenProject.setMnemonic(KeyEvent.VK_R);
		mntmOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mntmOpenProject.setActionCommand("openProject");
		mnProject.add(mntmOpenProject);
		
		mntmSaveProject = new JMenuItem(BUNDLE.getString("MainFrame.mntmSaveProject.text")); 
		mntmSaveProject.setMnemonic(KeyEvent.VK_B);
		mntmSaveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
		mntmSaveProject.setActionCommand("saveProject");
		mnProject.add(mntmSaveProject);
		
		JSeparator separator_2 = new JSeparator();
		mnProject.add(separator_2);
		
		mntmExit = new JMenuItem(BUNDLE.getString("MainFrame.mntmExit.text")); //$NON-NLS-1$
		mntmExit.setMnemonic(KeyEvent.VK_Q);
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mntmExit.setActionCommand("exit");
		mnProject.add(mntmExit);
		
		mnProjectExample = new JMenu(BUNDLE.getString("MainFrame.mnGisProject.text"));
		mnProjectExample.setMnemonic(KeyEvent.VK_P);
		menuBar.add(mnProjectExample);
		
		mntmExampleEpanet = new JMenuItem(BUNDLE.getString("MainFrame.mntmNewMenuItem.text"), KeyEvent.VK_W);
		mntmExampleEpanet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK));
		mnProjectExample.add(mntmExampleEpanet);
		mntmExampleEpanet.setActionCommand("exampleEpanet"); 
		
		mntmExampleEpaswmm = new JMenuItem(BUNDLE.getString("MainFrame.mntmCreateEpaswmmSample.text"));
		mntmExampleEpaswmm.setMnemonic(KeyEvent.VK_U);
		mntmExampleEpaswmm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK));
		mnProjectExample.add(mntmExampleEpaswmm);
		mntmExampleEpaswmm.setActionCommand("exampleEpaswmm"); 
		
		mntmExampleEpaswmm2D = new JMenuItem(BUNDLE.getString("MainFrame.mntmUrbanDrainaged.text")); //$NON-NLS-1$
		mntmExampleEpaswmm2D.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK));
		mntmExampleEpaswmm2D.setActionCommand(BUNDLE.getString("MainFrame.mntmExampleEpaswmm2D.actionCommand")); //$NON-NLS-1$
		mnProjectExample.add(mntmExampleEpaswmm2D);
		
		mntmExampleHecras = new JMenuItem(BUNDLE.getString("MainFrame.mntmCreateHecrasSample.text"));
		mntmExampleHecras.setMnemonic(KeyEvent.VK_R);
		mntmExampleHecras.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_MASK));
		mnProjectExample.add(mntmExampleHecras);
		mntmExampleHecras.setActionCommand("exampleHecras"); 
		
		JMenu mnData = new JMenu(BUNDLE.getString("MainFrame.mnData.text")); 
		mnData.setMnemonic(KeyEvent.VK_D);
		menuBar.add(mnData);
		
		mntmDatabaseAdministrator = new JMenuItem(BUNDLE.getString("MainFrame.mntmDatabaseAdministrator.text"));
		mntmDatabaseAdministrator.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mntmDatabaseAdministrator.setMnemonic(KeyEvent.VK_D);
		mnData.add(mntmDatabaseAdministrator);
		mntmDatabaseAdministrator.setActionCommand("openDatabaseAdmin");
		
		mntmSqlFileLauncher = new JMenuItem(BUNDLE.getString("MainFrame.mntmSqlFileLauncher.text")); //$NON-NLS-1$
		mntmSqlFileLauncher.setActionCommand(BUNDLE.getString("MainFrame.mntmSqlFileLauncher.actionCommand")); //$NON-NLS-1$
		mntmSqlFileLauncher.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		mnData.add(mntmSqlFileLauncher);
		
		mnConfiguration = new JMenu(BUNDLE.getString("MainFrame.mnConfiguration.text"));
		menuBar.add(mnConfiguration);
		
		mntmSoftware = new JMenuItem(BUNDLE.getString("MainFrame.mntmSoftwareConfiguration.text"));
		mntmSoftware.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		mntmSoftware.setMnemonic(KeyEvent.VK_S);
		mntmSoftware.setActionCommand("showSoftware");
		mnConfiguration.add(mntmSoftware);
		
		mnAbout = new JMenu(BUNDLE.getString("MainFrame.mnAbout.text"));
		menuBar.add(mnAbout);
		
		mntmWelcome = new JMenuItem(BUNDLE.getString("MainFrame.mntmWelcome.text")); 
		mntmWelcome.setMnemonic(KeyEvent.VK_W);
		mntmWelcome.setActionCommand("showWelcome");
		mnAbout.add(mntmWelcome);
		
		mntmLicense = new JMenuItem(BUNDLE.getString("MainFrame.mntmLicense.text")); 
		mntmLicense.setMnemonic(KeyEvent.VK_L);
		mntmLicense.setActionCommand("showLicense");
		mnAbout.add(mntmLicense);
		
		mntmUserManual = new JMenuItem(BUNDLE.getString("MainFrame.mntmHelp.text")); 
		mntmUserManual.setMnemonic(KeyEvent.VK_U);
		mnAbout.add(mntmUserManual);
		mntmUserManual.setActionCommand("openUserManual");
		
		mntmReferenceGuide = new JMenuItem(BUNDLE.getString("MainFrame.mntmReferenceGuide.text")); 
		mntmReferenceGuide.setMnemonic(KeyEvent.VK_T);
		mntmReferenceGuide.setHorizontalAlignment(SwingConstants.TRAILING);
		mntmReferenceGuide.setActionCommand("openReferenceGuide");
		mnAbout.add(mntmReferenceGuide);
		
		mntmWeb = new JMenuItem(BUNDLE.getString("MainFrame.mntmWebPage.text")); 
		mntmWeb.setActionCommand("openWeb");
		mnAbout.add(mntmWeb);
		
		mntmAgreements = new JMenuItem(BUNDLE.getString("MainFrame.mntmAgreements.text")); 
		mntmAgreements.setMnemonic(KeyEvent.VK_A);
		mntmAgreements.setActionCommand("showAcknowledgment");
		mnAbout.add(mntmAgreements);
		
		mntmCheckUpdates = new JMenuItem(BUNDLE.getString("MainFrame.mntmCheckUpdates.text")); 
		mntmCheckUpdates.setMnemonic(KeyEvent.VK_C);
		mntmCheckUpdates.setActionCommand("checkUpdates");
		mnAbout.add(mntmCheckUpdates);
		
		String path = Utils.getAppPath() + "images/download_16.png";
		final ImageIcon iconImage = new ImageIcon(path);
		mnNewVersionAvailable = new JMenu(BUNDLE.getString("MainFrame.mnNewVersionAvailable.text"));
		mnNewVersionAvailable.setActionCommand("downloadNewVersion"); 
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
		
        statusPanel = new JPanel();
        statusPanel.setBounds(54, 596, 446, 34);
        desktopPane.add(statusPanel);
        statusPanel.setLayout(new MigLayout("", "[50px:80px,grow][10px:n][100px:200px,grow][130px:n:130px]", "[::25px,fill]"));
        
        lblInfo = new JLabel();
        lblInfo.setFont(new Font("Tahoma", Font.PLAIN, 11));
        statusPanel.add(lblInfo, "cell 0 0");
        
        lblProcessInfo = new JLabel();
        lblProcessInfo.setFont(new Font("Tahoma", Font.PLAIN, 11));
        statusPanel.add(lblProcessInfo, "cell 2 0,alignx right");
        
        progressInfo = new JProgressBar();
        progressInfo.setFont(new Font("Tahoma", Font.PLAIN, 10));
        progressInfo.setVisible(false);
        statusPanel.add(progressInfo, "cell 3 0,alignx left");
                
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(desktopPane)
                .addComponent(statusPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(desktopPane, GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                    .addGap(1, 1, 1)
                    .addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

		setupListeners();
		
		this.addWindowListener(new WindowAdapter() {
			 @Override
			 public void windowClosing(WindowEvent e) {
			     closeApp();
			 }
		 });	
		
	}
	
	
	private void initFrames() {
		
		// Create and Add frames to main Panel
        epaSoftFrame = new EpaSoftFrame();
        hecRasFrame = new HecRasFrame();
        ppFrame = new ProjectPreferencesFrame();
        configFrame = new ConfigFrame();
              
        desktopPane.add(epaSoftFrame);
        desktopPane.add(hecRasFrame);     
        desktopPane.add(ppFrame);            
        desktopPane.add(configFrame);
        
        // Set specific configuration
        ppFrame.setTitle("Project Preferences");
        epaSoftFrame.setTitle("Main form");
        
        // Define one controller per panel           
		new HecRasController(hecRasFrame.getPanel(), this);
		new ProjectPreferencesController(ppFrame.getPanel(), this);
		new ConfigController(configFrame.getPanel());
        new EpaSoftController(epaSoftFrame.getPanel(), this);
        
        // Set frame sizes accordin desktop size
        getMainParams("MAIN");
        setVisible(true);
        Dimension desktopSize = desktopPane.getSize();
        epaSoftFrame.setSize(desktopSize);
        epaSoftFrame.setPreferredSize(desktopSize);
        hecRasFrame.setSize(desktopSize);
        hecRasFrame.setPreferredSize(desktopSize);
        ppFrame.setSize(desktopSize);
        ppFrame.setPreferredSize(desktopSize);
        configFrame.setSize(desktopSize);
        configFrame.setPreferredSize(desktopSize);
		
	}
	
	
	public void updateTitle(String path) {
		
		String title = BUNDLE.getString("MainFrame.this.title");
		if (versionCode != null) {
			title+= " "+versionCode;
		}
		title+= " - " + path;
		setTitle(title);
		
	}
	
	
	public void updateFrames() {
		getFrameParams(configFrame, "CONFIG");			
		getFrameParams(epaSoftFrame, "EPASOFT");
		getFrameParams(hecRasFrame, "HECRAS");
		getFrameParams(ppFrame, "PP");           
	}
	
	
	private void getFrameParams (JInternalFrame frame, String prefix) {
        boolean visible = Boolean.parseBoolean(PropertiesDao.getGswProperties().get(prefix + "_VISIBLE", "false"));
        frame.setVisible(visible);
	}
	
	
	private void putFrameParams (JInternalFrame frame, String prefix) {
		PropertiesDao.getGswProperties().put(prefix + "_VISIBLE", frame.isVisible());
	}
	
	
	private void getMainParams (String prefix) {

        int x, y, width, height;
        boolean maximized;
        x = Integer.parseInt(prop.get(prefix + "_X", "200"));
        y = Integer.parseInt(prop.get(prefix + "_Y", "100"));
        width = Integer.parseInt(prop.get(prefix + "_WIDTH", "665"));
        height = Integer.parseInt(prop.get(prefix + "_HEIGHT", "690"));
        maximized = Boolean.parseBoolean(prop.get(prefix + "_MAXIMIZED", "false"));
        this.setLocation(x, y);
        this.setSize(width, height);
        
        if (maximized) {
        	this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
                
	}
	
	
	private void putMainParams (String prefix) {
		
		boolean maximized = (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
		prop.put(prefix + "_MAXIMIZED", maximized);		
		prop.put(prefix + "_X", this.getX());
		prop.put(prefix + "_Y", this.getY());
		prop.put(prefix + "_WIDTH", this.getWidth());
		prop.put(prefix + "_HEIGHT", this.getHeight());
		PropertiesDao.savePropertiesFile(); 
		
	}	
	
	
	public void putEpaSoftParams() {
		
		EpaSoftPanel epaSoftPanel = epaSoftFrame.getPanel();
		PropertiesDao.getGswProperties().put("FILE_INP", epaSoftPanel.getFileInp());
		PropertiesDao.getGswProperties().put("FILE_RPT", epaSoftPanel.getFileRpt());
		PropertiesDao.getGswProperties().put("PROJECT_NAME", epaSoftPanel.getProjectName());   
    	
	}    
	
    
    public void putHecrasParams() {
    	
    	HecRasPanel hecRasPanel = hecRasFrame.getPanel();
    	PropertiesDao.getGswProperties().put("HECRAS_FILE_ASC", hecRasPanel.getFileAsc());
    	PropertiesDao.getGswProperties().put("HECRAS_FILE_SDF", hecRasPanel.getFileSdf());
    	
	}	
    
    
    public void putProjectPreferencecsParams() {
    	
    	ProjectPreferencesPanel ppPanel = ppFrame.getPanel();
    	
    	PropertiesDao.getGswProperties().put("SOFTWARE", ppPanel.getWaterSoftware());    	
    	String versionId = ppPanel.getVersionSoftware();
    	String exeName = ConfigDao.getExeName(versionId);
    	PropertiesDao.getGswProperties().put("VERSION", versionId);  
    	PropertiesDao.getGswProperties().put("EXE_NAME", exeName);  
    	if (ppPanel.getOptDatabaseSelected()){
    		PropertiesDao.getGswProperties().put("STORAGE", "DATABASE");
    	}
    	else if (ppPanel.getOptDbfSelected()) {
    		PropertiesDao.getGswProperties().put("STORAGE", "DBF");
    	}
    	else {
    		PropertiesDao.getGswProperties().put("STORAGE", "");
    	}	
    	PropertiesDao.getGswProperties().put("FOLDER_SHP", ppPanel.getFolderShp());    	
    	PropertiesDao.getGswProperties().put("SCHEMA", ppPanel.getSelectedSchema());
    	PropertiesDao.getGswProperties().put("POSTGIS_HOST", ppPanel.getHost());
    	PropertiesDao.getGswProperties().put("POSTGIS_PORT", ppPanel.getPort());
    	PropertiesDao.getGswProperties().put("POSTGIS_DATABASE", ppPanel.getDatabase());
    	PropertiesDao.getGswProperties().put("POSTGIS_USER", ppPanel.getUser());
    	PropertiesDao.getGswProperties().put("POSTGIS_PASSWORD", Encryption.encrypt(ppPanel.getPassword()));
    	PropertiesDao.getGswProperties().put("POSTGIS_REMEMBER", ppPanel.isRememberSelected().toString());
    	PropertiesDao.getGswProperties().put("POSTGIS_USESSL", ppPanel.isUseSslSelected().toString());
    	
	}	   
    
    
    public void putGisParams(GisPanel gisPanel) {
    	PropertiesDao.getGswProperties().put("GIS_FOLDER", gisPanel.getProjectFolder());
    	PropertiesDao.getGswProperties().put("GIS_NAME", gisPanel.getProjectName());    	
	}	
    
	
	public void saveGswFile() {

		// Update FILE_GSW parameter 
		prop.put("FILE_GSW", PropertiesDao.getGswPath());
		
		// Get EPASOFT (EPANET or SWMM) parameters
		putEpaSoftParams();
    	
		// Get HECRAS parameters
		putHecrasParams();		
		
		// Get Project preferences parameters
		putProjectPreferencecsParams();		
    	
    	// Save .gsw file
		PropertiesDao.saveGswPropertiesFile();
        
	}	
	
	
	public void closeApp() {
	
        putFrameParams(epaSoftFrame, "EPASOFT");
		putFrameParams(hecRasFrame, "HECRAS");
		putFrameParams(ppFrame, "PP");        
		putFrameParams(configFrame, "CONFIG");	
		putMainParams("MAIN");
		saveGswFile();  
		Utils.getLogger().info("Application closed");
		
	}
	
	
	private void setupListeners() {
		
		mntmNewPreferences.addActionListener(this);
		mntmOpenProject.addActionListener(this);
		mntmSaveProject.addActionListener(this);
		mntmOpenPreferences.addActionListener(this);
		mntmSavePreferences.addActionListener(this);
		mntmSaveAsPreferences.addActionListener(this);
		mntmEditPreferences.addActionListener(this);
		mntmExit.addActionListener(this);
		
		mntmExampleEpanet.addActionListener(this);
		mntmExampleEpaswmm.addActionListener(this);
		mntmExampleEpaswmm2D.addActionListener(this);
		mntmExampleHecras.addActionListener(this);	
		
		mntmDatabaseAdministrator.addActionListener(this);		
		mntmSqlFileLauncher.addActionListener(this);		
		
		mntmSoftware.addActionListener(this);
		
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


	public void openSoftware() {
		manageFrames(configFrame);
	}
	

	public void updateEpaFrames() {
		ppFrame.getPanel().selectSourceType(false);
	}
	
	public void enableMenuDatabase(boolean enable) {
		
		mntmOpenProject.setEnabled(enable);
		mntmSaveProject.setEnabled(enable);
		mntmExampleEpanet.setEnabled(enable);
		mntmExampleEpaswmm.setEnabled(enable);
		mntmExampleHecras.setEnabled(enable);
		
	}
	
	
    private void manageFrames(JInternalFrame frame) {
		try {
			frame.setMaximum(true);
			frame.setVisible(true);        
		} catch (PropertyVetoException e) {
			Utils.logError(e);
		}
    }


	public void setCursorFrames(Cursor cursor) {
		
		epaSoftFrame.getPanel().setCursor(cursor);
		hecRasFrame.getPanel().setCursor(cursor);
		configFrame.getPanel().setCursor(cursor);
		this.setCursor(cursor);
		
	}

	
	// Status Bar functions
	public void setIcons() {
		
		String path;
		String imgFolder = Utils.getAppPath() + "images" + File.separator;
		
		path = imgFolder + "info_16.png";	
		if (new File(path).exists()) {
			iconInfo = new ImageIcon(path);
		}
		path = imgFolder + "alert_16.png";	
		if (new File(path).exists()) {
			iconAlert = new ImageIcon(path);
		}
		path = imgFolder + "green_16.png";	
		if (new File(path).exists()) {
			iconGreen = new ImageIcon(path);
		}
		path = imgFolder + "red_16.png";	
		if (new File(path).exists()) {
			iconRed = new ImageIcon(path);
		}
		
	}

	
	// Valid when DB is selected
	public void updateConnectionInfo() {
		
		String schema;
		String info = "";
		if (MainDao.isConnected()) {
			schema = MainDao.getSchema();
			if (schema != null && !schema.equals("")) {
				info+= schema;
				ppFrame.getPanel().enableAccept(true);
			}
			else {
				info+= "Any project data selected";
				ppFrame.getPanel().enableAccept(false);
			}
			lblInfo.setIcon(iconGreen);
		}
		else {
			ppFrame.getPanel().enableAccept(false);
			lblInfo.setIcon(iconRed);
		}
		lblInfo.setText(info);
		
	}
	
	
	public void resetConnectionInfo() {
		ppFrame.getPanel().enableAccept(true);
		lblInfo.setIcon(null);
		lblInfo.setText("");
	}
	
	
	public void setProgressVisible(boolean visible) {
		progressInfo.setValue(0);
		progressInfo.setVisible(visible);
	}
	
	public void setProgressBarValue(int progress) {
		progressInfo.setVisible(true);
		progressInfo.setIndeterminate(true);
		progressInfo.setValue(progress);
	}

	public void setProgressBarEnd() {
		progressInfo.setVisible(true);
		progressInfo.setIndeterminate(false);
		progressInfo.setValue(100);
	}
	
	public void showMessage(String msg) {
		showMessage(msg, false, 5000);
	}
	
	public void showMessage(String msg, boolean isFixed) {
		showMessage(msg, isFixed, 5000);
	}
	
	public void showMessage(String msg, int delay) {
		showMessage(msg, false, delay);
	}
	
	public void showMessage(String msg, boolean isFixed, int delay) {
		lblProcessInfo.setIcon(iconInfo);		
		lblProcessInfo.setText(Utils.getBundleString(msg));
		if (!isFixed) {
			resetProcessInfo(delay);		
		}
	}
	
	public void showError(String msg) {
		lblProcessInfo.setIcon(iconAlert);	
		lblProcessInfo.setText(Utils.getBundleString(msg));
		Utils.logError(msg);
		resetProcessInfo();
	}
	
	public void showError(String msg, String param) {
		lblProcessInfo.setIcon(iconAlert);	
		lblProcessInfo.setText(Utils.getBundleString(msg) + " "+param);
		Utils.logError(msg);
		resetProcessInfo();
	}
	
	// Show info 5 seconds
	public void resetProcessInfo() {
		resetProcessInfo(5000);
	}
	
	public void resetProcessInfo(int delay) {
		
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				lblProcessInfo.setIcon(null);
				lblProcessInfo.setText("");
				progressInfo.setVisible(false);
		    }
		};
		Timer timer = new Timer(delay, taskPerformer);
		timer.setRepeats(false);
		timer.start();
		
	}
}