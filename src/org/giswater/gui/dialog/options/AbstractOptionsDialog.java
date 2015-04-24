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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.giswater.controller.OptionsController;

import com.toedter.calendar.JDateChooser;


@SuppressWarnings("rawtypes")
public abstract class AbstractOptionsDialog extends JDialog implements ActionListener {

	protected OptionsController controller;
	public HashMap<String, JComboBox> comboMap;
	public HashMap<String, JTextField> textMap;
	public HashMap<String, JDateChooser> dateMap;
	protected JButton btnSave;
	protected JButton btnClose;
	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("form");
	
	
	public AbstractOptionsDialog() { }
	
	
	public OptionsController getController() {
		return controller;
	}
	
	
	public void setController(OptionsController controller) {
		this.controller = controller;
	}	
	
	
	public void setComboModel(JComboBox<String> combo, Vector<String> items) {
		if (items != null){
			ComboBoxModel<String> cbm = new DefaultComboBoxModel<String>(items);
			combo.setModel(cbm);
		}
	}	
	
	
	public void setComboSelectedItem(JComboBox combo, String item) {
		combo.setSelectedItem(item);
	}	
	
	
	public void setTextField(JTextField textField, Object value) {
		if (value != null) {
			textField.setText(value.toString());
		}
		else {
			textField.setText("");
		}
	}	
	
	public void setDate(JDateChooser dateField, Date value) {
		if (value!=null) {
			dateField.setDate(value);
		}
		else{
			dateField.setDate(null);
		}
	}	
	
	public void enablePrevious(boolean enable) { }
	
	public void enableNext(boolean enable) {	}	
	
	public void enableSave(boolean enable) {
		if (btnSave != null) {
			btnSave.setEnabled(enable);
		}
	}	

	
	protected void createComponentMap() {
		
        comboMap = new HashMap<String, JComboBox>();
        textMap = new HashMap<String, JTextField>();
        dateMap = new HashMap<String, JDateChooser>();
        Component[] components = getContentPane().getComponents();

        for (int j=0; j<components.length; j++) {
        	if (components[j] instanceof JPanel){
        		JPanel panel = (JPanel) components[j];            
	            Component[] comp = panel.getComponents();        
	            for (int i=0; i < comp.length; i++) {        
	            	if (comp[i] instanceof JComboBox) {         	
	            		comboMap.put(comp[i].getName(), (JComboBox) comp[i]);
	            	}
	            	else if (comp[i] instanceof JTextField) {      
	            		textMap.put(comp[i].getName(), (JTextField) comp[i]);
	            	}
	            	else if (comp[i] instanceof JDateChooser) {      
	            		dateMap.put(comp[i].getName(), (JDateChooser) comp[i]);
	            	}	            	
	            }
        	}
        }

	}	
	

	protected void setupListeners() {
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				dispose();
			}
		});
		if (btnSave != null){
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					controller.saveData();
				}
			});		
		}
		if (btnClose != null){
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});		
		}		
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
		if (e.getActionCommand().equals("saveData")) {
			dispose();
		}
	}

	
}