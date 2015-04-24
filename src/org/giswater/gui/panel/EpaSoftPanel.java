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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.EpaSoftController;
import org.giswater.gui.frame.EpaSoftFrame;
import org.giswater.util.MaxLengthTextDocument;
import org.giswater.util.Utils;


public class EpaSoftPanel extends JPanel implements ActionListener {

	private EpaSoftController controller;
	private EpaSoftFrame epaSoftFrame;	

	private JPanel panelDataManager;
	private JButton btnMaterialCatalog;
	private JButton btnCurves;
	private JButton btnPatterns;
	private JButton btnTimeseries;
	private JButton btnArcCatalog;
	private JButton btnHydrologyCatalog;
	private JButton btnControls;
	private JButton btnProjectData;
	
	private JPanel panelPreprocess;
	private JButton btnSectorSelection;
	private JButton btnOptions;
	private JButton btnDesign;
	private JButton btnReport;
	
	private JPanel panelFileManager;
	private JTextField txtResultName;
	private JTextArea txtFileRpt;
	private JTextArea txtFileInp;
	private JButton btnFileInp;
	private JButton btnFileRpt;
	private JButton btnAccept;
	private JCheckBox chkExec;
	private JCheckBox chkImport;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JCheckBox chkExport;
	
	private JPanel panelPostprocess;
	private JButton btnResultCatalog;
	private JButton btnResultSelector;
	private JLabel lblFileRpt;
	private JLabel lblResultName;
	private JCheckBox chkSubcatchments;
	private JButton btnEditProjectPreferences;
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 	
	private static final Font FONT_PANEL_TITLE = new Font("Tahoma", Font.PLAIN, 11);

	
	public EpaSoftPanel() {
		try {
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
			System.exit(ERROR);
		}
	}
	

	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[575.00px][::8px]", "[5px:n][87.00][3px:n][][3px:n][][3px:n][][]"));
		
		panelDataManager = new JPanel();
		panelDataManager.setBorder(new TitledBorder(null, BUNDLE.getString("EpaSoftPanel.panelDataManager.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelDataManager, "cell 0 1 2 1,grow");
		panelDataManager.setLayout(new MigLayout("", "[150px][150][150][150px][]", "[][]"));
		
		btnMaterialCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnMaterialCatalog.text"));
		btnMaterialCatalog.setEnabled(false);
		btnMaterialCatalog.setActionCommand("showMaterialCatalog");
		panelDataManager.add(btnMaterialCatalog, "cell 0 0,growx");
		
		btnCurves = new JButton(BUNDLE.getString("EpaSoftPanel.btnCurves.text")); 
		btnCurves.setEnabled(false);
		btnCurves.setActionCommand("showCurves");
		panelDataManager.add(btnCurves, "cell 1 0,growx");
		
		btnPatterns = new JButton(BUNDLE.getString("EpaSoftPanel.btnPatterns.text")); 
		btnPatterns.setEnabled(false);
		btnPatterns.setActionCommand("showPatterns");
		panelDataManager.add(btnPatterns, "cell 2 0,growx");
		
		btnTimeseries = new JButton(BUNDLE.getString("EpaSoftPanel.btnTimeseries.text")); 
		btnTimeseries.setEnabled(false);
		btnTimeseries.setActionCommand("showTimeseries");
		panelDataManager.add(btnTimeseries, "cell 3 0,growx");
		
		btnArcCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnArcCatalog.text")); 
		btnArcCatalog.setEnabled(false);
		btnArcCatalog.setActionCommand("showArcCatalog");
		panelDataManager.add(btnArcCatalog, "cell 0 1,growx");
		
		btnHydrologyCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnSimulationCatalog.text"));
		btnHydrologyCatalog.setMinimumSize(new Dimension(120, 23));
		btnHydrologyCatalog.setPreferredSize(new Dimension(110, 23));
		btnHydrologyCatalog.setEnabled(false);
		btnHydrologyCatalog.setActionCommand("showHydrologyCatalog");
		panelDataManager.add(btnHydrologyCatalog, "cell 1 1,growx");
		
		btnControls = new JButton(BUNDLE.getString("EpaSoftPanel.btnControls.text")); //$NON-NLS-1$
		btnControls.setPreferredSize(new Dimension(110, 23));
		btnControls.setEnabled(false);
		btnControls.setActionCommand("showControls");
		panelDataManager.add(btnControls, "cell 2 1,growx");
		
		btnProjectData = new JButton(BUNDLE.getString("EpaSoftPanel.btnProjectData.text")); //$NON-NLS-1$
		btnProjectData.setPreferredSize(new Dimension(110, 23));
		btnProjectData.setEnabled(false);
		btnProjectData.setActionCommand("showProjectData");
		panelDataManager.add(btnProjectData, "cell 3 1,growx");
		
		panelPreprocess = new JPanel();
		panelPreprocess.setBorder(new TitledBorder(null, BUNDLE.getString("EpaSoftPanel.panelPreprocess.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelPreprocess, "cell 0 3 2 1,grow");
		panelPreprocess.setLayout(new MigLayout("", "[120px:n][120px:n][120px:n][121px:n]", "[]"));
		
		btnSectorSelection = new JButton(BUNDLE.getString("EpaSoftPanel.btnSectorSelection.text")); //$NON-NLS-1$
		btnSectorSelection.setEnabled(false);
		btnSectorSelection.setActionCommand("showSectorSelection"); 
		panelPreprocess.add(btnSectorSelection, "cell 0 0,alignx center");
		
		btnOptions = new JButton(BUNDLE.getString("EpaSoftPanel.btnOptions.text")); //$NON-NLS-1$
		btnOptions.setEnabled(false);
		btnOptions.setActionCommand("showInpOptions");
		panelPreprocess.add(btnOptions, "cell 1 0,growx");
		
		btnDesign = new JButton(BUNDLE.getString("EpaSoftPanel.btnDesign.text")); //$NON-NLS-1$
		btnDesign.setEnabled(false);
		btnDesign.setActionCommand("showRaingage"); 
		panelPreprocess.add(btnDesign, "cell 2 0,growx");
		
		btnReport = new JButton(BUNDLE.getString("EpaSoftPanel.btnReport.text")); //$NON-NLS-1$
		btnReport.setEnabled(false);
		btnReport.setActionCommand("showReport");
		panelPreprocess.add(btnReport, "cell 3 0,growx");
		
		panelFileManager = new JPanel();
		panelFileManager.setBorder(new TitledBorder(null, BUNDLE.getString("EpaSoftPanel.panelFileManager.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelFileManager, "cell 0 5 2 1,grow");
		panelFileManager.setLayout(new MigLayout("", "[][120.00][::5px][:355px:355px][:65px:65px]", "[::20px][34px:n][20][34px:n][20][][]"));
		
		chkExport = new JCheckBox();
		chkExport.setActionCommand("exportSelected");
		chkExport.setText(BUNDLE.getString("EpaPanel.chkExport.text")); 
		panelFileManager.add(chkExport, "cell 0 0 2 1,aligny bottom");
		
		chkSubcatchments = new JCheckBox();
		chkSubcatchments.setVisible(false);
		chkSubcatchments.setText(BUNDLE.getString("EpaSoftPanel.chkSubcatchments.text")); //$NON-NLS-1$
		panelFileManager.add(chkSubcatchments, "cell 3 0");

		JLabel label = new JLabel();
		label.setText(BUNDLE.getString("Form.label.text")); 
		panelFileManager.add(label, "cell 1 1,alignx right");
		
		scrollPane_2 = new JScrollPane();
		panelFileManager.add(scrollPane_2, "cell 3 1,grow");

		txtFileInp = new JTextArea();
		scrollPane_2.setViewportView(txtFileInp);
		txtFileInp.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtFileInp.setLineWrap(true);

		btnFileInp = new JButton();
		btnFileInp.setMinimumSize(new Dimension(65, 9));
		btnFileInp.setActionCommand("chooseFileInp");
		btnFileInp.setText("...");
		btnFileInp.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelFileManager.add(btnFileInp, "cell 4 1,growx");

		chkExec = new JCheckBox();
		chkExec.setText(BUNDLE.getString("Form.checkBox_1.text"));  //$NON-NLS-1$
		chkExec.setName("chk_exec");
		chkExec.setActionCommand("Exportaci\u00F3n a INP");
		panelFileManager.add(chkExec, "cell 0 2 3 1,alignx left,aligny bottom");

		lblFileRpt = new JLabel();
		lblFileRpt.setText(BUNDLE.getString("Form.label_1.text")); 
		panelFileManager.add(lblFileRpt, "cell 1 3,alignx right");
		
		scrollPane_3 = new JScrollPane();
		panelFileManager.add(scrollPane_3, "cell 3 3,grow");

		txtFileRpt = new JTextArea();
		scrollPane_3.setViewportView(txtFileRpt);
		txtFileRpt.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtFileRpt.setLineWrap(true);

		btnFileRpt = new JButton();
		btnFileRpt.setMinimumSize(new Dimension(65, 9));
		btnFileRpt.setActionCommand("chooseFileRpt");
		btnFileRpt.setText("...");
		btnFileRpt.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelFileManager.add(btnFileRpt, "cell 4 3,growx");

		chkImport = new JCheckBox();
		chkImport.setText(BUNDLE.getString("Form.chkImport.text")); 
		chkImport.setName("chk_import");
		chkImport.setActionCommand("Exportaci\u00F3n a INP");
		panelFileManager.add(chkImport, "cell 0 4 2 1,aligny bottom");

		lblResultName = new JLabel();
		lblResultName.setText(BUNDLE.getString("Form.label_2.text")); 
		lblResultName.setName("lbl_project");
		panelFileManager.add(lblResultName, "cell 1 5,alignx right");

		txtResultName = new JTextField();
		txtResultName.setName("txt_project");
		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(16);		
		txtResultName.setDocument(maxLength);				
		panelFileManager.add(txtResultName, "cell 3 5,growx,aligny top");
		
		btnAccept = new JButton();
		btnAccept.setMinimumSize(new Dimension(65, 23));
		btnAccept.setEnabled(false);
		btnAccept.setText(BUNDLE.getString("Form.btnAccept.text")); 
		btnAccept.setName("btn_accept_postgis");
		btnAccept.setActionCommand("execute");
		panelFileManager.add(btnAccept, "flowx,cell 4 6,alignx right");
		
		panelPostprocess = new JPanel();
		panelPostprocess.setBorder(new TitledBorder(null, BUNDLE.getString("EpaSoftPanel.panelPostprocess.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelPostprocess, "cell 0 7 2 1,grow");
		panelPostprocess.setLayout(new MigLayout("", "[120px:n][120px:n]", "[]"));
		
		btnResultCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnResultCatalog.text")); 
		btnResultCatalog.setPreferredSize(new Dimension(121, 23));
		btnResultCatalog.setEnabled(false);
		btnResultCatalog.setActionCommand("scenarioCatalog");
		panelPostprocess.add(btnResultCatalog, "cell 0 0");
		
		btnResultSelector = new JButton(BUNDLE.getString("EpaSoftPanel.btnResultSelector.text")); 
		btnResultSelector.setPreferredSize(new Dimension(121, 23));
		btnResultSelector.setEnabled(false);
		btnResultSelector.setActionCommand("scenarioManagement");
		panelPostprocess.add(btnResultSelector, "cell 1 0");
		
		btnEditProjectPreferences = new JButton(BUNDLE.getString("EpaSoftPanel.btnEditProjectPreferences.text")); //$NON-NLS-1$
		btnEditProjectPreferences.setActionCommand("gswEdit");
		btnEditProjectPreferences.setMinimumSize(new Dimension(120, 23));
		add(btnEditProjectPreferences, "flowx,cell 0 8,alignx right");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		
		// Data Manager
		btnMaterialCatalog.addActionListener(this);
		btnCurves.addActionListener(this);
		btnPatterns.addActionListener(this);
		btnTimeseries.addActionListener(this);
		btnArcCatalog.addActionListener(this);
		btnHydrologyCatalog.addActionListener(this);
		btnControls.addActionListener(this);
		btnProjectData.addActionListener(this);
		
		// Preprocess options
		btnSectorSelection.addActionListener(this);
		btnOptions.addActionListener(this);
		btnDesign.addActionListener(this);
		btnReport.addActionListener(this);
		
		// File manager
		btnFileInp.addActionListener(this);
		btnFileRpt.addActionListener(this);
		chkExport.addActionListener(this);
		btnAccept.addActionListener(this);
		
		// Postprocess options
		btnResultCatalog.addActionListener(this);
		btnResultSelector.addActionListener(this);
		
		btnEditProjectPreferences.addActionListener(this);
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
	public void setFrame(EpaSoftFrame epaSoftFrame) {
		this.epaSoftFrame = epaSoftFrame;
	}	
	
	public EpaSoftFrame getFrame(){
		return epaSoftFrame;
	}	

	public void setController(EpaSoftController controller) {
		this.controller = controller;
	}

	public EpaSoftController getController() {
		return controller;
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
		txtResultName.setText(projectName);
	}

	public String getProjectName() {
		return txtResultName.getText().trim();
	}
	
	public boolean isSubcatchmentsSelected() {
		return chkSubcatchments.isSelected();
	}
	
	public boolean isExportSelected() {
		return chkExport.isSelected();
	}

	public boolean isExecSelected() {
		return chkExec.isSelected();
	}

	public boolean isImportSelected() {
		return chkImport.isSelected();
	}	
	
	public void enableRunAndImport(boolean enable) {
		chkExec.setEnabled(enable);
		lblFileRpt.setEnabled(enable);
		txtFileRpt.setEnabled(enable);
		btnFileRpt.setEnabled(enable);
		chkImport.setEnabled(enable);
		lblResultName.setEnabled(enable);
		txtResultName.setEnabled(enable);		
	}
	
	public void enableAccept(boolean enable) {
		btnAccept.setEnabled(enable);
	}	
	
	public void setDesignButton(String text, String actionCommand) {
		btnDesign.setText(text);
		btnDesign.setActionCommand(actionCommand);
	}
	
	public void setOptionsButton(String text, String actionCommand) {
		btnOptions.setText(text);
		btnOptions.setActionCommand(actionCommand);
	}	
	
	public void setReportButton(String text, String actionCommand) {
		btnReport.setText(text);
		btnReport.setActionCommand(actionCommand);
	}	
	
    
    public void enableArcCat(boolean enable) {
    	btnArcCatalog.setEnabled(enable);
    }

    public void enableCurves(boolean enable) {
    	btnCurves.setEnabled(enable);
    }

    public void enableMaterials(boolean enable) {
    	btnMaterialCatalog.setEnabled(enable);
    }

    public void enablePatterns(boolean enable) {
    	btnPatterns.setEnabled(enable);
    }
    
	public void enableTimeseries(boolean enable) {
    	btnTimeseries.setEnabled(enable);
    }
    
    public void enableHydrologyCat(boolean enable) {
    	btnHydrologyCatalog.setEnabled(enable);
    }
    
    public void enableControls(boolean enable) {
    	btnControls.setEnabled(enable);
    }
    
    public void enableProjectData(boolean enable) {
    	btnProjectData.setEnabled(enable);
    }
    
	public void setButton4(String text, String actionCommand) {
		btnTimeseries.setText(text);
		btnTimeseries.setActionCommand(actionCommand);
	}	
	
	public void setButton5(String text, String actionCommand) {
		btnArcCatalog.setText(text);
		btnArcCatalog.setActionCommand(actionCommand);
	}	
	
	public void setButton6(String text, String actionCommand) {
		btnHydrologyCatalog.setText(text);
		btnHydrologyCatalog.setActionCommand(actionCommand);
	}	
	
    
	public void enableDatabaseButtons(boolean enable) {
		
    	enablePreprocess(enable);
    	enableMaterials(enable);
    	enableCurves(enable);
    	enablePatterns(enable);
    	enableTimeseries(enable);
    	enableArcCat(enable);
    	enableHydrologyCat(enable);
    	enableControls(enable);
    	enableProjectData(enable);
    	enableResultCatalog(enable);
    	enableResultSelector(enable);
    	
	}
	
	
	public void enablePreprocess(boolean enabled){
		btnSectorSelection.setEnabled(enabled);
		btnOptions.setEnabled(enabled);
		btnDesign.setEnabled(enabled);
		btnReport.setEnabled(enabled);
	}
    
    public void enableResultCatalog(boolean enable) {
    	btnResultCatalog.setEnabled(enable);
    }
    
    public void enableResultSelector(boolean enable) {
    	btnResultSelector.setEnabled(enable);
    }    
    
	public void setTitle(String title) {
		getFrame().setTitle(title);		
	}
	
	public void setSubcatchmentVisible(boolean visible) {
		chkSubcatchments.setVisible(visible);	
	}
	
	public void setSubcatchmentEnabled(boolean enabled) {
		chkSubcatchments.setEnabled(enabled);	
	}
	
	public void setSubcatchmentSelected(boolean selected) {
		chkSubcatchments.setSelected(selected);	
	}
	
	public void exportSelected() {
		controller.exportSelected();
	}

	
	
}