package org.giswater.task;

import javax.swing.SwingWorker;

import org.giswater.controller.NewProjectController;
import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.util.Utils;


public class CreateSchemaTask extends SwingWorker<Void, Void> {
	
	private ProjectPreferencesPanel parentPanel;
	private NewProjectController controller;
	private String software;
	private String schemaName;
	private String sridValue;
	private String title;
	private String author;
	private String date;
	private boolean status;
	
	
	public CreateSchemaTask(String software, String schemaName, String sridValue) {
		this.software = software;
		this.schemaName = schemaName;
		this.sridValue = sridValue;
	}
	
	public void setParentPanel(ProjectPreferencesPanel parentPanel){
		this.parentPanel = parentPanel;
	}
	
	public void setController(NewProjectController controller){
		this.controller = controller;
	}
	
	public void setParams(String title, String author, String date) {
		this.title = title;
		this.author = author;
		this.date = date;	
	}
	
	
    @Override
    public Void doInBackground() { 
		
		setProgress(1);
		
    	// Close view and disable parent view
		controller.closeProject();
    	Utils.setPanelEnabled(parentPanel, false);
    	
		if (software.equals("HECRAS")) {
			status = MainDao.createSchemaHecRas(software, schemaName, sridValue);	
		}
		else {
			status = MainDao.createSchema(software, schemaName, sridValue);	
		}
		if (status) {
			MainDao.setSchema(schemaName);
			if (MainDao.updateSchema()) {
				String sql = "INSERT INTO "+schemaName+".inp_project_id VALUES ('"+title+"', '"+author+"', '"+date+"')";
				Utils.getLogger().info(sql);
				MainDao.executeSql(sql, false);
				sql = "INSERT INTO "+schemaName+".version (giswater, wsoftware, postgres, postgis, date)" +
					" VALUES ('"+MainDao.getGiswaterVersion()+"', '"+software+"', '"+MainDao.getPostgreVersion()+"', '"+MainDao.getPostgisVersion()+"', now())";
				Utils.getLogger().info(sql);
				// Last SQL script. So commit all process
				MainDao.executeSql(sql, true);
			}
			else {
				MainDao.rollbackSchema(schemaName);
				status = false;
			}
		}
		else {
			MainDao.rollbackSchema(schemaName);
			status = false;
		}
		
		// Refresh view
    	Utils.setPanelEnabled(parentPanel, true);
		parentPanel.setSchemaModel(MainDao.getSchemas(software));	
		
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	if (status) {
    		MainClass.mdi.showMessage("schema_creation_completed");
    	}
    	else {
    		MainClass.mdi.showError("Schema could not be created");
    	}
		
    }

    
}