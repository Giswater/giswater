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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.util.MaxLengthTextDocument;


public class ResultCatDialog extends AbstractOptionsDialog {

	private JButton btnPrevious;
	private JButton btnNext;
	private JButton btnDelete;
	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form");
	
	
	public ResultCatDialog() {
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

		setTitle(BUNDLE.getString("ResultCatDialog.this.title")); //$NON-NLS-1$
		setBounds(0, 0, 500, 420);
		getContentPane().setLayout(new MigLayout("", "[90.00][380.00,grow]", "[322.00][5px:n][30]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("ResultCatDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[100.00:n][100:n][10px:10px][90:n][90,grow]", "[25px:n][25px:n][25px:n][25px:n][25px:n][25px:n][25px][25px][25px][25px:n]"));

		JLabel lblFlowUnits = new JLabel(BUNDLE.getString("ResultCatDialog.lblFlowUnits.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");
		
		JTextField txtResultId = new JTextField();
		txtResultId.setName("result_id");
		txtResultId.setDocument(new MaxLengthTextDocument(16));	
		panelGeneral.add(txtResultId, "cell 1 0 3 1,growx");
		
		JLabel lblImportDate = new JLabel(BUNDLE.getString("ResultCatDialog.lblImportDate.text")); //$NON-NLS-1$
		panelGeneral.add(lblImportDate, "cell 0 1,alignx trailing");
		
		JTextField textField_8 = new JTextField();
		textField_8.setName("exec_date");
		textField_8.setEnabled(false);
		textField_8.setColumns(10);
		panelGeneral.add(textField_8, "cell 1 1 3 1,growx");
		
		JLabel lblNewLabel_1 = new JLabel(BUNDLE.getString("ResultCatDialog.lblNewLabel_1.text")); //$NON-NLS-1$
		panelGeneral.add(lblNewLabel_1, "cell 0 2,alignx trailing");
		
		JTextField txtQuality = new JTextField();
		txtQuality.setEnabled(false);
		txtQuality.setName("flow_units");
		txtQuality.setColumns(10);
		panelGeneral.add(txtQuality, "cell 1 2,growx");
		
		JLabel lblFlowRouteM = new JLabel(BUNDLE.getString("ResultCatDialog.lblFlowRouteM.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowRouteM, "cell 3 2,alignx trailing");
		
		JTextField txtHydraulic = new JTextField();
		txtHydraulic.setEnabled(false);
		txtHydraulic.setName("flowrout_m");
		txtHydraulic.setColumns(10);
		panelGeneral.add(txtHydraulic, "cell 4 2,growx");
		
		JLabel lblPatternTimestep = new JLabel(BUNDLE.getString("ResultCatDialog.lblPatternTimestep.text")); //$NON-NLS-1$
		lblPatternTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternTimestep, "cell 0 3,alignx trailing");
		
		JTextField txtPattern = new JTextField();
		txtPattern.setEnabled(false);
		txtPattern.setName("rain_runof");
		txtPattern.setColumns(10);
		panelGeneral.add(txtPattern, "cell 1 3,growx");
		
		JLabel lblStartDate = new JLabel(BUNDLE.getString("ResultCatDialog.lblStartDate.text")); //$NON-NLS-1$
		lblStartDate.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblStartDate, "cell 3 3,alignx trailing");
		
		JTextField txtRule = new JTextField();
		txtRule.setEnabled(false);
		txtRule.setName("start_date");
		txtRule.setColumns(10);
		panelGeneral.add(txtRule, "cell 4 3,growx");
		
		JLabel lblReportTimestep = new JLabel(BUNDLE.getString("ResultCatDialog.lblReportTimestep.text")); //$NON-NLS-1$
		lblReportTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportTimestep, "cell 0 4,alignx trailing");
		
		JTextField txtReport = new JTextField();
		txtReport.setEnabled(false);
		txtReport.setName("snowmelt");
		txtReport.setColumns(10);
		panelGeneral.add(txtReport, "cell 1 4,growx");
		
		JLabel lblPatternStep = new JLabel(BUNDLE.getString("ResultCatDialog.lblPatternStep.text")); //$NON-NLS-1$
		lblPatternStep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternStep, "cell 3 4,alignx trailing");
		
		JTextField txtPatternStart = new JTextField();
		txtPatternStart.setEnabled(false);
		txtPatternStart.setName("end_date");
		txtPatternStart.setColumns(10);
		panelGeneral.add(txtPatternStart, "cell 4 4,growx");
		
		JLabel lblStartClocktime = new JLabel(BUNDLE.getString("ResultCatDialog.lblStartClocktime.text")); //$NON-NLS-1$
		lblStartClocktime.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblStartClocktime, "cell 0 5,alignx trailing");
		
		JTextField txtStart = new JTextField();
		txtStart.setEnabled(false);
		txtStart.setName("groundw");
		txtStart.setColumns(10);
		panelGeneral.add(txtStart, "cell 1 5,growx");
		
		JLabel lblReportStart = new JLabel(BUNDLE.getString("ResultCatDialog.lblReportStart.text")); //$NON-NLS-1$
		lblReportStart.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportStart, "cell 3 5,alignx trailing");
		
		JTextField txtReportStart = new JTextField();
		txtReportStart.setEnabled(false);
		txtReportStart.setName("dry_days");
		txtReportStart.setColumns(10);
		panelGeneral.add(txtReportStart, "cell 4 5,growx");
		
		JLabel lblFlowRouting = new JLabel(BUNDLE.getString("ResultCatDialog.lblFlowRouting.text")); //$NON-NLS-1$
		lblFlowRouting.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblFlowRouting, "cell 0 6,alignx trailing");
		
		JTextField textField = new JTextField();
		textField.setEnabled(false);
		textField.setName("flow_rout");
		textField.setColumns(10);
		panelGeneral.add(textField, "cell 1 6,growx");
		
		JLabel lblStatistic = new JLabel(BUNDLE.getString("ResultCatDialog.lblStatistic.text")); //$NON-NLS-1$
		panelGeneral.add(lblStatistic, "cell 3 6,alignx trailing");
		
		JTextField textField_4 = new JTextField();
		textField_4.setEnabled(false);
		textField_4.setName("rep_tstep");
		textField_4.setColumns(10);
		panelGeneral.add(textField_4, "cell 4 6,growx");
		
		JLabel lblPondAll = new JLabel(BUNDLE.getString("ResultCatDialog.lblPondAll.text")); //$NON-NLS-1$
		lblPondAll.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPondAll, "cell 0 7,alignx trailing");
		
		JTextField textField_1 = new JTextField();
		textField_1.setEnabled(false);
		textField_1.setName("pond_all");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 1 7,growx");
		
		JLabel lblInfiltration = new JLabel(BUNDLE.getString("ResultCatDialog.lblInfiltration.text")); //$NON-NLS-1$
		panelGeneral.add(lblInfiltration, "cell 3 7,alignx trailing");
		
		JTextField textField_5 = new JTextField();
		textField_5.setEnabled(false);
		textField_5.setName("wet_tstep");
		textField_5.setColumns(10);
		panelGeneral.add(textField_5, "cell 4 7,growx");
		
		JLabel lblWaterQ = new JLabel(BUNDLE.getString("ResultCatDialog.lblWaterQ.text")); //$NON-NLS-1$
		lblWaterQ.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblWaterQ, "cell 0 8,alignx trailing");
		
		JTextField textField_2 = new JTextField();
		textField_2.setEnabled(false);
		textField_2.setName("water_q");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 1 8,growx");
		
		JLabel lblRuleTimestep = new JLabel(BUNDLE.getString("ResultCatDialog.lblRuleTimestep.text")); //$NON-NLS-1$
		lblRuleTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblRuleTimestep, "cell 3 8,alignx trailing");
		
		JTextField textField_6 = new JTextField();
		textField_6.setEnabled(false);
		textField_6.setName("dry_tstep");
		textField_6.setColumns(10);
		panelGeneral.add(textField_6, "cell 4 8,growx");
		
		JLabel lblInfilM = new JLabel(BUNDLE.getString("ResultCatDialog.lblInfilM.text")); //$NON-NLS-1$
		panelGeneral.add(lblInfilM, "cell 0 9,alignx trailing");
		
		JTextField textField_3 = new JTextField();
		textField_3.setEnabled(false);
		textField_3.setName("infil_m");
		textField_3.setColumns(10);
		panelGeneral.add(textField_3, "cell 1 9,growx");
		
		JLabel lblRoutTimestep = new JLabel(BUNDLE.getString("ResultCatDialog.lblRoutTimestep.text")); //$NON-NLS-1$
		lblRoutTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblRoutTimestep, "cell 3 9,alignx trailing");
		
		JTextField textField_7 = new JTextField();
		textField_7.setEnabled(false);
		textField_7.setName("rout_tstep");
		textField_7.setColumns(10);
		panelGeneral.add(textField_7, "cell 4 9,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
		btnDelete = new JButton("-");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setToolTipText(BUNDLE.getString("ResultCatDialog.btnDelete.toolTipText")); //$NON-NLS-1$
		btnDelete.setActionCommand("delete");
		getContentPane().add(btnDelete, "cell 1 2,alignx right");
		
		btnPrevious = new JButton("<");
		btnPrevious.setToolTipText(BUNDLE.getString("ResultCatDialog.btnPrevious.toolTipText")); //$NON-NLS-1$
		btnPrevious.setActionCommand("movePrevious");
		getContentPane().add(btnPrevious, "flowx,cell 1 2,alignx right");
		
		btnNext = new JButton(">");
		btnNext.setToolTipText(BUNDLE.getString("ResultCatDialog.btnNext.toolTipText")); //$NON-NLS-1$
		btnNext.setActionCommand("moveNext");
		getContentPane().add(btnNext, "cell 1 2,alignx right");
		
		btnSave = new JButton(BUNDLE.getString("Generic.btnSave.text"));
		btnSave.setToolTipText(BUNDLE.getString("Generic.btnSave.toolTipText"));
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text"));
		btnClose.setToolTipText(BUNDLE.getString("Generic.btnClose.toolTipText"));
		getContentPane().add(btnClose, "cell 1 2,alignx right");			
		
		setupListeners();
		
	}

	
	protected void setupListeners() {
		super.setupListeners();
		btnPrevious.addActionListener(this);
		btnNext.addActionListener(this);		
		btnDelete.addActionListener(this);
	}		
	

}