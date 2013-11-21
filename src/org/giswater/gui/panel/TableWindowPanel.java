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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.giswater.dao.MainDao;
import org.giswater.model.TableModelCatchment;
import org.giswater.util.Utils;

import net.miginfocom.swing.MigLayout;


public class TableWindowPanel extends JPanel {
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$
	private static final long serialVersionUID = 7046850563517014315L;

	private TableModelCatchment tableModelCatchment;
	private JTable table;
	private JButton btnInsert;
	private JButton btnDelete;
	private JButton btnDeleteAll;
	private String schema;

	
	public TableWindowPanel(String schema) {
		this.schema = schema;
		initConfig();
		setData();
	}

	
	private void setData(){
		if (MainDao.schema == null){
			MainDao.setSchema(schema);
		}
		ResultSet rs = MainDao.getTableResultset("sector_selection");		
		if (rs == null) return;		
		tableModelCatchment = new TableModelCatchment(rs, "sector");
		tableModelCatchment.setTable(table);
		table.setModel(tableModelCatchment);
		tableModelCatchment.setCombos();
		btnInsert.setVisible(true);
		btnDelete.setVisible(true);			
	}
	
	
	private void initConfig(){
		
		setLayout(new MigLayout("", "[10px][100px:200px:400px,grow][12]", "[25.00][8px][:130px:200px][8px][]"));
		
		JLabel lblTable = new JLabel("Table sector_selection");
		lblTable.setFont(new Font("Tahoma", Font.BOLD, 14));
		add(lblTable, "cell 1 0,alignx center");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 1 2,grow");
		
		table = new JTable();
		table.setFont(new Font("Tahoma", Font.PLAIN, 10));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setRowSelectionAllowed(true);
		scrollPane.setViewportView(table);
		
		btnInsert = new JButton(BUNDLE.getString("TableWindowPanel.btnInsert.text")); //$NON-NLS-1$
		btnInsert.setMinimumSize(new Dimension(79, 23));
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insert();
			}
		});
		add(btnInsert, "flowx,cell 1 4,alignx left");
		
		btnDelete = new JButton(BUNDLE.getString("TableWindowPanel.btnDelete.text")); //$NON-NLS-1$
		btnDelete.setMinimumSize(new Dimension(79, 23));
		btnDelete.setMaximumSize(new Dimension(79, 23));
		btnDelete.setVisible(false);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delete();
			}
		});
		add(btnDelete, "cell 1 4");
		
		btnDeleteAll = new JButton(BUNDLE.getString("TableWindowPanel.btnDeleteAll.text")); //$NON-NLS-1$
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteAll();
			}
		});
		add(btnDeleteAll, "cell 1 4");
		
	}


	private void insert() {
		tableModelCatchment.insertEmptyRow();	
		tableModelCatchment.setCombos();
	}
	
	
	private void delete(){
    	int rowIndex = table.getSelectedRow();
    	tableModelCatchment.deleteRow(rowIndex);
    	setData();
	}

	
	private void deleteAll(){
		String msg = Utils.getBundleString("question_delete");
        int res = JOptionPane.showConfirmDialog(this, msg, "Giswater", JOptionPane.YES_NO_OPTION);
        if (res == 0){
        	String sql = "DELETE FROM "+MainDao.schema+".sector_selection";
        	MainDao.executeUpdateSql(sql);
    		setData();
        }
	}
	
	
}