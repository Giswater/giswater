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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.ProjectPreferencesController;
import org.giswater.dao.MainDao;
import org.giswater.model.TableModelSrid;
import org.giswater.util.Utils;


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
	
	private static final Font FONT_14 = new Font("Tahoma", Font.BOLD, 14);
	private static final Integer BUTTON_WIDTH = 80;
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
	
	public ProjectPreferencesPanel() {
		
		setLayout(new MigLayout("", "[75px:n][][20px:n,grow][]", "[10px][][][][][40][][][]"));
		
		JLabel lblProjectId = new JLabel("Project id:");
		add(lblProjectId, "cell 0 1,alignx trailing");
		
		textField = new JTextField();
		textField.setColumns(10);
		add(textField, "cell 1 1,growx");
		
		JLabel lblWaterSoftware = new JLabel("Water software:");
		add(lblWaterSoftware, "cell 0 2,alignx right");
		
		JRadioButton optSwmm = new JRadioButton("EPASWMM");
		add(optSwmm, "flowx,cell 1 2");
		
		JRadioButton optEpanet = new JRadioButton("EPANET");
		add(optEpanet, "cell 1 2");
		
		JRadioButton optHecras = new JRadioButton("HEC-RAS");
		add(optHecras, "cell 1 2");
		
		JLabel lblVersion = new JLabel("Version:");
		add(lblVersion, "cell 0 3,alignx trailing");
		
		cboSoftware = new JComboBox<String>();
		add(cboSoftware, "cell 1 3,growx");
		
		JLabel label = new JLabel("Data storage:");
		add(label, "cell 0 4,alignx right");
		
		optDbf = new JRadioButton("DBF");
		optDbf.setFont(new Font("Tahoma", Font.PLAIN, 12));
		optDbf.setActionCommand("selectSourceType");
		add(optDbf, "flowx,cell 1 4");
		
		optDatabase = new JRadioButton("Database");
		optDatabase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		optDatabase.setActionCommand("selectSourceType");
		add(optDatabase, "cell 1 4");
		
		lblFolderShp = new JLabel("Data folder:");
		add(lblFolderShp, "cell 0 5,alignx right");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 1 5 2 1,grow");
		
		txtInput = new JTextArea();
		scrollPane.setViewportView(txtInput);
		txtInput.setText("");
		txtInput.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnFolderShp = new JButton();
		btnFolderShp.setText("...");
		btnFolderShp.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFolderShp.setActionCommand("chooseFolderShp");
		add(btnFolderShp, "cell 3 5,alignx right");
		
		JPanel panelDatabase = new JPanel();
		panelDatabase.setName("panelDatabase");
		panelDatabase.setBorder(new TitledBorder(null, "Database Parameters", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		add(panelDatabase, "cell 0 6 4 1,grow");
		panelDatabase.setLayout(new MigLayout("", "[70px:n][::100px][30px][]", "[][][][][][][]"));
		
		lblNewLabel = new JLabel(BUNDLE.getString("Database.lblNewLabel.text_2")); //$NON-NLS-1$
		panelDatabase.add(lblNewLabel, "cell 0 0");

		cboDriver = new JComboBox<String>();
		cboDriver.setPreferredSize(new Dimension(24, 20));
		cboDriver.setMinimumSize(new Dimension(24, 20));
		//cboDriver.setModel(new DefaultComboBoxModel<String>(new String[] {"PG-9.1+PostGIS-1.5", "PG-9.2+PostGIS-2.0"}));
		cboDriver.setModel(new DefaultComboBoxModel<String>(new String[] {"PG-9.2 + PostGIS-2.0"}));
		panelDatabase.add(cboDriver, "cell 1 0 2 1,growx");

		lblIp = new JLabel(BUNDLE.getString("Database.lblIp.text")); //$NON-NLS-1$
		panelDatabase.add(lblIp, "cell 0 1");

		txtIP = new JTextField();
		panelDatabase.add(txtIP, "cell 1 1,growx");
		txtIP.setColumns(10);

		lblPort = new JLabel(BUNDLE.getString("Database.lblPort.text")); //$NON-NLS-1$
		panelDatabase.add(lblPort, "cell 0 2,alignx left");

		txtPort = new JTextField();
		txtPort.setColumns(10);
		panelDatabase.add(txtPort, "cell 1 2,growx");

		lblDatabase = new JLabel(BUNDLE.getString("Database.lblDatabase.text")); //$NON-NLS-1$
		panelDatabase.add(lblDatabase, "cell 0 3");

		txtDatabase = new JTextField();
		txtDatabase.setText("");
		txtDatabase.setColumns(10);
		panelDatabase.add(txtDatabase, "cell 1 3,growx");

		lblUser = new JLabel(BUNDLE.getString("Database.lblUser.text")); //$NON-NLS-1$
		panelDatabase.add(lblUser, "cell 0 4");

		txtUser = new JTextField();
		txtUser.setText("postgres");
		txtUser.setColumns(10);
		panelDatabase.add(txtUser, "cell 1 4,growx");

		lblPassword = new JLabel(BUNDLE.getString("Database.lblPassword.text")); //$NON-NLS-1$
		panelDatabase.add(lblPassword, "cell 0 5");

		txtPassword = new JPasswordField();
		txtPassword.setText("");
		panelDatabase.add(txtPassword, "cell 1 5,growx");
						
		btnTest = new JButton("Connection");
		btnTest.setMinimumSize(new Dimension(100, 23));
		btnTest.setActionCommand("testConnection");
		panelDatabase.add(btnTest, "flowx,cell 1 6");
		
		chkRemember = new JCheckBox("Remember password");
		chkRemember.setSelected(true);
		panelDatabase.add(chkRemember, "cell 2 6 2 1");
		
		JPanel panelManagement = new JPanel();
		panelManagement.setName("panelDatabase");
		panelManagement.setBorder(new TitledBorder(null, "Project Management", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		panelManagement.setLayout(new MigLayout("", "[70px:n][100px][30px][90px:n][grow]", "[23px][]"));
		add(panelManagement, "cell 0 7 4 1,grow");
		
		JLabel lblProject = new JLabel(BUNDLE.getString("ProjectPreferencesPanel.lblProject.text")); //$NON-NLS-1$
		panelManagement.add(lblProject, "cell 0 0,alignx left");
		
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
		panelManagement.add(btnRename, "cell 3 1,alignx right");
		
		btnCopy = new JButton(BUNDLE.getString("ProjectPreferencesPanel.btnCopy.text")); //$NON-NLS-1$
		btnCopy.setPreferredSize(new Dimension(BUTTON_WIDTH, 23));
		btnCopy.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnCopy.setEnabled(false);
		btnCopy.setActionCommand("createSchema");
		panelManagement.add(btnCopy, "cell 4 1,alignx right");
		
		JButton btnClose = new JButton();
		btnClose.setText("Close");
		btnClose.setMinimumSize(new Dimension(BUTTON_WIDTH, 23));
		btnClose.setActionCommand("closePanel");
		add(btnClose, "cell 3 8");
		
		setupListeners();

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