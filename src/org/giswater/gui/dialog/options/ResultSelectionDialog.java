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
package org.giswater.gui.dialog.options;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;


public class ResultSelectionDialog extends AbstractOptionsDialog {

	private static final long serialVersionUID = -6349825417550216902L;
	
	
	public ResultSelectionDialog() {
		initConfig();
		createComponentMap();
	}
	
	
	private void initConfig(){

		setTitle("Table result_selection");
		setBounds(0, 0, 415, 155);
		getContentPane().setLayout(new MigLayout("", "[90.00][392.00]", "[66.00][5px][30.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,growy");
		panelGeneral.setLayout(new MigLayout("", "[75.00][115.00:115.00][10px][80px][115.00:115.00px]", "[25px:n][10px:n]"));
		
		JLabel lblStatistic = new JLabel("Result id:");
		panelGeneral.add(lblStatistic, "cell 0 0,alignx trailing");
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setName("result_id");
		panelGeneral.add(comboBox, "cell 1 0,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
		setupListeners();
		
	}

	
	protected void setupListeners() {
		dispose();
	}		
	

}