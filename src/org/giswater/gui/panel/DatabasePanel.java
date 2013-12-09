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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.DatabaseController;
import org.giswater.controller.MainController;
import org.giswater.util.Utils;


public class DatabasePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private JFrame f;
	private MainController controller;
	private DatabaseController databaseController;
	private JTextField txtSchema;
	private JPanel panel;
	private JLabel lblNewLabel;
	private JComboBox<String> cboDriver;
	private JLabel lblPort;
	private JLabel lblIp;
	private JTextField txtIP;
	private JTextField txtPort;
	private JLabel lblDatabase;
	private JTextField txtDatabase;
	private JLabel lblUser;
	private JTextField txtUser;
	private JLabel lblPassword;
	private JPasswordField txtPassword;
	private JButton btnTest;
	private JCheckBox chkRemember;
	private JTabbedPane tabbedPane;

	
	public DatabasePanel() {
		try {
			initConfig();
		} catch (MissingResourceException e) {
			Utils.showError(e);
			System.exit(ERROR);
		}
	}
	
	public void setControl(MainController nodeController) {
		this.controller = nodeController;
	}

	public void setControl(DatabaseController databaseController) {
		this.databaseController = databaseController;
	}

	public JFrame getFrame() {
		return new JFrame();
	}

	public void setFrame(JFrame frame) {
		this.f = frame;
	}

	public JDialog getDialog() {
		return new JDialog();
	}

	public Integer getDriver() {
		return cboDriver.getSelectedIndex();
	}
	
	public String getHost() {
		return txtIP.getText().trim();
	}

	public void setHost(String text) {
		txtIP.setText(text);
	}

	public String getPort() {
		return txtPort.getText().trim();
	}

	public void setPort(String text) {
		txtPort.setText(text);
	}

	public String getDatabase() {
		return txtDatabase.getText().trim();
	}

	public void setDatabase(String text) {
		txtDatabase.setText(text);
	}

	public String getUser() {
		return txtUser.getText().trim();
	}

	public void setUser(String text) {
		txtUser.setText(text);
	}

	@SuppressWarnings("deprecation")
	public String getPassword() {
		return txtPassword.getText();
	}

	public void setPassword(String path) {
		txtPassword.setText(path);
	}

	public boolean getRemember() {
		return chkRemember.isSelected();
	}
	
	public void setConnectionText(String text){
		btnTest.setText(text);
	}
	
	public void close() {
		f.setVisible(false);
		f.dispose();
	}

	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][:531px:531px][40.00]", "[10px][410.00][12]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		// Panel 1
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Form.panel_1.title"), null, panel_1, null); //$NON-NLS-1$
		panel_1.setLayout(new MigLayout("", "[10][][380]", "[5][208.00][10]"));

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.add(panel, "cell 1 1 2 1,grow");
		panel.setLayout(new MigLayout("", "[5][77.00][15][140][11.00][125.00]", "[4][24][24][24][24][24][24][]"));

		lblNewLabel = new JLabel(BUNDLE.getString("Database.lblNewLabel.text_2")); //$NON-NLS-1$
		panel.add(lblNewLabel, "cell 1 1");

		cboDriver = new JComboBox<String>();
		cboDriver.setPreferredSize(new Dimension(24, 20));
		cboDriver.setMinimumSize(new Dimension(24, 20));
		//cboDriver.setModel(new DefaultComboBoxModel<String>(new String[] {"PG-9.1+PostGIS-1.5", "PG-9.2+PostGIS-2.0"}));
		cboDriver.setModel(new DefaultComboBoxModel<String>(new String[] {"PG-9.2 + PostGIS-2.0"}));
		panel.add(cboDriver, "cell 3 1,growx");

		lblIp = new JLabel(BUNDLE.getString("Database.lblIp.text")); //$NON-NLS-1$
		panel.add(lblIp, "cell 1 2");

		txtIP = new JTextField();
		panel.add(txtIP, "cell 3 2,growx");
		txtIP.setColumns(10);

		lblPort = new JLabel(BUNDLE.getString("Database.lblPort.text")); //$NON-NLS-1$
		panel.add(lblPort, "cell 1 3,alignx left");

		txtPort = new JTextField();
		txtPort.setColumns(10);
		panel.add(txtPort, "cell 3 3,growx");

		lblDatabase = new JLabel(BUNDLE.getString("Database.lblDatabase.text")); //$NON-NLS-1$
		panel.add(lblDatabase, "cell 1 4");

		txtDatabase = new JTextField();
		txtDatabase.setText("");
		txtDatabase.setColumns(10);
		panel.add(txtDatabase, "cell 3 4,growx");

		lblUser = new JLabel(BUNDLE.getString("Database.lblUser.text")); //$NON-NLS-1$
		panel.add(lblUser, "cell 1 5");

		txtUser = new JTextField();
		txtUser.setText("postgres");
		txtUser.setColumns(10);
		panel.add(txtUser, "cell 3 5,growx");

		lblPassword = new JLabel(BUNDLE.getString("Database.lblPassword.text")); //$NON-NLS-1$
		panel.add(lblPassword, "cell 1 6");

		txtPassword = new JPasswordField();
		txtPassword.setText("");
		panel.add(txtPassword, "cell 3 6,growx");

		chkRemember = new JCheckBox(BUNDLE.getString("Database.chkRemember.text")); //$NON-NLS-1$
		chkRemember.setSelected(true);
		panel.add(chkRemember, "cell 3 7,aligny baseline");

		btnTest = new JButton(BUNDLE.getString("Database.btnTest.text")); //$NON-NLS-1$
		btnTest.setMinimumSize(new Dimension(110, 23));
		btnTest.setActionCommand("testConnection");
		panel.add(btnTest, "cell 5 7,alignx right");

		// Panel 3
		JPanel panel_3 = new JPanel();
		panel_3.setVisible(false);
		//tabbedPane.addTab(BUNDLE.getString("Form.panel_4.title"), null, panel_3, null); //$NON-NLS-1$
		panel_3.setLayout(new MigLayout("",	"[10.00][][10.00][300.00][10.00][]", "[10][][10][45][15][]"));

		JLabel lblSchemaName = new JLabel(BUNDLE.getString("Form.lblSchemaName.text")); //$NON-NLS-1$
		panel_3.add(lblSchemaName, "cell 1 1");

		txtSchema = new JTextField();
		txtSchema.setMinimumSize(new Dimension(100, 20));
		txtSchema.setPreferredSize(new Dimension(150, 20));
		panel_3.add(txtSchema, "cell 3 1,alignx left");
		txtSchema.setColumns(10);

		JLabel label_3 = new JLabel();
		label_3.setText(BUNDLE.getString("Form.label.text")); //$NON-NLS-1$
		panel_3.add(label_3, "cell 1 3");

		JTextArea txtFileInp3 = new JTextArea();
		txtFileInp3.setLineWrap(true);
		panel_3.add(txtFileInp3, "cell 3 3,grow");

		// Select Database connection by default
		tabbedPane.setSelectedIndex(0);

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {

		// Panel Database connection
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				databaseController.action(e.getActionCommand());
			}
		});

	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}
	
}