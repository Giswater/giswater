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
package org.giswater.task;

import java.io.File;

import javax.swing.SwingWorker;

import org.giswater.gui.MainClass;
import org.giswater.gui.frame.MainFrame;
import org.giswater.util.Utils;


public class BackupProjectTask extends SwingWorker<Void, Void> {
	
	private MainFrame parentPanel;
	private File batFile;
	private boolean status;
	
	
	public BackupProjectTask(File batFile) {
		this.batFile = batFile;
	}
	
	
	public void setParentPanel(MainFrame parentPanel){
		this.parentPanel = parentPanel;
	}
	
	
    @Override
    public Void doInBackground() { 
		
		MainClass.mdi.showMessage("BackupProjectTask.process", true);    	
    	parentPanel.setProgressBarValue(1);
		if (!Utils.execProcess(batFile.getAbsolutePath())) {
			return null;
		}
    	status = true;
    	
		return null;
    	
    }

    
    public void done() {
    	
    	MainClass.mdi.setProgressBarEnd();
    	batFile.delete();    	
    	if (status) {   		
    		MainClass.mdi.showMessage("BackupProjectTask.completed");
    	}
    	else {
    		MainClass.mdi.showError("BackupProjectTask.error");
    	}
		
    }

    
}