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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.giswater.util.MaxLengthTextDocument;

import net.miginfocom.swing.MigLayout;
import java.util.ResourceBundle;


public class ProjectDialog extends AbstractCatalogDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form"); //$NON-NLS-1$

	
	public ProjectDialog() {
		initConfig();
		createComponentMap();
	}
	
	
	private void initConfig() {

		setTitle(BUNDLE.getString("ProjectDialog.this.title")); //$NON-NLS-1$
		setBounds(0, 0, 344, 206);
		getContentPane().setLayout(new MigLayout("", "[90.00][392.00]", "[113.00][5px][30.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("ProjectDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,growy");
		panelGeneral.setLayout(new MigLayout("", "[60][115.00:237.00,grow]", "[25px:n][][10px:n]"));
		
		JLabel lblStatistic = new JLabel(BUNDLE.getString("ProjectDialog.lblStatistic.text")); //$NON-NLS-1$
		panelGeneral.add(lblStatistic, "cell 0 0,alignx trailing");
		
		JTextField txtTitle = new JTextField();
		txtTitle.setName("title");
		txtTitle.setDocument(new MaxLengthTextDocument(254));	
		panelGeneral.add(txtTitle, "cell 1 0,growx");
		txtTitle.setColumns(10);
		
		JLabel lblAuthor = new JLabel(BUNDLE.getString("ProjectDialog.lblAuthor.text")); //$NON-NLS-1$
		panelGeneral.add(lblAuthor, "cell 0 1,alignx trailing");
		
		JTextField txtAuthor = new JTextField();
		txtAuthor.setName("author");
		txtAuthor.setDocument(new MaxLengthTextDocument(50));
		panelGeneral.add(txtAuthor, "cell 1 1,growx");
		
		JLabel lblDate = new JLabel(BUNDLE.getString("ProjectDialog.lblDate.text")); //$NON-NLS-1$
		panelGeneral.add(lblDate, "cell 0 2,alignx trailing");
		
		JTextField txtDate = new JTextField();
		txtDate.setName("date");
		txtDate.setDocument(new MaxLengthTextDocument(12));
		panelGeneral.add(txtDate, "cell 1 2,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
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