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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


public class UtilsFTP {

	public FTPClient client;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
	private InputStream inputStream;
    private String ftpVersion;   // FTP last version 
    
    private final String FTP_HOST = "ftp://download.giswater.org";
    private final String FTP_USER = "giswaterdownro";
    private final String FTP_PWD = "9kuKZCEaquwM6X7jAmuaMg==";
    private final String FTP_ROOT_FOLDER = "htdocs";
	private Integer newMinorVersion;
	private String newBuildVersion;
	private String newMinorVersionFolder;
    

	public UtilsFTP () {
		
        URL url = null;
        try {
            url = new URL(FTP_HOST);
        } catch (MalformedURLException e) {
            Utils.logError(e);
        }
        this.host = url != null ? url.getHost() : null;
		this.port = 0;
        this.username = FTP_USER;
        this.password = Encryption.decrypt(FTP_PWD);
		
	}
	
    
    public boolean connect() {
    	
    	boolean ok = false;
        if (client == null) {
            client = new FTPClient();
        }
        
        try {
            // Connecting client
            if (port > 0) {
                client.connect(host, port);
            } else {
                client.connect(host);
            }

            // Check connection
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                disconnect();
            } else {
                setFtpOptions();
                ok = true;
            }
        } catch (SocketException e) {
        	Utils.logError(e);
            disconnect();
        } catch (IOException e) {
        	Utils.logError(e);
            disconnect();
        }
        
        return ok;
        
    }

    
    public void disconnect () {
    	
        if (client != null) {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (IOException e) {
                	Utils.logError(e);
                }
            } else {
            	Utils.logError("client disconnected");
            }
        } 
        
    }

    
    public boolean login () {
    	
        if (isConnected()) {
            try {
                boolean ok = client.login(username, password);
                return ok;
            } catch (Exception e) {
            	Utils.logError(e);
                logout();
                return false;
            }
        }
		throw new IllegalStateException("Client disconnected");
		
    }

    
    public void store(String filename, FileInputStream fis) throws IOException {
    	this.client.storeFile(filename, fis);    	
    }
    
    
    public boolean logout () {
    	
        if (isConnected()) {
            try {
                return this.client.logout();
            } catch (IOException e) {
            	Utils.logError(e);
                return false;
            }
        }
		throw new IllegalStateException("Client disconnected");
		
    }

    
    public FTPFile[] listDirectories () {
    	
        if (isConnected()) {
            try {
                return this.client.listDirectories();
            } catch (IOException e) {
            	Utils.logError(e);
                return null;
            }
        }
		throw new IllegalStateException("Client disconnected");
		
    }

    
    public FTPFile[] listFiles() {
    	
        if (isConnected()) {
            try {
                return this.client.listFiles();
            } catch (IOException e) {
            	Utils.logError(e);
                return null;
            }
        }
		throw new IllegalStateException("Client disconnected");
		
    }
    
    
    public boolean isConnected () {
        if (client != null) {
            return client.isConnected();
        }
		return false;
    }
    

    private void setFtpOptions () {
        if (isConnected()) {
            client.enterLocalPassiveMode();
        }
    }

    
    public long getFileSize(String filePath) {
    	
    	long size = -1;
        try {
        	changeDirectory();
        	FTPFile[] ftpFile = client.listFiles(filePath);
	        if (ftpFile.length > 0){
	        	size = ftpFile[0].getSize();
	        }
	        else{
	        	Utils.showError(Utils.getBundleString("UtilsFTP.download_aborted")+filePath);
	        }
        } catch (IOException e) {
        	Utils.logError("Could not determine size of the file: " + e.getMessage());
        }
        return size;
        
    }
    
     
	public String getFtpVersion() {
		return ftpVersion;
	}
	
	
	public boolean prepareConnection() {
		
		if (!isConnected()) {
	        if (!connect()) {
	        	Utils.logError("FTP host not valid. Check FTP parameters defined in config.properties file");
	        	return false;
	        }
	        if (!login()) {
	        	Utils.logError("FTP user or password not valid. Check FTP parameters defined in config.properties file");
	        	return false;
	        }
		}
		return true;
		
	}
	
	
	public void changeDirectory() {
		
    	try {
			client.changeWorkingDirectory(FTP_ROOT_FOLDER);
			client.changeWorkingDirectory(newMinorVersionFolder);
			client.changeWorkingDirectory(newBuildVersion);
		} catch (IOException e) {
			Utils.logError(e);
		}
    	
	}
	

	public boolean checkVersion(Integer majorVersion, Integer minorVersion, Integer buildVersion) {
		
		if (!prepareConnection()){
			return false;
		}
		
		Boolean updateMinorVersion = false;
        Boolean updateBuildVersion = false;
		try {
			// Get last minor version available in root folder
	        client.changeWorkingDirectory(FTP_ROOT_FOLDER);
	        String currentMinorVersionFolder = "versions_"+majorVersion+"."+minorVersion;
	        updateMinorVersion = checkMinorVersion(currentMinorVersionFolder);
	        if (!updateMinorVersion) {
	        	newMinorVersionFolder = "versions_"+majorVersion+"."+minorVersion;
	        	newMinorVersion = minorVersion;
	        }
	        // Get last build version available of the last minor version folders
        	client.changeWorkingDirectory(newMinorVersionFolder);
        	updateBuildVersion = checkBuildVersion(majorVersion, minorVersion, buildVersion);
		} catch (NumberFormatException | IOException e) {
			Utils.logError(e);
		}

        logout();
        disconnect();
        
        Utils.getLogger().info("New minor version available: "+updateMinorVersion);
        Utils.getLogger().info("New build version available: "+updateBuildVersion);
        return updateBuildVersion;
		
	}

	
	private boolean checkMinorVersion(String currentMinorVersionFolder) {
		
        boolean updateMinorVersion = false;
        FTPFile[] listFolders = listDirectories();
        FTPFile folder = listFolders[listFolders.length-1];
        newMinorVersionFolder = folder.getName().trim().toLowerCase();
        newMinorVersion = Integer.parseInt(newMinorVersionFolder.substring(newMinorVersionFolder.length() - 1));
        Utils.getLogger().info("FTP last minor version folder name: "+newMinorVersionFolder);
        if (!currentMinorVersionFolder.equals(newMinorVersionFolder)) {
        	updateMinorVersion = true;
        }
        return updateMinorVersion;
		
	}
	

	private boolean checkBuildVersion(Integer majorVersion, Integer minorVersion, Integer buildVersion) {
		
        FTPFile[] listFolders = listDirectories();
        if (listFolders.length == 0) return false;
        
        FTPFile folder = listFolders[listFolders.length-1];
        newBuildVersion = folder.getName();
        ftpVersion = majorVersion+"."+newMinorVersion+"."+newBuildVersion;
        Utils.getLogger().info("FTP last version code: "+ftpVersion);
        Integer version = Integer.parseInt(folder.getName());
        if (newMinorVersion > minorVersion) {
        	return true;
        }
        else if (newMinorVersion == minorVersion && version > buildVersion) {
        	return true;
        }
        return false;
		
	}


	public boolean downloadLastVersion(String remoteName, String localPath) {
		
		if (!prepareConnection()) {
			return false;
		}

		try {
			changeDirectory();
	        FTPFile[] ftpFile = client.listFiles(remoteName);
	        if (ftpFile.length > 0) {
	            FileOutputStream fos = new FileOutputStream(localPath);		
				client.retrieveFile(remoteName, fos);
	        }
	        else{
	        	Utils.showError(Utils.getBundleString("UtilsFTP.download_aborted")+remoteName);
	        	return false;
	        }
		} catch (IOException e) {
			Utils.logError(e);
		}

        logout();
        disconnect();
        
        return true;
		
	}
	
	
    public boolean downloadFile(String downloadPath) {
    	
    	boolean status = false;
        try {
     		if (!prepareConnection()) {
    			return false;
    		}
            boolean success = client.setFileType(FTP.BINARY_FILE_TYPE);
            if (!success) {
            	Utils.showError(Utils.getBundleString("UtilsFTP.binary_file"));
            }
            inputStream = client.retrieveFileStream(downloadPath);
        } catch (IOException e) {
        	Utils.showError(Utils.getBundleString("UtilsFTP.error_downloading") + e.getMessage());
        }
        
        status = true;
        return status;
        
    }
    
    
    public void finish() throws IOException {
        inputStream.close();
        client.completePendingCommand();
    }
    
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    
    // Checks for connection to the internet through dummy request
    public static boolean isInternetReachable() {
    	
        try {
        	// Trying to retrieve data from the source. If there is no connection, this line will fail
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.getContent();
        } catch (UnknownHostException e) {
            return false;
        }
        catch (IOException e) {
            return false;
        }
        return true;
        
    }
        
        
}