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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.MainController;
import org.giswater.gui.frame.EpaFrame;
import org.giswater.util.MaxLengthTextDocument;
import org.giswater.util.Utils;


public class EpaPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private MainController controller;
	private EpaFrame epaFrame;	

	private JTextField txtProject;
	private JTextArea txtFileRpt;
	private JTextArea txtFileInp;
	private JButton btnFileInp;
	private JButton btnFileRpt;
	private JButton btnAccept;
	private JCheckBox chkExec;
	private JCheckBox chkImport;
	private JComboBox<String> cboSchema;
	private JButton btnSectorSelection;
	private JButton btnOptions;
	private JButton btnDesign;
	private JPanel panel_4;
	private JTabbedPane tabbedPane;
	private JComboBox<String> cboSoftware;
	private JLabel lblEpaSoftware;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JRadioButton optDbf;
	private JRadioButton optDatabase;
	private JCheckBox chkExport;
	private JTextArea txtInput;
	private JLabel lblFolderShp;
	private JButton btnFolderShp;
	private JScrollPane scrollPane_4;
	private JLabel lblChooseType;
	private JLabel lblSchema;
	private JButton btnCreateSchema;
	private JButton btnDeleteSchema;
	private JButton btnReport;
	
	private String softwareName;   // epaswmm | epanet
	private JButton btnDeleteData;
	private JLabel lblNewLabel;
	public JButton btnClose;

	
	public EpaPanel(String softwareName) {
		try {
			this.softwareName = softwareName;
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
			System.exit(ERROR);
		}
	}
	
	public void setFrame(EpaFrame epaFrame) {
		this.epaFrame = epaFrame;
	}	
	
	public EpaFrame getFrame(){
		return epaFrame;
	}	

	public void setController(MainController controller) {
		this.controller = controller;
	}

	public MainController getController() {
		return controller;
	}
	
	public String getSoftwareName() {
		return softwareName;
	}
	
	public JDialog getDialog() {
		return new JDialog();
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
	
	public String getFileRpt() {
		String fileRpt = txtFileRpt.getText().trim();
		return fileRpt;
	}
	
	public void setFileRpt(String path) {
		txtFileRpt.setText(path);
	}
	
	public String getFileInp() {
		String fileInp = txtFileInp.getText().trim();
		return fileInp;
	}

	public void setFileInp(String path) {
		txtFileInp.setText(path);
	}

	public void setProjectName(String projectName) {
		txtProject.setText(projectName);
	}

	public String getProjectName() {
		return txtProject.getText().trim();
	}
	
	public boolean isExportChecked() {
		return chkExport.isSelected();
	}

	public boolean isExecChecked() {
		return chkExec.isSelected();
	}

	public boolean isImportChecked() {
		return chkImport.isSelected();
	}
	
	public void enableControlsDbf(boolean enable) {
		boolean dbfSelected = optDbf.isSelected();
		lblFolderShp.setEnabled(dbfSelected);
		txtInput.setEnabled(dbfSelected);
		btnFolderShp.setEnabled(dbfSelected);
	}	
	
	public void enableControlsDatabase(boolean enable){
		btnCreateSchema.setEnabled(enable);
		btnDeleteSchema.setEnabled(enable);
		btnDeleteData.setEnabled(enable);
		btnOptions.setEnabled(enable);
		btnDesign.setEnabled(enable);
		btnSectorSelection.setEnabled(enable);
		btnReport.setEnabled(enable);
		lblSchema.setEnabled(enable);
		cboSchema.setEnabled(enable);	
	}	
	
	public void enableControlsText(boolean enable) {
		txtProject.setEnabled(enable);
		txtInput.setEnabled(enable);
		txtFileInp.setEnabled(enable);
		txtFileRpt.setEnabled(enable);
		this.requestFocusInWindow();		
	}		
	
	public void enableAccept(boolean enable){
		btnAccept.setEnabled(enable);
	}	
	
	public void setDesignButton(String text, String actionCommand){
		btnDesign.setText(text);
		btnDesign.setActionCommand(actionCommand);
	}
	
	public void setOptionsButton(String text, String actionCommand){
		btnOptions.setText(text);
		btnOptions.setActionCommand(actionCommand);
	}	
	
	public void setReportButton(String text, String actionCommand) {
		btnReport.setText(text);
		btnReport.setActionCommand(actionCommand);
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
		} else{
			DefaultComboBoxModel<String> theModel = (DefaultComboBoxModel<String>) cboSchema.getModel();
			theModel.removeAllElements();
		}
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

	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][614.00px]", "[5px][410.00,grow]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		// Panel 4
		panel_4 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Form.panel_3.title"), null, panel_4, null); //$NON-NLS-1$
		panel_4.setLayout(new MigLayout("", "[15px:15px][110px:110px][320.00px][115.00]", "[][25px][40px][25px][5px:5px:5px][][30px:n,bottom][40.00][24][40px][24][][30px:n,bottom]"));
		
		lblChooseType = new JLabel(BUNDLE.getString("EpaPanel.lbl.text")); //$NON-NLS-1$
		panel_4.add(lblChooseType, "cell 1 0");
		
		optDbf = new JRadioButton("DBF");
		optDbf.setActionCommand("selectSourceType");
		optDbf.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_4.add(optDbf, "flowx,cell 2 0");
		
		optDatabase = new JRadioButton(BUNDLE.getString("EpaPanel.Database.text"));
		optDatabase.setActionCommand("selectSourceType");
		optDatabase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_4.add(optDatabase, "cell 2 0");
		
		//Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(optDbf);
	    group.add(optDatabase);	

		lblEpaSoftware = new JLabel(BUNDLE.getString("Form.lblEpaSoftware.text")); //$NON-NLS-1$
		panel_4.add(lblEpaSoftware, "cell 1 1");

		cboSoftware = new JComboBox<String>();
		cboSoftware.setPreferredSize(new Dimension(24, 20));
		cboSoftware.setMinimumSize(new Dimension(150, 20));
		cboSoftware.setActionCommand("software");
		panel_4.add(cboSoftware, "cell 2 1,alignx left");
				
		lblFolderShp = new JLabel(BUNDLE.getString("EpaPanel.lblFolderData.text")); //$NON-NLS-1$
		panel_4.add(lblFolderShp, "cell 1 2");
		
		scrollPane_4 = new JScrollPane();
		panel_4.add(scrollPane_4, "cell 2 2,grow");
		
		txtInput = new JTextArea();
		scrollPane_4.setViewportView(txtInput);
		txtInput.setText("");
		txtInput.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnFolderShp = new JButton();
		btnFolderShp.setText("...");
		btnFolderShp.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFolderShp.setActionCommand("chooseFolderShp");
		panel_4.add(btnFolderShp, "cell 3 2");

		lblSchema = new JLabel(BUNDLE.getString("Form.lblSelectSchema.text")); //$NON-NLS-1$
		lblSchema.setEnabled(false);
		panel_4.add(lblSchema, "cell 1 3");

		cboSchema = new JComboBox<String>();
		cboSchema.setEnabled(false);
		cboSchema.setMaximumSize(new Dimension(110, 20));
		cboSchema.setPreferredSize(new Dimension(24, 20));
		cboSchema.setActionCommand("schemaChanged");
		cboSchema.setMinimumSize(new Dimension(110, 20));
		panel_4.add(cboSchema, "flowx,cell 2 3,alignx left");
		
		btnDeleteData = new JButton(BUNDLE.getString("EpaPanel.btnDeleteData.text")); //$NON-NLS-1$
		btnDeleteData.setPreferredSize(new Dimension(110, 23));
		btnDeleteData.setMinimumSize(new Dimension(110, 23));
		btnDeleteData.setMaximumSize(new Dimension(112, 23));
		btnDeleteData.setEnabled(false);
		btnDeleteData.setActionCommand("deleteData");
		panel_4.add(btnDeleteData, "cell 3 3,growx");
		
		lblNewLabel = new JLabel(BUNDLE.getString("EpaPanel.lblNewLabel.text")); //$NON-NLS-1$
		panel_4.add(lblNewLabel, "cell 1 5");
		
		btnSectorSelection = new JButton(BUNDLE.getString("Form.btnCatchSelection.text")); //$NON-NLS-1$
		btnSectorSelection.setMinimumSize(new Dimension(110, 23));
		btnSectorSelection.setPreferredSize(new Dimension(110, 23));
		btnSectorSelection.setEnabled(false);
		btnSectorSelection.setMaximumSize(new Dimension(110, 23));
		btnSectorSelection.setActionCommand("showSectorSelection");
		panel_4.add(btnSectorSelection, "flowx,cell 2 5,alignx right");
		
		btnOptions = new JButton(BUNDLE.getString("Form.btnOptions.text")); //$NON-NLS-1$
		btnOptions.setMaximumSize(new Dimension(110, 23));
		btnOptions.setEnabled(false);
		btnOptions.setMinimumSize(new Dimension(110, 23));
		btnOptions.setPreferredSize(new Dimension(110, 23));
		btnOptions.setActionCommand("showInpOptions");
		panel_4.add(btnOptions, "cell 2 5,alignx left");
		
		btnReport = new JButton(BUNDLE.getString("EpaPanel.btnReport.text")); //$NON-NLS-1$
		btnReport.setPreferredSize(new Dimension(110, 23));
		btnReport.setMinimumSize(new Dimension(110, 23));
		btnReport.setMaximumSize(new Dimension(112, 23));
		btnReport.setEnabled(false);
		btnReport.setActionCommand("showReport");
		panel_4.add(btnReport, "cell 3 5,growx");
		
		chkExport = new JCheckBox();
		chkExport.setText(BUNDLE.getString("EpaPanel.chkExport.text")); //$NON-NLS-1$
		panel_4.add(chkExport, "cell 0 6 2 1");

		JLabel label = new JLabel();
		label.setText(BUNDLE.getString("Form.label.text")); //$NON-NLS-1$
		panel_4.add(label, "cell 1 7");
		
		scrollPane_2 = new JScrollPane();
		panel_4.add(scrollPane_2, "cell 2 7,grow");

		txtFileInp = new JTextArea();
		scrollPane_2.setViewportView(txtFileInp);
		txtFileInp.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtFileInp.setLineWrap(true);

		btnFileInp = new JButton();
		btnFileInp.setActionCommand("chooseFileInp");
		btnFileInp.setText("...");
		btnFileInp.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_4.add(btnFileInp, "cell 3 7,alignx left");

		chkExec = new JCheckBox();
		chkExec.setText(BUNDLE.getString("Form.checkBox_1.text")); //$NON-NLS-1$
		chkExec.setName("chk_exec");
		chkExec.setActionCommand("Exportaci\u00F3n a INP");
		panel_4.add(chkExec, "cell 0 8 3 1,alignx left");

		JLabel label_1 = new JLabel();
		label_1.setText(BUNDLE.getString("Form.label_1.text")); //$NON-NLS-1$
		panel_4.add(label_1, "cell 1 9");
		
		scrollPane_3 = new JScrollPane();
		panel_4.add(scrollPane_3, "cell 2 9,grow");

		txtFileRpt = new JTextArea();
		scrollPane_3.setViewportView(txtFileRpt);
		txtFileRpt.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtFileRpt.setLineWrap(true);

		btnFileRpt = new JButton();
		btnFileRpt.setActionCommand("chooseFileRpt");
		btnFileRpt.setText("...");
		btnFileRpt.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_4.add(btnFileRpt, "cell 3 9,alignx left");

		chkImport = new JCheckBox();
		chkImport.setText(BUNDLE.getString("Form.chkImport.text")); //$NON-NLS-1$
		chkImport.setName("chk_import");
		chkImport.setActionCommand("Exportaci\u00F3n a INP");
		panel_4.add(chkImport, "cell 0 10 2 1");

		JLabel label_2 = new JLabel();
		label_2.setText(BUNDLE.getString("Form.label_2.text")); //$NON-NLS-1$
		label_2.setName("lbl_project");
		panel_4.add(label_2, "cell 1 11");

		txtProject = new JTextField();
		txtProject.setName("txt_project");
		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(16);		
		txtProject.setDocument(maxLength);				
		panel_4.add(txtProject, "cell 2 11,growx,aligny top");

		btnAccept = new JButton();
		btnAccept.setMinimumSize(new Dimension(60, 9));
		btnAccept.setEnabled(false);
		btnAccept.setText(BUNDLE.getString("Form.btnAccept.text")); //$NON-NLS-1$
		btnAccept.setName("btn_accept_postgis");
		btnAccept.setActionCommand("execute");
		panel_4.add(btnAccept, "flowx,cell 2 12,alignx right");
		
		btnCreateSchema = new JButton(BUNDLE.getString("EpaPanel.btnCreateSchema.text")); //$NON-NLS-1$
		btnCreateSchema.setMinimumSize(new Dimension(110, 23));
		btnCreateSchema.setPreferredSize(new Dimension(110, 23));
		btnCreateSchema.setEnabled(false);
		btnCreateSchema.setActionCommand("createSchema");
		panel_4.add(btnCreateSchema, "cell 2 3");
		
		btnDeleteSchema = new JButton(BUNDLE.getString("EpaPanel.btnDeleteSchema.text")); //$NON-NLS-1$
		btnDeleteSchema.setMinimumSize(new Dimension(110, 23));
		btnDeleteSchema.setPreferredSize(new Dimension(110, 23));
		btnDeleteSchema.setMaximumSize(new Dimension(110, 23));
		btnDeleteSchema.setEnabled(false);
		btnDeleteSchema.setActionCommand("deleteSchema");
		panel_4.add(btnDeleteSchema, "cell 2 3");
		
		btnDesign = new JButton(BUNDLE.getString("Form.btnNewButton.text")); //$NON-NLS-1$
		btnDesign.setMinimumSize(new Dimension(110, 23));
		btnDesign.setEnabled(false);
		btnDesign.setPreferredSize(new Dimension(110, 23));
		btnDesign.setMaximumSize(new Dimension(110, 23));
		btnDesign.setActionCommand("showRaingage");
		panel_4.add(btnDesign, "cell 2 5,alignx right,aligny baseline");
		
		btnClose = new JButton();
		btnClose.setMinimumSize(new Dimension(60, 9));
		btnClose.setText(BUNDLE.getString("Generic.btnClose.text")); //$NON-NLS-1$
		btnClose.setActionCommand("closePanel");
		panel_4.add(btnClose, "cell 3 12");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {

		optDbf.addActionListener(this);
		optDatabase.addActionListener(this);
		btnFolderShp.addActionListener(this);
		
		// Panel Postgis
		cboSchema.addActionListener(this);
		btnFileInp.addActionListener(this);
		btnFileRpt.addActionListener(this);
		btnAccept.addActionListener(this);
		btnCreateSchema.addActionListener(this);
		btnDeleteSchema.addActionListener(this);
		btnDeleteData.addActionListener(this);		
		btnOptions.addActionListener(this);
		btnSectorSelection.addActionListener(this);
		btnDesign.addActionListener(this);
		btnReport.addActionListener(this);		
		btnClose.addActionListener(this);		
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
}