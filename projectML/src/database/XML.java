package database;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class XML implements FileExtension {
	private String fileName;
	public Statement statement;
	public Connection connection;
	PreparedStatement ps;	//id
	
	XML(String fileName, Statement statement, Connection connection){
		this.fileName = fileName;
		this.statement = statement;
		this.connection = connection;
	}
	
	public void readData() {
		
		Person p = null;		
		List<String> list = new ArrayList<>();	//lista z kontaktami
		int typeOfContact = 0;
	
		try {
			statement = connection.createStatement();
				//do odczytu -- XMLEventReader
				//do stworzenia XMLEventReader potrzebny XMLInputFactory
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));

			while(eventReader.hasNext()) {
				XMLEvent nextEvent = eventReader.nextEvent();
				if(nextEvent.isStartElement()) {
					StartElement startElement = nextEvent.asStartElement();										
					switch(startElement.getName().getLocalPart()) {
					case "person":
						p = new Person();
						break;						
					case "name":
						nextEvent = eventReader.nextEvent();
						p.setName(nextEvent.asCharacters().getData());
						//System.out.println(p.getName());
						break;						
					case "surname":
						nextEvent = eventReader.nextEvent();
						p.setSurame(nextEvent.asCharacters().getData());
						//System.out.println(p.getSurname());
						break;
					case "age":
						nextEvent = eventReader.nextEvent();
						p.setAge(nextEvent.asCharacters().getData());
						break;			
					case "phone":
						nextEvent = eventReader.nextEvent();
						list.add(nextEvent.asCharacters().getData());
						typeOfContact = 2;
						break;
					case "email":
						nextEvent = eventReader.nextEvent();
						list.add(nextEvent.asCharacters().getData());
						typeOfContact = 1;
						break;
					case "jabber":
						nextEvent = eventReader.nextEvent();
						list.add(nextEvent.asCharacters().getData());
						typeOfContact = 3;
						break;
					case "icq":
						nextEvent = eventReader.nextEvent();
						list.add(nextEvent.asCharacters().getData());	
						typeOfContact = 0;
						break;
					}//end switch
				}
				if (nextEvent.isEndElement()) {
			        EndElement endElement = nextEvent.asEndElement();
			        if (endElement.getName().getLocalPart().equals("person")) {
			        	//dodawanie do tebeli customers
						String custQuery = "INSERT INTO Customers(name,surname,age) " + "VALUES ('" + p.getName() + "', '" + p.getSurname() + "', '" + p.getAge() + "')";
	            		ps = connection.prepareStatement(custQuery, Statement.RETURN_GENERATED_KEYS);
	            		//wykonanie zapytania
	            		ps.executeUpdate();
	            		//klucz g³ówny z customers
	            		ResultSet rs = ps.getGeneratedKeys();
	            		int idCustomer = 0;
	            		if (rs.next())	idCustomer = rs.getInt(1);	//z pierwszej col
           		
		        		//dodawanie do tabeli contacts
		        		for(int i=0; i<list.size(); i++) {
		        			//Contact contact = iterator.next();
		                	//utworzenie zapytania -- dodanie danych do tabeli Contacts	
		            		String conQuery = "INSERT INTO Contacts(id_customers,type,contact) " + "VALUES ('" + idCustomer + "','" + typeOfContact + "', '" + list.get(i) + "')";
		            		//wykonanie zapytania
		            		statement.executeUpdate(conQuery);
		            		System.out.println(list.get(i));

		        		}
		        		list.clear();
			        } 
			    }   
			}//end while
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}		
	}

}
