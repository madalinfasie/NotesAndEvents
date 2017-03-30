package EventsPackage;

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
	
	//Create table Events
	public void createTableEvents(){
		//create SQL string
		String sqlCreate = "CREATE TABLE IF NOT EXISTS Events(\n"
				+ "id integer PRIMARY KEY, \n"
				+ "title text, \n"
				+ "body text NOT NULL, \n"
				+ "day integer NOT NULL, \n"
				+ "month integer NOT NULL, \n"
				+ "year integer NOT NULL, \n"
				+ "createDate datetime default current_timestamp"
				+ ");";
//		String sqlDrop = "DROP TABLE Events";
		try(Connection conn = this.connect();
			Statement stmt = conn.createStatement()){
			//execute the statement
			stmt.execute(sqlCreate);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Insert into Events
	public void insertEvents(String title, String body, int day, int month, int year){
		String sqlInsert = "INSERT INTO Events(title, body, day, month, year) VALUES(?, ?, ? , ?, ?);";
		try (Connection conn = this.connect();
				PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
			//set the parameters
			stmt.setString(1, title);
			stmt.setString(2, body);
			stmt.setInt(3, day);
			stmt.setInt(4, month);
			stmt.setInt(5, year);
			//execute insert
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Update Events
	public void updateEvents(String title, String body, int day, int month, int year) {
		String sqlUpdate = "UPDATE Events SET title = ?, body = ?, createDate = ? WHERE day = ? AND month = ? AND year = ?";
		try (Connection conn = this.connect();
				PreparedStatement stmt = conn.prepareStatement(sqlUpdate)){
			//set the parameters
			stmt.setString(1, title);
			stmt.setString(2, body);
			stmt.setString(3, "current_timestamp");
			stmt.setInt(4, day);
			stmt.setInt(5, month);
			stmt.setInt(6, year);

			//execute update
			stmt.executeUpdate();
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void deleteEvents(int day, int month, int year) {
		String sqlDelete = "DELETE FROM Events WHERE day = ? AND month = ? AND year = ?";
		try(Connection conn = this.connect();
				PreparedStatement stmt = conn.prepareStatement(sqlDelete)){
			//set parameters
			stmt.setInt(1, day);
			stmt.setInt(2, month);
			stmt.setInt(3, year);
			//execute delete
			stmt.executeUpdate();
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
}

