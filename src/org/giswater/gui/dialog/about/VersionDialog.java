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
 *   David Erill <daviderill79@gmail.com>
 */
package org.giswater.gui.dialog.about;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.giswater.util.Utils;


public class VersionDialog extends JDialog {

	private static final long serialVersionUID = 2829254148112384387L;
	private JLabel lblInfo;
	public URI urlWeb = null;
	public URI urlGithub = null;	
	private final String URL_WEB = "http://www.giswater.org";
	private final String URL_GITHUB = "https://github.com/Tecnicsassociats/giswater";


	class OpenUrlWeb implements ActionListener {
	    @Override public void actionPerformed(ActionEvent e) {
	        if (Desktop.isDesktopSupported()) {
	            try {
	              Desktop.getDesktop().browse(urlWeb);
	            } catch (IOException e1) { }
	        }
	    }
	}	
	
	
	class OpenUrlGithub implements ActionListener {
	    @Override public void actionPerformed(ActionEvent e) {
	        if (Desktop.isDesktopSupported()) {
	            try {
	              Desktop.getDesktop().browse(urlGithub);
	            } catch (IOException e1) { }
	        }
	    }
	}	
	

	public VersionDialog(String title) {

		ImageIcon image = new ImageIcon("images/imago.png");
		setIconImage(image.getImage());		
		setTitle(title);
		setSize(432, 124);
		getContentPane().setLayout(new MigLayout("", "[116.00][173.00px,grow]", "[20px:20px:20px][20px:20px:20px][20px:20px:20px]"));
		
		try {
			urlWeb = new URI(URL_WEB);
			urlGithub = new URI(URL_GITHUB);
		} catch (URISyntaxException e) {
			Utils.getLogger().warning(e.getMessage());
		}		
		JButton btnWeb = new JButton();
		btnWeb.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnWeb.setText("<HTML><FONT color=\"#000099\"><U>"+URL_WEB+"</U></FONT></HTML>");
		btnWeb.setHorizontalAlignment(SwingConstants.LEFT);
		btnWeb.setBorderPainted(false);
		btnWeb.setOpaque(false);
		btnWeb.setBackground(Color.WHITE);
		btnWeb.setToolTipText(URL_WEB);
		btnWeb.addActionListener(new OpenUrlWeb());		
		
		getContentPane().add(btnWeb, "cell 0 0 2 1,alignx center");
		
		lblInfo = new JLabel("Developer: David Erill Carrera");
		lblInfo.setFont(new Font("Tahoma", Font.BOLD, 11));
		getContentPane().add(lblInfo, "cell 0 1 2 1,alignx center,aligny center");	
		
		JButton btnGithub = new JButton();
		btnGithub.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnGithub.setText("<HTML>Source code: <FONT color=\"#000099\"><U>"+URL_GITHUB+"</U></FONT></HTML>");
		btnGithub.setHorizontalAlignment(SwingConstants.LEFT);
		btnGithub.setBorderPainted(false);
		btnGithub.setOpaque(false);
		btnGithub.setBackground(Color.WHITE);
		btnGithub.setToolTipText(URL_GITHUB);
		btnGithub.addActionListener(new OpenUrlGithub());		
		getContentPane().add(btnGithub, "cell 0 2 2 1,alignx center");
		
		getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});		
	
	}

	
}