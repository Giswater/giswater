package org.giswater.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


public class UtilsFTP {

	public FTPClient client;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private static String ftpVersion;   // FTP last version 
	private static Integer majorVersion;
	private static Integer minorVersion;
	private static String newVersion;
	private static String ftpPwd;
    
    private static final String FTP_HOST = "ftp://download.giswater.org";
    private static final String FTP_USER = "giswaterdownro";
    private static final String FTP_PWD = "9kuKZCEaquwM6X7jAmuaMg==";
    private static final String FTP_ROOT_FOLDER = "htdocs";
    

    /**
     * Instanciate FTP server.
     * 
     * @param host
     *            FTP server address.
     * @param username
     *            FTP username.
     * @param password
     *            FTP password.
     */
    public UtilsFTP (final String address, final String username, final String password) {
    	
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            Utils.logError(e);
        }
        this.host = url != null ? url.getHost() : null;
        this.port = 0;
        this.username = username;
        this.password = password;
        
    }

    
    public boolean connect() {
    	
    	boolean ok = false;
        if (this.client == null) {
            this.client = new FTPClient();
            try {
                // Connecting client
                if (this.port > 0) {
                    this.client.connect(this.host, this.port);
                } else {
                    this.client.connect(this.host);
                }

                // Check connection
                int reply = this.client.getReplyCode();
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
        } 
        else {
        	Utils.logError("client is null");
        }
        
        return ok;
        
    }

    
    public void disconnect () {
    	
        if (this.client != null) {
            if (this.client.isConnected()) {
                try {
                    this.client.disconnect();
                } catch (IOException e) {
                	Utils.logError(e);
                }
            } else {
            	Utils.logError("client disconnected");
            }
        } else {
        	Utils.logError("client is null");
        }
        
    }

    
    public boolean login () {
    	
        if (isConnected()) {
            try {
                boolean ok = this.client.login(this.username, this.password);
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
        if (this.client != null) {
            return this.client.isConnected();
        }
		return false;
    }
    

    private void setFtpOptions () {
        if (isConnected()) {
            this.client.enterLocalPassiveMode();
        }
    }

    
	public static String getFtpVersion() {
		return ftpVersion;
	}
	
	
	public static boolean prepareConnection(UtilsFTP ftp){
		
        if (!ftp.connect()){
        	Utils.logError("FTP host not valid. Check FTP parameters defined in config.properties file");
        	return false;
        }
        if (!ftp.login()){
        	Utils.logError("FTP user or password not valid. Check FTP parameters defined in config.properties file");
        	return false;
        }
		return true;
		
	}
	

	public static boolean checkVersion(Integer majorVersion, Integer minorVersion, Integer buildVersion) {
		
		UtilsFTP.majorVersion = majorVersion;
		UtilsFTP.minorVersion = minorVersion;
		ftpPwd = Encryption.decrypt(FTP_PWD);
        UtilsFTP ftp = new UtilsFTP(FTP_HOST, FTP_USER, ftpPwd);
        if (!ftp.connect()){
        	Utils.logError("FTP host not valid. Check FTP parameters defined in config.properties file");
        	return false;
        }
        if (!ftp.login()){
        	Utils.logError("FTP user or password not valid. Check FTP parameters defined in config.properties file");
        	return false;
        }
        boolean updateVersion = false;
		try {
	        ftp.client.changeWorkingDirectory(FTP_ROOT_FOLDER);
	        ftp.client.changeWorkingDirectory("versions_"+majorVersion+"."+minorVersion);
	        
	        // Get last version available
	        FTPFile[] listFolders = ftp.listDirectories();
	        FTPFile folder = listFolders[listFolders.length-1];
	        UtilsFTP.newVersion = folder.getName();
	        ftpVersion = majorVersion+"."+minorVersion+"."+newVersion;
	        Utils.getLogger().info("FTP last version is: "+ftpVersion);
	        Integer version = Integer.parseInt(folder.getName());
	        if (version > buildVersion){
	        	updateVersion = true;
	        }
		} catch (IOException e) {
			Utils.logError(e);
		}

        ftp.logout();
        ftp.disconnect();
        
        return updateVersion;
		
	}


	public static boolean downloadLastVersion(String remoteName, String localPath) {
		
		UtilsFTP ftp = new UtilsFTP(FTP_HOST, FTP_USER, ftpPwd);
		if (!prepareConnection(ftp)){
			return false;
		}

		try {
	        ftp.client.changeWorkingDirectory(FTP_ROOT_FOLDER);
	        ftp.client.changeWorkingDirectory("versions_"+majorVersion+"."+minorVersion);
	        ftp.client.changeWorkingDirectory(newVersion);
	        FTPFile[] ftpFile = ftp.client.listFiles(remoteName);
	        if (ftpFile.length > 0){
	            FileOutputStream fos = new FileOutputStream(localPath);		
				ftp.client.retrieveFile(remoteName, fos);
	        }
	        else{
	        	Utils.showError("Downloaded aborted. File not found in FTP server:\n"+remoteName);
	        	return false;
	        }
		} catch (IOException e) {
			Utils.logError(e);
		}

        ftp.logout();
        ftp.disconnect();
        
        return true;
		
	}
    
    
}