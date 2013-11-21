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
package org.giswater.util;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.geotools.data.shapefile.dbf.DbaseFileReader;


public class Dbf2PgSql extends Thread{

	private String pgsql_database;
	private String pgsql_esquema;
	private String pgsql_host;
	private String pgsql_port;
	private String pgsql_user;
	private String pgsql_password;
	private boolean crear_script;
	private File[] dbf_files;
	private String sql_file;
	boolean log;
	boolean only_sql;
	boolean append_mode;
	String taula_append;
	
	boolean sortir = false;
	
	JProgressBar jpb;
	JFrame frame;
	JLabel jl;
	JLabel jl2;
	JLabel jl3;
	
	
	public Dbf2PgSql(String charset, String database, String esquema, String host, String port, 
		String user, String password, boolean crear_sql, File[] dbf_files, String sql_file, 
		boolean crear_log, boolean nomes_sql, boolean append, String t_append){
		
		this.pgsql_database = database;
		this.pgsql_esquema = esquema;
		this.pgsql_host = host;
		this.pgsql_port = port;
		this.pgsql_user = user;
		this.pgsql_password = password;
		this.crear_script = crear_sql;
		this.dbf_files = dbf_files;
		this.sql_file = sql_file;
		this.log = crear_log;
		this.only_sql = nomes_sql;
		this.append_mode = append;
		this.taula_append = t_append;
		
		frame = new JFrame("Dbf to PostgreSQL");
		//frame.setSize(500, 100);
		jpb = new JProgressBar(0,100);
		jpb.setStringPainted(true);	
		jl = new JLabel();
        jl2 = new JLabel();
        jl3 = new JLabel();

        frame.setResizable(false);
        jl.setText("jLabel1");
        jl2.setText("jLabel1");
        jl3.setText("jLabel1");
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		
        frame.addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent arg0) {
                System.out.println("Window close event occur");
                sortir = true;
            }
            public void windowActivated(WindowEvent arg0) {
                System.out.println("Window Activated");
            }
            public void windowClosing(WindowEvent arg0) {
                System.out.println("Window Closing");
            }
            public void windowDeactivated(WindowEvent arg0) {
                System.out.println("Window Deactivated");
            }
            public void windowDeiconified(WindowEvent arg0) {
                System.out.println("Window Deiconified");
            }
            public void windowIconified(WindowEvent arg0) {
                System.out.println("Window Iconified");
            }
            public void windowOpened(WindowEvent arg0) {
                System.out.println("Window Opened");
            }
        });
        
	}
	
	
	public void run() {
		try {
			main();
		}
		catch(Exception e) {
		}
	}
	
	
	@SuppressWarnings("resource")
	public void main() throws Exception {
		
		System.out.println("Dbf to PostgreSql");
		
		try {
		
			BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/aaa.sql"));
			File fd = new File(System.getProperty("user.dir")+"/"+"aaa.sql");
			fd.delete();
			
			BufferedWriter bwlog = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/geodata-tools-dbf2pgsql.log"));
			
			if((this.crear_script) || (this.only_sql))
				bw = new BufferedWriter(new FileWriter(sql_file));
			
			FileChannel in;
			
			int l=0;
			
			jpb.setMinimum(0);
	    	jpb.setMaximum(dbf_files.length);
		
	    for_fitxers:
			for (l=0; l<dbf_files.length; l++)	{
				
				if(sortir) {
	        		int s=0;
	        		for(s=l; s<dbf_files.length; s++)
	        			dbf_files[l]=null;
	        		break;
	        	}	
	        	jl.setText("Llegint dbf...");
	        	jpb.setValue(l+1);
	        	jl2.setText("  " + (l+1) + "/" + dbf_files.length);
	        	jl3.setText(dbf_files[l].getName());
	        	Thread.sleep(500);
			
				in = new FileInputStream(dbf_files[l]).getChannel();
				DbaseFileReader dbf = new DbaseFileReader(in);
				
			 	String head = dbf.getHeader().toString();
			 	System.out.println(head);
			 	
			 	int ncols = dbf.getHeader().getNumFields();
			 	//int records = dbf.getHeader().getNumRecords();
			 	DbaseFileReader.Row fila;
			 	
			 	String nom_atribut;
			 	char tipus_atribut;
			 	int longitud_atribut;
			 	int longitud_decimal;
			 	//int decimals_atribut;
			 	String tipus_postgres="";
			 	
			 	String nom_taula = dbf_files[l].getName().toLowerCase().replace(".dbf", "");
			 	String crear_taula = "CREATE TABLE " + this.pgsql_esquema + nom_taula.toLowerCase() + " (";
			 	
			 	//crear_taula += nom_taula + "__id serial NOT NULL,";	 	 
			 	
			 	jl.setText("Determinant tipus de dades Postgre...");
	        	Thread.sleep(500);
			 	
	        	boolean gid_clau_primaria = false;
	        	boolean crear_gid_pk = true;
			 	int i;
			 	for(i=0; i<ncols; i++) {
			 		
			 		gid_clau_primaria = false;
				 	nom_atribut = dbf.getHeader().getFieldName(i);
				 	if(nom_atribut.compareTo("gid")==0) {
				 		gid_clau_primaria = true;
				 		crear_gid_pk = false;
				 	}
				 	
				 	tipus_atribut = dbf.getHeader().getFieldType(i);
				 	longitud_atribut = dbf.getHeader().getFieldLength(i);
				 	longitud_decimal = dbf.getHeader().getFieldDecimalCount(i);				 	
				 	
				 	switch(tipus_atribut) {
				 	
				 	case 'N':
				 		
				 		if(longitud_decimal > 0) {
				 			tipus_postgres = " double precision ";
					 		break;
				 		}
				 		
				 		// integer
				 		else if (longitud_atribut < 10) {
				 			tipus_postgres = " integer ";
				 		}
				 		// bigint
				 		else if( (longitud_atribut >= 10) && (longitud_atribut < 15) ) {
				 			tipus_postgres = " bigint ";
				 		}
				 		// double precision
				 		else {
				 			tipus_postgres = " double precision ";
				 		}
				 		break;
				 		
				 	case 'C':
				 		//character varyng(longitud_atribut)
				 		tipus_postgres = " character varying(" + (longitud_atribut) + ") ";
				 		break;
				 		
				 	case 'F':
				 		tipus_postgres = " float4 ";
				 		break;
				 		
				 	case 'D':
				 		tipus_postgres = " date ";
				 		break;	
				 		
				 	case 'L':
				 		tipus_postgres = " boolean ";
				 		break;				 	
				 		
				 	default:
				 		JOptionPane.showMessageDialog(null, "tipus de dades no suportat ", "tipus de dades no suportat", JOptionPane.ERROR_MESSAGE);
				 		bwlog.write("\ntipus de dades no suportat\n");
				 		break;	 	
				 	}	 	
				 	
				 	if(gid_clau_primaria) {
				 		crear_taula += nom_atribut + " serial NOT NULL,";
				 		crear_taula += "CONSTRAINT " + nom_taula + "_pkey PRIMARY KEY (" + nom_atribut + ")";
				 	} else
				 		crear_taula += nom_atribut + " " + tipus_postgres;
				 		
				 	if (i+1 != ncols)	
				 		crear_taula += ",";
				 	
			 	}
			 	
			 	if(crear_gid_pk) {
			 		crear_taula += ", gid serial NOT NULL,";
			 		crear_taula += "CONSTRAINT " + nom_taula + "_pkey PRIMARY KEY (gid));";
			 	} else
			 		crear_taula += ");";
			 		
			 	if((!this.only_sql) && (!this.append_mode)){ 
			 		jl.setText("Creant taula...");
			 		Thread.sleep(500);
			 	}
			 	
			 	Connection db = null;
			 	Statement consulta = null;
			 	ResultSet rs = null;
			 	
			 	try {
			 	
				 	Class.forName("org.postgresql.Driver");
			        String url = "jdbc:postgresql://" + this.pgsql_host + ":" + this.pgsql_port + "/" + this.pgsql_database;
				 	db = DriverManager.getConnection(url, this.pgsql_user, this.pgsql_password);
				 	
				 	if((!this.only_sql) && (!this.append_mode)){ 
				 		consulta = db.createStatement();   		 	
	        		 	
					 	rs = consulta.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_name = '" + nom_taula.toLowerCase() + "'");
	        		 	while (rs.next()) {
					 		if(rs.getString("table_name").equalsIgnoreCase(nom_taula)) {
					 			int sn = JOptionPane.showConfirmDialog(null, "La taula '"+nom_taula+"' ja existeix.\nVols sobreescriure-la?", "Taula existent", JOptionPane.YES_NO_OPTION);
	        	        		if(sn==0) {
	        	        			consulta.execute("DROP TABLE " + nom_taula);
	        	        		} else {
	        	        			continue for_fitxers;
	        	        		}
					 			break;
					 		}	
				 		}
					 	
					 	consulta = db.createStatement();
					 	consulta.execute(crear_taula);
					 	System.out.println(crear_taula);
				 	}
					 	
				 	if (((this.crear_script) || this.only_sql) && (!this.append_mode)) {
				 		bw.write("\n" + crear_taula + "\n");
				 	}
				 		
				 	String insert = "";
				 	String value = "";
			
				 	if(!this.only_sql) {
				 		jl.setText("Insertant dades...");
				 		Thread.sleep(500);
				 	}
				 	
				 	i=0;
				 	while (dbf.hasNext()) { 
					 	
				 		fila = dbf.readRow();
					 	//System.out.println(fila.toString());
				 		if(this.append_mode) {
				 			insert = "INSERT INTO " + this.pgsql_esquema + "\"" + this.taula_append.toLowerCase() + "\"(";
				 		} else {
				 			insert = "INSERT INTO " + this.pgsql_esquema + "\"" + nom_taula.toLowerCase() + "\"(";
				 		}
					 	
					 	int j;
					 	for (j=0; j < ncols; j++) {
					 		if(dbf.getHeader().getFieldName(j).compareTo("gid")!=0) {
					 			insert += dbf.getHeader().getFieldName(j);
					 			
					 			if ((j+1 != ncols) && (dbf.getHeader().getFieldName(j+1).compareTo("gid")!=0)){
					 				insert += ",";
					 			}
					 		}
					 	}
					 	insert += ") VALUES (";
					 	
					 	for (int k = 0; k < ncols; k++) {
					 		
					 		if(dbf.getHeader().getFieldName(k).compareTo("gid")!=0) {
					 			
						 		if (fila.read(k) == null) {
						 			insert += "NULL";
						 			//JOptionPane.showMessageDialog(null, "null ", "null ", JOptionPane.ERROR_MESSAGE);
						 		//System.out.println(valor);
						 		}
						 		
						 		else {
						 			
						 			value = fila.read(k).toString();
						 			
						 			if (dbf.getHeader().getFieldType(k) == 'L'){ //(value.length() == 1) {
						 				//insert += "NULL";
						 			    if (value.compareToIgnoreCase(" ")==0)
						 			    	insert += "NULL";
						 				else
						 					insert += value;
						 				//JOptionPane.showMessageDialog(null, "trim ", "trim ", JOptionPane.ERROR_MESSAGE);
						 			}
						 		
							 		else if ( (dbf.getHeader().getFieldType(k) == 'D') ) {				
							 			//System.out.println(valor);
							 			String[] t = value.split(" ");
							 			//for(int v=0; v<t.length; v++)
							 				//System.out.println(t[v]);
							 			//insert += "'Jan-08-1999'";
							 			//insert += "'" + t[1] + "-" + t[2] + "-" + t[5] + "'";
							 			insert += "'" + t[1] + " " + t[2] + " " + t[5] + "'";
							 		}
							 			
							 		else if ( (dbf.getHeader().getFieldType(k) == 'N') || (dbf.getHeader().getFieldType(k) == 'F') ) {
							 			//insert += "'" + fila.read(k).toString().replace(".0", "") + "'";
							 			insert += value;
							 		}
							 		
							 		else {
							 			insert += "'" + value.replace("'", "''") + "'";
							 		}
						 		}
						 		
						 		if ((k+1 != ncols) && (dbf.getHeader().getFieldName(k+1).compareTo("gid")!=0)){
						 			insert += ",";	
						 		}
					 		}
					 	} 
					 	
					 	insert += ");";
					 	//System.out.println(insert);
					 	
					 	if ((this.crear_script) || (this.only_sql)) {
					 		bw.write(insert + "\n");
					 	}
					 	
					 	if(!this.only_sql) {
					 		//db = DriverManager.getConnection(url, this.pgsql_user, this.pgsql_password);
					 		consulta = db.createStatement();
		        		 	consulta.execute(insert);
						 	consulta.close();
						 	//db.close();
					 	}
					 	
					 	//System.out.println("\n---------------");
					 	i++;
				 	} 
			 	
			 	} catch(SQLException sqle) {
			 		JOptionPane.showMessageDialog(null, "Error al fitxer " + dbf_files[l].getAbsolutePath() + "\n" + sqle.getMessage(), "ERROR (SQLException)", JOptionPane.ERROR_MESSAGE);
			 		bwlog.write("\nError al fitxer " + dbf_files[l].getAbsolutePath() + "\n" + sqle.getMessage() + "\n");
			 		rs.close();
				 	consulta.close();
				 	db.close();
			 	} catch(NullPointerException npe) {
			    	JOptionPane.showMessageDialog(null, "Error al fitxer " + dbf_files[l].getAbsolutePath() + "\n" + npe.getMessage(), "ERROR (NullPointerException)", JOptionPane.ERROR_MESSAGE);
			 		npe.printStackTrace();
			 		bwlog.write("\nError al fitxer " + dbf_files[l].getAbsolutePath() + "\n" + npe.getMessage() + "\n");
			 		rs.close();
				 	consulta.close();
				 	db.close();
			 	} finally {
			 		rs.close();
				 	consulta.close();
				 	db.close();
			 	}
			 	
			 	dbf.close();
			}
			
			if (this.crear_script || this.only_sql)
				bw.close();
			
			frame.dispose();
			bwlog.close();
			
		} catch(IOException ioe) {
			JOptionPane.showMessageDialog(null, ioe.getMessage(), "ERROR (IOException)", JOptionPane.ERROR_MESSAGE);
		}
	}

}