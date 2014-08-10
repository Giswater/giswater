package org.giswater.gui.panel;

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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.giswater.controller.HecRasController;
import org.giswater.controller.NewProjectController;
import org.giswater.model.TableModelSrid;
import org.giswater.util.MaxLengthTextDocument;


public class ProjectPanel extends JPanel implements ActionListener{
	
	private JDialog parent;
	private NewProjectController controller;
	private HecRasController hecRasController;
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
	private JLabel lblInpFile;
	private JTextArea txtFileLoadInp;
	private JScrollPane scrollPane;
	private JButton btnFileInp;
	private JPanel panelSrid;
	private String defaultSrid;
	
	private static final Font FONT_12 = new Font("Tahoma", Font.BOLD, 12);
	private JCheckBox chkLoadData;
	private JLabel lbloptional;
	private JLabel label;
	
	
	public ProjectPanel(String defaultSrid) {
		this.defaultSrid = defaultSrid;
		initConfig();
	}
		
	
	private void initConfig() {
		
		setLayout(new MigLayout("", "[][217.00px:n,grow][grow]", "[5px:n][][][][][5px:n][240.00,grow][5px:n][][40px:40px][5px:n][]"));
		
		JLabel lblProjectName = new JLabel("Project name:");
		add(lblProjectName, "cell 0 1,alignx trailing,aligny center");
		
		txtName = new JTextField();
		txtName.setColumns(10);
		MaxLengthTextDocument maxLength = new MaxLengthTextDocument(25);		
		txtName.setDocument(maxLength);		
		add(txtName, "cell 1 1,growx,aligny bottom");
		
		JLabel lblProjectTitle = new JLabel("Project title:");
		add(lblProjectTitle, "cell 0 2,alignx trailing");
		
		txtTitle = new JTextField();
		txtTitle.setColumns(10);
		maxLength = new MaxLengthTextDocument(250);		
		txtTitle.setDocument(maxLength);	
		add(txtTitle, "cell 1 2,growx");
		
		JLabel lblAuthor = new JLabel("Author:");
		add(lblAuthor, "cell 0 3,alignx trailing");
		
		txtAuthor = new JTextField();
		txtAuthor.setColumns(10);
		maxLength = new MaxLengthTextDocument(50);		
		txtAuthor.setDocument(maxLength);
		add(txtAuthor, "cell 1 3,growx");
		
		lbloptional = new JLabel("(optional)");
		add(lbloptional, "cell 2 3");
		
		JLabel lblDate = new JLabel("Date:");
		add(lblDate, "cell 0 4,alignx trailing");
		
		txtDate = new JTextField();
		txtDate.setColumns(10);
		maxLength = new MaxLengthTextDocument(12);		
		txtDate.setDocument(maxLength);
		DateFormat dateFormat = new SimpleDateFormat("MMM-yyyy");
		Date date = new Date();
		txtDate.setText(dateFormat.format(date));
		add(txtDate, "cell 1 4,growx");
		
		label = new JLabel("(optional)");
		add(label, "cell 2 4");
		
		panelSrid = new JPanel();
		panelSrid.setBorder(new TitledBorder(null, "Select SRID", TitledBorder.LEADING, TitledBorder.TOP, FONT_12, null));
		panelSrid.setLayout(new MigLayout("", "[62px:n][200.00px:n][50px:n][::95.00px]", "[][][::217.00px,grow]"));
		add(panelSrid, "cell 0 6 3 1,grow");
		
		JLabel lblFilter = new JLabel("Filter:");
		panelSrid.add(lblFilter, "cell 0 0");
		
		txtFilter = new JTextField(defaultSrid);
		panelSrid.add(txtFilter, "cell 1 0,growx");
		txtFilter.setColumns(10);
		
		chkLoadData = new JCheckBox("Load Data");
		chkLoadData.setEnabled(false);
		chkLoadData.setActionCommand("loadData");
		add(chkLoadData, "cell 0 8");
		
		lblInpFile = new JLabel("Project file:");
		lblInpFile.setEnabled(false);
		add(lblInpFile, "cell 0 9,alignx right");
		
		scrollPane = new JScrollPane();
		add(scrollPane, "cell 1 9,grow");
		
		txtFileLoadInp = new JTextArea();
		txtFileLoadInp.setEnabled(false);
		txtFileLoadInp.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(txtFileLoadInp);
		
		btnFileInp = new JButton("...");
		btnFileInp.setEnabled(false);
		btnFileInp.setActionCommand("chooseFileLoadInp");
		add(btnFileInp, "cell 2 9");
		
		btnAccept = new JButton("Accept");
		btnAccept.setActionCommand("acceptProject");
		add(btnAccept, "flowx,cell 1 11,alignx trailing");
		
		btnClose = new JButton("Close");
		btnClose.setActionCommand("closeProject");
		add(btnClose, "cell 2 11");
		
		JLabel lblType = new JLabel("Type:");
		panelSrid.add(lblType, "cell 0 1");
		
		chkGeogcs = new JCheckBox("GEOGCS");
		panelSrid.add(chkGeogcs, "flowx,cell 1 1");
		chkGeogcs.setActionCommand("checkedType");
		chkGeogcs.setSelected(true);
		
		chkProjcs = new JCheckBox("PROJCS");
		panelSrid.add(chkProjcs, "cell 1 1");
		chkProjcs.setActionCommand("checkedType");
		chkProjcs.setSelected(true);
		
		tblSrid = new JTable();
		tblSrid.setFont(new Font("Tahoma", Font.PLAIN, 10));
		tblSrid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tblSrid.setAutoCreateRowSorter(true);
		tblSrid.setRowHeight(20);
		tblSrid.getTableHeader().setReorderingAllowed(false);	
		
		panelTable = new JScrollPane();
		panelSrid.add(panelTable, "cell 0 2 3 1");
		panelTable.setBorder(new TitledBorder(null, "Coordinate Reference Systems", TitledBorder.LEADING, TitledBorder.TOP));
		panelTable.setViewportView(tblSrid);
		
		setupListeners();
		
	}	
	
	
	private void setupListeners() {
		
		txtFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (controller != null){
					controller.updateTableModel();
				}
			}
		});
		chkGeogcs.addActionListener(this);
		chkProjcs.addActionListener(this);
		chkLoadData.addActionListener(this);
		btnFileInp.addActionListener(this);
		btnAccept.addActionListener(this);
		btnClose.addActionListener(this);
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (controller != null){
			controller.action(e.getActionCommand());
		}
		else{
			hecRasController.action(e.getActionCommand());
		}
	}
	

	public void setController(NewProjectController controller) {
		this.controller = controller;
	}

	public void setHecRasController(HecRasController hecRasController) {
		this.hecRasController = hecRasController;
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


	public JDialog getDialog() {
		return parent;
	}


	public void setParent(JDialog projectDialog) {
		parent = projectDialog;
	}


	public void setFileLoadInp(String path) {
		txtFileLoadInp.setText(path);
	}

	
}