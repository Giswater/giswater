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
package org.giswater.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
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
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.Jr;
import javax.swing.JTextArea;


public class Utils {

    private static final String LOG_FOLDER = "giswater" + File.separator + "log" + File.separator;
    private static final String GIS_FOLDER = "gis" + File.separator;
    private static final String CONFIG_FOLDER = "config" + File.separator;
    private static final String ICON_PATH = "images" + File.separator + "imago.png";
    private static final int NUM_LEVELS = 10;

	private static Logger logger;
    private static Logger loggerSql;
    private static String configFolder;
	private static String logFolder;
	private static String gisFolder;
	private static String appPath;
	private static boolean isQgis;			
	private static ResourceBundle bundleText;
    
    
	public static Logger getLogger() {

    	if (logger == null) {
    		
			try {
				
				// Check Operating System
				boolean isWindows = UtilsOS.isWindows();
				
				// Look&Feel
				String className = UIManager.getSystemLookAndFeelClassName();
				if (!isWindows) {
					className = UIManager.getCrossPlatformLookAndFeelClassName();	
					//className = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";		    		
				}
				try {
					UIManager.setLookAndFeel(className);
				} catch (Exception e) {
					Utils.logError(e.getMessage());
				}  		    	
				
				// Get gis and config folders
				String folderRoot = getAppPath();         	
				gisFolder = folderRoot + GIS_FOLDER;
				configFolder = folderRoot + CONFIG_FOLDER;
				
				// Log folder and files
				logFolder = System.getProperty("user.home") + File.separator + LOG_FOLDER;
				File folderFile = new File(logFolder);
				folderFile.mkdirs();
				if (!folderFile.exists()) {
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
				
				// Check QGIS file association
				isQgis = true;
				if (isWindows) {
					isQgis = checkFileExtensionQgis();
				}
			    
			} catch (IOException e) {
			    JOptionPane.showMessageDialog(null, e.getMessage(), "Log creation: IOException", JOptionPane.ERROR_MESSAGE);
			} catch (SecurityException e) {
			    JOptionPane.showMessageDialog(null, e.getMessage(), "Log creation: SecurityException", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
		return logger;

    }
	   
	
	public static void setLocale(Locale locale) {
		
		Locale.setDefault(locale);	
		try {
			bundleText = ResourceBundle.getBundle("text", locale);		
		}
		catch (Exception e) {
			Utils.logError(e);
		}
		
    	UIManager.put("OptionPane.yesButtonText", getBundleString("yes"));
    	UIManager.put("OptionPane.noButtonText", getBundleString("no"));		
    	UIManager.put("OptionPane.cancelButtonText", getBundleString("cancel"));	
		
    	UIManager.put("FileChooser.openButtonText", getBundleString("open"));	
    	UIManager.put("FileChooser.openButtonToolTipText", getBundleString("open_tooltip"));	
    	UIManager.put("FileChooser.saveButtonText", getBundleString("save"));	
    	UIManager.put("FileChooser.saveButtonToolTipText", getBundleString("save_tooltip"));	
    	UIManager.put("FileChooser.cancelButtonText", getBundleString("cancel"));	
    	UIManager.put("FileChooser.cancelButtonToolTipText", getBundleString("cancel_tooltip"));	
    	UIManager.put("FileChooser.lookInLabelText", getBundleString("look_in"));	
    	UIManager.put("FileChooser.saveInLabelText", getBundleString("save_in"));	    	
    	UIManager.put("FileChooser.fileNameLabelText", getBundleString("file_name"));	
    	UIManager.put("FileChooser.filesOfTypeLabelText", getBundleString("file_of_type"));	
    	UIManager.put("FileChooser.folderNameLabelText", getBundleString("folder_name"));	
	
	}	
	
	
	// Returns true if .qgs file extension is associated (works for Windows only)
	public static boolean checkFileExtensionQgis() {
			
		final String regKey = "SOFTWARE\\Classes";
	
		List<String> listValues = null;
		try {
			listValues = WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, regKey);
			if (listValues != null) {
				return listValues.contains(".qgs");
			}
			else {
				return false;
			}
		} catch (IllegalArgumentException e) {
			logError(e);
		} catch (IllegalAccessException e) {
			logError(e);
		} catch (InvocationTargetException e) {
			logError(e);
		}
		return false;
			
	}
	
    
    public static String getAppPath() {
    	
    	if (appPath == null) {
	    	CodeSource codeSource = Utils.class.getProtectionDomain().getCodeSource();
	    	File jarFile;
	    	try {
	    		jarFile = new File(codeSource.getLocation().toURI().getPath());
	    	   	appPath = jarFile.getParentFile().getPath()+File.separator;  
	    	}
	    	catch (URISyntaxException e) {
	    		JOptionPane.showMessageDialog(null, e.getMessage(), "getAppPath Error", JOptionPane.ERROR_MESSAGE);
	    	}
    	}
    	return appPath;
    	
    }
    
    public static String getIconPath() {
    	return getAppPath() + ICON_PATH;
    }    
    
    public static String getLogFolder() {
    	return logFolder;
    }    
    
    public static String getGisFolder() {
    	return gisFolder;
    }       

    public static String getConfigFolder() {
    	return configFolder;
    }   
    
    public static boolean isQgisAssociated() {
    	return isQgis;
    }       

	public static String getBundleString(String key) {
		return getBundleString(bundleText, key);
	}
	
	public static String getBundleString(ResourceBundle bundle, String key) {
		try {
			return bundle.getString(key);
		} catch (Exception e) {
			return key;	
		}
	}
	
	
	public static JDialog openDialogForm(JPanel view, Component parent, String title) {
		return openDialogForm(view, parent, title, -1, -1);
	}
		
	public static JDialog openDialogForm(JPanel view, Component parent, String title, int width, int height) {
		
		JDialog f = new JDialog();
		f.setTitle(title);
		f.setModal(true);
	    f.setLayout(new BorderLayout());
	    f.add(view, BorderLayout.CENTER);
	    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	    
	    f.pack();
	    if (width > -1) {
	    	f.setSize(width, height);
	    }
		ImageIcon image = new ImageIcon("images/imago.png"); 
        f.setIconImage(image.getImage());	    
	    f.setLocationRelativeTo(parent);   	
	    return f;
	    
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

   
    public static void showMessage(String msg) {
    	showMessage(msg, "");
    }
    
    public static void showMessage(String msg, String param) {
    	showMessage(null, msg, "");
    }  
    
    public static void showMessage(Component comp, String msg) {
    	showMessage(comp, msg, "");
    }       
    
    public static void showMessage(Component comp, String msg, String param) {
    	
    	String userMsg = getBundleString(msg);
		if (!param.equals("")) {
			userMsg += "\n" + param;
		}    	
		if (logger != null) {
			String infoMsg = getBundleString(msg);
			if (!param.equals("")) {
				infoMsg += "\nParameter: " + param;
			}			
			logger.info(infoMsg);
		}
		JOptionPane.showMessageDialog(comp, userMsg, getBundleString("inp_descr"), JOptionPane.INFORMATION_MESSAGE);
		
    }      

    
    public static void showError(String msg) {
    	showError(msg, "");
    }
    
    public static void showError(Exception e) {
    	showError(e, "");
    }    
        
    public static void showError(String msg, String param) {
    	showError(null, msg, param);
    }
    
    public static void showError(Component comp, String msg) {
    	showError(null, msg, "");
    }    
    
    public static void showError(Component comp, String msg, String param) {
    	
    	String userMsg = getBundleString(msg);
		if (!param.equals("")) {
			userMsg += "\n" + param;
		}
		logError(msg, param);
		JOptionPane.showMessageDialog(comp, userMsg, getBundleString("inp_descr"), JOptionPane.WARNING_MESSAGE);
		
    }    
    
    public static void showError(Exception e, String param) {
		logError(e, param);
		JOptionPane.showMessageDialog(null, e.getMessage(), getBundleString("inp_descr"), JOptionPane.WARNING_MESSAGE);		
    }     
    
    public static void showSQLError(Exception e, String context) {
    	logError(e);
		String msg = "";
		final int MAX_SIZE = 500;
		if (context != "") {
			msg = "Error found at '"+context+"':\n";
		}
		int size = e.getMessage().length();
		if (size > MAX_SIZE) size = MAX_SIZE;
		msg+= e.getMessage().substring(0, size);    	
    	JOptionPane.showMessageDialog(null, msg, getBundleString("inp_descr"), JOptionPane.WARNING_MESSAGE);		
    }     
    
    
    public static void logError(String msg) {
    	logError(msg, "");
    }     

    public static void logError(Exception e) {
    	logError(e, "");
    }    
    
    public static void logError(String msg, String param) {
    	if (logger != null) {
    		String errorMsg = getBundleString(msg);
    		if (!param.equals("")) {
    			errorMsg += "\nParameter: " + param;
    		}    	
    		logger.warning(errorMsg);
    	}
    } 
    
    public static void logError(Exception e, String param) {
        
    	if (logger != null) {
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
	        if (param != "") {
	        	shortStack+= "Parameter: " + param;
	        }
	        logger.warning(shortStack);
    	}
        
    }         
    
    public static void logInfo(String msg) {
    	logInfo(msg, "");
    }  
    
    public static void logInfo(String msg, String param) {
    	if (logger != null) {
    		String infoMsg = getBundleString(msg);
    		if (!param.equals("")) {
    			infoMsg += "\nParameter: " + param;
    		}    	
    		logger.info(infoMsg);
    	}
    }     
    
    public static int showYesNoDialog(String msg) {
    	return showYesNoDialog(msg, getBundleString("inp_descr"));
    }  
    
    
    public static int showYesNoDialog(String msg, String title) {
    	return JOptionPane.showConfirmDialog(null, getBundleString(msg), getBundleString(title), JOptionPane.YES_NO_OPTION);	
    }          

    
    public static int showYesNoDialog(Component comp, String msg) {
    	return showYesNoDialog(comp, msg, getBundleString("inp_descr"));	
    }    
    
    
    public static int showYesNoDialog(Component comp, String msg, String title) {
    	return JOptionPane.showConfirmDialog(comp, getBundleString(msg), getBundleString(title), JOptionPane.YES_NO_OPTION); 	
    }    
    
    public static int showYesNoCancelDialog(Component comp, String msg) {
    	return JOptionPane.showConfirmDialog(comp, getBundleString(msg), getBundleString("inp_descr"), JOptionPane.YES_NO_CANCEL_OPTION);  	
    }       
    
    public static String showInputDialog(Component comp, String msg) {
    	return JOptionPane.showInputDialog(comp, getBundleString(msg));  	
    }       
	
    
	public static boolean execProcess(String process) {
		
		try {   
			String command = "cmd /C start /wait " + process;
			Process p = Runtime.getRuntime().exec(command);				
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			logError(e);
			return false;
		}	
		return true;

	}
	
	
	public static boolean openFile(String filePath) {

		File exec = new File(filePath);
		if (exec.exists()) {
			try {
				Desktop.getDesktop().open(exec);
			} catch (IOException e) {
				logError(e);
				return false;
			}
		}
		else{
			showMessage("file_not_found", filePath);
			return false;
		}
		return true;
		
	}    
	
	
    public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
    	
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } 
        else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
        
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
    
	
	public static void deleteFile(String filePath) {
		
		File f = new File(filePath);
	
	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists()) {
	    	logError("Delete: no such file or directory: " + filePath);
	    }
	    if (!f.canWrite()) {
	    	logError("Delete: write protected: " + filePath);	    	
	    }
	
	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	    	String[] files = f.list();
	    	if (files.length > 0) {
	    		logError("Delete: directory not empty: " + filePath);
	    	}
	    }
	
	    // Attempt to delete it
	    boolean success = f.delete();
	    if (!success) {
	    	logError("Delete: deletion failed");
	    }
	
	}
	
	
	public static boolean fillFile(File file, String text) {

		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.setLength(0);
			raf.writeBytes(text);
			raf.close();
			return true;
		} catch (Exception e) {
			logError(e.getMessage());
			return false;
		}

	}		
	
	
	public static String readFile(String filePath) throws IOException {
		return readFile(filePath, true);
	}
	
	public static String readFile(String filePath, boolean showError) throws IOException {
		
		File fileName = new File(filePath);			
		if (!fileName.exists()) {
			if (showError) {
				showError("inp_error_notfound", filePath);
			}
			else {
				logError("inp_error_notfound", filePath);
			}
			return "";
		}
		
		String content = "";	
		FileChannel channel = new FileInputStream(fileName).getChannel();
		MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		byte[] bytes;
		if (buffer.hasArray()) {
		    bytes = buffer.array();
		} else {
		    bytes = new byte[buffer.remaining()];
		    buffer.get(bytes);
		}
		content = new String(bytes, "UTF-8");		
		channel.close();		
		
		return content;
		
	}	
	
	
	public static ArrayList<String> fileToArray(String filePath) {
		
		File fileName = new File(filePath);			
		if (!fileName.exists()) {
			showError("inp_error_notfound", filePath);
			return null;
		}
		ArrayList<String> fileContent = new ArrayList<String>();
		String line;
		try {
			RandomAccessFile raf = new RandomAccessFile(filePath, "r");
			while (raf.getFilePointer() < raf.length()) {		
				line = raf.readLine().trim();	
				fileContent.add(line);
			}
			raf.close();
		} catch (IOException e) {
			Utils.showError(e);
		}	
		return fileContent;
		
	}

	
	public static void openWeb(String url) {

		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			logError(e);
		}
		
	} 	
	
	
	public static Integer parseInt(String s) {
		Integer value = -1;
		if (isInteger(s)) {
			value = Integer.parseInt(s);
		}
		return value;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}


	public static boolean isNumeric(String number) {
		try {
			Double.parseDouble(number);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	
	public static void setPanelEnabled(JPanel panel, Boolean enabled) {
		
	    Component[] comps = panel.getComponents();
	    for (Component comp : comps) {
	        if (comp instanceof JLabel || comp instanceof JRadioButton || comp instanceof JComboBox || comp instanceof JCheckBox || comp instanceof JButton ||
	        	comp instanceof JTextField || comp instanceof JTextArea || comp instanceof JDateChooser){
	            comp.setEnabled(enabled);
	        }
	        else if (comp instanceof JPanel) {
	        	JPanel aux = (JPanel) comp;
	    		Utils.setPanelEnabled(aux, enabled);
	        }	  
	        else if (comp instanceof JScrollPane) {
	        	JScrollPane scrollPane = (JScrollPane) comp; 
	        	JViewport viewport = scrollPane.getViewport();
	    	    Component[] comps2 = viewport.getComponents();
	    	    for (Component comp2 : comps2) {
	    	        if (comp2 instanceof JLabel || comp2 instanceof JComboBox || comp instanceof JCheckBox || comp instanceof JButton ||
	    		        comp2 instanceof JTextField || comp2 instanceof JTextArea || comp2 instanceof JDateChooser){
	    	        	comp.setEnabled(enabled);
	    		    }	    	    	
	    	    }
	        }		        
	    }		
		
	}
	
	
	public static void setPanelFont(JPanel panel, Font fontPanel, Font fontComponents) {
		
	    Component[] comps = panel.getComponents();
	    for (Component comp : comps) {
	        if (comp instanceof JLabel || comp instanceof JComboBox || 
	        	comp instanceof JTextField || comp instanceof JTextArea || comp instanceof JDateChooser){
	            comp.setFont(fontComponents);
	        }
	        else if (comp instanceof JPanel) {
	        	JPanel aux = (JPanel) comp;
	    		TitledBorder tb = (TitledBorder) aux.getBorder();	 
	    		if (tb!=null){
	    			tb.setTitleFont(fontPanel);
	    			Utils.setPanelFont(aux, fontPanel, fontComponents);
	    		}
	        }	  
	        else if (comp instanceof JScrollPane) {
	        	JScrollPane scrollPane = (JScrollPane) comp; 
	        	JViewport viewport = scrollPane.getViewport();
	    	    Component[] comps2 = viewport.getComponents();
	    	    for (Component comp2 : comps2) {
	    	        if (comp2 instanceof JLabel || comp2 instanceof JComboBox || 
	    		        comp2 instanceof JTextField || comp2 instanceof JTextArea || comp2 instanceof JDateChooser){
	    	        	comp2.setFont(fontComponents);
	    		    }	    	    	
	    	    }
	        }		        
	    }		
		
	}
	

	public static boolean renameFile(String pathFrom, String pathTo) {
		
		File fileFrom = new File(pathFrom);			
		File fileTo = new File(pathTo);		
		if (!fileFrom.exists()) {
			Utils.logError("File not found: "+fileFrom);
			return false;
		}		
		if (fileTo.exists()) {
			fileTo.delete();
		}
		boolean result = fileFrom.renameTo(fileTo);

		return result;
		
	}	

	
}
