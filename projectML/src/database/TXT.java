package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TXT implements FileExtension {
	
	private String fileName;
	public static String[] data = new String[100];
	public Statement statement;
	public Connection connection;
	PreparedStatement ps;	//id
	
	TXT(String fileName,Statement statement, Connection connection){
		this.fileName = fileName;
		this.statement = statement;
		this.connection = connection;
	}
	
	public void readData() {
		String m;	//przechowywanie biezacej linii
        int typeOfContact = 0;	//typ kontaktu -- 0-nieznany; 1-email; 2-phone;3-jabber

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
        	//odczytanie pliku 
            while((m = br.readLine()) != null){
                try {
                	//przypisz rozdzielone przecinkiem dane do data
                	data = m.split(",");
                	//for(int i=0; i<data.length; i++)	System.out.println(data[i]);	//wyswietl wszystkie dane	                	

                	statement = connection.createStatement();
                	//utworzenie zapytania -- dodanie danych do tabeli Customers
                	for(int i=0; i<data.length; i++) if(data[i].isEmpty()) data[i]=null;
            		String custQuery = "INSERT INTO Customers(name,surname,age) " + "VALUES ('" + data[0] + "', '" + data[1] + "', '" + data[2] + "')";	
            		ps = connection.prepareStatement(custQuery, Statement.RETURN_GENERATED_KEYS);
            		//wykonanie zapytania
            		ps.executeUpdate();
            		//klucz g³ówny z customers
            		ResultSet rs = ps.getGeneratedKeys();
            		int idCustomer = 0;
            		if (rs.next())	idCustomer = rs.getInt(1);	//z pierwszej col
            		
            		//pozycja 4 to zawsze pierwszy kontakt, wykonuj dopoki s¹ kontakty   
            		for(int i=4;i<data.length;i++) {
            			//sprawdzenie typu kontaktu 
                		if(data[i].contains("@")) {
                			typeOfContact = 1;
                		}else if ((data[i].matches(".*\\d.*")) && (data[i].length() >= 9)){	//zawiera cyfry
                			typeOfContact = 2;
                		}else	typeOfContact = 0;
                	//utworzenie zapytania -- dodanie danych do tabeli Contacts	
            		String conQuery = "INSERT INTO Contacts(id_customers,type,contact) " + "VALUES ('" + idCustomer + "', '" + typeOfContact + "', '" + data[i] + "')";
            		//wykonanie zapytania
            		statement.executeUpdate(conQuery);
            		}

                }catch(Exception exc){
                    System.out.println("Niepoprawny format: " + exc);
                }
                
            }//end while
        }   catch(IOException exc){
            System.out.println("B³¹d IO" + exc);
        }
	}

}
