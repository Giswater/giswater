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
package org.giswater.gui.dialog.about;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.giswater.util.Utils;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class LicenseDialog extends JDialog {

	public URI uri = null;
	public File file = null;
	private JButton btnLicense;

	
	public LicenseDialog(String title, String info, String info1, String info2, String info3) {
			
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 8));
		ImageIcon image = new ImageIcon("images/imago.png");
		setIconImage(image.getImage());
		setTitle(title);		
		setSize(640, 260);
		getContentPane().setLayout(new MigLayout("", "[5px:n:5px][150px,grow][5px:n:5px]", "[5px][25px][][30.00px][30.00]"));
		
		JLabel lblInfo = new JLabel(info.toUpperCase());
		lblInfo.setFont(new Font("Tahoma", Font.BOLD, 12));
		getContentPane().add(lblInfo, "cell 1 1,alignx left");	
		
		JLabel lblInfo12 = new JLabel(info1);
		getContentPane().add(lblInfo12, "cell 1 2");
		
		JLabel lblInfo2 = new JLabel(info2.toUpperCase());
		lblInfo2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		getContentPane().add(lblInfo2, "cell 1 3,alignx center");
		
    	String folderRoot;
		try {
			folderRoot = new File(".").getCanonicalPath() + File.separator + "legal" + File.separator;
			file = new File(folderRoot + "licensing.txt");			
		} catch (IOException e) {
			Utils.logError(e);
		}   			
		
		btnLicense = new JButton();
		btnLicense.setFont(new Font("Tahoma", Font.BOLD, 12));
		String text = "<HTML><FONT color=\"#000099\"><U>"+info3.toUpperCase()+"</U></FONT></HTML>";
		btnLicense.setText(text);
		btnLicense.setHorizontalAlignment(SwingConstants.LEFT);
		btnLicense.setBorderPainted(false);
		btnLicense.setOpaque(false);
		btnLicense.setBackground(Color.WHITE);
		btnLicense.setToolTipText(file.toString());
		getContentPane().add(btnLicense, "flowx,cell 1 4,alignx center");
		
		setupListeners();
		
	}
	
	
	private void setupListeners(){
		
		getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});		

		class OpenUrlAction implements ActionListener {
		    @Override public void actionPerformed(ActionEvent event) {
				try {
					if (file.exists()){
						Desktop.getDesktop().open(file);
					}
				} catch (IOException e) {
					Utils.logError(e);
				}
		    }
		}		
		
		btnLicense.addActionListener(new OpenUrlAction());				
		
	}

	
}