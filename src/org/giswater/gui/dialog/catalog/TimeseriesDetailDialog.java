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
package org.giswater.gui.dialog.catalog;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.giswater.util.Utils;

import net.miginfocom.swing.MigLayout;


@SuppressWarnings("rawtypes")
public class TimeseriesDetailDialog extends AbstractDialog{

	private static final long serialVersionUID = -6349825417550216902L;
	private JTextField textField_2;
	private JTextField txtDate;
	private JTextField txtHour;
	private JTextField txtTime;
	private JTextField txtFname;
	private JTextField txtValue;
	protected JButton btnSave;	
	private JComboBox cboType;
	private JComboBox<String> cboTimserId;
	private TimeseriesDialog parent;
	
	
	public TimeseriesDetailDialog(TimeseriesDialog parentDialog) {
		this.parent = parentDialog;
		initConfig();
		createComponentMap();
	}


	public void setTimserId(String timserId) {
		cboTimserId.setEnabled(true);
		cboTimserId.setSelectedItem(timserId);
	}
	

	@SuppressWarnings({ "unchecked" })
	private void initConfig(){

		setTitle("Table inp_timeseries");
		setBounds(100, 100, 502, 253);
		getContentPane().setLayout(new MigLayout("", "[401.00,grow][200px]", "[161.00][5px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,grow");
		panelGeneral.setLayout(new MigLayout("", "[60.00][150,grow][10px][80px][150]", "[][][][][]"));
		
		JLabel lblInfiltration = new JLabel("Id:");
		panelGeneral.add(lblInfiltration, "cell 0 0,alignx trailing");
		
		textField_2 = new JTextField();
		textField_2.setEnabled(false);
		textField_2.setName("id");
		textField_2.setColumns(10);
		panelGeneral.add(textField_2, "cell 1 0,growx");
				
		JLabel lblTsectid = new JLabel("Timser_id:");
		panelGeneral.add(lblTsectid, "cell 0 1,alignx trailing");
		
		cboTimserId = new JComboBox();
		cboTimserId.setEnabled(false);
		cboTimserId.setName("timser_id");
		cboTimserId.setActionCommand("shapeChanged");
		panelGeneral.add(cboTimserId, "cell 1 1,growx");
		
		JLabel lblFlowUnits = new JLabel("Type:");
		panelGeneral.add(lblFlowUnits, "cell 3 1,alignx trailing");

		cboType = new JComboBox();
		cboType.setActionCommand("timesTypeChanged");
		panelGeneral.add(cboType, "cell 4 1,growx");
		cboType.setName("times_type");
		
		JLabel lblGeom = new JLabel("Date:");
		panelGeneral.add(lblGeom, "cell 0 2,alignx trailing");
		
		txtDate = new JTextField();
		txtDate.setName("date");
		txtDate.setColumns(10);
		panelGeneral.add(txtDate, "cell 1 2,growx");
		
		JLabel lblGeom_1 = new JLabel("Hour:");
		panelGeneral.add(lblGeom_1, "cell 3 2,alignx trailing");
		
		txtHour = new JTextField();
		txtHour.setName("hour");
		txtHour.setColumns(10);
		panelGeneral.add(txtHour, "cell 4 2,growx");
		
		JLabel lblGeom_2 = new JLabel("Time:");
		panelGeneral.add(lblGeom_2, "cell 0 3,alignx trailing");
		
		txtTime = new JTextField();
		txtTime.setName("time");
		txtTime.setColumns(10);
		panelGeneral.add(txtTime, "cell 1 3,growx");
		
		JLabel lblNewLabel_1 = new JLabel("Value:");
		panelGeneral.add(lblNewLabel_1, "cell 3 3,alignx trailing");
		
		txtValue = new JTextField();
		txtValue.setName("value");
		txtValue.setColumns(10);
		panelGeneral.add(txtValue, "cell 4 3,growx");
		
		JLabel lblGeom_3 = new JLabel("FName:");
		panelGeneral.add(lblGeom_3, "cell 0 4,alignx trailing");
		
		txtFname = new JTextField();
		txtFname.setName("fname");
		txtFname.setColumns(10);
		panelGeneral.add(txtFname, "cell 1 4,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());
		
		btnSave = new JButton("Save");
		btnSave.setToolTipText("Save record");
		btnSave.setActionCommand("saveData");
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		setupListeners();
		
	}

	
	protected void setupListeners() {
		
		cboType.addActionListener(this);		
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveData();
				dispose();
			}
		});
		
		// Event to update detail table contents with new data
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				int res = Utils.confirmDialog("save_data?");
				if (res == 0){
					controller.saveData();
				    parent.getController().closeDetailDialog();
				}
			}
		});		
		
	}
	
	
	public void timesTypeChanged(){

		txtDate.setEnabled(false);
		txtHour.setEnabled(false);
		txtTime.setEnabled(false);
		txtValue.setEnabled(false);
		txtFname.setEnabled(false);
		String type = cboType.getSelectedItem().toString().toUpperCase();
		if (type.equals("ABSOLUTE")){
			txtDate.setEnabled(true);
			txtHour.setEnabled(true);
			txtValue.setEnabled(true);
		}
		else if (type.equals("RELATIVE")){
			txtTime.setEnabled(true);
			txtValue.setEnabled(true);
		}
		if (type.equals("FILE")){
			txtFname.setEnabled(true);
		}
		
	}
	
	
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		controller.action(e.getActionCommand());
//		if (e.getActionCommand().equals("saveData")){
//			dispose();
//		}
//	}	
	
	
}