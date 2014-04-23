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
package org.giswater.model.table;

import java.sql.ResultSet;


public class TableModelCurves extends TableModelSuper {

	private static final long serialVersionUID = -3793339630551246161L;
	
	
	public TableModelCurves(ResultSet results) {
		setMetadata(results);
		setResultSet(results);
		this.rs = results;
	}
	
	
	public void setValueAt(Object value, int row, int col) {
		super.setValueAt(value, row, col);
	}	
	
	
    public boolean isCellEditable(int row, int column) {
        return true;
    }	

    
}