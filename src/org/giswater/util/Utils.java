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
import java.io.RandomAccessFile;
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
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.giswater.dao.MainDao;


public class Utils {

	private static final ResourceBundle BUNDLE_TEXT = ResourceBundle.getBundle("text"); //$NON-NLS-1$
    private static final String LOG_FOLDER = "log/";
    private static final String ICON_PATH = "images\\imago.png";
    private static final int NUM_LEVELS = 4;

    private static int stackTraceLevel = 3;
	private static Logger logger;
    private static Logger loggerSql;
	private static String logFolder;
	private static boolean isSqlLogged;
    
    
	public static Logger getLogger() {

    	if (logger == null) {
            try {
            	String folderRoot = Utils.getAppPath();         	
                logFolder = folderRoot + LOG_FOLDER;
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
    		e.printStackTrace();
    	}
    	return appPath;
    	
    }
    
    public static String getIconPath(){
    	return getAppPath() + ICON_PATH;
    }    
    
    public static String getLogFolder(){
    	return logFolder;
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
        	getLogger().info("File from: " + srFile + "\nFile to:   " + dtFile);
            ok = true;
            
        } catch (FileNotFoundException e) {
        	getLogger().warning(e.getMessage() + " in the specified directory.");
        } catch (IOException e) {
        	getLogger().warning(e.getMessage());
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
    
    
    public static void showError(String msg, String param) {
    	
    	String userMsg = getBundleString(msg);
		if (!param.equals("")){
			userMsg += "\n" + param;
		}
		JOptionPane.showMessageDialog(null, userMsg, getBundleString("inp_descr"), JOptionPane.WARNING_MESSAGE);
		if (logger != null) {
			String errorMsg = getBundleString(msg);
			if (!param.equals("")){
				errorMsg += "\nParameter: " + param;
			}
			logger.warning(errorMsg);			
		}
		
    }
    
    
    public static void showError(Exception e) {
    	stackTraceLevel = 4;
    	showError(e, "");
    	stackTraceLevel = 3;
    }    
    
    
    public static void showError(Exception e, String param) {
    	
    	String errorInfo = getErrorInfo(stackTraceLevel);
		JOptionPane.showMessageDialog(null, e.getMessage(), getBundleString("inp_descr"), JOptionPane.WARNING_MESSAGE);
		if (logger != null) {
			String errorMsg = e.toString() + "\n" + errorInfo;
			if (!param.equals("")){
				errorMsg += "\nParameter: " + param;
			}
			logger.warning(errorMsg);				
		}
		
    }     
    
    
    public static void logError(Exception e) {
    	stackTraceLevel = 4;
    	logError(e, "");
    	stackTraceLevel = 3;    	
    }      
    
    
    public static void logError(Exception e, String param) {
    	
    	String errorInfo = getErrorInfo(stackTraceLevel);
		if (logger != null) {
			String errorMsg = e.toString() + "\n" + errorInfo;
			if (!param.equals("")){
				errorMsg += "\nParameter: " + param;
			}
			logger.warning(errorMsg);
		}
		
    }         
    
    
    public static int confirmDialog(String msg) {
    	return confirmDialog(msg, getBundleString("inp_descr"));
    }  
    
    
    public static int confirmDialog(String msg, String title) {
    	int reply = JOptionPane.showConfirmDialog(null, getBundleString(msg), getBundleString(title), JOptionPane.YES_NO_OPTION);
        return reply;    	
    }        

    
    private static String getErrorInfo(int firstLevel){
    	StackTraceElement[] ste = Thread.currentThread().getStackTrace();
    	String aux = "";
    	int lastLevel = (ste.length < firstLevel+NUM_LEVELS) ? ste.length : firstLevel+NUM_LEVELS;
    	for (int i=firstLevel; i<lastLevel; i++) {
			aux += ste[i].toString() + "\n";
		}
    	return aux;
    }
    

    /**
     * Returns the class name of the installed LookAndFeel with a name
     * containing the name snippet or null if none found.
     * 
     * @param nameSnippet a snippet contained in the Laf's name
     * @return the class name if installed, or null
     */
    public static String getLookAndFeelClassName(String nameSnippet) {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo info : plafs) {
            if (info.getName().contains(nameSnippet)) {
                return info.getClassName();
            }
        }
        return null;
    }    

    
	public static void execProcess(String process){
		
		try{    
			Process p = Runtime.getRuntime().exec("cmd /c start " + process);				
			p.waitFor();
		} catch (IOException e) {
			Utils.logError(e);
		} catch (InterruptedException e) {
			Utils.logError(e);
		}	
		
	}
	
		
	public static void openFile(String filePath){

		File exec = new File(filePath);
		if (exec.exists()){
			try {
				Desktop.getDesktop().open(exec);
			} catch (IOException e) {
				Utils.logError(e);
			}
		}
		else{
			Utils.showMessage("file_not_found", filePath);
		}
		
	}    
	
	
	public static void deleteFile(String sFile){
		
		File f = new File(sFile);
	
	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists()){
	    	getLogger().warning("Delete: no such file or directory: " + sFile);
	    }
	    if (!f.canWrite()){
	    	getLogger().warning("Delete: write protected: " + sFile);	    	
	    }
	
	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	    	String[] files = f.list();
	    	if (files.length > 0){
	    		getLogger().warning("Delete: directory not empty: " + sFile);
	    	}
	    }
	
	    // Attempt to delete it
	    boolean success = f.delete();
	    if (!success) {
    		getLogger().warning("Delete: deletion failed");
	    }
	
	}
	
	
	public static void fillFile(File file, String text) {

		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.setLength(0);
			raf.writeBytes(text);
			raf.close();
		} catch (Exception e) {
			getLogger().warning(e.getMessage());
		}

	}		

	
	public static void setSqlLog(String string) {
		Utils.isSqlLogged = Boolean.parseBoolean(string);
	}	
	
	
}