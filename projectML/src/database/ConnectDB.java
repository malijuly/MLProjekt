package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {
	
		public Connection connectDB() {

		Connection connection = null;
		
		try {
			
			//register driver class
			Class.forName("org.postgresql.Driver");
			//uzyskanie polaczenia -- port 5432; nazwa DB; username; haslo
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","mlipiec");
			
			if(connection != null) {
				System.out.println("Connection OK");
			}else {
				System.out.println("Connection NOK");
			}			
		}catch(Exception e) {
			System.out.println(e);
		}
		
		return connection;
	}//end connDB

}
