package org.giswater.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import org.giswater.util.MaxLengthTextDocument;


public class ProjectPanel extends JPanel implements ActionListener{
	
	private MainController controller;
	private JTextField txtName;
	private JTextField txtTitle;
	private JTextField txtFilter;
	private JTextField txtAuthor;
	private JTextField txtDate;
	private JCheckBox chkGeogcs;
	private JCheckBox chkProjcs;
	private JScrollPane panelTable;
	private JTable tblSrid;
	private JButton btnAccept;
	private JButton btnClose;
	
	
	public ProjectPanel(String defaultSrid) {
		initConfig();
		txtFilter.setText(defaultSrid);
		DateFormat dateFormat = new SimpleDateFormat("MMM-yyyy");
		Date date = new Date();
		txtDate.setText(dateFormat.format(date));
	}
		
	
	private void initConfig() {
		
		setLayout(new MigLayout("", "[][270.00,grow][grow]", "[][][][][10px:n][][220px:247.00,grow][]"));
		
		JLabel lblProjectName = new JLabel("Project name:");
		add(lblProjectName, "cell 0 0,alignx trailing,aligny center");
		
		txtName = new JTextField();
		txtName.setColumns(10);
		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(50);		
		txtName.setDocument(maxLength);		
		add(txtName, "cell 1 0,growx,aligny bottom");
		
		JLabel lblProjectTitle = new JLabel("Project title:");
		add(lblProjectTitle, "cell 0 1,alignx trailing");
		
		txtTitle = new JTextField();
		txtTitle.setColumns(10);
		maxLength = new MaxLengthTextDocument(250);		
		txtTitle.setDocument(maxLength);	
		add(txtTitle, "cell 1 1,growx");
		
		JLabel lblAuthor = new JLabel("Author:");
		add(lblAuthor, "cell 0 2,alignx trailing");
		
		txtAuthor = new JTextField();
		txtAuthor.setColumns(10);
		maxLength = new MaxLengthTextDocument(50);		
		txtAuthor.setDocument(maxLength);
		add(txtAuthor, "cell 1 2,growx");
		
		JLabel lblDate = new JLabel("Date:");
		add(lblDate, "cell 0 3,alignx trailing");
		
		txtDate = new JTextField();
		txtDate.setColumns(10);
		maxLength = new MaxLengthTextDocument(12);		
		txtDate.setDocument(maxLength);
		add(txtDate, "cell 1 3,growx");
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(50, 2));
		separator.setForeground(Color.BLACK);
		add(separator, "cell 0 4 2 1,growx");
		
		JLabel lblSelectSrid = new JLabel("Select SRID:");
		lblSelectSrid.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblSelectSrid, "cell 0 5 2 1,alignx left");
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel, "cell 0 6 3 1,grow");
		panel.setLayout(new MigLayout("", "[60px:n][270.00][50px,grow]", "[][][24px][159.00,grow]"));
		
		JLabel lblNewLabel = new JLabel("Filter:");
		panel.add(lblNewLabel, "cell 0 0");
		
		txtFilter = new JTextField();
		panel.add(txtFilter, "cell 1 0,growx");
		txtFilter.setColumns(10);
		
		JLabel lblType = new JLabel("Type:");
		panel.add(lblType, "cell 0 1");
		
		chkGeogcs = new JCheckBox("GEOGCS");
		chkGeogcs.setActionCommand("checkedType");
		chkGeogcs.setSelected(true);
		panel.add(chkGeogcs, "flowx,cell 1 1");
		
		JLabel lblCoordinateReferenceSystems = new JLabel("Coordinate reference systems:");
		panel.add(lblCoordinateReferenceSystems, "cell 0 2 2 1");
		
		tblSrid = new JTable();
		tblSrid.setFont(new Font("Tahoma", Font.PLAIN, 10));
		tblSrid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tblSrid.setAutoCreateRowSorter(true);
		tblSrid.setRowHeight(20);
		tblSrid.getTableHeader().setReorderingAllowed(false);	
		
		panelTable = new JScrollPane();
		panelTable.setViewportView(tblSrid);
		panel.add(panelTable, "cell 0 3 3 1,grow");
		
		chkProjcs = new JCheckBox("PROJCS");
		chkProjcs.setActionCommand("checkedType");
		chkProjcs.setSelected(true);
		panel.add(chkProjcs, "cell 1 1");
		
		btnAccept = new JButton("Accept");
		btnAccept.setActionCommand("acceptProject");
		add(btnAccept, "flowx,cell 1 7,alignx trailing");
		
		btnClose = new JButton("Close");
		btnClose.setActionCommand("closeProject");
		add(btnClose, "cell 2 7");
		
		setupListeners();
		
	}	
	
	
	private void setupListeners() {
		
		txtFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				controller.updateTableModel();
			}
		});
		chkGeogcs.addActionListener(this);
		chkProjcs.addActionListener(this);
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
		if (tblSrid.getRowCount() > 0){
			tblSrid.setRowSelectionInterval(0, 0);
		}
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
	
	public String getAuthor() {
		return txtAuthor.getText().trim();
	}	
	
	public String getDate() {
		return txtDate.getText().trim();
	}	
	
	public Boolean isGeoSelected(){
		return chkGeogcs.isSelected();
	}
	
	public Boolean isProjSelected(){
		return chkProjcs.isSelected();
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