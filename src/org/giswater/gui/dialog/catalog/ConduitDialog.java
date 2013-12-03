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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class ConduitDialog extends AbstractCatalogDialog{

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField txtTsect;
	protected JButton btnPrevious;
	protected JButton btnNext;
	protected JButton btnSave;	
	protected JButton btnCreate;
	protected JButton btnDelete;	
	private JComboBox cboShape;
	private JTextField txtCurve;
	
	
	public ConduitDialog() {
		initConfig();
		createComponentMap();
	}	


	private void initConfig(){

		setTitle("Table cat_arc");
		setBounds(100, 100, 502, 316);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[220.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[60.00][150][10px][80px][150]", "[][][][][][][]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		textField_2 = new JTextField();
		textField_2.setName("id");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 1 0,growx");
		
		JLabel lblTsectid = new JLabel("tsect_id:");
		panelGeneral.add(lblTsectid, "cell 3 0,alignx trailing");
		
		txtTsect = new JTextField();
		txtTsect.setEnabled(false);
		txtTsect.setName("tsect_id");
		txtTsect.setColumns(10);
		panelGeneral.add(txtTsect, "cell 4 0,growx");
		
		JLabel lblFlowUnits = new JLabel("Shape:");
		panelGeneral.add(lblFlowUnits, "cell 0 1,alignx trailing");

		cboShape = new JComboBox();
		cboShape.setActionCommand("shapeChanged");
		panelGeneral.add(cboShape, "cell 1 1,growx");
		cboShape.setName("shape");
		
		JLabel lblCurveid = new JLabel("curve_id:");
		panelGeneral.add(lblCurveid, "cell 3 1,alignx trailing");
		
		txtCurve = new JTextField();
		txtCurve.setEnabled(false);
		txtCurve.setName("curve_id");
		txtCurve.setColumns(10);
		panelGeneral.add(txtCurve, "cell 4 1,growx");
		
		JLabel lblGeom = new JLabel("geom1:");
		panelGeneral.add(lblGeom, "cell 0 2,alignx trailing");
		
		textField = new JTextField();
		textField.setName("geom1");
		textField.setColumns(10);
		panelGeneral.add(textField, "cell 1 2,growx");
		
		JLabel lblNewLabel_1 = new JLabel("ts:");
		panelGeneral.add(lblNewLabel_1, "cell 3 2,alignx trailing");
		
		textField_8 = new JTextField();
		textField_8.setName("ts");
		textField_8.setColumns(10);
		panelGeneral.add(textField_8, "cell 4 2,growx");
		
		JLabel lblGeom_1 = new JLabel("geom2:");
		panelGeneral.add(lblGeom_1, "cell 0 3,alignx trailing");
		
		textField_3 = new JTextField();
		textField_3.setName("geom2");
		textField_3.setColumns(10);
		panelGeneral.add(textField_3, "cell 1 3,growx");
		
		JLabel lblThr = new JLabel("thr:");
		panelGeneral.add(lblThr, "cell 3 3,alignx trailing");
		
		textField_1 = new JTextField();
		textField_1.setName("thr");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 4 3,growx");
		
		JLabel lblGeom_2 = new JLabel("geom3:");
		panelGeneral.add(lblGeom_2, "cell 0 4,alignx trailing");
		
		textField_5 = new JTextField();
		textField_5.setName("geom3");
		textField_5.setColumns(10);
		panelGeneral.add(textField_5, "cell 1 4,growx");
		
		JLabel lblShortDesc = new JLabel("short desc:");
		panelGeneral.add(lblShortDesc, "cell 3 4,alignx trailing");
		
		textField_9 = new JTextField();
		textField_9.setName("short_des");
		textField_9.setColumns(10);
		panelGeneral.add(textField_9, "cell 4 4,growx");
		
		JLabel lblGeom_3 = new JLabel("geom4:");
		panelGeneral.add(lblGeom_3, "cell 0 5,alignx trailing");
		
		textField_6 = new JTextField();
		textField_6.setName("geom4");
		textField_6.setColumns(10);
		panelGeneral.add(textField_6, "cell 1 5,growx");
		
		JLabel lblIgnoreSnowmelt = new JLabel("descript");
		panelGeneral.add(lblIgnoreSnowmelt, "cell 3 5,alignx trailing");
		
		textField_4 = new JTextField();
		textField_4.setName("descript");
		textField_4.setColumns(10);
		panelGeneral.add(textField_4, "cell 4 5,growx");
		
		JLabel lblGeomr = new JLabel("geom_r:");
		panelGeneral.add(lblGeomr, "cell 0 6,alignx trailing");
		
		textField_7 = new JTextField();
		textField_7.setName("geom_r");
		textField_7.setColumns(10);
		panelGeneral.add(textField_7, "cell 1 6,growx");
		
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
		
		setupListeners();
		
	}
	
	
	protected void setupListeners() {
		
		cboShape.addActionListener(this);	
		btnPrevious.addActionListener(this);
		btnNext.addActionListener(this);
		btnCreate.addActionListener(this);
		btnDelete.addActionListener(this);

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveData();
				dispose();
			}
		});
		
		super.setupListeners();
		
	}
	
	
	public void shapeChanged(){
		
		txtCurve.setEnabled(false);
		txtTsect.setEnabled(false);
		String shape = cboShape.getSelectedItem().toString();
		if (shape.toUpperCase().equals("CUSTOM")){
			txtCurve.setEnabled(true);
		}
		else if (shape.toUpperCase().equals("IRREGULAR")){
			txtTsect.setEnabled(true);
		}
		
	}
	
	
}