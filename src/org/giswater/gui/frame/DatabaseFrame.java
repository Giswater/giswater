package org.giswater.gui.frame;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.giswater.gui.MainFrame;
import org.giswater.gui.panel.DatabasePanel;
import org.giswater.util.Utils;


public class DatabaseFrame extends JInternalFrame {

	private static final long serialVersionUID = 5510726193938743935L;
	private DatabasePanel panel;
	public MainFrame mainFrame;
	
	
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
        
        setClosable(true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setVisible(false);

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, 466, GroupLayout.PREFERRED_SIZE)
        			.addGap(28))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(panel, 407, 407, 410)
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