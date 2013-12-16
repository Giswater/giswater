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
 *   David Erill <daviderill79@gmail.com>
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
import javax.swing.JDialog;
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
	
	private JPanel panel;
	private JCheckBox chkConnect;
	private JTabbedPane tabbedPane;
	private JLabel lblSwmmFolder;
	private JLabel lblEpanetFolder;
	private JTextField txtSwmmFolder;
	private JTextField txtEpanetFolder;
	private JButton btnSwmmFolder;
	private JButton btnEpanetFolder;
	private JButton btnAccept;
	private ConfigFrame configFrame;

	
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
	

	public JDialog getDialog() {
		return new JDialog();
	}

	
	public void setAutoConnect(String isChecked) {
		Boolean connect = Boolean.parseBoolean(isChecked);
		chkConnect.setSelected(connect);
	}	

	public boolean getAutoConnect() {
		return chkConnect.isSelected();
	}	
	
	public void setEpanetFile(String path) {
		txtEpanetFolder.setText(path);
	}		
	
	public String getEpanetFile() {
		return txtEpanetFolder.getText().trim().toLowerCase();
	}		
	
	public void setSwmmFile(String path) {
		txtSwmmFolder.setText(path);
	}		
	
	public String getSwmmFile() {
		return txtSwmmFolder.getText().trim().toLowerCase();
	}	

	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][:531px:531px][40.00]", "[5px][::200px]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		// Panel Database connection
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Config.panel.title"), null, panel_1, null); //$NON-NLS-1$
		panel_1.setLayout(new MigLayout("", "[:96.00:120px][:290:280][]", "[100.00][10px][25]"));

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.add(panel, "cell 0 0 3 1,grow");
		panel.setLayout(new MigLayout("", "[5][83.00][140,grow][5.00][50.00]", "[4][24][24][24][10.00]"));
		
		lblSwmmFolder = new JLabel(BUNDLE.getString("Config.lblSwmmFolder"));
		panel.add(lblSwmmFolder, "cell 1 1");
		
		txtSwmmFolder = new JTextField();
		txtSwmmFolder.setText((String) null);
		txtSwmmFolder.setColumns(10);
		panel.add(txtSwmmFolder, "cell 2 1,growx");
		
		btnSwmmFolder = new JButton();
		btnSwmmFolder.setText("...");
		btnSwmmFolder.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSwmmFolder.setActionCommand("chooseFileSwmm");
		panel.add(btnSwmmFolder, "cell 4 1");
		
		lblEpanetFolder = new JLabel(BUNDLE.getString("Config.lblEpanetFolder"));
		panel.add(lblEpanetFolder, "cell 1 2");
		
		txtEpanetFolder = new JTextField();
		txtEpanetFolder.setText((String) null);
		txtEpanetFolder.setColumns(10);
		panel.add(txtEpanetFolder, "cell 2 2,growx");
		
		btnEpanetFolder = new JButton();
		btnEpanetFolder.setText("...");
		btnEpanetFolder.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEpanetFolder.setActionCommand("chooseFileEpanet");
		panel.add(btnEpanetFolder, "cell 4 2");
		
		chkConnect = new JCheckBox(BUNDLE.getString("Config.chkConnect")); //$NON-NLS-1$
		chkConnect.setSelected(true);
		panel.add(chkConnect, "cell 1 3 2 1,aligny baseline");
		
		btnAccept = new JButton(BUNDLE.getString("Form.btnAccept.text")); //$NON-NLS-1$
		btnAccept.setActionCommand("configAccept");
		panel_1.add(btnAccept, "cell 2 2");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		btnSwmmFolder.addActionListener(this);
		btnEpanetFolder.addActionListener(this);
		btnAccept.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}

	
}