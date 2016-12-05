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
	public JCheckBox chkTriggers;
	public JCheckBox chkFunctions;
	public JCheckBox chkViews;
	private JButton btnFilesToDb;
	private JButton btnDbToFiles;
	private JButton btnClose;
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	private JPanel panel;
	private JPanel panelDev;
	
	
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

		setLayout(new MigLayout("", "[75px:n,grow][80px:n][80px:n]", "[20px:n][20px:n][]"));
		
		panelDev = new JPanel();
		panelDev.setBorder(new TitledBorder(null, BUNDLE.getString("DevToolboxPanel.panelTitle.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panelDev, "cell 0 0 3 1,grow");
		panelDev.setLayout(new MigLayout("", "[75px:n,grow][80px:n][]", "[grow]"));
		
		panel = new JPanel();
		panelDev.add(panel, "cell 0 0 3 1,grow");
		panel.setLayout(new MigLayout("", "[][][]", "[][][][][]"));
		
		chkFunctions = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkFunctions.text"));
		panel.add(chkFunctions, "cell 0 0");
		chkFunctions.setSelected(true);
		
		chkTriggers = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkTriggers.text"));
		panel.add(chkTriggers, "cell 0 1");
		chkTriggers.setSelected(true);
		
		chkViews = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkViews.text"));
		panel.add(chkViews, "cell 0 2");
		chkViews.setSelected(true);
		
		btnFilesToDb = new JButton("Files to DB");
		panel.add(btnFilesToDb, "cell 0 4");
		btnFilesToDb.setPreferredSize(new Dimension(100, 23));
		btnFilesToDb.setActionCommand("filesToDb");
		
		btnDbToFiles = new JButton("DB to Files");
		panel.add(btnDbToFiles, "cell 2 4");
		btnDbToFiles.setPreferredSize(new Dimension(100, 23));
		btnDbToFiles.setActionCommand("dbToFiles");
		
		btnClose = new JButton("Close");
		btnClose.setMinimumSize(new Dimension(75, 23));
		btnClose.setActionCommand("closePanel");
		add(btnClose, "cell 2 2");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		btnFilesToDb.addActionListener(this);		
		btnDbToFiles.addActionListener(this);		
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