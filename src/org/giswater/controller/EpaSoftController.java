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

import java.awt.Cursor;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.giswater.dao.PropertiesDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.gui.panel.EpaSoftPanel;
import org.giswater.gui.panel.ProjectPreferencesPanel;
import org.giswater.gui.panel.SectorSelectionPanel;
import org.giswater.task.ExecuteTask;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;


public class EpaSoftController extends AbstractController {

	private EpaSoftPanel view;
	private ProjectPreferencesPanel ppPanel;
	private MainFrame mainFrame;
    private PropertiesMap gswProp;

    
    public EpaSoftController(EpaSoftPanel view, MainFrame mainFrame) {
    	
    	this.view = view;	
    	this.mainFrame = mainFrame;
    	this.ppPanel = mainFrame.ppFrame.getPanel();
        this.gswProp = PropertiesDao.getGswProperties();
	    view.setController(this);        
    	
	}
	
    
    public void checkFileManager(boolean selected) {
    	view.checkFileManager(selected);
    }
	
	public void showSectorSelection() {
		SectorSelectionPanel panel = new SectorSelectionPanel();
        JDialog dialog = Utils.openDialogForm(panel, view, Utils.getBundleString("EpaSoftController.sector_selection"), 190, 260);
        panel.setParent(dialog);
        dialog.setVisible(true);
	}	

    public void chooseFileInp() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("INP extension file", "inp");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_inp"));
        File file = new File(gswProp.get("FILE_INP", usersFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileInp = chooser.getSelectedFile();
            String path = fileInp.getAbsolutePath();
            if (path != null && path.length() >= 4) {  
                String ext = path.substring(path.length() - 4).toLowerCase();
                if (!ext.equals(".inp")) {
                    path += ".inp";
                    fileInp = new File(path);
                }
            }
            view.setFileInp(fileInp.getAbsolutePath());            
        }

    }


    public void chooseFileRpt() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("RPT extension file", "rpt");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(Utils.getBundleString("file_rpt"));
        File file = new File(gswProp.get("FILE_RPT", usersFolder));	
        chooser.setCurrentDirectory(file.getParentFile());
        int returnVal = chooser.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileRpt = chooser.getSelectedFile();
            String path = fileRpt.getAbsolutePath();
            if (path != null && path.length() >= 4) {  
                String ext = path.substring(path.length() - 4).toLowerCase();
                if (!ext.equals(".rpt")) {
                    path += ".rpt";
                    fileRpt = new File(path);
                }
            }
            view.setFileRpt(fileRpt.getAbsolutePath());
        }

    }
    
    
    public File getFileInp() {
    	
        String path = view.getFileInp();
        if (path.equals("")) {
            return null;        	
        }
        if (path.lastIndexOf(".") == -1) {
            path += ".inp";
        }
        File fileInp = new File(path); 
        path = path.replace("\\", "/");
        gswProp.put("FILE_INP", path);
        PropertiesDao.savePropertiesFile();
        return fileInp;    
        
    }

    
    public File getFileRpt() {
    	
        String path = view.getFileRpt();
        if (path.equals("")) {
            return null;        	
        }
        if (path.lastIndexOf(".") == -1) {
            path += ".rpt";
        }
        File fileRpt = new File(path);
        path = path.replace("\\", "/");
        gswProp.put("FILE_RPT", path);
        PropertiesDao.savePropertiesFile();
        return fileRpt;    
        
    }
    
    
    public void execute(Boolean onlyCheck) {
    
    	view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	
		// Execute task
    	ExecuteTask task = new ExecuteTask();
        task.setController(this);
        task.setParentPanel(view);
        task.setProjectPreferencesPanel(ppPanel);
        task.addPropertyChangeListener(this);
        task.setOnlyCheck(onlyCheck);
        task.execute();
        
    	view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));    	
    	
    }
    
    
    public void execute() {
       	execute(false);    
    }
	
		
    public void openProjectPreferences() {
    	
        try {
        	mainFrame.ppFrame.setVisible(true);
        	mainFrame.ppFrame.setMaximum(true);
		} catch (PropertyVetoException e) {
			Utils.logError(e);
		}
        
    }
    
    
    public void exportSelected() {
    	if (!ppPanel.getVersionSoftware().equals("EPASWMM_51006_2D")) {
    		view.setSubcatchmentEnabled(view.isExportSelected());
    	}
    }
    
    
    public void importSelected() {
    }
    
    
	public void closePanel() {
		view.getFrame().setVisible(false);
		openProjectPreferences();
	}
	
	
}