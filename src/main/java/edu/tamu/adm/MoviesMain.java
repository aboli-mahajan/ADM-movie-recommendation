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
		//Movie collection is loaded in a variable 
		MongoCollection collection = dbDataLoad.getCollectionOfDatabase(db);
		//Data is loaded into the database
		dbDataLoad.WriteIntoMongo(collection);
		//Data is read from the database
		dbDataLoad.ReadFromMongo(collection);
		//Show top movie recommendations
		RecommendMovies recommendMovies = new RecommendMovies();
		recommendMovies.getInputs(collection);
	
	}
	
}