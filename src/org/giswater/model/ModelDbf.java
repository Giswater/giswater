/*
 * This file is part of gisWater
 * Copyright (C) 2012  Tecnics Associats
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
package org.giswater.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;
import org.giswater.dao.MainDao;
import org.giswater.util.Utils;


public class ModelDbf extends Model{

	private static Map<Integer, File> dbfFiles;

	
	// Read content of the DBF file and saved it in an Array
	@SuppressWarnings("resource")
	public static ArrayList<LinkedHashMap<String, String>> readDBF(File file) {

		FileChannel in;
		Row row;
		ArrayList<LinkedHashMap<String, String>> mAux = null;
		LinkedHashMap<String, String> mDades;
		try {
			mAux = new ArrayList<LinkedHashMap<String, String>>();
			in = new FileInputStream(file).getChannel();
			DbaseFileReader r = new DbaseFileReader(in);
			int fields = r.getHeader().getNumFields();
			while (r.hasNext()) {
				mDades = new LinkedHashMap<String, String>();
				row = r.readRow();
				for (int i = 0; i < fields; i++) {
					String field = r.getHeader().getFieldName(i).toLowerCase();
					Object oAux = row.read(i);
					String value = oAux.toString();
					mDades.put(field, value);
				}
				mAux.add(mDades);
			}
			r.close();
		} catch (FileNotFoundException e) {
			return mAux;
		} catch (IOException e) {
			return mAux;
		} catch (Exception e){
			Utils.getLogger().warning(e.getMessage());
		}

		return mAux;

	}


	// Main procedure
	public static boolean processAll(File fileInp) {

        // Get log file
        logger = Utils.getLogger();
        logger.info("exportINP(dbf)");

		iniProperties = MainDao.getPropertiesFile();         
    	String sql = "";

		try {
	    	
            // Get default INP output file
            if (fileInp == null) {
                String sFile = iniProperties.get("DEFAULT_INP");
                sFile = MainDao.folderConfig + sFile;
                fileInp = new File(sFile);
            }
			
            // Get INP template File
            String templatePath = MainDao.folderConfig + softwareVersion + ".inp";
            File fileTemplate = new File(templatePath);
            if (!fileTemplate.exists()){
            	Utils.showMessage("inp_error_notfound", fileTemplate.getAbsolutePath(), "inp_descr");
            	return false;
            }			
			
			// Open template and output file
			rat = new RandomAccessFile(fileTemplate, "r");
			raf = new RandomAccessFile(fileInp, "rw");
			raf.setLength(0);

			// Get content of target table	
			sql = "SELECT id, name, table_id, lines FROM inp_target";	
			Statement stat = connectionDrivers.createStatement();
			ResultSet rs = stat.executeQuery(sql);					
			while (rs.next()) {
            	logger.info("INP target: " + rs.getInt("table_id") + " - " + rs.getString("name") + " - " + rs.getInt("lines"));		
				processTarget(rs.getInt("id"), rs.getInt("table_id"), rs.getInt("lines"));	
			}		    
			rs.close();
			rat.close();
			raf.close();

            // Ending message
            String msg = Utils.getBundleText().getString("inp_end") + "\n" + fileInp.getAbsolutePath() + "\n" + 
            	Utils.getBundleText().getString("view_file");
            int answer = JOptionPane.showConfirmDialog(null, msg, Utils.getBundleText().getString("inp_descr"), JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION){
            	Utils.openFile(fileInp.getAbsolutePath());
            }               
            return true;
			
		} catch (IOException e) {
			Utils.showError("inp_error_io", e.getMessage(), "inp_descr");
			return false;
		} catch (SQLException e) {
			Utils.showError("inp_error_execution", e.getMessage(), "inp_descr");
			return false;
		}

	}


	// Process target specified by id parameter
	private static void processTarget(int id, int fileIndex, int lines) throws IOException, SQLException {

		// Go to the first line of the target
		for (int i = 1; i <= lines; i++) {
			String line = rat.readLine();
			raf.writeBytes(line + "\r\n");
		}

		// If file is null or out of bounds or not exists then exit function
		if (fileIndex < 0){
			return;
		}
		File file = dbfFiles.get(fileIndex);		
		if (file == null || !file.exists()){
			return;
		}

		// Get data of the specified DBF file
		try{
			lMapDades = readDBF(file);
		}
		catch (Exception e){
			Utils.getLogger().warning(e.getMessage());
		}
		if (lMapDades.isEmpty()) return;		

		// Get DBF fields to write into this target
		mHeader = new LinkedHashMap<String, Integer>();		
		String sql = "SELECT name, space FROM inp_target_fields WHERE target_id = " + id + " ORDER BY pos" ;
		Statement stat = connectionDrivers.createStatement();
		ResultSet rs = stat.executeQuery(sql);			 		
		while (rs.next()) {
			mHeader.put(rs.getString("name").trim().toLowerCase(), rs.getInt("space"));
		}
		rs.close();

		ListIterator<LinkedHashMap<String, String>> it = lMapDades.listIterator();
		Map<String, String> m;   // Current DBF row data
		String sValor = null;
		int size = 0;
		// Iterate over DBF content
		while (it.hasNext()) {
			m = it.next();
			Set<String> set = mHeader.keySet();
			Iterator<String> itKey = set.iterator();
			// Iterate over fields specified in table target_fields
			while (itKey.hasNext()) {
				String sKey = (String) itKey.next();
				sKey = sKey.toLowerCase();
				size = mHeader.get(sKey);
				// Write to the output file if the field exists in DBF file
				if (m.containsKey(sKey)) {
					sValor = (String) m.get(sKey);
					raf.writeBytes(sValor);
					// Complete spaces with empty values
					for (int j = sValor.length(); j <= size; j++) {
						raf.writeBytes(" ");
					}
				}
				// If key doesn't exist write empty spaces
				else{
					for (int j = 0; j <= size; j++) {
						raf.writeBytes(" ");
					}					
				}
			}
            raf.writeBytes("\r\n");			
		}

	}


	// Check all the necessary files to run the process
	public static boolean checkFiles(String sDirShp) {

        dbfFiles = new HashMap<Integer, File>();
        
		String sql = "SELECT id, dbf_table FROM inp_table WHERE id > -1 ORDER BY id";
		try {
			Statement stat = connectionDrivers.createStatement();
			ResultSet rs = stat.executeQuery(sql);		
			while (rs.next()) {
				String sDBF = sDirShp + File.separator + rs.getString("dbf_table").trim() + ".dbf";
				dbfFiles.put(rs.getInt("id"), new File(sDBF));
			}
			rs.close();
		} catch (SQLException e) {
			Utils.showError("inp_error_execution", e.getMessage(), "inp_descr");				
			return false;	
		}				

		return true;

	}

	
}