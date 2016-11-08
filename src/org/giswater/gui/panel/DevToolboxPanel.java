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

		setLayout(new MigLayout("", "[57.00px:n][75px:n][80px:n][][80px:n]", "[5px:n][20px:n][20px:n][20px:n][20px:n][]"));
		
		chkFunctions = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkFunctions.text")); 
		add(chkFunctions, "cell 1 1 2 1");
		chkFunctions.setSelected(true);
		
		chkTriggers = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkTriggers.text")); 
		add(chkTriggers, "cell 1 2 2 1");
		chkTriggers.setSelected(true);
		
		chkViews = new JCheckBox(BUNDLE.getString("DevToolboxPanel.chkViews.text"));
		chkViews.setSelected(true);
		add(chkViews, "cell 1 3");
		
		btnFilesToDb = new JButton("Files to DB");
		btnFilesToDb.setPreferredSize(new Dimension(100, 23));
		btnFilesToDb.setActionCommand("filesToDb");
		add(btnFilesToDb, "cell 1 5");
		
		btnDbToFiles = new JButton("DB to Files");
		btnDbToFiles.setPreferredSize(new Dimension(100, 23));
		btnDbToFiles.setActionCommand("dbToFiles");
		add(btnDbToFiles, "cell 2 5");
		
		btnClose = new JButton("Close");
		btnClose.setMinimumSize(new Dimension(75, 23));
		btnClose.setActionCommand("closePanel");
		add(btnClose, "cell 4 5");

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