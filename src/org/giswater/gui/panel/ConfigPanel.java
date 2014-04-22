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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.ConfigController;
import org.giswater.gui.frame.ConfigFrame;
import org.giswater.util.Utils;


public class ConfigPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private ConfigController controller;	
	private ConfigFrame configFrame;	
	
	private JPanel panel;
	private JCheckBox chkConnect;
	private JTabbedPane tabbedPane;
	private JLabel lblSwmmFolder;
	private JLabel lblEpanetFolder;
	private JTextField txtFileSwmm;
	private JTextField txtFileEpanet;
	private JButton btnFileSwmm;
	private JButton btnFileEpanet;
	private JButton btnAccept;
	private JButton btnClose;
	private JLabel lblNotepad;
	private JLabel lblNotepad2;
	private JLabel lblWindowsGuiSoftware;
	private JLabel lblPgadmin;
	private JTextField txtFilePgAdmin;
	private JButton btnFilePgAdmin;

	
	public ConfigPanel(ConfigFrame configFrame) {
		this.configFrame = configFrame;
		try {
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
			System.exit(ERROR);
		}		
	}
	
	public ConfigFrame getFrame(){
		return configFrame;
	}

	public void setController(ConfigController controller) {
		this.controller = controller;
	}
	
	
	public void setAutoConnect(String isChecked) {
		Boolean connect = Boolean.parseBoolean(isChecked);
		chkConnect.setSelected(connect);
	}	

	public boolean getAutoConnect() {
		return chkConnect.isSelected();
	}	
	
	public void setEpanetFile(String path) {
		txtFileEpanet.setText(path);
	}		
	
	public String getEpanetFile() {
		return txtFileEpanet.getText().trim().toLowerCase();
	}		
	
	public void setSwmmFile(String path) {
		txtFileSwmm.setText(path);
	}		
	
	public String getSwmmFile() {
		return txtFileSwmm.getText().trim().toLowerCase();
	}	

	public void setPgAdminFile(String path) {
		txtFilePgAdmin.setText(path);
	}		
	
	public String getPgAdminFile() {
		return txtFilePgAdmin.getText().trim().toLowerCase();
	}	
	
	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][:531px:531px][40.00]", "[5px][::306.00px]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Config.panel.title"), null, panel_1, null); //$NON-NLS-1$
		panel_1.setLayout(new MigLayout("", "[:96.00:120px][:290:280][]", "[144.00][10px][][][10px:n][25]"));

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.add(panel, "cell 0 0 3 1,grow");
		panel.setLayout(new MigLayout("", "[5][83.00][140,grow][5.00][50.00]", "[][24px:n][24px:n][24px:n][24][10.00]"));
		
		lblWindowsGuiSoftware = new JLabel(BUNDLE.getString("ConfigPanel.lblWindowsGuiSoftware.text")); //$NON-NLS-1$
		lblWindowsGuiSoftware.setVisible(false);
		panel.add(lblWindowsGuiSoftware, "cell 1 0 2 1");
		
		lblSwmmFolder = new JLabel(BUNDLE.getString("Config.lblSwmmFolder"));
		panel.add(lblSwmmFolder, "cell 1 1,alignx trailing");
		
		txtFileSwmm = new JTextField();
		txtFileSwmm.setText((String) null);
		txtFileSwmm.setColumns(10);
		panel.add(txtFileSwmm, "cell 2 1,growx");
		
		btnFileSwmm = new JButton();
		btnFileSwmm.setText("...");
		btnFileSwmm.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFileSwmm.setActionCommand("chooseFileSwmm");
		panel.add(btnFileSwmm, "cell 4 1");
		
		lblEpanetFolder = new JLabel(BUNDLE.getString("Config.lblEpanetFolder"));
		panel.add(lblEpanetFolder, "cell 1 2,alignx trailing");
		
		txtFileEpanet = new JTextField();
		txtFileEpanet.setText((String) null);
		txtFileEpanet.setColumns(10);
		panel.add(txtFileEpanet, "cell 2 2,growx");
		
		btnFileEpanet = new JButton();
		btnFileEpanet.setText("...");
		btnFileEpanet.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFileEpanet.setActionCommand("chooseFileEpanet");
		panel.add(btnFileEpanet, "cell 4 2");
		
		lblPgadmin = new JLabel(BUNDLE.getString("ConfigPanel.lblPgadmin.text")); //$NON-NLS-1$
		panel.add(lblPgadmin, "cell 1 3,alignx trailing");
		
		txtFilePgAdmin = new JTextField();
		txtFilePgAdmin.setText((String) null);
		txtFilePgAdmin.setColumns(10);
		panel.add(txtFilePgAdmin, "cell 2 3,growx");
		
		btnFilePgAdmin = new JButton();
		btnFilePgAdmin.setText("...");
		btnFilePgAdmin.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnFilePgAdmin.setActionCommand(BUNDLE.getString("ConfigPanel.btnFilePgAdmin.actionCommand")); //$NON-NLS-1$
		panel.add(btnFilePgAdmin, "cell 4 3");
		
		chkConnect = new JCheckBox(BUNDLE.getString("Config.chkConnect")); //$NON-NLS-1$
		chkConnect.setSelected(true);
		panel.add(chkConnect, "cell 1 4 2 1,aligny baseline");
		
		lblNotepad = new JLabel(BUNDLE.getString("ConfigPanel.lblNotepad.text")); //$NON-NLS-1$
		panel_1.add(lblNotepad, "cell 0 2 3 1,aligny top");
		
		lblNotepad2 = new JLabel(BUNDLE.getString("ConfigPanel.lblNotepad2.text")); //$NON-NLS-1$
		panel_1.add(lblNotepad2, "cell 0 3");
		
		btnAccept = new JButton(BUNDLE.getString("Form.btnAccept.text")); //$NON-NLS-1$
		btnAccept.setActionCommand("configAccept");
		panel_1.add(btnAccept, "cell 1 5,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text")); //$NON-NLS-1$
		btnClose.setActionCommand("closePanel");
		panel_1.add(btnClose, "cell 2 5");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		
		btnFileSwmm.addActionListener(this);
		btnFileEpanet.addActionListener(this);
		btnFilePgAdmin.addActionListener(this);
		btnAccept.addActionListener(this);
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