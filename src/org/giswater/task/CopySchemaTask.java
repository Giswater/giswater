package org.giswater.task;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;

import org.giswater.controller.ProjectPreferencesController;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.util.Utils;


public class CopySchemaTask extends SwingWorker<Void, Void> {
	
	private ProjectPreferencesPanel parentPanel;
	private ProjectPreferencesController controller;
	private String schemaName;
	private String newSchemaName;
	private boolean status;
	
	
	public CopySchemaTask(String schemaName, String newSchemaName) {
		this.schemaName = schemaName;
		this.newSchemaName = newSchemaName;
	}
	
	public void setParentPanel(ProjectPreferencesPanel parentPanel){
		this.parentPanel = parentPanel;
	}
	
	public void setController(ProjectPreferencesController controller){
		this.controller = controller;
	}
	
	
    @Override
    public Void doInBackground() { 
		
    	setProgress(1);
    	
    	// Disable view
    	Utils.setPanelEnabled(parentPanel, false);

		String sql = "SELECT "+schemaName+".clone_schema('"+schemaName+"', '"+newSchemaName+"')";
		Utils.logSql(sql);
		status = MainDao.executeSql(sql, true);
		if (status){
			// Now we have to execute functrigger.sql
			try {
				String folderRoot = new File(".").getCanonicalPath()+File.separator;
				String filePath = folderRoot+"sql"+File.separator+MainDao.getWaterSoftware()+"_functrigger.sql";
				String content = Utils.readFile(filePath);
				content = content.replace("SCHEMA_NAME", newSchemaName);
				Utils.logSql(content);
				if (MainDao.executeSql(content, true)){
					controller.selectSourceType();
				}
			} catch (IOException e) {
				Utils.logError(e);
				status = false;
			}	
		}
		
		// Refresh view
    	Utils.setPanelEnabled(parentPanel, true);
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage("Project copied successfuly");
    	}
    	else {
    		MainClass.mdi.showError("Schema could not be copied");
    	}
		
    }

    
}