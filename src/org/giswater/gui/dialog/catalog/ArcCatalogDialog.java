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
package org.giswater.gui.dialog.catalog;

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



public class ArcCatalogDialog extends AbstractCatalogDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$
	
	private JTextField txtTsect;
	private JComboBox<String> cboShape;
	private JTextField txtCurve;
	protected JButton btnPrevious;
	protected JButton btnNext;
	protected JButton btnCreate;
	protected JButton btnDelete;	
	
	
	public ArcCatalogDialog() {
		initConfig();
		createComponentMap();
		
	}	

	public void enablePrevious(boolean enable) {
		if (btnPrevious != null) {
			btnPrevious.setEnabled(enable);
		}
	}
	
	public void enableNext(boolean enable) {
		if (btnNext != null) {
			btnNext.setEnabled(enable);
		}
	}		

	private void initConfig() {

		setTitle(BUNDLE.getString("ArcCatalogDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 502, 278);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[178.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("ArcCatalogDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[65.00][150][10px][80px][150]", "[][][][][][]"));
		
		JLabel lblInfiltration = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblInfiltration.text")); //$NON-NLS-1$
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		JTextField textField_2 = new JTextField();
		textField_2.setName("id");
		textField_2.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField_2, "cell 1 0,growx");
				
		JLabel lblFlowUnits = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblFlowUnits.text")); //$NON-NLS-1$
		panelGeneral.add(lblFlowUnits, "cell 3 0,alignx trailing");

		cboShape = new JComboBox<String>();
		cboShape.setActionCommand("shapeChanged");
		panelGeneral.add(cboShape, "cell 4 0,growx");
		cboShape.setName("shape");
		
		JLabel lblShortDesc = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblShortDesc.text")); //$NON-NLS-1$
		panelGeneral.add(lblShortDesc, "cell 0 1,alignx trailing");
		
		JTextField textField_9 = new JTextField();
		textField_9.setName("short_des");
		textField_9.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField_9, "cell 1 1,growx");
		
		JLabel lblCurveid = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblCurveid.text")); //$NON-NLS-1$
		panelGeneral.add(lblCurveid, "cell 3 1,alignx trailing");
		
		txtCurve = new JTextField();
		txtCurve.setEnabled(false);
		txtCurve.setName("curve_id");
		txtCurve.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(txtCurve, "cell 4 1,growx");
		
		JLabel lblGeomr = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblGeomr.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeomr, "cell 0 2,alignx trailing");
		
		JTextField textField_7 = new JTextField();
		textField_7.setName("geom_r");
		textField_7.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField_7, "cell 1 2,growx");
		
		JLabel lblTsectid = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblTsectid.text")); //$NON-NLS-1$
		panelGeneral.add(lblTsectid, "cell 3 2,alignx trailing");
		
		txtTsect = new JTextField();
		txtTsect.setEnabled(false);
		txtTsect.setName("tsect_id");
		txtTsect.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(txtTsect, "cell 4 2,growx");
		
		JLabel lblGeom = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblGeom.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeom, "cell 0 3,alignx trailing");
		
		JTextField textField = new JTextField();
		textField.setName("geom1");
		textField.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField, "cell 1 3,growx");
		
		JLabel lblGeom_2 = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblGeom_2.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeom_2, "cell 3 3,alignx trailing");
		
		JTextField textField_5 = new JTextField();
		textField_5.setName("geom3");
		textField_5.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField_5, "cell 4 3,growx");
		
		JLabel lblGeom_1 = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblGeom_1.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeom_1, "cell 0 4,alignx trailing");
		
		JTextField textField_3 = new JTextField();
		textField_3.setName("geom2");
		textField_3.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField_3, "cell 1 4,growx");
		
		JLabel lblGeom_3 = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblGeom_3.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeom_3, "cell 3 4,alignx trailing");
		
		JTextField textField_6 = new JTextField();
		textField_6.setName("geom4");
		textField_6.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField_6, "cell 4 4,growx");
		
		JLabel lblIgnoreSnowmelt = new JLabel(BUNDLE.getString("ArcCatalogDialog.lblIgnoreSnowmelt.text")); //$NON-NLS-1$
		panelGeneral.add(lblIgnoreSnowmelt, "cell 0 5,alignx trailing");
		
		JTextField textField_4 = new JTextField();
		textField_4.setName("descript");
		textField_4.setDocument(new MaxLengthTextDocument(16));
		panelGeneral.add(textField_4, "cell 1 5 4 1,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnCreate = new JButton("+");
		btnCreate.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCreate.setToolTipText(BUNDLE.getString("ArcCatalogDialog.btnCreate.toolTipText")); //$NON-NLS-1$
		btnCreate.setActionCommand("create");
		getContentPane().add(btnCreate, "flowx,cell 1 2");
		
		btnDelete = new JButton("-");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setToolTipText(BUNDLE.getString("ArcCatalogDialog.btnDelete.toolTipText")); //$NON-NLS-1$
		btnDelete.setActionCommand("delete");
		getContentPane().add(btnDelete, "cell 1 2");
		
		btnPrevious = new JButton("<");
		btnPrevious.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnPrevious.setToolTipText(BUNDLE.getString("ArcCatalogDialog.btnPrevious.toolTipText")); //$NON-NLS-1$
		btnPrevious.setActionCommand("movePrevious");
		getContentPane().add(btnPrevious, "cell 1 2");
		
		btnNext = new JButton(">");
		btnNext.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext.setToolTipText(BUNDLE.getString("ArcCatalogDialog.btnNext.toolTipText")); //$NON-NLS-1$
		btnNext.setActionCommand("moveNext");
		getContentPane().add(btnNext, "cell 1 2");
		
		btnSave = new JButton(BUNDLE.getString("Generic.btnSave.text"));
		btnSave.setToolTipText(BUNDLE.getString("Generic.btnSave.toolTipText"));
		btnSave.setActionCommand("saveData");
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		btnClose = new JButton(BUNDLE.getString("Generic.btnClose.text"));
		btnClose.setToolTipText(BUNDLE.getString("Generic.btnClose.toolTipText"));
		btnClose.setActionCommand("closeWindow");
		getContentPane().add(btnClose, "cell 1 2,alignx right");
		
		setupListeners();
		
	}
	
	
	protected void setupListeners() {
		
		cboShape.addActionListener(this);	
		btnPrevious.addActionListener(this);
		btnNext.addActionListener(this);
		btnCreate.addActionListener(this);
		btnDelete.addActionListener(this);
		super.setupListeners();
		
	}
	
	
	public void shapeChanged() {
		
		txtCurve.setEnabled(false);
		txtTsect.setEnabled(false);
		String shape = cboShape.getSelectedItem().toString();
		if (shape.toUpperCase().equals("CUSTOM")) {
			txtCurve.setEnabled(true);
		}
		else if (shape.toUpperCase().equals("IRREGULAR")) {
			txtTsect.setEnabled(true);
		}
		
	}
	
	
}