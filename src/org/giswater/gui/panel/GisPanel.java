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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.FileUtils;
import org.giswater.dao.MainDao;
import org.giswater.gui.frame.GisFrame;
import org.giswater.util.Encryption;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class GisPanel extends JPanel implements ActionListener, FocusListener  {

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

    private PropertiesMap gswProp;
	private String gisExtension;   // qgs or gvp
	private JComboBox<String> cboSchema;
	private JLabel lblSchema;
	private JLabel lblDataStorage;
	private JComboBox<String> cboDataStorage;
	private JPanel panel_1;
	private JButton btnClose;
    

	public GisPanel() {
        this.gswProp = MainDao.getGswProperties();        
		initConfig();
		setDefaultValues();
	}
	
	public GisFrame getFrame(){
		return gisFrame;
	}

	public void setFrame(GisFrame gisFrame){
		this.gisFrame = gisFrame;
	}
	
	public JDialog getDialog() {
		return new JDialog();
	}

	public void setGisExtension(String gis) {
		this.gisExtension = gis;
	}	
	
	public void setPanelTitle(String title) {
		tabbedPane.setTitleAt(0, title);
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
		else if (software.equals("HECRAS")){
			software = "HEC-RAS";
		}		
		cboSoftware.setSelectedItem(software);
	}	
	
	public String getProjectSoftware() {
		String software = cboSoftware.getSelectedItem().toString();
		if (software.equals("EPA SWMM")){
			software = "EPASWMM";
		}
		else if (software.equals("HEC-RAS")){
			software = "HECRAS";
		}
		return software;
	}	
	
	public void setDataStorage(String type) {
		cboDataStorage.setSelectedItem(type);
	}	
	
	public String getDataStorage() {
		return cboDataStorage.getSelectedItem().toString();
	}		
	
	public void setSchemaModel(Vector<String> v) {
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

	public void enableControls(boolean enable){
		lblSchema.setEnabled(enable);
		cboSchema.setEnabled(enable);
	}	
	
    private void setDefaultValues(){
    	
		setProjectFolder(gswProp.get("GIS_FOLDER"));
		setProjectName(gswProp.get("GIS_NAME"));
		setProjectSoftware(gswProp.get("GIS_SOFTWARE"));	
		if (MainDao.isConnected()){
			setSchemaModel(MainDao.getSchemas(getProjectSoftware()));
			setSelectedSchema(gswProp.get("GIS_SCHEMA"));
			cboSchema.setEnabled(true);
		}
		else{setSchemaModel(null);
			cboSchema.setEnabled(false);				
		}
		
    }	
	
	
	private void initConfig() throws MissingResourceException {

		setLayout(new MigLayout("", "[8.00][:531px:531px][40.00]", "[5px][226.00][12]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(tabbedPane, "cell 1 1,grow");

		// Panel Database connection
		panel_1 = new JPanel();
		tabbedPane.addTab(BUNDLE.getString("Gis.panel.title"), null, panel_1, null); //$NON-NLS-1$
		panel_1.setLayout(new MigLayout("", "[115px:n:115px][135:n:135][133.00][]", "[40][25:25][25:25][25:25][25:25][]"));
		
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
		cboSoftware.setActionCommand(BUNDLE.getString("GisPanel.cboSoftware.actionCommand")); //$NON-NLS-1$
		cboSoftware.setModel(new DefaultComboBoxModel<String>(new String[] {"EPANET", "EPA SWMM", "HEC-RAS"}));
		panel_1.add(cboSoftware, "cell 1 2,growx");
		
		lblDataStorage = new JLabel(BUNDLE.getString("GisPanel.lblDataStorage.text")); //$NON-NLS-1$
		panel_1.add(lblDataStorage, "cell 0 3,alignx trailing");
		
		cboDataStorage = new JComboBox<String>();
		cboDataStorage.setActionCommand("selectSourceType");
		cboDataStorage.setModel(new DefaultComboBoxModel<String>(new String[] {"Database", "DBF"}));
		panel_1.add(cboDataStorage, "cell 1 3,growx");
		
		lblSchema = new JLabel(BUNDLE.getString("GisPanel.lblSchema.text")); //$NON-NLS-1$
		panel_1.add(lblSchema, "cell 0 4,alignx trailing");
		
		cboSchema = new JComboBox<String>();
		cboSchema.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				getFocus();
			}
		});
		panel_1.add(cboSchema, "cell 1 4,growx");
		
		btnAccept = new JButton(BUNDLE.getString("Form.btnAccept.text")); //$NON-NLS-1$
		btnAccept.setActionCommand("gisAccept");
		panel_1.add(btnAccept, "cell 2 5,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("GisPanel.btnClose.text")); //$NON-NLS-1$
		btnClose.addActionListener(this);
		btnClose.setActionCommand("closePanel");
		panel_1.add(btnClose, "cell 3 5");

		setupListeners();

	}

	
	// Setup component's listener
	private void setupListeners() {
		btnProjectFolder.addActionListener(this);
		btnAccept.addActionListener(this);	
		cboDataStorage.addActionListener(this);		
		tabbedPane.addFocusListener(this);	
		btnClose.addActionListener(this);	
		cboSoftware.addActionListener(this);		
	}

	
	public void chooseProjectFolder() {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(Utils.getBundleString("gis_folder"));
		File file = new File(gswProp.get("GIS_FOLDER", System.getProperty("user.home")));
		chooser.setCurrentDirectory(file);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = chooser.getSelectedFile();
			setProjectFolder(folder.getAbsolutePath());
		}

	}	
	

	public void selectSourceType(){

		String dataStorage = this.getDataStorage();
		// Database selected
		if (dataStorage.toUpperCase().equals("DATABASE")){
			// Check if we already are connected
			if (MainDao.isConnected()){
				this.enableControls(true);
				this.setSchemaModel(MainDao.getSchemas(getProjectSoftware()));
				this.setSelectedSchema(gswProp.get("GIS_SCHEMA"));				
			} 
			else{
				//Utils.showMessage("You should be connected to a Database");
				this.enableControls(false);				
				this.setSchemaModel(null);				
			}
		}
		// DBF selected
		else{
			this.enableControls(false);		
			this.setSchemaModel(null);					
		}
		gswProp.put("GIS_TYPE", dataStorage.toUpperCase());			
		
	}
	
	
	public void softwareChanged(){
		getFocus();
	}	
	
	
	public void gisAccept(){
		
		// Get parameteres from view or properties file
		String folder = getProjectFolder();		
		String name = getProjectName();
		String software = getProjectSoftware();
		String gisType = gswProp.get("GIS_TYPE");
		String schema = getSelectedSchema();
		String table = "arc";
		if (software.equals("HECRAS")){
			table = "banks";
		}
		String schemaSrid = MainDao.getTableSrid(schema, table).toString();
		
		// Create GIS Project
		if (gisType.equals("DATABASE")){
			if (MainDao.isConnected()){
				// Update properties file
				gswProp.put("GIS_FOLDER", folder);
				gswProp.put("GIS_NAME", name);
				gswProp.put("GIS_SOFTWARE", software);
				gswProp.put("GIS_SCHEMA", schema);
				// TODO: i18n
				String msg = "WARNING:";
				msg+= "\nYour database password will be stored in plain text in your project files " +
					"and in your home directory on Unix-like systems, or in your user profile on Windows.";
				msg+= "\nIf you do not want this to happen, please press 'No' button or consider fixing it in GIS desktop project";
				int answer = Utils.confirmDialog(msg);
				if (answer == JOptionPane.YES_OPTION){
					gisProjectDatabase(gisExtension, folder + File.separator, name, software, schema, schemaSrid);
				}
			} 
			else{
				Utils.showMessage("You should connect to a Database");
				this.enableControls(false);				
				this.setSchemaModel(null);				
			}			
		}
		else if (gisType.equals("DBF")){
			gisProjectDbf(gisExtension, folder + File.separator, name, software);
		}
		
	}

	
	private void gisProjectDbf(String gisExtension, String folder, String name, String software) {
		
		if (software.equals("HECRAS")){
			Utils.showMessage("DBF option not available for HECRAS software");
			return;
		}
    	String gisFolder = Utils.getGisFolder();
		String templatePath = gisFolder + software;
		File templateFolder = new File(templatePath);
		if (!templateFolder.exists()){
			Utils.showError("inp_error_notfound", templatePath);
			return;
		}
		
		String destPath = folder + software+"_"+name;
		File destFolder = new File(destPath);
		try {
			Utils.getLogger().info("GIS Folder: " + destPath);
			setCursor(new Cursor(Cursor.WAIT_CURSOR));				
			FileUtils.copyDirectory(templateFolder, destFolder);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
            // Ending message
            String msg = Utils.getBundleString("gis_end") + "\n" + destPath +      
            	"\nYou can build your own GIS project with all shape files and DBF created." +
    			"\nTo do it, you can use any GIS software. " +
    			"\nWe recommend you Open Source GIS software like gvSIG or QGIS.";
    		Utils.showMessage(msg);            
		} catch (IOException e) {
        	Utils.showError(e);
		}
		
	}

	
	private void gisProjectDatabase(String gisExtension, String folderPath, String name, String software, 
		String schema, String schemaSrid) {
		
		String templatePath = "";
		try {

	    	String gisFolder = Utils.getGisFolder();
	    	templatePath = gisFolder + software+"."+gisExtension;
			File templateFile = new File(templatePath);
			if (!templateFile.exists()){
				Utils.showError("inp_error_notfound", templatePath);
				return;
			}
			File folder = new File(folderPath);
			if (!folder.exists()){
				folder.mkdir();
			}
			String destPath = folderPath + software+"_"+name+"."+gisExtension;
			File destFile = new File(destPath);
			if (destFile.exists()){
	            int answer = Utils.confirmDialog("overwrite_file");
	            if (answer == JOptionPane.NO_OPTION){
	            	return;
	            }
			}
			
			gisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));	
			
			// Get parameteres connection from properties file
			String host, port, db, user, password;			
			host = gswProp.get("POSTGIS_HOST", "localhost");		
			port = gswProp.get("POSTGIS_PORT", "5431");
			db = gswProp.get("POSTGIS_DATABASE", "giswater");
			user = gswProp.get("POSTGIS_USER", "postgres");
			password = gswProp.get("POSTGIS_PASSWORD");		
			password = Encryption.decrypt(password);
			password = (password == null) ? "" : password;		
			
			// Get File content
			Utils.getLogger().info("Reading template file... " + templatePath);			
    		String content = Utils.readFile(templatePath);
			Utils.getLogger().info("Creating GIS file... " + destPath);	    		

			// Replace spatialrefsys and extent parameters
    		content = MainDao.replaceSpatialParameters(schemaSrid, content);
    		content = MainDao.replaceExtentParameters(software, schema, content);
    		
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
    		int res = Utils.confirmDialog(msg);             
            if (res == JOptionPane.YES_OPTION){
               	Utils.openFile(destPath);
            }  	        
			
        } catch (FileNotFoundException e) {
    	    Utils.showError("inp_error_notfound", templatePath);
    	} catch (IOException e) {
        	Utils.showError(e, templatePath);
    	}
		
	}


	private void getFocus(){
		if (MainDao.isConnected()){
			this.enableControls(true);
			this.setSchemaModel(MainDao.getSchemas(getProjectSoftware()));
			this.setSelectedSchema(gswProp.get("GIS_SCHEMA"));				
		} 
		else{
			this.enableControls(false);				
			this.setSchemaModel(null);				
		}		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("chooseProjectFolder")){
			chooseProjectFolder();
		}
		else if (e.getActionCommand().equals("selectSourceType")){
			selectSourceType();
		}
		else if (e.getActionCommand().equals("softwareChanged")){
			softwareChanged();
		}			
		else if (e.getActionCommand().equals("gisAccept")){
			gisAccept();
		}
		else if (e.getActionCommand().equals("closePanel")){
			this.getFrame().setVisible(false);	
		}		
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		getFocus();
	}

	@Override
	public void focusLost(FocusEvent e) { }
	
	
}