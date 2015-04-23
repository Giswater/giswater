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

import org.giswater.model.table.TableModelCurves;
import org.giswater.util.MaxLengthTextDocument;
import org.giswater.util.Utils;
import java.util.ResourceBundle;


public class CurvesDialog extends AbstractCatalogDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private JTable table;
	private JTextField txtId;
	private JScrollPane panelTable;
	private JButton btnDetailDelete;
	private JButton btnDetailCreate;
	
	
	public CurvesDialog() {
		initConfig();
		createComponentMap();		
	}	

	
	public JTable getTable() {
		return table;
	}
	
	
	public void setTable(TableModelCurves model) {

		table = new JTable(model);
		table.setFont(new Font("Tahoma", Font.PLAIN, 10));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		table.getColumnModel().getColumn(1).setPreferredWidth(5);
		table.getColumnModel().getColumn(1).setResizable(false);
		panelTable.setViewportView(table);	
		
		table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    controller.editRecord("edit", "");
                }
            }
        });				

	}
	
	
	public void setModel(TableModelCurves model) {
		table.setModel(model);
	}

	
	private void initConfig() {

		setTitle(BUNDLE.getString("CurvesDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 574, 437);
		getContentPane().setLayout(new MigLayout("", "[401][260px]", "[341.00][5px][36.00]"));
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());			
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("CurvesDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[20][80][50][120][185,grow]", "[][10px][][grow][][][]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		txtId = new JTextField();
		txtId.setName("id");
		txtId.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(txtId, "cell 1 0,growx");
		
		JLabel lblTsectid = new JLabel(BUNDLE.getString("CurvesDialog.lblTsectid.text")); //$NON-NLS-1$
		panelGeneral.add(lblTsectid, "cell 2 0,alignx trailing");
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setName("curve_type");
		panelGeneral.add(comboBox, "cell 3 0,growx");
		
		btnCreate = new JButton("+");
		panelGeneral.add(btnCreate, "flowx,cell 4 0");
		btnCreate.setToolTipText(BUNDLE.getString("CurvesDialog.btnInsert.toolTipText"));
		btnCreate.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCreate.setActionCommand("create");
		
		JLabel lblOther = new JLabel(BUNDLE.getString("CurvesDialog.lblOther.text")); //$NON-NLS-1$
		lblOther.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelGeneral.add(lblOther, "cell 0 2 2 1,alignx left");
		
		panelTable = new JScrollPane();
		panelGeneral.add(panelTable, "cell 0 3 5 4,grow");
		
		btnDelete = new JButton("-");
		panelGeneral.add(btnDelete, "cell 4 0");
		btnDelete.setToolTipText(BUNDLE.getString("CurvesDialog.btnDelete.toolTipText"));
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setActionCommand("delete");
		
		btnPrevious = new JButton("<");
		panelGeneral.add(btnPrevious, "cell 4 0");
		btnPrevious.setToolTipText(BUNDLE.getString("CurvesDialog.btnPrevious.toolTipText"));
		btnPrevious.setActionCommand("movePrevious");
		
		btnNext = new JButton(">");
		panelGeneral.add(btnNext, "cell 4 0");
		btnNext.setToolTipText(BUNDLE.getString("CurvesDialog.btnNext.toolTipText"));
		btnNext.setActionCommand("moveNext");
		
		btnDetailCreate = new JButton(BUNDLE.getString("CurvesDialog.btnDetailCreate.text"));
		btnDetailCreate.setToolTipText(BUNDLE.getString("CurvesDialog.btnDetailCreate.toolTipText")); //$NON-NLS-1$
		btnDetailCreate.setActionCommand("detailCreateCurves");
		getContentPane().add(btnDetailCreate, "flowx,cell 1 2");
		
		btnDetailDelete = new JButton(BUNDLE.getString("CurvesDialog.btnDetailDelete.text")); //$NON-NLS-1$
		btnDetailDelete.setToolTipText(BUNDLE.getString("CurvesDialog.btnDetailDelete.toolTipText")); //$NON-NLS-1$
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
				controller.detailCreateCurves(id);
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