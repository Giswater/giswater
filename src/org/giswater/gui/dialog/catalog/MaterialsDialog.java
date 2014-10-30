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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.giswater.util.MaxLengthTextDocument;

import net.miginfocom.swing.MigLayout;


public class MaterialsDialog extends AbstractCatalogDialog{

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField txtId;
	private JTextField txtOther;
	private JTextField txtDescript;
	private JLabel lblOther;
	
	
	public MaterialsDialog() {
		initConfig();
	}	

	
	public void setName(String name){
		lblOther.setText(name + ":");
		txtOther.setName(name);
		createComponentMap();
	}
	

	private void initConfig(){

		setTitle("Table cat_mat");
		setBounds(100, 100, 454, 190);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[89.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[50][150][10px][50][150]", "[][][][][]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		txtId = new JTextField();
		txtId.setName("id");
		txtId.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(txtId, "cell 1 0,growx");
		
		lblOther = new JLabel("n:");
		panelGeneral.add(lblOther, "cell 3 0,alignx trailing");
		
		txtOther = new JTextField();
		txtOther.setName("n");
		txtOther.setDocument(new MaxLengthTextDocument(14));
		panelGeneral.add(txtOther, "cell 4 0,growx");
		
		JLabel lblTsectid = new JLabel("descript:");
		panelGeneral.add(lblTsectid, "cell 0 1,alignx trailing");
		
		txtDescript = new JTextField();
		txtDescript.setName("descript");
		txtDescript.setDocument(new MaxLengthTextDocument(100));
		panelGeneral.add(txtDescript, "cell 1 1,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnCreate = new JButton("+");
		btnCreate.setToolTipText("Insert record");
		btnCreate.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCreate.setActionCommand("create");
		getContentPane().add(btnCreate, "flowx,cell 1 2");
		
		btnDelete = new JButton("-");
		btnDelete.setToolTipText("Delete record");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setActionCommand("delete");
		getContentPane().add(btnDelete, "cell 1 2");
		
		btnPrevious = new JButton("<");
		btnPrevious.setToolTipText("Previous record");
		btnPrevious.setActionCommand("movePrevious");
		getContentPane().add(btnPrevious, "cell 1 2");
		
		btnNext = new JButton(">");
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

	
}