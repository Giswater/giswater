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

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.giswater.dao.MainDao;
import org.giswater.gui.frame.GisFrame;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import org.apache.commons.io.FileUtils;


public class GisPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2576460232916596200L;
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private GisFrame gisFrame;	
	private JTabbedPane tabbedPane;
	private JLabel lblProjectFolder;
	private JButton btnProjectFolder;
	private JLabel lblProjectName;
	private JTextField txtProjectName;
	private JButton btnAccept;
	private JTextArea txtProjectFolder;
	private JScrollPane scrollPane;
	private JLabel lblSoftware;
	private JComboBox<String> cboSoftware;

    private PropertiesMap prop;
	private String gisExtension;   // qgs or gvp
	private JComboBox<String> cboSchema;
	private JLabel lblSchema;
    
	
	public GisPanel(GisFrame gisFrame) {
		this.gisFrame = gisFrame;
        this.prop = MainDao.getPropertiesFile();
		initConfig();
		setDefaultValues();
	}
	
	public GisFrame getFrame(){
		return gisFrame;
	}

	public JDialog getDialog() {
		return new JDialog();
	}

	public void setGisExtension(String gis) {
		this.gisExtension = gis;
	}	
	
	public void setProjectFolder(String path) {
		txtProjectFolder.setText(path);
	}		
	
	public String getProjectFolder() {
		return txtProjectFolder.getText().trim().toLowerCase();
	}	

	public void setProjectName(String name) {
		txtProjectName.setText(name);
	}		
	
	public String getProjectName() {
		return txtProjectName.getText().trim().toLowerCase();
	}		
	
	public void setProjectSoftware(String software) {
		cboSoftware.setSelectedItem(software);
	}	
	
	public String getProjectSoftware() {
		return cboSoftware.getSelectedItem().toString();
	}	
	
	public void setSchemaList(Vector<String> v) {
		ComboBoxModel<String> cbm = null;
		if (v != null){
			cbm = new DefaultComboBoxModel<String>(v);
			cboSchema.setModel(cbm);		
		} else{
			DefaultComboBoxModel<String> theModel = (DefaultComboBoxModel<String>) cboSchema.getModel();
			theModel.removeAllElements();
		}
	}
	
	public void setSelectedSchema(String schemaName) {
		cboSchema.setSelectedItem(schemaName);
	}

	public String getSelectedSchema() {
		String elem = "";
		if (cboSchema.getSelectedIndex() != -1) {
			elem = cboSchema.getSelectedItem().toString();
		}
		return elem;
	}		
	
	
    private void setDefaultValues(){
    	
		setProjectFolder(prop.get("GIS_FOLDER"));
		setProjectName(prop.get("GIS_NAME"));
		setProjectSoftware(prop.get("GIS_SOFTWARE"));	
		setSchemaList(MainDao.getSchemas());
		setSelectedSchema(prop.get("GIS_SCHEMA"));
		String gisType = prop.get("GIS_TYPE");
		if (gisType.equals("DATABASE")){
			cboSchema.setEnabled(true);
		}
		else{
			cboSchema.setEnabled(false);
		}
		
    }	
	
	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][:531px:531px][40.00]", "[5px][190.00][12]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		// Panel Database connection
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Gis.panel.title"), null, panel_1, null); //$NON-NLS-1$
		panel_1.setLayout(new MigLayout("", "[100px:n:100px][:134.00:280,grow][133.00][]", "[45.00][25:25][25:25][25:25][::5px][]"));
		
		lblProjectFolder = new JLabel(BUNDLE.getString("Gis.lblProjectFolder"));
		panel_1.add(lblProjectFolder, "cell 0 0,alignx right");
		
		scrollPane = new JScrollPane();
		panel_1.add(scrollPane, "cell 1 0 2 1,grow");
		
		txtProjectFolder = new JTextArea();
		txtProjectFolder.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(txtProjectFolder);
		txtProjectFolder.setText("");
		
		btnProjectFolder = new JButton();
		panel_1.add(btnProjectFolder, "cell 3 0");
		btnProjectFolder.setText("...");
		btnProjectFolder.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnProjectFolder.setActionCommand("chooseProjectFolder"); //$NON-NLS-1$
		
		lblProjectName = new JLabel(BUNDLE.getString("Gis.lblProjectName"));
		panel_1.add(lblProjectName, "cell 0 1,alignx right");
		
		txtProjectName = new JTextField();
		panel_1.add(txtProjectName, "cell 1 1,growx");
		txtProjectName.setText((String) null);
		txtProjectName.setColumns(10);
		
		lblSoftware = new JLabel(BUNDLE.getString("GisPanel.lblSoftware.text")); //$NON-NLS-1$
		panel_1.add(lblSoftware, "cell 0 2,alignx trailing");
		
		cboSoftware = new JComboBox<String>();
		cboSoftware.setModel(new DefaultComboBoxModel<String>(new String[] {"EPASWMM", "EPANET", "HECRAS"}));
		panel_1.add(cboSoftware, "cell 1 2,growx");
		
		lblSchema = new JLabel(BUNDLE.getString("GisPanel.lblSchema.text")); //$NON-NLS-1$
		panel_1.add(lblSchema, "cell 0 3,alignx trailing");
		
		cboSchema = new JComboBox<String>();
		panel_1.add(cboSchema, "cell 1 3,growx");
		
		btnAccept = new JButton(BUNDLE.getString("Form.btnAccept.text")); //$NON-NLS-1$
		btnAccept.setActionCommand("gisAccept");
		panel_1.add(btnAccept, "cell 2 5,alignx right");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		btnProjectFolder.addActionListener(this);
		btnAccept.addActionListener(this);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("chooseProjectFolder")){
			chooseProjectFolder();
		}
		else if (e.getActionCommand().equals("gisAccept")){
			gisAccept();
		}
	}

	
	public void chooseProjectFolder() {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(Utils.getBundleString("gis_folder"));
		File file = new File(prop.get("GIS_FOLDER", System.getProperty("user.home")));
		chooser.setCurrentDirectory(file);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = chooser.getSelectedFile();
			setProjectFolder(folder.getAbsolutePath());
		}

	}	
	
	
	public void gisAccept(){
		
		// Get parameteres from view or properties file
		String folder = getProjectFolder();		
		String name = getProjectName();
		String software = getProjectSoftware();
		String gisType = prop.get("GIS_TYPE");
		String schema = getSelectedSchema();
		
		// Update properties file
		prop.put("GIS_FOLDER", folder);
		prop.put("GIS_NAME", name);
		prop.put("GIS_SOFTWARE", software);
		prop.put("GIS_SCHEMA", schema);

		// Create GIS Project
		if (gisType.equals("DATABASE")){
			// TODO: i18n
			String msg = "You have opted to create GIS project.";
			msg+= "\nWARNINGS:";
			msg+= "\n1- Your database password will be stored in plain text in your project files and in your home directory on Unix-like systems, or in your user profile on Windows.";
			msg+= "\n2- The only SRID avaliable is EPSG-23031.";
			msg+= "\nIf you do not want this to happen, please press the Cancel button or Consider fixing it in GIS desktop project";
			int answer = Utils.confirmDialog(msg);
			if (answer == JOptionPane.YES_OPTION){
				gisProjectDatabase(gisExtension, folder + File.separator, name, software, schema);
			}
		}
		else if (gisType.equals("DBF")){
			gisProjectDbf(gisExtension, folder + File.separator, name, software);
		}
		
		// Close frame
		//this.getFrame().setVisible(false);		
		
	}

	
	public boolean gisProjectDbf(String gisExtension, String folder, String name, String software) {
		
		boolean status = false;
		
    	String gisFolder = Utils.getGisFolder();
		String templatePath = gisFolder + software;
		File templateFolder = new File(templatePath);
		if (!templateFolder.exists()){
			Utils.showError("inp_error_notfound", templatePath);
			return false;
		}
		
		String destPath = folder + software+"_"+name;
		File destFolder = new File(destPath);
		try {
			Utils.getLogger().info("GIS Folder: " + destPath);
			setCursor(new Cursor(Cursor.WAIT_CURSOR));				
			FileUtils.copyDirectory(templateFolder, destFolder);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
            // Ending message
            String msg = Utils.getBundleString("gis_end") + "\n" + destPath + "\n" + 
            	Utils.getBundleString("view_file");
    		int res = Utils.confirmDialog(msg);             
            if (res == JOptionPane.YES_OPTION){
               	Utils.openFile(destPath);
            }  			
		} catch (IOException e) {
        	Utils.showError(e);
		}
		
		return status;
		
	}

	
	public boolean gisProjectDatabase(String gisExtension, String folder, String name, String software, String schema) {
		
		boolean status = false;
		String templatePath = "";
		String content = "";
		String destPath = "";
		
		String host, port, db, user, password, srid;
		
		// Get parameteres connection from properties file
		host = prop.get("POSTGIS_HOST", "localhost");		
		port = prop.get("POSTGIS_PORT", "5431");
		db = prop.get("POSTGIS_DATABASE", "giswater");
		user = prop.get("POSTGIS_USER", "postgres");
		password = prop.get("POSTGIS_PASSWORD");		
		password = Encryption.decrypt(password);
		password = (password == null) ? "" : password;		
		srid = prop.get("SRID_DEFAULT");		
    	
		try {

	    	String gisFolder = Utils.getGisFolder();
			templatePath = gisFolder + software+"."+gisExtension;
			File templateFile = new File(templatePath);
			if (!templateFile.exists()){
				Utils.showError("inp_error_notfound", templatePath);
				return false;
			}

			destPath = folder + software+"_"+name+"."+gisExtension;
			File destFile = new File(destPath);
			if (destFile.exists()){
	            int answer = Utils.confirmDialog("overwrite_file");
	            if (answer == JOptionPane.NO_OPTION){
	            	return false;
	            }
			}
			
			Utils.getLogger().info("GIS File: " + destPath);
			setCursor(new Cursor(Cursor.WAIT_CURSOR));	
			
			// Get File content
    		content = Utils.readFile(templatePath);
			
	    	// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter
			content = content.replace("SCHEMA_NAME", schema);
			content = content.replace("SRID_VALUE", srid);

	    	// Replace __DBNAME__ for db parameter. __HOST__ for host parameter, __PORT__ for port parameter
	    	// Replace __USER__ for user parameter. __PASSWORD__ for password parameter			
			content = content.replace("__DBNAME__", db);
			content = content.replace("__HOST__", host);
			content = content.replace("__PORT__", port);
			content = content.replace("__USER__", user);
			content = content.replace("__PASSWORD__", password);
			
			// Save content to destination file
			Utils.fillFile(destFile, content);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));				
			
            // Ending message
            String msg = Utils.getBundleString("gis_end") + "\n" + destPath + "\n" + 
            	Utils.getBundleString("view_file");
    		int res = Utils.confirmDialog(msg);             
            if (res == JOptionPane.YES_OPTION){
               	Utils.openFile(destPath);
            }  	        
			
			status = true;
			
        } catch (FileNotFoundException e) {
    	    Utils.showError("inp_error_notfound", templatePath);
    	} catch (IOException e) {
        	Utils.showError(e, templatePath);
    	}
		
		return status;
		
	}
	
	
}