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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class RptTarget {

	private Integer id;
	private String table;
	private String description;
	private Integer type;
	private Integer titleLines;
	private Integer tokens;
	private Integer parametrized;
	private HashMap<String, RptTargetField> mapRptTargetFields;
	private ArrayList<String> columnNames;
	private ArrayList<Integer> columnTypes;
	
	
	public RptTarget(ResultSet rs) throws SQLException {
		
		this.id = rs.getInt(1);
		this.table = rs.getString(2);
		this.description = rs.getString(3);
		this.type = rs.getInt(4);
		this.titleLines = rs.getInt(5);
		this.tokens = rs.getInt(6);
		this.parametrized = rs.getInt(7);
		this.mapRptTargetFields = new HashMap<String, RptTargetField>();
		this.columnNames = new ArrayList<String>();
		this.columnTypes = new ArrayList<Integer>();
		
	}
	
	
	public Integer getId() {
		return id;
	}

	public String getTable() {
		return table;
	}

	public String getDescription() {
		return description;
	}

	public Integer getType() {
		return type;
	}
	
	public Integer getTitleLines() {
		return titleLines;
	}

	public Integer getTokens() {
		return tokens;
	}	
	
	public Integer getParametrized() {
		return parametrized;
	}	

	public void addRptTargetField(RptTargetField rptTargetField) {
		mapRptTargetFields.put(rptTargetField.getId(), rptTargetField);
	}
	
	
	public void addColumnName(String columnName){
		columnNames.add(columnName);
	}
	
	public void addColumnType(Integer columnType){
		columnTypes.add(columnType);
	}
	
	public String getColumnName(int index){
		index--;
		if (index < columnNames.size()){
			return columnNames.get(index);
		}
		return "";
	}
	
	public Integer getColumnType(int index){
		index--;
		if (index < columnTypes.size()){
			return columnTypes.get(index);
		}
		return -1;
	}
	
	public int getColumnCount() {
		return columnNames.size();
	}
	
	
}