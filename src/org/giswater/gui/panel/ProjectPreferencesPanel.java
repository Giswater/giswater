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


public class ProjectPreferencesPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$
	private ProjectPreferencesController controller;
	
	private JTextField textField;
	private JLabel lblNewLabel;
	private JComboBox<String> cboDriver;
	private JLabel lblPort;
	private JLabel lblIp;
	private JTextField txtIP;
	private JTextField txtPort;
	private JLabel lblDatabase;
	private JTextField txtDatabase;
	private JLabel lblUser;
	private JTextField txtUser;
	private JLabel lblPassword;
	private JPasswordField txtPassword;
	private JCheckBox chkRemember;
	private JRadioButton optDatabase;
	private JRadioButton optDbf;
	private JButton btnCopy;
	private JButton btnDelete;
	private JButton btnRename;
	private JButton btnCreate;
	private JButton btnFolderShp;
	private JTextArea txtInput;
	private JLabel lblFolderShp;
	private JComboBox<String> cboSoftware;
	private JComboBox<String> cboSchema;
	private JButton btnTest;
	private JLabel lblTitle;
	private JLabel lblAuthor;
	private JLabel lblDate;
	private JTextField textField_1;
	private JTextField textField_2;
	private JPanel panelDbf;
	private JScrollPane scrollPane_1;
	private JButton btnCreateGisProject;
	private JPanel panelGis;
	private JPanel panelDB;
	
	private static final Font FONT_14 = new Font("Tahoma", Font.BOLD, 14);
	private static final Font FONT_12 = new Font("Tahoma", Font.BOLD, 12);
	private static final Integer BUTTON_WIDTH = 80;
	
	
	public ProjectPreferencesPanel() {
		
		setLayout(new MigLayout("", "[75px:n,grow][][154.00px:n,grow][]", "[10px][][][35.00][61.00][][59.00][]"));
		
		JLabel lblWaterSoftware = new JLabel("Water software:");
		add(lblWaterSoftware, "cell 0 1,alignx right");
		
		JRadioButton optSwmm = new JRadioButton("EPASWMM");
		optSwmm.setActionCommand(BUNDLE.getString("ProjectPreferencesPanel.optSwmm.actionCommand")); //$NON-NLS-1$
		add(optSwmm, "flowx,cell 1 1");
		
		JRadioButton optEpanet = new JRadioButton("EPANET");
		optEpanet.setActionCommand(BUNDLE.getString("ProjectPreferencesPanel.optEpanet.actionCommand")); //$NON-NLS-1$
		add(optEpanet, "cell 1 1");
		
		JRadioButton optHecras = new JRadioButton("HEC-RAS");
		optHecras.setActionCommand(BUNDLE.getString("ProjectPreferencesPanel.optHecras.actionCommand")); //$NON-NLS-1$
		add(optHecras, "cell 1 1");
		
		JLabel lblVersion = new JLabel("Version:");
		add(lblVersion, "cell 0 2,alignx trailing");
		
		cboSoftware = new JComboBox<String>();
		add(cboSoftware, "cell 1 2,growx");
		
		JLabel label = new JLabel("Data storage:");
		add(label, "cell 0 3,alignx right");
		
		optDbf = new JRadioButton("DBF");
		optDbf.setFont(new Font("Tahoma", Font.PLAIN, 12));
		optDbf.setActionCommand("selectSourceType");
		add(optDbf, "flowx,cell 1 3");
		
		optDatabase = new JRadioButton("Database");
		optDatabase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		optDatabase.setActionCommand("selectSourceType");
		add(optDatabase, "cell 1 3");
		
		panelDbf = new JPanel();
		panelDbf.setName("panelDatabase");
		panelDbf.setBorder(new TitledBorder(null, "DBF Storage", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		add(panelDbf, "cell 0 4 4 1,grow");
		panelDbf.setLayout(new MigLayout("", "[70px:n][100px:n,grow][10px:n][]", "[]"));
		
		lblFolderShp = new JLabel("Data folder:");
		panelDbf.add(lblFolderShp, "cell 0 0,alignx right");
		
		scrollPane_1 = new JScrollPane();
		panelDbf.add(scrollPane_1, "cell 1 0,grow");
		
		txtInput = new JTextArea();
		scrollPane_1.setViewportView(txtInput);
		txtInput.setText("");
		txtInput.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnFolderShp = new JButton();
		btnFolderShp.setMinimumSize(new Dimension(60, 9));
		panelDbf.add(btnFolderShp, "cell 3 0");
		btnFolderShp.setText("...");
		btnFolderShp.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFolderShp.setActionCommand("chooseFolderShp");
		
		panelDB = new JPanel();
		panelDB.setName("panelDatabase");
		panelDB.setBorder(new TitledBorder(null, "Database Storage", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		add(panelDB, "cell 0 5 4 1,grow");
		panelDB.setLayout(new MigLayout("", "[]", "[][][]"));
		
		JPanel panelDatabase = new JPanel();
		panelDB.add(panelDatabase, "cell 0 0");
		panelDatabase.setName("panelDatabase");
		panelDatabase.setBorder(new TitledBorder(null, "Connection Parameters", TitledBorder.LEADING, TitledBorder.TOP, FONT_12, null));
		panelDatabase.setLayout(new MigLayout("", "[70px:n][100px:n:100px][20px][]", "[][][][][][][]"));
		
		lblNewLabel = new JLabel(BUNDLE.getString("Database.lblNewLabel.text_2")); //$NON-NLS-1$
		panelDatabase.add(lblNewLabel, "cell 0 0,alignx right");

		cboDriver = new JComboBox<String>();
		cboDriver.setPreferredSize(new Dimension(24, 20));
		cboDriver.setMinimumSize(new Dimension(24, 20));
		cboDriver.setModel(new DefaultComboBoxModel<String>(new String[] {"PG-9.2 + PostGIS-2.0"}));
		panelDatabase.add(cboDriver, "cell 1 0 3 1,growx");

		lblIp = new JLabel(BUNDLE.getString("Database.lblIp.text")); //$NON-NLS-1$
		panelDatabase.add(lblIp, "cell 0 1,alignx right");

		txtIP = new JTextField();
		panelDatabase.add(txtIP, "cell 1 1 3 1,growx");
		txtIP.setColumns(10);

		lblPort = new JLabel(BUNDLE.getString("Database.lblPort.text")); //$NON-NLS-1$
		panelDatabase.add(lblPort, "cell 0 2,alignx right");

		txtPort = new JTextField();
		txtPort.setColumns(10);
		panelDatabase.add(txtPort, "cell 1 2 3 1,growx");

		lblDatabase = new JLabel(BUNDLE.getString("Database.lblDatabase.text")); //$NON-NLS-1$
		panelDatabase.add(lblDatabase, "cell 0 3,alignx right");

		txtDatabase = new JTextField();
		txtDatabase.setText("");
		txtDatabase.setColumns(10);
		panelDatabase.add(txtDatabase, "cell 1 3 3 1,growx");

		lblUser = new JLabel(BUNDLE.getString("Database.lblUser.text")); //$NON-NLS-1$
		panelDatabase.add(lblUser, "cell 0 4,alignx right");

		txtUser = new JTextField();
		txtUser.setText("postgres");
		txtUser.setColumns(10);
		panelDatabase.add(txtUser, "cell 1 4 3 1,growx");

		lblPassword = new JLabel(BUNDLE.getString("Database.lblPassword.text")); //$NON-NLS-1$
		panelDatabase.add(lblPassword, "cell 0 5,alignx right");

		txtPassword = new JPasswordField();
		txtPassword.setText("");
		panelDatabase.add(txtPassword, "cell 1 5 3 1,growx");
		
		chkRemember = new JCheckBox("Remember password");
		chkRemember.setSelected(true);
		panelDatabase.add(chkRemember, "cell 1 6");
		
		btnTest = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnTest.text")); //$NON-NLS-1$
		btnTest.setMaximumSize(new Dimension(120, 23));
		btnTest.setMinimumSize(new Dimension(120, 23));
		btnTest.setActionCommand("testConnection");
		panelDatabase.add(btnTest, "flowx,cell 3 6");
		
		JPanel panelManagement = new JPanel();
		panelDB.add(panelManagement, "cell 0 1");
		panelManagement.setName("panelDatabase");
		panelManagement.setBorder(new TitledBorder(null, "Project Management", TitledBorder.LEADING, TitledBorder.TOP, FONT_12, null));
		panelManagement.setLayout(new MigLayout("", "[70px:n][100px,grow][30px][][][][]", "[23px][][]"));
		
		JLabel lblProject = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblProject.text")); //$NON-NLS-1$
		panelManagement.add(lblProject, "cell 0 0,alignx right");
		
		cboSchema = new JComboBox<String>();
		panelManagement.add(cboSchema, "cell 1 0 2 1,growx,aligny center");
		cboSchema.setPreferredSize(new Dimension(24, 20));
		cboSchema.setMinimumSize(new Dimension(110, 20));
		cboSchema.setMaximumSize(new Dimension(999, 20));
		cboSchema.setEnabled(false);
		cboSchema.setActionCommand("schemaChanged");
		
		btnCreate = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnCreate.text")); //$NON-NLS-1$
		panelManagement.add(btnCreate, "cell 3 0,alignx right,aligny top");
		btnCreate.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnCreate.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnCreate.setEnabled(false);
		btnCreate.setActionCommand("createSchema");
		
		btnDelete = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnDelete.text")); //$NON-NLS-1$
		btnDelete.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnDelete.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnDelete.setMaximumSize(new Dimension(BUTTON_WIDTH, 23));
		btnDelete.setEnabled(false);
		btnDelete.setActionCommand("deleteSchema");
		panelManagement.add(btnDelete, "cell 4 0,alignx right");
		
		btnRename = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnRename.text")); //$NON-NLS-1$
		btnRename.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnRename.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnRename.setEnabled(false);
		btnRename.setActionCommand("createSchema");
		panelManagement.add(btnRename, "cell 5 0,alignx right");
		
		btnCopy = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnCopy.text")); //$NON-NLS-1$
		btnCopy.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnCopy.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnCopy.setEnabled(false);
		btnCopy.setActionCommand("createSchema");
		panelManagement.add(btnCopy, "cell 6 0,alignx right");
		
		lblTitle = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblTitle.text")); //$NON-NLS-1$
		panelManagement.add(lblTitle, "cell 0 1,alignx right");
		
		textField = new JTextField();
		panelManagement.add(textField, "cell 1 1 3 1,growx");
		textField.setColumns(10);
		
		lblAuthor = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblAuthor.text")); //$NON-NLS-1$
		panelManagement.add(lblAuthor, "cell 4 1,alignx right");
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		panelManagement.add(textField_1, "cell 5 1 2 1,growx");
		
		lblDate = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblDate.text")); //$NON-NLS-1$
		panelManagement.add(lblDate, "cell 0 2,alignx trailing");
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		panelManagement.add(textField_2, "cell 1 2 6 1,growx");
		
		panelGis = new JPanel();
		panelGis.setName("panelDatabase");
		panelGis.setBorder(new TitledBorder(null, "GIS Project", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		add(panelGis, "cell 0 6 4 1,grow");
		panelGis.setLayout(new MigLayout("", "[]", "[]"));
		
		btnCreateGisProject = new JButton();
		panelGis.add(btnCreateGisProject, "cell 0 0");
		btnCreateGisProject.setText(BUNDLE.getString("ProjectPreferencesPanel.btnCreateGisProject.text")); //$NON-NLS-1$
		btnCreateGisProject.setMinimumSize(new Dimension(80, 23));
		btnCreateGisProject.setActionCommand("closePanel");
		
		JButton btnClose = new JButton();
		btnClose.setText("Close");
		btnClose.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnClose.setActionCommand("closePanel");
		add(btnClose, "cell 3 7");
		
		setupListeners();

		
		//Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(optDbf);
	    group.add(optDatabase);	
	    
	}

	
	// Setup component's listener
	private void setupListeners() {

		// Panel Database connection
//		btnTest.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				databaseController.action(e.getActionCommand());
//			}
//		});
//		
//		btnClose.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				getFrame().setVisible(false);
//			}
//		});		

	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		//controller.action(e.getActionCommand());
	}


	public void setControl(ProjectPreferencesController controller) {
		this.controller = controller;
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
		controller.selectSourceType(false);
	}
	
	public void enableControlsDbf(boolean enable) {
		boolean dbfSelected = optDbf.isSelected();
		lblFolderShp.setEnabled(dbfSelected);
		txtInput.setEnabled(dbfSelected);
		btnFolderShp.setEnabled(dbfSelected);
	}	
	
	public void enableControlsDatabase(boolean enable){
		btnCreate.setEnabled(enable);
		btnDelete.setEnabled(enable);
		btnRename.setEnabled(enable);
//		btnOptions.setEnabled(enable);
//		btnDesign.setEnabled(enable);
//		btnSectorSelection.setEnabled(enable);
//		btnReport.setEnabled(enable);
//		lblSchema.setEnabled(enable);
//		cboSchema.setEnabled(enable);	
	}	
	
	public void enableControlsText(boolean enable) {
		txtInput.setEnabled(enable);
//		txtProject.setEnabled(enable);
//		txtFileInp.setEnabled(enable);
//		txtFileRpt.setEnabled(enable);
		this.requestFocusInWindow();		
	}	
	
	
	public void setSoftware(Vector<String> v) {
		ComboBoxModel<String> cbm = new DefaultComboBoxModel<String>(v);
		cboSoftware.setModel(cbm);		
	}
	
	public String getSoftware() {
		String elem = "";
		if (cboSoftware.getSelectedIndex() != -1) {
			elem = cboSoftware.getSelectedItem().toString();
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
	public void setSchemaModel(Vector<String> v) {
		
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
//		btnDelete.setEnabled(enabled);
//		btnRename.setEnabled(enabled);
//		btnSectorSelection.setEnabled(enabled);
//		btnOptions.setEnabled(enabled);
//		btnDesign.setEnabled(enabled);
//		btnReport.setEnabled(enabled);
		
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
	
	public void enableControls(boolean enabled) {
		cboDriver.setEnabled(enabled);
		txtIP.setEnabled(enabled);
		txtPort.setEnabled(enabled);
		txtDatabase.setEnabled(enabled);
		txtUser.setEnabled(enabled);
		txtPassword.setEnabled(enabled);
		chkRemember.setEnabled(enabled);
	}
	
		
}