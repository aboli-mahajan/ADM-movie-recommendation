package edu.tamu.adm.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.MapReduceAction;

import edu.tamu.adm.utility.Utility;

public class MapReduce {

	public void performMapReduce(MongoCollection collection, Bson filter) {

		// Map reduce function to find average ratings for all movies filtered as per genres
		try {
						
			String mapFunction = "function() { " +
					"emit(this.movieTitle, {rating: this.rating, count : 1, genres: this.genres});}";
			
			String reduceFunction = "function( key, ratings ) { var n = { count: 0, rating: 0, genres: ''};" +
	                   " for ( var i = 0; i < ratings.length; i ++ ) { n.rating += ratings[i].rating; " + 
	                   " n.count += ratings[i].count; if(n.genres == '') {n.genres = ratings[i].genres}} return n; }";

	        String finalizeFunction = "function( key, values ) { values.avg = values.rating / values.count;" +
	        		"return {rating: values.avg, genres: values.genres};}";
	        
			MapReduceIterable<Document> out = collection.mapReduce(mapFunction, reduceFunction).finalizeFunction(finalizeFunction);
			Map<String, Double> map = new HashMap<String, Double>();
			
			for (Document o:out.filter(filter)) {
				String movie = (String) o.get("_id");
				Document value = (Document) o.get("value");
				Double rating = (Double) value.get("rating");
				map.put(movie, rating);
			}
			
			Map<String, Double> sortedMapbyRating = Utility.sortByComparator(map);
			Utility.printMovieList(sortedMapbyRating);

		} catch (Exception e) {
			System.out.println("An exception occured while running Map-Reduce.");
			e.printStackTrace();
		}

	}
}