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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;


public class HydrologyCatalogDialog extends AbstractCatalogDialog{

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField txtId;
	private JTextField txtDescription;
	private JComboBox<String> cboInfiltration;
	protected JButton btnPrevious;
	protected JButton btnNext;
	protected JButton btnCreate;
	protected JButton btnDelete;	
	
	
	public HydrologyCatalogDialog() {
		initConfig();
		createComponentMap();
	}	

	public void enablePrevious(boolean enable){
		if (btnPrevious != null){
			btnPrevious.setEnabled(enable);
		}
	}
	
	public void enableNext(boolean enable){
		if (btnNext != null){
			btnNext.setEnabled(enable);
		}
	}		

	private void initConfig(){

		setTitle("Table cat_hydrology");
		setBounds(100, 100, 502, 170);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[78.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[65.00][150][10px][80px][150]", "[][]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		txtId = new JTextField();
		txtId.setName("id");
		txtId.setColumns(10);
		panelGeneral.add(txtId, "cell 1 0,growx");
				
		JLabel lblFlowUnits = new JLabel("Infiltration:");
		panelGeneral.add(lblFlowUnits, "cell 3 0,alignx trailing");

		cboInfiltration = new JComboBox<String>();
		cboInfiltration.setActionCommand("shapeChanged");
		panelGeneral.add(cboInfiltration, "cell 4 0,growx");
		cboInfiltration.setName("infiltration");
		
		JLabel lblIgnoreSnowmelt = new JLabel("Description:");
		panelGeneral.add(lblIgnoreSnowmelt, "cell 0 1,alignx trailing");
		
		txtDescription = new JTextField();
		txtDescription.setName("descript");
		txtDescription.setColumns(10);
		panelGeneral.add(txtDescription, "cell 1 1 4 1,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnCreate = new JButton("+");
		btnCreate.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCreate.setToolTipText("Insert record");
		btnCreate.setActionCommand("create");
		getContentPane().add(btnCreate, "flowx,cell 1 2");
		
		btnDelete = new JButton("-");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setToolTipText("Delete record");
		btnDelete.setActionCommand("delete");
		getContentPane().add(btnDelete, "cell 1 2");
		
		btnPrevious = new JButton("<");
		btnPrevious.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnPrevious.setToolTipText("Previous record");
		btnPrevious.setActionCommand("movePrevious");
		getContentPane().add(btnPrevious, "cell 1 2");
		
		btnNext = new JButton(">");
		btnNext.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext.setToolTipText("Next record");
		btnNext.setActionCommand("moveNext");
		getContentPane().add(btnNext, "cell 1 2");
		
		btnSave = new JButton("Save");
		btnSave.setToolTipText("Save record");
		btnSave.setActionCommand("saveData");
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		btnClose = new JButton("Close");
		btnClose.setToolTipText("Close window");
		btnClose.setActionCommand("closeWindow");
		getContentPane().add(btnClose, "cell 1 2,alignx right");
		
		setupListeners();
		
	}
	
	
	protected void setupListeners() {
		
		btnPrevious.addActionListener(this);
		btnNext.addActionListener(this);
		btnCreate.addActionListener(this);
		btnDelete.addActionListener(this);
		super.setupListeners();
		
	}
	
	
}