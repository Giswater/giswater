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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.ConfigController;
import org.giswater.gui.frame.ConfigFrame;
import org.giswater.util.MaxLengthTextDocument;
import org.giswater.util.Utils;
import javax.swing.JRadioButton;


public class ConfigPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 

	private ConfigController controller;	
	private ConfigFrame configFrame;	
	
	private JPanel panel;
	private JCheckBox chkAutoconnect;
	private JTabbedPane tabbedPane;
	private JButton btnAccept;
	private JButton btnClose;
	private JTextField txtFileDbAdmin;
	private JButton btnFileDbAdmin;
	private JCheckBox chkAutostart;
	private JRadioButton optInpAlways;
	private JRadioButton optInpAsk;
	private JRadioButton optInpNever;
	private JRadioButton optRptAlways;
	private JRadioButton optRptAsk;
	private JRadioButton optRptNever;	
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
	
	
	public ConfigPanel(ConfigFrame configFrame) {
		this.configFrame = configFrame;
		try {
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
			System.exit(ERROR);
		}		
	}
	
	public ConfigFrame getFrame(){
		return configFrame;
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
	
	public void setOpenInp(String status){
		if (status.equals("always")){
			optInpAlways.setSelected(true);
		}
		else if (status.equals("ask")){
			optInpAsk.setSelected(true);
		}
		else if (status.equals("never")){
			optInpNever.setSelected(true);
		}		
	}
	
	public String getOpenInp() {
		String value = "";
		if (optInpAlways.isSelected()){
			value = optInpAlways.getName();
		}
		else if (optInpAsk.isSelected()){
			value = optInpAsk.getName();
		}
		else if (optInpNever.isSelected()){
			value = optInpNever.getName();
		}		
		return value;
	}
	
	public void setOpenRpt(String status){
		if (status.equals("always")){
			optRptAlways.setSelected(true);
		}
		else if (status.equals("ask")){
			optRptAsk.setSelected(true);
		}
		else if (status.equals("never")){
			optRptNever.setSelected(true);
		}		
	}
	
	public String getOverwriteInp() {
		String value = "false";
		if (optInpOwEnabled.isSelected()){
			value = "true";
		}
		return value;
	}	
	
	public void setOverwriteInp(String status){
		if (status.equals("true")){
			optInpOwEnabled.setSelected(true);
		}
		else if (status.equals("false")){
			optInpOwDisabled.setSelected(true);
		}	
	}
	
	public String getOverwriteRpt() {
		String value = "false";
		if (optRptOwEnabled.isSelected()){
			value = "true";
		}
		return value;
	}	
	
	public void setOverwriteRpt(String status){
		if (status.equals("true")){
			optRptOwEnabled.setSelected(true);
		}
		else if (status.equals("false")){
			optRptOwDisabled.setSelected(true);
		}	
	}
	
	public String getAutoImportRpt() {
		String value = "false";
		if (optImportEnabled.isSelected()){
			value = "true";
		}
		return value;
	}	
	
	public void setAutoImportRpt(String status) {
		if (status.equals("true")){
			optImportEnabled.setSelected(true);
		}
		else if (status.equals("false")){
			optImportDisabled.setSelected(true);
		}	
	}
	
	public String getOpenRpt() {
		String value = "";
		if (optRptAlways.isSelected()){
			value = optRptAlways.getName();
		}
		else if (optRptAsk.isSelected()){
			value = optRptAsk.getName();
		}
		else if (optRptNever.isSelected()){
			value = optRptNever.getName();
		}		
		return value;
	}	
	
	public void setSqlLog(String status){
		if (status.equals("true")){
			optSQLEnabled.setSelected(true);
		}
		else if (status.equals("false")){
			optSQLDisabled.setSelected(true);
		}
	}
	
	public String getSqlLog() {
		String value = "false";
		if (optSQLEnabled.isSelected()){
			value = "true";
		}
		return value;
	}	
	
	public void setSridQuestion(String status){
		if (status.equals("true")){
			optSridEnabled.setSelected(true);
		}
		else if (status.equals("false")){
			optSridDisabled.setSelected(true);
		}
	}
	
	public String getSridQuestion() {
		String value = "false";
		if (optSridEnabled.isSelected()){
			value = "true";
		}
		return value;
	}	
	
	public void setLoadRaster(String status){
		if (status.equals("true")){
			optLoadRasterEnabled.setSelected(true);
		}
		else if (status.equals("false")){
			optLoadRasterDisabled.setSelected(true);
		}
	}
	
	public String getLoadRaster() {
		String value = "false";
		if (optLoadRasterEnabled.isSelected()){
			value = "true";
		}
		return value;
	}	
	
	public void setCheckUpdates(String status){
		if (status.equals("true")){
			optUpdatesEnabled.setSelected(true);
		}
		else if (status.equals("false")){
			optUpdatesDisabled.setSelected(true);
		}
	}
	
	public String getCheckUpdates() {
		String value = "false";
		if (optUpdatesEnabled.isSelected()){
			value = "true";
		}
		return value;
	}	
	
	public void setLogFolderSize(Integer size){
		txtLogFolderSize.setText(size.toString());
	}
	
	public Integer getLogFolderSize() {
		Integer size = 10;
        String aux = txtLogFolderSize.getText();
        try{
	        size = Integer.parseInt(aux);		
		} catch (NumberFormatException e){
        	String msg = "Value of Log folder size is not valid. It must be a number";
        	Utils.logError(msg);
        }              
		return size;
	}	
	
	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][:500px:531px][-33.00]", "[5px][grow]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Config.panel.title"), null, panel_1, null); 
		panel_1.setLayout(new MigLayout("", "[:120.00:120px][220][]", "[387.00,grow][10px][][][10px:n][25]"));

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.add(panel, "cell 0 0 3 1,grow");
		panel.setLayout(new MigLayout("", "[][:150:300px][50px:n][45.00]", "[5px:n][][][][][][][][][][][5px][][10.00][][10px]"));
		
	    ButtonGroup group = new ButtonGroup();
	    ButtonGroup group2 = new ButtonGroup();
	    ButtonGroup group3 = new ButtonGroup();
	    ButtonGroup group5 = new ButtonGroup();
	    ButtonGroup group6 = new ButtonGroup();
	    ButtonGroup group7 = new ButtonGroup();
	    ButtonGroup group8 = new ButtonGroup();
	    ButtonGroup group9 = new ButtonGroup();
	    ButtonGroup group10 = new ButtonGroup();
		
		JLabel lblOpenInpFiles = new JLabel(BUNDLE.getString("ConfigPanel.lblOpenInpFiles.text")); 
		panel.add(lblOpenInpFiles, "cell 0 1,alignx trailing");
		
		JLabel lblOpenRptFiles = new JLabel("Open RPT files:");
		panel.add(lblOpenRptFiles, "cell 0 2,alignx trailing");
		
		lblImportResults = new JLabel(BUNDLE.getString("ConfigPanel.lblImportResults.text"));
		panel.add(lblImportResults, "cell 0 3,alignx right");

		
		JLabel lblOverwriteInpFiles = new JLabel(BUNDLE.getString("ConfigPanel.lblOverwriteInpFiles.text")); 
	    panel.add(lblOverwriteInpFiles, "cell 0 4,alignx trailing");
	    
	    JLabel lblOverwriteRptFiles = new JLabel(BUNDLE.getString("ConfigPanel.lblOverwriteRptFiles.text")); 
	    panel.add(lblOverwriteRptFiles, "cell 0 5,alignx right");
	    
	    JLabel lblCreateSqlLog = new JLabel(BUNDLE.getString("ConfigPanel.lblCreateSqlLog.text")); 
	    panel.add(lblCreateSqlLog, "cell 0 6,alignx trailing");

	    JLabel lblSrid = new JLabel(BUNDLE.getString("ConfigPanel.lblSrid.text")); 
	    panel.add(lblSrid, "cell 0 7,alignx trailing");

	    JLabel lblLoadRaster = new JLabel(BUNDLE.getString("ConfigPanel.lblLoadRaster.text")); 
	    panel.add(lblLoadRaster, "cell 0 8,alignx trailing");
	    
	    JLabel lblCheckUpdates = new JLabel(BUNDLE.getString("ConfigPanel.lblCheckUpdates.text")); 
	    panel.add(lblCheckUpdates, "cell 0 9,alignx trailing");
	        
	    JLabel lblLogFolderSize = new JLabel(BUNDLE.getString("ConfigPanel.lblLogFolderSize.text")); 
	    panel.add(lblLogFolderSize, "cell 0 10,alignx trailing");
	    
	    JLabel lblDbAdmin = new JLabel(BUNDLE.getString("ConfigPanel.lblDbAdmin.text")); 
	    panel.add(lblDbAdmin, "cell 0 14,alignx trailing");
	    
	    optInpOwEnabled = new JRadioButton("Enabled");
	    optInpOwEnabled.setName("true");
	    panel.add(optInpOwEnabled, "flowx,cell 1 4");
	    group7.add(optInpOwEnabled);
	    
	    optInpOwDisabled = new JRadioButton("Disabled");
	    optInpOwDisabled.setName("false");
	    panel.add(optInpOwDisabled, "cell 1 4");
	    group7.add(optInpOwDisabled);
	    
	    optRptOwEnabled = new JRadioButton("Enabled");
	    optRptOwEnabled.setName("true");
	    panel.add(optRptOwEnabled, "flowx,cell 1 5");
	    group8.add(optRptOwEnabled);
	    
	    optRptOwDisabled = new JRadioButton("Disabled");
	    optRptOwDisabled.setName("false");
	    panel.add(optRptOwDisabled, "cell 1 5");
	    group8.add(optRptOwDisabled);
	    
	    optInpAlways = new JRadioButton(BUNDLE.getString("ConfigPanel.always")); 
	    optInpAlways.setName("always");
	    panel.add(optInpAlways, "flowx,cell 1 1");
	    group.add(optInpAlways);	    
	    
	    optInpAsk = new JRadioButton(BUNDLE.getString("ConfigPanel.askme")); 
	    optInpAsk.setName("ask");
	    panel.add(optInpAsk, "cell 1 1");
	    group.add(optInpAsk);		    
	    
	    optInpNever = new JRadioButton(BUNDLE.getString("ConfigPanel.never")); 
	    optInpNever.setName("never");
	    panel.add(optInpNever, "cell 1 1");
	    group.add(optInpNever);			 
	    
	    optRptAlways = new JRadioButton(BUNDLE.getString("ConfigPanel.always")); 
	    optRptAlways.setName("always");
	    panel.add(optRptAlways, "flowx,cell 1 2");
	    group2.add(optRptAlways);
	    
	    optRptAsk = new JRadioButton(BUNDLE.getString("ConfigPanel.askme")); 
	    optRptAsk.setName("ask");
	    panel.add(optRptAsk, "cell 1 2");
	    group2.add(optRptAsk);	
	    
	    optRptNever = new JRadioButton(BUNDLE.getString("ConfigPanel.never")); 
	    optRptNever.setName("never");
	    panel.add(optRptNever, "cell 1 2");
	    group2.add(optRptNever);		   	    
	    
	    optSQLEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.enabled")); 
	    optSQLEnabled.setName("true");
	    panel.add(optSQLEnabled, "flowx,cell 1 6");
	    group3.add(optSQLEnabled);
	    
	    optSQLDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.disabled")); 
	    optSQLDisabled.setName("false");
	    panel.add(optSQLDisabled, "cell 1 6");
	    group3.add(optSQLDisabled);
	    
	    optSridEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.enabled")); 
	    optSridEnabled.setName("true");
	    panel.add(optSridEnabled, "flowx,cell 1 7");
	    group5.add(optSridEnabled);
	    
	    optSridDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.disabled")); 
	    optSridDisabled.setName("false");
	    panel.add(optSridDisabled, "cell 1 7");
	    group5.add(optSridDisabled);
	    
	    optLoadRasterEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.enabled")); 
	    optLoadRasterEnabled.setName("true");
	    panel.add(optLoadRasterEnabled, "flowx,cell 1 8");
	    group6.add(optLoadRasterEnabled);
	    
	    optLoadRasterDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.disabled")); 
	    optLoadRasterDisabled.setName("false");
	    panel.add(optLoadRasterDisabled, "cell 1 8");
	    group6.add(optLoadRasterDisabled);		    
	    
	    optUpdatesEnabled = new JRadioButton("Enabled");
	    optUpdatesEnabled.setName("true");
	    panel.add(optUpdatesEnabled, "flowx,cell 1 9");
	    group9.add(optUpdatesEnabled);
	    
	    optUpdatesDisabled = new JRadioButton("Disabled");
	    optUpdatesDisabled.setName("false");
	    panel.add(optUpdatesDisabled, "cell 1 9");
	    group9.add(optUpdatesDisabled);
	    
	    txtLogFolderSize = new JTextField();
	    txtLogFolderSize.setText("");
	    panel.add(txtLogFolderSize, "flowx,cell 1 10,alignx left");
	    txtLogFolderSize.setColumns(8);
		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(4);		
		txtLogFolderSize.setDocument(maxLength);	
	    
	    btnOpenLogFolder = new JButton(BUNDLE.getString("ConfigPanel.btnOpenLogFolder.text")); 
	    btnOpenLogFolder.setActionCommand("openLogFolder");
	    panel.add(btnOpenLogFolder, "cell 2 10 2 1,alignx right");
	    
	    chkAutostart = new JCheckBox(BUNDLE.getString("ConfigPanel.chkAutostart.text")); 
	    chkAutostart.setSelected(true);
	    panel.add(chkAutostart, "cell 1 12");
	    
	    chkAutoconnect = new JCheckBox(BUNDLE.getString("Config.chkConnect")); 
	    chkAutoconnect.setSelected(true);
	    panel.add(chkAutoconnect, "cell 1 13,aligny baseline");
	    
	    txtFileDbAdmin = new JTextField();
	    txtFileDbAdmin.setText((String) null);
	    txtFileDbAdmin.setColumns(10);
	    panel.add(txtFileDbAdmin, "cell 1 14 2 1,growx");
	    
	    btnFileDbAdmin = new JButton();
	    btnFileDbAdmin.setText("...");
	    btnFileDbAdmin.setFont(new Font("Tahoma", Font.BOLD, 12));
	    btnFileDbAdmin.setActionCommand("chooseFileDbAdmin");
	    panel.add(btnFileDbAdmin, "cell 3 14,alignx right");
	    
	    JLabel lblMb = new JLabel(BUNDLE.getString("ConfigPanel.lblMb.text")); 
	    panel.add(lblMb, "cell 1 10");    
		
		optImportEnabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optImportEnabled.text"));
		optImportEnabled.setName("true");
		panel.add(optImportEnabled, "flowx,cell 1 3");
		group10.add(optImportEnabled);
		
	    optImportDisabled = new JRadioButton(BUNDLE.getString("ConfigPanel.optImportDisabled.text"));
	    optImportDisabled.setName("true");
	    panel.add(optImportDisabled, "cell 1 3");
	    group10.add(optImportDisabled);
		
		JLabel lblNotepad = new JLabel(BUNDLE.getString("ConfigPanel.lblNotepad.text")); 
		panel_1.add(lblNotepad, "cell 0 2 3 1,aligny top");
		
		JLabel lblNotepad2 = new JLabel(BUNDLE.getString("ConfigPanel.lblNotepad2.text")); 
		panel_1.add(lblNotepad2, "cell 0 3");
		
		btnAccept = new JButton(BUNDLE.getString("Form.btnAccept.text")); 
		btnAccept.setActionCommand("configAccept");
		panel_1.add(btnAccept, "cell 1 5,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text")); 
		btnClose.setActionCommand("closePanel");
		panel_1.add(btnClose, "cell 2 5");

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