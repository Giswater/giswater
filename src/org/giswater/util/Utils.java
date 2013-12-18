/*
 * This file is part of Giswater
 * Copyright (C) 2013PrincesaMonoayaM-2009s Associats
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
package org.giswater.util;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.giswater.dao.MainDao;


public class Utils {

	private static final ResourceBundle BUNDLE_TEXT = ResourceBundle.getBundle("text"); //$NON-NLS-1$
    private static final String LOG_FOLDER = "giswater" + File.separator + "log" + File.separator;
    private static final String GIS_FOLDER = "gis" + File.separator;
    private static final String ICON_PATH = "images" + File.separator + "imago.png";
    private static final int NUM_LEVELS = 10;

	private static Logger logger;
    private static Logger loggerSql;
	private static String logFolder;
	private static String gisFolder;
	private static boolean isSqlLogged;	
    
    
	public static Logger getLogger() {

    	if (logger == null) {
            try {
            	String folderRoot = Utils.getAppPath();         	
            	logFolder = System.getProperty("user.home") + File.separator + LOG_FOLDER;
                gisFolder = folderRoot + GIS_FOLDER;
                File folderFile = new File(logFolder);
                folderFile.mkdirs();
                if (!folderFile.exists()){
                    JOptionPane.showMessageDialog(null, "Could not create log folder", "Log creation", JOptionPane.ERROR_MESSAGE);                	
                }
                String logFile = logFolder + "log_"+getCurrentTimeStamp()+".log";
                FileHandler fh = new FileHandler(logFile, true);
                LogFormatter lf = new LogFormatter();
                fh.setFormatter(lf);
                logger = Logger.getLogger(logFile);
                logger.addHandler(fh);
                // SQL logger file
                logFile = logFolder + "sql_"+getCurrentTimeStamp()+".log";
                fh = new FileHandler(logFile, true);
                lf = new LogFormatter();
                fh.setFormatter(lf);
                loggerSql = Logger.getLogger(logFile);
                loggerSql.addHandler(fh);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Log creation: IOException", JOptionPane.ERROR_MESSAGE);
            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Log creation: SecurityException", JOptionPane.ERROR_MESSAGE);
            }
        }
        return logger;

    }
	   
	
	public static void logSql(String msg){
		if (isSqlLogged){
			loggerSql.info(msg);
		}
	}
	
    
    public static String getAppPath(){
    	
    	CodeSource codeSource = MainDao.class.getProtectionDomain().getCodeSource();
    	File jarFile;
    	String appPath = "";
    	try {
    		jarFile = new File(codeSource.getLocation().toURI().getPath());
    	   	appPath = jarFile.getParentFile().getPath() + File.separator;  
    	}
    	catch (URISyntaxException e) {
    		JOptionPane.showMessageDialog(null, e.getMessage(), "getAppPath Error", JOptionPane.ERROR_MESSAGE);
    	}
    	return appPath;
    	
    }
    
    public static String getIconPath(){
    	return getAppPath() + ICON_PATH;
    }    
    
    public static String getLogFolder(){
    	return logFolder;
    }    
    
    public static String getGisFolder(){
    	return gisFolder;
    }       

	public static String getBundleString(String key){
		return getBundleString(BUNDLE_TEXT, key);
	}
	
	public static String getBundleString(ResourceBundle bundle, String key){
		try{
			return bundle.getString(key.toLowerCase());
		} catch (Exception e){
			return key;	
		}
	}
	
		
	public static JDialog openDialogForm(JPanel view, JDialog f, int width, int height){
		f.setModal(true);
	    f.setLayout(new BorderLayout());
	    f.add(view, BorderLayout.CENTER);
	    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	    
	    f.pack();
	    f.setSize(width, height);
	    f.setLocationRelativeTo(null);   	
	    return f;
	}     

	
	public static JDialog openDialogForm(JPanel view, int width, int height){
		JDialog f = new JDialog();
		return openDialogForm(view, f, width, height);
	} 
	
	
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        String date = sdfDate.format(now);
        return date;
    }


    public static String dateToString(Date date) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String parsedDate = sdfDate.format(date);
        return parsedDate;
    }


    public static boolean copyFile(String srFile, String dtFile) {

    	boolean ok = false;
    	
        try {

            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            // For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        	logger.info("File from: " + srFile + "\nFile to:   " + dtFile);
            ok = true;
            
        } catch (FileNotFoundException e) {
        	logError(e.getMessage() + " in the specified directory.");
        } catch (IOException e) {
        	logError(e.getMessage());
        }
        
    	return ok;
    	
    }

    
    public static void showMessage(String msg) {
    	showMessage(msg, "");
    }
    
    public static void showMessage(String msg, String param) {
    	
    	String userMsg = getBundleString(msg);
		if (!param.equals("")){
			userMsg += "\n" + param;
		}    	
		JOptionPane.showMessageDialog(null, userMsg, getBundleString("inp_descr"), JOptionPane.PLAIN_MESSAGE);
		if (logger != null) {
			String infoMsg = getBundleString(msg);
			if (!param.equals("")){
				infoMsg += "\nParameter: " + param;
			}			
			logger.info(infoMsg);
		}
		
    }    

    
    public static void showError(String msg) {
    	showError(msg, "");
    }
    
    public static void showError(Exception e) {
    	showError(e, "");
    }    
        
    public static void showError(String msg, String param) {
    	
    	String userMsg = getBundleString(msg);
		if (!param.equals("")){
			userMsg += "\n" + param;
		}
		JOptionPane.showMessageDialog(null, userMsg, getBundleString("inp_descr"), JOptionPane.WARNING_MESSAGE);
		logError(msg, param);
		
    }
    
    public static void showError(Exception e, String param) {
		JOptionPane.showMessageDialog(null, e.getMessage(), getBundleString("inp_descr"), JOptionPane.WARNING_MESSAGE);
		logError(e, param);
    }     
    
    
    public static void logError(String msg) {
    	logError(msg, "");
    }     

    public static void logError(Exception e) {
    	logError(e, "");
    }    
    
    public static void logError(String msg, String param) {
    	if (logger != null){
    		String errorMsg = getBundleString(msg);
    		if (!param.equals("")){
    			errorMsg += "\nParameter: " + param;
    		}    	
    		logger.warning(errorMsg);
    	}
    } 
    
    public static void logError(Exception e, String param) {
        
    	if (logger != null){
	    	StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw, true);
	        e.printStackTrace(pw);
	        pw.flush();
	        sw.flush();
	        String fullStack = sw.toString();
	        String[] lines = fullStack.split(System.getProperty("line.separator"));
	        String shortStack = "";
	        for (int i = 0; i < NUM_LEVELS; i++) {
	        	shortStack+= lines[i] + "\n";
	        }
	        shortStack+= "Parameter: " + param;
	        logger.warning(shortStack);
    	}
        
    }         
    
    
    public static int confirmDialog(String msg) {
    	return confirmDialog(msg, getBundleString("inp_descr"));
    }  
    
    
    public static int confirmDialog(String msg, String title) {
    	int reply = JOptionPane.showConfirmDialog(null, getBundleString(msg), getBundleString(title), JOptionPane.YES_NO_OPTION);
        return reply;    	
    }          

    
	public static void execProcess(String process){
		
		try{    
			Process p = Runtime.getRuntime().exec("cmd /c start " + process);				
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			logError(e);
		}	
		
	}
	
	
	public static void openFile(String filePath){

		File exec = new File(filePath);
		if (exec.exists()){
			try {
				Desktop.getDesktop().open(exec);
			} catch (IOException e) {
				logError(e);
			}
		}
		else{
			showMessage("file_not_found", filePath);
		}
		
	}    
	
	
	public static void openWeb(String url){

		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			logError(e);
		}
		
	} 	
	
	
	public static void deleteFile(String filePath){
		
		File f = new File(filePath);
	
	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists()){
	    	logError("Delete: no such file or directory: " + filePath);
	    }
	    if (!f.canWrite()){
	    	logError("Delete: write protected: " + filePath);	    	
	    }
	
	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	    	String[] files = f.list();
	    	if (files.length > 0){
	    		logError("Delete: directory not empty: " + filePath);
	    	}
	    }
	
	    // Attempt to delete it
	    boolean success = f.delete();
	    if (!success) {
	    	logError("Delete: deletion failed");
	    }
	
	}
	
	
	public static void fillFile(File file, String text) {

		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.setLength(0);
			raf.writeBytes(text);
			raf.close();
		} catch (Exception e) {
			logError(e.getMessage());
		}

	}		
	
	
	public static String readFile(String filePath) throws IOException{
		
		File fileName = new File(filePath);			
		if (!fileName.exists()){
			Utils.showError("inp_error_notfound", filePath);
			return "";
		}
		RandomAccessFile rat = new RandomAccessFile(fileName, "r");
		String line;
		String content = "";
		while ((line = rat.readLine()) != null){
			content += line + "\n";
		}
		rat.close();		
		
		return content;
		
	}	

	
	public static void setSqlLog(String string) {
		Utils.isSqlLogged = Boolean.parseBoolean(string);
	}	
	
	
}