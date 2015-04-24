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
package org.giswater.model;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.giswater.dao.PropertiesDao;
import org.giswater.util.Utils;


public class ExecuteEpa extends Model {

    
    // Execute EPA Software
    public static boolean process(File fileInp, File fileRpt) {

        Utils.getLogger().info("execEPASOFT");
        
		String exeName = PropertiesDao.getGswProperties().get("EXE_NAME");
        File exeFile = new File(exeName);
        exeName = Utils.getAppPath() + "epa" + File.separator + exeName;	
	    exeFile = new File(exeName);			
        
        // Check if file exists
		if (!exeFile.exists()) {         
    		Utils.showError("inp_error_notfound", exeName);
    		return false;
		}

        if (!fileInp.exists()) {
			Utils.showError("inp_error_notfound", fileInp.getAbsolutePath());     
			return false;
        }
        
        // Overwrite RPT file if already exists?
        if (fileRpt.exists()) {
            String owRpt = PropertiesDao.getPropertiesFile().get("OVERWRITE_RPT", "true").toLowerCase();
            if (owRpt.equals("false")) {
                String msg = Utils.getBundleString("ExecuteEpa.file_already_exists")+fileRpt.getAbsolutePath()+Utils.getBundleString("ExecuteEpa.overwrite"); //$NON-NLS-1$ //$NON-NLS-2$
            	int res = Utils.showYesNoDialog(msg);             
            	if (res == JOptionPane.NO_OPTION) return false;
            }  
        }
        
        String sFile = fileRpt.getAbsolutePath().replace(".rpt", ".out");
        File fileOut = new File(sFile);

        // Create command
        String exeCmd = "\"" + exeName + "\"";
        exeCmd += " \"" + fileInp.getAbsolutePath() + "\" \"" + fileRpt.getAbsolutePath() + "\" \"" + fileOut.getAbsolutePath() + "\"";

        // Ending message
        Utils.getLogger().info(exeCmd);            

        // Exec process
		try {
			Process p = Runtime.getRuntime().exec(exeCmd);
	        p.waitFor();			
	        p.destroy();
		} catch (IOException e) {
			Utils.showError("inp_error_io", exeCmd);
			return false;
		} catch (InterruptedException e) {
			Utils.showError("inp_error_io", exeCmd);
			return false;
		}

        // Open RPT file
        String openFile = PropertiesDao.getPropertiesFile().get("OPEN_RPT").toLowerCase();
        if (openFile.equals("always")) {
        	Utils.openFile(fileRpt.getAbsolutePath());
        }
        else if (openFile.equals("ask")) {    
            String msg = Utils.getBundleString("inp_end") + "\n" + fileRpt.getAbsolutePath() + "\n" + Utils.getBundleString("view_file");
        	int res = Utils.showYesNoDialog(msg);             
        	if (res == JOptionPane.YES_OPTION) {
               	Utils.openFile(fileRpt.getAbsolutePath());
            }   
        }                            
        return true;

    }


}