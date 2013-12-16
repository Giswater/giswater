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
package org.giswater.gui.frame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.giswater.controller.ConfigController;
import org.giswater.controller.DatabaseController;
import org.giswater.controller.HecRasController;
import org.giswater.controller.MainController;
import org.giswater.controller.MenuController;
import org.giswater.dao.MainDao;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MainFrame extends JFrame implements ActionListener{
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private static final long serialVersionUID = -6630818426483107558L;
	private MenuController menuController;
    private JDesktopPane desktopPane;
    
    private JMenuItem mntmSwmm;
	private JMenuItem mntmEpanet;
	private JMenuItem mntmHecras;

	private JMenu mnCatalog;
	private JMenuItem mntmProjectId;	
	private JMenuItem mntmConduit;
	private JMenuItem mntmMaterials;
	private JMenuItem mntmTimeseries;
	private JMenuItem mntmCurves;
	private JMenuItem mntmPatterns;	
	
	private JMenu mnAnalysis;	
	private JMenuItem mntmCatalog;
	private JMenuItem mntmManagement;
	
	private JMenu mnConfiguration;
	private JMenuItem mntmSoftware;
	private JMenuItem mntmDatabase;
	private JMenuItem mntmQgis;
	private JMenuItem mntmGvsig;
	
	private JMenuItem mntmWelcome;		
	private JMenuItem mntmVersion;
	private JMenuItem mntmLicense;
	private JMenuItem mntmAgreements;
	private JMenuItem mntmUserManual;	
	private JMenuItem mntmReferenceGuide;	
	private JMenuItem mntmWeb;

	public EpaFrame swmmFrame;
	public EpaFrame epanetFrame;
	public HecRasFrame hecRasFrame;
	
	public DatabaseFrame dbFrame;
	public ConfigFrame configFrame;
	public GisFrame gisFrame;
		
	private PropertiesMap prop;


	public MainFrame(boolean isConnected) {
		initConfig();
		try {
			initFrames();
			hecRasFrame.getPanel().enableButtons(isConnected);
		} catch (PropertyVetoException e) {
            Utils.logError(e.getMessage());
		}
	}

	
	public void setControl(MenuController menuController) {
		this.menuController = menuController;
	}	
	
	
	private void initConfig(){

		ImageIcon image = new ImageIcon("images/imago.png");
		setIconImage(image.getImage());
		setTitle(BUNDLE.getString("MainFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		prop = MainDao.getPropertiesFile();
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnForms = new JMenu("Software");
		menuBar.add(mnForms);
		
		mntmSwmm = new JMenuItem("EPA SWMM");
		mntmSwmm.setActionCommand("openSwmm");
		mnForms.add(mntmSwmm);
		
		mntmEpanet = new JMenuItem("EPANET");
		mntmEpanet.setActionCommand("openEpanet");
		mnForms.add(mntmEpanet);
		
		mntmHecras = new JMenuItem("HECRAS");
		mntmHecras.setActionCommand("openHecras");
		mnForms.add(mntmHecras);
		
		JMenu mnGisProject = new JMenu(BUNDLE.getString("MainFrame.mnGisProject.text"));
		menuBar.add(mnGisProject);
		
		mntmQgis = new JMenuItem(BUNDLE.getString("MainFrame.mntmQgis.text"));
		mntmQgis.setActionCommand("showProjectQGIS");
		mnGisProject.add(mntmQgis);
		
		mntmGvsig = new JMenuItem(BUNDLE.getString("MainFrame.mntmGvsig.text"));
		mntmGvsig.setActionCommand("showProjectGVSIG");
		mnGisProject.add(mntmGvsig);
		
		mnCatalog = new JMenu(BUNDLE.getString("MainFrame.mnManager.text")); //$NON-NLS-1$
		mnCatalog.setEnabled(false);
		menuBar.add(mnCatalog);
		
		mntmProjectId = new JMenuItem(BUNDLE.getString("MainFrame.mntmProjectId.text"));
		mntmProjectId.setFocusCycleRoot(true);
		mntmProjectId.setActionCommand("showProjectId");
		mnCatalog.add(mntmProjectId);
		
		mntmConduit = new JMenuItem(BUNDLE.getString("MainFrame.mntmConduit.text")); //$NON-NLS-1$
		mntmConduit.setActionCommand("showConduit");
		mnCatalog.add(mntmConduit);
		
		mntmMaterials = new JMenuItem(BUNDLE.getString("MainFrame.mntmMaterials.text")); //$NON-NLS-1$
		mntmMaterials.setActionCommand("showMaterials");
		mnCatalog.add(mntmMaterials);
		
		mntmTimeseries = new JMenuItem(BUNDLE.getString("MainFrame.mntmTimeseries.text"));
		mntmTimeseries.setActionCommand("showTimeseries");
		mnCatalog.add(mntmTimeseries);
		
		mntmCurves = new JMenuItem(BUNDLE.getString("MainFrame.mntmCurves.text"));
		mntmCurves.setActionCommand("showCurves");
		mnCatalog.add(mntmCurves);
		
		mntmPatterns = new JMenuItem(BUNDLE.getString("MainFrame.mntmPatterns.text")); //$NON-NLS-1$
		mntmPatterns.setActionCommand("showPatterns");
		mnCatalog.add(mntmPatterns);
		
		mnAnalysis = new JMenu(BUNDLE.getString("MainFrame.mnScenarios.text"));
		menuBar.add(mnAnalysis);
		
		mntmCatalog = new JMenuItem(BUNDLE.getString("MainFrame.mntmCatalog.text"));
		mntmCatalog.setActionCommand("scenarioCatalog");
		mnAnalysis.add(mntmCatalog);
		
		mntmManagement = new JMenuItem(BUNDLE.getString("MainFrame.mntmManagement.text")); //$NON-NLS-1$
		mntmManagement.setActionCommand("scenarioManagement");
		mnAnalysis.add(mntmManagement);
		
		mnConfiguration = new JMenu(BUNDLE.getString("MainFrame.mnConfiguration.text")); //$NON-NLS-1$
		menuBar.add(mnConfiguration);
		
		mntmDatabase = new JMenuItem(BUNDLE.getString("MainFrame.mntmDatabase.text")); //$NON-NLS-1$
		mntmDatabase.setActionCommand("showDatabase");
		mnConfiguration.add(mntmDatabase);
		
		mntmSoftware = new JMenuItem(BUNDLE.getString("MainFrame.mntmSoftwareConfiguration.text"));
		mntmSoftware.setActionCommand("showSoftware");
		mnConfiguration.add(mntmSoftware);
		
		JMenu mnAbout = new JMenu(BUNDLE.getString("MainFrame.mnAbout.text")); //$NON-NLS-1$
		menuBar.add(mnAbout);
		
		mntmWelcome = new JMenuItem(BUNDLE.getString("MainFrame.mntmWelcome.text")); //$NON-NLS-1$
		mnAbout.add(mntmWelcome);
		mntmWelcome.setActionCommand("showWelcome");
		
		mntmVersion = new JMenuItem(BUNDLE.getString("MainFrame.mntmVersion.text")); //$NON-NLS-1$
		mntmVersion.setActionCommand("showVersion");
		mnAbout.add(mntmVersion);
		
		mntmLicense = new JMenuItem(BUNDLE.getString("MainFrame.mntmLicense.text")); //$NON-NLS-1$
		mntmLicense.setActionCommand("showLicense");
		mnAbout.add(mntmLicense);
		
		mntmAgreements = new JMenuItem(BUNDLE.getString("MainFrame.mntmAgreements.text")); //$NON-NLS-1$
		mntmAgreements.setActionCommand("showAgreements");
		mnAbout.add(mntmAgreements);
		
		mntmUserManual = new JMenuItem(BUNDLE.getString("MainFrame.mntmHelp.text")); //$NON-NLS-1$
		mnAbout.add(mntmUserManual);
		mntmUserManual.setActionCommand("openUserManual");
		
		mntmReferenceGuide = new JMenuItem(BUNDLE.getString("MainFrame.mntmReferenceGuide.text")); //$NON-NLS-1$
		mntmReferenceGuide.setActionCommand("openReferenceGuide");
		mnAbout.add(mntmReferenceGuide);
		
		mntmWeb = new JMenuItem(BUNDLE.getString("MainFrame.mntmWebPage.text")); //$NON-NLS-1$
		mntmWeb.setActionCommand("openWeb");
		mnAbout.add(mntmWeb);
		
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
        swmmFrame = new EpaFrame("epaswmm");
        epanetFrame = new EpaFrame("epanet");
        hecRasFrame = new HecRasFrame();
        dbFrame = new DatabaseFrame(this);
        configFrame = new ConfigFrame();
        gisFrame = new GisFrame();
        gisFrame.setTitle(BUNDLE.getString("MainFrame.gisFrame.title")); //$NON-NLS-1$
        gisFrame.setLocation(177, 81);
        
        desktopPane.add(swmmFrame);
        desktopPane.add(epanetFrame);
        desktopPane.add(hecRasFrame);     
        desktopPane.add(dbFrame);        
        desktopPane.add(configFrame);
        desktopPane.add(gisFrame);  
        
        // Set specific configuration
		swmmFrame.setTitle("EPASWMM");
		swmmFrame.getPanel().setDesignButton("Raingage", "showRaingage");
		swmmFrame.getPanel().setOptionsButton("Options", "showInpOptions");
		swmmFrame.getPanel().setReportButton("Report options", "showReport");
		epanetFrame.setTitle("EPANET");
		epanetFrame.getPanel().setDesignButton("Times values", "showTimesValues");
		epanetFrame.getPanel().setOptionsButton("Options", "showInpOptionsEpanet");
		epanetFrame.getPanel().setReportButton("Report options", "showReportEpanet");

        // Get info from properties
		getMainParams("MAIN");
        getFrameParams(swmmFrame, "SWMM");
        getFrameParams(epanetFrame, "EPANET");
        getFrameParams(hecRasFrame, "HECRAS");
        getFrameParams(dbFrame, "DB");      
        getFrameParams(configFrame, "CONFIG");

        // Define one controller per panel           
		new HecRasController(hecRasFrame.getPanel(), this);
		new DatabaseController(dbFrame.getPanel(), this);
		new ConfigController(configFrame.getPanel());
        MainController mcSwmm = new MainController(swmmFrame.getPanel(), this, "EPASWMM");   
        MainController mcEpanet = new MainController(epanetFrame.getPanel(), this, "EPANET");
        
        boolean overwrite = Boolean.parseBoolean(prop.get("IMPORT_OVERWRITE", "true"));
        mnAnalysis.setEnabled(!overwrite);
        
        // TODO: Test
//        mcSwmm.selectSourceType(false);
//        mcSwmm.schemaTest("_david");
//        mcEpanet.selectSourceType(false);
//        mcEpanet.schemaTest("epanet");
		
	}
	
	
	private void getFrameParams (JInternalFrame frame, String prefix) throws PropertyVetoException{

        int x, y;
        boolean visible, selected, maximized;
        x = Integer.parseInt(prop.get(prefix + "_X", "0"));
        y = Integer.parseInt(prop.get(prefix + "_Y", "0"));
        visible = Boolean.parseBoolean(prop.get(prefix + "_VISIBLE", "false"));
        selected = Boolean.parseBoolean(prop.get(prefix + "_SELECTED", "false"));
        maximized = true;
        frame.setLocation(x, y);
        frame.setVisible(visible);
        frame.setSelected(selected);
        frame.setMaximum(maximized);
		
	}
	
	
	private void setFrameParams (JInternalFrame frame, String prefix) throws PropertyVetoException{

		prop.setProperty(prefix + "_X", frame.getX());
		prop.setProperty(prefix + "_Y", frame.getY());
		prop.setProperty(prefix + "_VISIBLE", frame.isVisible());
		prop.setProperty(prefix + "_SELECTED", frame.isSelected());
		prop.setProperty(prefix + "_MAXIMIZED", frame.isMaximum());
		MainDao.savePropertiesFile(); 
		
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
	
	
	private void setMainParams (String prefix) throws PropertyVetoException{

		boolean maximized = (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
		prop.setProperty(prefix + "_MAXIMIZED", maximized);		
		prop.setProperty(prefix + "_X", this.getX());
		prop.setProperty(prefix + "_Y", this.getY());
		prop.setProperty(prefix + "_WIDTH", this.getWidth());
		prop.setProperty(prefix + "_HEIGHT", this.getHeight());
		MainDao.savePropertiesFile(); 
		
	}	
	
	
	public void closeApp(){
	
        try {
			setFrameParams(swmmFrame, "SWMM");
	        setFrameParams(epanetFrame, "EPANET");
	        setFrameParams(hecRasFrame, "HECRAS");
	        setFrameParams(dbFrame, "DB");      
	        setFrameParams(configFrame, "CONFIG");	
	        setMainParams("MAIN");
	    	Utils.getLogger().info("Application closed");	        
		} catch (PropertyVetoException e) {
            Utils.logError(e.getMessage());			
		}
		
	}
	
	
	private void setupListeners(){
		
		mntmSwmm.addActionListener(this);
		mntmEpanet.addActionListener(this);
		mntmHecras.addActionListener(this);
		
		mntmProjectId.addActionListener(this);		
		mntmConduit.addActionListener(this);
		mntmMaterials.addActionListener(this);
		mntmPatterns.addActionListener(this);		
		mntmTimeseries.addActionListener(this);
		mntmCurves.addActionListener(this);

		mntmCatalog.addActionListener(this);		
		mntmManagement.addActionListener(this);
		
		mntmDatabase.addActionListener(this);
		mntmSoftware.addActionListener(this);
		mntmQgis.addActionListener(this);
		mntmGvsig.addActionListener(this);
		
		mntmWelcome.addActionListener(this);
		mntmVersion.addActionListener(this);
		mntmLicense.addActionListener(this);		
		mntmAgreements.addActionListener(this);		
		mntmUserManual.addActionListener(this);
		mntmReferenceGuide.addActionListener(this);		
		mntmWeb.addActionListener(this);
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		menuController.action(e.getActionCommand());
	}


	public void openSwmm() {
		manageFrames(swmmFrame);
	}	
	
	public void openEpanet() {
		manageFrames(epanetFrame);
	}	

	public void openHecras() {
		manageFrames(hecRasFrame);
	}	

	public void openDatabase() {
		manageFrames(dbFrame);
	}		

	public void openSoftware() {
		manageFrames(configFrame);
	}
	
	public void openProjectQGIS() {
		manageFrames(gisFrame);
		gisFrame.setGisExtension("qgs");
	}	
	
	public void openProjectGVSIG() {
		manageFrames(gisFrame);
		gisFrame.setGisExtension("gvp");
	}	
	
	public void enableCatalog(boolean enable) {
		mnCatalog.setEnabled(enable);
		mnAnalysis.setEnabled(enable);
	}
	
	public void enableProjectId(boolean enable) {
		mntmProjectId.setEnabled(enable);
	}
	
	public void enableConduit(boolean enable) {
		mntmConduit.setEnabled(enable);
	}

	public void enableCurves(boolean enable) {
		mntmCurves.setEnabled(enable);
	}

	public void enableMaterials(boolean enable) {
		mntmMaterials.setEnabled(enable);
	}

	public void enablePatterns(boolean enable) {
		mntmPatterns.setEnabled(enable);
	}
	
	public void enableTimeseries(boolean enable) {
		mntmTimeseries.setEnabled(enable);
	}
	
	
	public void enableResultCat(boolean enable) {
		mntmCatalog.setEnabled(enable);
	}
	
	public void enableResultSelection(boolean enable) {
		mntmManagement.setEnabled(enable);
	}	
	
	
    private void manageFrames(JInternalFrame frame) {
    	
        try {
            frame.setVisible(true);        	
            frame.setSelected(true);
            frame.setIcon(false);
            frame.setMaximum(true);
            frame.show();
        } catch (PropertyVetoException e) {
            Utils.logError(e);
        }
        
    }
}