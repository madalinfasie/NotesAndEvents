package NotesPackage;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class AddNotes{	

	private JPanel pnlControl;
	private JPanel pnlNote;
	private JFrame frame;
	private JTextArea textArea;
	private int id;
	
	public AddNotes(){
		//add note constructor
		id = -1;
		buildGui();
	}
	
	public AddNotes(int note_id){
		//edit note constructor
		id = note_id;
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
		frame.add(pnlControl, BorderLayout.NORTH);
		frame.add(pnlNote);
		
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	protected void createComponents(){
		
		//build control panel
		pnlControl = new JPanel(null);
		
		//build notes panel
		pnlNote = new JPanel();
		pnlNote.setBackground(Color.lightGray);
		pnlNote.setLayout(new FlowLayout());
		//create the buttons for the control panel
		JButton btnCancelNote = new JButton("Cancel");
		JButton btnSaveNote = new JButton("Save");
		JButton btnDeleteNote = new JButton("Delete");
		
		//set a command for each button
		btnCancelNote.setActionCommand("Cancel");
		btnSaveNote.setActionCommand("Save");
		btnDeleteNote.setActionCommand("Delete");
		
		//set the listener
		btnCancelNote.addActionListener(new ButtonClickListener());
		btnSaveNote.addActionListener(new ButtonClickListener());
		btnDeleteNote.addActionListener(new ButtonClickListener());
		
		//set component sizes and positions
		btnSaveNote.setBounds(380-btnSaveNote.getPreferredSize().width/2, 5, 100, 30);
		btnCancelNote.setBounds(12, 5, 75, 30);
		btnDeleteNote.setBounds(690, 5, 90, 30);
		
		pnlControl.setPreferredSize(new Dimension(800, 40));
		
		//set font and text sizes
		btnSaveNote.setFont(new Font("Helvetica", 0, 17));
		btnCancelNote.setFont(new Font("Helvetica", 0, 17));
		btnDeleteNote.setFont(new Font("Helvetica", 0, 17));

		//set colors
		pnlControl.setBackground(new Color(230,220, 240));
		pnlNote.setBackground(new Color(230,220, 240));
		
		//add those buttons to the control panel
		pnlControl.add(btnSaveNote);
		pnlControl.add(btnCancelNote);
		//add the delete button only if we edit a note
		if (id != -1){
			pnlControl.add(btnDeleteNote);
		}
		
		// create text area object
		textArea = new JTextArea("",7,20);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Helvetica", 0, 20));
		
		//if we edit the note, set the text for textArea
		if(id!=-1){
			String sql="SELECT body FROM Notes WHERE id=?";
			try(Connection conn= new Models().connect();
					PreparedStatement pstmt= conn.prepareStatement(sql)){
				pstmt.setInt(1, id);
				try(ResultSet rs= pstmt.executeQuery()){
					if(rs!=null)
						while(rs.next()){
							textArea.setText(rs.getString("body"));
						}
				}catch(SQLException ex){
					System.out.println(ex.getMessage());
				}
			}catch(SQLException e){
				System.out.println(e.getMessage());
			}
		}
		
		//Put the editor pane in a scroll pane.
		JScrollPane textScrollPane = new JScrollPane(textArea);
		textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.setPreferredSize(new Dimension(775, 744));
		textScrollPane.setMinimumSize(new Dimension(10, 10));
		pnlNote.add(textScrollPane);
	}
	
	//custom listener for every button in controlPanel
	private class ButtonClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if(command.equals("Cancel")){
				new ShowNotes();
				frame.setVisible(false);
				frame.dispose();
			} else if(command.equals("Save")){
				String body = textArea.getText();
				final int MAX_CHAR = 100;
				String title;
				
				if (body.length() > MAX_CHAR){
					title = body.substring(0, MAX_CHAR) + "...";
				} else {
					title = body.substring(0, body.length());
				}
				
				Models model = new Models();
				if(id == -1){
					model.createTableNotes();
					model.insertNotes(title, body);
				}else{
					model.updateNotes(id, title, body);	
				}
				new ShowNotes();
				frame.setVisible(false);
				frame.dispose();
			} else if(command.equals("Delete")){
				Models model = new Models();
				model.deleteNotes(id);
				new ShowNotes();
				frame.setVisible(false);
				frame.dispose();
			}
		}
		
	}
}
