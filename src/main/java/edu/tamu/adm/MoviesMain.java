package edu.tamu.adm;
import com.mongodb.client.MongoCollection;

import edu.tamu.adm.controller.MapReduce;
import edu.tamu.adm.controller.RecommendMovies;
import edu.tamu.adm.data.DBConnect;

public class MoviesMain {
	public static void main(String args []) {
		//The main class handles function calls to all other classes.
		//A new connection is made
		DBConnect dbDataLoad = new DBConnect();
		dbDataLoad.createDBConnection();
		@SuppressWarnings("rawtypes")
		MongoCollection collection = dbDataLoad.getCollectionOfDatabase(dbDataLoad.database, "movies");
		//Load Data 
		dbDataLoad.WriteIntoMongo(collection);
		//Read Data
		dbDataLoad.ReadFromMongo(collection);
		//Perform MapReduce to get average rating of all movies
		MapReduce mapReduce = new MapReduce();
		mapReduce.performMapReduce(dbDataLoad, collection);
		RecommendMovies recommendMovies = new RecommendMovies();
		recommendMovies.getInputs(dbDataLoad,collection);
	}	
}