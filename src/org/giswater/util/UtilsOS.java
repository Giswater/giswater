package org.giswater.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public final class UtilsOS {

	private static String osName = null;
	
	
	public static String getOsName() {
		if (osName == null) { 
			osName = System.getProperty("os.name"); 
		}
		return osName;
	}
	
	public static boolean isWindows() {
		return getOsName().startsWith("Windows");
	}

	public static boolean isUnix() {
		return false;
	} 
	
	public static String getExecutionPath() {
		
		String path = "";
		
		boolean isEclipse = runningInEclipse();
		if (isEclipse) {
			try {
				path = new File(".").getCanonicalPath();
			} catch (IOException e) {
				System.out.println("IOException");
			}
		}
		else {
			path = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			path = path.substring(0, path.lastIndexOf("/"));
			path = path.replaceAll("%20", " ");		
		}
		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException");
		}
		
		return path;
		
	}
	
	public static boolean runningInEclipse() {
		
	    boolean isEclipse = true;
	    if (System.getenv("eclipse") == null) {
	        isEclipse = false;
	    }
	    return isEclipse;
	    
	}
	
	
}