package org.giswater.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import org.giswater.dao.MainDao;
import org.giswater.gui.MainClass;
import org.giswater.util.Utils;


public abstract class AbstractController implements PropertyChangeListener {
		
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

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            MainClass.mdi.setProgressBarValue(progress);
        }
    }
    
	
}