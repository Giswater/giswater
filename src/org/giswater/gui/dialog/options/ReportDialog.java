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
package org.giswater.gui.dialog.options;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;


@SuppressWarnings("rawtypes")
public class ReportDialog extends AbstractOptionsDialog {

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField txtStatistic;
	private JTextField textField_1;
	private JTextField textField_2;
	//private AbstractButton btnSave;
	
	
	public ReportDialog() {
		initConfig();
		createComponentMap();
	}
	

	private void initConfig(){

		setTitle("Report Table");
		setBounds(0, 0, 454, 239);
		getContentPane().setLayout(new MigLayout("", "[90.00][435.00]", "[163.00][10px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[70:70][115.00px:115.00px][10px][75:75][115.00px:115.00px]", "[25px:n][25px:n][25px:n][25px:n][10px:n]"));

		JLabel lblFlowUnits = new JLabel("Input:");
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");
		
		JComboBox comboBox_5 = new JComboBox();
		comboBox_5.setName("input");
		panelGeneral.add(comboBox_5, "cell 1 0,growx");
		
		JLabel lblStartClocktime = new JLabel("Subcatchments:");
		lblStartClocktime.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblStartClocktime, "cell 3 0,alignx trailing");
		
		textField_2 = new JTextField();
		textField_2.setName("subcatchments");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 4 0,growx");
		
		JLabel lblNewLabel_1 = new JLabel("Continuity:");
		panelGeneral.add(lblNewLabel_1, "cell 0 1,alignx trailing");
		
		JComboBox comboBox_6 = new JComboBox();
		comboBox_6.setName("continuity");
		panelGeneral.add(comboBox_6, "cell 1 1,growx");
		
		JLabel lblNodes = new JLabel("Nodes:");
		lblNodes.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblNodes, "cell 3 1,alignx trailing");
		
		txtStatistic = new JTextField();
		txtStatistic.setName("nodes");
		txtStatistic.setColumns(10);
		panelGeneral.add(txtStatistic, "cell 4 1,growx");
		
		JLabel lblPatternTimestep = new JLabel("Flowstats:");
		lblPatternTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternTimestep, "cell 0 2,alignx trailing");
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setName("flowstats");
		panelGeneral.add(comboBox_2, "cell 1 2,growx");
		
		JLabel lblLinks = new JLabel("Links:");
		lblLinks.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblLinks, "cell 3 2,alignx trailing");
		
		textField_1 = new JTextField();
		textField_1.setName("links");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 4 2,growx");
		
		JLabel lblReportTimestep = new JLabel("Controls:");
		lblReportTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportTimestep, "cell 0 3,alignx trailing");
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setName("controls");
		panelGeneral.add(comboBox_3, "cell 1 3,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnSave = new JButton("Save");
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		setupListeners();
		
	}
	

}