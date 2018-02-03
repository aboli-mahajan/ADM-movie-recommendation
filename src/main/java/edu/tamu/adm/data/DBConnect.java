package edu.tamu.adm.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.tamu.adm.util.Utility;

public class DBConnect {

	private static final String BLANK = "";
	private static final String MOVIES_DB = "moviesDB";
	private static final String MOVIES = "movies";
	private static final String SAMPLE_MOVIES_CSV = "data/movies_data.csv";
	MongoClient mongoClient = null;
	public MongoDatabase database = null;
	MongoCollection<Document> collection;
	

	// Connecting to MongoDB
	public MongoDatabase createDBConnection() {
		if(this.mongoClient == null) {
			System.out.println("\nAttempting to connect to the mongodb server.\n");
			this.mongoClient = new MongoClient("localhost", 27017);
		}
		if(this.database == null) {
			this.database = mongoClient.getDatabase(MOVIES_DB);
			System.out.println("\nConnection established.\n");
		}
		return this.database;
	}

	@SuppressWarnings("rawtypes")
	public MongoCollection getCollectionOfDatabase(MongoDatabase database, String collectionName) {
		collection = database.getCollection(collectionName);
		return collection;
	}

	// Close Mongo Connection
	public void CloseMongo() {
		System.out.println("\nClosing database connection.\n");
		mongoClient.close();
	}

	@SuppressWarnings("rawtypes")
	public void WriteIntoMongo(MongoCollection collection) {
		// fetching CSV file
		String csvFile = SAMPLE_MOVIES_CSV;
		String line = BLANK;
		collection.drop();
		System.out.println("\nWriting 100000 documents to the database.\n");
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			@SuppressWarnings("unchecked")
			List<Document> docs = new ArrayList<>();
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] attributes = line.split(",");
				Document document = new Document();
				document.append("userId", attributes[0].trim());
				document.append("userName", attributes[1].trim());
				document.append("email", attributes[2].trim());
				document.append("movieId", attributes[3].trim());
				document.append("movieTitle", attributes[4].trim());
				document.append("movieYear", Integer.parseInt(attributes[5].trim()));
				document.append("genres", attributes[6].trim());
				document.append("rating", Float.parseFloat(attributes[7].trim()));
				document.append("timestamp", Integer.parseInt(attributes[8].trim()));
				document.append("imdbId", Integer.parseInt(attributes[9].trim()));
				document.append("tmdbId", Integer.parseInt(attributes[10].trim()));
				docs.add(document);
			}
			collection.insertMany(docs);
			System.out.println("\nAll records inserted!\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void ReadFromMongo(MongoCollection collection) {
		//Reading from the database
		System.out.println("\nReading the movie titles and ratings for first 20 records:\n");
		FindIterable<Document> iterator = collection.find().limit(20);
		Utility.print(iterator, 1);
		System.out.println("\nAll records read!\n");
	}
	
	public void ReadUniqueMovies(Pattern regex, int prefer) {
		MongoCollection collection2 = getCollectionOfDatabase(database, "uniqueMovies");
		BasicDBObject Query = new BasicDBObject();
		Query.put("value.genres", regex);
		switch(prefer) {
		case 1: 
				Query.append("value.movieYear", new BasicDBObject("$lte", Calendar.getInstance().get(Calendar.YEAR)).append("$gt", Calendar.getInstance().get(Calendar.YEAR)-5));
				break;
		case 2: 
				Query.append("value.movieYear", new BasicDBObject("$lt", 2000).append("$gte", 1900));
				break;
		default:
				break;
		}
		
		FindIterable<Document> iterator = collection2.find(Query).sort(new BasicDBObject("value.rating",-1).append("value.movieYear", -1)).limit(20);
		Utility.print(iterator, 3);
	}
}