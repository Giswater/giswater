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
	
	private JPanel panelPreprocess;
	private JButton btnDesign;
	private JButton btnOptions;
	private JButton btnStateSelection;
	private JButton btnSectorSelection;
	
	private JPanel panelFileManager;
	private JLabel lblFileRpt;
	private JLabel lblResultName;
	private JCheckBox chkSubcatchments;	
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

	private JButton btnProjectPreferences;
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 	
	private static final Font FONT_PANEL_TITLE = new Font("Tahoma", Font.PLAIN, 11);
	private JLabel lblDummy;

	
	public EpaSoftPanel() {
		try {
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
			System.exit(ERROR);
		}
	}
	

	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[::508px,grow]", "[67px:n][10px:n][][10px:n][]"));
		
		panelPreprocess = new JPanel();
		panelPreprocess.setBorder(new TitledBorder(null, BUNDLE.getString("EpaSoftPanel.panelPreprocess.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelPreprocess, "cell 0 0,grow");
		panelPreprocess.setLayout(new MigLayout("", "[115px:n][115px:n][115px:n][128px:n,grow]", "[]"));
		
		btnSectorSelection = new JButton(BUNDLE.getString("EpaSoftPanel.btnSectorSelection.text")); //$NON-NLS-1$
		btnSectorSelection.setEnabled(false);
		btnSectorSelection.setActionCommand("showSectorSelection");
		panelPreprocess.add(btnSectorSelection, "cell 0 0,growx");
		
		btnStateSelection = new JButton(BUNDLE.getString("EpaSoftPanel.btnStateSelection.text"));
		btnStateSelection.setEnabled(false);
		btnStateSelection.setActionCommand("showStateSelection");
		panelPreprocess.add(btnStateSelection, "cell 1 0,growx");
		
		btnOptions = new JButton(BUNDLE.getString("EpaSoftPanel.btnDesign.text")); //$NON-NLS-1$
		btnOptions.setEnabled(false);
		btnOptions.setActionCommand("showOptions");
		panelPreprocess.add(btnOptions, "flowx,cell 2 0,growx");
		
		btnDesign = new JButton(BUNDLE.getString("EpaSoftPanel.btnRaingage.text")); //$NON-NLS-1$
		btnDesign.setEnabled(false);
		btnDesign.setActionCommand("showRaingage");
		panelPreprocess.add(btnDesign, "cell 3 0,growx");
		
		panelFileManager = new JPanel();
		panelFileManager.setBorder(new TitledBorder(null, BUNDLE.getString("EpaSoftPanel.panelFileManager.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelFileManager, "cell 0 2,grow");
		panelFileManager.setLayout(new MigLayout("", "[][104.00][::3px][228px:n,grow][::3px][65px:n][61px:n]", "[30px:n][24px:n][34px:n][40px:n][34px:n][40px:n][][]"));
		
		chkExport = new JCheckBox();
		chkExport.setToolTipText(BUNDLE.getString("EpaSoftPanel.chkExport.toolTipText")); //$NON-NLS-1$
		chkExport.setActionCommand("exportSelected");
		chkExport.setText(BUNDLE.getString("EpaSoftPanel.chkExport.text")); 
		panelFileManager.add(chkExport, "cell 0 0 2 1,aligny bottom");
		
		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(16);	
		
		chkSubcatchments = new JCheckBox();
		chkSubcatchments.setToolTipText(BUNDLE.getString("EpaSoftPanel.chkSubcatchments.toolTipText")); //$NON-NLS-1$
		chkSubcatchments.setVisible(false);
		chkSubcatchments.setText(BUNDLE.getString("EpaSoftPanel.chkSubcatchments.text")); //$NON-NLS-1$
		panelFileManager.add(chkSubcatchments, "cell 3 0");
				
		lblResultName = new JLabel();
		lblResultName.setText(BUNDLE.getString("Form.label_2.text")); 
		lblResultName.setName("lbl_project");
		panelFileManager.add(lblResultName, "cell 1 1,alignx right");
		
		txtResultName = new JTextField();
		txtResultName.setName("txt_project");
		txtResultName.setDocument(maxLength);				
		panelFileManager.add(txtResultName, "cell 3 1,growx,aligny top");

		JLabel label = new JLabel();
		label.setText(BUNDLE.getString("Form.label.text")); 
		panelFileManager.add(label, "cell 1 2,alignx right");
		
		scrollPane_2 = new JScrollPane();
		panelFileManager.add(scrollPane_2, "cell 3 2,grow");

		txtFileInp = new JTextArea();
		scrollPane_2.setViewportView(txtFileInp);
		txtFileInp.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtFileInp.setLineWrap(true);

		btnFileInp = new JButton();
		btnFileInp.setMinimumSize(new Dimension(65, 9));
		btnFileInp.setActionCommand("chooseFileInp");
		btnFileInp.setText("...");
		btnFileInp.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelFileManager.add(btnFileInp, "cell 5 2,growx");

		chkExec = new JCheckBox();
		chkExec.setToolTipText(BUNDLE.getString("EpaSoftPanel.chkExec.toolTipText")); //$NON-NLS-1$
		chkExec.setText(BUNDLE.getString("EpaSoftPanel.chkExec.text"));  //$NON-NLS-1$
		chkExec.setName("chk_exec");
		panelFileManager.add(chkExec, "cell 0 3 3 1,alignx left,aligny bottom");

		lblFileRpt = new JLabel();
		lblFileRpt.setText(BUNDLE.getString("Form.label_1.text")); 
		panelFileManager.add(lblFileRpt, "cell 1 4,alignx right");
		
		scrollPane_3 = new JScrollPane();
		panelFileManager.add(scrollPane_3, "cell 3 4,grow");

		txtFileRpt = new JTextArea();
		scrollPane_3.setViewportView(txtFileRpt);
		txtFileRpt.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtFileRpt.setLineWrap(true);

		btnFileRpt = new JButton();
		btnFileRpt.setMinimumSize(new Dimension(65, 9));
		btnFileRpt.setActionCommand("chooseFileRpt");
		btnFileRpt.setText("...");
		btnFileRpt.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelFileManager.add(btnFileRpt, "cell 5 4,growx");

		chkImport = new JCheckBox();
		chkImport.setActionCommand("importSelected");
		chkImport.setToolTipText(BUNDLE.getString("EpaSoftPanel.chkImport.toolTipText")); //$NON-NLS-1$
		chkImport.setText(BUNDLE.getString("EpaSoftPanel.chkImport.text")); 
		chkImport.setName("chk_import");
		panelFileManager.add(chkImport, "cell 0 5 2 1,aligny bottom");	
		
		btnAccept = new JButton();
		btnAccept.setPreferredSize(new Dimension(115, 9));
		btnAccept.setMinimumSize(new Dimension(65, 23));
		btnAccept.setEnabled(false);
		btnAccept.setText(BUNDLE.getString("Generic.btnAccept.text")); 
		btnAccept.setName("btn_accept_postgis");
		btnAccept.setActionCommand("execute");
		panelFileManager.add(btnAccept, "flowx,cell 5 7 2 1,growx");
		
		btnProjectPreferences = new JButton("Project Preferences");
		btnProjectPreferences.setMinimumSize(new Dimension(130, 23));
		btnProjectPreferences.setPreferredSize(new Dimension(115, 23));
		btnProjectPreferences.setActionCommand("openProjectPreferences");
		add(btnProjectPreferences, "flowx,cell 0 4,alignx right");
		
		lblDummy = new JLabel("  ");
		add(lblDummy, "cell 0 4");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		
		// Preprocess options		
		btnSectorSelection.addActionListener(this);		
		btnStateSelection.addActionListener(this);		
		btnOptions.addActionListener(this);
		btnDesign.addActionListener(this);
		
		// File manager
		btnFileInp.addActionListener(this);
		btnFileRpt.addActionListener(this);
		chkExport.addActionListener(this);
		chkImport.addActionListener(this);		
		btnAccept.addActionListener(this);
		
		btnProjectPreferences.addActionListener(this);
		
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

	public String getResultName() {
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
	
	public void setSelected(boolean selected) {
		chkExport.setSelected(selected);
		chkExec.setSelected(selected);
		chkExport.setEnabled(!selected);
		chkExec.setEnabled(!selected);
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
	
	
	public void setOptionsButton(String text, String actionCommand) {
		btnOptions.setText(text);
		btnOptions.setActionCommand(actionCommand);
	}	
	
	public void setDesignButton(String text, String actionCommand) {
		btnDesign.setText(text);
		btnDesign.setActionCommand(actionCommand);
	}
    
	public void enableDatabaseButtons(boolean enable) {
    	enablePreprocess(enable);
	}
	
	public void enablePreprocess(boolean enabled){
		btnSectorSelection.setEnabled(enabled);
		btnStateSelection.setEnabled(enabled);
		btnOptions.setEnabled(enabled);
		btnDesign.setEnabled(enabled);
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
	
	public void checkFileManager(boolean selected) {
		chkExport.setSelected(selected);	
		chkExec.setSelected(selected);	
		chkImport.setSelected(selected);		
	}

	
}