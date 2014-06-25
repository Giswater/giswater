package org.giswater.gui.panel;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.giswater.util.DownloadTask;
import org.giswater.util.UtilsFTP;


public class DownloadPanel extends JPanel implements PropertyChangeListener {
	
	private JProgressBar progressBar;
	private JLabel lblInfo;
	private String remote;
	private String local;
	private UtilsFTP ftp;
	private String totalSize;
	private DownloadTask task;


	public DownloadPanel(String remote, String local, UtilsFTP ftp) {
		
        this.remote = remote;
        this.local = local;
        this.ftp = ftp;
        
		setLayout(new MigLayout("", "[10px:n][250.00][10px:n]", "[10px:n][][25px:n][::20px][]"));
		
		lblInfo = new JLabel("");
		add(lblInfo, "cell 1 1");
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setMinimumSize(new Dimension(10, 25));
		add(progressBar, "cell 1 2,growx,aligny center");
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancelDownload();
			}
		});
		add(btnCancelar, "cell 1 4,alignx trailing");
		
		startDownload();
		
	}

	
    private void cancelDownload() {
    	task.cancel(true);
    }
    
	
    private void startDownload() {
        progressBar.setValue(0);
        task = new DownloadTask(remote, local, this, ftp);
        task.addPropertyChangeListener(this);
        task.execute();
    }
	
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }		
	}

	
    public void setFileSize(double fileSize) {
    	double aux = (fileSize / 1048576);
    	DecimalFormat df = new DecimalFormat("#,##0.##");
    	totalSize = df.format(aux);
        lblInfo.setText(String.valueOf(fileSize));
    }


	public void setBytesRead(double bytesRead) {
		double aux = (bytesRead / 1048576);
    	DecimalFormat df = new DecimalFormat("#,##0.##");
    	String readSize = df.format(aux);
		String msg = "Downloading file: "+readSize+" Mb of "+totalSize+" Mb";
		lblInfo.setText(msg);
	}


	public void close() {
		Window w = SwingUtilities.getWindowAncestor(this);
		w.dispose();
	}
	
	
}