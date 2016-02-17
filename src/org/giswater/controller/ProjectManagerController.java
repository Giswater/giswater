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

import javax.swing.JDialog;

import org.giswater.dao.MainDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.GisPanel;
import org.giswater.gui.panel.ProjectManagerPanel;
import org.giswater.util.Utils;


public class ProjectManagerController extends AbstractController {

	private ProjectManagerPanel view;
	private MainFrame mainFrame;
	
	private static final Integer GIS_DIALOG_WIDTH = 420; 
	private static final Integer GIS_DIALOG_HEIGHT = 245; 	


	/**
	 * @wbp.parser.entryPoint
	 */
	public ProjectManagerController(ProjectManagerPanel pmPanel, MainFrame mf) {
		
		this.view = pmPanel;	
		this.mainFrame = mf;
    	this.usersFolder = MainDao.getGiswaterUsersFolder(); 
	    view.setController(this);    
	    
	}
	

	public void createGisProject() {
		
		GisPanel gisPanel = new GisPanel();
		JDialog gisDialog = 
			Utils.openDialogForm(gisPanel, view, Utils.getBundleString("ProjectPreferencesController.create_gis"), GIS_DIALOG_WIDTH, GIS_DIALOG_HEIGHT); //$NON-NLS-1$
		gisPanel.setParent(gisDialog);
        gisDialog.setVisible(true);
        
	}
	
	
}