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
package org.giswater.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.giswater.util.Utils;


public class HecRasDao extends MainDao {
	
	
	public static boolean createSchemaHecRas(String softwareName, String schemaName, String srid) {
		
		boolean status = false;
		String filePath = "";
		try {		
			filePath = Utils.getAppPath()+"sql"+File.separator+softwareName+".sql";
			String content = Utils.readFile(filePath);
			if (content.equals("")) return false;
	    	// Replace SCHEMA_NAME for schemaName parameter. __USER__ for user
			content = content.replace("SCHEMA_NAME", schemaName);
			content = content.replace("SRID_VALUE", srid);			
			content = content.replace("__USER__", PropertiesDao.getGswProperties().get("POSTGIS_USER"));		
			Utils.logSql(content);
			status = executeUpdateSql(content, false, true);
        } catch (FileNotFoundException e) {
            Utils.showError("inp_error_notfound", filePath);
        } catch (IOException e) {
            Utils.showError(e, filePath);
		}
		return status;
		
	}
	
	
	public static Integer createSdfFile(String schemaName, String fileName, 
		boolean MA, boolean IA, boolean Levees, boolean BO, boolean Manning) {
		
		String sql = "SELECT "+schemaName+".gr_export_geo('"+fileName+"', "+MA+", "+IA+", "+Levees+", "+BO+", "+Manning+");";
		Utils.logSql(sql);
		Integer value = Integer.parseInt(stringQuery(sql));
        return value;	
        
	}
	
	
	public static boolean clearData(String schemaName) {
		String sql = "SELECT "+schemaName+".gr_clear();";
		Utils.logSql(sql);
		return executeSql(sql, true);	
	}


	public static boolean loadRaster(String schemaName, String rasterPath, String rasterName) {

		String srid = MainDao.getTableSrid(schemaName, "mdt").toString();
		String logFolder = Utils.getLogFolder();
		String fileSql = logFolder + rasterName.replace(".asc", ".sql");
		
		// Check if mdt table already exists
		if (MainDao.checkTableHasData(schemaName, "mdt")) {
			String msg = "MDT table already loaded. Do you want to overwrite it?";
			int res = Utils.confirmDialog(msg);
			if (res != 0) {
				return false;
			}			
		}
		
		// Set content of .bat file
		String aux = "\""+bin+"raster2pgsql\" -d -s "+srid+" -I -C -M \""+rasterPath+"\" -F -t 100x100 "+schemaName+".mdt > \""+fileSql+"\"";
		aux+= "\n";
		aux+= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+" -c \"drop table if exists "+schemaName+".mdt\";";
		aux+= "\n";		
		aux+= "\""+bin+"psql\" -U "+user+" -h "+host+" -p "+port+" -d "+db+" -f \""+fileSql+"\" > \""+logFolder+"raster2pgsql.log\"";
		aux+= "\ndel " + fileSql;
		aux+= "\nexit";				
		Utils.getLogger().info(aux);

        // Fill and execute .bat File	
		File batFile = new File(logFolder + "raster2pgsql.bat");        
		Utils.fillFile(batFile, aux);    		
		Utils.openFile(batFile.getAbsolutePath());
		
		return true;
			
	}	
	
	
}