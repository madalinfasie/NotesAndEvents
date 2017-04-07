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
	
	private JFrame frame;
	private JPanel pnlControl;
	private JPanel pnlNotes;
	private JScrollPane pnlScrollNotes;
	
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
		frame.add(pnlControl, BorderLayout.NORTH);
		frame.add(pnlScrollNotes, BorderLayout.CENTER);
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	protected void createComponents(){		
		//build control panel
		pnlControl = new JPanel(null);
		
		//build notes panel
		pnlNotes = new JPanel();
		pnlNotes.setLayout(new FlowLayout());
		
		//apply a scroll panel
		pnlScrollNotes = new JScrollPane(pnlNotes);
		
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
		pnlControl.setPreferredSize(new Dimension(800, 40));
		
		//set font and text sizes
		eventsButton.setFont(new Font("Helvetica", 0, 17));
		addNoteButton.setFont(new Font("Helvetica", 0, 17));
		
		//set background colors
		pnlControl.setBackground(new Color(230,220, 240));
		pnlNotes.setBackground(new Color(230,220, 240));
		
		//add those buttons to the control panel
		pnlControl.add(eventsButton);
		pnlControl.add(addNoteButton);
		
		// show all notes from database
		String sqlSelect = "SELECT * FROM Notes;";
		try (Connection connection = new Models().connect();
		     PreparedStatement statement = connection.prepareStatement(sqlSelect);
		     ResultSet rs = statement.executeQuery();
		    ) {
			if (rs != null){
				if (!rs.isBeforeFirst()){
					JLabel defaultText = new JLabel("No notes. Click \"Add\" button to add a new one.");
					defaultText.setFont(new Font("Helvetica", 0, 19));
					pnlNotes.add(defaultText);					
				}
				while (rs.next()) {	
				    final int ID_NOTE = rs.getInt("id");

					JTextArea notesTitle = new JTextArea(2, 45);
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
							new AddNotes(ID_NOTE);

							frame.setVisible(false);
							frame.dispose();
					     }
					});
					
					JPanel pnlWrapNotes = new JPanel();
					
					pnlWrapNotes.setBackground(new Color(255, 255, 120));
					pnlWrapNotes.setBorder(BorderFactory.createMatteBorder(5, 12, 5, 5, new Color(200,200, 160)));
					
					pnlWrapNotes.add(notesTitle);
					pnlNotes.add(pnlWrapNotes);
					pnlNotes.setPreferredSize(new Dimension(0, 100 * pnlNotes.getComponents().length));

				}
				 
			} 
		} catch (SQLException e){
			System.out.println(e.getMessage());
		}
		
		pnlScrollNotes.validate();
		pnlScrollNotes.repaint();

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
