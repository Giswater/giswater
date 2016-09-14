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
package org.giswater.gui.panel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.ProjectPreferencesController;
import org.giswater.gui.frame.ProjectPreferencesFrame;
import org.giswater.util.MaxLengthTextDocument;
import org.giswater.util.Utils;


public class ProjectPreferencesPanel extends JPanel implements ActionListener {
	
	private ProjectPreferencesController controller;
	private ProjectPreferencesFrame frame;
	
	private JPanel panelWaterProject;
	private ButtonGroup groupSoftware;
	private JRadioButton optEpaSwmm;
	private JRadioButton optEpanet;
	private JRadioButton optHecras;
	
	private JPanel panelDatabase;
	private JComboBox<String> cboDriver;
	private JTextField txtIP;
	private JTextField txtPort;
	private JTextField txtDatabase;
	private JTextField txtUser;
	private JPasswordField txtPassword;
	private JCheckBox chkRemember;
	private JCheckBox chkSsl;
	private JButton btnCopy;
	private JButton btnDelete;
	private JButton btnRename;
	private JButton btnCreate;
	private JComboBox<String> cboVersionSoftware;
	private JComboBox<String> cboSchema;
	private JButton btnTest;
	
	private JPanel panelManagement;
	private JTextField txtAuthor;
	private JTextField txtDate;
	private JTextField txtDescription;
	
	private JLabel lblInfo;
	private JButton btnGoToEpa;
	private JButton btnCreateGisProject;
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	private static final Font FONT_PANEL_TITLE2 = new Font("Tahoma", Font.PLAIN, 11);
	private static final Integer BUTTON_WIDTH = 72;
	private JButton btnCopyFunctions;

	
	public ProjectPreferencesPanel() {
		initConfig();
	}
	
	
	private void initConfig() {
		
		setLayout(new MigLayout("", "[90px:n:90px][60px:n][::250px,grow][::88px]", "[60.00][::10px][124.00][::10px][][]"));
		
		panelWaterProject = new JPanel();
		panelWaterProject.setBorder(new TitledBorder(null, BUNDLE.getString("ProjectPreferencesPanel.panelWaterProject.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE2, null));
		add(panelWaterProject, "cell 0 0 4 1,grow");
		panelWaterProject.setLayout(new MigLayout("", "[::5px][][2px:n][][2px:n][][2px:n][127px:n,grow]", "[]"));
		
		optEpaSwmm = new JRadioButton(BUNDLE.getString("ProjectPreferencesPanel.optEpaSwmm.text")); //$NON-NLS-1$
		panelWaterProject.add(optEpaSwmm, "cell 1 0");
		optEpaSwmm.setActionCommand("changeSoftware");
		
		optEpanet = new JRadioButton(BUNDLE.getString("ProjectPreferencesPanel.optEpanet.text")); //$NON-NLS-1$
		panelWaterProject.add(optEpanet, "cell 3 0");
		optEpanet.setActionCommand("changeSoftware");
		
		optHecras = new JRadioButton(BUNDLE.getString("ProjectPreferencesPanel.optHecras.text")); //$NON-NLS-1$
		panelWaterProject.add(optHecras, "cell 5 0");
		optHecras.setActionCommand("changeSoftware");
		
		groupSoftware = new ButtonGroup();
		groupSoftware.add(optEpaSwmm);	
		groupSoftware.add(optEpanet);
		groupSoftware.add(optHecras);
		
		cboVersionSoftware = new JComboBox<String>();
		panelWaterProject.add(cboVersionSoftware, "cell 7 0,growx");
		
		panelDatabase = new JPanel();
		add(panelDatabase, "cell 0 2 4 1,growx");
		panelDatabase.setBorder(new TitledBorder(null, BUNDLE.getString("ProjectPreferencesPanel.panelDatabase.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE2, null)); //$NON-NLS-1$
		panelDatabase.setLayout(new MigLayout("", "[80px:n:80px][80px:n][grow]", "[][][][][][][]"));
		
		JLabel lblNewLabel = new JLabel(BUNDLE.getString("Database.lblNewLabel.text_2")); 
		panelDatabase.add(lblNewLabel, "cell 0 0,alignx right");
		
		cboDriver = new JComboBox<String>();
		cboDriver.setPreferredSize(new Dimension(24, 20));
		cboDriver.setMinimumSize(new Dimension(24, 20));
		cboDriver.setModel(new DefaultComboBoxModel<String>(new String[] {"PG-9.2 + PostGIS-2.0"}));
		panelDatabase.add(cboDriver, "cell 1 0 2 1,growx");
		
		JLabel lblIp = new JLabel(BUNDLE.getString("Database.lblIp.text")); 
		panelDatabase.add(lblIp, "cell 0 1,alignx right");
		
		txtIP = new JTextField();
		panelDatabase.add(txtIP, "cell 1 1 2 1,growx");
		txtIP.setColumns(10);
		
		JLabel lblPort = new JLabel(BUNDLE.getString("Database.lblPort.text")); 
		panelDatabase.add(lblPort, "cell 0 2,alignx right");
		
		txtPort = new JTextField();
		txtPort.setColumns(10);
		panelDatabase.add(txtPort, "cell 1 2 2 1,growx");
		
		JLabel lblDatabase = new JLabel(BUNDLE.getString("Database.lblDatabase.text")); 
		panelDatabase.add(lblDatabase, "cell 0 3,alignx right");
		
		txtDatabase = new JTextField();
		txtDatabase.setText("");
		txtDatabase.setColumns(10);
		panelDatabase.add(txtDatabase, "cell 1 3 2 1,growx");
		
		JLabel lblUser = new JLabel(BUNDLE.getString("Database.lblUser.text")); 
		panelDatabase.add(lblUser, "cell 0 4,alignx right");
		
		txtUser = new JTextField();
		txtUser.setText("postgres");
		txtUser.setColumns(10);
		panelDatabase.add(txtUser, "cell 1 4 2 1,growx");
		
		JLabel lblPassword = new JLabel(BUNDLE.getString("Database.lblPassword.text")); 
		panelDatabase.add(lblPassword, "cell 0 5,alignx right");
		
		txtPassword = new JPasswordField();
		txtPassword.setText("");
		panelDatabase.add(txtPassword, "cell 1 5 2 1,growx");
		
		chkRemember = new JCheckBox(BUNDLE.getString("ProjectPreferencesPanel.chkRemember.text")); //$NON-NLS-1$
		chkRemember.setSelected(true);
		panelDatabase.add(chkRemember, "flowx,cell 2 6,alignx right");
		
		chkSsl = new JCheckBox(BUNDLE.getString("ProjectPreferencesPanel.chkSsl.text")); //$NON-NLS-1$
		chkSsl.setSelected(true);
		panelDatabase.add(chkSsl, "cell 2 6,alignx right");
		
		btnTest = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnTest.text")); 
		btnTest.setMaximumSize(new Dimension(120, 23));
		btnTest.setMinimumSize(new Dimension(120, 23));
		btnTest.setActionCommand("testConnection");
		panelDatabase.add(btnTest, "cell 2 6,alignx right");
		
		panelManagement = new JPanel();
		add(panelManagement, "cell 0 4 4 1,growx");
		panelManagement.setBorder(new TitledBorder(null, BUNDLE.getString("ProjectPreferencesPanel.panelManagement.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE2, null)); //$NON-NLS-1$
		panelManagement.setLayout(new MigLayout("", "[5px:n][60px:n][30px:n][100px:n:100px][30px:n][10px:n][40px:n][72px:n:72px][::15px][]", "[23px][23px][]"));
		
		cboSchema = new JComboBox<String>();
		panelManagement.add(cboSchema, "cell 1 0 4 1,growx,aligny center");
		cboSchema.setPreferredSize(new Dimension(24, 20));
		cboSchema.setMinimumSize(new Dimension(110, 20));
		cboSchema.setMaximumSize(new Dimension(999, 20));
		cboSchema.setEnabled(false);
		cboSchema.setActionCommand("schemaChanged");
		
		txtDescription = new JTextField();
		txtDescription.setColumns(30);
		txtDescription.setEnabled(false);
		panelManagement.add(txtDescription, "cell 6 0 4 1,growx");
		
		btnCreate = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnCreate.text")); 
		btnCreate.setMaximumSize(new Dimension(999, 23));
		panelManagement.add(btnCreate, "cell 1 1 2 1,alignx left,aligny top");
		btnCreate.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnCreate.setMinimumSize(new Dimension(100, 23));
		btnCreate.setEnabled(false);
		btnCreate.setActionCommand("createSchemaAssistant");
		
		btnDelete = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnDelete.text")); 
		btnDelete.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnDelete.setMinimumSize(new Dimension(100, 23));
		btnDelete.setMaximumSize(new Dimension(999, 23));
		btnDelete.setEnabled(false);
		btnDelete.setActionCommand("deleteSchema");
		panelManagement.add(btnDelete, "cell 3 1 2 1,alignx right");
		
		btnRename = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnRename.text"));
		btnRename.setMaximumSize(new Dimension(999, 23));
		btnRename.setMinimumSize(new Dimension(100, 23));
		btnRename.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnRename.setEnabled(false);
		btnRename.setActionCommand("renameSchema");
		panelManagement.add(btnRename, "cell 6 1 2 1,alignx left");
		
		btnCopy = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnCopy.text"));
		btnCopy.setMinimumSize(new Dimension(100, 23));
		btnCopy.setMaximumSize(new Dimension(120, 23));
		btnCopy.setEnabled(false);
		btnCopy.setActionCommand("copySchema"); 
		panelManagement.add(btnCopy, "cell 8 1 2 1,alignx right");
		
		JLabel lblTitle = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblTitle.text")); 
		panelManagement.add(lblTitle, "cell 1 2,alignx left");
		
		txtAuthor = new JTextField();
		txtAuthor.setEnabled(false);
		panelManagement.add(txtAuthor, "cell 2 2 3 1,growx");
		txtAuthor.setDocument(new MaxLengthTextDocument(50));
		
		JLabel lblAuthor = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblAuthor.text")); 
		panelManagement.add(lblAuthor, "cell 6 2,alignx left");
		
		txtDate = new JTextField();
		txtDate.setColumns(25);
		txtDate.setEnabled(false);
		txtDate.setDocument(new MaxLengthTextDocument(12));
		panelManagement.add(txtDate, "cell 7 2 3 1,alignx center");
		
		lblInfo = new JLabel(""); 
		add(lblInfo, "flowx,cell 0 5 2 1");
		
		btnCreateGisProject = new JButton("Create Gis Project");
		btnCreateGisProject.setPreferredSize(new Dimension(121, 23));
		btnCreateGisProject.setActionCommand("createGisProject");
		add(btnCreateGisProject, "cell 0 5 2 1");
		
		btnCopyFunctions = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnCopyFunctions.text")); //$NON-NLS-1$
		btnCopyFunctions.setVisible(false);
		btnCopyFunctions.setPreferredSize(new Dimension(100, 23));
		btnCopyFunctions.setActionCommand(BUNDLE.getString("ProjectPreferencesPanel.btnCopyFunctions.actionCommand")); //$NON-NLS-1$
		add(btnCopyFunctions, "cell 2 5");
		
		btnGoToEpa = new JButton();
		btnGoToEpa.setText(BUNDLE.getString("ProjectPreferencesPanel.btnEpa.text")); //$NON-NLS-1$
		btnGoToEpa.setMnemonic(KeyEvent.VK_A);
		btnGoToEpa.setMinimumSize(new Dimension(72, 23));
		btnGoToEpa.setActionCommand("openEpaSoft");
		add(btnGoToEpa, "cell 3 5,growx");
		
		setupListeners();
	    
	}

	
	// Setup component's listener
	private void setupListeners() {

		// Water software
		optEpaSwmm.addActionListener(this);
		optEpanet.addActionListener(this);
		optHecras.addActionListener(this);
		
		// Panel Database connection
		btnTest.addActionListener(this);
		
		// Panel Project management
		cboSchema.addActionListener(this);
		btnCreate.addActionListener(this);
		btnDelete.addActionListener(this);
		btnRename.addActionListener(this);
		btnCopy.addActionListener(this);
		btnCreateGisProject.addActionListener(this);
		btnGoToEpa.addActionListener(this);
		
		btnCopyFunctions.addActionListener(this);		

	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
	public ProjectPreferencesFrame getFrame() {
		return frame;
	}

	public void setFrame(ProjectPreferencesFrame frame) {
		this.frame = frame;
	}

	public void setController(ProjectPreferencesController controller) {
		this.controller = controller;
	}

	public ProjectPreferencesController getController() {
		return controller;
	}
	
	public String getStorage() {
		String storage = "DATABASE";
		return storage;
	}
	
	public void selectSourceType(boolean loadVersionSoftwareModel) {
		controller.selectSourceType(loadVersionSoftwareModel);
	}
	
	public void enableConnectionParameters(boolean enable) {
		Utils.setPanelEnabled(panelDatabase, enable);
	}	
	
	public void enableProjectManagement(boolean enable) {
		Utils.setPanelEnabled(panelManagement, enable);
		disableProjectDataInfo();
	}	
	
	public void disableProjectDataInfo() {
		txtDescription.setEnabled(false);
		txtAuthor.setEnabled(false);
		txtDate.setEnabled(false);
	}	
	
	
	// Water software
	public void setWaterSoftware(String waterSoftware) {
		
		groupSoftware.clearSelection();
		if (waterSoftware.equals("EPANET")) {
			optEpanet.setSelected(true);
		}
		else if (waterSoftware.equals("EPASWMM")) {
			optEpaSwmm.setSelected(true);
		}	
		else if (waterSoftware.equals("HECRAS")) {
			optHecras.setSelected(true);
		}
		controller.setWaterSoftware(waterSoftware);
		
	}	
	
	public String getWaterSoftware() {
		
		String waterSoftware = "";
		if (optEpanet.isSelected()) {
			waterSoftware = "EPANET";
		} 
		else if (optEpaSwmm.isSelected()) {
			waterSoftware = "EPASWMM";
		}
		else if (optHecras.isSelected()) {
			waterSoftware = "HECRAS";
		}			
		return waterSoftware;
		
	}	
	
	public void setVersionSoftwareModel(Vector<String> v) {
		ComboBoxModel<String> cbm = new DefaultComboBoxModel<String>(v);
		cboVersionSoftware.setModel(cbm);		
	}
	
	public void setVersionSoftware(String version) {
		cboVersionSoftware.setSelectedItem(version);
	}	
	
	public String getVersionSoftware() {
		String elem = "";
		if (cboVersionSoftware.getSelectedIndex() != -1) {
			elem = cboVersionSoftware.getSelectedItem().toString();
		}
		return elem;
	}	
	

	
	// Postgis
	public void enableSchemaOptions(boolean enabled){
		btnDelete.setEnabled(enabled);
		btnRename.setEnabled(enabled);
		btnCopy.setEnabled(enabled);	
	}
	
	
	public boolean setSchemaModel(Vector<String> v) {
		
		ComboBoxModel<String> cbm = null;
		if (v != null) {
			cbm = new DefaultComboBoxModel<String>(v);
			cboSchema.setModel(cbm);		
		} 
		else {
			DefaultComboBoxModel<String> theModel = (DefaultComboBoxModel<String>) cboSchema.getModel();
			theModel.removeAllElements();
		}
		boolean enabled = (v != null && v.size() > 0);
		return enabled;
		
	}

	public void setSelectedSchema(String schemaName) {
		cboSchema.setSelectedItem(schemaName);
	}	

	public String getSelectedSchema() {
		String elem = "";
		if (cboSchema.getSelectedIndex() != -1) {
			elem = cboSchema.getSelectedItem().toString();
		}
		return elem;
	}	
	
	
	// Database parameters
	public JDialog getDialog() {
		return new JDialog();
	}

	public Integer getDriver() {
		return cboDriver.getSelectedIndex();
	}
	
	public String getHost() {
		return txtIP.getText().trim();
	}

	public void setHost(String text) {
		txtIP.setText(text);
	}

	public String getPort() {
		return txtPort.getText().trim();
	}

	public void setPort(String text) {
		txtPort.setText(text);
	}

	public String getDatabase() {
		return txtDatabase.getText().trim();
	}

	public void setDatabase(String text) {
		txtDatabase.setText(text);
	}

	public String getUser() {
		return txtUser.getText().trim();
	}

	public void setUser(String text) {
		txtUser.setText(text);
	}

	public String getPassword() {
		return txtPassword.getText();
	}

	public void setPassword(String path) {
		txtPassword.setText(path);
	}
	
	public Boolean isRememberSelected() {
		return chkRemember.isSelected();
	}
	
	public void selectRemember(Boolean isSelected) {
		chkRemember.setSelected(isSelected);
	}
	
	public Boolean isUseSslSelected() {
		return chkSsl.isSelected();
	}
	
	public void selectUseSsl(Boolean isSelected) {
		chkSsl.setSelected(isSelected);
	}
	
	public void setConnectionText(String text) {
		btnTest.setText(text);
	}
	
	public void enableControlsText(boolean enabled) {
//		txtInput.setEnabled(enabled);
		this.requestFocusInWindow();		
	}

	public void setInfo(String text) {
		lblInfo.setText(text);
	}

	public void enableRename(boolean enabled) {
		btnRename.setEnabled(enabled);
	}
	
	public void enableCopy(boolean enabled) {
		btnCopy.setEnabled(enabled);
	}
	
	public void enableAccept(boolean enabled) {
		btnGoToEpa.setEnabled(enabled);
	}	
	
	public void updateProjectData(String title, String author, String date) {
		txtDescription.setText(title);
		txtAuthor.setText(author);
		txtDate.setText(date);
		disableProjectDataInfo();
	}

	public void enableDbControls(boolean enabled) {
		cboDriver.setEnabled(enabled);
		txtIP.setEnabled(enabled);
		txtPort.setEnabled(enabled);
		txtDatabase.setEnabled(enabled);
		txtUser.setEnabled(enabled);
		txtPassword.setEnabled(enabled);
		chkRemember.setEnabled(enabled);
		chkSsl.setEnabled(enabled);
	}
	
		
}