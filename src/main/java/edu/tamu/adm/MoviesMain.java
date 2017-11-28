package edu.tamu.adm;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.tamu.adm.controller.RecommendMovies;
import edu.tamu.adm.data.DBConnect;

public class MoviesMain {
	public static void main(String args []) {
		//The main class handles function calls to all other classes.
		//A new connection is made
		DBConnect dbDataLoad = new DBConnect();
		MongoDatabase db = dbDataLoad.createDBConnection();
		@SuppressWarnings("rawtypes")
		MongoCollection collection = dbDataLoad.getCollectionOfDatabase(db, "movies");
		//Load Data 
		dbDataLoad.WriteIntoMongo(collection);
		//Read Data
		dbDataLoad.ReadFromMongo(collection);
		RecommendMovies recommendMovies = new RecommendMovies();
		recommendMovies.getInputs(collection);	
	}
	
}