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
package org.giswater.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.swing.SwingWorker;

import org.giswater.gui.panel.DownloadPanel;
import org.giswater.util.Utils;
import org.giswater.util.UtilsFTP;


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
	 protected Void doInBackground() {
		  
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
			 Utils.showMessage("File downloaded successfully in:\n"+localePath+"\nPlease, close Giswater before executing this file.");
		 } 
		 else{
			 gui.close();
			 isCancelled = true;
			 Utils.showMessage("Downloaded aborted");
		 }
		 
	 }


}