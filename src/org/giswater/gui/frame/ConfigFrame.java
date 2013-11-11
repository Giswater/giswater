package org.giswater.gui.frame;

import java.beans.PropertyVetoException;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JInternalFrame;

import org.giswater.gui.panel.ConfigPanel;
import org.giswater.util.Utils;


public class ConfigFrame extends JInternalFrame {

	private static final long serialVersionUID = 5510726193938743935L;
	private ConfigPanel panel;
	
	
	public ConfigFrame(){
		initComponents();
	}
	
	public ConfigPanel getPanel(){
		return panel;
	}
	
	           
    private void initComponents() {

    	panel = new ConfigPanel();

        setTitle(Utils.getBundleString("software_panel"));
		setMaximizable(true);
    	setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        
        setFrameIcon(new ImageIcon(Utils.getIconPath()));
		try {
			setIcon(true);
		} catch (PropertyVetoException e) {
			Utils.getLogger().warning(e.getMessage());
		}        

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(panel, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        			.addContainerGap())
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(14, Short.MAX_VALUE))
        );
        getContentPane().setLayout(layout);
        
        pack();
        
    }

    
}