package org.giswater.gui.frame;

import java.beans.PropertyVetoException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.giswater.gui.panel.DatabasePanel;
import org.giswater.util.Utils;


public class DatabaseFrame extends JInternalFrame {

	private static final long serialVersionUID = 5510726193938743935L;
	private DatabasePanel panel;
	private MainFrame mainFrame;
	
	
	public DatabaseFrame(){
		initComponents();
	}
	
	public DatabaseFrame(MainFrame mf){
		this.mainFrame = mf;
		initComponents();
	}
	
	public DatabasePanel getPanel(){
		return panel;
	}
	
	           
    private void initComponents() {

    	panel = new DatabasePanel();

    	setTitle(Utils.getBundleString("db_options"));
		setMaximizable(true);    	
        setClosable(true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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
        			.addContainerGap()
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, 466, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(panel, GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        			.addContainerGap())
        );
        getContentPane().setLayout(layout);
        
        this.addInternalFrameListener(new InternalFrameAdapter() {
        	public void internalFrameClosing(InternalFrameEvent e) {
        		mainFrame.swmmFrame.getPanel().setOptDatabaseSelected();
        		mainFrame.epanetFrame.getPanel().setOptDatabaseSelected();
        	}
        });        

        pack();
        
    }

    
}