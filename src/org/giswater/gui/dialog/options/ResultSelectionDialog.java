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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.giswater.util.Utils;

import net.miginfocom.swing.MigLayout;

import java.util.ResourceBundle;


public class ResultSelectionDialog extends AbstractOptionsDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	private JComboBox<String> cboResultSelection;
	

	public ResultSelectionDialog() {
		initConfig();
		createComponentMap();
	}	
	
	public String getResultSelection() {
		return cboResultSelection.getSelectedItem().toString();
	}
	
	
	private void initConfig(){

		setTitle(BUNDLE.getString("ResultSelectionDialog.this.title")); //$NON-NLS-1$
		setBounds(0, 0, 375, 176);
		getContentPane().setLayout(new MigLayout("", "[90.00][392.00]", "[40px:n][66.00][5px][30.00]"));
		
		String msg = Utils.getBundleString("ResultSelectionDialog.result_selection_message"); //$NON-NLS-1$
		JLabel lblNewLabel = new JLabel(msg);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		getContentPane().add(lblNewLabel, "cell 0 0 2 1,alignx center,aligny center");
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("ResultSelectionDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 1 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[75.00][115.00:159.00][40.00px]", "[25px:n][10px:n]"));
		
		JLabel lblStatistic = new JLabel(BUNDLE.getString("ResultSelectionDialog.lblStatistic.text")); //$NON-NLS-1$
		panelGeneral.add(lblStatistic, "cell 0 0,alignx trailing");
		
		cboResultSelection = new JComboBox<String>();
		cboResultSelection.setActionCommand("changeResultSelection");
		cboResultSelection.setName("result_id");
		panelGeneral.add(cboResultSelection, "cell 1 0,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
		setupListeners();
		
	}

	
	protected void setupListeners() {
		cboResultSelection.addActionListener(this);
	}		
	

}