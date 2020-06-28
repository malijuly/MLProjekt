package database;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class Demo {

	public static void main(String[] args) {
		
		ConnectDB c = new ConnectDB();
		c.connectDB();

		Connection connection = null;
		Statement statement = null;
		connection = c.connectDB();
		
		FileExtension f;
		String fileName;
		boolean program = true;

		while(true) {
		//odczytanie z klawiatury
		System.out.println("Podaj nazwê pliku z rozszerzeniem.");
		Scanner s = new Scanner(System.in);
		fileName = s.nextLine();
		
		TXT txt = new TXT(fileName, statement, connection);
		XML xml = new XML(fileName, statement, connection);
        
		
	try(BufferedReader br = new BufferedReader(new FileReader(fileName))){    	
        	String check = br.readLine();
        	if(check.contains(","))	{	//csv format
        		f = txt;
        		f.readData();
        	}
        	else if(check.contains("<")) {	//xml format
        		f = xml;
        		f.readData();
        	}

      }catch(IOException exc){
    	  	System.out.println("B³¹d IO" + exc);    	
      }
		
		}

	}

}
