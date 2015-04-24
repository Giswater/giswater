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

import org.giswater.util.MaxLengthTextDocument;

import net.miginfocom.swing.MigLayout;
import java.util.ResourceBundle;


public class TimesDialog extends AbstractOptionsDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	
	public TimesDialog() {
		initConfig();
		createComponentMap();
	}
	
	
	private void initConfig(){

		setTitle(BUNDLE.getString("TimesDialog.this.title")); //$NON-NLS-1$
		setBounds(0, 0, 480, 283);
		getContentPane().setLayout(new MigLayout("", "[90.00][392.00]", "[179.00][10px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("TimesDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,growy");
		panelGeneral.setLayout(new MigLayout("", "[75.00][110.00][10px][80px][110]", "[25px:n][25px:n][25px:n][25px:n][25px:n][10px:n]"));

		JLabel lblFlowUnits = new JLabel(BUNDLE.getString("TimesDialog.lblFlowUnits.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");
		
		JTextField txtDuration = new JTextField();
		txtDuration.setName("duration");
		txtDuration.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtDuration, "cell 1 0,growx");
		
		JLabel lblInfiltration = new JLabel(BUNDLE.getString("TimesDialog.lblInfiltration.text")); //$NON-NLS-1$
		panelGeneral.add(lblInfiltration, "cell 3 0,alignx trailing");
		
		JTextField txtHydraulic = new JTextField();
		txtHydraulic.setName("hydraulic_timestep");
		txtHydraulic.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtHydraulic, "cell 4 0,growx");
		
		JLabel lblNewLabel_1 = new JLabel(BUNDLE.getString("TimesDialog.lblNewLabel_1.text")); //$NON-NLS-1$
		panelGeneral.add(lblNewLabel_1, "cell 0 1,alignx trailing");
		
		JTextField txtQuality = new JTextField();
		txtQuality.setName("quality_timestep");
		txtQuality.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtQuality, "cell 1 1,growx");
		
		JLabel lblRuleTimestep = new JLabel(BUNDLE.getString("TimesDialog.lblRuleTimestep.text")); //$NON-NLS-1$
		lblRuleTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblRuleTimestep, "cell 3 1,alignx trailing");
		
		JTextField txtRule = new JTextField();
		txtRule.setName("rule_timestep");
		txtRule.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtRule, "cell 4 1,growx");
		
		JLabel lblPatternTimestep = new JLabel(BUNDLE.getString("TimesDialog.lblPatternTimestep.text")); //$NON-NLS-1$
		lblPatternTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternTimestep, "cell 0 2,alignx trailing");
		
		JTextField txtPattern = new JTextField();
		txtPattern.setName("pattern_timestep");
		txtPattern.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtPattern, "cell 1 2,growx");
		
		JLabel lblPatternStep = new JLabel(BUNDLE.getString("TimesDialog.lblPatternStep.text")); //$NON-NLS-1$
		lblPatternStep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternStep, "cell 3 2,alignx trailing");
		
		JTextField txtPatternStart = new JTextField();
		txtPatternStart.setName("pattern_start");
		txtPatternStart.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtPatternStart, "cell 4 2,growx");
		
		JLabel lblReportTimestep = new JLabel(BUNDLE.getString("TimesDialog.lblReportTimestep.text")); //$NON-NLS-1$
		lblReportTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportTimestep, "cell 0 3,alignx trailing");
		
		JTextField txtReport = new JTextField();
		txtReport.setName("report_timestep");
		txtReport.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtReport, "cell 1 3,growx");
		
		JLabel lblReportStart = new JLabel(BUNDLE.getString("TimesDialog.lblReportStart.text")); //$NON-NLS-1$
		lblReportStart.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportStart, "cell 3 3,alignx trailing");
		
		JTextField txtReportStart = new JTextField();
		txtReportStart.setName("report_start");
		txtReportStart.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtReportStart, "cell 4 3,growx");
		
		JLabel lblStartClocktime = new JLabel(BUNDLE.getString("TimesDialog.lblStartClocktime.text")); //$NON-NLS-1$
		lblStartClocktime.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblStartClocktime, "cell 0 4,alignx trailing");
		
		JTextField txtStart = new JTextField();
		txtStart.setName("start_clocktime");
		txtStart.setDocument(new MaxLengthTextDocument(10));
		panelGeneral.add(txtStart, "cell 1 4,growx");
		
		JLabel lblStatistic = new JLabel(BUNDLE.getString("TimesDialog.lblStatistic.text")); //$NON-NLS-1$
		panelGeneral.add(lblStatistic, "cell 3 4,alignx trailing");
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setName("statistic");
		panelGeneral.add(comboBox, "cell 4 4,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnSave = new JButton(BUNDLE.getString("Generic.btnSave.text"));
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text"));
		getContentPane().add(btnClose, "cell 1 2,alignx right");		
		
		setupListeners();
		
	}


}