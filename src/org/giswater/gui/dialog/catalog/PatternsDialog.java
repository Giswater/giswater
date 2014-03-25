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


@SuppressWarnings("rawtypes")
public class PatternsDialog extends AbstractCatalogDialog{

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField textField_2;
	private JTextField textField;
	private JTextField textField_3;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_10;
	private JLabel lblFactor;
	private JTextField textField_7;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField textField_13;
	private JTextField textField_14;
	private JTextField textField_15;
	private JTextField textField_16;
	private JLabel lblFactor_1;
	private JLabel lblFactor_2;
	private JLabel lblFactor_3;
	private JLabel lblFactor_4;
	private JLabel lblFactor_5;
	private JLabel lblFactor_6;
	private JLabel lblFactor_7;
	private JComboBox cboType;
	private JLabel lblFactor_8;
	private JTextField textField_4;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_17;
	private JTextField textField_18;
	private JTextField textField_19;
	private JTextField textField_20;
	private JTextField textField_21;
	private JTextField textField_22;
	private JTextField textField_23;
	private JTextField textField_24;
	private JLabel lblFactor_9;
	private JLabel lblFactor_10;
	private JLabel lblFactor_11;
	private JLabel lblFactor_12;
	private JLabel lblFactor_13;
	private JLabel lblFactor_14;
	private JLabel lblFactor_15;
	private JLabel lblFactor_16;
	private JLabel lblFactor_17;
	private JLabel lblFactor_18;
	private JTextField textField_1;
	private JLabel lblFactor_19;
	
	
	public PatternsDialog() {
		initConfig();
		createComponentMap();
	}	

	
	public void enableType(boolean enable){
		cboType.setEnabled(enable);
	}

	
	private void initConfig(){

		setTitle("Table inp_patterns");
		setBounds(100, 100, 506, 446);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[361.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[60.00][150][10px][80px][150,grow]", "[][][][][][][][][][][][][]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		textField_2 = new JTextField();
		textField_2.setName("pattern_id");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 1 0,growx");
		
		JLabel lblType = new JLabel("type:");
		panelGeneral.add(lblType, "cell 3 0,alignx trailing");
		
		cboType = new JComboBox();
		cboType.setActionCommand("");
		cboType.setName("pattern_type");
		panelGeneral.add(cboType, "cell 4 0,growx");
		
		JLabel lblGeom = new JLabel("factor 1:");
		panelGeneral.add(lblGeom, "cell 0 1,alignx trailing");
		
		textField = new JTextField();
		textField.setName("factor_1");
		textField.setColumns(10);
		panelGeneral.add(textField, "cell 1 1,growx");
		
		lblFactor_8 = new JLabel("factor 13:");
		panelGeneral.add(lblFactor_8, "cell 3 1,alignx trailing");
		
		textField_4 = new JTextField();
		textField_4.setName("factor_13");
		textField_4.setColumns(10);
		panelGeneral.add(textField_4, "cell 4 1,growx");
		
		JLabel lblGeom_1 = new JLabel("factor 2:");
		panelGeneral.add(lblGeom_1, "cell 0 2,alignx trailing");
		
		textField_3 = new JTextField();
		textField_3.setName("factor_2");
		textField_3.setColumns(10);
		panelGeneral.add(textField_3, "cell 1 2,growx");
		
		lblFactor_9 = new JLabel("factor 14:");
		panelGeneral.add(lblFactor_9, "cell 3 2,alignx trailing");
		
		textField_8 = new JTextField();
		textField_8.setName("factor_14");
		textField_8.setColumns(10);
		panelGeneral.add(textField_8, "cell 4 2,growx");
		
		JLabel lblGeom_2 = new JLabel("factor 3:");
		panelGeneral.add(lblGeom_2, "cell 0 3,alignx trailing");
		
		textField_5 = new JTextField();
		textField_5.setName("factor_3");
		textField_5.setColumns(10);
		panelGeneral.add(textField_5, "cell 1 3,growx");
		
		lblFactor_10 = new JLabel("factor 15:");
		panelGeneral.add(lblFactor_10, "cell 3 3,alignx trailing");
		
		textField_9 = new JTextField();
		textField_9.setName("factor_15");
		textField_9.setColumns(10);
		panelGeneral.add(textField_9, "cell 4 3,growx");
		
		JLabel lblGeom_3 = new JLabel("factor 4:");
		panelGeneral.add(lblGeom_3, "cell 0 4,alignx trailing");
		
		textField_6 = new JTextField();
		textField_6.setName("factor_4");
		textField_6.setColumns(10);
		panelGeneral.add(textField_6, "cell 1 4,growx");
		
		lblFactor_11 = new JLabel("factor 16:");
		panelGeneral.add(lblFactor_11, "cell 3 4,alignx trailing");
		
		textField_17 = new JTextField();
		textField_17.setName("factor_16");
		textField_17.setColumns(10);
		panelGeneral.add(textField_17, "cell 4 4,growx");
		
		lblFactor = new JLabel("factor 5:");
		panelGeneral.add(lblFactor, "cell 0 5,alignx trailing");
		
		textField_10 = new JTextField();
		textField_10.setName("factor_5");
		textField_10.setColumns(10);
		panelGeneral.add(textField_10, "cell 1 5,growx");
		
		lblFactor_12 = new JLabel("factor 17:");
		panelGeneral.add(lblFactor_12, "cell 3 5,alignx trailing");
		
		textField_18 = new JTextField();
		textField_18.setName("factor_17");
		textField_18.setColumns(10);
		panelGeneral.add(textField_18, "cell 4 5,growx");
		
		lblFactor_1 = new JLabel("factor 6:");
		panelGeneral.add(lblFactor_1, "cell 0 6,alignx trailing");
		
		textField_7 = new JTextField();
		textField_7.setName("factor_6");
		textField_7.setColumns(10);
		panelGeneral.add(textField_7, "cell 1 6,growx");
		
		lblFactor_13 = new JLabel("factor 18:");
		panelGeneral.add(lblFactor_13, "cell 3 6,alignx trailing");
		
		textField_19 = new JTextField();
		textField_19.setName("factor_18");
		textField_19.setColumns(10);
		panelGeneral.add(textField_19, "cell 4 6,growx");
		
		lblFactor_2 = new JLabel("factor 7:");
		panelGeneral.add(lblFactor_2, "cell 0 7,alignx trailing");
		
		textField_11 = new JTextField();
		textField_11.setName("factor_7");
		textField_11.setColumns(10);
		panelGeneral.add(textField_11, "cell 1 7,growx");
		
		lblFactor_14 = new JLabel("factor 19:");
		panelGeneral.add(lblFactor_14, "cell 3 7,alignx trailing");
		
		textField_20 = new JTextField();
		textField_20.setName("factor_19");
		textField_20.setColumns(10);
		panelGeneral.add(textField_20, "cell 4 7,growx");
		
		lblFactor_3 = new JLabel("factor 8:");
		panelGeneral.add(lblFactor_3, "cell 0 8,alignx trailing");
		
		textField_12 = new JTextField();
		textField_12.setName("factor_8");
		textField_12.setColumns(10);
		panelGeneral.add(textField_12, "cell 1 8,growx");
		
		lblFactor_15 = new JLabel("factor 20:");
		panelGeneral.add(lblFactor_15, "cell 3 8,alignx trailing");
		
		textField_21 = new JTextField();
		textField_21.setName("factor_20");
		textField_21.setColumns(10);
		panelGeneral.add(textField_21, "cell 4 8,growx");
		
		lblFactor_4 = new JLabel("factor 9:");
		panelGeneral.add(lblFactor_4, "cell 0 9,alignx trailing");
		
		textField_13 = new JTextField();
		textField_13.setName("factor_9");
		textField_13.setColumns(10);
		panelGeneral.add(textField_13, "cell 1 9,growx");
		
		lblFactor_16 = new JLabel("factor 21:");
		panelGeneral.add(lblFactor_16, "cell 3 9,alignx trailing");
		
		textField_22 = new JTextField();
		textField_22.setName("factor_21");
		textField_22.setColumns(10);
		panelGeneral.add(textField_22, "cell 4 9,growx");
		
		lblFactor_5 = new JLabel("factor 10:");
		panelGeneral.add(lblFactor_5, "cell 0 10,alignx trailing");
		
		textField_14 = new JTextField();
		textField_14.setName("factor_10");
		textField_14.setColumns(10);
		panelGeneral.add(textField_14, "cell 1 10,growx");
		
		lblFactor_17 = new JLabel("factor 22:");
		panelGeneral.add(lblFactor_17, "cell 3 10,alignx trailing");
		
		textField_23 = new JTextField();
		textField_23.setName("factor_22");
		textField_23.setColumns(10);
		panelGeneral.add(textField_23, "cell 4 10,growx");
		
		lblFactor_6 = new JLabel("factor 11:");
		panelGeneral.add(lblFactor_6, "cell 0 11,alignx trailing");
		
		textField_15 = new JTextField();
		textField_15.setName("factor_11");
		textField_15.setColumns(10);
		panelGeneral.add(textField_15, "cell 1 11,growx");
		
		lblFactor_18 = new JLabel("factor 23:");
		panelGeneral.add(lblFactor_18, "cell 3 11,alignx trailing");
		
		textField_24 = new JTextField();
		textField_24.setName("factor_23");
		textField_24.setColumns(10);
		panelGeneral.add(textField_24, "cell 4 11,growx");
		
		lblFactor_7 = new JLabel("factor 12:");
		panelGeneral.add(lblFactor_7, "cell 0 12,alignx trailing");
		
		textField_16 = new JTextField();
		textField_16.setName("factor_12");
		textField_16.setColumns(10);
		panelGeneral.add(textField_16, "cell 1 12,growx");
		
		lblFactor_19 = new JLabel("factor 24:");
		panelGeneral.add(lblFactor_19, "cell 3 12,alignx trailing");
		
		textField_1 = new JTextField();
		textField_1.setName("factor_24");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 4 12,growx");
		
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

	
}