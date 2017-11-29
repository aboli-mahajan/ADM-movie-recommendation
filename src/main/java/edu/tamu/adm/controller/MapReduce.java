package edu.tamu.adm.controller;

import org.bson.Document;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;

import edu.tamu.adm.data.DBConnect;

public class MapReduce {

	public void performMapReduce(DBConnect dbDataLoad,MongoCollection collection) {

		// Map reduce function to find average ratings for all movies filtered as per genres
		try {
						
			String mapFunction = "function() { " +
					"emit(this.movieTitle, {rating: this.rating, count : 1, genres: this.genres, movieYear: this.movieYear});}";
			
			String reduceFunction = "function( key, ratings ) { var n = { count: 0, rating: 0, genres: '',movieYear: ''};" +
	                   " for ( var i = 0; i < ratings.length; i ++ ) { n.rating += ratings[i].rating; " + 
	                   " n.count += ratings[i].count; if(n.genres == '' || n.movieYear == '') {n.genres = ratings[i].genres; "
	                   + "n.movieYear = ratings[i].movieYear;}} return n; }";

	        String finalizeFunction = "function( key, values ) { values.avg = values.rating / values.count;" +
	        		"return {rating: values.avg, genres: values.genres, movieYear: values.movieYear};}";
	        
			MapReduceIterable<Document> out = collection.mapReduce(mapFunction, reduceFunction).finalizeFunction(finalizeFunction);
			MapReduceIterable<Document> uniqueMovies = out.collectionName("uniqueMovies");
			for (Document o:out) {
				String movie = (String) o.get("_id");
				break;
			}

		} catch (Exception e) {
			System.out.println("An exception occured while running Map-Reduce.");
			e.printStackTrace();
		}

	}
}