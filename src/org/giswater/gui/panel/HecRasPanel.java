/*
 * This file is part of gisWater
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
package org.giswater.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.HecRasController;
import org.giswater.util.Utils;


public class HecRasPanel extends JPanel implements ActionListener, FocusListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private HecRasController controller;	
	
	private JFrame f;
	private JButton btnFileAsc;
	private JComboBox<String> cboSchema;
	
	private JPanel panel_2;
	private JTabbedPane tabbedPane;
	private JLabel lblNombre;
	private JPanel panel_3;
	private JLabel lblDataManager;
	private JButton btnClearData;
	private JButton btnLoadRaster;
	private JButton btnExportSdf;
	private JLabel lblAscFile_1;
	private JScrollPane scrollPane;
	private JTextArea txtFileAsc;
	private JPanel panel_4;
	private JLabel lblSchemaManager;
	private JButton btnSaveCase;
	private JButton btnLoadCase;
	private JButton btnDeleteCase;
	private JTextField txtSchemaName;
	private JLabel lblAscFile;
	private JTextArea txtFileSdf;
	private JScrollPane scrollPane_1;
	private JButton btnFileSdf;
	private JButton btnDatabase;

	
	public HecRasPanel() {
		try {
			initConfig();
			enableButtons(false);
		} catch (MissingResourceException e) {
			Utils.showError(e.getMessage(), "", "Error");
			System.exit(ERROR);
		}
	}

	public void setControl(HecRasController nodeController) {
		this.controller = nodeController;
	}

	public JFrame getFrame() {
		return new JFrame();
	}

	public void setFrame(JFrame frame) {
		this.f = frame;
	}

	public JDialog getDialog() {
		return new JDialog();
	}

	
	// Panel Data Manager
	public void enableButtons(boolean isEnabled) {
		btnClearData.setEnabled(isEnabled);
		btnLoadRaster.setEnabled(isEnabled);
		btnExportSdf.setEnabled(isEnabled);
		btnSaveCase.setEnabled(isEnabled);
		btnLoadCase.setEnabled(isEnabled);
		btnDeleteCase.setEnabled(isEnabled);
		btnDatabase.setVisible(!isEnabled);
	}
	
	public void setNewSchemaName(String projectName) {
		txtSchemaName.setText(projectName.trim());
	}
	
	public String getNewSchemaName() {
		return txtSchemaName.getText().trim();
	}
	
//	public void setSchemas(Vector<String> v) {
//		ComboBoxModel<String> cbm = new DefaultComboBoxModel<String>(v);
//		cboSchema.setModel(cbm);
//	}
	
	public void setSchema(Vector<String> v) {
		ComboBoxModel<String> cbm = null;
		if (v != null){
			cbm = new DefaultComboBoxModel<String>(v);
			cboSchema.setModel(cbm);			
		} else{
			DefaultComboBoxModel<String> theModel = (DefaultComboBoxModel<String>) cboSchema.getModel();
			theModel.removeAllElements();
		}
	}
	
	public String getSchema() {
		String elem = "";
		if (cboSchema.getSelectedIndex() != -1) {
			elem = cboSchema.getSelectedItem().toString();
		}
		return elem;
	}

	public String getFileSdf() {
		String file = txtFileSdf.getText().trim();
		return file;
	}	
	
	public void setFileSdf(String path) {
		txtFileSdf.setText(path);
	}
	
	public String getFileAsc() {
		String file = txtFileAsc.getText().trim();
		return file;
	}	
	
	public void setFileAsc(String path) {
		txtFileAsc.setText(path);
	}	

	public String getRasterFile() {
		return null;
	}

	public void focusSchemaName() {
		txtSchemaName.requestFocus();
	}
	
	public void close() {
		f.setVisible(false);
		f.dispose();
	}

	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][:531px:531px][40.00]", "[10px][410.00][12]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		// Panel gisRAS
		panel_2 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Form.panel_3.title"), null, panel_2, null); //$NON-NLS-1$
		panel_2.setLayout(new MigLayout("", "[40px][90.00px][152.00][:114.00:100px][]", "[8px][15][30][30px][50][][10][15][104.00][:5px:5px]"));
		
		lblDataManager = new JLabel(BUNDLE.getString("HecRasPanel.lblDataManager.text")); //$NON-NLS-1$
		lblDataManager.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(lblDataManager, "flowx,cell 0 1 5 1,alignx center");
		
		panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.add(panel_3, "flowx,cell 0 2 5 4,grow");
		panel_3.setLayout(new MigLayout("", "[40px][:100px:100px,grow][:100px:100px][:100px:100px][]", "[25px][4px][45px][45]"));
		
		btnClearData = new JButton(BUNDLE.getString("HecRasPanel.btnClearData.text")); //$NON-NLS-1$
		btnClearData.setMaximumSize(new Dimension(95, 23));
		btnClearData.setMinimumSize(new Dimension(95, 23));
		btnClearData.setActionCommand("clearData");
		panel_3.add(btnClearData, "flowx,cell 1 0,alignx center,aligny center");
		
		btnLoadRaster = new JButton(BUNDLE.getString("HecRasPanel.btnLoadRaster.text"));
		btnLoadRaster.setMaximumSize(new Dimension(95, 23));
		btnLoadRaster.setMinimumSize(new Dimension(95, 23));
		btnLoadRaster.setActionCommand("loadRaster");
		panel_3.add(btnLoadRaster, "cell 2 0,alignx center,aligny center");
		
		btnExportSdf = new JButton(BUNDLE.getString("HecRasPanel.btnExportSdf.text")); //$NON-NLS-1$
		btnExportSdf.setMinimumSize(new Dimension(100, 23));
		btnExportSdf.setMaximumSize(new Dimension(100, 23));
		btnExportSdf.setActionCommand("exportSdf");
		panel_3.add(btnExportSdf, "cell 3 0,alignx center,aligny center");
		
		lblAscFile_1 = new JLabel();
		lblAscFile_1.setText(BUNDLE.getString("HecRasPanel.lblAscFile_1.text")); //$NON-NLS-1$
		panel_3.add(lblAscFile_1, "cell 0 2,alignx right");
				
		scrollPane = new JScrollPane();
		panel_3.add(scrollPane, "cell 1 2 3 1,grow");
		
		txtFileAsc = new JTextArea();
		txtFileAsc.setLineWrap(true);
		txtFileAsc.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(txtFileAsc);

		btnFileAsc = new JButton();
		panel_3.add(btnFileAsc, "cell 4 2");
		btnFileAsc.setActionCommand("chooseFileAsc");
		btnFileAsc.setText("...");
		btnFileAsc.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		lblAscFile = new JLabel();
		lblAscFile.setText(BUNDLE.getString("HecRasPanel.lblAscFile.text")); //$NON-NLS-1$
		panel_3.add(lblAscFile, "cell 0 3");
		
		scrollPane_1 = new JScrollPane();
		panel_3.add(scrollPane_1, "cell 1 3 3 1,grow");
		
		txtFileSdf = new JTextArea();
		scrollPane_1.setViewportView(txtFileSdf);
		txtFileSdf.setLineWrap(true);
		txtFileSdf.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnFileSdf = new JButton();
		btnFileSdf.setText("...");
		btnFileSdf.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFileSdf.setActionCommand("chooseFileSdf");
		panel_3.add(btnFileSdf, "cell 4 3");
		
		lblSchemaManager = new JLabel(BUNDLE.getString("HecRasPanel.lblSchemaManager.text")); //$NON-NLS-1$
		lblSchemaManager.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(lblSchemaManager, "cell 0 7 5 1,alignx center");
		
		panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.add(panel_4, "cell 0 8 5 1,grow");
		panel_4.setLayout(new MigLayout("", "[40px][:100px:100px][:100px:100px][:100px:100px][]", "[25px][4][][]"));
		
		btnSaveCase = new JButton(BUNDLE.getString("HecRasPanel.btnSaveCase.text")); //$NON-NLS-1$
		btnSaveCase.setMaximumSize(new Dimension(95, 23));
		btnSaveCase.setMinimumSize(new Dimension(95, 23));
		btnSaveCase.setActionCommand("saveCase");
		panel_4.add(btnSaveCase, "cell 1 0,alignx center,aligny center");
		
		lblNombre = new JLabel(BUNDLE.getString("HecRasPanel.lblNombre.text"));
		panel_4.add(lblNombre, "cell 2 0,alignx right");
		
		txtSchemaName = new JTextField();
		txtSchemaName.setText("schema_name");
		txtSchemaName.setColumns(10);
		panel_4.add(txtSchemaName, "cell 3 0 2 1,growx");
		
		btnLoadCase = new JButton(BUNDLE.getString("HecRasPanel.btnLoadCase.text"));
		panel_4.add(btnLoadCase, "cell 1 2,alignx center,aligny center");
		btnLoadCase.setMaximumSize(new Dimension(95, 23));
		btnLoadCase.setMinimumSize(new Dimension(95, 23));
		btnLoadCase.setActionCommand("loadCase");
		
		JLabel lblSelectSchema = new JLabel(BUNDLE.getString("HecRasPanel.lblSelectSchema.text"));
		panel_4.add(lblSelectSchema, "cell 2 2 1 2,alignx right");
		
		cboSchema = new JComboBox<String>();
		panel_4.add(cboSchema, "cell 3 2 2 2,growx");
		cboSchema.setPreferredSize(new Dimension(24, 20));
		cboSchema.setActionCommand("schemaChanged");
		cboSchema.setMinimumSize(new Dimension(150, 20));
		
		btnDeleteCase = new JButton(BUNDLE.getString("HecRasPanel.btnDeleteCase.text"));
		panel_4.add(btnDeleteCase, "cell 1 3,alignx center,aligny center");
		btnDeleteCase.setMaximumSize(new Dimension(95, 23));
		btnDeleteCase.setMinimumSize(new Dimension(100, 23));
		btnDeleteCase.setActionCommand("deleteCase");
		
		btnDatabase = new JButton(BUNDLE.getString("HecRasPanel.btnDatabase.text")); //$NON-NLS-1$
		btnDatabase.setMinimumSize(new Dimension(95, 23));
		btnDatabase.setMaximumSize(new Dimension(140, 23));
		btnDatabase.setActionCommand("openDatabase");
		btnDatabase.setVisible(false);
		panel_2.add(btnDatabase, "cell 3 1 2 1");

		enableButtons(false);
		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		
		// Panel Data Manager
		btnClearData.addActionListener(this);
		btnLoadRaster.addActionListener(this);
		btnExportSdf.addActionListener(this);
		btnFileAsc.addActionListener(this);
		btnFileSdf.addActionListener(this);
		btnSaveCase.addActionListener(this);
		btnLoadCase.addActionListener(this);
		btnDeleteCase.addActionListener(this);
		cboSchema.addActionListener(this);
		btnDatabase.addActionListener(this);

		tabbedPane.addFocusListener(this);

	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	@Override
	public void focusGained(FocusEvent e) {
		controller.isConnected();
	}

	@Override
	public void focusLost(FocusEvent arg0) { }

	
}