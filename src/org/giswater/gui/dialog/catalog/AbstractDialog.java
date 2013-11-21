package org.giswater.gui.dialog.catalog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.giswater.controller.catalog.CatalogController;


@SuppressWarnings("rawtypes")
public abstract class AbstractDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = -7319857198967955753L;
	protected CatalogController controller;
	public HashMap<String, JComboBox> comboMap;
	public HashMap<String, JTextField> textMap;
	protected JButton btnPrevious;
	protected JButton btnNext;
	protected JButton btnSave;
	protected JButton btnCreate;	
	protected JButton btnDelete;	
	
	
	public void setControl(CatalogController controller) {
		this.controller = controller;
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
	
	
	public void setTextField(JTextField textField, Object value) {
		if (value!=null){
			textField.setText(value.toString());
		}
		else{
			textField.setText("");
		}
	}	
	
	
	protected void createComponentMap() {
		
        comboMap = new HashMap<String, JComboBox>();
        textMap = new HashMap<String, JTextField>();
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
	            }
        	}
        }

	}	
	
	
//	protected void setupListeners() {
//		btnSave.addActionListener(this);	
//		btnPrevious.addActionListener(this);
//		btnNext.addActionListener(this);
//		btnCreate.addActionListener(this);
//		btnDelete.addActionListener(this);
//	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}
	
	
}
