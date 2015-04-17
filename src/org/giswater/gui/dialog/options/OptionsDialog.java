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
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import net.miginfocom.swing.MigLayout;


@SuppressWarnings("rawtypes")
public class OptionsDialog extends AbstractOptionsDialog {

	private JTextField txtMaxTrials;
	private JTextField txtSysFlowTol;
	private JTextField txtHeadTol;
	private JTextField txtLatFlowTol;
	private JLabel lblMaxTrials;
	private JLabel lblHeadTol;
	private JLabel lblSysFlowTol;
	private JLabel lblLatFlowTol;
	
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 
	
	
	public OptionsDialog() {
		initConfig();
		createComponentMap();
	}
	
	
	private void initConfig() {

		setTitle(BUNDLE.getString("OptionsDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 680, 616);
		getContentPane().setLayout(new MigLayout("", "[90.00][200px]", "[][][157.00][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("OptionsDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[110.00][150.00][10px][150px][145px]", "[][][][][][]"));

		JLabel lblFlowUnits = new JLabel(BUNDLE.getString("OptionsDialog.lblFlowUnits.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");

		JComboBox flow_units = new JComboBox();
		panelGeneral.add(flow_units, "cell 1 0,growx");
		flow_units.setName("flow_units");
		
		JLabel lblIgnoreRainfall = new JLabel(BUNDLE.getString("OptionsDialog.lblIgnoreRainfall.text"));
		panelGeneral.add(lblIgnoreRainfall, "cell 3 0,alignx trailing");
		
		JComboBox comboBox_2 = new JComboBox();
		panelGeneral.add(comboBox_2, "cell 4 0,growx");
		comboBox_2.setName("ignore_rainfall");

		JLabel lblHydrology = new JLabel(BUNDLE.getString("OptionsDialog.lblHydrology.text")); //$NON-NLS-1$
		lblHydrology.setName("");
		panelGeneral.add(lblHydrology, "cell 0 1,alignx trailing");

		JComboBox hydrology = new JComboBox();
		panelGeneral.add(hydrology, "cell 1 1,growx");
		hydrology.setName("hydrology");
		
		JLabel lblIgnoreSnowmelt = new JLabel(BUNDLE.getString("OptionsDialog.lblIgnoreSnowmelt.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreSnowmelt, "cell 3 1,alignx trailing");
		
		JComboBox comboBox_3 = new JComboBox();
		panelGeneral.add(comboBox_3, "cell 4 1,growx");
		comboBox_3.setName("ignore_snowmelt");

		JLabel lblFlowRouting = new JLabel(BUNDLE.getString("OptionsDialog.lblFlowRouting.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowRouting, "cell 0 2,alignx trailing");

		JComboBox flow_routing = new JComboBox();
		panelGeneral.add(flow_routing, "cell 1 2,growx");
		flow_routing.setName("flow_routing");
		
		JLabel lblIgnoreGroundwater = new JLabel(BUNDLE.getString("OptionsDialog.lblIgnoreGroundwater.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreGroundwater, "cell 3 2,alignx trailing");
		
		JComboBox comboBox_4 = new JComboBox();
		panelGeneral.add(comboBox_4, "cell 4 2,growx");
		comboBox_4.setName("ignore_groundwater");
		
		JLabel lblLinkOffsets = new JLabel(BUNDLE.getString("OptionsDialog.lblLinkOffsets.text")); //$NON-NLS-1$
		panelGeneral.add(lblLinkOffsets, "cell 0 3,alignx trailing");
		lblLinkOffsets.setName("link_offsets");
		
		JComboBox comboBox = new JComboBox();
		panelGeneral.add(comboBox, "cell 1 3,growx");
		comboBox.setName("link_offsets");
		
		JLabel lblIgnoreRouting = new JLabel(BUNDLE.getString("OptionsDialog.lblIgnoreRouting.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreRouting, "cell 3 3,alignx trailing");
		
		JComboBox comboBox_5 = new JComboBox();
		panelGeneral.add(comboBox_5, "cell 4 3,growx");
		comboBox_5.setName("ignore_routing");
		
		JLabel lblAllowPonding = new JLabel(BUNDLE.getString("OptionsDialog.lblAllowPonding.text")); //$NON-NLS-1$
		panelGeneral.add(lblAllowPonding, "cell 0 4,alignx trailing");
		
		JComboBox comboBox_10 = new JComboBox();
		panelGeneral.add(comboBox_10, "cell 1 4,growx");
		comboBox_10.setName("allow_ponding");
		
		JLabel lblIgnoreQuality = new JLabel(BUNDLE.getString("OptionsDialog.lblIgnoreQuality.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreQuality, "cell 3 4,alignx trailing");
		
		JComboBox comboBox_6 = new JComboBox();
		panelGeneral.add(comboBox_6, "cell 4 4,growx");
		comboBox_6.setName("ignore_quality");
		
		JLabel lblMinSlope = new JLabel(BUNDLE.getString("OptionsDialog.lblMinSlope.text")); //$NON-NLS-1$
		panelGeneral.add(lblMinSlope, "cell 0 5,alignx trailing");
		lblMinSlope.setName("");
		
		JTextField textField_15 = new JTextField();
		panelGeneral.add(textField_15, "cell 1 5,growx");
		textField_15.setName("min_slope");
		textField_15.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel(BUNDLE.getString("OptionsDialog.lblNewLabel_1.text")); //$NON-NLS-1$
		panelGeneral.add(lblNewLabel_1, "cell 3 5,alignx trailing");
		
		JComboBox comboBox_7 = new JComboBox();
		panelGeneral.add(comboBox_7, "cell 4 5,growx,aligny top");
		comboBox_7.setName("skip_steady_state");
		
		JPanel panelSteps = new JPanel();
		panelSteps.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("OptionsDialog.panelSteps.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelSteps, "cell 0 1 2 1,grow");
		panelSteps.setLayout(new MigLayout("", "[145px][150.00][10px][185.00px][145px]", "[grow][][grow][][grow][][]"));
		
		JLabel lblStartDate = new JLabel(BUNDLE.getString("OptionsDialog.lblStartDate.text")); //$NON-NLS-1$
		panelSteps.add(lblStartDate, "cell 0 0,alignx trailing");

		JDateChooser dateStart = new JDateChooser();		
		dateStart.setName("start_date");
        dateStart.setDateFormatString("MM/dd/yyyy");
        dateStart.setFocusTraversalPolicyProvider(true);
        dateStart.setMinSelectableDate(new java.util.Date(978307286000L));
		panelSteps.add(dateStart, "cell 1 0,growx");        
		
		JLabel lblWetSteps = new JLabel(BUNDLE.getString("OptionsDialog.lblWetSteps.text")); //$NON-NLS-1$
		panelSteps.add(lblWetSteps, "cell 3 0,alignx trailing");
		lblWetSteps.setName("");
		
		JTextField textField_12 = new JTextField();
		panelSteps.add(textField_12, "cell 4 0,growx");
		textField_12.setName("wet_step");
		textField_12.setColumns(10);
		
		JLabel lblStartTime = new JLabel(BUNDLE.getString("OptionsDialog.lblStartTime.text")); //$NON-NLS-1$
		panelSteps.add(lblStartTime, "cell 0 1,alignx trailing");
		
		JTextField textField = new JTextField();
		panelSteps.add(textField, "cell 1 1,growx");
		textField.setName("start_time");
		textField.setColumns(10);
		
		JLabel lblDrySteps = new JLabel(BUNDLE.getString("OptionsDialog.lblDrySteps.text")); //$NON-NLS-1$
		panelSteps.add(lblDrySteps, "cell 3 1,alignx trailing");
		lblDrySteps.setName("dry_step");
		
		JTextField textField_10 = new JTextField();
		panelSteps.add(textField_10, "cell 4 1,growx");
		textField_10.setName("dry_step");
		textField_10.setColumns(10);
		
		JLabel label_1 = new JLabel(BUNDLE.getString("OptionsDialog.label_1.text")); //$NON-NLS-1$
		panelSteps.add(label_1, "cell 0 2,alignx trailing");
		
		JDateChooser dateEnd = new JDateChooser();
		dateEnd.setName("end_date");
		dateEnd.setFocusTraversalPolicyProvider(true);
		dateEnd.setDateFormatString("MM/dd/yyyy");
		panelSteps.add(dateEnd, "cell 1 2,grow");
		
		JLabel lblRoutingSteps = new JLabel(BUNDLE.getString("OptionsDialog.lblRoutingSteps.text")); //$NON-NLS-1$
		panelSteps.add(lblRoutingSteps, "cell 3 2,alignx trailing");
		lblRoutingSteps.setName("");
		
		JTextField textField_9 = new JTextField();
		panelSteps.add(textField_9, "cell 4 2,growx");
		textField_9.setName("routing_step");
		textField_9.setColumns(10);
		
		JLabel lblEndTime = new JLabel(BUNDLE.getString("OptionsDialog.lblEndTime.text")); //$NON-NLS-1$
		panelSteps.add(lblEndTime, "cell 0 3,alignx trailing");
		
		JTextField textField_2 = new JTextField();
		panelSteps.add(textField_2, "cell 1 3,growx");
		textField_2.setName("end_time");
		textField_2.setColumns(10);
		
		JLabel lblReportStartDate = new JLabel(BUNDLE.getString("OptionsDialog.lblReportStartDate.text")); //$NON-NLS-1$
		panelSteps.add(lblReportStartDate, "cell 0 4,alignx trailing");
		
		JDateChooser dateReportStart = new JDateChooser();
		dateReportStart.setName("report_start_date");
		dateReportStart.setFocusTraversalPolicyProvider(true);
		dateReportStart.setDateFormatString("MM/dd/yyyy");
		panelSteps.add(dateReportStart, "cell 1 4,grow");
		
		JLabel lblSweepstart = new JLabel(BUNDLE.getString("OptionsDialog.lblSweepstart.text")); //$NON-NLS-1$
		panelSteps.add(lblSweepstart, "cell 3 4,alignx trailing");
		lblSweepstart.setName("");
		
		JTextField textField_6 = new JTextField();
		panelSteps.add(textField_6, "cell 4 4,growx");
		textField_6.setName("sweep_start");
		textField_6.setColumns(10);
		
		JLabel lblReportStartTime = new JLabel(BUNDLE.getString("OptionsDialog.lblReportStartTime.text")); //$NON-NLS-1$
		panelSteps.add(lblReportStartTime, "cell 0 5,alignx trailing");
		
		JTextField textField_3 = new JTextField();
		panelSteps.add(textField_3, "cell 1 5,growx");
		textField_3.setName("report_start_time");
		textField_3.setColumns(10);
		
		JLabel lblSweepEnd = new JLabel(BUNDLE.getString("OptionsDialog.lblSweepEnd.text")); //$NON-NLS-1$
		panelSteps.add(lblSweepEnd, "cell 3 5,alignx trailing");
		lblSweepEnd.setName("");
		
		JTextField textField_7 = new JTextField();
		panelSteps.add(textField_7, "cell 4 5,growx");
		textField_7.setName("sweep_end");
		textField_7.setColumns(10);
		
		JLabel lblDryDays = new JLabel(BUNDLE.getString("OptionsDialog.lblDryDays.text")); //$NON-NLS-1$
		panelSteps.add(lblDryDays, "cell 0 6,alignx trailing");
		lblDryDays.setName("");
		
		JTextField textField_5 = new JTextField();
		panelSteps.add(textField_5, "cell 1 6,growx");
		textField_5.setName("report_step");
		textField_5.setColumns(10);
		
		JLabel label = new JLabel(BUNDLE.getString("OptionsDialog.label.text")); //$NON-NLS-1$
		panelSteps.add(label, "cell 3 6,alignx trailing");
		label.setName("");
		
		JTextField textField_8 = new JTextField();
		panelSteps.add(textField_8, "cell 4 6,growx");
		textField_8.setName("dry_days");
		textField_8.setColumns(10);
		
		JPanel panelDynamic = new JPanel();
		panelDynamic.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("OptionsDialog.panelDynamic.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelDynamic, "cell 0 2 2 1,grow");
		panelDynamic.setLayout(new MigLayout("", "[145.00][150][10px][185px][145px]", "[][][][][]"));
		
		JLabel lblNewLabel = new JLabel(BUNDLE.getString("OptionsDialog.lblNewLabel.text")); //$NON-NLS-1$
		panelDynamic.add(lblNewLabel, "cell 0 0,alignx trailing");
		
		JComboBox comboBox_1 = new JComboBox();
		panelDynamic.add(comboBox_1, "cell 1 0,growx");
		comboBox_1.setName("force_main_equation");
		
		JLabel lblVariableSteps = new JLabel(BUNDLE.getString("OptionsDialog.lblVariableSteps.text")); //$NON-NLS-1$
		panelDynamic.add(lblVariableSteps, "cell 3 0,alignx trailing");
		lblVariableSteps.setName("");
		
		JTextField textField_13 = new JTextField();
		panelDynamic.add(textField_13, "cell 4 0,growx");
		textField_13.setName("variable_step");
		textField_13.setColumns(10);
		
		JLabel lblNormalflowlimited = new JLabel(BUNDLE.getString("OptionsDialog.lblNormalflowlimited.text")); //$NON-NLS-1$
		panelDynamic.add(lblNormalflowlimited, "cell 0 1,alignx trailing");
		lblNormalflowlimited.setName("");
		
		JComboBox comboBox_9 = new JComboBox();
		panelDynamic.add(comboBox_9, "cell 1 1,growx");
		comboBox_9.setName("normal_flow_limited");
		
		JLabel lblInertialdamping = new JLabel(BUNDLE.getString("OptionsDialog.lblInertialdamping.text")); //$NON-NLS-1$
		panelDynamic.add(lblInertialdamping, "cell 3 1,alignx trailing");
		lblInertialdamping.setName("");
		
		JComboBox comboBox_8 = new JComboBox();
		panelDynamic.add(comboBox_8, "cell 4 1,growx");
		comboBox_8.setName("inertial_damping");
		
		JLabel lblLengtheningSteps = new JLabel(BUNDLE.getString("OptionsDialog.lblLengtheningSteps.text")); //$NON-NLS-1$
		panelDynamic.add(lblLengtheningSteps, "cell 0 2,alignx trailing");
		lblLengtheningSteps.setName("");
		
		JTextField textField_11 = new JTextField();
		panelDynamic.add(textField_11, "cell 1 2,growx");
		textField_11.setName("lengthening_step");
		textField_11.setColumns(10);
		
		JLabel lblMinSurfarea = new JLabel(BUNDLE.getString("OptionsDialog.lblMinSurfarea.text")); //$NON-NLS-1$
		panelDynamic.add(lblMinSurfarea, "cell 3 2,alignx trailing");
		lblMinSurfarea.setName("");
		
		JTextField textField_14 = new JTextField();
		panelDynamic.add(textField_14, "cell 4 2,growx");
		textField_14.setName("min_surfarea");
		textField_14.setColumns(10);
		
		lblMaxTrials = new JLabel(BUNDLE.getString("OptionsDialog.lblMaxTrials.text")); //$NON-NLS-1$
		lblMaxTrials.setName("");
		panelDynamic.add(lblMaxTrials, "cell 0 3,alignx trailing");
		
		txtMaxTrials = new JTextField();
		txtMaxTrials.setName("max_trials");
		txtMaxTrials.setColumns(10);
		panelDynamic.add(txtMaxTrials, "cell 1 3,growx");
		
		lblHeadTol = new JLabel(BUNDLE.getString("OptionsDialog.lblHeadTol.text")); //$NON-NLS-1$
		lblHeadTol.setName("");
		panelDynamic.add(lblHeadTol, "cell 3 3,alignx trailing");
		
		txtHeadTol = new JTextField();
		txtHeadTol.setName("head_tolerance");
		txtHeadTol.setColumns(10);
		panelDynamic.add(txtHeadTol, "cell 4 3,growx");
		
		lblSysFlowTol = new JLabel(BUNDLE.getString("OptionsDialog.lblSysFlowTol.text")); //$NON-NLS-1$
		lblSysFlowTol.setName("");
		panelDynamic.add(lblSysFlowTol, "cell 0 4,alignx trailing");
		
		txtSysFlowTol = new JTextField();
		txtSysFlowTol.setName("sys_flow_tol");
		txtSysFlowTol.setColumns(10);
		panelDynamic.add(txtSysFlowTol, "cell 1 4,growx");
		
		lblLatFlowTol = new JLabel(BUNDLE.getString("OptionsDialog.lblLatFlowTol.text")); //$NON-NLS-1$
		lblLatFlowTol.setName("");
		panelDynamic.add(lblLatFlowTol, "cell 3 4,alignx trailing");
		
		txtLatFlowTol = new JTextField();
		txtLatFlowTol.setName("lat_flow_tol");
		txtLatFlowTol.setColumns(10);
		panelDynamic.add(txtLatFlowTol, "cell 4 4,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
		btnSave = new JButton(BUNDLE.getString("TableWindowPanel.btnSave.text"));
		getContentPane().add(btnSave, "cell 1 3,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("TableWindowPanel.btnClose.text"));
		getContentPane().add(btnClose, "cell 1 3,alignx right");		
		
		setupListeners();
		
	}

	
	public void setFieldsEnabled (boolean enabled) {
		lblMaxTrials.setEnabled(enabled);
		lblSysFlowTol.setEnabled(enabled);
		lblHeadTol.setEnabled(enabled);
		lblLatFlowTol.setEnabled(enabled);
		txtMaxTrials.setEnabled(enabled);
		txtSysFlowTol.setEnabled(enabled);
		txtHeadTol.setEnabled(enabled);
		txtLatFlowTol.setEnabled(enabled);
	}
	
	
}