package org.giswater.gui.frame;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JInternalFrame;

import org.giswater.gui.panel.ConfigPanel;


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

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setTitle("EPA Software");

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
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap())
        );
        getContentPane().setLayout(layout);
        
        pack();
        
    }

    
}