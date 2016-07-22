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
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.ConfigController;
import org.giswater.gui.frame.ConfigFrame;
import org.giswater.util.MaxLengthTextDocument;
import org.giswater.util.Utils;


public class ConfigPanel extends JPanel implements ActionListener {

	private ConfigController controller;	
	private ConfigFrame configFrame;	
	private JCheckBox chkAutoconnect;
	private JButton btnAccept;
	private JButton btnClose;
	private JButton btnFileDbAdmin;
	private JCheckBox chkAutostart;
	private JRadioButton optInpAlways;
	private JRadioButton optInpAsk;
	private JRadioButton optInpNever;
	private JRadioButton optRptAlways;
	private JRadioButton optRptAsk;
	private JRadioButton optRptNever;	
	private JRadioButton optProjectUpdAlways;
	private JRadioButton optProjectUpdAsk;
	private JRadioButton optProjectUpdNever;
	private JRadioButton optSQLEnabled;
	private JRadioButton optSQLDisabled;
	private JRadioButton optSridEnabled;
	private JRadioButton optSridDisabled;	
	private JRadioButton optLoadRasterEnabled;
	private JRadioButton optLoadRasterDisabled;		
	private JRadioButton optInpOwEnabled;
	private JRadioButton optInpOwDisabled;
	private JRadioButton optRptOwEnabled;
	private JRadioButton optRptOwDisabled;
	private JRadioButton optUpdatesEnabled;
	private JRadioButton optUpdatesDisabled;
	private JTextField txtLogFolderSize;
	private JButton btnOpenLogFolder;
	private JLabel lblImportResults;
	private JRadioButton optImportEnabled;
	private JRadioButton optImportDisabled;
	private JTextArea txtFileDbAdmin;
	private JScrollPane scrollPane;
	private JLabel lblChooseLanguage;
	private JComboBox<String> cboLocale;
	private JLabel lblRequiresRestart;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	
	
	public ConfigPanel() {
		try {
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
		}		
	}
	
	public ConfigFrame getFrame() {
		return configFrame;
	}
	
	public void setFrame(ConfigFrame configFrame) {
		this.configFrame = configFrame;
	}
	
	public void setController(ConfigController controller) {
		this.controller = controller;
	}
	
	public void setAutoConnect(String isChecked) {
		Boolean connect = Boolean.parseBoolean(isChecked);
		chkAutoconnect.setSelected(connect);
	}	

	public Boolean getAutoConnect() {
		return chkAutoconnect.isSelected();
	}	
	
	public void setAutoStart(String isChecked) {
		Boolean connect = Boolean.parseBoolean(isChecked);
		chkAutostart.setSelected(connect);
	}	
	
	public Boolean getAutoStart() {
		return chkAutostart.isSelected();
	}		
	
	public void setDbAdminFile(String path) {
		txtFileDbAdmin.setText(path);
	}		
	
	public String getDgAdminFile() {
		return txtFileDbAdmin.getText().trim().toLowerCase();
	}	
	
	public void setOpenInp(String status) {
		if (status.equals("always")) {
			optInpAlways.setSelected(true);
		}
		else if (status.equals("ask")) {
			optInpAsk.setSelected(true);
		}
		else if (status.equals("never")) {
			optInpNever.setSelected(true);
		}		
	}
	
	public String getOpenInp() {
		String value = "";
		if (optInpAlways.isSelected()) {
			value = optInpAlways.getName();
		}
		else if (optInpAsk.isSelected()) {
			value = optInpAsk.getName();
		}
		else if (optInpNever.isSelected()) {
			value = optInpNever.getName();
		}		
		return value;
	}
	
	public void setOpenRpt(String status){
		if (status.equals("always")) {
			optRptAlways.setSelected(true);
		}
		else if (status.equals("ask")) {
			optRptAsk.setSelected(true);
		}
		else if (status.equals("never")) {
			optRptNever.setSelected(true);
		}		
	}
	
	public String getOpenRpt() {
		String value = "";
		if (optRptAlways.isSelected()) {
			value = optRptAlways.getName();
		}
		else if (optRptAsk.isSelected()) {
			value = optRptAsk.getName();
		}
		else if (optRptNever.isSelected()) {
			value = optRptNever.getName();
		}		
		return value;
	}	
	
	public void setProjectUpdate(String status){
		if (status.equals("always")) {
			optProjectUpdAlways.setSelected(true);
		}
		else if (status.equals("ask")) {
			optProjectUpdAsk.setSelected(true);
		}
		else if (status.equals("never")) {
			optProjectUpdNever.setSelected(true);
		}		
	}
	
	public String getProjectUpdate() {
		String value = "";
		if (optProjectUpdAlways.isSelected()) {
			value = optProjectUpdAlways.getName();
		}
		else if (optProjectUpdAsk.isSelected()) {
			value = optProjectUpdAsk.getName();
		}
		else if (optProjectUpdNever.isSelected()) {
			value = optProjectUpdNever.getName();
		}		
		return value;
	}
	
	public String getOverwriteInp() {
		String value = "false";
		if (optInpOwEnabled.isSelected()) {
			value = "true";
		}
		return value;
	}	
	
	public void setOverwriteInp(String status) {
		if (status.equals("true")) {
			optInpOwEnabled.setSelected(true);
		}
		else if (status.equals("false")) {
			optInpOwDisabled.setSelected(true);
		}	
	}
	
	public String getOverwriteRpt() {
		String value = "false";
		if (optRptOwEnabled.isSelected()) {
			value = "true";
		}
		return value;
	}	
	
	public void setOverwriteRpt(String status) {
		if (status.equals("true")) {
			optRptOwEnabled.setSelected(true);
		}
		else if (status.equals("false")) {
			optRptOwDisabled.setSelected(true);
		}	
	}
	
	public String getAutoImportRpt() {
		String value = "false";
		if (optImportEnabled.isSelected()) {
			value = "true";
		}
		return value;
	}	
	
	public void setAutoImportRpt(String status) {
		if (status.equals("true")) {
			optImportEnabled.setSelected(true);
		}
		else if (status.equals("false")) {
			optImportDisabled.setSelected(true);
		}	
	}
	
	public void setSqlLog(String status) {
		if (status.equals("true")) {
			optSQLEnabled.setSelected(true);
		}
		else if (status.equals("false")) {
			optSQLDisabled.setSelected(true);
		}
	}
	
	public String getSqlLog() {
		String value = "false";
		if (optSQLEnabled.isSelected()) {
			value = "true";
		}
		return value;
	}	
	
	public void setSridQuestion(String status) {
		if (status.equals("true")) {
			optSridEnabled.setSelected(true);
		}
		else if (status.equals("false")) {
			optSridDisabled.setSelected(true);
		}
	}
	
	public String getSridQuestion() {
		String value = "false";
		if (optSridEnabled.isSelected()) {
			value = "true";
		}
		return value;
	}	
	
	public void setLoadRaster(String status) {
		if (status.equals("true")) {
			optLoadRasterEnabled.setSelected(true);
		}
		else if (status.equals("false")) {
			optLoadRasterDisabled.setSelected(true);
		}
	}
	
	public String getLoadRaster() {
		String value = "false";
		if (optLoadRasterEnabled.isSelected()) {
			value = "true";
		}
		return value;
	}	
	
	public void setCheckUpdates(String status) {
		if (status.equals("true")) {
			optUpdatesEnabled.setSelected(true);
		}
		else if (status.equals("false")) {
			optUpdatesDisabled.setSelected(true);
		}
	}
	
	public String getCheckUpdates() {
		String value = "false";
		if (optUpdatesEnabled.isSelected()) {
			value = "true";
		}
		return value;
	}	
	
	public void setLanguage(String language) {
		if (language.equals("en")) {
			cboLocale.setSelectedItem("English");
		}
		else if (language.equals("es")) {
			cboLocale.setSelectedItem("Spanish");
		}
		else if (language.equals("pt")) {
			cboLocale.setSelectedItem("Portuguese");
		}
		else if (language.equals("pt_BR")) {
			cboLocale.setSelectedItem("Brazilian Portuguese");
		}		
	}

	public String getLanguage() {
		String locale = "en";
		String language = cboLocale.getSelectedItem().toString();
		if (language.equals("English")) {
			locale = "en";
		}
		else if (language.equals("Spanish")) {
			locale = "es";
		}
		else if (language.equals("Portuguese")) {
			locale = "pt";
		}
		else if (language.equals("Brazilian Portuguese")) {
			locale = "pt_BR";
		}		
		return locale;
	}	
	
	public void setLogFolderSize(Integer size) {
		txtLogFolderSize.setText(size.toString());
	}
	
	public Integer getLogFolderSize() {
		Integer size = 10;
        String aux = txtLogFolderSize.getText();
        try {
	        size = Integer.parseInt(aux);		
		} catch (NumberFormatException e) {
        	String msg = "Value of Log folder size is not valid. It must be a number";
        	Utils.logError(msg);
        }              
		return size;
	}	
	
	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[115px:n][4px:n][75px:n:75px][75px:n:75px][60px:n:60px][80px:n:80px][75px:n:75px]", "[5px:n][][][][][][][][][][][][][2px:n][::17px][::17px][2px:n][34px:n][6px:n][]"));
		
		// Define button groups
	    ButtonGroup group = new ButtonGroup();
	    ButtonGroup group2 = new ButtonGroup();
	    ButtonGroup group3 = new ButtonGroup();
	    ButtonGroup group5 = new ButtonGroup();
	    ButtonGroup group6 = new ButtonGroup();
	    ButtonGroup group7 = new ButtonGroup();
	    ButtonGroup group8 = new ButtonGroup();
	    ButtonGroup group9 = new ButtonGroup();
	    ButtonGroup group10 = new ButtonGroup();
	    ButtonGroup group11 = new ButtonGroup();
	    
		JLabel lblOpenInpFiles = new JLabel(BUNDLE.getString("ConfigPanel.lblOpenInpFiles.text"));
		add(lblOpenInpFiles, "cell 0 1,alignx trailing");
		
		optInpAlways = new JRadioButton(BUNDLE.getString("ConfigPanel.optInpAlways.text"));  //$NON-NLS-1$
		add(optInpAlways, "cell 2 1");
		optInpAlways.setName("always");
		group.add(optInpAlways);	    
		
		optInpAsk = new JRadioButton(BUNDLE.getString("ConfigPanel.optInpAsk.text"));  //$NON-NLS-1$
		add(optInpAsk, "cell 3 1");
		optInpAsk.setName("ask");
		group.add(optInpAsk);		    
		
		optInpNever = new JRadioButton(BUNDLE.getString("ConfigPanel.optInpNever.text"));  //$NON-NLS-1$
		add(optInpNever, "cell 4 1");
		optInpNever.setName("never");
		group.add(optInpNever);			 
		
		JLabel lblOpenRptFiles = new JLabel(BUNDLE.getString("ConfigPanel.lblOpenRptFiles.text")); //$NON-NLS-1$
		add(lblOpenRptFiles, "cell 0 2,alignx trailing");
		
		optRptAlways = new JRadioButton(BUNDLE.getString("ConfigPanel.always")); 
		add(optRptAlways, "cell 2 2");
		optRptAlways.setName("always");
		group2.add(optRptAlways);
		
		optRptAsk = new JRadioButton(BUNDLE.getString("ConfigPanel.askme")); 
		add(optRptAsk, "cell 3 2");
		optRptAsk.setName("ask");
		group2.add(optRptAsk);	
		
		optRptNever = new JRadioButton(BUNDLE.getString("ConfigPanel.never")); 
		add(optRptNever, "cell 4 2");
		optRptNever.setName("never");
		group2.add(optRptNever);		   	    
		
		JLabel lblProjectUpdate = new JLabel(BUNDLE.getString("ConfigPanel.lblProjectUpdate.text")); //$NON-NLS-1$
		add(lblProjectUpdate, "cell 0 3,alignx right");
		
		optProjectUpdAlways = new JRadioButton(BUNDLE.getString("ConfigPanel.always")); //$NON-NLS-1$
		optProjectUpdAlways.setName("always");
		add(optProjectUpdAlways, "cell 2 3");
		group11.add(optProjectUpdAlways);
		
		optProjectUpdAsk = new JRadioButton(BUNDLE.getString("ConfigPanel.askme")); //$NON-NLS-1$
		optProjectUpdAsk.setName("ask");
		add(optProjectUpdAsk, "cell 3 3");
		group11.add(optProjectUpdAsk);
		
		optProjectUpdNever = new JRadioButton(BUNDLE.getString("ConfigPanel.never")); //$NON-NLS-1$
		optProjectUpdNever.setName("never");
		add(optProjectUpdNever, "cell 4 3");
		group11.add(optProjectUpdNever);
		
		lblImportResults = new JLabel(BUNDLE.getString("ConfigPanel.lblImportResults.text"));
		add(lblImportResults, "cell 0 4,alignx right");
		
		optImportEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optImportEnabled.text"));
		add(optImportEnabled, "cell 2 4");
		optImportEnabled.setName("true");
		group10.add(optImportEnabled);
		
	    optImportDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optImportDisabled.text"));
	    add(optImportDisabled, "cell 3 4");
	    optImportDisabled.setName("true");
	    group10.add(optImportDisabled);
	
		JLabel lblOverwriteInpFiles = new JLabel(BUNDLE.getString("ConfigPanel.lblOverwriteInpFiles.text"));
		add(lblOverwriteInpFiles, "cell 0 5,alignx right");
		
		optInpOwEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optInpOwEnabled.text")); //$NON-NLS-1$
		add(optInpOwEnabled, "cell 2 5");
		optInpOwEnabled.setName("true");
		group7.add(optInpOwEnabled);
		
		optInpOwDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optInpOwDisabled.text")); //$NON-NLS-1$
		add(optInpOwDisabled, "cell 3 5");
		optInpOwDisabled.setName("false");
		group7.add(optInpOwDisabled);
		
		JLabel lblOverwriteRptFiles = new JLabel(BUNDLE.getString("ConfigPanel.lblOverwriteRptFiles.text"));
		add(lblOverwriteRptFiles, "cell 0 6,alignx right");
		
		optRptOwEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optRptOwEnabled.text")); //$NON-NLS-1$
		add(optRptOwEnabled, "cell 2 6");
		optRptOwEnabled.setName("true");
		group8.add(optRptOwEnabled);
		
		optRptOwDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optRptOwDisabled.text")); //$NON-NLS-1$
		add(optRptOwDisabled, "cell 3 6");
		optRptOwDisabled.setName("false");
		group8.add(optRptOwDisabled);
		
		JLabel lblCreateSqlLog = new JLabel(BUNDLE.getString("ConfigPanel.lblCreateSqlLog.text"));
		add(lblCreateSqlLog, "cell 0 7,alignx right");
			    
	    optSQLEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optSQLEnabled.text"));  //$NON-NLS-1$
	    add(optSQLEnabled, "cell 2 7");
	    optSQLEnabled.setName("true");
	    group3.add(optSQLEnabled);
	    
	    optSQLDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optSQLDisabled.text"));  //$NON-NLS-1$
	    add(optSQLDisabled, "cell 3 7");
	    optSQLDisabled.setName("false");
	    group3.add(optSQLDisabled);

	    JLabel lblSrid = new JLabel(BUNDLE.getString("ConfigPanel.lblSrid.text"));
	    add(lblSrid, "cell 0 8,alignx right");
		
		optSridEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optSridEnabled.text"));  //$NON-NLS-1$
		add(optSridEnabled, "cell 2 8");
		optSridEnabled.setName("true");
		group5.add(optSridEnabled);
		
		optSridDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optSridDisabled.text"));  //$NON-NLS-1$
		add(optSridDisabled, "cell 3 8");
		optSridDisabled.setName("false");
		group5.add(optSridDisabled);
		
	    JLabel lblLoadRaster = new JLabel(BUNDLE.getString("ConfigPanel.lblLoadRaster.text"));
	    add(lblLoadRaster, "cell 0 9,alignx right");
		
		optLoadRasterEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.enabled")); 
		add(optLoadRasterEnabled, "cell 2 9");
		optLoadRasterEnabled.setName("true");
		group6.add(optLoadRasterEnabled);
		
		optLoadRasterDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.disabled")); 
		add(optLoadRasterDisabled, "cell 3 9");
		optLoadRasterDisabled.setName("false");
		group6.add(optLoadRasterDisabled);		    
		
		JLabel lblCheckUpdates = new JLabel(BUNDLE.getString("ConfigPanel.lblCheckUpdates.text"));
		add(lblCheckUpdates, "cell 0 10,alignx right");
	    
	    optUpdatesEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optUpdatesEnabled.text")); //$NON-NLS-1$
	    add(optUpdatesEnabled, "cell 2 10");
	    optUpdatesEnabled.setName("true");
	    group9.add(optUpdatesEnabled);
	    
	    optUpdatesDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optUpdatesDisabled.text")); //$NON-NLS-1$
	    add(optUpdatesDisabled, "cell 3 10");
	    optUpdatesDisabled.setName("false");
	    group9.add(optUpdatesDisabled);
	    
	    lblChooseLanguage = new JLabel(BUNDLE.getString("ConfigPanel.lblChooseLanguage.text")); //$NON-NLS-1$
	    add(lblChooseLanguage, "cell 0 11,alignx right");
	    
	    cboLocale = new JComboBox<String>();
	    cboLocale.setModel(new DefaultComboBoxModel<String>(new String[] {"English", "Brazilian Portuguese", "Portuguese"}));
	    add(cboLocale, "cell 2 11 2 1,growx");
	    
	    lblRequiresRestart = new JLabel(BUNDLE.getString("ConfigPanel.lblrequieresRestart.text")); //$NON-NLS-1$
	    add(lblRequiresRestart, "cell 4 11 2 1");
		
	    JLabel lblLogFolderSize = new JLabel(BUNDLE.getString("ConfigPanel.lblLogFolderSize.text"));
	    add(lblLogFolderSize, "cell 0 12,alignx right");
		
		JLabel lblMb = new JLabel(BUNDLE.getString("ConfigPanel.lblMb.text"));
		add(lblMb, "cell 4 12,alignx left");
		
		btnOpenLogFolder = new JButton(BUNDLE.getString("ConfigPanel.btnOpenLogFolder.text"));
		add(btnOpenLogFolder, "cell 5 12,growx");
		btnOpenLogFolder.setActionCommand("openLogFolder");
		
		chkAutostart = new JCheckBox(BUNDLE.getString("ConfigPanel.chkAutostart.text")); 
		add(chkAutostart, "cell 2 14 3 1");
		chkAutostart.setSelected(true);
		
		chkAutoconnect = new JCheckBox(BUNDLE.getString("Config.chkConnect")); 
		add(chkAutoconnect, "cell 2 15 3 1");
		chkAutoconnect.setSelected(true);
		
		JLabel lblDbAdmin = new JLabel(BUNDLE.getString("ConfigPanel.lblDbAdmin.text"));
		add(lblDbAdmin, "cell 0 17,alignx right");
		
		scrollPane = new JScrollPane();
		add(scrollPane, "cell 2 17 4 1,grow");
		
		txtFileDbAdmin = new JTextArea();
		txtFileDbAdmin.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(txtFileDbAdmin);
		txtFileDbAdmin.setText("");
		
		btnFileDbAdmin = new JButton();
		add(btnFileDbAdmin, "cell 6 17,growx");
		btnFileDbAdmin.setMinimumSize(new Dimension(72, 9));
		btnFileDbAdmin.setText("...");
		btnFileDbAdmin.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFileDbAdmin.setActionCommand("chooseFileDbAdmin");
		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(4);		
		
		txtLogFolderSize = new JTextField();
		add(txtLogFolderSize, "cell 2 12 2 1,growx");
		txtLogFolderSize.setText("");
		txtLogFolderSize.setColumns(8);
		txtLogFolderSize.setDocument(maxLength);	
		
		btnAccept = new JButton(BUNDLE.getString("Generic.btnAccept.text")); 
		add(btnAccept, "cell 5 19,alignx right");
		btnAccept.setMinimumSize(new Dimension(75, 23));
		btnAccept.setActionCommand("configAccept");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text")); 
		add(btnClose, "cell 6 19,alignx right");
		btnClose.setMinimumSize(new Dimension(75, 23));
		btnClose.setActionCommand("closePanel");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
			
		btnFileDbAdmin.addActionListener(this);
		btnOpenLogFolder.addActionListener(this);
		btnAccept.addActionListener(this);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getFrame().setVisible(false);
			}
		});		
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
}