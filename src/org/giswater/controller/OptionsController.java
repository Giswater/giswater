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
package org.giswater.controller;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.giswater.dao.MainDao;
import org.giswater.gui.dialog.options.AbstractOptionsDialog;
import org.giswater.gui.dialog.options.OptionsEpanetDialog;
import org.giswater.gui.dialog.options.RaingageDialog;
import org.giswater.gui.dialog.options.ResultCatDialog;
import org.giswater.gui.dialog.options.ResultCatEpanetDialog;
import org.giswater.gui.dialog.options.ResultSelectionDialog;
import org.giswater.util.Utils;

import com.toedter.calendar.JDateChooser;


public class OptionsController extends AbstractController{

	private AbstractOptionsDialog view;
    private ResultSet rs;
	private String action;
	private Integer current;
	private Integer total;
	
	
	public OptionsController(AbstractOptionsDialog dialog, ResultSet rs) {
		this.view = dialog;
        this.rs = rs;
        this.current = 1;
        this.total = MainDao.getNumberOfRows(rs);
	    view.setController(this);        
	}
	
	
	public boolean setComponents() {
		return setComponents(true);
	}
	
	
	// Update ComboBox items and selected item
	@SuppressWarnings({"unchecked", "rawtypes"})	
	public boolean setComponents(boolean fillData) {

		try {
			
			HashMap<String, JComboBox> map = view.comboMap; 
			for (Map.Entry<String, JComboBox> entry : map.entrySet()) {
			    String key = entry.getKey();
			    JComboBox combo = entry.getValue();
				view.setComboModel(combo, getComboValues(key));
				String value = "";
				if (fillData){
					// Process hydrology field
					if (key.equals("hydrology")) {
						String sql = "SELECT * FROM "+MainDao.getSchema()+".hydrology_selection";
						value = MainDao.queryToString(sql);
					}
					else {
						value = rs.getString(key);
					}
				}
				view.setComboSelectedItem(combo, value);
			}
			HashMap<String, JTextField> textMap = view.textMap; 
			for (Map.Entry<String, JTextField> entry : textMap.entrySet()) {
			    String key = entry.getKey();
			    JTextField component = entry.getValue();
			    Object value = "";
				if (fillData) {
					// Check if field exists in the table
					if (MainDao.checkColumn(rs, key)) {
						value = rs.getObject(key);
					}
				}
				view.setTextField(component, value);
			}	
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			
			HashMap<String, JDateChooser> dateMap = view.dateMap; 
			for (Map.Entry<String, JDateChooser> entry : dateMap.entrySet()) {
			    String key = entry.getKey();
			    JDateChooser component = entry.getValue();
			    String aux = null;
			    Date value = null;
				if (fillData) {
					aux = rs.getString(key);
					if (aux != null){
						try {
							value = sdf.parse(aux);
						} catch (ParseException e) {
							Utils.logError("ParseExceptionError");
						}		
					}
				}
				view.setDate(component, value);
			}			
			manageNavigationButtons();
			
		} catch (SQLException e) {
			Utils.showError(e);
			return false;
		}
		return true;
		
	}
	

	// Get ComboBox items from Database
	public Vector<String> getComboValues(String comboName) {

		Vector<String> values = null;
		String tableName = "";
		String fields = "*";
		
		// Raingage
		if (comboName.equals("form_type")) {
			tableName = "inp_value_raingage";
		}
		else if (comboName.equals("timser_id")) {
			tableName = "inp_timser_id";
		}
		else if (comboName.equals("rgage_type")) {
			tableName = "inp_typevalue_raingage";
		}		
		
		// Options
		else if (comboName.equals("flow_units")) {
			tableName = "inp_value_options_fu";
		}
		else if (comboName.equals("hydrology")) {
			tableName = "cat_hydrology";
		}
		else if (comboName.equals("force_main_equation")) {
			tableName = "inp_value_options_fme";
		}
		else if (comboName.equals("flow_routing")) {
			tableName = "inp_value_options_fr";
		}
		else if (comboName.equals("inertial_damping")) {
			tableName = "inp_value_options_id";
		}
		else if (comboName.equals("link_offsets")) {
			tableName = "inp_value_options_lo";
		}
		else if (comboName.equals("normal_flow_limited")) {
			tableName = "inp_value_options_nfl";
		}	

		// Options Epanet
		else if (comboName.equals("units")) {
			tableName = "inp_value_opti_units";
		}
		else if (comboName.equals("pattern")) {
			tableName = "inp_pattern";
		}
		else if (comboName.equals("hydraulics")) {
			tableName = "inp_value_opti_hyd";
		}
		else if (comboName.equals("unbalanced")) {
			tableName = "inp_value_opti_unbal";
		}	
		
		// Options Epanet or Report Epanet
		else if (comboName.equals("headloss")) {
			if (view instanceof OptionsEpanetDialog) {			
				tableName = "inp_value_opti_headloss";
			}
			else{
				tableName = "inp_value_yesno";
			}
		}			
		else if (comboName.equals("quality")) {
			if (view instanceof OptionsEpanetDialog) {			
				tableName = "inp_value_opti_qual";
			}
			else{
				tableName = "inp_value_yesno";
			}
		}
		
		// Report
		else if (comboName.equals("status")) {
			tableName = "inp_value_yesnofull";
		}		

		// ResultSelection
		else if (comboName.equals("result_id")) {
			tableName = "rpt_result_cat";
			fields = "result_id";
		}	
		
		// Times
		else if (comboName.equals("statistic")) {
			tableName = "inp_value_times";
		}
		
		else{
			tableName = "inp_value_yesno";
		}				
		values = MainDao.getTable(tableName, null, false, fields);
		
		return values;
		
	}


	// Update Database table
	@SuppressWarnings("rawtypes")
	public void saveData() {

		try {
			
			String key;
			Object value, auxDate;			
			ResultSetMetaData metadata = rs.getMetaData();		
			HashMap<String, JDateChooser> dateMap = view.dateMap; 			
			for (Map.Entry<String, JDateChooser> entry : dateMap.entrySet()) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");				
				key = entry.getKey();
				JDateChooser date = entry.getValue();
				auxDate = date.getDate();
				if (auxDate == null) {
					rs.updateNull(key);						
				} 
				else {
					value = sdf.format(auxDate); 					
					rs.updateString(key, (String) value);
				}
			}			
			HashMap<String, JComboBox> map = view.comboMap; 			
			for (Map.Entry<String, JComboBox> entry : map.entrySet()) {
				key = entry.getKey();
				JComboBox combo = entry.getValue();
				if (combo.isEnabled()){
					value = combo.getSelectedItem();
					if (value == null || ((String)value).trim().equals("")) {
						rs.updateNull(key);						
					} 
					else{
						// Save into hydrology_selection
						if (key.equals("hydrology")) {
							String sql = "UPDATE "+MainDao.getSchema()+".hydrology_selection SET hydrology_id = '"+value+"'";
							MainDao.executeUpdateSql(sql, true);
						}
						else{
							rs.updateString(key, (String) value);
						}
					}
				}
			}
			
			HashMap<String, JTextField> textMap = view.textMap; 			
			for (Map.Entry<String, JTextField> entry : textMap.entrySet()) {
				key = entry.getKey();
				// Check if field exists in the table
				if (MainDao.checkColumn(rs, key)) {
					JTextField component = entry.getValue();
					if (component.isEnabled()){
						value = component.getText();
						int col = rs.findColumn(key);
						int columnType = metadata.getColumnType(col);
						if (columnType == Types.CHAR || columnType == Types.VARCHAR || columnType == Types.LONGVARCHAR) {
							if (value == null || ((String)value).trim().equals("")) {
								rs.updateNull(col);						
							} 
							else {
								rs.updateString(col, (String) value);
							}
						}
						else if (columnType == Types.SMALLINT || columnType == Types.INTEGER || columnType == Types.BIGINT) {
							if (((String)value).trim().equals("")){
								rs.updateNull(col);
							} 
							else {					
								Integer aux = Integer.parseInt(value.toString());
								rs.updateInt(col, aux);						
							}
						}
						else if (columnType == Types.NUMERIC || columnType == Types.DECIMAL || columnType == Types.DOUBLE || 
							columnType == Types.FLOAT || columnType == Types.REAL) {
							if (((String)value).trim().equals("")){
								rs.updateNull(col);
							} 
							else {					
								Double aux = Double.parseDouble(value.toString().replace(",", "."));
								rs.updateDouble(col, aux);						
							}
						}	
						else if (columnType == Types.TIME || columnType == Types.DATE) {
							rs.updateTimestamp(col, (Timestamp) value);
						}			
					}
				}	
			}
			
			if (action.equals("create")) {
				rs.insertRow();
				rs.last();
				total++;
				current = total;
				action = "saved";
				setComponents();
			}
			else if (!action.equals("create_detail")) {
				rs.updateRow();
			}
			
		} catch (SQLException e) {
			Utils.showError(e);
		} catch (Exception e) {
			Utils.showError(e);
			return;
		}
		
	}
	
	
	private void manageNavigationButtons() {
		
		if (action.equals("create")){
			Utils.getLogger().info("Editing new record...");
			view.enablePrevious(false);
			view.enableNext(false);		
			view.enableSave(true);
		}
		else{
			Utils.getLogger().info("Record: " + current + " of " + total);
			view.enablePrevious(current > 1);
			view.enableNext(current < total);
			view.enableSave(current > 0);
		}
		
	}
	
	
	public void moveFirst() {
		
		action = "other";
		try {
			rs.first();
			current = 1;	
			setComponents();
		} catch (SQLException e) {
			Utils.showError(e);
		}
		
	}		
	
	
	public void movePrevious() {
		
		action = "other";
		try {
			if (!rs.isFirst()) {
				rs.previous();
				current--;		
				setComponents();
			}
		} catch (SQLException e) {
			Utils.showError(e);
		}
		
	}
	
	
	public void moveNext() {
		
		action = "other";
		try {
			if (!rs.isLast()) {
				rs.next();
				current++;
				setComponents();
			}
		} catch (SQLException e) {
			Utils.showError(e);
		}
		
	}
	
	
	public void create() {
		
		action = "create";
		try {
			rs.moveToInsertRow();
			setComponents(false);
		} catch (SQLException e) {
			Utils.logError(e);
		}
		
	}	
	

	public void delete() {
		
		action = "other";
		try {
			int res = Utils.showYesNoDialog("delete_record?");
	        if (res == JOptionPane.YES_OPTION){
				rs.deleteRow();				
				rs.first();
				current = 1;
				total--;
				if (total == 0) {
					current = 0;
					if (view instanceof ResultCatDialog || view instanceof ResultCatEpanetDialog){
						view.dispose();
						Utils.showMessage(view, "result_cat_empty");
						return;
					}
				}
				setComponents();
	        }   
		} catch (SQLException e) {
			Utils.logError(e);
		}		
		
	}
	
	
	// Options Epanet
	public void changeCombo(String comboName, String value) {
		
		boolean isVisible = false;
		if (comboName.equals("hydraulics")) {
			if (value.toUpperCase().equals("USE") || value.toUpperCase().equals("SAVE")){
				isVisible = true;
			}
		}
		else if (comboName.equals("unbalanced")) {
			if (value.toUpperCase().equals("CONTINUE")) {
				isVisible = true;
			}
		}		
		else if (comboName.equals("quality")) {
			if (value.toUpperCase().equals("TRACE")) {
				isVisible = true;
			}
		}		
		if (view instanceof OptionsEpanetDialog) {
			OptionsEpanetDialog optionsEpanetDialog = (OptionsEpanetDialog) view;
			optionsEpanetDialog.setComboEnabled(comboName, isVisible);
		}		
		
	}		
	
	
	// ResultSelectionDialog
	public void changeResultSelection() {
		
		if (view instanceof ResultSelectionDialog) {
			ResultSelectionDialog dialog = (ResultSelectionDialog) view;
			String result = dialog.getResultSelection();
	        // Update table: result_selection
	   		MainDao.setResultSelect(MainDao.getSchema(), "result_selection", result);			
		}	
		
	}
	
	
	// Raingage
    public void chooseFileFname() {

        JFileChooser chooser = new JFileChooser();
//        FileFilter filter = new FileNameExtensionFilter("RPT extension file", "rpt");
//        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_fname"));
        File file = new File(System.getProperty("user.home"));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileFname = chooser.getSelectedFile();
    		if (view instanceof RaingageDialog){
    			RaingageDialog child = (RaingageDialog) view;
    			child.setFileFname(fileFname.getAbsolutePath());
    		}
        }

    }	
	
    
    // Raingage
    public void changeRaingageType() {
    	
		if (view instanceof RaingageDialog){
			RaingageDialog child = (RaingageDialog) view;
			String value = child.getRaingageType();
			if (value.equals("FILE")) {
				child.enablePanelFile(true);
			} 
			else {
				child.enablePanelFile(false);
			}
		}
    	
    }
    
	
}