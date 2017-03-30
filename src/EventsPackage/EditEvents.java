package EventsPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class EditEvents {
	private int selectedDay;
	private int selectedMonth;
	private int selectedYear;
	protected JPanel controlPanel;
	protected JPanel addPanel;
	protected JFrame frame;
	private JTextArea textArea;
	
	public EditEvents(int day, int month, int year){
		selectedDay = day;
		selectedMonth = month;
		selectedYear = year;
		buildGui();
	}
	
	protected void buildGui(){
		//build the frame
		frame = new JFrame("NotesAndEvents");
		frame.setSize(800, 800);
		frame.setLayout(new BorderLayout());
		
		//createComponents
		createComponents();
		
		//add the panels to the frame
		frame.add(controlPanel, BorderLayout.NORTH);
		frame.add(addPanel);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	protected void createComponents(){
		//build control panel
		controlPanel = new JPanel(null);
		
		//build notes panel
		addPanel = new JPanel();
		addPanel.setBackground(Color.lightGray);
		addPanel.setLayout(new FlowLayout());
		//create the buttons for the control panel
		JButton backEventButton = new JButton("Cancel");
		JButton saveEventButton = new JButton("Save");
		JButton deleteEventButton = new JButton("Delete");
		
		//selected date label
		JLabel selectedDate = new JLabel();
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		selectedDate.setText(selectedDay + " - " + months[selectedMonth] + " - " + selectedYear);
		
		//set delete button invisible by default
		deleteEventButton.setVisible(false);
		
		//set a command for each button
		backEventButton.setActionCommand("BackEvents");
		deleteEventButton.setActionCommand("Delete");
		
		//set the listener
		backEventButton.addActionListener(new ButtonClickListener());
		saveEventButton.addActionListener(new ButtonClickListener());
		deleteEventButton.addActionListener(new ButtonClickListener());
		
		//set component sizes and positions
		saveEventButton.setBounds(380-saveEventButton.getPreferredSize().width/2, 5, 100, 30);
		backEventButton.setBounds(12, 5, 75, 30);
		deleteEventButton.setBounds(714, 5, 75, 30);
		selectedDate.setBounds(394-selectedDate.getPreferredSize().width/2, 36, 200, 30);
		controlPanel.setPreferredSize(new Dimension(800, 70));
		
		//set font and text sizes
		saveEventButton.setFont(new Font("Helvetica", 0, 17));
		backEventButton.setFont(new Font("Helvetica", 0, 17));
		deleteEventButton.setFont(new Font("Helvetica", 0, 17));
		selectedDate.setFont(new Font("Helvetica", 0, 17));
		
		//set colors
		controlPanel.setBackground(new Color(230,220, 240));
		addPanel.setBackground(new Color(230,220, 240));
		
		//add those buttons to the control panel
		controlPanel.add(selectedDate);
		controlPanel.add(saveEventButton);
		controlPanel.add(deleteEventButton);
		controlPanel.add(backEventButton);
		
		// create text area object
		textArea = new JTextArea("",7,20);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Helvetica", 0, 20));
		
		//create table if it doesn't exist
		Models model = new Models();
		model.createTableEvents();
		
		//set the text of textArea to the text from database
		String sqlSelect = "SELECT body FROM Events WHERE day = ? AND month = ? AND year = ?;";
		try (Connection connection = new Models().connect();
		     PreparedStatement statement = connection.prepareStatement(sqlSelect);
		    ) {
			statement.setInt(1, selectedDay);
			statement.setInt(2, selectedMonth);
			statement.setInt(3, selectedYear);
			ResultSet rs = null;
			saveEventButton.setActionCommand("Add");
			try{
				rs = statement.executeQuery();
				if (rs != null){
					while (rs.next()) {
						textArea.setText(rs.getString("body"));
						saveEventButton.setActionCommand("Edit");
						//when editing an event, show the delete button
						deleteEventButton.setVisible(true);
					}
				}
			} catch(SQLException e){
				System.out.println("ResultSet exception: " + e.getMessage());
			} finally {
				rs.close();
			}
		} catch (SQLException e){
			System.out.println("Connection or statement exception: " + e.getMessage());
		}
		
		
		//Put the editor pane in a scroll pane.
		JScrollPane textScrollPane = new JScrollPane(textArea);
		textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.setPreferredSize(new Dimension(775, 710));
		textScrollPane.setMinimumSize(new Dimension(10, 10));
		addPanel.add(textScrollPane);
	}
	
	//custom listener for every button in controlPanel
	private class ButtonClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if(command.equals("BackEvents")){
				new ShowEvents();
				frame.setVisible(false);
				frame.dispose();
			} else if(command.equals("Add")){
				String body = textArea.getText();
				final int MAX_CHAR = 100;
				String title;
				if (body.length() > MAX_CHAR){
					title = body.substring(0, MAX_CHAR) + "...";
				} else {
					title = body.substring(0, body.length());
				}
				
				Models model = new Models();
				model.insertEvents(title, body, selectedDay, selectedMonth, selectedYear);
				
				new ShowEvents();
				frame.setVisible(false);
				frame.dispose();
			} else if (command.equals("Edit")) {
				String body = textArea.getText();
				final int MAX_CHAR = 100;
				String title;
				if (body.length() > MAX_CHAR){
					title = body.substring(0, MAX_CHAR) + "...";
				} else {
					title = body.substring(0, body.length());
				}
				
				Models model = new Models();
				model.updateEvents(title, body, selectedDay, selectedMonth, selectedYear);
				
				new ShowEvents();
				frame.setVisible(false);
				frame.dispose();
			} else if(command.equals("Delete")) {
				Models model = new Models();
				model.deleteEvents(selectedDay, selectedMonth, selectedYear);
				
				new ShowEvents();
				frame.setVisible(false);
				frame.dispose();
			}
		}
		
	}
}
