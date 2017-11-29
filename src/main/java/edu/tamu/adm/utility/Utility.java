package edu.tamu.adm.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import edu.tamu.adm.data.DBConnect;

public class Utility {

    public void evaluateQuery(DBConnect dbconnect, MongoCollection<Document> collection, float rating, int year, String genres, String email, int type) {
		FindIterable<Document> cursor = null;
		switch(type) {
		case 1:
			
			//Recommend based on movie ratings
			BasicDBObject ratingQuery = new BasicDBObject();
			ratingQuery.put("rating", new BasicDBObject("$gte", rating));
			
			AggregateIterable<Document> iterable = collection.aggregate(
				Arrays.asList(
				new Document("$group", new Document("_id", "$movieTitle").append("rating", new Document("$avg", "$rating"))),
				Aggregates.match(ratingQuery),
				Aggregates.sort(new Document("rating", -1)),
				Aggregates.limit(20)
				)
			);
			System.out.println("\n");
			System.out.println("Displaying top 20 results as per ratings:");
			for (Document doc: iterable) {
				System.out.println("Movie Title: " + doc.get("_id") + "\t Rating: " + doc.get("rating"));
			}	
			break;
			
		case 2:
			
			//Recommend based on release year
	
			BasicDBObject yearQuery = new BasicDBObject();
			yearQuery.put("value.movieYear", new BasicDBObject("$lte", year));
			MongoCollection uniqueMovieCollection = dbconnect.getCollectionOfDatabase(dbconnect.database, "uniqueMovies");
			cursor = uniqueMovieCollection.find(yearQuery).sort(new BasicDBObject("value.movieYear", -1)).limit(20);
			System.out.println("\n");
			System.out.println("Displaying top 20 results as per releasing year:");
			for (Document doc: cursor) {
				System.out.println("Movie Title: " + doc.get("_id") + "\t Release Year: " + ((Double) ((Document) doc.get("value")).get("movieYear")).intValue());
			}
			break;
			
		case 3:
			
			String str = "";
			String[] genreArray = genres.split(",");
	        for(int i = 0; i < (genreArray.length-1); i++) {
	        	str += "(?=.*" + genreArray[i].trim() + ")";
	        	str += "|";
	        }
	        str += "(?=.*" + genreArray[genreArray.length-1].trim() + ")";
	        Pattern regex = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
	        
			dbconnect.ReadUniqueMovies(regex);
			break;
			
		case 4:
			evaluateOnFrequentGenres(dbconnect,collection, email);
			break;
			
		case 5:
			
			//Recommend using similarity matrix
			if(email == "" || email == null) {
				System.out.println("Please enter a valid list of genres.");
				break;
			}
			
			break;
		}	
	}
	
	public void evaluateOnFrequentGenres(DBConnect dbconnect, MongoCollection collection, String email) {
		@SuppressWarnings("unchecked")
		AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(
	            new Document("$match", new Document("email", email)),
	            new Document("$project", new Document("email", 1).append("userName", 1).append("movieTitle", 1).append("rating", 1).append("genres", 1)),
	            new Document("$sort", new Document("rating", -1))));
		
		
		Map<String, Double> genresMap = new HashMap<>();
		String moviegenres = "";
		String[] splitgenres;
		
		for(Document doc : iterable) {
			moviegenres = (String) doc.get("genres");
			splitgenres = moviegenres.split("\\|");
			for(int i = 0; i < splitgenres.length; i++) {
				Double count = genresMap.getOrDefault(splitgenres[i], (double) 0);
				if(count == 0) {
					genresMap.put(splitgenres[i], (double) 1);
				} else {
					genresMap.put(splitgenres[i], count+1);
				}
			}
		}
		
		// Sort the map of genres and their frequencies in descending order to get top 3 genres by frequency of reviewing
		Map<String, Double> commonGenres = Utility.sortByComparator(genresMap);
		moviegenres = "";
		
		Iterator it = commonGenres.entrySet().iterator();
		int cnt = 0;
		
	    while (it.hasNext() && cnt < 3) {
	        Map.Entry entry = (Map.Entry)it.next();
	        moviegenres += entry.getKey();
	        moviegenres += ",";
	        it.remove();
	        cnt ++;
	    }
	    
	    moviegenres = moviegenres.substring(0, moviegenres.length()-1);		
		System.out.println("The top 3 frequent genres are: " + moviegenres);
		
		String str = "";
		String[] genreArray = moviegenres.split(",");
        for(int i = 0; i < (genreArray.length-1); i++) {
        	str += "(?=.*" + genreArray[i].trim() + ")";
        	str += "|";
        }
        str += "(?=.*" + genreArray[genreArray.length-1].trim() + ")";
        Pattern regex = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
		dbconnect.ReadUniqueMovies(regex);	
	}
	
	public void rateMovie(MongoCollection collection, String movie, Float rating, String email, int operation) {
		Scanner sc = new Scanner(System.in);
		FindIterable<Document> cursor = null;
		Document moviecursor = null;
		Document usercursor = null;
		Pattern regex = Pattern.compile(movie, Pattern.CASE_INSENSITIVE);
		Bson filter = Filters.eq("movieTitle", regex);
		BasicDBObject movieQuery = new BasicDBObject();
		movieQuery.put("movieTitle", regex);
		moviecursor = (Document) collection.find(movieQuery).first();
		usercursor = (Document) collection.find(new BasicDBObject("email", email)).first();
		
		if(operation == 1) {
			if(moviecursor != null) {
				BasicDBObject userQuery = new BasicDBObject();
				userQuery.put("email", email);
				userQuery.append("movieTitle", moviecursor.get("movieTitle"));
				cursor = collection.find(userQuery);
				// User has not rated the movie
				if(cursor == null || cursor.first() == null) {
					Document document = new Document();
					document.append("userId", usercursor.get("userId"));
					document.append("userName", usercursor.get("userName"));
					document.append("email", email);
					document.append("movieId", moviecursor.get("movieId"));
					document.append("movieTitle", moviecursor.get("movieTitle"));
					document.append("movieYear", moviecursor.get("movieYear"));
					document.append("genres", moviecursor.get("genres"));
					document.append("rating", rating);
					document.append("timestamp", System.currentTimeMillis()/1000);
					document.append("imdbId", moviecursor.get("imdbId"));
					document.append("tmdbId", moviecursor.get("tmdbId"));
					collection.insertOne(document);
					System.out.println("Movie is rated successfully!");
				} else {
					// User has previously rated the movie
					System.out.println("Initial movie rating is: " + Math.round((double)cursor.first().get("rating")*100.0)/100.0);
					collection.updateOne(cursor.first(), new Document("$set", new Document("rating", rating)));
					System.out.println("Updated movie rating is: " + Math.round((double)cursor.first().get("rating") * 100.0)/100.0);
				}
			} else {
				// Movie not present in the database
				System.out.println("Enter movie release year: ");
				String year = sc.nextLine();
				System.out.println("Enter movie genres separated by |: ");
				String genres = sc.nextLine();
				Document document = new Document();
				document.append("userId", usercursor.get("userId"));
				document.append("userName", usercursor.get("userName"));
				document.append("email", email);
				document.append("movieTitle", movie);
				document.append("movieYear", year);
				document.append("genres", genres);
				document.append("rating", rating);
				document.append("timestamp", System.currentTimeMillis()/1000);
				collection.insertOne(document);
				System.out.println("Created a new entry for the movie. Movie is rated successfully!");
			}
		} else {
			if(moviecursor != null) {
				// User has rated the movie
				BasicDBObject userQuery = new BasicDBObject();
				userQuery.put("email", email);
				userQuery.append("movieTitle", moviecursor.get("movieTitle"));
				cursor = collection.find(userQuery);
				System.out.println("Initial movie rating is: " + Math.round((double)cursor.first().get("rating")*100.0)/100.0);
				collection.updateOne(cursor.first(), new Document("$set", new Document("rating", rating)));
				System.out.println("Updated movie rating is: " + Math.round((double)cursor.first().get("rating")*100.0)/100.0);
			} else if (moviecursor == null) {
				System.out.println("The movie does not exist in the database. Please try again.");
			}
		}
		
	}
	
	
    public boolean validateEmail(MongoCollection collection, String email) {
		Document cursor = null;
		if(email == "" || email == null) {
			System.out.println("Please enter a valid email ID");
			return false;
		}
		if(!email.contains("@") || !email.matches("[a-zA-Z.@]*")) {
			System.out.println("Please enter a valid email format.");
			return false;
		}
		cursor = (Document) collection.find(new BasicDBObject("email", email)).first();
		if (cursor == null) {
			System.out.println("User does not exist in the database. Please check with the administrator.");
			return false;
		}
		return true;
	}
    
    //Method to sort the Map in descending order based on rating
  	public static Map<String, Double> sortByComparator(Map<String, Double> unsortMap) {
        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());
        // Sort the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2)
            {
                return o2.getValue().compareTo(o1.getValue());      
            }
        });
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
  	}

}