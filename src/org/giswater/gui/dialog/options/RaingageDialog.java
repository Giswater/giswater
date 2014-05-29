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
package org.giswater.gui.dialog.options;

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
public class RaingageDialog extends AbstractOptionsDialog {

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField textField_18;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField txtFname;
	private JTextField textField_4;
	private JTextField textField_5;
	private JButton btnPrevious;
	private JButton btnNext;
	private JButton btnFileFname;

	
	public RaingageDialog() {
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
	
	public void setFileFname(String path) {
		txtFname.setText(path);
	}	

	
	private void initConfig() {

		setTitle("Raingage Table");
		setBounds(100, 100, 526, 339);
		getContentPane().setLayout(new MigLayout("", "[402.00][200px]", "[100:122.00][60.00px][80.00][10px][36.00]"));

		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[80px:80px][150.00:150.00][10px][80px][150]", "[][][][10]"));

		JLabel lblid = new JLabel("\u206FId:");
		panelGeneral.add(lblid, "cell 0 0,alignx trailing");

		textField_5 = new JTextField();
		textField_5.setName("rg_id");
		textField_5.setColumns(10);
		panelGeneral.add(textField_5, "cell 1 0,growx");
								
		JLabel lblIgnoreSnowmelt = new JLabel("Raingage Type:");
		panelGeneral.add(lblIgnoreSnowmelt, "cell 0 1,alignx trailing");
		
		JComboBox comboBox_3 = new JComboBox();
		panelGeneral.add(comboBox_3, "cell 1 1,growx");
		comboBox_3.setName("rgage_type");

		JLabel lblFlowUnits = new JLabel("Form type:");
		panelGeneral.add(lblFlowUnits, "cell 3 1,alignx trailing");
		
		JComboBox flow_units = new JComboBox();
		panelGeneral.add(flow_units, "cell 4 1,growx");
		flow_units.setName("form_type");
				
		JLabel lblNewLabel_1 = new JLabel("Intvl:");
		panelGeneral.add(lblNewLabel_1, "cell 0 2,alignx trailing");

		textField_1 = new JTextField();
		textField_1.setName("intvl");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 1 2,growx");

		JLabel lblInfiltration = new JLabel("Scf:");
		panelGeneral.add(lblInfiltration, "cell 3 2,alignx trailing");
		
		textField_2 = new JTextField();
		textField_2.setName("scf");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 4 2,growx");

		ImageIcon image = new ImageIcon("images/imago.png");
		super.setIconImage(image.getImage());

		btnPrevious = new JButton("<");
		btnPrevious.setActionCommand("movePrevious");
		getContentPane().add(btnPrevious, "flowx,cell 1 4");

		btnNext = new JButton(">");
		btnNext.setActionCommand("moveNext");
		getContentPane().add(btnNext, "cell 1 4,alignx right");

		btnSave = new JButton("Save");
		btnSave.setActionCommand("saveData");
		getContentPane().add(btnSave, "cell 1 4,alignx right");
		
		btnClose = new JButton("Close");
		getContentPane().add(btnClose, "cell 1 4,alignx right");				

		JPanel panelTimeseries = new JPanel();
		panelTimeseries.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelTimeseries.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "TIMESERIES", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelTimeseries, "cell 0 1 2 1,grow");
		panelTimeseries.setLayout(new MigLayout("", "[80px:80px][150px:150][10.00][80][200]", "[]"));

		JLabel lblFlowRouting = new JLabel("Timeseries id:");
		panelTimeseries.add(lblFlowRouting, "cell 0 0,alignx right");

		JComboBox flow_routing = new JComboBox();
		panelTimeseries.add(flow_routing, "cell 1 0,growx");
		flow_routing.setName("timser_id");

		JPanel panelFile = new JPanel();
		panelFile.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelFile.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "FILE", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelFile, "cell 0 2 2 1,grow");
		panelFile.setLayout(new MigLayout("", "[80px:80px][150.00:150][80px][10px][150]", "[][]"));
				
		JLabel lblIgnoreRouting = new JLabel("Units:");
		panelFile.add(lblIgnoreRouting, "cell 0 0,alignx right");

		textField_4 = new JTextField();
		panelFile.add(textField_4, "cell 1 0,growx");
		textField_4.setName("units");
		textField_4.setColumns(10);

		JLabel lblMinSlope = new JLabel("Sta:");
		panelFile.add(lblMinSlope, "cell 2 0 2 1,alignx right");
		lblMinSlope.setName("");

		textField_18 = new JTextField();
		panelFile.add(textField_18, "cell 4 0,growx");
		textField_18.setName("sta");
		textField_18.setColumns(10);
								
		JLabel lblIgnoreGroundwater = new JLabel("Fname:");
		panelFile.add(lblIgnoreGroundwater, "cell 0 1,alignx right");

		txtFname = new JTextField();
		panelFile.add(txtFname, "cell 1 1 2 1,growx");
		txtFname.setName("fname");
		txtFname.setColumns(10);

		btnFileFname = new JButton("...");
		btnFileFname.setActionCommand("chooseFileFname");
		panelFile.add(btnFileFname, "cell 4 1,alignx left");

		setupListeners();

	}

	
	// Setup component's listener
	protected void setupListeners() {
		btnPrevious.addActionListener(this);
		btnNext.addActionListener(this);
		btnFileFname.addActionListener(this);
		super.setupListeners();
	}

	
}