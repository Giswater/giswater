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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.ProjectManagerController;
import org.giswater.gui.frame.ProjectManagerFrame;
import org.giswater.util.Utils;


public class ProjectManagerPanel extends JPanel implements ActionListener {

	private ProjectManagerController controller;
	private ProjectManagerFrame projectManagerFrame;	
	
	private JPanel panelConfiguration;
	
	private JPanel panelAnalysis;
	private JButton btnResultCatalog;
	private JButton btnResultSelector;
	private JButton btnGoToEpa;
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 	
	private static final Font FONT_PANEL_TITLE = new Font("Tahoma", Font.PLAIN, 11);
	private JButton btnProjectPreferences;
	private JLabel lblSnappingTolerance;
	private JTextField txtSnappingTolerance;
	private JLabel lblNodeTolerance;
	private JTextField txtNodeTolerance;
	private JPanel panelDataManager;
	private JButton btnMaterialCatalog;
	private JButton btnNodeCatalog;
	private JButton btnArcCatalog;
	private JButton btnTableWizard;
	private JButton btnUndoWizard;
	private JButton btnSyncWizard;
	private JButton btnNodeFlowTrace;
	private JButton btnArcFlowTrace;
	private JButton btnCreateGisProject;

	
	public ProjectManagerPanel() {
		try {
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
			System.exit(ERROR);
		}
	}
	

	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[130px:n][120px:n][120px:n][::8px]", "[][3px][][3px][88.00][3px:n][][]"));
		
		btnCreateGisProject = new JButton(BUNDLE.getString("ProjectManagerPanel.btnCrea.text")); //$NON-NLS-1$
		add(btnCreateGisProject, "cell 0 0");
		btnCreateGisProject.setPreferredSize(new Dimension(121, 23));
		btnCreateGisProject.setEnabled(false);
		btnCreateGisProject.setActionCommand((String) null);
		
		panelConfiguration = new JPanel();
		panelConfiguration.setBorder(new TitledBorder(null, BUNDLE.getString("ProjectManager.panelConfiguration.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelConfiguration, "cell 0 2 4 1,grow");
		panelConfiguration.setLayout(new MigLayout("", "[120px:n][60.00px:n][][5px:n]", "[][]"));
		
		lblSnappingTolerance = new JLabel();
		lblSnappingTolerance.setText(BUNDLE.getString("ProjectManager.lblSnappingTolerance.text")); //$NON-NLS-1$
		lblSnappingTolerance.setName("lbl_project");
		panelConfiguration.add(lblSnappingTolerance, "cell 0 0,alignx trailing");
		
		txtSnappingTolerance = new JTextField();
		txtSnappingTolerance.setName("txt_project");
		panelConfiguration.add(txtSnappingTolerance, "cell 1 0,growx");
		
		lblNodeTolerance = new JLabel();
		lblNodeTolerance.setText(BUNDLE.getString("ProjectManager.lblNodeTolerance.text")); //$NON-NLS-1$
		lblNodeTolerance.setName("lbl_project");
		panelConfiguration.add(lblNodeTolerance, "cell 0 1,alignx trailing");
		
		txtNodeTolerance = new JTextField();
		txtNodeTolerance.setName("txt_project");
		panelConfiguration.add(txtNodeTolerance, "cell 1 1,growx");
//		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(16);
		
		panelDataManager = new JPanel();
		panelDataManager.setBorder(new TitledBorder(null, BUNDLE.getString("ProjectManager.panelDataManager.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelDataManager, "cell 0 4 4 1,grow");
		panelDataManager.setLayout(new MigLayout("", "[120.00:n][120.00:n][120.00:n]", "[][]"));
		
		btnMaterialCatalog = new JButton(BUNDLE.getString("ProjectManager.btnMaterialCatalog.text")); //$NON-NLS-1$
		btnMaterialCatalog.setPreferredSize(new Dimension(121, 23));
		btnMaterialCatalog.setEnabled(false);
		btnMaterialCatalog.setActionCommand(BUNDLE.getString("ProjectManager.button_1.actionCommand")); //$NON-NLS-1$
		panelDataManager.add(btnMaterialCatalog, "cell 0 0");
		
		btnNodeCatalog = new JButton(BUNDLE.getString("ProjectManager.btnNodeCatalog.text")); //$NON-NLS-1$
		btnNodeCatalog.setPreferredSize(new Dimension(121, 23));
		btnNodeCatalog.setEnabled(false);
		btnNodeCatalog.setActionCommand(BUNDLE.getString("ProjectManager.btnNodeCatalog.actionCommand")); //$NON-NLS-1$
		panelDataManager.add(btnNodeCatalog, "cell 1 0");
		
		btnArcCatalog = new JButton(BUNDLE.getString("ProjectManager.btnArcCatalog.text")); //$NON-NLS-1$
		btnArcCatalog.setPreferredSize(new Dimension(121, 23));
		btnArcCatalog.setEnabled(false);
		btnArcCatalog.setActionCommand(BUNDLE.getString("ProjectManager.btnArcCatalog.actionCommand")); //$NON-NLS-1$
		panelDataManager.add(btnArcCatalog, "cell 2 0");
		
		btnTableWizard = new JButton(BUNDLE.getString("ProjectManager.btnTableWizard.text")); //$NON-NLS-1$
		btnTableWizard.setPreferredSize(new Dimension(121, 23));
		btnTableWizard.setEnabled(false);
		btnTableWizard.setActionCommand("scenarioCatalog");
		panelDataManager.add(btnTableWizard, "cell 0 1");
		
		btnUndoWizard = new JButton(BUNDLE.getString("ProjectManager.btnUndoWizard.text")); //$NON-NLS-1$
		btnUndoWizard.setPreferredSize(new Dimension(121, 23));
		btnUndoWizard.setEnabled(false);
		btnUndoWizard.setActionCommand("scenarioCatalog");
		panelDataManager.add(btnUndoWizard, "cell 1 1");
		
		btnSyncWizard = new JButton(BUNDLE.getString("ProjectManager.btnSyncWizard.text")); //$NON-NLS-1$
		btnSyncWizard.setPreferredSize(new Dimension(121, 23));
		btnSyncWizard.setEnabled(false);
		btnSyncWizard.setActionCommand("scenarioCatalog");
		panelDataManager.add(btnSyncWizard, "cell 2 1");
		
		panelAnalysis = new JPanel();
		panelAnalysis.setBorder(new TitledBorder(null, BUNDLE.getString("ProjectManager.panelAnalysis.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, FONT_PANEL_TITLE, null));
		add(panelAnalysis, "cell 0 6 4 1,grow");
		panelAnalysis.setLayout(new MigLayout("", "[120px:n][120px:n][]", "[][]"));
		
		btnResultCatalog = new JButton(BUNDLE.getString("EpaSoftPanel.btnResultCatalog.text")); 
		btnResultCatalog.setPreferredSize(new Dimension(121, 23));
		btnResultCatalog.setEnabled(false);
		btnResultCatalog.setActionCommand("scenarioCatalog");
		panelAnalysis.add(btnResultCatalog, "cell 0 0");
		
		btnResultSelector = new JButton(BUNDLE.getString("EpaSoftPanel.btnResultSelector.text")); 
		btnResultSelector.setPreferredSize(new Dimension(121, 23));
		btnResultSelector.setEnabled(false);
		btnResultSelector.setActionCommand("scenarioManagement");
		panelAnalysis.add(btnResultSelector, "cell 1 0");
		
		btnNodeFlowTrace = new JButton(BUNDLE.getString("ProjectManager.btnNodeFlowTrace.text")); //$NON-NLS-1$
		btnNodeFlowTrace.setPreferredSize(new Dimension(121, 23));
		btnNodeFlowTrace.setEnabled(false);
		btnNodeFlowTrace.setActionCommand("nodeFlowTrace");
		panelAnalysis.add(btnNodeFlowTrace, "cell 0 1");
		
		btnArcFlowTrace = new JButton(BUNDLE.getString("ProjectManager.btnArcFlowTrace.text")); //$NON-NLS-1$
		btnArcFlowTrace.setPreferredSize(new Dimension(121, 23));
		btnArcFlowTrace.setEnabled(false);
		btnArcFlowTrace.setActionCommand("arcFlowTrace");
		panelAnalysis.add(btnArcFlowTrace, "cell 1 1");
		
		btnProjectPreferences = new JButton("Project Preferences");
		btnProjectPreferences.setMinimumSize(new Dimension(110, 23));
		btnProjectPreferences.setActionCommand("gswEdit");
		add(btnProjectPreferences, "flowx,cell 1 7,alignx right");
		
		btnGoToEpa = new JButton("Go 2 Epa");
		btnGoToEpa.setActionCommand("goToEpa");
		btnGoToEpa.setMinimumSize(new Dimension(120, 23));
		add(btnGoToEpa, "cell 2 7,alignx right");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		
		// Postprocess options
		btnResultCatalog.addActionListener(this);
		btnResultSelector.addActionListener(this);
		
		btnCreateGisProject.addActionListener(this);		
		btnGoToEpa.addActionListener(this);
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
	public void setFrame(ProjectManagerFrame projectManagerFrame) {
		this.projectManagerFrame = projectManagerFrame;
	}	
	
	public ProjectManagerFrame getFrame(){
		return projectManagerFrame;
	}	

	public void setController(ProjectManagerController controller) {
		this.controller = controller;
	}

	public ProjectManagerController getController() {
		return controller;
	}
    
	public void enableDatabaseButtons(boolean enable) {
    	enableResultCatalog(enable);
    	enableResultSelector(enable);
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

	
}