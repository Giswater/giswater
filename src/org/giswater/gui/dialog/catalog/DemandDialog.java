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

import net.miginfocom.swing.MigLayout;

import org.giswater.util.MaxLengthTextDocument;
import java.util.ResourceBundle;


public class DemandDialog extends AbstractCatalogDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); 

	
	public DemandDialog() {
		initConfig();
		createComponentMap();
	}	
	

	private void initConfig() {

		setTitle(BUNDLE.getString("DemandDialog.this.title")); 
		setBounds(100, 100, 398, 225);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[123.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("DemandDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); 
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[50][231.00,grow][150]", "[][][][]"));
		
		JLabel lblInfiltration = new JLabel(BUNDLE.getString("DemandDialog.lblInfiltration.text")); 
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		JComboBox<String> cboNode = new JComboBox<String>();
		cboNode.setName("node_id");
		cboNode.setActionCommand("");
		panelGeneral.add(cboNode, "cell 1 0,growx");
		
		JLabel lblTsectid = new JLabel(BUNDLE.getString("DemandDialog.lblTsectid.text")); 
		panelGeneral.add(lblTsectid, "cell 0 1,alignx trailing");
		
		JTextField txtDemand = new JTextField();
		txtDemand.setName("demand");
		txtDemand.setDocument(new MaxLengthTextDocument(18));
		panelGeneral.add(txtDemand, "cell 1 1,growx");
		
		JLabel lblPatternId = new JLabel(BUNDLE.getString("DemandDialog.lblPatternId.text")); 
		panelGeneral.add(lblPatternId, "cell 0 2,alignx trailing");
		
		JComboBox<String> cboPattern = new JComboBox<String>();
		cboPattern.setName("pattern_id");
		cboPattern.setActionCommand("");
		panelGeneral.add(cboPattern, "cell 1 2,growx");
		
		JLabel lblDemandType = new JLabel(BUNDLE.getString("DemandDialog.lblDemandType.text")); 
		panelGeneral.add(lblDemandType, "cell 0 3,alignx trailing");
		
		JTextField txtDemandType = new JTextField();
		txtDemandType.setName("deman_type");
		txtDemandType.setDocument(new MaxLengthTextDocument(18));
		panelGeneral.add(txtDemandType, "cell 1 3,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnCreate = new JButton("+");
		btnCreate.setToolTipText(BUNDLE.getString("DemandDialog.btnInsert.toolTipText"));
		btnCreate.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCreate.setActionCommand("create");
		getContentPane().add(btnCreate, "flowx,cell 1 2");
		
		btnDelete = new JButton("-");
		btnDelete.setToolTipText(BUNDLE.getString("DemandDialog.btnDelete.toolTipText"));
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDelete.setActionCommand("delete");
		getContentPane().add(btnDelete, "cell 1 2");
		
		btnPrevious = new JButton("<");
		btnPrevious.setToolTipText(BUNDLE.getString("DemandDialog.btnPrevious.toolTipText"));
		btnPrevious.setActionCommand("movePrevious");
		getContentPane().add(btnPrevious, "cell 1 2");
		
		btnNext = new JButton(">");
		btnNext.setToolTipText(BUNDLE.getString("DemandDialog.btnNext.toolTipText"));
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

	
}