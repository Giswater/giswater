/*
 * This file is part of gisWater
 * Copyright (C) 2012  Tecnics Associats
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
package org.giswater.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.giswater.controller.OptionsController;

import net.miginfocom.swing.MigLayout;


@SuppressWarnings("rawtypes")
public class OptionsDialog extends JDialog {

	private static final long serialVersionUID = -6349825417550216902L;
	private OptionsController controller;
	public HashMap<String, JComboBox> componentMap;
	public HashMap<String, JTextField> textMap;
	private JTextField textField;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField textField_13;
	private JTextField textField_14;
	private JTextField textField_16;
	private JTextField textField_18;
	private JTextField textField_1;
	
	
	public OptionsDialog() {
		initConfig();
		createComponentMap();
	}
	
	
	public void setControl(OptionsController inpOptionsController) {
		this.controller = inpOptionsController;
	}		

	
	public void setTextField(JTextField textField, Object value) {
		if (value!=null){
			textField.setText(value.toString());
		}
	}	
	
	
	public void setComboModel(JComboBox<String> combo, Vector<String> items) {
		if (items != null){
			ComboBoxModel<String> cbm = new DefaultComboBoxModel<String>(items);
			combo.setModel(cbm);
		}
	}	
	
	
	public void setComboSelectedItem(JComboBox combo, String item){
		combo.setSelectedItem(item);
	}
	
	
	private void createComponentMap() {
		
        componentMap = new HashMap<String, JComboBox>();
        textMap = new HashMap<String, JTextField>();
        Component[] components = getContentPane().getComponents();
    
        JPanel panel = null;
        Component[] comp;
		panel = (JPanel) components[0];               
        comp = panel.getComponents(); 		
        for (int i=0; i < comp.length; i++) {
			if (comp[i] instanceof JComboBox) {         	
				componentMap.put(comp[i].getName(), (JComboBox) comp[i]);
			}
			else if (comp[i] instanceof JTextField) {      
				textMap.put(comp[i].getName(), (JTextField) comp[i]);
			}			
        }
        
		panel = (JPanel) components[1];               
        comp = panel.getComponents(); 		
        for (int i=0; i < comp.length; i++) {
			if (comp[i] instanceof JComboBox) {         	
				componentMap.put(comp[i].getName(), (JComboBox) comp[i]);
			}
			else if (comp[i] instanceof JTextField) {      
				textMap.put(comp[i].getName(), (JTextField) comp[i]);
			}			
        }
        
		panel = (JPanel) components[2];               
        comp = panel.getComponents(); 		
        for (int i=0; i < comp.length; i++) {
			if (comp[i] instanceof JComboBox) {         	
				componentMap.put(comp[i].getName(), (JComboBox) comp[i]);
			}
			else if (comp[i] instanceof JTextField) {      
				textMap.put(comp[i].getName(), (JTextField) comp[i]);
			}			
        }        
        
	}

	
	private void initConfig(){

		setTitle("Options Table");
		setBounds(100, 100, 680, 600);
		getContentPane().setLayout(new MigLayout("", "[90.00][200px]", "[][][][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[90.00][200.00][10px][100px][200px]", "[][][][][][]"));

		JLabel lblFlowUnits = new JLabel("Flow units:");
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");

		JComboBox flow_units = new JComboBox();
		panelGeneral.add(flow_units, "cell 1 0,growx");
		flow_units.setName("flow_units");
		
		JLabel lblIgnoreRainfall = new JLabel("Ignore rainfall:");
		panelGeneral.add(lblIgnoreRainfall, "cell 3 0,alignx trailing");
		
		JComboBox comboBox_2 = new JComboBox();
		panelGeneral.add(comboBox_2, "cell 4 0,growx");
		comboBox_2.setName("ignore_rainfall");

		JLabel lblInfiltration = new JLabel("Infiltration:");
		panelGeneral.add(lblInfiltration, "cell 0 1,alignx trailing");

		JComboBox infiltration = new JComboBox();
		panelGeneral.add(infiltration, "cell 1 1,growx");
		infiltration.setName("infiltration");
		
		JLabel lblIgnoreSnowmelt = new JLabel("Ignore snowmelt:");
		panelGeneral.add(lblIgnoreSnowmelt, "cell 3 1,alignx trailing");
		
		JComboBox comboBox_3 = new JComboBox();
		panelGeneral.add(comboBox_3, "cell 4 1,growx");
		comboBox_3.setName("ignore_snowmelt");

		JLabel lblFlowRouting = new JLabel("Flow routing:");
		panelGeneral.add(lblFlowRouting, "cell 0 2,alignx trailing");

		JComboBox flow_routing = new JComboBox();
		panelGeneral.add(flow_routing, "cell 1 2,growx");
		flow_routing.setName("flow_routing");
		
		JLabel lblIgnoreGroundwater = new JLabel("Ignore groundwater:");
		panelGeneral.add(lblIgnoreGroundwater, "cell 3 2,alignx trailing");
		
		JComboBox comboBox_4 = new JComboBox();
		panelGeneral.add(comboBox_4, "cell 4 2,growx");
		comboBox_4.setName("ignore_groundwater");
		
		JLabel lblLinkOffsets = new JLabel("Link offsets:");
		panelGeneral.add(lblLinkOffsets, "cell 0 3,alignx trailing");
		lblLinkOffsets.setName("link_offsets");
		
		JComboBox comboBox = new JComboBox();
		panelGeneral.add(comboBox, "cell 1 3,growx");
		comboBox.setName("link_offsets");
		
		JLabel lblIgnoreRouting = new JLabel("Ignore routing:");
		panelGeneral.add(lblIgnoreRouting, "cell 3 3,alignx trailing");
		
		JComboBox comboBox_5 = new JComboBox();
		panelGeneral.add(comboBox_5, "cell 4 3,growx");
		comboBox_5.setName("ignore_routing");
		
		JLabel lblAllowPonding = new JLabel("Allow ponding:");
		panelGeneral.add(lblAllowPonding, "cell 0 4,alignx trailing");
		
		JComboBox comboBox_10 = new JComboBox();
		panelGeneral.add(comboBox_10, "cell 1 4,growx");
		comboBox_10.setName("allow_ponding");
		
		JLabel lblIgnoreQuality = new JLabel("Ignore quality:");
		panelGeneral.add(lblIgnoreQuality, "cell 3 4,alignx trailing");
		
		JComboBox comboBox_6 = new JComboBox();
		panelGeneral.add(comboBox_6, "cell 4 4,growx");
		comboBox_6.setName("ignore_quality");
		
		JLabel lblMinSlope = new JLabel("Min slope:");
		panelGeneral.add(lblMinSlope, "cell 0 5,alignx trailing");
		lblMinSlope.setName("");
		
		textField_18 = new JTextField();
		panelGeneral.add(textField_18, "cell 1 5,growx");
		textField_18.setName("min_slope");
		textField_18.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Skip steady state:");
		panelGeneral.add(lblNewLabel_1, "cell 3 5,alignx trailing");
		
		JComboBox comboBox_7 = new JComboBox();
		panelGeneral.add(comboBox_7, "cell 4 5,growx,aligny top");
		comboBox_7.setName("skip_steady_state");
		
		JPanel panelSteps = new JPanel();
		panelSteps.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "DATE & TIME STEPS", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelSteps, "cell 0 1 2 1,grow");
		panelSteps.setLayout(new MigLayout("", "[90px][200.00][10px][100.00px][200px]", "[][][][][][][]"));
		
		JLabel lblStartDate = new JLabel("Start date:");
		panelSteps.add(lblStartDate, "cell 0 0,alignx trailing");
		
		textField = new JTextField();
		panelSteps.add(textField, "cell 1 0,growx");
		textField.setName("start_date");
		textField.setColumns(10);
		
		JLabel lblWetSteps = new JLabel("Runoff wet step:");
		panelSteps.add(lblWetSteps, "cell 3 0,alignx trailing");
		lblWetSteps.setName("");
		
		textField_12 = new JTextField();
		panelSteps.add(textField_12, "cell 4 0,growx");
		textField_12.setName("wet_step");
		textField_12.setColumns(10);
		
		JLabel lblStartTime = new JLabel("Start time:");
		panelSteps.add(lblStartTime, "cell 0 1,alignx trailing");
		
		textField_2 = new JTextField();
		panelSteps.add(textField_2, "cell 1 1,growx");
		textField_2.setName("start_time");
		textField_2.setColumns(10);
		
		JLabel lblDrySteps = new JLabel("Runoff dry step:");
		panelSteps.add(lblDrySteps, "cell 3 1,alignx trailing");
		lblDrySteps.setName("dry_step");
		
		textField_10 = new JTextField();
		panelSteps.add(textField_10, "cell 4 1,growx");
		textField_10.setName("dry_step");
		textField_10.setColumns(10);
		
		JLabel label_1 = new JLabel("End date:");
		panelSteps.add(label_1, "cell 0 2,alignx trailing");
		
		textField_1 = new JTextField();
		panelSteps.add(textField_1, "cell 1 2,growx");
		textField_1.setName("end_date");
		textField_1.setColumns(10);
		
		JLabel lblRoutingSteps = new JLabel("Routing step:");
		panelSteps.add(lblRoutingSteps, "cell 3 2,alignx trailing");
		lblRoutingSteps.setName("");
		
		textField_13 = new JTextField();
		panelSteps.add(textField_13, "cell 4 2,growx");
		textField_13.setName("routing_step");
		textField_13.setColumns(10);
		
		JLabel lblEndTime = new JLabel("End time:");
		panelSteps.add(lblEndTime, "cell 0 3,alignx trailing");
		
		textField_3 = new JTextField();
		panelSteps.add(textField_3, "cell 1 3,growx");
		textField_3.setName("end_time");
		textField_3.setColumns(10);
		
		JLabel lblReportStartDate = new JLabel("Report start date:");
		panelSteps.add(lblReportStartDate, "cell 0 4,alignx trailing");
		
		textField_4 = new JTextField();
		panelSteps.add(textField_4, "cell 1 4,growx");
		textField_4.setName("report_start_date");
		textField_4.setColumns(10);
		
		JLabel lblSweepstart = new JLabel("Sweep start:");
		panelSteps.add(lblSweepstart, "cell 3 4,alignx trailing");
		lblSweepstart.setName("");
		
		textField_6 = new JTextField();
		panelSteps.add(textField_6, "cell 4 4,growx");
		textField_6.setName("sweep_start");
		textField_6.setColumns(10);
		
		JLabel lblReportStartTime = new JLabel("Report start time:");
		panelSteps.add(lblReportStartTime, "cell 0 5,alignx trailing");
		
		textField_5 = new JTextField();
		panelSteps.add(textField_5, "cell 1 5,growx");
		textField_5.setName("report_start_time");
		textField_5.setColumns(10);
		
		JLabel lblSweepEnd = new JLabel("Sweep end:");
		panelSteps.add(lblSweepEnd, "cell 3 5,alignx trailing");
		lblSweepEnd.setName("");
		
		textField_7 = new JTextField();
		panelSteps.add(textField_7, "cell 4 5,growx");
		textField_7.setName("sweep_end");
		textField_7.setColumns(10);
		
		JLabel lblDryDays = new JLabel("Report step:");
		panelSteps.add(lblDryDays, "cell 0 6,alignx trailing");
		lblDryDays.setName("");
		
		textField_8 = new JTextField();
		panelSteps.add(textField_8, "cell 1 6,growx");
		textField_8.setName("report_step");
		textField_8.setColumns(10);
		
		JLabel label = new JLabel("Dry days:");
		panelSteps.add(label, "cell 3 6,alignx trailing");
		label.setName("");
		
		textField_9 = new JTextField();
		panelSteps.add(textField_9, "cell 4 6,growx");
		textField_9.setName("dry_days");
		textField_9.setColumns(10);
		
		JPanel panelDynamic = new JPanel();
		panelDynamic.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "DYNAMIC WAVE", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelDynamic, "cell 0 2 2 1,grow");
		panelDynamic.setLayout(new MigLayout("", "[90.00][200.00][10px][100px][200px]", "[][][]"));
		
		JLabel lblNewLabel = new JLabel("Force main:");
		panelDynamic.add(lblNewLabel, "cell 0 0,alignx trailing");
		
		JComboBox comboBox_1 = new JComboBox();
		panelDynamic.add(comboBox_1, "cell 1 0,growx");
		comboBox_1.setName("force_main_equation");
		
		JLabel lblVariableSteps = new JLabel("Variable step:");
		panelDynamic.add(lblVariableSteps, "cell 3 0,alignx trailing");
		lblVariableSteps.setName("");
		
		textField_14 = new JTextField();
		panelDynamic.add(textField_14, "cell 4 0,growx");
		textField_14.setName("variable_step");
		textField_14.setColumns(10);
		
		JLabel lblNormalflowlimited = new JLabel("Normal flow limited");
		panelDynamic.add(lblNormalflowlimited, "cell 0 1,alignx trailing");
		lblNormalflowlimited.setName("");
		
		JComboBox comboBox_9 = new JComboBox();
		panelDynamic.add(comboBox_9, "cell 1 1,growx");
		comboBox_9.setName("normal_flow_limited");
		
		JLabel lblInertialdamping = new JLabel("Inertial damping:");
		panelDynamic.add(lblInertialdamping, "cell 3 1,alignx trailing");
		lblInertialdamping.setName("");
		
		JComboBox comboBox_8 = new JComboBox();
		panelDynamic.add(comboBox_8, "cell 4 1,growx");
		comboBox_8.setName("inertial_damping");
		
		JLabel lblLengtheningSteps = new JLabel("Lengthening step:");
		panelDynamic.add(lblLengtheningSteps, "cell 0 2,alignx trailing");
		lblLengtheningSteps.setName("");
		
		textField_11 = new JTextField();
		panelDynamic.add(textField_11, "cell 1 2,growx");
		textField_11.setName("lengthening_step");
		textField_11.setColumns(10);
		
		JLabel lblMinSurfarea = new JLabel("Min surfarea:");
		panelDynamic.add(lblMinSurfarea, "cell 3 2,alignx trailing");
		lblMinSurfarea.setName("");
		
		textField_16 = new JTextField();
		panelDynamic.add(textField_16, "cell 4 2,growx");
		textField_16.setName("min_surfarea");
		textField_16.setColumns(10);
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveData();
			}
		});
		getContentPane().add(btnSave, "cell 1 3,alignx right");
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				System.out.println("jdialog window closed event received");
			}
			public void windowClosing(WindowEvent e){
				//controller.saveData();
			}
		});		
		
	}

	
}