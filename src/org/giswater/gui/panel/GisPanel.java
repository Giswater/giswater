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
 *
 */
package org.giswater.gui.panel;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.giswater.dao.ConfigDao;
import org.giswater.dao.MainDao;
import org.giswater.dao.PropertiesDao;
import org.giswater.gui.MainClass;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class GisPanel extends JPanel implements ActionListener, FocusListener  {

	private PropertiesMap gswProp;
	private JDialog parent;
	
	private String selectedSchema;
	
	private JTextArea txtProjectFolder;
	private JButton btnProjectFolder;
	private JTextField txtProjectName;
    private JComboBox<String> cboSoftware;
    private JLabel lblSchema;
	private JComboBox<String> cboSchema;
	private JButton btnAccept;
	private JButton btnClose;
    
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	private static final String GIS_EXTENSION = "qgs";
	

	public GisPanel() {
        this.gswProp = PropertiesDao.getGswProperties();     
		initConfig();
		setDefaultValues();
	}
	
	public JDialog getDialog() {
		return parent;
	}
	
	public void setParent(JDialog gisDialog) {
		parent = gisDialog;		
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
		if (software.equals("EPASWMM")){
			software = "EPA SWMM";
		}
		cboSoftware.setSelectedItem(software);
	}	
	
	public String getProjectSoftware() {
		String software = cboSoftware.getSelectedItem().toString();
		if (software.equals("EPA SWMM")) {
			software = "ud";
		}
		else {
			software = "ws";
		}
		return software;
	}		
	
	public void setSchemaModel(Vector<String> v) {
		ComboBoxModel<String> cbm = null;
		if (v != null) {
			cbm = new DefaultComboBoxModel<String>(v);
			cboSchema.setModel(cbm);		
		} else {
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

	public void enableControls(boolean enable) {
		lblSchema.setEnabled(enable);
		cboSchema.setEnabled(enable);
	}	
	
    private void setDefaultValues() {
		setProjectFolder(gswProp.get("GIS_FOLDER"));
		setProjectName(gswProp.get("GIS_NAME"));
		setProjectSoftware(MainDao.getWaterSoftware());
		selectedSchema = PropertiesDao.getSelectedSchema();
		setSelectedSchema(selectedSchema);
    }	
	
	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[][:225px:300px][40.00][]", "[5px:n][34px:n][][][][10px:n][]"));
		
		JLabel lblProjectFolder = new JLabel(BUNDLE.getString("Gis.lblProjectFolder"));
		add(lblProjectFolder, "cell 0 1,alignx right");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 1 1 2 1,grow");
		
		txtProjectFolder = new JTextArea();
		txtProjectFolder.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(txtProjectFolder);
		txtProjectFolder.setText("");
		
		btnProjectFolder = new JButton();
		btnProjectFolder.setMinimumSize(new Dimension(72, 9));
		add(btnProjectFolder, "cell 3 1");
		btnProjectFolder.setText("...");
		btnProjectFolder.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnProjectFolder.setActionCommand("chooseProjectFolder"); 
		
		JLabel lblProjectName = new JLabel(BUNDLE.getString("Gis.lblProjectName"));
		add(lblProjectName, "cell 0 2,alignx right");
		
		txtProjectName = new JTextField();
		add(txtProjectName, "cell 1 2,growx");
		txtProjectName.setText((String) null);
		txtProjectName.setColumns(10);
		
		JLabel lblSoftware = new JLabel(BUNDLE.getString("GisPanel.lblSoftware.text")); 
		add(lblSoftware, "cell 0 3,alignx trailing");
		
		cboSoftware = new JComboBox<String>();
		cboSoftware.setActionCommand("softwareChanged");
		cboSoftware.setModel(new DefaultComboBoxModel(new String[] {"EPANET", "EPA SWMM"}));
		add(cboSoftware, "cell 1 3,growx");
		
		lblSchema = new JLabel(BUNDLE.getString("GisPanel.lblSchema.text")); 
		add(lblSchema, "cell 0 4,alignx trailing");
		
		cboSchema = new JComboBox<String>();
		add(cboSchema, "cell 1 4,growx");
		
		btnAccept = new JButton(BUNDLE.getString("Generic.btnAccept.text")); 
		btnAccept.setMinimumSize(new Dimension(72, 23));
		btnAccept.setActionCommand("gisAccept");
		add(btnAccept, "cell 2 6,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text")); 
		btnClose.setMinimumSize(new Dimension(72, 23));
		btnClose.addActionListener(this);
		btnClose.setActionCommand("closePanel");
		add(btnClose, "cell 3 6");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		
		addFocusListener(this);	
		btnProjectFolder.addActionListener(this);
		btnAccept.addActionListener(this);
		btnClose.addActionListener(this);	
		cboSoftware.addActionListener(this);
		cboSchema.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				getFocus();
			}
		});		
		
	}

	
	public void chooseProjectFolder() {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(Utils.getBundleString("gis_folder"));
		File file = new File(gswProp.get("GIS_FOLDER", System.getProperty("user.home")));
		chooser.setCurrentDirectory(file);
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = chooser.getSelectedFile();
			setProjectFolder(folder.getAbsolutePath());
		}

	}	
	
	
	public void softwareChanged() {
		getFocus();
	}	
	
	
	public void gisAccept() {
		
		// Get parameteres from view or properties file
		String folder = getProjectFolder();		
		String name = getProjectName();
		String software = getProjectSoftware();
		
		if (folder.equals("")) {
			MainClass.mdi.showMessage(Utils.getBundleString("GisPanel.select_folder")); //$NON-NLS-1$
			return;
		}
		
		if (name.equals("")) {
			MainClass.mdi.showMessage(Utils.getBundleString("GisPanel.type_name")); //$NON-NLS-1$
			return;
		}
		
		// Create GIS Project
		if (MainDao.isConnected()) {
			String schema = getSelectedSchema();
			if (schema.equals("")) {
				MainClass.mdi.showMessage("any_schema_selected");
				return;
			}
			String schemaSrid = MainDao.getSrid(schema);
			// Update properties file
			gswProp.put("GIS_FOLDER", folder);
			gswProp.put("GIS_NAME", name);
			int answer = Utils.showYesNoDialog(this, "gis_creation");
			if (answer == JOptionPane.YES_OPTION) {
				gisProjectDatabase(GIS_EXTENSION, folder + File.separator, name, software, schema, schemaSrid);
			}
		} 
		else {
			MainClass.mdi.showMessage(Utils.getBundleString("GisPanel.connect_database")); //$NON-NLS-1$
			this.enableControls(false);				
			this.setSchemaModel(null);				
		}			
		
		MainClass.mdi.putGisParams(this);
		
		// Close dialog
		getDialog().dispose();
		
	}

	
	private void gisProjectDatabase(String gisExtension, String folderPath, String name, String software, 
		String schema, String schemaSrid) {
		
		String templatePath = "";
		try {

	    	String gisFolder = Utils.getGisFolder();
	    	templatePath = gisFolder+software+"_template."+gisExtension;
			File templateFile = new File(templatePath);
			if (!templateFile.exists()) {
	    	    MainClass.mdi.showError("inp_error_notfound", templatePath);
				return;
			}
			File folder = new File(folderPath);
			if (!folder.exists()) {
				folder.mkdir();
			}
			
			// Check if QGIS file extension is associated
			if (!Utils.isQgisAssociated()) {
				String msg = Utils.getBundleString("GisPanel.warning_qgis1") +  //$NON-NLS-1$
					Utils.getBundleString("GisPanel.warning_qgis2") + //$NON-NLS-1$
					Utils.getBundleString("GisPanel.warning_qgis3"); //$NON-NLS-1$
				int answer = Utils.showYesNoDialog(this, msg);
		        if (answer == JOptionPane.NO_OPTION) return;	        	
			}
			
			String destPath = folderPath+name+"."+gisExtension;
			File destFile = new File(destPath);
			if (destFile.exists()) {
	            int answer = Utils.showYesNoDialog(this, "overwrite_file");
	            if (answer == JOptionPane.NO_OPTION) return;
			}
			
			this.requestFocusInWindow();
			setCursor(new Cursor(Cursor.WAIT_CURSOR));	
			
			// Get parameteres connection from properties file
			String host, port, db, user, password;			
			host = gswProp.get("POSTGIS_HOST", "127.0.0.1");		
			port = gswProp.get("POSTGIS_PORT", "5431");
			db = gswProp.get("POSTGIS_DATABASE", "postgres");
			user = gswProp.get("POSTGIS_USER", "postgres");
			password = gswProp.get("POSTGIS_PASSWORD");		
			password = Encryption.decrypt(password);
			password = (password == null) ? "" : password;		
			
			// Get File content
			Utils.getLogger().info("Reading template file... " + templatePath);			
    		String content = Utils.readFile(templatePath);
			Utils.getLogger().info("Creating GIS file... " + destPath);	    		

			// Replace spatialrefsys and extent parameters
    		content = ConfigDao.replaceSpatialParameters(schemaSrid, content);
    		content = MainDao.replaceExtentParameters(schema, content);
    		
	    	// Replace SCHEMA_NAME for schemaName parameter. SRID_VALUE for srid parameter
			content = content.replace("SCHEMA_NAME", schema);
			content = content.replace("SRID_VALUE", schemaSrid);

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
			Utils.getLogger().info("GIS file completed");			
            String msg = Utils.getBundleString("gis_end") + "\n" + destPath + "\n" + 
            	Utils.getBundleString("view_file");
    		int res = Utils.showYesNoDialog(msg);             
            if (res == JOptionPane.YES_OPTION) {
               	Utils.openFile(destPath);
            }  	        
			
        } catch (FileNotFoundException e) {
    	    MainClass.mdi.showError("inp_error_notfound", templatePath);
    	} catch (IOException e) {
        	Utils.showError(e, templatePath);
    	}
		
	}


	private void getFocus() {
			
		if (MainDao.isConnected()) {
			enableControls(true);
			setSchemaModel(MainDao.getSchemas(getProjectSoftware()));
			setSelectedSchema(selectedSchema);	
		} 
		else {
			enableControls(false);				
			setSchemaModel(null);
		}
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("chooseProjectFolder")) {
			chooseProjectFolder();
		}
		else if (e.getActionCommand().equals("softwareChanged")) {
			softwareChanged();
		}			
		else if (e.getActionCommand().equals("gisAccept")) {
			gisAccept();
		}
		else if (e.getActionCommand().equals("closePanel")) {
			getDialog().dispose();
		}	
		
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		getFocus();
	}

	@Override
	public void focusLost(FocusEvent e) { }


}