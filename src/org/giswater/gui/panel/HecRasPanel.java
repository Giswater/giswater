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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.HecRasController;
import org.giswater.dao.MainDao;
import org.giswater.gui.frame.HecRasFrame;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;
import java.awt.event.FocusAdapter;


public class HecRasPanel extends JPanel implements ActionListener, FocusListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private HecRasController controller;	
	
	private HecRasFrame hecRasFrame;
	private JButton btnFileAsc;
	private JComboBox<String> cboSchema;
	
	private JPanel panel_2;
	private JTabbedPane tabbedPane;
	private JButton btnLoadRaster;
	private JButton btnExportSdf;
	private JLabel lblAscFile_1;
	private JScrollPane scrollPane;
	private JTextArea txtFileAsc;
	private JButton btnCreateSchema;
	private JLabel lblAscFile;
	private JTextArea txtFileSdf;
	private JScrollPane scrollPane_1;
	private JButton btnFileSdf;
	private JButton btnDatabase;
	private JButton btnDeleteSchema;
	private JButton btnClearData;
	private JButton btnClose;

	
	public HecRasPanel() {
		try {
			initConfig();
			enableButtons(false);
		} catch (MissingResourceException e) {
			Utils.showError(e);
			System.exit(ERROR);
		}
	}

	public void setControl(HecRasController nodeController) {
		this.controller = nodeController;
	}

	public HecRasFrame getFrame() {
		return hecRasFrame;
	}

	public void setFrame(HecRasFrame hecRasFrame) {
		this.hecRasFrame = hecRasFrame;
	}

	public JDialog getDialog() {
		return new JDialog();
	}

	
	public void enableButtons(boolean isEnabled) {
		
		btnCreateSchema.setEnabled(isEnabled);
		btnDeleteSchema.setEnabled(isEnabled);
		btnClearData.setEnabled(isEnabled);
		txtFileSdf.setEnabled(isEnabled);	
		btnFileSdf.setEnabled(isEnabled);	
		btnExportSdf.setEnabled(isEnabled);		
		btnDatabase.setVisible(!isEnabled);
		
		// Check if we have to enable Load Raster button
		PropertiesMap prop = MainDao.getPropertiesFile();
		boolean isLoad = Boolean.parseBoolean(prop.getProperty("LOAD_RASTER", "false"));
		if (isEnabled && isLoad){
			btnLoadRaster.setEnabled(true);
			txtFileAsc.setEnabled(true);
			btnFileAsc.setEnabled(true);
		} 
		else{
			btnLoadRaster.setEnabled(false);
			txtFileAsc.setEnabled(false);
			btnFileAsc.setEnabled(false);
		}
		
	}
	
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

	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[5][:572.00px:531px][188.00]", "[10px][][12]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		panel_2 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Form.panel_3.title"), null, panel_2, null); //$NON-NLS-1$
		panel_2.setLayout(new MigLayout("", "[70:n][110:n][150.00][50][45:n][70]", "[15px][8px][10:n][40:n][10px][40:n][:5px:5px][]"));
		
		JLabel lblSelectSchema = new JLabel(BUNDLE.getString("HecRasPanel.lblSelectSchema.text"));
		panel_2.add(lblSelectSchema, "cell 0 1,alignx right");
		
		cboSchema = new JComboBox<String>();
		cboSchema.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				controller.isConnected();
			}
		});
		panel_2.add(cboSchema, "cell 1 1");
		cboSchema.setPreferredSize(new Dimension(24, 20));
		cboSchema.setActionCommand("schemaChanged");
		cboSchema.setMinimumSize(new Dimension(110, 20));
		
		btnCreateSchema = new JButton(BUNDLE.getString("HecRasPanel.btnSaveCase.text")); //$NON-NLS-1$
		panel_2.add(btnCreateSchema, "flowx,cell 2 1");
		btnCreateSchema.setMaximumSize(new Dimension(108, 23));
		btnCreateSchema.setMinimumSize(new Dimension(110, 23));
		btnCreateSchema.setActionCommand("createSchema");
		
		lblAscFile_1 = new JLabel();
		lblAscFile_1.setText(BUNDLE.getString("HecRasPanel.lblAscFile_1.text")); //$NON-NLS-1$
		panel_2.add(lblAscFile_1, "cell 0 3,alignx right");
				
		scrollPane = new JScrollPane();
		panel_2.add(scrollPane, "cell 1 3 2 1,grow");
		
		txtFileAsc = new JTextArea();
		txtFileAsc.setLineWrap(true);
		txtFileAsc.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(txtFileAsc);
		
		btnFileAsc = new JButton();
		panel_2.add(btnFileAsc, "cell 4 3,aligny center");
		btnFileAsc.setActionCommand("chooseFileAsc");
		btnFileAsc.setText("...");
		btnFileAsc.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		lblAscFile = new JLabel();
		lblAscFile.setText(BUNDLE.getString("HecRasPanel.lblAscFile.text")); //$NON-NLS-1$
		panel_2.add(lblAscFile, "cell 0 5,alignx right");
		
		scrollPane_1 = new JScrollPane();
		panel_2.add(scrollPane_1, "cell 1 5 2 1,grow");
		
		txtFileSdf = new JTextArea();
		scrollPane_1.setViewportView(txtFileSdf);
		txtFileSdf.setLineWrap(true);
		txtFileSdf.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnLoadRaster = new JButton(BUNDLE.getString("HecRasPanel.btnLoadRaster.text"));
		btnLoadRaster.setMaximumSize(new Dimension(105, 23));
		btnLoadRaster.setMinimumSize(new Dimension(110, 23));
		btnLoadRaster.setActionCommand("loadRaster");
		panel_2.add(btnLoadRaster, "cell 5 3,alignx center,aligny center");
		
		btnFileSdf = new JButton();
		btnFileSdf.setText("...");
		btnFileSdf.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFileSdf.setActionCommand("chooseFileSdf");
		panel_2.add(btnFileSdf, "cell 4 5,aligny center");
		
		btnExportSdf = new JButton(BUNDLE.getString("HecRasPanel.btnExportSdf.text")); //$NON-NLS-1$
		btnExportSdf.setMinimumSize(new Dimension(110, 23));
		btnExportSdf.setMaximumSize(new Dimension(105, 23));
		btnExportSdf.setActionCommand("exportSdf");
		panel_2.add(btnExportSdf, "cell 5 5,alignx center,aligny center");
		
		btnDeleteSchema = new JButton(BUNDLE.getString("HecRasPanel.btnDeleteSchema.text")); //$NON-NLS-1$
		btnDeleteSchema.setMinimumSize(new Dimension(110, 23));
		btnDeleteSchema.setMaximumSize(new Dimension(108, 23));
		btnDeleteSchema.setActionCommand("deleteSchema");
		panel_2.add(btnDeleteSchema, "cell 2 1");
		
		btnClearData = new JButton("Clear Data");
		btnClearData.setMinimumSize(new Dimension(110, 23));
		btnClearData.setMaximumSize(new Dimension(108, 23));
		btnClearData.setActionCommand("clearData");
		panel_2.add(btnClearData, "flowx,cell 2 1");
		
		btnDatabase = new JButton(BUNDLE.getString("HecRasPanel.btnDatabase.text")); //$NON-NLS-1$
		btnDatabase.setMinimumSize(new Dimension(95, 23));
		btnDatabase.setMaximumSize(new Dimension(140, 23));
		btnDatabase.setActionCommand("openDatabase");
		btnDatabase.setVisible(false);
		panel_2.add(btnDatabase, "cell 2 7,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text")); //$NON-NLS-1$
		btnClose.setMinimumSize(new Dimension(60, 23));
		btnClose.setMaximumSize(new Dimension(105, 23));
		btnClose.setActionCommand("closePanel");
		panel_2.add(btnClose, "cell 4 7");

		enableButtons(false);
		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		
		btnLoadRaster.addActionListener(this);
		btnExportSdf.addActionListener(this);
		btnFileAsc.addActionListener(this);
		btnFileSdf.addActionListener(this);
		
		btnCreateSchema.addActionListener(this);
		btnDeleteSchema.addActionListener(this);		
		btnClearData.addActionListener(this);
		
		cboSchema.addActionListener(this);
		btnDatabase.addActionListener(this);

		tabbedPane.addFocusListener(this);
		
		btnClose.addActionListener(this);		

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