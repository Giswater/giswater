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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;


public class TimeseriesDetailDialog extends AbstractCatalogDialog {
	
	private String timesType;
	private JTextField txtId;
	private JTextField txtTimserId;
	private JTextField txtDate;
	private JTextField txtHour;
	private JTextField txtTime;
	private JTextField txtFname;
	private JTextField txtValue;
	private TimeseriesDialog parent;
	private JButton btnFileFname;
	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form");
	
	
	public TimeseriesDetailDialog(TimeseriesDialog parentDialog, String timesType) {
		this.timesType = timesType;
		this.parent = parentDialog;
		initConfig();
		setTimesType();		
		createComponentMap();
	}


	private void initConfig() {

		setTitle(BUNDLE.getString("TimeseriesDetailDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 502, 196);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[109.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), BUNDLE.getString("TimeseriesDetailDialog.panelGeneral.borderTitle"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[60.00][150,grow][10px][80px][150]", "[][][]"));
		
		JLabel lblGeom = new JLabel(BUNDLE.getString("TimeseriesDetailDialog.lblGeom.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeom, "cell 0 0,alignx trailing");
		
		txtDate = new JTextField();
		txtDate.setName("date");
		txtDate.setColumns(10);
		panelGeneral.add(txtDate, "cell 1 0,growx");
		
		JLabel lblGeom_1 = new JLabel(BUNDLE.getString("TimeseriesDetailDialog.lblGeom_1.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeom_1, "cell 3 0,alignx trailing");
		
		txtHour = new JTextField();
		txtHour.setName("hour");
		txtHour.setColumns(10);
		panelGeneral.add(txtHour, "cell 4 0,growx");
		
		JLabel lblGeom_2 = new JLabel(BUNDLE.getString("TimeseriesDetailDialog.lblGeom_2.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeom_2, "cell 0 1,alignx trailing");
		
		txtTime = new JTextField();
		txtTime.setName("time");
		txtTime.setColumns(10);
		panelGeneral.add(txtTime, "cell 1 1,growx");
		
		JLabel lblNewLabel_1 = new JLabel(BUNDLE.getString("TimeseriesDetailDialog.lblNewLabel_1.text")); //$NON-NLS-1$
		panelGeneral.add(lblNewLabel_1, "cell 3 1,alignx trailing");
		
		txtValue = new JTextField();
		txtValue.setName("value");
		txtValue.setColumns(10);
		panelGeneral.add(txtValue, "cell 4 1,growx");
		
		JLabel lblGeom_3 = new JLabel(BUNDLE.getString("TimeseriesDetailDialog.lblGeom_3.text")); //$NON-NLS-1$
		panelGeneral.add(lblGeom_3, "cell 0 2,alignx trailing");
		
		txtFname = new JTextField();
		txtFname.setName("fname");
		txtFname.setColumns(10);
		panelGeneral.add(txtFname, "cell 1 2 3 1,growx");
		
		btnFileFname = new JButton("...");
		btnFileFname.setActionCommand("chooseFileFname");
		panelGeneral.add(btnFileFname, "flowx,cell 4 2");
		
		txtTimserId = new JTextField();
		txtTimserId.setVisible(false);
		panelGeneral.add(txtTimserId, "cell 4 2");
		txtTimserId.setName("timser_id");
		txtTimserId.setColumns(10);
		
		txtId = new JTextField();
		txtId.setVisible(false);
		panelGeneral.add(txtId, "cell 4 2");
		txtId.setName("id");
		txtId.setColumns(10);
		
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

	
	protected void setupListeners() {
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveData();
			}
		});
		
		// Event to update detail table contents with new data
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				parent.getController().closeDetailDialog();
			}
		});		
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				parent.getController().closeDetailDialog();			
			}
		});		
		btnFileFname.addActionListener(this);
		
	}
	
	
	public void setTimesType() {

		txtDate.setEnabled(false);
		txtHour.setEnabled(false);
		txtTime.setEnabled(false);
		txtValue.setEnabled(false);
		txtFname.setEnabled(false);
		if (timesType.equals("ABSOLUTE")) {
			txtDate.setEnabled(true);
			txtHour.setEnabled(true);
			txtValue.setEnabled(true);
		}
		else if (timesType.equals("RELATIVE")) {
			txtTime.setEnabled(true);
			txtValue.setEnabled(true);
		}
		if (timesType.equals("FILE")) {
			txtFname.setEnabled(true);
		}
		
	}


	public void setTimserId(String timserId) {
		txtTimserId.setText(timserId);
	}
	
	public void setFileFname(String path) {
		txtFname.setText(path);
	}
	
	
}