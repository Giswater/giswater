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
import java.util.ResourceBundle;


public class PatternsDialog extends AbstractCatalogDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 

	private JComboBox<String> cboType;


	public PatternsDialog() {
		initConfig();
		createComponentMap();
	}	

	
	public void enableType(boolean enable) {
		cboType.setEnabled(enable);
	}

	
	private void initConfig() {

		setTitle(BUNDLE.getString("PatternsDialog.this.title")); 
		setBounds(100, 100, 506, 446);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[361.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("PatternsDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); 
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[60.00][150][10px][80px][150,grow]", "[][][][][][][][][][][][][]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		JTextField textField_2 = new JTextField();
		textField_2.setName("pattern_id");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 1 0,growx");
		
		JLabel lblType = new JLabel(BUNDLE.getString("PatternsDialog.lblType.text")); 
		panelGeneral.add(lblType, "cell 3 0,alignx trailing");
		
		cboType = new JComboBox<String>();
		cboType.setActionCommand("");
		cboType.setName("pattern_type");
		panelGeneral.add(cboType, "cell 4 0,growx");
		
		JLabel lblGeom = new JLabel(BUNDLE.getString("PatternsDialog.lblGeom.text")); 
		panelGeneral.add(lblGeom, "cell 0 1,alignx trailing");
		
		JTextField textField = new JTextField();
		textField.setName("factor_1");
		textField.setColumns(10);
		panelGeneral.add(textField, "cell 1 1,growx");
		
		JLabel lblFactor_8 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_8.text")); 
		panelGeneral.add(lblFactor_8, "cell 3 1,alignx trailing");
		
		JTextField textField_4 = new JTextField();
		textField_4.setName("factor_13");
		textField_4.setColumns(10);
		panelGeneral.add(textField_4, "cell 4 1,growx");
		
		JLabel lblGeom_1 = new JLabel(BUNDLE.getString("PatternsDialog.lblGeom_1.text")); 
		panelGeneral.add(lblGeom_1, "cell 0 2,alignx trailing");
		
		JTextField textField_3 = new JTextField();
		textField_3.setName("factor_2");
		textField_3.setColumns(10);
		panelGeneral.add(textField_3, "cell 1 2,growx");
		
		JLabel lblFactor_9 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_9.text")); 
		panelGeneral.add(lblFactor_9, "cell 3 2,alignx trailing");
		
		JTextField textField_8 = new JTextField();
		textField_8.setName("factor_14");
		textField_8.setColumns(10);
		panelGeneral.add(textField_8, "cell 4 2,growx");
		
		JLabel lblGeom_2 = new JLabel(BUNDLE.getString("PatternsDialog.lblGeom_2.text")); 
		panelGeneral.add(lblGeom_2, "cell 0 3,alignx trailing");
		
		JTextField textField_5 = new JTextField();
		textField_5.setName("factor_3");
		textField_5.setColumns(10);
		panelGeneral.add(textField_5, "cell 1 3,growx");
		
		JLabel lblFactor_10 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_10.text")); 
		panelGeneral.add(lblFactor_10, "cell 3 3,alignx trailing");
		
		JTextField textField_9 = new JTextField();
		textField_9.setName("factor_15");
		textField_9.setColumns(10);
		panelGeneral.add(textField_9, "cell 4 3,growx");
		
		JLabel lblGeom_3 = new JLabel(BUNDLE.getString("PatternsDialog.lblGeom_3.text")); 
		panelGeneral.add(lblGeom_3, "cell 0 4,alignx trailing");
		
		JTextField textField_6 = new JTextField();
		textField_6.setName("factor_4");
		textField_6.setColumns(10);
		panelGeneral.add(textField_6, "cell 1 4,growx");
		
		JLabel lblFactor_11 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_11.text")); 
		panelGeneral.add(lblFactor_11, "cell 3 4,alignx trailing");
		
		JTextField textField_17 = new JTextField();
		textField_17.setName("factor_16");
		textField_17.setColumns(10);
		panelGeneral.add(textField_17, "cell 4 4,growx");
		
		JLabel lblFactor = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor.text")); 
		panelGeneral.add(lblFactor, "cell 0 5,alignx trailing");
		
		JTextField textField_10 = new JTextField();
		textField_10.setName("factor_5");
		textField_10.setColumns(10);
		panelGeneral.add(textField_10, "cell 1 5,growx");
		
		JLabel lblFactor_12 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_12.text")); 
		panelGeneral.add(lblFactor_12, "cell 3 5,alignx trailing");
		
		JTextField textField_18 = new JTextField();
		textField_18.setName("factor_17");
		textField_18.setColumns(10);
		panelGeneral.add(textField_18, "cell 4 5,growx");
		
		JLabel lblFactor_1 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_1.text")); 
		panelGeneral.add(lblFactor_1, "cell 0 6,alignx trailing");
		
		JTextField textField_7 = new JTextField();
		textField_7.setName("factor_6");
		textField_7.setColumns(10);
		panelGeneral.add(textField_7, "cell 1 6,growx");
		
		JLabel lblFactor_13 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_13.text")); 
		panelGeneral.add(lblFactor_13, "cell 3 6,alignx trailing");
		
		JTextField textField_19 = new JTextField();
		textField_19.setName("factor_18");
		textField_19.setColumns(10);
		panelGeneral.add(textField_19, "cell 4 6,growx");
		
		JLabel lblFactor_2 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_2.text")); 
		panelGeneral.add(lblFactor_2, "cell 0 7,alignx trailing");
		
		JTextField textField_11 = new JTextField();
		textField_11.setName("factor_7");
		textField_11.setColumns(10);
		panelGeneral.add(textField_11, "cell 1 7,growx");
		
		JLabel lblFactor_14 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_14.text")); 
		panelGeneral.add(lblFactor_14, "cell 3 7,alignx trailing");
		
		JTextField textField_20 = new JTextField();
		textField_20.setName("factor_19");
		textField_20.setColumns(10);
		panelGeneral.add(textField_20, "cell 4 7,growx");
		
		JLabel lblFactor_3 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_3.text")); 
		panelGeneral.add(lblFactor_3, "cell 0 8,alignx trailing");
		
		JTextField textField_12 = new JTextField();
		textField_12.setName("factor_8");
		textField_12.setColumns(10);
		panelGeneral.add(textField_12, "cell 1 8,growx");
		
		JLabel lblFactor_15 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_15.text")); 
		panelGeneral.add(lblFactor_15, "cell 3 8,alignx trailing");
		
		JTextField textField_21 = new JTextField();
		textField_21.setName("factor_20");
		textField_21.setColumns(10);
		panelGeneral.add(textField_21, "cell 4 8,growx");
		
		JLabel lblFactor_4 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_4.text")); 
		panelGeneral.add(lblFactor_4, "cell 0 9,alignx trailing");
		
		JTextField textField_13 = new JTextField();
		textField_13.setName("factor_9");
		textField_13.setColumns(10);
		panelGeneral.add(textField_13, "cell 1 9,growx");
		
		JLabel lblFactor_16 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_16.text")); 
		panelGeneral.add(lblFactor_16, "cell 3 9,alignx trailing");
		
		JTextField textField_22 = new JTextField();
		textField_22.setName("factor_21");
		textField_22.setColumns(10);
		panelGeneral.add(textField_22, "cell 4 9,growx");
		
		JLabel lblFactor_5 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_5.text")); 
		panelGeneral.add(lblFactor_5, "cell 0 10,alignx trailing");
		
		JTextField textField_14 = new JTextField();
		textField_14.setName("factor_10");
		textField_14.setColumns(10);
		panelGeneral.add(textField_14, "cell 1 10,growx");
		
		JLabel lblFactor_17 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_17.text")); 
		panelGeneral.add(lblFactor_17, "cell 3 10,alignx trailing");
		
		JTextField textField_23 = new JTextField();
		textField_23.setName("factor_22");
		textField_23.setColumns(10);
		panelGeneral.add(textField_23, "cell 4 10,growx");
		
		JLabel lblFactor_6 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_6.text")); 
		panelGeneral.add(lblFactor_6, "cell 0 11,alignx trailing");
		
		JTextField textField_15 = new JTextField();
		textField_15.setName("factor_11");
		textField_15.setColumns(10);
		panelGeneral.add(textField_15, "cell 1 11,growx");
		
		JLabel lblFactor_18 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_18.text")); 
		panelGeneral.add(lblFactor_18, "cell 3 11,alignx trailing");
		
		JTextField textField_24 = new JTextField();
		textField_24.setName("factor_23");
		textField_24.setColumns(10);
		panelGeneral.add(textField_24, "cell 4 11,growx");
		
		JLabel lblFactor_7 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_7.text")); 
		panelGeneral.add(lblFactor_7, "cell 0 12,alignx trailing");
		
		JTextField textField_16 = new JTextField();
		textField_16.setName("factor_12");
		textField_16.setColumns(10);
		panelGeneral.add(textField_16, "cell 1 12,growx");
		
		JLabel lblFactor_19 = new JLabel(BUNDLE.getString("PatternsDialog.lblFactor_19.text")); 
		panelGeneral.add(lblFactor_19, "cell 3 12,alignx trailing");
		
		JTextField textField_1 = new JTextField();
		textField_1.setName("factor_24");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 4 12,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnCreate = new JButton("+");
		btnCreate.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCreate.setToolTipText(BUNDLE.getString("PatternsDialog.btnInsert.toolTipText"));
		btnCreate.setActionCommand("create");
		getContentPane().add(btnCreate, "flowx,cell 1 2");
		
		btnDelete = new JButton("-");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setToolTipText(BUNDLE.getString("PatternsDialog.btnDelete.toolTipText"));
		btnDelete.setActionCommand("delete");
		getContentPane().add(btnDelete, "cell 1 2");
		
		btnPrevious = new JButton("<");
		btnPrevious.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnPrevious.setToolTipText(BUNDLE.getString("PatternsDialog.btnPrevious.toolTipText"));
		btnPrevious.setActionCommand("movePrevious");
		getContentPane().add(btnPrevious, "cell 1 2");
		
		btnNext = new JButton(">");
		btnNext.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext.setToolTipText(BUNDLE.getString("PatternsDialog.btnNext.toolTipText"));
		btnNext.setActionCommand("moveNext");
		getContentPane().add(btnNext, "cell 1 2");
		
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

	
}