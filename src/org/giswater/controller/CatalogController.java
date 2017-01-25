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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.giswater.dao.MainDao;
import org.giswater.gui.dialog.catalog.AbstractCatalogDialog;
import org.giswater.gui.dialog.catalog.CurvesDialog;
import org.giswater.gui.dialog.catalog.TimeseriesDetailDialog;
import org.giswater.gui.dialog.catalog.TimeseriesDialog;
import org.giswater.model.table.TableModelCurves;
import org.giswater.model.table.TableModelTimeseries;
import org.giswater.util.Utils;


public class CatalogController extends AbstractController {

	private AbstractCatalogDialog view;
    private ResultSet rs;
	private String action;
	private Integer current;
	private Integer total;
	
	
	public CatalogController(AbstractCatalogDialog dialog, ResultSet rs) {
		this.view = dialog;
        this.rs = rs;
        this.current = 1;
        this.total = MainDao.getNumberOfRows(rs);
	    view.setController(this);        
	}

	
	public void setComponents() {
		setComponents(true);
	}
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void setComponents(boolean fillData) {

		try {
			// Update ComboBox items and selected item with values from current ResultSet row			
			HashMap<String, JComboBox> map = view.comboMap; 
			if (map == null) return;
			for (Map.Entry<String, JComboBox> entry : map.entrySet()) {
			    String key = entry.getKey();
			    Vector vector = getComboValues(key);			    
			    JComboBox combo = entry.getValue();
				view.setComboModel(combo, vector);
				String value = "";
				if (fillData && MainDao.checkColumn(rs, key)) {
					if (rs.getRow() != 0) {
						value = rs.getString(key);
					}
				}
				view.setComboSelectedItem(combo, value);
			}
			// Update textField items with values from current ResultSet row
			HashMap<String, JTextField> textMap = view.textMap; 
			for (Map.Entry<String, JTextField> entry : textMap.entrySet()) {
			    String key = entry.getKey();
			    JTextField component = entry.getValue();
			    Object value = "";
				if (fillData && MainDao.checkColumn(rs, key)) {
					if (rs.getRow() != 0) {					
						value = rs.getObject(key);
					}
				} 
				view.setTextField(component, value);
			}

			// Update also detail table content
			if (view instanceof TimeseriesDialog) {
				String id = "-1";
				if (!action.equals("create")) {
					if (rs.getRow() != 0){	
						id = rs.getString("id");
					}
				}
				String fields = "id, date, hour, time, value, fname";
				updateDetailTable("inp_timeseries", fields, "timser_id", id);
			}
			else if (view instanceof CurvesDialog) {
				String id = "-1";
				if (!action.equals("create")) {
					if (rs.getRow() != 0){	
						id = rs.getString("id");
					}					
				}
				updateDetailTable("inp_curve", "*", "curve_id", id);					
			}	
			manageNavigationButtons();
			
		} catch (SQLException e) {
			Utils.logError(e);
		}
				
	}
	
	
	// Get ComboBox items from Database
	public Vector<String> getComboValues(String comboName) {

		Vector<String> values = null;
		String tableName = "";
		if (comboName.equals("shape")) {
			tableName = "inp_value_catarc";
		}
		else if (comboName.equals("pattern_type")) {
			tableName = "inp_typevalue_pattern";
		}
		else if (comboName.equals("timser_type")) {
			tableName = "inp_value_timserid";
		}	
		else if (comboName.equals("times_type")) {
			tableName = "inp_typevalue_timeseries";
		}		
		else if (comboName.equals("timser_id")) {
			tableName = "inp_timser_id";
		}		
		else if (comboName.equals("curve_type")) {
			tableName = "inp_value_curve";
		}		
		else if (comboName.equals("infiltration")) {
			tableName = "inp_value_options_in";
		}
		else if (comboName.equals("node_id")) {
			tableName = "node";
		}		
		else if (comboName.equals("pattern_id")) {
			tableName = "inp_pattern";
		}		
		values = MainDao.getTable(tableName, null);
		
		return values;
		
	}

	
	// Update Table record
	@SuppressWarnings("rawtypes")
	public void saveData() {

		try {
			
			String key;
			Object value;			
			ResultSetMetaData metadata = rs.getMetaData();					
			HashMap<String, JComboBox> map = view.comboMap; 			
			for (Map.Entry<String, JComboBox> entry : map.entrySet()) {
				key = entry.getKey();
				JComboBox combo = entry.getValue();
				if (combo.isEnabled()){
					value = combo.getSelectedItem();
					if (value == null || ((String)value).trim().equals("")) {
						rs.updateNull(key);						
					} 
					else {
						rs.updateString(key, (String) value);
					}	
				}
			}
			
			HashMap<String, JTextField> textMap = view.textMap; 			
			for (Map.Entry<String, JTextField> entry : textMap.entrySet()) {
				key = entry.getKey();
				JTextField component = entry.getValue();
				if (component.isEnabled()) {
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
						if (((String)value).trim().equals("")) {
							if (!key.equals("id")) {
								rs.updateNull(col);
							}
						} 
						else {					
							Integer aux = Integer.parseInt(value.toString());
							rs.updateInt(col, aux);						
						}
					}
					else if (columnType == Types.NUMERIC || columnType == Types.DECIMAL || columnType == Types.DOUBLE || 
						columnType == Types.FLOAT || columnType == Types.REAL) {
						if (((String)value).trim().equals("")) {
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
		}
		
	}
	
	
	private void manageNavigationButtons() {
		
		if (action.equals("create")) {
			Utils.getLogger().info("Editing new record...");
			view.enableDelete(false);
			view.enablePrevious(false);
			view.enableNext(false);		
			view.enableSave(true);
		}
		else {
			Utils.getLogger().info("Record: " + current + " of " + total);
			view.enableDelete(total > 0);
			view.enablePrevious(current > 1);
			view.enableNext(current < total);
			view.enableSave(current > 0);
		}
		
	}
	
	
	public void moveFirst() {
		
		action = "other";
		try {
			if (rs.isBeforeFirst() ) {    
				rs.first();
			}
			current = 1;			
			setComponents();
		} catch (SQLException e) {
			Utils.logError(e);
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
			Utils.logError(e);
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
			Utils.logError(e);
		}
		
	}

	
	public void create() {
		create("create");
	}		
	
	public void create(String action) {
		
		this.action = action;
		try {
			rs.moveToInsertRow();
			setComponents(false);
		} catch (SQLException e) {
			Utils.logError(e);
		}
		
	}

	
	public void createCurve(ResultSet rs, String curveId) {
		
		this.action = "create_curve";
		try {
			rs.moveToInsertRow();
			updateDetailTable("inp_curve", "*", "curve_id", curveId);
		} catch (SQLException e) {
			Utils.logError(e);
		}
		
	}

	
	public void delete() {
		
		action = "other";
		try {
			int res = Utils.showYesNoDialog(Utils.getBundleString("CatalogController.delete_record")); //$NON-NLS-1$
	        if (res == JOptionPane.YES_OPTION) {
				rs.deleteRow();
				rs.first();
				current = 1;
				total--;
				if (total == 0) current = 0;
				setComponents();
	        }   
		} catch (SQLException e) {
			Utils.showError(e);
		}	
		
	}
	
	
	public void closeWindow() {
		view.dispose();
	}
	
	
	public void updateDetailTable(String table, String fields, String fieldId, String valueId) throws SQLException {
		
		String sql = "SELECT "+fields+" FROM "+MainDao.getSchema()+"."+table+
			" WHERE "+fieldId+" = '"+valueId+"'";
		if (table.equals("inp_timeseries")) {
			sql+= " ORDER BY time";
			ResultSet rsRelated = MainDao.getResultset(sql);			
			TableModelTimeseries model = new TableModelTimeseries(rsRelated);
			TimeseriesDialog dialog = (TimeseriesDialog) view;
			dialog.setModel(model);
		}
		else if (table.equals("inp_curve")) {
			if (action.equals("create_curve")) {
				String sql2 = "INSERT INTO "+MainDao.getSchema()+"."+table+" ("+fieldId+") VALUES ('"+valueId+"')";
				MainDao.executeSql(sql2);
			}
			ResultSet rsRelated = MainDao.getResultset(sql);			
			TableModelCurves model = new TableModelCurves(rsRelated);
			CurvesDialog dialog = (CurvesDialog) view;
			dialog.setModel(model);
		}

	}	
	
	
	// Only for TimeseriesDialog
	public void detailCreateTimeseries(String timesType, String timserId) {
		
		if (view instanceof TimeseriesDialog) {
        	String sql = "SELECT * FROM "+MainDao.getSchema()+".inp_timeseries";
        	ResultSet rs = MainDao.getResultset(sql);
    		if (rs == null) return;
			TimeseriesDialog tsDialog = (TimeseriesDialog) view;
        	TimeseriesDetailDialog detailDialog = new TimeseriesDetailDialog(tsDialog, timesType);
        	CatalogController controller = new CatalogController(detailDialog, rs);
        	// Open dialog form to create new record
        	controller.create();
        	detailDialog.setTimserId(timserId);
        	detailDialog.setModal(true);
        	detailDialog.setLocationRelativeTo(null);   
        	detailDialog.setVisible(true);   	
		}
		
	}	
	
	
	// Only for CurvesDialog
	public void detailCreateCurves(String curveId) {
		
		if (view instanceof CurvesDialog) {		
        	String sql = "SELECT * FROM "+MainDao.getSchema()+".inp_curve";
        	sql+= " WHERE curve_id = '"+curveId+"'";
        	ResultSet rs = MainDao.getResultset(sql);
    		if (rs == null) return;
   			createCurve(rs, curveId);
		}
		
	}		
	
	
	// For TimeseriesDialog and CurvesDialog
	public void detailDelete() {
		
		if (view instanceof TimeseriesDialog) {
			TimeseriesDialog tsDialog = (TimeseriesDialog) view;
			String listId = tsDialog.detailDelete();
			if (!listId.equals("")) {
            	String sql = "DELETE FROM "+MainDao.getSchema()+".inp_timeseries WHERE id IN ("+listId+")";
            	Utils.getLogger().info(sql);
            	MainDao.executeSql(sql);       
            	setComponents();
			}
		}
		else if (view instanceof CurvesDialog) {		
			CurvesDialog curvesDialog = (CurvesDialog) view;
			String listId = curvesDialog.detailDelete();
			if (!listId.equals("")){
            	String sql = "DELETE FROM "+MainDao.getSchema()+".inp_curve WHERE id IN ("+listId+")";
            	Utils.getLogger().info(sql);
            	MainDao.executeSql(sql);       
            	setComponents();
			}
		}
		
	}
	
	
	// Only for TimeseriesDialog	
	public void editRecord(String action, String timesType) {
    	
		if (view instanceof TimeseriesDialog) {
			TimeseriesDialog tsDialog = (TimeseriesDialog) view;
	    	int row = tsDialog.getTable().getSelectedRow();
	        if (row != -1) {
	        	Object aux = tsDialog.getTable().getModel().getValueAt(row, 0);
	        	Integer id = Integer.parseInt(aux.toString());
	        	String sql = "SELECT * FROM "+MainDao.getSchema()+".inp_timeseries WHERE id = "+id;
	        	ResultSet rs = MainDao.getResultset(sql);
	    		if (rs == null) return;	        	
	        	TimeseriesDetailDialog detailDialog = new TimeseriesDetailDialog(tsDialog, timesType);
	        	CatalogController controller = new CatalogController(detailDialog, rs);
	        	// Open dialog form to edit selected row
	        	controller.moveFirst();
	        	detailDialog.setModal(true);
	        	detailDialog.setLocationRelativeTo(null);   
	        	detailDialog.setVisible(true);	
	        }
		}
		
    }	
    
	
	// Only for TimeseriesDetailDialog
	public void timesTypeChanged() {
		if (view instanceof TimeseriesDetailDialog) {
			TimeseriesDetailDialog tsDialog = (TimeseriesDetailDialog) view;
			tsDialog.setTimesType();
		}
	}
	

	// Only for TimeseriesDetailDialog	
	public void closeDetailDialog() {
		action = "other";
		setComponents();
	}
	
	
    public void chooseFileFname() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_fname"));
        File file = new File(System.getProperty("user.home"));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileFname = chooser.getSelectedFile();
            view.setFileFname(fileFname.getAbsolutePath());
        }

    }	
	
	
}