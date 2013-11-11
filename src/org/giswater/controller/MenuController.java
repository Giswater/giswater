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
package org.giswater.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;

import org.giswater.dao.MainDao;
import org.giswater.gui.MainFrame;
import org.giswater.gui.dialog.VersionDialog;
import org.giswater.gui.dialog.LicenseDialog;
import org.giswater.gui.dialog.WelcomeDialog;
import org.giswater.util.PropertiesMap;
import org.giswater.util.Utils;

public class MenuController {

	private MainFrame view;
	private PropertiesMap prop;

	public MenuController(MainFrame mainFrame) {
		this.view = mainFrame;
		this.prop = MainDao.getPropertiesFile();
		view.setControl(this);
	}

	public void action(String actionCommand) {

		Method method;
		try {
			if (Utils.getLogger() != null) {
				Utils.getLogger().info(actionCommand);
			}
			method = this.getClass().getMethod(actionCommand);
			method.invoke(this);
		} catch (Exception e) {
			if (Utils.getLogger() != null) {
				Utils.logError(e, actionCommand);
			} else {
				Utils.showError(e, actionCommand);
			}
		}

	}

	public void openSwmm() {
		view.openSwmm();
	}

	public void openEpanet() {
		view.openEpanet();
	}

	public void openHecras() {
		view.openHecras();
	}

	public void showSoftware() {
		view.openSoftware();
	}

	public void showDatabase() {
		view.openDatabase();
	}

	public void openHelp() {
		if (MainDao.fileHelp != null) {
			try {
				Desktop.getDesktop().open(MainDao.fileHelp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// TODO: i18n
	public void showWelcome() {
		String title = "Welcome";
		String info = "Welcome to Giswater, the EPANET, EPASWMM and HEC-RAS communication tool";
		String info2 = "Please read the documentation and enjoy using the software";
		WelcomeDialog about = new WelcomeDialog(title, info, info2);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
	}

	public void showVersion() {
		String version = "Giswater version " + prop.get("VERSION_CODE");
		VersionDialog about = new VersionDialog(version);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
	}

	public void showLicense() {
		String title = "License";
		String info = "This product as a whole is distributed under the GNU General Public License version 3";
		String info2Begin = "<html><p align=\"justify\"><font size='2'>";
		String info2Body = 
				"This product has been funded wholly or in part by TECNICSASSOCIATS, " +
				"TALLER D'ARQUITECTURA I ENGINYERIA, SL. (hereafter TECNICSASSOCIATS). Mention of trade names or commercial products " +
				"does not constitute endorsement or recommendation for use. Although It has been subjected to technical review before " +
				"being released and although it has made a considerable effort to assure that the results obtained are correct, " +
				"the computer programs are experimental. Therefore the author and TECNICSASSOCIATS are not responsible and assume no " +
				"liability whatsoever for any results or any use made of the results obtained from these programs, nor for any damages " +
				"or litigation that result from the use of these programs for any purpose.";
		String info2End = "</font></p></html>";		
		String info2 = info2Begin + info2Body + info2End;
		String info3 = "View license file";
		LicenseDialog about = new LicenseDialog(title, info, info2, info3);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
	}

	public void showAgreements() {
		String title = "Agreements";
		String info = "Special thanks for his contribution to the project to:";
		String info2 = "Gemma García Ribot & Andrés Rodríguez Valero";
		WelcomeDialog about = new WelcomeDialog(title, info, info2);
		about.setModal(true);
		about.setLocationRelativeTo(null);
		about.setVisible(true);
	}

}