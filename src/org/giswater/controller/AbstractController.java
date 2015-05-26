/*
 * This file is part of Giswater
 * Copyright (C) 2013 Tecnics Associats
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author:
 *   David Erill <derill@giswater.org>
 */
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
		usersFolder = MainDao.getGiswaterUsersFolder(); 	
	}
	
	public void action(String actionCommand) {
		
		try {
			Method method = this.getClass().getMethod(actionCommand);
			if (!actionCommand.equals("schemaChanged")) {
				Utils.getLogger().info(method.toString());
			}
			method.invoke(this);		
		} catch (Exception e) {
			if (Utils.getLogger() != null) {			
				Utils.logError(e);
			} else {
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