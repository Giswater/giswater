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

import org.giswater.util.MaxLengthTextDocument;

import net.miginfocom.swing.MigLayout;
import java.util.ResourceBundle;


@SuppressWarnings("rawtypes")
public class RaingageDialog extends AbstractOptionsDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private JTextField txtSta;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField txtFname;
	private JTextField txtUnits;
	private JTextField textField_5;
	private JButton btnPrevious;
	private JButton btnNext;
	private JButton btnFileFname;
	private JComboBox cboRaingageType;
	private JPanel panelFile;
	private JPanel panelTimeseries;
	private JComboBox cboTimeseries;

	
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
	
	public String getRaingageType() {
		if (cboRaingageType.getSelectedIndex() > -1){
			return cboRaingageType.getSelectedItem().toString();
		}
		return "";
	}
	
	public void enablePanelFile(boolean enabled) {
		panelTimeseries.setEnabled(!enabled);
		cboTimeseries.setEnabled(!enabled);
		panelFile.setEnabled(enabled);
		txtUnits.setEnabled(enabled);
		txtSta.setEnabled(enabled);
		txtFname.setEnabled(enabled);
		btnFileFname.setEnabled(enabled);
	}

	
	private void initConfig() {

		setTitle(BUNDLE.getString("RaingageDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 526, 339);
		getContentPane().setLayout(new MigLayout("", "[402.00][200px]", "[100:122.00][60.00px][80.00][10px][36.00]"));

		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("RaingageDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[85px:85px][130.00:150.00][10px][80px][150]", "[][][][10]"));

		JLabel lblid = new JLabel(BUNDLE.getString("RaingageDialog.lblid.text")); //$NON-NLS-1$
		panelGeneral.add(lblid, "cell 0 0,alignx trailing");

		textField_5 = new JTextField();
		textField_5.setName("rg_id");
		textField_5.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField_5, "cell 1 0,growx");
								
		JLabel lblIgnoreSnowmelt = new JLabel(BUNDLE.getString("RaingageDialog.lblIgnoreSnowmelt.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreSnowmelt, "cell 0 1,alignx trailing");
		
		cboRaingageType = new JComboBox();
		cboRaingageType.setActionCommand("changeRaingageType");
		panelGeneral.add(cboRaingageType, "cell 1 1,growx");
		cboRaingageType.setName("rgage_type");

		JLabel lblFlowUnits = new JLabel(BUNDLE.getString("RaingageDialog.lblFlowUnits.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowUnits, "cell 3 1,alignx trailing");
		
		JComboBox flow_units = new JComboBox();
		panelGeneral.add(flow_units, "cell 4 1,growx");
		flow_units.setName("form_type");
				
		JLabel lblNewLabel_1 = new JLabel(BUNDLE.getString("RaingageDialog.lblNewLabel_1.text")); //$NON-NLS-1$
		panelGeneral.add(lblNewLabel_1, "cell 0 2,alignx trailing");

		textField_1 = new JTextField();
		textField_1.setName("intvl");
		textField_1.setDocument(new MaxLengthTextDocument(50));
		panelGeneral.add(textField_1, "cell 1 2,growx");

		JLabel lblInfiltration = new JLabel(BUNDLE.getString("RaingageDialog.lblInfiltration.text")); //$NON-NLS-1$
		panelGeneral.add(lblInfiltration, "cell 3 2,alignx trailing");
		
		textField_2 = new JTextField();
		textField_2.setName("scf");
		textField_2.setDocument(new MaxLengthTextDocument(14));
		panelGeneral.add(textField_2, "cell 4 2,growx");

		ImageIcon image = new ImageIcon("images/imago.png");
		super.setIconImage(image.getImage());

		btnPrevious = new JButton("<");
		btnPrevious.setActionCommand("movePrevious");
		getContentPane().add(btnPrevious, "flowx,cell 1 4");

		btnNext = new JButton(">");
		btnNext.setActionCommand("moveNext");
		getContentPane().add(btnNext, "cell 1 4,alignx right");

		btnSave = new JButton(BUNDLE.getString("Generic.btnSave.text"));
		btnSave.setActionCommand("saveData");
		getContentPane().add(btnSave, "cell 1 4,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text"));
		getContentPane().add(btnClose, "cell 1 4,alignx right");				

		panelTimeseries = new JPanel();
		panelTimeseries.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelTimeseries.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("RaingageDialog.panelTimeseries.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelTimeseries, "cell 0 1 2 1,grow");
		panelTimeseries.setLayout(new MigLayout("", "[85px:85px][150px:150][10.00][80][200]", "[]"));

		JLabel lblFlowRouting = new JLabel(BUNDLE.getString("RaingageDialog.lblFlowRouting.text")); //$NON-NLS-1$
		panelTimeseries.add(lblFlowRouting, "cell 0 0,alignx right");

		cboTimeseries = new JComboBox();
		panelTimeseries.add(cboTimeseries, "cell 1 0,growx");
		cboTimeseries.setName("timser_id");

		panelFile = new JPanel();
		panelFile.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelFile.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("RaingageDialog.panelFile.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelFile, "cell 0 2 2 1,grow");
		panelFile.setLayout(new MigLayout("", "[85px:85px][150.00:150][80px][10px][150]", "[][]"));
				
		JLabel lblIgnoreRouting = new JLabel(BUNDLE.getString("RaingageDialog.lblIgnoreRouting.text")); //$NON-NLS-1$
		panelFile.add(lblIgnoreRouting, "cell 0 0,alignx right");

		txtUnits = new JTextField();
		panelFile.add(txtUnits, "cell 1 0,growx");
		txtUnits.setName("units");
		txtUnits.setDocument(new MaxLengthTextDocument(3));

		JLabel lblMinSlope = new JLabel(BUNDLE.getString("RaingageDialog.lblMinSlope.text")); //$NON-NLS-1$
		panelFile.add(lblMinSlope, "cell 2 0 2 1,alignx right");
		lblMinSlope.setName("");

		txtSta = new JTextField();
		panelFile.add(txtSta, "cell 4 0,growx");
		txtSta.setName("sta");
		txtSta.setDocument(new MaxLengthTextDocument(12));
								
		JLabel lblIgnoreGroundwater = new JLabel(BUNDLE.getString("RaingageDialog.lblIgnoreGroundwater.text")); //$NON-NLS-1$
		panelFile.add(lblIgnoreGroundwater, "cell 0 1,alignx right");

		txtFname = new JTextField();
		panelFile.add(txtFname, "cell 1 1 2 1,growx");
		txtFname.setName("fname");
		txtFname.setDocument(new MaxLengthTextDocument(254));

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
		cboRaingageType.addActionListener(this);
		super.setupListeners();
	}

	
}