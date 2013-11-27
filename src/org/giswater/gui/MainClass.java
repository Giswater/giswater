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
 *   David Erill <daviderill79@gmail.com>
 */
package org.giswater.gui;

import java.util.Locale;

import javax.swing.UIManager;

import org.giswater.controller.MenuController;
import org.giswater.dao.MainDao;
import org.giswater.gui.frame.MainFrame;
import org.giswater.util.Utils;


public class MainClass {

    public static MainFrame mdi;

    
    public static void main(String[] args) {
    	
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	
            	// English language
            	Locale.setDefault(Locale.ENGLISH);

            	// Look&Feel
            	String className = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
            	try {
        			UIManager.setLookAndFeel(className);
        		} catch (Exception e) {
        			Utils.getLogger().warning(e.getMessage());
        		}  

            	// Initial configuration
            	if (!MainDao.configIni()){
            		return;
            	}            	
            	
            	// Create MainFrame and Menu controller
            	mdi = new MainFrame(MainDao.isConnected());
                new MenuController(mdi);            	
                mdi.setVisible(true);
                
            }
        });

    }
    
    
}