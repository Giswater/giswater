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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.ProjectPreferencesController;
import org.giswater.gui.frame.ProjectPreferencesFrame;
import org.giswater.util.Utils;
import java.awt.event.KeyEvent;


public class ProjectPreferencesPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	
	private ProjectPreferencesController controller;
	private ProjectPreferencesFrame frame;
	
	private JRadioButton optEpaSwmm;
	private JRadioButton optEpanet;
	private JRadioButton optHecras;
	
	private JPanel panelDbf;
	private JTextArea txtInput;
	private JButton btnFolderShp;
	
	private JPanel panelDB;
	private JPanel panelDatabase;
	private JComboBox<String> cboDriver;
	private JTextField txtIP;
	private JTextField txtPort;
	private JTextField txtDatabase;
	private JTextField txtUser;
	private JPasswordField txtPassword;
	private JCheckBox chkRemember;
	private JRadioButton optDatabase;
	private JRadioButton optDbf;
	private JButton btnCopy;
	private JButton btnDelete;
	private JButton btnRename;
	private JButton btnCreate;
	private JComboBox<String> cboVersionSoftware;
	private JComboBox<String> cboSchema;
	private JButton btnTest;
	
	private JPanel panelManagement;
	private JLabel lblTitle;
	private JLabel lblAuthor;
	private JLabel lblDate;
	private JTextField txtAuthor;
	private JTextField txtDate;
	private JTextField txtDescription;
	private JScrollPane scrollPane_1;
	
	private JButton btnAccept;
	private JButton btnApply;
	private JButton btnClose;
	
	private static final Font FONT_PANEL_TITLE = new Font("Tahoma", Font.PLAIN, 11);
	private static final Font FONT_PANEL_TITLE2 = new Font("Tahoma", Font.PLAIN, 11);
	private static final Integer BUTTON_WIDTH = 72;
	private JLabel lblInfo;
	private JButton btnCreateGisProject;
	private ButtonGroup groupSoftware;

	
	public ProjectPreferencesPanel() {
		initConfig();
	}
	
	
	private void initConfig(){
		
		setLayout(new MigLayout("", "[8px:n][78.00px:n][75px:n:75px][65px:n:65px][][10px:n][154.00px:n][][]", "[][][61.00][][]"));
		
		JLabel lblWaterSoftware = new JLabel("Water software:");
		add(lblWaterSoftware, "cell 0 0 2 1,alignx right");
		
		optEpaSwmm = new JRadioButton("EPASWMM");
		optEpaSwmm.setActionCommand("changeSoftware"); 
		add(optEpaSwmm, "flowx,cell 2 0");
		
		optEpanet = new JRadioButton("EPANET");
		optEpanet.setActionCommand("changeSoftware"); 
		add(optEpanet, "cell 3 0");
		
		optHecras = new JRadioButton("HEC-RAS");
		optHecras.setActionCommand("changeSoftware"); 
		add(optHecras, "cell 4 0");
		
		groupSoftware = new ButtonGroup();
		groupSoftware.add(optEpaSwmm);	
		groupSoftware.add(optEpanet);
		groupSoftware.add(optHecras);

		JLabel label = new JLabel("Data storage:");
		add(label, "cell 1 1,alignx right");
		
		optDatabase = new JRadioButton(BUNDLE.getString("ProjectPreferencesPanel.optDatabase.text")); //$NON-NLS-1$
		optDatabase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		optDatabase.setActionCommand("selectSourceType");
		add(optDatabase, "flowx,cell 2 1");
		
		optDbf = new JRadioButton("DBF");
		optDbf.setFont(new Font("Tahoma", Font.PLAIN, 12));
		optDbf.setActionCommand("selectSourceType");
		add(optDbf, "cell 3 1");
		
		ButtonGroup groupStorage = new ButtonGroup();
		groupStorage.add(optDatabase);
		groupStorage.add(optDbf);
		
		panelDbf = new JPanel();
		panelDbf.setBorder(new TitledBorder(null, "DBF Storage", TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelDbf, "cell 0 2 9 1,grow");
		panelDbf.setLayout(new MigLayout("", "[75px:n][300.00px:n,grow][10px:n][][8px:n]", "[34px:n]"));
		
		JLabel lblFolderShp = new JLabel("Data folder:");
		panelDbf.add(lblFolderShp, "cell 0 0,alignx right");
		
		scrollPane_1 = new JScrollPane();
		panelDbf.add(scrollPane_1, "cell 1 0,grow");
		
		txtInput = new JTextArea();
		scrollPane_1.setViewportView(txtInput);
		txtInput.setText("");
		txtInput.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnFolderShp = new JButton();
		btnFolderShp.setMinimumSize(new Dimension(72, 9));
		panelDbf.add(btnFolderShp, "cell 3 0,growx");
		btnFolderShp.setText("...");
		btnFolderShp.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFolderShp.setActionCommand("chooseFolderShp");
		
		panelDB = new JPanel();
		panelDB.setBorder(new TitledBorder(null, "Database Storage", TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelDB, "cell 0 3 9 1,grow");
		panelDB.setLayout(new MigLayout("", "[]", "[][]"));
		
		panelDatabase = new JPanel();
		panelDB.add(panelDatabase, "cell 0 0,growx");
		panelDatabase.setBorder(new TitledBorder(null, "Connection Parameters", TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE2, null));
		panelDatabase.setLayout(new MigLayout("", "[65px:n][100px:n:100px][20px][grow]", "[][][][][][][]"));
		
		JLabel lblNewLabel = new JLabel(BUNDLE.getString("Database.lblNewLabel.text_2")); 
		panelDatabase.add(lblNewLabel, "cell 0 0,alignx right");

		cboDriver = new JComboBox<String>();
		cboDriver.setPreferredSize(new Dimension(24, 20));
		cboDriver.setMinimumSize(new Dimension(24, 20));
		cboDriver.setModel(new DefaultComboBoxModel<String>(new String[] {"PG-9.2 + PostGIS-2.0"}));
		panelDatabase.add(cboDriver, "cell 1 0 3 1,growx");

		JLabel lblIp = new JLabel(BUNDLE.getString("Database.lblIp.text")); 
		panelDatabase.add(lblIp, "cell 0 1,alignx right");

		txtIP = new JTextField();
		panelDatabase.add(txtIP, "cell 1 1 3 1,growx");
		txtIP.setColumns(10);

		JLabel lblPort = new JLabel(BUNDLE.getString("Database.lblPort.text")); 
		panelDatabase.add(lblPort, "cell 0 2,alignx right");

		txtPort = new JTextField();
		txtPort.setColumns(10);
		panelDatabase.add(txtPort, "cell 1 2 3 1,growx");

		JLabel lblDatabase = new JLabel(BUNDLE.getString("Database.lblDatabase.text")); 
		panelDatabase.add(lblDatabase, "cell 0 3,alignx right");

		txtDatabase = new JTextField();
		txtDatabase.setText("");
		txtDatabase.setColumns(10);
		panelDatabase.add(txtDatabase, "cell 1 3 3 1,growx");

		JLabel lblUser = new JLabel(BUNDLE.getString("Database.lblUser.text")); 
		panelDatabase.add(lblUser, "cell 0 4,alignx right");

		txtUser = new JTextField();
		txtUser.setText("postgres");
		txtUser.setColumns(10);
		panelDatabase.add(txtUser, "cell 1 4 3 1,growx");

		JLabel lblPassword = new JLabel(BUNDLE.getString("Database.lblPassword.text")); 
		panelDatabase.add(lblPassword, "cell 0 5,alignx right");

		txtPassword = new JPasswordField();
		txtPassword.setText("");
		panelDatabase.add(txtPassword, "cell 1 5 3 1,growx");
		
		chkRemember = new JCheckBox("Remember password");
		chkRemember.setSelected(true);
		panelDatabase.add(chkRemember, "flowx,cell 3 6,alignx right");
		
		btnTest = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnTest.text")); 
		btnTest.setMaximumSize(new Dimension(120, 23));
		btnTest.setMinimumSize(new Dimension(120, 23));
		btnTest.setActionCommand("testConnection");
		panelDatabase.add(btnTest, "cell 3 6,alignx right");
		
		panelManagement = new JPanel();
		panelDB.add(panelManagement, "cell 0 1");
		panelManagement.setBorder(new TitledBorder(null, "Project Data Management", TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE2, null));
		panelManagement.setLayout(new MigLayout("", "[65px:n][100px,grow][59.00px][][][][]", "[23px][][]"));
		
		JLabel lblProject = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblProject.text")); 
		panelManagement.add(lblProject, "cell 0 0,alignx right");
		
		cboSchema = new JComboBox<String>();
		panelManagement.add(cboSchema, "cell 1 0 2 1,growx,aligny center");
		cboSchema.setPreferredSize(new Dimension(24, 20));
		cboSchema.setMinimumSize(new Dimension(110, 20));
		cboSchema.setMaximumSize(new Dimension(999, 20));
		cboSchema.setEnabled(false);
		cboSchema.setActionCommand("schemaChanged");
		
		btnCreate = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnCreate.text")); 
		panelManagement.add(btnCreate, "cell 3 0,alignx right,aligny top");
		btnCreate.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnCreate.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnCreate.setEnabled(false);
		btnCreate.setActionCommand("createSchemaAssistant");
		
		btnDelete = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnDelete.text")); 
		btnDelete.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnDelete.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnDelete.setMaximumSize(new Dimension(BUTTON_WIDTH, 23));
		btnDelete.setEnabled(false);
		btnDelete.setActionCommand("deleteSchema");
		panelManagement.add(btnDelete, "cell 4 0,alignx right");
		
		btnRename = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnRename.text"));
		btnRename.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnRename.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnRename.setEnabled(false);
		btnRename.setActionCommand("renameSchema");
		panelManagement.add(btnRename, "cell 5 0,alignx right");
		
		btnCopy = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnCopy.text"));
		btnCopy.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnCopy.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnCopy.setEnabled(false);
		btnCopy.setActionCommand("copySchema"); 
		panelManagement.add(btnCopy, "cell 6 0,alignx right");
		
		lblTitle = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblTitle.text")); 
		panelManagement.add(lblTitle, "cell 0 1,alignx right");
		
		txtAuthor = new JTextField();
		panelManagement.add(txtAuthor, "cell 1 1 3 1,growx");
		txtAuthor.setColumns(10);
		
		lblAuthor = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblAuthor.text")); 
		panelManagement.add(lblAuthor, "cell 4 1,alignx right");
		
		txtDate = new JTextField();
		txtDate.setColumns(10);
		panelManagement.add(txtDate, "cell 5 1 2 1,growx");
		
		lblDate = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblDate.text")); 
		panelManagement.add(lblDate, "cell 0 2,alignx trailing");
		
		txtDescription = new JTextField();
		txtDescription.setColumns(10);
		panelManagement.add(txtDescription, "cell 1 2 6 1,growx");
		
		lblInfo = new JLabel(""); 
		add(lblInfo, "flowx,cell 1 4 2 1");
		
		btnApply = new JButton();
		btnApply.setMnemonic(KeyEvent.VK_P);
		btnApply.setText(BUNDLE.getString("ProjectPreferencesPanel.btnApply.text")); 
		btnApply.setMinimumSize(new Dimension(72, 23));
		btnApply.setActionCommand("applyPreferences");
		add(btnApply, "flowx,cell 6 4,alignx right");
		
		btnAccept = new JButton();
		btnAccept.setMnemonic(KeyEvent.VK_A);
		btnAccept.setText(BUNDLE.getString("ProjectPreferencesPanel.btnAccept.text")); 
		btnAccept.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnAccept.setActionCommand("acceptPreferences");
		add(btnAccept, "cell 6 4,alignx right");
		
		btnClose = new JButton();
		btnClose.setMnemonic(KeyEvent.VK_C);
		btnClose.setText("Close");
		btnClose.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnClose.setActionCommand("closePreferences");
		add(btnClose, "cell 7 4,growx");
			
		btnCreateGisProject = new JButton();
		btnCreateGisProject.setText(BUNDLE.getString("ProjectPreferencesPanel.btnCreateGisProject.text_1")); //$NON-NLS-1$
		btnCreateGisProject.setMinimumSize(new Dimension(80, 23));
		btnCreateGisProject.setActionCommand("createGisProject");
		add(btnCreateGisProject, "cell 1 4 2 1,alignx right");
		
		cboVersionSoftware = new JComboBox<String>();
		add(cboVersionSoftware, "cell 6 0 2 1,growx");
		
		setupListeners();
	    
	}

	
	// Setup component's listener
	private void setupListeners() {

		// Water software
		optEpaSwmm.addActionListener(this);
		optEpanet.addActionListener(this);
		optHecras.addActionListener(this);
		
		// Data Storage
		optDbf.addActionListener(this);
		optDatabase.addActionListener(this);
		
		// Panel DBF
		btnFolderShp.addActionListener(this);
		
		// Panel Database connection
		btnTest.addActionListener(this);
		btnApply.addActionListener(this);
		btnAccept.addActionListener(this);
		btnClose.addActionListener(this);
		
		// Panel Project management
		cboSchema.addActionListener(this);
		btnCreate.addActionListener(this);
		btnDelete.addActionListener(this);
		btnRename.addActionListener(this);
		btnCopy.addActionListener(this);
		
		// Gis Project
		btnCreateGisProject.addActionListener(this);

	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
	public ProjectPreferencesFrame getFrame(){
		return frame;
	}

	public void setFrame(ProjectPreferencesFrame frame){
		this.frame = frame;
	}

	public void setController(ProjectPreferencesController controller) {
		this.controller = controller;
	}

	public ProjectPreferencesController getController(){
		return controller;
	}
	
	public boolean getOptDatabaseSelected(){
		return optDatabase.isSelected();
	}
	
	public void setDatabaseSelected(boolean isSelected){
		optDatabase.setSelected(isSelected);
	}	
	
	public boolean getOptDbfSelected(){
		return optDbf.isSelected();
	}	

	public void setDbfSelected(boolean isSelected){
		optDbf.setSelected(isSelected);
	}	
	
	public void selectSourceType(){
		controller.selectSourceType();
	}
	
	public void enableDbfStorage(boolean enable) {
		Utils.setPanelEnabled(panelDbf, enable);
		scrollPane_1.setEnabled(enable);
		txtInput.setEnabled(enable);
	}	
	
	public void enableConnectionParameters(boolean enable){
		Utils.setPanelEnabled(panelDatabase, enable);
	}	
	
	public void enableProjectManagement(boolean enable){
		Utils.setPanelEnabled(panelManagement, enable);
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
		if (optEpanet.isSelected()){
			waterSoftware = "EPANET";
		} 
		else if (optEpaSwmm.isSelected()){
			waterSoftware = "EPASWMM";
		}
		else if (optHecras.isSelected()){
			waterSoftware = "HECRAS";
		}			
		return waterSoftware;
		
	}	
	
	public void setVersionSoftware(Vector<String> v) {
		ComboBoxModel<String> cbm = new DefaultComboBoxModel<String>(v);
		cboVersionSoftware.setModel(cbm);		
	}
	
	public String getVersionSoftware() {
		String elem = "";
		if (cboVersionSoftware.getSelectedIndex() != -1) {
			elem = cboVersionSoftware.getSelectedItem().toString();
		}
		return elem;
	}	
		

	// Panel DBF
	public void setFolderShp(String path) {
		txtInput.setText(path);
	}
	
	public String getFolderShp() {
		String folderShp = txtInput.getText().trim();
		return folderShp;
	}	

	
	// Postgis
	public void enableSchemaOptions(boolean enabled){
		btnDelete.setEnabled(enabled);
		btnRename.setEnabled(enabled);
		btnCopy.setEnabled(enabled);	
	}
	
	
	public boolean setSchemaModel(Vector<String> v) {
		
		ComboBoxModel<String> cbm = null;
		if (v != null){
			cbm = new DefaultComboBoxModel<String>(v);
			cboSchema.setModel(cbm);		
		} 
		else{
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

	@SuppressWarnings("deprecation")
	public String getPassword() {
		return txtPassword.getText();
	}

	public void setPassword(String path) {
		txtPassword.setText(path);
	}
	
	public Boolean getRemember() {
		return chkRemember.isSelected();
	}
	
	public void setRemember(Boolean isSelected){
		chkRemember.setSelected(isSelected);
	}
	
	public void setConnectionText(String text){
		btnTest.setText(text);
	}
	
	public void enableControlsText(boolean enabled) {
		txtInput.setEnabled(enabled);
		this.requestFocusInWindow();		
	}

	public void setInfo(String text) {
		lblInfo.setText(text);
		
	}
	
		
}