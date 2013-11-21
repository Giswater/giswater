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

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.OptionsEpanetController;


@SuppressWarnings("rawtypes")
public class OptionsEpanetDialog extends JDialog {

	private static final long serialVersionUID = -6349825417550216902L;
	private OptionsEpanetController controller;
	public HashMap<String, JComboBox> comboMap;
	public HashMap<String, JTextField> textMap;
	private JTextField textField_18;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField txtUnbalancedN;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField txtHydraulicsFname;
	private JTextField txtNode;
	private JTextField textField_14;
	private JComboBox hydraulics;
	private JComboBox quality;
	private JComboBox unbalanced;
	
	
	public OptionsEpanetDialog() {
		initConfig();
		createComponentMap();
	}
	
	
	public void setControl(OptionsEpanetController inpOptionsController) {
		this.controller = inpOptionsController;
	}		

	
	public void setTextField(JTextField textField, Object value) {
		if (value != null){
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
		
        comboMap = new HashMap<String, JComboBox>();
        textMap = new HashMap<String, JTextField>();
        Component[] components = getContentPane().getComponents();
    
        for (int j=0; j<components.length; j++) {
        	if (components[j] instanceof JPanel){
        		JPanel panel = (JPanel) components[j];            
	            Component[] comp = panel.getComponents();        
	            for (int i=0; i<comp.length; i++) {        
	            	if (comp[i] instanceof JComboBox) {         	
	            		comboMap.put(comp[i].getName(), (JComboBox) comp[i]);
	            	}
	            	else if (comp[i] instanceof JTextField) {      
	            		textMap.put(comp[i].getName(), (JTextField) comp[i]);
	            	}
	            }
        	}
        }      
        
	}

	
	private void initConfig(){

		setTitle("Options Table");
		setBounds(100, 100, 502, 378);
		getContentPane().setLayout(new MigLayout("", "[90.00][200px]", "[287.00][10][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[85:85][130:130][10px][85:85][130:130]", "[][][][][][][][][][][]"));

		JLabel lblFlowUnits = new JLabel("Units:");
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");

		JComboBox units = new JComboBox();
		panelGeneral.add(units, "cell 1 0,growx");
		units.setName("units");
		
		JLabel lblIgnoreRainfall = new JLabel("Max check:");
		panelGeneral.add(lblIgnoreRainfall, "cell 3 0,alignx trailing");
		
		textField_14 = new JTextField();
		textField_14.setName("maxcheck");
		textField_14.setColumns(10);
		panelGeneral.add(textField_14, "cell 4 0,growx");

		JLabel lblInfiltration = new JLabel("Headloss:");
		panelGeneral.add(lblInfiltration, "cell 0 1,alignx trailing");

		JComboBox headloss = new JComboBox();
		panelGeneral.add(headloss, "cell 1 1,growx");
		headloss.setName("headloss");
		
		JLabel lblIgnoreSnowmelt = new JLabel("Damp limit:");
		panelGeneral.add(lblIgnoreSnowmelt, "cell 3 1,alignx trailing");
		
		textField_5 = new JTextField();
		textField_5.setName("damplimit");
		textField_5.setColumns(10);
		panelGeneral.add(textField_5, "cell 4 1,growx");

		JLabel lblFlowRouting = new JLabel("Hydraulics:");
		panelGeneral.add(lblFlowRouting, "cell 0 2,alignx trailing");

		hydraulics = new JComboBox();
		panelGeneral.add(hydraulics, "cell 1 2,growx");
		hydraulics.setName("hydraulics");
		
		JLabel lblHydraulicsFname = new JLabel("Hydraulics fname");
		panelGeneral.add(lblHydraulicsFname, "cell 3 2,alignx trailing");
		
		txtHydraulicsFname = new JTextField();
		txtHydraulicsFname.setName("hydraulics_fname");
		txtHydraulicsFname.setColumns(10);
		panelGeneral.add(txtHydraulicsFname, "cell 4 2,growx");
		
		JLabel lblLinkOffsets = new JLabel("Quality:");
		panelGeneral.add(lblLinkOffsets, "cell 0 3,alignx trailing");
		lblLinkOffsets.setName("link_offsets");
		
		quality = new JComboBox();
		panelGeneral.add(quality, "cell 1 3,growx");
		quality.setName("quality");
		
		JLabel lblNodeId = new JLabel("Node id:");
		panelGeneral.add(lblNodeId, "cell 3 3,alignx trailing");
		
		txtNode = new JTextField();
		panelGeneral.add(txtNode, "cell 4 3,growx");
		txtNode.setName("node_id");
		txtNode.setColumns(10);
		
		JLabel lblAllowPonding = new JLabel("Unbalanced:");
		panelGeneral.add(lblAllowPonding, "cell 0 4,alignx trailing");
		
		unbalanced = new JComboBox();
		panelGeneral.add(unbalanced, "cell 1 4,growx");
		unbalanced.setName("unbalanced");
		
		JLabel lblUnbalanced = new JLabel("Unbalanced_n:");
		lblUnbalanced.setName("");
		panelGeneral.add(lblUnbalanced, "cell 3 4,alignx trailing");
		
		txtUnbalancedN = new JTextField();
		txtUnbalancedN.setName("unbalanced_n");
		txtUnbalancedN.setColumns(10);
		panelGeneral.add(txtUnbalancedN, "cell 4 4,growx");
		
		JLabel lblMinSlope = new JLabel("Viscosity:");
		panelGeneral.add(lblMinSlope, "cell 0 5,alignx trailing");
		lblMinSlope.setName("");
		
		textField_18 = new JTextField();
		panelGeneral.add(textField_18, "cell 1 5,growx");
		textField_18.setName("viscosity");
		textField_18.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Specific Gravity:");
		panelGeneral.add(lblNewLabel_1, "cell 3 5,alignx trailing");
		
		textField_9 = new JTextField();
		textField_9.setName("specific_gravity");
		textField_9.setColumns(10);
		panelGeneral.add(textField_9, "cell 4 5,growx");
		
		JLabel lblTrials = new JLabel("Trials:");
		lblTrials.setName("");
		panelGeneral.add(lblTrials, "cell 0 6,alignx trailing");
		
		textField_1 = new JTextField();
		textField_1.setName("trials");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 1 6,growx");
		
		JLabel lblDiffusivity = new JLabel("Diffusivity:");
		panelGeneral.add(lblDiffusivity, "cell 3 6,alignx trailing");
		
		textField_10 = new JTextField();
		textField_10.setName("diffusivity");
		textField_10.setColumns(10);
		panelGeneral.add(textField_10, "cell 4 6,growx");
		
		JLabel lblAccuracy = new JLabel("Accuracy:");
		lblAccuracy.setName("");
		panelGeneral.add(lblAccuracy, "cell 0 7,alignx trailing");
		
		textField_2 = new JTextField();
		textField_2.setName("accuracy");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 1 7,growx");
		
		JLabel lblTolerance = new JLabel("Tolerance:");
		panelGeneral.add(lblTolerance, "cell 3 7,alignx trailing");
		
		textField_11 = new JTextField();
		textField_11.setName("tolerance");
		textField_11.setColumns(10);
		panelGeneral.add(textField_11, "cell 4 7,growx");
		
		JLabel lblIgnoreQuality = new JLabel("Emitter exponent:");
		panelGeneral.add(lblIgnoreQuality, "cell 0 8,alignx trailing");
		
		textField_8 = new JTextField();
		panelGeneral.add(textField_8, "cell 1 8,growx");
		textField_8.setName("emitter_exponent");
		textField_8.setColumns(10);
		
		JLabel lblIgnoreGroundwater = new JLabel("Pattern:");
		panelGeneral.add(lblIgnoreGroundwater, "cell 3 8,alignx trailing");
		
		textField_6 = new JTextField();
		panelGeneral.add(textField_6, "cell 4 8,growx");
		textField_6.setName("pattern");
		textField_6.setColumns(10);
		
		JLabel lblCheckFreq = new JLabel("Check freq:");
		lblCheckFreq.setName("");
		panelGeneral.add(lblCheckFreq, "cell 0 9,alignx trailing");
		
		textField_4 = new JTextField();
		textField_4.setName("checkfreq");
		textField_4.setColumns(10);
		panelGeneral.add(textField_4, "cell 1 9,growx");
		
		JLabel lblIgnoreRouting = new JLabel("Demand multiplier:");
		panelGeneral.add(lblIgnoreRouting, "cell 3 9,alignx trailing");
		
		textField_7 = new JTextField();
		textField_7.setName("demand_multiplier");
		textField_7.setColumns(10);
		panelGeneral.add(textField_7, "cell 4 9,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveData();
			}
		});
		getContentPane().add(btnSave, "flowx,cell 1 2,alignx right");
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				System.out.println("jdialog window closed event received");
			}
			public void windowClosing(WindowEvent e){
				//controller.saveData();
			}
		});	
		
		setupListeners();
		
	}


	private void setupListeners() {
		
		hydraulics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String value = "";
				if (hydraulics.getSelectedIndex() != -1) {
					value = hydraulics.getSelectedItem().toString();
				}
				controller.changeCombo("hydraulics", value);
			}
		});
		
		quality.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String value = "";
				if (quality.getSelectedIndex() != -1) {
					value = quality.getSelectedItem().toString();
				}
				controller.changeCombo("quality", value);				
			}
		});		
		
		unbalanced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String value = "";
				if (unbalanced.getSelectedIndex() != -1) {
					value = unbalanced.getSelectedItem().toString();
				}
				controller.changeCombo("unbalanced", value);					
			}
		});		
		
	}


	public void setComboVisible(String comboName, boolean isVisible) {
		
		if (comboName.equals("hydraulics")){
			txtHydraulicsFname.setEnabled(isVisible);
		} 
		else if (comboName.equals("unbalanced")){
			txtUnbalancedN.setEnabled(isVisible);
		}
		else if (comboName.equals("quality")){
			txtNode.setEnabled(isVisible);
		}
		
	}

	
}