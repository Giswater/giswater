package org.giswater.gui.frame;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JInternalFrame;

import org.giswater.gui.panel.EpaPanel;


public class EpaFrame extends JInternalFrame {

	private static final long serialVersionUID = 5510726193938743935L;
	private EpaPanel panel;
	
	
	public EpaFrame(){
		setMaximizable(true);
		initComponents();
	}
	

	public EpaPanel getPanel(){
		return panel;
	}
	
	           
    private void initComponents() {

    	panel = new EpaPanel();

        setTitle("");
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setVisible(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, 558, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(0, Short.MAX_VALUE))
        );
        getContentPane().setLayout(layout);

        pack();
        
    }

    
}