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
import java.util.ResourceBundle;

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

import org.giswater.util.MaxLengthTextDocument;


public class ReportEpanetDialog extends AbstractOptionsDialog {
	
	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form");
	
	
	public ReportEpanetDialog() {
		initConfig();
		createComponentMap();
	}


	@SuppressWarnings("rawtypes")
	private void initConfig(){

		setTitle(BUNDLE.getString("ReportEpanetDialog.this.title")); //$NON-NLS-1$
		setBounds(0, 0, 437, 405);
		getContentPane().setLayout(new MigLayout("", "[90.00][435.00]", "[:292.00:320px][10px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("ReportEpanetDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,growy");
		panelGeneral.setLayout(new MigLayout("", "[70:70][115.00px:115.00px][10px][70:70][115.00px:115.00px,grow]", "[25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][10px:n]"));
		
		JLabel lblPatternTimestep = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblPatternTimestep.text")); //$NON-NLS-1$
		lblPatternTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternTimestep, "cell 0 0,alignx trailing");
		
		JComboBox<String> comboBox_2 = new JComboBox<String>();
		comboBox_2.setName("status");
		panelGeneral.add(comboBox_2, "cell 1 0,growx");
		
		JLabel lblLinks = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblLinks.text")); //$NON-NLS-1$
		lblLinks.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblLinks, "cell 3 0,alignx trailing");
		
		JTextField txtLinks = new JTextField();
		txtLinks.setName("links");
		txtLinks.setDocument(new MaxLengthTextDocument(254));	
		panelGeneral.add(txtLinks, "cell 4 0,growx");
		
		JLabel lblReportTimestep = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblReportTimestep.text")); //$NON-NLS-1$
		lblReportTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportTimestep, "cell 0 1,alignx trailing");
		
		JComboBox<String> comboBox_3 = new JComboBox<String>();
		comboBox_3.setName("summary");
		panelGeneral.add(comboBox_3, "cell 1 1,growx");
		
		JLabel lblPatternStep = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblPatternStep.text")); //$NON-NLS-1$
		lblPatternStep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternStep, "cell 3 1,alignx trailing");
		
		JComboBox comboBox_14 = new JComboBox();
		comboBox_14.setName("length");
		panelGeneral.add(comboBox_14, "cell 4 1,growx");
		
		JLabel lblStartClocktime = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblStartClocktime.text")); //$NON-NLS-1$
		lblStartClocktime.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblStartClocktime, "cell 0 2,alignx trailing");
		
		JComboBox comboBox_4 = new JComboBox();
		comboBox_4.setName("energy");
		panelGeneral.add(comboBox_4, "cell 1 2,growx");
		
		JLabel lblReportStart = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblReportStart.text")); //$NON-NLS-1$
		lblReportStart.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportStart, "cell 3 2,alignx trailing");
		
		JComboBox<String> comboBox_15 = new JComboBox<String>();
		comboBox_15.setName("diameter");
		panelGeneral.add(comboBox_15, "cell 4 2,growx");
		
		JLabel lblNodes = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblNodes.text")); //$NON-NLS-1$
		lblNodes.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblNodes, "cell 0 3,alignx trailing");
		
		JTextField txtNodes = new JTextField();
		txtNodes.setName("nodes");
		txtNodes.setDocument(new MaxLengthTextDocument(254));	
		panelGeneral.add(txtNodes, "cell 1 3,growx");
		
		JLabel lblStatistic = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblStatistic.text")); //$NON-NLS-1$
		panelGeneral.add(lblStatistic, "cell 3 3,alignx trailing");
		
		JComboBox comboBox_11 = new JComboBox();
		comboBox_11.setName("flow");
		panelGeneral.add(comboBox_11, "cell 4 3,growx");
		
		JLabel lblElevation = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblElevation.text")); //$NON-NLS-1$
		lblElevation.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblElevation, "cell 0 4,alignx right");
		
		JComboBox comboBox = new JComboBox();
		comboBox.setName("elevation");
		panelGeneral.add(comboBox, "cell 1 4,growx");
		
		JLabel lblVelocity = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblVelocity.text")); //$NON-NLS-1$
		panelGeneral.add(lblVelocity, "cell 3 4,alignx trailing");
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setName("velocity");
		panelGeneral.add(comboBox_1, "cell 4 4,growx");
		
		JLabel lblDemand = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblDemand.text")); //$NON-NLS-1$
		lblDemand.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblDemand, "cell 0 5,alignx trailing");
		
		JComboBox comboBox_5 = new JComboBox();
		comboBox_5.setName("demand");
		panelGeneral.add(comboBox_5, "cell 1 5,growx");
		
		JLabel lblHeadloss = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblHeadloss.text")); //$NON-NLS-1$
		panelGeneral.add(lblHeadloss, "cell 3 5,alignx trailing");
		
		JComboBox comboBox_7 = new JComboBox();
		comboBox_7.setName("headloss");
		panelGeneral.add(comboBox_7, "cell 4 5,growx");
		
		JLabel lblHead = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblHead.text")); //$NON-NLS-1$
		lblHead.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblHead, "cell 0 6,alignx trailing");
		
		JComboBox comboBox_6 = new JComboBox();
		comboBox_6.setName("head");
		panelGeneral.add(comboBox_6, "cell 1 6,growx");
		
		JLabel lblSetting = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblSetting.text")); //$NON-NLS-1$
		panelGeneral.add(lblSetting, "cell 3 6,alignx trailing");
		
		JComboBox comboBox_8 = new JComboBox();
		comboBox_8.setName("setting");
		panelGeneral.add(comboBox_8, "cell 4 6,growx");
		
		JLabel lblInfiltration = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblInfiltration.text")); //$NON-NLS-1$
		lblInfiltration.setName("");
		panelGeneral.add(lblInfiltration, "cell 0 7,alignx trailing");
		
		JComboBox comboBox_12 = new JComboBox();
		comboBox_12.setName("pressure");
		panelGeneral.add(comboBox_12, "cell 1 7,growx");
		
		JLabel lblReaction = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblReaction.text")); //$NON-NLS-1$
		panelGeneral.add(lblReaction, "cell 3 7,alignx trailing");
		
		JComboBox comboBox_9 = new JComboBox();
		comboBox_9.setName("reaction");
		panelGeneral.add(comboBox_9, "cell 4 7,growx");
		
		JLabel lblRuleTimestep = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblRuleTimestep.text")); //$NON-NLS-1$
		lblRuleTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblRuleTimestep, "cell 0 8,alignx trailing");
		
		JComboBox comboBox_13 = new JComboBox();
		comboBox_13.setName("quality");
		panelGeneral.add(comboBox_13, "cell 1 8,growx");
		
		JLabel lblFFactor = new JLabel(BUNDLE.getString("ReportEpanetDialog.lblFFactor.text")); //$NON-NLS-1$
		panelGeneral.add(lblFFactor, "cell 3 8,alignx trailing");
		
		JComboBox comboBox_10 = new JComboBox();
		comboBox_10.setName("f_factor");
		panelGeneral.add(comboBox_10, "cell 4 8,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnSave = new JButton(BUNDLE.getString("Generic.btnSave.text"));
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text"));
		getContentPane().add(btnClose, "cell 1 2,alignx right");		
		
		setupListeners();
		
	}


}