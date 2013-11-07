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
package org.giswater.gui;

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
import org.giswater.gui.frame.ConfigFrame;
import org.giswater.gui.frame.DatabaseFrame;
import org.giswater.gui.frame.EpaFrame;
import org.giswater.gui.frame.HecRasFrame;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class MainFrame extends JFrame implements ActionListener{
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private static final long serialVersionUID = -6630818426483107558L;
	private MenuController menuController;
    private JDesktopPane desktopPane;
    
	private JMenuItem mntmVersion;
	private JMenuItem mntmAgreements;
	private JMenuItem mntmLicense;
	private JMenuItem mntmHelp;
	private JMenuItem mntmWelcome;
	
	private JMenuItem mntmSwmm;
	private JMenuItem mntmEpanet;
	private JMenuItem mntmHecras;

	private JMenu mnConfiguration;
	private JMenuItem mntmSoftware;
	private JMenuItem mntmDatabase;

	public EpaFrame swmmFrame;
	public EpaFrame epanetFrame;
	public HecRasFrame hecRasFrame;
	
	public DatabaseFrame dbFrame;
	public ConfigFrame configFrame;
	
	private PropertiesMap prop;
	

	public MainFrame(boolean isConnected) {
		initConfig();
		try {
			initFrames();
			hecRasFrame.getPanel().enableButtons(isConnected);
		} catch (PropertyVetoException e) {
            Utils.getLogger().warning(e.getMessage());
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
		mntmVersion.setActionCommand("showAuthor");
		mnAbout.add(mntmVersion);
		
		mntmLicense = new JMenuItem(BUNDLE.getString("MainFrame.mntmLicense.text")); //$NON-NLS-1$
		mntmLicense.setActionCommand("showLicense");
		mnAbout.add(mntmLicense);
		
		mntmAgreements = new JMenuItem(BUNDLE.getString("MainFrame.mntmAgreements.text")); //$NON-NLS-1$
		mntmAgreements.setActionCommand("showAgreements");
		mnAbout.add(mntmAgreements);
		
		mntmHelp = new JMenuItem(BUNDLE.getString("MainFrame.mntmHelp.text")); //$NON-NLS-1$
		mnAbout.add(mntmHelp);
		mntmHelp.setActionCommand("openHelp");
		
		desktopPane = new JDesktopPane();
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
	
	
	private void initFrames() throws PropertyVetoException{

        // Create and Add frames to main Panel
        swmmFrame = new EpaFrame();
        epanetFrame = new EpaFrame();
        hecRasFrame = new HecRasFrame();
        dbFrame = new DatabaseFrame(this);
        configFrame = new ConfigFrame();
        
        desktopPane.add(swmmFrame);
        desktopPane.add(epanetFrame);
        desktopPane.add(hecRasFrame);     
        desktopPane.add(dbFrame);        
        desktopPane.add(configFrame);            
        
        // Set specific configuration
		swmmFrame.setTitle("EPASWMM");
		swmmFrame.getPanel().setDesignButton("Raingage", "showRaingage");
		swmmFrame.getPanel().setOptionsButton("Options", "showOptions");
		swmmFrame.getPanel().setReportButton(false);
		epanetFrame.setTitle("EPANET");
		epanetFrame.getPanel().setDesignButton("Times values", "showTimesValues");
		epanetFrame.getPanel().setOptionsButton("Options", "showOptionsEpanet");
		epanetFrame.getPanel().setReportButton(true);

        // Get info from properties
		getMainParams("MAIN");
        getFrameParams(swmmFrame, "SWMM");
        getFrameParams(epanetFrame, "EPANET");
        getFrameParams(hecRasFrame, "HECRAS");
        getFrameParams(dbFrame, "DB");      
        getFrameParams(configFrame, "CONFIG");
       
        // Define one controller per panel
		new MainController(swmmFrame.getPanel(), this, "EPASWMM");
		new MainController(epanetFrame.getPanel(), this, "EPANET");
		new HecRasController(hecRasFrame.getPanel(), this);
		new DatabaseController(dbFrame.getPanel(), this);
		new ConfigController(configFrame.getPanel());		
		
	}
	
	
	private void getFrameParams (JInternalFrame frame, String prefix) throws PropertyVetoException{

        int x, y;
        boolean visible, selected, maximized;
        x = Integer.parseInt(prop.get(prefix + "_X", "0"));
        y = Integer.parseInt(prop.get(prefix + "_Y", "0"));
        visible = Boolean.parseBoolean(prop.get(prefix + "_VISIBLE", "false"));
        selected = Boolean.parseBoolean(prop.get(prefix + "_SELECTED", "false"));
        maximized = Boolean.parseBoolean(prop.get(prefix + "_MAXIMIZED", "false"));
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
            Utils.getLogger().warning(e.getMessage());			
		}
		
	}
	
	
	private void setupListeners(){
		
		mntmSwmm.addActionListener(this);
		mntmEpanet.addActionListener(this);
		mntmHecras.addActionListener(this);
		
		mntmDatabase.addActionListener(this);
		mntmSoftware.addActionListener(this);		
		
		mntmWelcome.addActionListener(this);
		mntmHelp.addActionListener(this);
		mntmSoftware.addActionListener(this);
		mntmVersion.addActionListener(this);
		mntmLicense.addActionListener(this);		
		mntmAgreements.addActionListener(this);		
		
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
	
	
    private void manageFrames(JInternalFrame frame) {
    	
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Utils.getLogger().warning(ex.getMessage());
        }
        
    }
    
    
}