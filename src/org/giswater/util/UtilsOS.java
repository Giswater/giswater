package org.giswater.util;


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
	
}