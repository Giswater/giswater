package org.giswater.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.swing.SwingWorker;

import org.giswater.gui.panel.DownloadPanel;


public class DownloadTask extends SwingWorker<Void, Void> {

	private static final int BUFFER_SIZE = 4096;
	private String downloadPath;
	private String localePath;
	private DownloadPanel gui;
	private UtilsFTP ftp;
	private Boolean isCancelled = false;
	
	
	public DownloadTask(String downloadPath, String localePath, DownloadPanel gui, UtilsFTP ftp) {
		this.downloadPath = downloadPath;
		this.localePath = localePath;
		this.gui = gui;
		this.ftp = ftp;
	}

	
	/**
	 * Executed in background thread
	 */
	 @Override
	 protected Void doInBackground() throws Exception {
		  
		 try {
			 
			 if (!ftp.prepareConnection()){
				 return null;
			 }

			 byte[] buffer = new byte[BUFFER_SIZE];
			 int bytesRead = -1;
			 long totalBytesRead = 0;
			 int percentCompleted = 0;

			 long fileSize = ftp.getFileSize(downloadPath);
			 gui.setFileSize(fileSize);

			 File localeFile = new File(localePath);
			 FileOutputStream outputStream = new FileOutputStream(localeFile);

			 ftp.downloadFile(downloadPath);
			 InputStream inputStream = ftp.getInputStream();

			 while ((bytesRead = inputStream.read(buffer)) != -1 && !isCancelled) {
				 outputStream.write(buffer, 0, bytesRead);
				 totalBytesRead += bytesRead;
				 percentCompleted = (int) (totalBytesRead * 100 / fileSize);
				 gui.setBytesRead(totalBytesRead);
				 setProgress(percentCompleted);
			 }

			 outputStream.close();
			 ftp.finish();
			 
		 } catch (Exception ex) {
			 Utils.showMessage("Error downloading file: "+ex.getMessage());
			 setProgress(0);
			 cancel(true);
		 } finally {
			 ftp.disconnect();
		 }

		 return null;
	 }

	 
	 /**
	  * Executed in Swing's event dispatching thread
	  */
	 @Override
	 protected void done() {
		 
		 if (!isCancelled()) {
			 gui.close();
			 Utils.showMessage("Downloaded completed");
		 } 
		 else{
			 gui.close();
			 isCancelled = true;
			 Utils.showMessage("Downloaded aborted");
		 }
		 
	 }


}