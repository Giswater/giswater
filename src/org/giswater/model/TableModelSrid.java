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
package org.giswater.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.giswater.util.Utils;


public class TableModelSrid extends DefaultTableModel {

	protected String[] columnNames = new String[0];
	protected Vector<String[]> rows_data = new Vector<String[]>();
	protected ResultSetMetaData metadata;
	protected int columns;
	protected JTable table;
	
	protected int row;
	protected int col;
	protected Object value;
	
	
	public TableModelSrid() { }
	
	
	protected void setMetadata(ResultSet results) {

		try {
			metadata = results.getMetaData();
			columns = metadata.getColumnCount();
			columnNames = new String[columns];
			for (int i = 0; i < columns; i++) {
				columnNames[i] = metadata.getColumnLabel(i + 1);
			}	
		} catch (SQLException e) {
			Utils.showError(e);
		}
	
	}

	
	public void setRs(ResultSet rs) {
		setMetadata(rs);
		setResultSet(rs, false);		
	}
	
	
	public void setResultSet(ResultSet results) {
		setResultSet(results, true);
	}

	
	public void setResultSet(ResultSet results, Boolean moveCursor) {
		
		try {
			
			rows_data.clear();
			String[] rowData;
			if (moveCursor){
				results.absolute(0);				
			}
			while (results.next()) {
				rowData = new String[columns];
				for (int i=0; i<columns; i++) {
					rowData[i] = results.getString(i + 1);
				}
				rows_data.addElement(rowData);
			}
			
		} catch (SQLException e) {
			Utils.showError(e.getMessage());
		}

	}
	
	
	public void setTable(JTable table) {
		this.table = table;
	}	
	
	
	public void insertEmptyRow() {
		String[] rowData = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			rowData[i] = "";
		}
		rows_data.addElement(rowData);
		this.fireTableChanged(null);
	}

	
	public void setValueAt(Object value, int row, int col) { }
	
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return rows_data == null ? 0 : rows_data.size();
	}

	public String getValueAt(int row, int column) {
		return rows_data.elementAt(row)[column];
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	
}