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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.DevToolboxController;
import org.giswater.gui.frame.DevToolboxFrame;
import org.giswater.util.Utils;
import javax.swing.border.TitledBorder;


public class DevToolboxPanel extends JPanel implements ActionListener {

	private DevToolboxController controller;	
	private DevToolboxFrame frame;	
	private JPanel panelOptions;
	private JPanel panelOptions_2;
	public JCheckBox chkFunctions;
	public JCheckBox chkTriggers;
	public JCheckBox chkFk;
	public JCheckBox chkViews;
	public JCheckBox chkRules;
	public JCheckBox chkValueDefault;
	private JButton btnFilesToDb;
	private JButton btnDbToFiles;
	
	private JPanel panelCustomOptions;
	private JPanel panelCustomOptions_2;
	public JCheckBox chkCustomFunctions;
	public JCheckBox chkCustomTriggers;
	public JCheckBox chkCustomFk;
	public JCheckBox chkCustomViews;
	public JCheckBox chkCustomRules;
	public JCheckBox chkCustomValueDefault;
	private JButton btnCustomFilesToDb;
	private JButton btnCustomDbToFiles;
	private JButton btnClose;
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	
	
	public DevToolboxPanel() {
		try {
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
		}		
	}
	
	public DevToolboxFrame getFrame() {
		return frame;
	}
	
	public void setFrame(DevToolboxFrame frame) {
		this.frame = frame;
	}
	
	public void setController(DevToolboxController controller) {
		this.controller = controller;
	}
	
	public void setAutoConnect(String isChecked) {
		Boolean connect = Boolean.parseBoolean(isChecked);
		chkTriggers.setSelected(connect);
	}	

	public Boolean getAutoConnect() {
		return chkTriggers.isSelected();
	}	
	
	public void setAutoStart(String isChecked) {
		Boolean connect = Boolean.parseBoolean(isChecked);
		chkFunctions.setSelected(connect);
	}	
	
	public Boolean getAutoStart() {
		return chkFunctions.isSelected();
	}		
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[75px:n,grow][80px:n][77px:n]", "[10px:n][20px:n][10px:n][136.00][20px:n][]"));
		
		panelOptions = new JPanel();
		panelOptions.setBorder(new TitledBorder(null, BUNDLE.getString("DevToolboxPanel.panelTitle.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panelOptions, "cell 0 1 3 1,grow");
		panelOptions.setLayout(new MigLayout("", "[75px:n,grow][80px:n][]", "[grow]"));
		
		panelOptions_2 = new JPanel();
		panelOptions.add(panelOptions_2, "cell 0 0 3 1,grow");
		panelOptions_2.setLayout(new MigLayout("", "[][10px:n][][10px:n][]", "[][][10px:n][]"));
		
		chkFunctions = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkFunctions.text"));
		panelOptions_2.add(chkFunctions, "cell 0 0");
		chkFunctions.setSelected(true);
		
		chkFk = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkFk.text")); //$NON-NLS-1$
		chkFk.setSelected(true);
		panelOptions_2.add(chkFk, "cell 2 0");
		
		chkRules = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkRules.text")); //$NON-NLS-1$
		chkRules.setSelected(true);
		panelOptions_2.add(chkRules, "cell 4 0");
		
		chkTriggers = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkTriggers.text"));
		panelOptions_2.add(chkTriggers, "cell 0 1");
		chkTriggers.setSelected(true);
		
		chkViews = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkViews.text"));
		panelOptions_2.add(chkViews, "cell 2 1");
		chkViews.setSelected(true);
		
		chkValueDefault = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkValueDefault.text")); //$NON-NLS-1$
		chkValueDefault.setSelected(true);
		panelOptions_2.add(chkValueDefault, "cell 4 1");
		
		btnFilesToDb = new JButton("Files to DB");
		panelOptions_2.add(btnFilesToDb, "cell 0 3");
		btnFilesToDb.setPreferredSize(new Dimension(100, 23));
		btnFilesToDb.setActionCommand("filesToDb");
		
		btnDbToFiles = new JButton("DB to Files");
		panelOptions_2.add(btnDbToFiles, "cell 2 3");
		btnDbToFiles.setPreferredSize(new Dimension(100, 23));
		btnDbToFiles.setActionCommand("dbToFiles");
		
		panelCustomOptions = new JPanel();
		panelCustomOptions.setBorder(new TitledBorder(null, BUNDLE.getString("DevToolboxPanel.panelCustomDev.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panelCustomOptions, "cell 0 3 3 1,grow");
		panelCustomOptions.setLayout(new MigLayout("", "[grow]", "[grow][]"));
		
		panelCustomOptions_2 = new JPanel();
		panelCustomOptions.add(panelCustomOptions_2, "cell 0 0,grow");
		panelCustomOptions_2.setLayout(new MigLayout("", "[][10px:n][][10px:n][]", "[][][10px:n][]"));
		
		chkCustomFunctions = new JCheckBox("Functions");
		chkCustomFunctions.setSelected(true);
		panelCustomOptions_2.add(chkCustomFunctions, "cell 0 0");
		
		chkCustomFk = new JCheckBox("Foreign Keys");
		chkCustomFk.setSelected(true);
		panelCustomOptions_2.add(chkCustomFk, "cell 2 0");
		
		chkCustomRules = new JCheckBox("Rules");
		chkCustomRules.setSelected(true);
		panelCustomOptions_2.add(chkCustomRules, "cell 4 0");
		
		chkCustomTriggers = new JCheckBox("Triggers");
		chkCustomTriggers.setSelected(true);
		panelCustomOptions_2.add(chkCustomTriggers, "cell 0 1");
		
		chkCustomViews = new JCheckBox("Views");
		chkCustomViews.setSelected(true);
		panelCustomOptions_2.add(chkCustomViews, "cell 2 1");
		
		chkCustomValueDefault = new JCheckBox("Value Default");
		chkCustomValueDefault.setSelected(true);
		panelCustomOptions_2.add(chkCustomValueDefault, "cell 4 1");
		
		btnCustomFilesToDb = new JButton("Files to DB");
		btnCustomFilesToDb.setPreferredSize(new Dimension(100, 23));
		btnCustomFilesToDb.setActionCommand(BUNDLE.getString("DevToolboxPanel.btnCustomFilesToDb.actionCommand")); //$NON-NLS-1$
		panelCustomOptions_2.add(btnCustomFilesToDb, "cell 0 3");
		
		btnCustomDbToFiles = new JButton("DB to Files");
		btnCustomDbToFiles.setVisible(false);
		btnCustomDbToFiles.setPreferredSize(new Dimension(100, 23));
		btnCustomDbToFiles.setActionCommand("dbToFiles");
		panelCustomOptions_2.add(btnCustomDbToFiles, "cell 2 3");
		
		btnClose = new JButton("Close");
		btnClose.setMinimumSize(new Dimension(75, 23));
		btnClose.setActionCommand("closePanel");
		add(btnClose, "cell 2 5");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		btnFilesToDb.addActionListener(this);		
		btnDbToFiles.addActionListener(this);		
		btnCustomFilesToDb.addActionListener(this);		
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