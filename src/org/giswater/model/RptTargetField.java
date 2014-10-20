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


public class RptTargetField {

	private String id;   // targetId + "_" + dbName
	private Integer targetId;
	private String rptName;
	private String dbName;
	// 0: varchar 
	// 1: integer	
	private Integer dbType;   
	
	
	public RptTargetField(ResultSet rs) throws SQLException {
		this.id = rs.getString(1);
		this.targetId = rs.getInt(2);
		this.rptName = rs.getString(3);
		this.dbName = rs.getString(4);
		this.dbType = rs.getInt(5);
	}
	
	
	public String getId() {
		return id;
	}
	
	public Integer getTargetId() {
		return targetId;
	}

	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}

	public String getRptName() {
		return rptName;
	}

	public void setRptName(String rptName) {
		this.rptName = rptName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Integer getDbType() {
		return dbType;
	}

	public void setDbType(Integer dbType) {
		this.dbType = dbType;
	}

	
}