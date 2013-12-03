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
package org.giswater.gui.dialog.catalog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.model.table.TableModelTimeseries;
import org.giswater.util.Utils;


public class TimeseriesDialog extends AbstractCatalogDialog{

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField txtId;
	protected JButton btnPrevious;
	protected JButton btnNext;
	protected JButton btnSave;	
	private JButton btnCreate;
	private JButton btnDelete;
	private JLabel lblOther;
	private JTable table;
	private JPanel panelGeneral;
	private JScrollPane panelTable;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBox;
	private JButton btnDetailDelete;
	private JButton btnDetailCreate;
	
	
	public TimeseriesDialog() {
		initConfig();
		createComponentMap();		
	}	

	
	public JTable getTable(){
		return table;
	}
	
	
	public void setTable(TableModelTimeseries model) {

		table = new JTable(model);
		table.setFont(new Font("Tahoma", Font.PLAIN, 10));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setRowSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);
		table.setUpdateSelectionOnSort(false);		
		table.getColumnModel().getColumn(1).setPreferredWidth(5);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setHeaderValue("Resizable");	
		panelTable.setViewportView(table);	
		
		table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    controller.editRecord("edit");
                }
            }
        });				

	}
	
	
	public void setModel(TableModelTimeseries model){
		table.setModel(model);
	}

	
	@SuppressWarnings("rawtypes")
	private void initConfig(){

		setTitle("Table timeseries");
		setBounds(100, 100, 574, 437);
		getContentPane().setLayout(new MigLayout("", "[401.00][200px]", "[341.00][5px][36.00]"));
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());			
		
		panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[20][80][50][120][185,grow]", "[][10px][][grow][][][]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		txtId = new JTextField();
		txtId.setName("id");
		txtId.setColumns(10);
		panelGeneral.add(txtId, "cell 1 0,growx");
		
		JLabel lblTsectid = new JLabel("Type:");
		panelGeneral.add(lblTsectid, "cell 2 0,alignx trailing");
		
		comboBox = new JComboBox();
		comboBox.setName("timser_type");
		panelGeneral.add(comboBox, "cell 3 0,growx");
		
		btnCreate = new JButton("+");
		panelGeneral.add(btnCreate, "flowx,cell 4 0");
		btnCreate.setToolTipText("Insert record");
		btnCreate.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCreate.setActionCommand("create");
		
		lblOther = new JLabel("Timeseries:");
		lblOther.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelGeneral.add(lblOther, "cell 0 2 2 1,alignx left");
		
		panelTable = new JScrollPane();
		panelGeneral.add(panelTable, "cell 0 3 5 4,grow");
		
		btnDelete = new JButton("-");
		panelGeneral.add(btnDelete, "cell 4 0");
		btnDelete.setToolTipText("Delete record");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setActionCommand("delete");
		
		btnPrevious = new JButton("<");
		panelGeneral.add(btnPrevious, "cell 4 0");
		btnPrevious.setToolTipText("Previous record");
		btnPrevious.setActionCommand("movePrevious");
		
		btnNext = new JButton(">");
		panelGeneral.add(btnNext, "cell 4 0");
		btnNext.setToolTipText("Next record");
		btnNext.setActionCommand("moveNext");
		
		btnSave = new JButton("Save");
		panelGeneral.add(btnSave, "cell 4 0");
		btnSave.setToolTipText("Save record");
		btnSave.setActionCommand("saveData");
		
		btnDetailCreate = new JButton("New");
		btnDetailCreate.setMinimumSize(new Dimension(80, 23));
		btnDetailCreate.setToolTipText("Insert new row");
		btnDetailCreate.setActionCommand("detailCreate");
		getContentPane().add(btnDetailCreate, "flowx,cell 1 2");
		
		btnDetailDelete = new JButton("Delete");
		btnDetailDelete.setMinimumSize(new Dimension(80, 23));
		btnDetailDelete.setToolTipText("Delete selected rows");
		btnDetailDelete.setActionCommand("detailDelete");
		getContentPane().add(btnDetailDelete, "cell 1 2");
		
		setupListeners();
		
	}

	
	protected void setupListeners() {
			
		btnPrevious.addActionListener(this);
		btnNext.addActionListener(this);
		btnCreate.addActionListener(this);
		btnDelete.addActionListener(this);
		btnDetailCreate.addActionListener(this);		
		btnDetailDelete.addActionListener(this);
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveData();
				dispose();
			}
		});
		
		super.setupListeners();
		
	}

	
	private boolean deleteConfirm() {
        if (table.getSelectedRows().length > 0) {
            int res = Utils.confirmDialog("delete_selected_records?");
            return (res == 0);
        }
        return false;
    }
    
	
	public String detailDelete() {

        String listId = "";
        if (deleteConfirm()) {
        	int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length > 0) {
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                	Object aux = table.getModel().getValueAt(table.getSelectedRows()[i], 0);
                	Integer id = Integer.parseInt(aux.toString());
                	listId += id+", ";
                }
                listId = listId.substring(0, listId.length() - 2);
            }
        }
        return listId;
        
    }


	public String getTimserId() {
		return txtId.getText().trim();
	}
    
    
}