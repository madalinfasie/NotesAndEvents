package NotesPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class Models {
	
	//Start connection
	public Connection connect(){
		Connection conn = null;
		try{
			//start a connection
			String url = "jdbc:sqlite:/home/madalin/Workspace/Eclipse/WORK/NotesAndEvents/src/NotesAndEventsDB.db";
			conn = DriverManager.getConnection(url);
					
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}	

		return conn;
	}
	
	//Create table Notes
	public void createTableNotes(){
		//create SQL string
		String sqlCreate = "CREATE TABLE IF NOT EXISTS Notes(\n"
				+ "id integer PRIMARY KEY, \n"
				+ "title text, \n"
				+ "body text NOT NULL, \n"
				+ "createDate datetime default current_timestamp"
				+ ");";
		try(Connection conn = this.connect();
			Statement stmt = conn.createStatement()){
			//execute the statement
			stmt.execute(sqlCreate);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Insert into Notes
	public void insertNotes(String title, String body){
		String sqlInsert = "INSERT INTO Notes(title, body) VALUES(?, ?)";
		try (Connection conn = this.connect();
				PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
			//set the parameters
			stmt.setString(1, title);
			stmt.setString(2, body);
			//execute insert
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Update Notes
	public void updateNotes(int id, String title, String body) {
		String sqlUpdate = "UPDATE Notes SET title = ?, body = ?, createDate = ? WHERE id = ?";
		try (Connection conn = this.connect();
				PreparedStatement stmt = conn.prepareStatement(sqlUpdate)){
			//set the parameters
			stmt.setString(1, title);
			stmt.setString(2, body);
			stmt.setString(3, "current_timestamp");
			stmt.setInt(4, id);
			//execute update
			stmt.executeUpdate();
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void deleteNotes(int id) {
		String sqlDelete = "DELETE FROM Notes WHERE id = ?";
		try(Connection conn = this.connect();
				PreparedStatement stmt = conn.prepareStatement(sqlDelete)){
			//set parameters
			stmt.setInt(1, id);
			//execute delete
			stmt.executeUpdate();
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
}
