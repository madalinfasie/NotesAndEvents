package NotesPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class AddNotes{	

	public AddNotes(){
		buildGui();
	}

	protected JPanel controlPanel;
	protected JPanel addPanel;
	protected JFrame frame;
	private JTextArea textArea;
	
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
		JButton backNoteButton = new JButton("Cancel");
		JButton saveNoteButton = new JButton("Save");
		
		//set a command for each button
		backNoteButton.setActionCommand("BackNote");
		saveNoteButton.setActionCommand("Save");
		
		//set the listener
		backNoteButton.addActionListener(new ButtonClickListener());
		saveNoteButton.addActionListener(new ButtonClickListener());
		
		//set component sizes and positions
		saveNoteButton.setBounds(380-saveNoteButton.getPreferredSize().width/2, 5, 100, 30);
		backNoteButton.setBounds(12, 5, 75, 30);
		controlPanel.setPreferredSize(new Dimension(800, 40));
		
		//set font and text sizes
		saveNoteButton.setFont(new Font("Helvetica", 0, 17));
		backNoteButton.setFont(new Font("Helvetica", 0, 17));
		
		//set colors
		controlPanel.setBackground(new Color(230,220, 240));
		addPanel.setBackground(new Color(230,220, 240));
		
		//add those buttons to the control panel
		controlPanel.add(saveNoteButton);
		controlPanel.add(backNoteButton);
		
		// create text area object
		textArea = new JTextArea("",7,20);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Helvetica", 0, 20));
		//Put the editor pane in a scroll pane.
		JScrollPane textScrollPane = new JScrollPane(textArea);
		textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.setPreferredSize(new Dimension(775, 744));
		textScrollPane.setMinimumSize(new Dimension(10, 10));
		addPanel.add(textScrollPane);
	}
	
	//custom listener for every button in controlPanel
	private class ButtonClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if(command.equals("BackNote")){
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
				model.createTableNotes();
				model.insertNotes(title, body);
				
				new ShowNotes();
				frame.setVisible(false);
				frame.dispose();
			}
		}
		
	}
}
