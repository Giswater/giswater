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
package org.giswater.gui.dialog.catalog;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.model.table.TableModelTimeseries;
import org.giswater.util.Utils;


public class TimeseriesDialog extends AbstractCatalogDialog {
	
	private JTextField txtId;
	private JComboBox<String> cboTimesType;
	private JTable table;
	private JScrollPane panelTable;
	private JButton btnDetailCreate;
	private JButton btnDetailDelete;	
	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form");	
	
	
	public TimeseriesDialog() {
		initConfig();
		createComponentMap();		
	}	


	public String getTimserId() {
		return txtId.getText().trim();
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
		table.getColumnModel().getColumn(1).setPreferredWidth(5);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setHeaderValue("Resizable");	
		panelTable.setViewportView(table);	
		
		table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                	String timesType = cboTimesType.getSelectedItem().toString();
                    controller.editRecord("edit", timesType);
                }
            }
        });				

	}
	
	
	public void setModel(TableModelTimeseries model) {
		table.setModel(model);
	}

	
	private void initConfig() {

		setTitle(BUNDLE.getString("TimeseriesDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 574, 437);
		getContentPane().setLayout(new MigLayout("", "[401.00][260px]", "[363.00][5px][36.00]"));
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());			
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("TimeseriesDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[70][161.00][30][185,grow]", "[][][][5px][][195.00,grow]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		txtId = new JTextField();
		txtId.setName("id");
		txtId.setColumns(10);
		panelGeneral.add(txtId, "cell 1 0,growx");
		
		btnCreate = new JButton("+");
		panelGeneral.add(btnCreate, "flowx,cell 3 0");
		btnCreate.setToolTipText(BUNDLE.getString("TimeseriesDialog.btnInsert.toolTipText"));
		btnCreate.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCreate.setActionCommand("create");
		
		JLabel lblTsectid = new JLabel(BUNDLE.getString("TimeseriesDialog.lblTsectid.text")); //$NON-NLS-1$
		panelGeneral.add(lblTsectid, "cell 0 1,alignx trailing");
		
		JComboBox<String> cboTimserType = new JComboBox<String>();
		cboTimserType.setName("timser_type");
		panelGeneral.add(cboTimserType, "cell 1 1,growx");
		
		JLabel lblTimesType = new JLabel(BUNDLE.getString("TimeseriesDialog.lblTimesType.text")); //$NON-NLS-1$
		panelGeneral.add(lblTimesType, "cell 0 2,alignx trailing");
		
		cboTimesType = new JComboBox<String>();
		cboTimesType.setName("times_type");
		panelGeneral.add(cboTimesType, "cell 1 2,growx");
		
		JLabel lblOther = new JLabel(BUNDLE.getString("TimeseriesDialog.lblOther.text")); //$NON-NLS-1$
		lblOther.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelGeneral.add(lblOther, "cell 0 4 2 1,alignx left");
		
		panelTable = new JScrollPane();
		panelGeneral.add(panelTable, "cell 0 5 4 1,grow");
		
		btnDelete = new JButton("-");
		panelGeneral.add(btnDelete, "cell 3 0");
		btnDelete.setToolTipText(BUNDLE.getString("TimeseriesDialog.btnDelete.toolTipText"));
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setActionCommand("delete");
		
		btnPrevious = new JButton("<");
		panelGeneral.add(btnPrevious, "cell 3 0");
		btnPrevious.setToolTipText(BUNDLE.getString("TimeseriesDialog.btnPrevious.toolTipText"));
		btnPrevious.setActionCommand("movePrevious");
		
		btnNext = new JButton(">");
		btnNext.setToolTipText(BUNDLE.getString("TimeseriesDialog.btnNext.toolTipText"));
		btnNext.setActionCommand("moveNext");
		panelGeneral.add(btnNext, "cell 3 0");
		
		btnDetailCreate = new JButton(BUNDLE.getString("TimeseriesDialog.btnDetailCreate.text"));
		btnDetailCreate.setToolTipText(BUNDLE.getString("TimeseriesDialog.btnDetailCreate.toolTipText")); //$NON-NLS-1$
		btnDetailCreate.setActionCommand("detailCreate");
		getContentPane().add(btnDetailCreate, "flowx,cell 1 2");
		
		btnDetailDelete = new JButton(BUNDLE.getString("TimeseriesDialog.btnDetailDelete.text")); //$NON-NLS-1$
		btnDetailDelete.setToolTipText(BUNDLE.getString("TimeseriesDialog.btnDetailDelete.toolTipText")); //$NON-NLS-1$
		btnDetailDelete.setActionCommand("detailDelete");
		getContentPane().add(btnDetailDelete, "cell 1 2");
		
		btnSave = new JButton(BUNDLE.getString("Generic.btnSave.text"));
		btnSave.setToolTipText(BUNDLE.getString("Generic.btnSave.toolTipText"));
		btnSave.setActionCommand("saveData");
		getContentPane().add(btnSave, "cell 1 2,alignx right");			
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text"));
		btnClose.setToolTipText(BUNDLE.getString("Generic.btnClose.toolTipText"));
		btnClose.setActionCommand("closeWindow");
		getContentPane().add(btnClose, "cell 1 2,alignx right");		
		
		setupListeners();
		
	}

	
	protected void setupListeners() {
			
		btnDetailDelete.addActionListener(this);
		btnDetailCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveData();
				String id = txtId.getText().trim();
            	String timesType = cboTimesType.getSelectedItem().toString();				
				controller.detailCreateTimeseries(timesType, id);
			}
		});
		super.setupListeners();
		
	}

	
	private boolean deleteConfirm() {
        if (table.getSelectedRows().length > 0) {
            int res = Utils.showYesNoDialog("delete_selected_records?");
            return (res == JOptionPane.YES_OPTION);
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
    

}