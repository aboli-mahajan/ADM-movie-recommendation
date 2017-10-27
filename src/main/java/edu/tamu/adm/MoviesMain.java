package edu.tamu.adm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MoviesMain {
	public static void main(String args []) {
		
		//Connecting to MongoDB
		System.out.println("\nAttempting to connect to the mongodb server.\n");		
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("moviesDB");
		MongoCollection<Document> collection = database.getCollection("movies");
		System.out.println("\nConnection established.\n");
		System.out.println("\nConnection established.\n");
		//Writing 30000 movie documents into DB
		WriteIntoMongo(mongoClient, collection);
		
		//Reading the movie titles and ratings for first 10 records from database
		ReadFromMongo(collection);
	    
		//Closing the connection 
	    CloseMongo(mongoClient);
	    
	}
	
	
	public static void CloseMongo(MongoClient mongoClient) {
		System.out.println("\nClosing database connection.\n");
	    mongoClient.close();
	}
	
	public static  void WriteIntoMongo(MongoClient mongoClient, MongoCollection collection) {
		//fetching CSV file
		String csvFile = "data/sample_movies.csv";
	    String line = "";
	    
	    System.out.println("\nWriting 30000 documents to the database.\n");
	    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	    	@SuppressWarnings("unchecked")
			List <Document> docs = new ArrayList<>();
	    	line = br.readLine();
	        while ((line = br.readLine()) != null) {
	               String[] attributes = line.split(",");
	               Document document = new Document();
	               document.append("userId", attributes[0]);
	               document.append("movieId", attributes[1]);
	               document.append("movieTitle", attributes[2]);
	               document.append("genres", attributes[3]);
	               document.append("rating", attributes[4]);
	               document.append("tags", attributes[5]);
	               document.append("timestamp", attributes[6]);
	               document.append("imdbId", attributes[7]);
	               document.append("tmdbId", attributes[8]);
	               docs.add(document);
	        }
	        collection.insertMany(docs);
	        System.out.println("\nAll records inserted!\n");
	
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void ReadFromMongo(MongoCollection collection) {
		System.out.println("\nReading the movie titles and ratings for first 10 records:\n");
	    FindIterable <Document> iterator = collection.find().limit(10);
	    for(Document doc: iterator) {
	    	System.out.println("Movie Title: " + doc.get("movieTitle") + "\t Rating: " + doc.get("rating"));
	    }
	    System.out.println("\nAll records read!\n");
		
	}
		
}
