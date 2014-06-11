package org.giswater.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.MainController;
import org.giswater.model.TableModelSrid;


public class ProjectPanel extends JPanel implements ActionListener{
	
	private JTextField txtName;
	private JTextField txtTitle;
	private JTextField txtFilter;
	private JTable tblSrid;
	private MainController controller;
	private JScrollPane panelTable;
	private JButton btnAccept;
	private JButton btnClose;
	
	
	public ProjectPanel() {
		initConfig();
	}
		
	
	private void initConfig() {
		
		setLayout(new MigLayout("", "[][270.00][grow]", "[][][10px:n][][220px:220.00,grow][]"));
		
		JLabel lblProjectName = new JLabel("Project name:");
		add(lblProjectName, "cell 0 0,alignx trailing,aligny center");
		
		txtName = new JTextField();
		add(txtName, "cell 1 0,growx,aligny bottom");
		txtName.setColumns(10);
		
		JLabel lblProjectTitle = new JLabel("Project title:");
		add(lblProjectTitle, "cell 0 1,alignx trailing");
		
		txtTitle = new JTextField();
		txtTitle.setColumns(10);
		add(txtTitle, "cell 1 1,growx");
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(50, 2));
		separator.setForeground(Color.BLACK);
		add(separator, "cell 0 2 2 1,growx");
		
		JLabel lblSelectSrid = new JLabel("Select SRID:");
		lblSelectSrid.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblSelectSrid, "cell 0 3 2 1,alignx left");
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel, "cell 0 4 3 1,grow");
		panel.setLayout(new MigLayout("", "[60px:n][270.00][50px,grow]", "[][24px][159.00,grow]"));
		
		JLabel lblNewLabel = new JLabel("Filter:");
		panel.add(lblNewLabel, "cell 0 0");
		
		txtFilter = new JTextField();
		panel.add(txtFilter, "cell 1 0,growx");
		txtFilter.setColumns(10);
		
		JLabel lblCoordinateReferenceSystems = new JLabel("Coordinate reference systems:");
		panel.add(lblCoordinateReferenceSystems, "cell 0 1 2 1");
		
		tblSrid = new JTable();
		tblSrid.setFont(new Font("Tahoma", Font.PLAIN, 10));
		tblSrid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tblSrid.setAutoCreateRowSorter(true);
		tblSrid.setRowHeight(20);
		tblSrid.getTableHeader().setReorderingAllowed(false);	
		
		panelTable = new JScrollPane();
		panelTable.setViewportView(tblSrid);
		panel.add(panelTable, "cell 0 2 3 1,grow");
		
		btnAccept = new JButton("Accept");
		btnAccept.setActionCommand("acceptProject");
		add(btnAccept, "flowx,cell 1 5,alignx trailing");
		
		btnClose = new JButton("Close");
		btnClose.setActionCommand("closeProject");
		add(btnClose, "cell 2 5");
		
		setupListeners();
		
	}	
	
	
	private void setupListeners() {
		
		txtFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				controller.updateTableModel();
			}
		});
		btnAccept.addActionListener(this);
		btnClose.addActionListener(this);
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.action(e.getActionCommand());
	}
	

	public void setController(MainController controller) {
		this.controller = controller;
	}


	public JTable getTable() {
		return tblSrid;
	}
	
	
	public void setTableModel(TableModelSrid model) {
		tblSrid.setModel(model);		
		model.fireTableDataChanged();
	}


	public String getFilter() {
		return txtFilter.getText().trim();
	}	
	
	public String getName() {
		return txtName.getText().trim();
	}	

	public String getTitle() {
		return txtTitle.getText().trim();
	}	
	
	public String getSrid() {
		int row = tblSrid.getSelectedRow();
		if (row > -1){
			String srid = (String) tblSrid.getValueAt(row, 1);
			return srid;
		}
		return "-1";
	}	

	
}