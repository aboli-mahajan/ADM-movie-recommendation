package edu.tamu.adm.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import edu.tamu.adm.utility.Utility;

public class MapReduce {

	public void performMapReduce(MongoCollection collection, String[] genres) {

		// Map reduce function to find average ratings for all movies filtered as per genres
		try {
						
			String mapFunction = "function() { " +
					"emit(this.movieTitle, {rating: this.rating, count : 1});}";
			
			String reduceFunction = "function( key, ratings ) { var n = { count: 0, rating: 0};" +
	                   " for ( var i = 0; i < ratings.length; i ++ ) { n.rating += ratings[i].rating; " + 
	                   " n.count += ratings[i].count; } return n; }";

	        String finalizeFunction = "function( key, ratings ) { ratings.avg = ratings.rating / ratings.count;" +
	        		"return ratings.avg;}";
	        String str = "";
	        for(int i = 0; i < (genres.length-1); i++) {
	        	str += "(?=.*" + genres[i].trim() + ")";
	        	str += "|";
	        }
	        str += "(?=.*" + genres[genres.length-1].trim() + ")";
	        Pattern regex = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
	        
	        // Filter for the genres received by the function
	        Bson filter = Filters.eq("genres", regex);
			MapReduceIterable<Document> out = collection.mapReduce(mapFunction, reduceFunction).finalizeFunction(finalizeFunction).filter(filter);
			
			Map<String, Double> map = new HashMap<String, Double>();
			
			//Store MapReduce output to a Map
			for (Document o:out) {
				String movie = (String) o.get("_id");
				Double rate = (Double) o.get("value");
				map.put(movie, rate);
			}
			
			//Sort Map based on rating
			Map<String, Double> sortedMapbyRating = Utility.sortByComparator(map);
			Utility.printMovieList(sortedMapbyRating);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}