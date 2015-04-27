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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.util.MaxLengthTextDocument;


@SuppressWarnings("rawtypes")
public class OptionsEpanetDialog extends AbstractOptionsDialog {

	private JTextField txtUnbalancedN;
	private JTextField txtHydraulicsFname;
	private JTextField txtNode;
	private JComboBox hydraulics;
	private JComboBox quality;
	private JComboBox unbalanced;
	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form");
	
	
	public OptionsEpanetDialog() {
		initConfig();
		createComponentMap();
	}
	
	
	private void initConfig(){

		setTitle(BUNDLE.getString("OptionsEpanetDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 502, 368);
		getContentPane().setLayout(new MigLayout("", "[90.00][200px]", "[276.00][5][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("OptionsEpanetDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[130][110][10px][120][110]", "[][][][][][][][][][][]"));

		JLabel lblFlowUnits = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblFlowUnits.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");

		JComboBox units = new JComboBox();
		panelGeneral.add(units, "cell 1 0,growx");
		units.setName("units");
		
		JLabel lblIgnoreRainfall = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblIgnoreRainfall.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreRainfall, "cell 3 0,alignx trailing");
		
		JTextField textField_14 = new JTextField();
		textField_14.setName("maxcheck");
		textField_14.setColumns(10);
		panelGeneral.add(textField_14, "cell 4 0,growx");

		JLabel lblInfiltration = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblInfiltration.text")); //$NON-NLS-1$
		panelGeneral.add(lblInfiltration, "cell 0 1,alignx trailing");

		JComboBox headloss = new JComboBox();
		panelGeneral.add(headloss, "cell 1 1,growx");
		headloss.setName("headloss");
		
		JLabel lblIgnoreSnowmelt = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblIgnoreSnowmelt.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreSnowmelt, "cell 3 1,alignx trailing");
		
		JTextField textField_5 = new JTextField();
		textField_5.setName("damplimit");
		textField_5.setColumns(10);
		panelGeneral.add(textField_5, "cell 4 1,growx");

		JLabel lblFlowRouting = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblFlowRouting.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowRouting, "cell 0 2,alignx trailing");

		hydraulics = new JComboBox();
		panelGeneral.add(hydraulics, "cell 1 2,growx");
		hydraulics.setName("hydraulics");
		
		JLabel lblHydraulicsFname = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblHydraulicsFname.text")); //$NON-NLS-1$
		panelGeneral.add(lblHydraulicsFname, "cell 3 2,alignx trailing");
		
		txtHydraulicsFname = new JTextField();
		txtHydraulicsFname.setName("hydraulics_fname");
		txtHydraulicsFname.setDocument(new MaxLengthTextDocument(254));
		panelGeneral.add(txtHydraulicsFname, "cell 4 2,growx");
		
		JLabel lblLinkOffsets = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblLinkOffsets.text")); //$NON-NLS-1$
		panelGeneral.add(lblLinkOffsets, "cell 0 3,alignx trailing");
		lblLinkOffsets.setName("link_offsets");
		
		quality = new JComboBox();
		panelGeneral.add(quality, "cell 1 3,growx");
		quality.setName("quality");
		
		JLabel lblNodeId = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblNodeId.text")); //$NON-NLS-1$
		panelGeneral.add(lblNodeId, "cell 3 3,alignx trailing");
		
		txtNode = new JTextField();
		panelGeneral.add(txtNode, "cell 4 3,growx");
		txtNode.setName("node_id");
		txtNode.setColumns(10);
		
		JLabel lblAllowPonding = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblAllowPonding.text")); //$NON-NLS-1$
		panelGeneral.add(lblAllowPonding, "cell 0 4,alignx trailing");
		
		unbalanced = new JComboBox();
		panelGeneral.add(unbalanced, "cell 1 4,growx");
		unbalanced.setName("unbalanced");
		
		JLabel lblUnbalanced = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblUnbalanced.text")); //$NON-NLS-1$
		lblUnbalanced.setName("");
		panelGeneral.add(lblUnbalanced, "cell 3 4,alignx trailing");
		
		txtUnbalancedN = new JTextField();
		txtUnbalancedN.setName("unbalanced_n");	
		txtUnbalancedN.setDocument(new MaxLengthTextDocument(12));
		panelGeneral.add(txtUnbalancedN, "cell 4 4,growx");
		
		JLabel lblMinSlope = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblMinSlope.text")); //$NON-NLS-1$
		panelGeneral.add(lblMinSlope, "cell 0 5,alignx trailing");
		lblMinSlope.setName("");
		
		JTextField textField = new JTextField();
		panelGeneral.add(textField, "cell 1 5,growx");
		textField.setName("viscosity");
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblNewLabel_1.text")); //$NON-NLS-1$
		panelGeneral.add(lblNewLabel_1, "cell 3 5,alignx trailing");
		
		JTextField textField_9 = new JTextField();
		textField_9.setName("specific_gravity");
		textField_9.setColumns(10);
		panelGeneral.add(textField_9, "cell 4 5,growx");
		
		JLabel lblTrials = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblTrials.text")); //$NON-NLS-1$
		lblTrials.setName("");
		panelGeneral.add(lblTrials, "cell 0 6,alignx trailing");
		
		JTextField textField_1 = new JTextField();
		textField_1.setName("trials");
		textField_1.setColumns(10);
		panelGeneral.add(textField_1, "cell 1 6,growx");
		
		JLabel lblDiffusivity = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblDiffusivity.text")); //$NON-NLS-1$
		panelGeneral.add(lblDiffusivity, "cell 3 6,alignx trailing");
		
		JTextField textField_8 = new JTextField();
		textField_8.setName("diffusivity");
		textField_8.setColumns(10);
		panelGeneral.add(textField_8, "cell 4 6,growx");
		
		JLabel lblAccuracy = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblAccuracy.text")); //$NON-NLS-1$
		lblAccuracy.setName("");
		panelGeneral.add(lblAccuracy, "cell 0 7,alignx trailing");
		
		JTextField textField_2 = new JTextField();
		textField_2.setName("accuracy");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 1 7,growx");
		
		JLabel lblTolerance = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblTolerance.text")); //$NON-NLS-1$
		panelGeneral.add(lblTolerance, "cell 3 7,alignx trailing");
		
		JTextField textField_7 = new JTextField();
		textField_7.setName("tolerance");
		textField_7.setColumns(10);
		panelGeneral.add(textField_7, "cell 4 7,growx");
		
		JLabel lblIgnoreQuality = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblIgnoreQuality.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreQuality, "cell 0 8,alignx trailing");
		
		JTextField textField_3 = new JTextField();
		panelGeneral.add(textField_3, "cell 1 8,growx");
		textField_3.setName("emitter_exponent");
		textField_3.setColumns(10);
		
		JLabel lblIgnoreGroundwater = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblIgnoreGroundwater.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreGroundwater, "cell 3 8,alignx trailing");
		
		JComboBox pattern = new JComboBox();
		pattern.setName("pattern");
		panelGeneral.add(pattern, "cell 4 8,growx");
		
		JLabel lblCheckFreq = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblCheckFreq.text")); //$NON-NLS-1$
		lblCheckFreq.setName("");
		panelGeneral.add(lblCheckFreq, "cell 0 9,alignx trailing");
		
		JTextField textField_4 = new JTextField();
		textField_4.setName("checkfreq");
		textField_4.setColumns(10);
		panelGeneral.add(textField_4, "cell 1 9,growx");
		
		JLabel lblIgnoreRouting = new JLabel(BUNDLE.getString("OptionsEpanetDialog.lblIgnoreRouting.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreRouting, "cell 3 9,alignx trailing");
		
		JTextField textField_6 = new JTextField();
		textField_6.setName("demand_multiplier");
		textField_6.setColumns(10);
		panelGeneral.add(textField_6, "cell 4 9,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
		btnSave = new JButton(BUNDLE.getString("Generic.btnSave.text"));
		getContentPane().add(btnSave, "flowx,cell 1 2,alignx right");

		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text"));
		getContentPane().add(btnClose, "cell 1 2,alignx right");		
		
		setupListeners();
		
	}


	protected void setupListeners() {
		
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
		
		super.setupListeners();
		
	}


	public void setComboEnabled(String comboName, boolean isEnabled) {
		
		if (comboName.equals("hydraulics")){
			txtHydraulicsFname.setEnabled(isEnabled);
			if (!isEnabled){
				txtHydraulicsFname.setText("");
			}
		} 
		else if (comboName.equals("unbalanced")){
			txtUnbalancedN.setEnabled(isEnabled);
		}
		else if (comboName.equals("quality")){
			txtNode.setEnabled(isEnabled);
			String text = (isEnabled ? "" : " ");
			txtNode.setText(text);
		}
		
	}

	
}