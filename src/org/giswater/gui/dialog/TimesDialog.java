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

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.TimesController;
import org.giswater.util.Utils;


@SuppressWarnings("rawtypes")
public class TimesDialog extends JDialog {

	private static final long serialVersionUID = -6349825417550216902L;
	private TimesController controller;
	public HashMap<String, JComboBox> comboMap;
	public HashMap<String, JTextField> textMap;
	private JTextField txtQuality;
	private JTextField txtHydraulic;
	private JTextField txtDuration;
	private JTextField txtRule;
	private JTextField txtPattern;
	private JTextField txtReport;
	private JTextField txtReportStart;
	private JTextField txtPatternStart;
	private JTextField txtStart;
	private AbstractButton btnSave;
	
	
	public TimesDialog() {
		initConfig();
		createComponentMap();
	}
	
	
	public void setControl(TimesController controller) {
		this.controller = controller;
	}		

	
	public void setTextField(JTextField textField, Object value) {
		if (value!=null){
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
        
		JPanel panel = (JPanel) components[0];            
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


	private void initConfig(){

		setTitle("Times Table");
		setBounds(0, 0, 468, 283);
		getContentPane().setLayout(new MigLayout("", "[90.00][392.00]", "[179.00][10px][36.00]"));
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelGeneral.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "GENERAL", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		getContentPane().add(panelGeneral, "cell 0 0 2 1,growy");
		panelGeneral.setLayout(new MigLayout("", "[75.00][115.00:115.00][10px][80px][115.00:115.00px]", "[25px:n][25px:n][25px:n][25px:n][25px:n][10px:n]"));

		JLabel lblFlowUnits = new JLabel("Duration:");
		panelGeneral.add(lblFlowUnits, "cell 0 0,alignx trailing");
		
		txtDuration = new JTextField();
		txtDuration.setName("duration");
		txtDuration.setColumns(10);
		panelGeneral.add(txtDuration, "cell 1 0,growx");
		
		JLabel lblInfiltration = new JLabel("Hydraulic timestep:");
		panelGeneral.add(lblInfiltration, "cell 3 0,alignx trailing");
		
		txtHydraulic = new JTextField();
		txtHydraulic.setName("hydraulic_timestep");
		txtHydraulic.setColumns(10);
		panelGeneral.add(txtHydraulic, "cell 4 0,growx");
		
		JLabel lblNewLabel_1 = new JLabel("Quality timestep:");
		panelGeneral.add(lblNewLabel_1, "cell 0 1,alignx trailing");
		
		txtQuality = new JTextField();
		txtQuality.setName("quality_timestep");
		txtQuality.setColumns(10);
		panelGeneral.add(txtQuality, "cell 1 1,growx");
		
		JLabel lblRuleTimestep = new JLabel("Rule timestep:");
		lblRuleTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblRuleTimestep, "cell 3 1,alignx trailing");
		
		txtRule = new JTextField();
		txtRule.setName("rule_timestep");
		txtRule.setColumns(10);
		panelGeneral.add(txtRule, "cell 4 1,growx");
		
		JLabel lblPatternTimestep = new JLabel("Pattern timestep:");
		lblPatternTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternTimestep, "cell 0 2,alignx trailing");
		
		txtPattern = new JTextField();
		txtPattern.setName("pattern_timestep");
		txtPattern.setColumns(10);
		panelGeneral.add(txtPattern, "cell 1 2,growx");
		
		JLabel lblPatternStep = new JLabel("Pattern start:");
		lblPatternStep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblPatternStep, "cell 3 2,alignx trailing");
		
		txtPatternStart = new JTextField();
		txtPatternStart.setName("pattern_start");
		txtPatternStart.setColumns(10);
		panelGeneral.add(txtPatternStart, "cell 4 2,growx");
		
		JLabel lblReportTimestep = new JLabel("Report timestep:");
		lblReportTimestep.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportTimestep, "cell 0 3,alignx trailing");
		
		txtReport = new JTextField();
		txtReport.setName("report_timestep");
		txtReport.setColumns(10);
		panelGeneral.add(txtReport, "cell 1 3,growx");
		
		JLabel lblReportStart = new JLabel("Report start:");
		lblReportStart.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblReportStart, "cell 3 3,alignx trailing");
		
		txtReportStart = new JTextField();
		txtReportStart.setName("report_start");
		txtReportStart.setColumns(10);
		panelGeneral.add(txtReportStart, "cell 4 3,growx");
		
		JLabel lblStartClocktime = new JLabel("Start clocktime:");
		lblStartClocktime.setHorizontalAlignment(SwingConstants.TRAILING);
		panelGeneral.add(lblStartClocktime, "cell 0 4,alignx trailing");
		
		txtStart = new JTextField();
		txtStart.setName("start_clocktime");
		txtStart.setColumns(10);
		panelGeneral.add(txtStart, "cell 1 4,growx");
		
		JLabel lblStatistic = new JLabel("Statistic:");
		panelGeneral.add(lblStatistic, "cell 3 4,alignx trailing");
		
		JComboBox comboBox = new JComboBox();
		comboBox.setName("statistic");
		panelGeneral.add(comboBox, "cell 4 4,growx");
		
		ImageIcon image = new ImageIcon("images/imago.png");        
		super.setIconImage(image.getImage());		
		
		btnSave = new JButton("Save");
		getContentPane().add(btnSave, "cell 1 2,alignx right");
		
		setupListeners();
		
	}

	
	private void setupListeners() {
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.saveData();
				dispose();
			}
		});
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				int res = Utils.confirmDialog("save_data?");
				if (res == 0){
					controller.saveData();
				}
			}
		});			
		
	}		
	

}