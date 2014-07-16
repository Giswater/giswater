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
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.EpaSoftController;
import org.giswater.gui.frame.EpaFrame;
import org.giswater.util.MaxLengthTextDocument;
import org.giswater.util.Utils;


public class EpaSoftPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private EpaSoftController controller;
	private EpaFrame epaFrame;	

	private JTextField txtProject;
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
	private JPanel panelDataManager;
	
	private static final Font FONT_14 = new Font("Tahoma", Font.BOLD, 14);
	private JButton btnArcCatalog;
	private JButton btnMaterialCatalog;
	private JButton btnTimeseries;
	private JButton btnCurves;
	private JButton btnPatterns;
	private JPanel panelPreprocess;
	private JButton btnSectorSelection;
	private JButton btnOptions;
	private JButton btnDesign;
	private JButton btnReport;
	private JPanel panelFileManager;
	private JPanel panelAnalysis;
	private JButton btnResultCatalog;
	private JButton btnResultSelector;
	private JButton button;
	private JButton btnSimulationCatalog;

	
	public EpaSoftPanel() {
		try {
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

	public void setController(EpaSoftController controller) {
		this.controller = controller;
	}

	public EpaSoftController getController() {
		return controller;
	}
	
	public JDialog getDialog() {
		return new JDialog();
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
	
	
    public void enableProjectId(boolean enable) {
        btnProjectId.setEnabled(enable);
    }
    
    public void enableConduit(boolean enable) {
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
    
    public void enableResultCat(boolean enable) {
    	btnResultCatalog.setEnabled(enable);
    }
    
    public void enableResultSelection(boolean enable) {
    	btnResultSelector.setEnabled(enable);
    }    
    



	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][614.00px,grow]", "[5px][87.00][][][][]"));
		
		panelDataManager = new JPanel();
		panelDataManager.setBorder(new TitledBorder(null, "Data Manager", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		add(panelDataManager, "cell 1 1,grow");
		panelDataManager.setLayout(new MigLayout("", "[][][][][]", "[][]"));
		
		btnMaterialCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnMaterialCatalog.text")); //$NON-NLS-1$
		btnMaterialCatalog.setPreferredSize(new Dimension(120, 23));
		btnMaterialCatalog.setMinimumSize(new Dimension(110, 23));
		btnMaterialCatalog.setMaximumSize(new Dimension(999, 23));
		btnMaterialCatalog.setEnabled(false);
		btnMaterialCatalog.setActionCommand(BUNDLE.getString("EpaSoftPanel.btnMaterialCatalog.actionCommand")); //$NON-NLS-1$
		panelDataManager.add(btnMaterialCatalog, "cell 0 0");
		
		btnCurves = new JButton(BUNDLE.getString("EpaSoftPanel.btnCurves.text")); //$NON-NLS-1$
		btnCurves.setPreferredSize(new Dimension(120, 23));
		btnCurves.setMinimumSize(new Dimension(110, 23));
		btnCurves.setMaximumSize(new Dimension(999, 23));
		btnCurves.setEnabled(false);
		btnCurves.setActionCommand(BUNDLE.getString("EpaSoftPanel.btnCurves.actionCommand")); //$NON-NLS-1$
		panelDataManager.add(btnCurves, "cell 1 0");
		
		btnPatterns = new JButton(BUNDLE.getString("EpaSoftPanel.btnPatterns.text")); //$NON-NLS-1$
		btnPatterns.setPreferredSize(new Dimension(120, 23));
		btnPatterns.setMinimumSize(new Dimension(110, 23));
		btnPatterns.setMaximumSize(new Dimension(999, 23));
		btnPatterns.setEnabled(false);
		btnPatterns.setActionCommand(BUNDLE.getString("EpaSoftPanel.btnPatterns.actionCommand")); //$NON-NLS-1$
		panelDataManager.add(btnPatterns, "cell 2 0");
		
		btnTimeseries = new JButton(BUNDLE.getString("EpaSoftPanel.btnTimeseries.text")); //$NON-NLS-1$
		btnTimeseries.setPreferredSize(new Dimension(120, 23));
		btnTimeseries.setMinimumSize(new Dimension(110, 23));
		btnTimeseries.setMaximumSize(new Dimension(999, 23));
		btnTimeseries.setEnabled(false);
		btnTimeseries.setActionCommand(BUNDLE.getString("EpaSoftPanel.btnTimeseries.actionCommand")); //$NON-NLS-1$
		panelDataManager.add(btnTimeseries, "cell 3 0");
		
		btnArcCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnArcCatalog.text")); //$NON-NLS-1$
		btnArcCatalog.setPreferredSize(new Dimension(120, 23));
		btnArcCatalog.setMinimumSize(new Dimension(110, 23));
		btnArcCatalog.setMaximumSize(new Dimension(999, 23));
		btnArcCatalog.setEnabled(false);
		btnArcCatalog.setActionCommand(BUNDLE.getString("EpaSoftPanel.btnArcCatalog.actionCommand")); //$NON-NLS-1$
		panelDataManager.add(btnArcCatalog, "cell 0 1");
		
		btnSimulationCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnSimulationCatalog.text")); //$NON-NLS-1$
		btnSimulationCatalog.setPreferredSize(new Dimension(120, 23));
		btnSimulationCatalog.setMinimumSize(new Dimension(110, 23));
		btnSimulationCatalog.setMaximumSize(new Dimension(999, 23));
		btnSimulationCatalog.setEnabled(false);
		btnSimulationCatalog.setActionCommand("showArcCatalog");
		panelDataManager.add(btnSimulationCatalog, "cell 1 1");
		
		panelPreprocess = new JPanel();
		panelPreprocess.setBorder(new TitledBorder(null, "Preprocess options", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		add(panelPreprocess, "cell 1 2,grow");
		panelPreprocess.setLayout(new MigLayout("", "[][][][]", "[]"));
		
		btnSectorSelection = new JButton("Sector selection");
		btnSectorSelection.setPreferredSize(new Dimension(120, 23));
		btnSectorSelection.setMinimumSize(new Dimension(110, 23));
		btnSectorSelection.setMaximumSize(new Dimension(999, 23));
		btnSectorSelection.setEnabled(false);
		btnSectorSelection.setActionCommand("showSectorSelection");
		panelPreprocess.add(btnSectorSelection, "cell 0 0");
		
		btnOptions = new JButton("Options");
		btnOptions.setPreferredSize(new Dimension(120, 23));
		btnOptions.setMinimumSize(new Dimension(110, 23));
		btnOptions.setMaximumSize(new Dimension(999, 23));
		btnOptions.setEnabled(false);
		btnOptions.setActionCommand("showInpOptions");
		panelPreprocess.add(btnOptions, "cell 1 0");
		
		btnDesign = new JButton("Design values");
		btnDesign.setPreferredSize(new Dimension(120, 23));
		btnDesign.setMinimumSize(new Dimension(110, 23));
		btnDesign.setMaximumSize(new Dimension(999, 23));
		btnDesign.setEnabled(false);
		btnDesign.setActionCommand("showRaingage");
		panelPreprocess.add(btnDesign, "cell 2 0");
		
		btnReport = new JButton("Report options");
		btnReport.setPreferredSize(new Dimension(120, 23));
		btnReport.setMinimumSize(new Dimension(110, 23));
		btnReport.setMaximumSize(new Dimension(999, 23));
		btnReport.setEnabled(false);
		btnReport.setActionCommand("showReport");
		panelPreprocess.add(btnReport, "cell 3 0");
		
		panelFileManager = new JPanel();
		panelFileManager.setBorder(new TitledBorder(null, "File manager", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		add(panelFileManager, "cell 1 3,grow");
		panelFileManager.setLayout(new MigLayout("", "[][106.00][::5px][grow][]", "[::20px][35px:45px:45px][20][35px:n][20][][]"));
		
		chkExport = new JCheckBox();
		chkExport.setText(BUNDLE.getString("EpaPanel.chkExport.text")); //$NON-NLS-1$
		panelFileManager.add(chkExport, "cell 0 0 4 1,aligny bottom");

		JLabel label = new JLabel();
		label.setText(BUNDLE.getString("Form.label.text")); //$NON-NLS-1$
		panelFileManager.add(label, "cell 1 1,alignx right");
		
		scrollPane_2 = new JScrollPane();
		panelFileManager.add(scrollPane_2, "cell 3 1,grow");

		txtFileInp = new JTextArea();
		scrollPane_2.setViewportView(txtFileInp);
		txtFileInp.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtFileInp.setLineWrap(true);

		btnFileInp = new JButton();
		btnFileInp.setActionCommand("chooseFileInp");
		btnFileInp.setText("...");
		btnFileInp.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelFileManager.add(btnFileInp, "cell 4 1,alignx left");

		chkExec = new JCheckBox();
		chkExec.setText(BUNDLE.getString("Form.checkBox_1.text")); //$NON-NLS-1$
		chkExec.setName("chk_exec");
		chkExec.setActionCommand("Exportaci\u00F3n a INP");
		panelFileManager.add(chkExec, "cell 0 2 4 1,alignx left,aligny bottom");

		JLabel label_1 = new JLabel();
		label_1.setText(BUNDLE.getString("Form.label_1.text")); //$NON-NLS-1$
		panelFileManager.add(label_1, "cell 1 3,alignx right");
		
		scrollPane_3 = new JScrollPane();
		panelFileManager.add(scrollPane_3, "cell 3 3,grow");

		txtFileRpt = new JTextArea();
		scrollPane_3.setViewportView(txtFileRpt);
		txtFileRpt.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtFileRpt.setLineWrap(true);

		btnFileRpt = new JButton();
		btnFileRpt.setActionCommand("chooseFileRpt");
		btnFileRpt.setText("...");
		btnFileRpt.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelFileManager.add(btnFileRpt, "cell 4 3,alignx left");

		chkImport = new JCheckBox();
		chkImport.setText(BUNDLE.getString("Form.chkImport.text")); //$NON-NLS-1$
		chkImport.setName("chk_import");
		chkImport.setActionCommand("Exportaci\u00F3n a INP");
		panelFileManager.add(chkImport, "cell 0 4 4 1,aligny bottom");

		JLabel label_2 = new JLabel();
		label_2.setText(BUNDLE.getString("Form.label_2.text")); //$NON-NLS-1$
		label_2.setName("lbl_project");
		panelFileManager.add(label_2, "cell 1 5,alignx right");

		txtProject = new JTextField();
		txtProject.setName("txt_project");
		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(16);		
		txtProject.setDocument(maxLength);				
		panelFileManager.add(txtProject, "cell 3 5,growx,aligny top");

		btnAccept = new JButton();
		btnAccept.setMinimumSize(new Dimension(80, 23));
		btnAccept.setEnabled(false);
		btnAccept.setText(BUNDLE.getString("Form.btnAccept.text")); //$NON-NLS-1$
		btnAccept.setName("btn_accept_postgis");
		btnAccept.setActionCommand("execute");
		panelFileManager.add(btnAccept, "flowx,cell 3 6,alignx right");
		
		panelAnalysis = new JPanel();
		panelAnalysis.setBorder(new TitledBorder(null, "Post process options", TitledBorder.LEADING, TitledBorder.TOP, FONT_14, null));
		add(panelAnalysis, "cell 1 4,grow");
		panelAnalysis.setLayout(new MigLayout("", "[][]", "[]"));
		
		btnResultCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnResultCatalog.text")); //$NON-NLS-1$
		btnResultCatalog.setPreferredSize(new Dimension(110, 23));
		btnResultCatalog.setMinimumSize(new Dimension(110, 23));
		btnResultCatalog.setMaximumSize(new Dimension(110, 23));
		btnResultCatalog.setEnabled(false);
		btnResultCatalog.setActionCommand(BUNDLE.getString("EpaSoftPanel.btnResultCatalog.actionCommand")); //$NON-NLS-1$
		panelAnalysis.add(btnResultCatalog, "cell 0 0");
		
		btnResultSelector = new JButton(BUNDLE.getString("EpaSoftPanel.btnResultSelector.text")); //$NON-NLS-1$
		btnResultSelector.setPreferredSize(new Dimension(110, 23));
		btnResultSelector.setMinimumSize(new Dimension(110, 23));
		btnResultSelector.setMaximumSize(new Dimension(110, 23));
		btnResultSelector.setEnabled(false);
		btnResultSelector.setActionCommand(BUNDLE.getString("EpaSoftPanel.button_1.actionCommand")); //$NON-NLS-1$
		panelAnalysis.add(btnResultSelector, "cell 1 0");
		
		button = new JButton();
		button.setText("Close");
		button.setMinimumSize(new Dimension(80, 23));
		button.setActionCommand("closePanel");
		add(button, "cell 1 5,alignx right");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		btnFileInp.addActionListener(this);
		btnFileRpt.addActionListener(this);
		btnAccept.addActionListener(this);
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
}