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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.giswater.util.Utils;


public class WelcomeDialog extends JDialog {

	private static final long serialVersionUID = 2829254148112384387L;
	public URI urlWeb = null;
	public URI urlGithub = null;	
	private final String URL_WEB = "http://www.giswater.org";
	private final String URL_GITHUB = "http://github.com/giswater/giswater";	


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
	
	
	public WelcomeDialog(String title, String info, String info2, String version) {
		
		final ImageIcon iconImage = new ImageIcon("images/imago.png");
		final ImageIcon backgroundImage = new ImageIcon("images/giswater.png");

		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 8));
		setIconImage(iconImage.getImage());
		setTitle(title);		
		setSize(525, 220);
		getContentPane().setLayout(new MigLayout("", "[518.00px]", "[8px][45.00][20px][20.00px][::20px][::20px][20px:n:20px]"));
		try {
			urlWeb = new URI(URL_WEB);
			urlGithub = new URI(URL_GITHUB);
		} catch (URISyntaxException e) {
			Utils.logError(e.getMessage());
		}			

        JPanel panelLogo = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 3096090575648819722L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 145, 0, 205, 40, this);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width = Math.max(backgroundImage.getIconWidth(), size.width);
                size.height = Math.max(backgroundImage.getIconHeight(), size.height);
                return size;
            }
            
        };
        
        getContentPane().add(panelLogo, "cell 0 1,alignx center");
		
		JLabel lblInfo = new JLabel(info);
		lblInfo.setFont(new Font("Tahoma", Font.BOLD, 12));
		getContentPane().add(lblInfo, "cell 0 2,alignx center");	
		
		JLabel lblInfo2 = new JLabel(info2);
		lblInfo2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		getContentPane().add(lblInfo2, "cell 0 3,alignx center");
		
		JButton btnWeb = new JButton();
		btnWeb.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnWeb.setText("<HTML>Project website: <FONT color=\"#000099\"><U>http://www.giswater.org</U></FONT></HTML>");
		btnWeb.setHorizontalAlignment(SwingConstants.LEFT);
		btnWeb.setBorderPainted(false);
		btnWeb.setOpaque(false);
		btnWeb.setBackground(Color.WHITE);
		btnWeb.setToolTipText("http://www.giswater.org");
		btnWeb.addActionListener(new OpenUrlWeb());		
		getContentPane().add(btnWeb, "cell 0 5,alignx center");
		
		JButton btnGithub = new JButton();
		btnGithub.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnGithub.setText("<HTML>Source code: <FONT color=\"#000099\"><U>"+URL_GITHUB+"</U></FONT></HTML>");
		btnGithub.setHorizontalAlignment(SwingConstants.LEFT);
		btnGithub.setBorderPainted(false);
		btnGithub.setOpaque(false);
		btnGithub.setBackground(Color.WHITE);
		btnGithub.setToolTipText(URL_GITHUB);
		btnGithub.addActionListener(new OpenUrlGithub());	
		getContentPane().add(btnGithub, "cell 0 4,alignx center");
		
		JLabel lblVersion = new JLabel(version);
		lblVersion.setFont(new Font("Tahoma", Font.BOLD, 12));
		getContentPane().add(lblVersion, "cell 0 6,alignx center");
		
		getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});		
		
	}

	
}