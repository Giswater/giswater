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
package org.giswater.gui.panel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;

import org.giswater.dao.MainDao;
import org.giswater.model.table.TableModelSectorSelection;
import org.giswater.util.Utils;


public class SectorSelectionPanel extends JPanel {
	
	private TableModelSectorSelection tableModelSectorSelection;
	private JTable table;
	private JButton btnInsert;
	private JButton btnDelete;
	private JButton btnDeleteAll;
	private JButton btnClose;
	private JDialog dialog;

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	private final String TABLE_SECTOR = "sector";
	private final String TABLE_SECTOR_SELECTION = "sector_selection";
	
	
	public SectorSelectionPanel() {
		initConfig();
		setData();
	}

	
	private void setData() {
		
		if (MainDao.isConnected()) {
			ResultSet rs = MainDao.getTableResultset(TABLE_SECTOR_SELECTION);		
			if (rs == null) return;		
			tableModelSectorSelection = new TableModelSectorSelection(rs, TABLE_SECTOR);
			tableModelSectorSelection.setTable(table);
			table.setModel(tableModelSectorSelection);
			tableModelSectorSelection.setCombos();
			btnInsert.setVisible(true);
			btnDelete.setVisible(true);			
		}
		
	}
	
	public void setParent(JDialog dialog) {
		this.dialog = dialog;
	}
	
	
	private void initConfig() {
		
		setLayout(new MigLayout("", "[100px:200px:400px,grow]", "[25.00][5px][:130px:200px][5px][]"));
		
		JLabel lblTable = new JLabel(BUNDLE.getString("EpaSoft.lblTableSectorSelection")); 
		lblTable.setFont(new Font("Tahoma", Font.BOLD, 14));
		add(lblTable, "cell 0 0,alignx center");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 2,grow");
		
		table = new JTable();
		table.setFont(new Font("Tahoma", Font.PLAIN, 10));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setRowSelectionAllowed(true);
		scrollPane.setViewportView(table);
		
		btnInsert = new JButton(BUNDLE.getString("TableWindowPanel.btnInsert.text")); 
		btnInsert.setMaximumSize(new Dimension(79, 23));
		btnInsert.setMinimumSize(new Dimension(79, 23));
		add(btnInsert, "flowx,cell 0 4,alignx left");
		
		btnDelete = new JButton(BUNDLE.getString("TableWindowPanel.btnDelete.text")); 
		btnDelete.setMinimumSize(new Dimension(79, 23));
		btnDelete.setMaximumSize(new Dimension(79, 23));
		btnDelete.setVisible(false);
		add(btnDelete, "cell 0 4");
		
		btnDeleteAll = new JButton(BUNDLE.getString("TableWindowPanel.btnDeleteAll.text"));
		btnDeleteAll.setMinimumSize(new Dimension(79, 23));
		add(btnDeleteAll, "cell 0 4");
		
		btnClose = new JButton(BUNDLE.getString("TableWindowPanel.btnClose.text"));
		btnClose.setMinimumSize(new Dimension(79, 23));
		btnClose.setMaximumSize(new Dimension(79, 23));
		btnClose.setActionCommand("closePanel");
		add(btnClose, "cell 0 4");

		setupListeners();
		
	}


	private void setupListeners() {
		
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insert();
			}
		});
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delete();
			}
		});		
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteAll();
			}
		});		
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});		
		
	}


	private void insert() {
		tableModelSectorSelection.insertEmptyRow();	
		tableModelSectorSelection.setCombos();
	}
	
	
	private void delete() {
		
    	int rowIndex = table.getSelectedRow();
    	if (rowIndex!=-1) {
    		String value = (String) table.getModel().getValueAt(rowIndex, 0);
    		String msg = Utils.getBundleString("delete_record?") + "\n" + value;
            int res = Utils.showYesNoDialog(msg);
            if (res == JOptionPane.YES_OPTION) {    		
            	tableModelSectorSelection.deleteRow(rowIndex);
            	setData();
            }
    	}
    	else {
    		Utils.showMessage(this, "no_record_selected");
    	}
    	
	}

	
	private void deleteAll() {
		
        int res = Utils.showYesNoDialog("question_delete");
        if (res == JOptionPane.YES_OPTION) {
        	String sql = "DELETE FROM "+MainDao.getSchema()+"."+TABLE_SECTOR_SELECTION;
        	MainDao.executeUpdateSql(sql);
    		setData();
        }
        
	}

	
}