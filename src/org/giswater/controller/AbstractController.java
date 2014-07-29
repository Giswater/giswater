package org.giswater.controller;

import java.lang.reflect.Method;

import org.giswater.dao.MainDao;
import org.giswater.util.Utils;


public abstract class AbstractController {
		
	protected String usersFolder;
	
	
    public AbstractController() {
		usersFolder = MainDao.getRootFolder(); 	
	}
	
	public void action(String actionCommand) {
		
		try {
			Method method = this.getClass().getMethod(actionCommand);
			if (!actionCommand.equals("schemaChanged")){
				Utils.getLogger().info(method.toString());
			}
			method.invoke(this);		
		} catch (Exception e) {
			if (Utils.getLogger() != null){			
				Utils.logError(e);
			} else{
				Utils.showError(e);
			}
		}
		
	}	

	
}