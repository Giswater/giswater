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

import java.awt.Cursor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.giswater.dao.MainDao;
import org.giswater.gui.dialog.options.AbstractOptionsDialog;
import org.giswater.gui.dialog.options.OptionsEpanetDialog;
import org.giswater.gui.dialog.options.ResultCatDialog;
import org.giswater.gui.dialog.options.ResultCatEpanetDialog;
import org.giswater.gui.dialog.options.ResultSelectionDialog;
import org.giswater.util.Utils;


public class OptionsController {

	private AbstractOptionsDialog view;
    private ResultSet rs;
	private String action;
	private Integer current;
	private Integer total;
	
	
	public OptionsController(AbstractOptionsDialog dialog, ResultSet rs) {
		this.view = dialog;
        this.rs = rs;
        this.current = 1;
        this.total = MainDao.getRowCount(rs);
	    view.setController(this);        
	}
	
	
	public void action(String actionCommand) {
		
		Method method;
		try {
			if (Utils.getLogger() != null){
				Utils.getLogger().info(actionCommand);
			}
			method = this.getClass().getMethod(actionCommand);
			method.invoke(this);	
			view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));			
		} catch (Exception e) {
			view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			if (Utils.getLogger() != null){			
				Utils.logError(e);
			} else{
				Utils.showError(e);
			}
		}
		
	}	
	
	
	public boolean setComponents(){
		return setComponents(true);
	}
	
	
	// Update ComboBox items and selected item
	@SuppressWarnings({"unchecked", "rawtypes"})	
	public boolean setComponents(boolean fillData){

		try {
			
			HashMap<String, JComboBox> map = view.comboMap; 
			for (Map.Entry<String, JComboBox> entry : map.entrySet()) {
			    String key = entry.getKey();
			    JComboBox combo = entry.getValue();
				view.setComboModel(combo, getComboValues(key));
				String value = "";
				if (fillData){
					value = rs.getString(key);
				}
				view.setComboSelectedItem(combo, value);
			}
			HashMap<String, JTextField> textMap = view.textMap; 
			for (Map.Entry<String, JTextField> entry : textMap.entrySet()) {
			    String key = entry.getKey();
			    JTextField component = entry.getValue();
			    Object value = "";
				if (fillData){
					value = rs.getObject(key);
				}
				view.setTextField(component, value);
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
		if (comboName.equals("form_type")){
			tableName = "inp_value_raingage";
		}
		else if (comboName.equals("timser_id")){
			tableName = "inp_timser_id";
		}
		else if (comboName.equals("rgage_type")){
			tableName = "inp_typevalue_raingage";
		}		
		
		// Options
		else if (comboName.equals("flow_units")){
			tableName = "inp_value_options_fu";
		}
		else if (comboName.equals("infiltration")){
			tableName = "inp_value_options_in";
		}
		else if (comboName.equals("force_main_equation")){
			tableName = "inp_value_options_fme";
		}
		else if (comboName.equals("flow_routing")){
			tableName = "inp_value_options_fr";
		}
		else if (comboName.equals("inertial_damping")){
			tableName = "inp_value_options_id";
		}
		else if (comboName.equals("link_offsets")){
			tableName = "inp_value_options_lo";
		}
		else if (comboName.equals("normal_flow_limited")){
			tableName = "inp_value_options_nfl";
		}	

		// Options Epanet
		else if (comboName.equals("units")){
			tableName = "inp_value_opti_units";
		}
		else if (comboName.equals("pattern")){
			tableName = "inp_pattern";
		}
		else if (comboName.equals("hydraulics")){
			tableName = "inp_value_opti_hyd";
		}
		else if (comboName.equals("quality")){
			tableName = "inp_value_yesno";
		}
		else if (comboName.equals("unbalanced")){
			tableName = "inp_value_opti_unbal";
		}		
		
		// Report
		else if (comboName.equals("status")){
			tableName = "inp_value_yesnofull";
		}		

		// ResultSelection
		else if (comboName.equals("result_id")){
			tableName = "rpt_result_cat";
			fields = "result_id";
		}	
		
		// Times
		else if (comboName.equals("statistic")){
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

		String key;
		Object value;
		try {
			ResultSetMetaData metadata = rs.getMetaData();					
			HashMap<String, JComboBox> map = view.comboMap; 			
			for (Map.Entry<String, JComboBox> entry : map.entrySet()) {
				key = entry.getKey();
				JComboBox combo = entry.getValue();
				value = combo.getSelectedItem();
				if (value == null || ((String)value).trim().equals("")){
					rs.updateNull(key);						
				} 
				else{
					rs.updateString(key, (String) value);
				}				
			}
			HashMap<String, JTextField> textMap = view.textMap; 	
			
			for (Map.Entry<String, JTextField> entry : textMap.entrySet()) {
				key = entry.getKey();
				JTextField component = entry.getValue();
				value = component.getText();
				int col = rs.findColumn(key);
				int columnType = metadata.getColumnType(col);
				if (columnType == Types.CHAR || columnType == Types.VARCHAR || columnType == Types.LONGVARCHAR) {
					if (value == null || ((String)value).trim().equals("")){
						rs.updateNull(col);						
					} 
					else{
						rs.updateString(col, (String) value);
					}
				}
				else if (columnType == Types.SMALLINT || columnType == Types.INTEGER || columnType == Types.BIGINT) {
					if (((String)value).trim().equals("")){
						rs.updateNull(col);
					} 
					else{					
						Integer aux = Integer.parseInt(value.toString());
						rs.updateInt(col, aux);						
					}
				}
				else if (columnType == Types.NUMERIC || columnType == Types.DECIMAL || columnType == Types.DOUBLE || 
					columnType == Types.FLOAT || columnType == Types.REAL) {
					if (((String)value).trim().equals("")){
						rs.updateNull(col);
					} 
					else{					
						Double aux = Double.parseDouble(value.toString().replace(",", "."));
						rs.updateDouble(col, aux);						
					}
				}	
				else if (columnType == Types.TIME || columnType == Types.TIMESTAMP || columnType == Types.DATE) {
					rs.updateTimestamp(col, (Timestamp) value);
				}				
			}
			
			if (action.equals("create")){
				rs.insertRow();
				rs.last();
				total++;
				current = total;
				action = "saved";
				setComponents();
			}
			else if (!action.equals("create_detail")){
				rs.updateRow();
			}
			view.dispose();
			
		} catch (SQLException e) {
			Utils.showError(e);
		} catch (Exception e) {
			Utils.showError(e);
			return;
		}
		
	}
	
	
	private void manageNavigationButtons(){
		
		if (action.equals("create")){
			Utils.getLogger().info("Editing new record...");
//			view.enableDelete(false);
			view.enablePrevious(false);
			view.enableNext(false);		
			view.enableSave(true);
		}
		else{
			Utils.getLogger().info("Record: " + current + " of " + total);
//			view.enableDelete(total > 0);
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
	
	
	public void movePrevious(){
		
		action = "other";
		try {
			if (!rs.isFirst()){
				rs.previous();
				current--;		
				setComponents();
			}
		} catch (SQLException e) {
			Utils.showError(e);
		}
		
	}
	
	
	public void moveNext(){
		
		action = "other";
		try {
			if (!rs.isLast()){
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
	

	public void delete(){
		
		action = "other";
		try {
			int res = Utils.confirmDialog("delete_record?");
	        if (res == 0){
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
		if (comboName.equals("hydraulics")){
			if (value.toUpperCase().equals("USE") || value.toUpperCase().equals("SAVE")){
				isVisible = true;
			}
		}
		else if (comboName.equals("unbalanced")){
			if (value.toUpperCase().equals("CONTINUE")){
				isVisible = true;
			}
		}		
		else if (comboName.equals("quality")){
			if (value.toUpperCase().equals("TRACE")){
				isVisible = true;
			}
		}		
		if (view instanceof OptionsEpanetDialog){
			OptionsEpanetDialog optionsEpanetDialog = (OptionsEpanetDialog) view;
			optionsEpanetDialog.setComboVisible(comboName, isVisible);
		}		
		
	}		
	
	
	// ResultSelectionDialog
	public void changeResultSelection(){
		
		if (view instanceof ResultSelectionDialog){
			ResultSelectionDialog dialog = (ResultSelectionDialog) view;
			String result = dialog.getResultSelection();
	        // Update table: result_selection
	   		MainDao.setResultSelect(MainDao.getSchema(), "result_selection", result);			
		}	
		
	}
	
	
}