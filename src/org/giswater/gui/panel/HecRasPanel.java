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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.HecRasController;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.frame.HecRasFrame;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class HecRasPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	private static final Font FONT_PANEL_TITLE = new Font("Tahoma", Font.PLAIN, 11);

	private HecRasController controller;	
	private HecRasFrame hecRasFrame;
	private JPanel panelFileManager;
	private JButton btnFileAsc;
	private JButton btnLoadRaster;
	private JButton btnExportSdf;
	private JTextArea txtFileAsc;
	private JCheckBox chkExportSA;
	private JCheckBox chkExportIA;
	private JCheckBox chkExportLevees;
	private JCheckBox chkExportBO;
	private JCheckBox chkExportManning;
	private JTextArea txtFileSdf;
	private JButton btnFileSdf;

	private JPanel panelDataManager;
	private JButton btnLogFile;
	private JButton btnErrorFile;
	private JButton btnShowProjectData;
	private JButton btnExportMdt;
	
	private JButton btnEditProjectPreferences;
	private JButton btnClose;
	
	
	public HecRasPanel() {
		try {
			initConfig();
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
		
		txtFileSdf.setEnabled(isEnabled);	
		btnFileSdf.setEnabled(isEnabled);	
		btnExportSdf.setEnabled(isEnabled);
		
		// Check if we have to enable Load Raster button
		PropertiesMap prop = PropertiesDao.getPropertiesFile();
		boolean isLoad = Boolean.parseBoolean(prop.getProperty("LOAD_RASTER", "false"));
		if (isEnabled && isLoad) {
			btnLoadRaster.setEnabled(true);
			txtFileAsc.setEnabled(true);
			btnFileAsc.setEnabled(true);
		} 
		else {
			btnLoadRaster.setEnabled(false);
			txtFileAsc.setEnabled(false);
			btnFileAsc.setEnabled(false);
		}
		
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

	
	public boolean isMASelected() {
		return chkExportSA.isSelected();
	}
	
	public boolean isIASelected() {
		return chkExportIA.isSelected();
	}
	
	public boolean isLeveesSelected() {
		return chkExportLevees.isSelected();
	}
	
	public boolean isBOSelected() {
		return chkExportBO.isSelected();
	}
	
	public boolean isManningSelected() {
		return chkExportManning.isSelected();
	}
	
	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[:507.00px:531px][10px:n:8px]", "[5px:n][][::50px][10px:n][]"));
		
		panelFileManager = new JPanel();
		panelFileManager.setBorder(new TitledBorder(null, "File manager", TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelFileManager, "cell 0 1 2 1,grow");
		panelFileManager.setLayout(new MigLayout("", "[50px:n][grow][5px:n][][]", "[34px:n][5px:n][][34px:n]"));
		
		JLabel lblAscFile_1 = new JLabel();
		panelFileManager.add(lblAscFile_1, "cell 0 0,alignx right");
		lblAscFile_1.setText(BUNDLE.getString("HecRasPanel.lblAscFile_1.text"));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panelFileManager.add(scrollPane_2, "cell 1 0,grow");
		
		txtFileAsc = new JTextArea();
		scrollPane_2.setViewportView(txtFileAsc);
		txtFileAsc.setLineWrap(true);
		txtFileAsc.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnFileAsc = new JButton();
		btnFileAsc.setMinimumSize(new Dimension(65, 9));
		panelFileManager.add(btnFileAsc, "cell 3 0");
		btnFileAsc.setActionCommand("chooseFileAsc");
		btnFileAsc.setText("...");
		btnFileAsc.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		btnLoadRaster = new JButton(BUNDLE.getString("HecRasPanel.btnLoadRaster.text"));
		btnLoadRaster.setPreferredSize(new Dimension(100, 23));
		panelFileManager.add(btnLoadRaster, "cell 4 0");
		btnLoadRaster.setMaximumSize(new Dimension(105, 23));
		btnLoadRaster.setMinimumSize(new Dimension(85, 23));
		btnLoadRaster.setActionCommand("loadRaster");
		
		chkExportSA = new JCheckBox(BUNDLE.getString("HecRasPanel.chckbxExportSa.text")); //$NON-NLS-1$
		panelFileManager.add(chkExportSA, "flowx,cell 1 2");
		
		JLabel lbloptionalParameters = new JLabel(BUNDLE.getString("HecRasPanel.lbloptionalParameters.text")); //$NON-NLS-1$
		panelFileManager.add(lbloptionalParameters, "cell 3 2 2 1");
		
		JLabel lblAscFile = new JLabel();
		panelFileManager.add(lblAscFile, "cell 0 3,alignx right");
		lblAscFile.setText(BUNDLE.getString("HecRasPanel.lblAscFile.text"));
		
		JScrollPane scrollPane_3 = new JScrollPane();
		panelFileManager.add(scrollPane_3, "cell 1 3,grow");
		
		txtFileSdf = new JTextArea();
		scrollPane_3.setViewportView(txtFileSdf);
		txtFileSdf.setLineWrap(true);
		txtFileSdf.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		btnFileSdf = new JButton();
		btnFileSdf.setMinimumSize(new Dimension(65, 9));
		panelFileManager.add(btnFileSdf, "cell 3 3");
		btnFileSdf.setText("...");
		btnFileSdf.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFileSdf.setActionCommand("chooseFileSdf");
		
		btnExportSdf = new JButton(BUNDLE.getString("HecRasPanel.btnExportSdf.text")); 
		btnExportSdf.setPreferredSize(new Dimension(100, 23));
		panelFileManager.add(btnExportSdf, "cell 4 3");
		btnExportSdf.setMinimumSize(new Dimension(80, 23));
		btnExportSdf.setMaximumSize(new Dimension(105, 23));
		btnExportSdf.setActionCommand("exportSdf");
		
		chkExportIA = new JCheckBox(BUNDLE.getString("HecRasPanel.chckbxExportIa.text")); //$NON-NLS-1$
		panelFileManager.add(chkExportIA, "cell 1 2");
		
		chkExportLevees = new JCheckBox(BUNDLE.getString("HecRasPanel.chckbxExportLevees.text")); //$NON-NLS-1$
		panelFileManager.add(chkExportLevees, "cell 1 2");
		
		chkExportBO = new JCheckBox(BUNDLE.getString("HecRasPanel.chckbxBo.text")); //$NON-NLS-1$
		panelFileManager.add(chkExportBO, "cell 1 2,alignx right,aligny top");
		
		chkExportManning = new JCheckBox(BUNDLE.getString("HecRasPanel.chckbxManning.text")); //$NON-NLS-1$
		panelFileManager.add(chkExportManning, "cell 1 2");
		
		panelDataManager = new JPanel();
		panelDataManager.setBorder(new TitledBorder(null, "Data Manager", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panelDataManager, "cell 0 2 2 1,grow");
		panelDataManager.setLayout(new MigLayout("", "[][][][]", "[]"));
		
		btnLogFile = new JButton(BUNDLE.getString("HecRasPanel.btnNewButton.text")); //$NON-NLS-1$
		btnLogFile.setMinimumSize(new Dimension(120, 23));
		panelDataManager.add(btnLogFile, "cell 0 0");
		
		btnErrorFile = new JButton(BUNDLE.getString("HecRasPanel.btnErrorFile.text")); //$NON-NLS-1$
		btnErrorFile.setMinimumSize(new Dimension(120, 23));
		panelDataManager.add(btnErrorFile, "cell 1 0");
		
		btnShowProjectData = new JButton(BUNDLE.getString("HecRasPanel.btnProjectData.text")); //$NON-NLS-1$
		btnShowProjectData.setActionCommand(BUNDLE.getString("HecRasPanel.btnProjectData.actionCommand")); //$NON-NLS-1$
		btnShowProjectData.setMinimumSize(new Dimension(120, 23));
		panelDataManager.add(btnShowProjectData, "cell 2 0");
		
		btnExportMdt = new JButton(BUNDLE.getString("HecRasPanel.btnExportMdt.text")); //$NON-NLS-1$
		btnExportMdt.setMinimumSize(new Dimension(120, 23));
		panelDataManager.add(btnExportMdt, "cell 3 0");
		
		btnEditProjectPreferences = new JButton("Edit Project Preferences");
		btnEditProjectPreferences.setMinimumSize(new Dimension(120, 23));
		btnEditProjectPreferences.setActionCommand("gswEdit");
		add(btnEditProjectPreferences, "flowx,cell 0 4,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text")); 
		add(btnClose, "cell 0 4,alignx right");
		btnClose.setMinimumSize(new Dimension(65, 23));
		btnClose.setMaximumSize(new Dimension(105, 23));
		btnClose.setActionCommand("closePanel");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		
		btnLoadRaster.addActionListener(this);
		btnExportSdf.addActionListener(this);
		btnFileAsc.addActionListener(this);
		btnFileSdf.addActionListener(this);
		
		// Data Manager
		btnShowProjectData.addActionListener(this);
		
		btnEditProjectPreferences.addActionListener(this);		
		btnClose.addActionListener(this);		

	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
}