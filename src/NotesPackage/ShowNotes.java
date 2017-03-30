package NotesPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

import EventsPackage.ShowEvents;

public class ShowNotes {
	
	protected JFrame frame;
	protected JPanel controlPanel;
	protected JPanel notesPanel;
	private JPanel notesWrapPanel;
	JScrollPane notesScrollPanel;
	
	public ShowNotes(){
		buildGui();
	}
	
	public static void main(String[] args){
		// Apply the OS theme to the GUI so that it looks nicer
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		// start the application
		new ShowNotes();
	}
	
	public void buildGui(){
		//build the frame
		frame = new JFrame("NotesAndEvents");
		frame.setSize(800, 800);
		frame.setLayout(new BorderLayout());
		
		//createComponents
		createComponents();
		//add the panels to the frame
		frame.add(controlPanel, BorderLayout.NORTH);
		frame.add(notesScrollPanel, BorderLayout.CENTER);
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	protected void createComponents(){		
		//build control panel
		controlPanel = new JPanel(null);
		
		//build notes panel
		notesPanel = new JPanel();
		notesPanel.setLayout(new FlowLayout());
		
		//apply a scroll panel
		notesScrollPanel = new JScrollPane(notesPanel);
		
		//create the buttons for the control panel
		JButton eventsButton = new JButton("Events");
		JButton addNoteButton = new JButton("Add");
		
		//set a command for each button
		eventsButton.setActionCommand("GoToEvents");
		addNoteButton.setActionCommand("AddNote");
		
		//set the listener
		eventsButton.addActionListener(new ButtonClickListener());
		addNoteButton.addActionListener(new ButtonClickListener());
		
		//set component sizes and positions
		eventsButton.setBounds(10, 5, 75, 30);
		addNoteButton.setBounds(717, 5, 70, 30);
		controlPanel.setPreferredSize(new Dimension(800, 40));
		
		//set font and text sizes
		eventsButton.setFont(new Font("Helvetica", 0, 17));
		addNoteButton.setFont(new Font("Helvetica", 0, 17));
		
		//set background colors
		controlPanel.setBackground(new Color(230,220, 240));
		notesPanel.setBackground(new Color(230,220, 240));
		
		//add those buttons to the control panel
		controlPanel.add(eventsButton);
		controlPanel.add(addNoteButton);
		
		// show all notes from database
		String sqlSelect = "SELECT * FROM Notes;";
		try (Connection connection = new Models().connect();
		     PreparedStatement statement = connection.prepareStatement(sqlSelect);
		     ResultSet rs = statement.executeQuery();
		    ) {
			if (rs != null){
				if (!rs.isBeforeFirst()){
				 	notesWrapPanel = new JPanel();
				 	
					JLabel defaultText = new JLabel("No notes. Click \"Add\" button to add a new one.");
					defaultText.setFont(new Font("Helvetica", 0, 19));
					
					notesWrapPanel.add(defaultText);
					notesPanel.add(notesWrapPanel);
					
					notesWrapPanel.setBackground(notesPanel.getBackground());
					
					notesPanel.setPreferredSize(new Dimension(0, 100 * notesPanel.getComponents().length));
					notesScrollPanel.validate();
					notesScrollPanel.repaint();
					
				}
				while (rs.next()) {
					int id = 0;
					try {
						id = rs.getInt("id");
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					//the id for EditNotes in the mouse event must be a constant
					final int FINALID = id;
					
					JTextArea notesTitle = new JTextArea(2, 20);
					notesTitle.setText(rs.getString("title"));
					notesTitle.setWrapStyleWord(true);
					notesTitle.setLineWrap(true);
				    notesTitle.setOpaque(false);
				    notesTitle.setEditable(false);
				    notesTitle.setFocusable(false);
				    notesTitle.setBackground(UIManager.getColor("Label.background"));
					notesTitle.setFont(new Font("Helvetica", 0, 18));
				    notesTitle.setBorder(UIManager.getBorder("Label.border"));
				    notesTitle.setBounds(5, 5, 750, 20);
				    
				    // Go to edit note
				    notesTitle.addMouseListener(new MouseAdapter(){
						@Override
					     public void mousePressed(MouseEvent e) {
							new EditNotes(FINALID);

							frame.setVisible(false);
							frame.dispose();
					     }
					});
					
					notesWrapPanel = new JPanel();

					notesWrapPanel.add(notesTitle);
					// Go to edit note
					notesWrapPanel.addMouseListener(new MouseAdapter(){
						@Override
					     public void mousePressed(MouseEvent e) {
							new EditNotes(FINALID);
							
							frame.setVisible(false);
							frame.dispose();
					     }
					});
					notesWrapPanel.setBackground(new Color(255, 255, 120));
					notesWrapPanel.setBorder(BorderFactory.createMatteBorder(5, 12, 5, 5, new Color(200,200, 160)));
					
					notesPanel.add(notesWrapPanel);
					
					notesPanel.setPreferredSize(new Dimension(0, 100 * notesPanel.getComponents().length));
					notesScrollPanel.validate();
					notesScrollPanel.repaint();
				}
				 
			} 
		} catch (SQLException e){
			System.out.println(e.getMessage());
		}
		

	}
	
	//custom listener for every button in controlPanel
	private class ButtonClickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if(command.equals("GoToEvents")){
				new ShowEvents();
				frame.setVisible(false);
				frame.dispose();
			} else if(command.equals("AddNote")){
				new AddNotes();
				frame.setVisible(false);
				frame.dispose();
			}
		}
		
	}
	
	
}
