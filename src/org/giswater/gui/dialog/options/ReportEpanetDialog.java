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
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;


@SuppressWarnings("rawtypes")
public class ReportEpanetDialog extends AbstractOptionsDialog {

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField txtDuration;
	private JTextField txtStatistic;
	private JTextField txtPattern;
	private JTextField textField_1;
	
	
	public ReportEpanetDialog() {
		initConfig();
		createComponentMap();
	}


	private void initConfig(){

		setTitle("Report Table");
		setBounds(0, 0, 437, 428);
		getContentPane().setLayout(new MigLayout("", "[90.00][435.00]", "[:320.00:320px][10px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,growy");
		panelGeneral.setLayout(new MigLayout("", "[70:70][115.00px:115.00px][10px][70:70][115.00px:115.00px,grow]", "[25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][10px:n]"));

		JLabel lblFlowUnits = new JLabel("Pagesize:");
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");
		
		txtDuration = new JTextField();
		txtDuration.setName("pagesize");
		txtDuration.setColumns(10);
		panelGeneral.add(txtDuration, "cell 1 0,growx");
		
		JLabel lblInfiltration = new JLabel("Pressure:");
		lblInfiltration.setName("");
		panelGeneral.add(lblInfiltration, "cell 3 0,alignx trailing");
		
		JComboBox comboBox_12 = new JComboBox();
		comboBox_12.setName("pressure");
		panelGeneral.add(comboBox_12, "cell 4 0,growx");
		
		JLabel lblNewLabel_1 = new JLabel("File:");
		panelGeneral.add(lblNewLabel_1, "cell 0 1,alignx trailing");
		
		txtPattern = new JTextField();
		txtPattern.setName("file");
		txtPattern.setColumns(10);
		panelGeneral.add(txtPattern, "cell 1 1,growx");
		
		JLabel lblRuleTimestep = new JLabel("Quality:");
		lblRuleTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblRuleTimestep, "cell 3 1,alignx trailing");
		
		JComboBox comboBox_13 = new JComboBox();
		comboBox_13.setName("quality");
		panelGeneral.add(comboBox_13, "cell 4 1,growx");
		
		JLabel lblPatternTimestep = new JLabel("Status:");
		lblPatternTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternTimestep, "cell 0 2,alignx trailing");
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setName("status");
		panelGeneral.add(comboBox_2, "cell 1 2,growx");
		
		JLabel lblPatternStep = new JLabel("Length:");
		lblPatternStep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternStep, "cell 3 2,alignx trailing");
		
		JComboBox comboBox_14 = new JComboBox();
		comboBox_14.setName("length");
		panelGeneral.add(comboBox_14, "cell 4 2,growx");
		
		JLabel lblReportTimestep = new JLabel("Summary:");
		lblReportTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportTimestep, "cell 0 3,alignx trailing");
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setName("summary");
		panelGeneral.add(comboBox_3, "cell 1 3,growx");
		
		JLabel lblReportStart = new JLabel("Diameter:");
		lblReportStart.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportStart, "cell 3 3,alignx trailing");
		
		JComboBox comboBox_15 = new JComboBox();
		comboBox_15.setName("diameter");
		panelGeneral.add(comboBox_15, "cell 4 3,growx");
		
		JLabel lblStartClocktime = new JLabel("Energy:");
		lblStartClocktime.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblStartClocktime, "cell 0 4,alignx trailing");
		
		JComboBox comboBox_4 = new JComboBox();
		comboBox_4.setName("energy");
		panelGeneral.add(comboBox_4, "cell 1 4,growx");
		
		JLabel lblStatistic = new JLabel("Flow:");
		panelGeneral.add(lblStatistic, "cell 3 4,alignx trailing");
		
		JComboBox comboBox_11 = new JComboBox();
		comboBox_11.setName("flow");
		panelGeneral.add(comboBox_11, "cell 4 4,growx");
		
		JLabel lblNodes = new JLabel("Nodes:");
		lblNodes.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblNodes, "cell 0 5,alignx trailing");
		
		txtStatistic = new JTextField();
		txtStatistic.setName("nodes");
		txtStatistic.setColumns(10);
		panelGeneral.add(txtStatistic, "cell 1 5,growx");
		
		JLabel lblVelocity = new JLabel("Velocity:");
		panelGeneral.add(lblVelocity, "cell 3 5,alignx trailing");
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setName("velocity");
		panelGeneral.add(comboBox_1, "cell 4 5,growx");
		
		JLabel lblLinks = new JLabel("Links:");
		lblLinks.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblLinks, "cell 0 6,alignx trailing");
		
		textField_1 = new JTextField();
		textField_1.setName("links");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 1 6,growx");
		
		JLabel lblHeadloss = new JLabel("Headloss:");
		panelGeneral.add(lblHeadloss, "cell 3 6,alignx trailing");
		
		JComboBox comboBox_7 = new JComboBox();
		comboBox_7.setName("headloss");
		panelGeneral.add(comboBox_7, "cell 4 6,growx");
		
		JLabel lblElevation = new JLabel("Elevation:");
		lblElevation.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblElevation, "cell 0 7,alignx right");
		
		JComboBox comboBox = new JComboBox();
		comboBox.setName("elevation");
		panelGeneral.add(comboBox, "cell 1 7,growx");
		
		JLabel lblSetting = new JLabel("Setting:");
		panelGeneral.add(lblSetting, "cell 3 7,alignx trailing");
		
		JComboBox comboBox_8 = new JComboBox();
		comboBox_8.setName("setting");
		panelGeneral.add(comboBox_8, "cell 4 7,growx");
		
		JLabel lblDemand = new JLabel("Demand:");
		lblDemand.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblDemand, "cell 0 8,alignx trailing");
		
		JComboBox comboBox_5 = new JComboBox();
		comboBox_5.setName("demand");
		panelGeneral.add(comboBox_5, "cell 1 8,growx");
		
		JLabel lblReaction = new JLabel("Reaction:");
		panelGeneral.add(lblReaction, "cell 3 8,alignx trailing");
		
		JComboBox comboBox_9 = new JComboBox();
		comboBox_9.setName("reaction");
		panelGeneral.add(comboBox_9, "cell 4 8,growx");
		
		JLabel lblHead = new JLabel("Head:");
		lblHead.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblHead, "cell 0 9,alignx trailing");
		
		JComboBox comboBox_6 = new JComboBox();
		comboBox_6.setName("head");
		panelGeneral.add(comboBox_6, "cell 1 9,growx");
		
		JLabel lblFFactor = new JLabel("F factor:");
		panelGeneral.add(lblFFactor, "cell 3 9,alignx trailing");
		
		JComboBox comboBox_10 = new JComboBox();
		comboBox_10.setName("f_factor");
		panelGeneral.add(comboBox_10, "cell 4 9,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnSave = new JButton("Save");
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		btnClose = new JButton("Close");
		getContentPane().add(btnClose, "cell 1 2,alignx right");		
		
		setupListeners();
		
	}


}