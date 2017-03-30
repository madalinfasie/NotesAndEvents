package EventsPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import NotesPackage.ShowNotes;

public class ShowEvents {
	
	private JFrame frame;
	private static JLabel lblMonth;
	private static JLabel lblYear;
	private static JButton btnPrev;
	private static JButton btnNext;
	private static JComboBox<String> cmbYear;
	private static JTable tblCalendar;
	private static Container contPane;
	private static DefaultTableModel dtmCalendar;
	private static JScrollPane spCalendar;
	private static JPanel pnlCalendar;
	private static int realDay, realMonth, realYear, currentMonth, currentYear;
	
	JPanel mainPanel;
	public ShowEvents(){
		buildGui();
	}
	
	private void buildGui(){
		//build the frame
		frame = new JFrame("NotesAndEvents");
		frame.setSize(800, 800);
		frame.setLayout(new BorderLayout());
		
		//create components
		createCalendarComponents();
		
		//add the panels to the frame
		//frame.add(pnlControlPanel, BorderLayout.NORTH);
		//frame.add(pnlCalendar, BorderLayout.CENTER);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void createCalendarComponents() {
		//instantiate the components
		lblMonth = new JLabel("January");
		lblYear = new JLabel("Change year:");
		cmbYear = new JComboBox<String>();
		btnPrev = new JButton("<");
		btnNext = new JButton(">");
		dtmCalendar = new DefaultTableModel(){
			private static final long serialVersionUID = 1L;

			//make table cells not editable
			public boolean isCellEditable(int rowIndex, int mColIndex){
				return false;
			}
		};
		tblCalendar = new JTable(dtmCalendar);
		spCalendar = new JScrollPane(tblCalendar);
		pnlCalendar = new JPanel(null);
		
		//create back to notes button
		JButton notesButton = new JButton("Notes");
		notesButton.setActionCommand("GoToNotes");
		notesButton.addActionListener(new ControlButtonClickListener());
		
		
		//setup the container pane
		contPane = frame.getContentPane();
		contPane.setLayout(null);
		
		//set the border for the calendar panel
		//pnlCalendar.setBorder(BorderFactory.createTitledBorder("Calendar"));
		
		contPane.add(pnlCalendar);

		//set action command to buttons
		btnPrev.setActionCommand("btnPrev");
		btnNext.setActionCommand("btnNext");
		cmbYear.setActionCommand("cmbYear");
		
		//set the action listeners
		btnPrev.addActionListener(new CalendarButtonClickListener());
		btnNext.addActionListener(new CalendarButtonClickListener());
		cmbYear.addActionListener(new CalendarButtonClickListener());
		
		//add components to the calendar panel
		pnlCalendar.add(lblMonth);
		pnlCalendar.add(lblYear);
		pnlCalendar.add(cmbYear);
		pnlCalendar.add(btnPrev);
		pnlCalendar.add(btnNext);
		pnlCalendar.add(spCalendar);
		pnlCalendar.add(notesButton);
		
		//set the position of the components
		pnlCalendar.setBounds(0,0,850,850);
		lblMonth.setBounds(400-lblMonth.getPreferredSize().width/2, 25, 130, 25);
		lblYear.setBounds(48, 740, 120, 20);
		cmbYear.setBounds(164,740, 80, 20);
		btnPrev.setBounds(48, 55, 50, 25);
		btnNext.setBounds(695, 55, 50, 25);
		spCalendar.setBounds(47, 85, 700, 645);
		notesButton.setBounds(47, 5, 70, 30);
		
		//change fonts and text sizes
		lblMonth.setFont(new Font("Helvetica", 0, 20));
		lblYear.setFont(new Font("Helvetica", 0, 17));
		btnPrev.setFont(new Font("Helvetica", 0, 17));
		btnNext.setFont(new Font("Helvetica", 0, 17));
		cmbYear.setFont(new Font("Helvetica", 0, 17));
		spCalendar.setFont(new Font("Helvetica", 0, 15));
		notesButton.setFont(new Font("Helvetica", 0, 17));

		pnlCalendar.setBackground(new Color(230,220, 240));
		
		//create the calendar and get the real date
		GregorianCalendar calendar = new GregorianCalendar();
		realDay = calendar.get(GregorianCalendar.DAY_OF_MONTH);
		realMonth = calendar.get(GregorianCalendar.MONTH);
		realYear = calendar.get(GregorianCalendar.YEAR);
		currentMonth = realMonth;
		currentYear = realYear;
		
		//Create the calendar
		
		//add the headers
		String[] headers = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
		for (int i=0; i<7; i++){
			dtmCalendar.addColumn(headers[i]);
		}
		//set background
		tblCalendar.getParent().setBackground(tblCalendar.getBackground());
		
		//disable resize and reorder for table headers
		tblCalendar.getTableHeader().setResizingAllowed(false);
		tblCalendar.getTableHeader().setReorderingAllowed(false);
		
		//allow only single cell selection
		tblCalendar.setColumnSelectionAllowed(true);
		tblCalendar.setRowSelectionAllowed(true);
		tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//set row/column count
		tblCalendar.setRowHeight(103);
		dtmCalendar.setColumnCount(7);
		dtmCalendar.setRowCount(6);
		
		//populate the combobox
		//this for loop should be after the preparation of the table
		for (int i=realYear-100; i<=realYear+100; i++) {
			cmbYear.addItem(String.valueOf(i));
		}
		
		//add day numbers to the calendar
		refreshCalendar(realMonth, realYear);
		
		//add double click event to table
		tblCalendar.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			    if (e.getClickCount() == 2) {
			    	JTable target = (JTable)e.getSource();
			    	int row = target.getSelectedRow();
			    	int column = target.getSelectedColumn();
			    	
			    	final int FINAL_DAY;
			    	if (tblCalendar.getValueAt(row, column) == null){
			    		FINAL_DAY = -1;
			    	} else {
			    		FINAL_DAY = (int)tblCalendar.getValueAt(row, column);
			    	}
			    	final int FINAL_MONTH = currentMonth;
			    	final int FINAL_YEAR = currentYear;
			    	if (FINAL_DAY != -1){
				    	new EditEvents(FINAL_DAY, FINAL_MONTH, FINAL_YEAR);
						frame.setVisible(false);
						frame.dispose();
			    	}
			    }
			  }
		});
		
	}
	
	public static void refreshCalendar(int month, int year){
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		int numberOfDays, startOfMonth;
		
		btnPrev.setEnabled(true);
		btnNext.setEnabled(true);
		//disable previous and next buttons when the end of the calendar is reached
		if (month == 0 && year <= realYear-100) {
			btnPrev.setEnabled(false);
		}
		if (month == 11 && year >= realYear+100) {
			btnNext.setEnabled(false);
		}
		
		//refresh the month label and re-center it
		lblMonth.setText(months[month]);
		lblMonth.setBounds(400-lblMonth.getPreferredSize().width/2, 25, 130, 25);
		//select the correct year in the combobox
		cmbYear.setSelectedItem(String.valueOf(year));
		
		//get first day of month and number of days
		GregorianCalendar calendar = new GregorianCalendar(year, month, 1);
		numberOfDays = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		startOfMonth = calendar.get(GregorianCalendar.DAY_OF_WEEK);
		
		//clear table
		for (int i=0; i<6; i++){
			for (int j=0; j<7; j++){
				dtmCalendar.setValueAt(null, i, j);
			}
		}
		
		//draw the calendar
		for (int i=1; i<=numberOfDays; i++){
			int row = new Integer((i + startOfMonth - 2) / 7);
			int column = (i + startOfMonth - 2) % 7;
			dtmCalendar.setValueAt(i, row, column);
		}
		
		//apply the renderer
		tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
	}
	
	private static class tblCalendarRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//this class will change the color of weekend columns
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column){
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			
			//if column is weekend, then paint it, otherwise leave it white
			if (column == 5 || column == 6){
				setBackground(new Color(255, 220, 50));
			} else {
				setBackground(new Color(255, 255, 255));
			}
			
			//if column is current day, then paint it
			if (value != null){
				if (Integer.parseInt(value.toString()) == realDay && currentMonth == realMonth && currentYear == realYear){
					setBackground(new Color(220,150, 255));
				}
			}
			
			// Set different color for every day that has an event
			if (value != null){
				String sqlSelect = "SELECT * FROM Events WHERE month = ? AND year = ?;";
				try (Connection connection = new Models().connect();
				     PreparedStatement statement = connection.prepareStatement(sqlSelect);
				    ) {
					//get day
					int day = Integer.parseInt(value.toString());

					statement.setInt(1, currentMonth);
					statement.setInt(2, currentYear);
					ResultSet rs = null;
					try{
						rs = statement.executeQuery();
						if (rs != null){
							while (rs.next()) {
								if (day == rs.getInt("day") && currentMonth == rs.getInt("month") && currentYear == rs.getInt("year")){
									if (day != realDay){ //color of the cell if there is an event on a day different than the current day
										setBackground(new Color(20,150, 255));
									} else if (day == realDay) { //color of the cell if there is an event on the current day
										setBackground(new Color(10, 230, 20));
									}
								}
							}
						}
					} catch(SQLException e){
						System.out.println("ResultSet exception: " + e.getMessage());
					} finally {
						rs.close();
					}
					
				} catch (SQLException e){
					System.out.println(e.getMessage());
				}
			}

			setBorder(null);
			setForeground(Color.BLACK);
			return this;
		}
	}
	
	private class CalendarButtonClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if (command.equals("btnPrev")) {
				if (currentMonth == 0) {
					currentMonth = 11;
					currentYear -= 1;
				} else {
					currentMonth -= 1;
				}
				refreshCalendar(currentMonth, currentYear);
				
			} else if (command.equals("btnNext")) {
				if(currentMonth == 11){
					currentMonth = 0;
					currentYear += 1;
				} else {
					currentMonth += 1;
				}
				refreshCalendar(currentMonth, currentYear);
			} else if (command.equals("cmbYear")) {
				if (cmbYear.getSelectedItem() != null) {
					String selected = cmbYear.getSelectedItem().toString();
					currentYear = Integer.parseInt(selected);
					refreshCalendar(currentMonth, currentYear);
				}
			}
		}
		
	}
	
	//custom listener for every button in controlPanel
	private class ControlButtonClickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if(command.equals("GoToNotes")){
				new ShowNotes();
				frame.setVisible(false);
				frame.dispose();
			}
		}
		
	}
}
