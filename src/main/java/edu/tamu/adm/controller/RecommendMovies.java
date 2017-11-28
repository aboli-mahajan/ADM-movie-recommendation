package edu.tamu.adm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import edu.tamu.adm.utility.Utility;

public class RecommendMovies {
	
	@SuppressWarnings("rawtypes")
	public void getInputs(MongoCollection<Document> collection) {
		//Take user input for recommendation criteria
		Scanner sc = new Scanner(System.in);
		float rating;
		int year;
		String genres;
		Boolean option = true;
		String email;
		
		while(option == true) {
			System.out.println("\n\nMENU");
			System.out.println("Enter function:");
			System.out.println("1. Recommend by Rating \n2. Recommend by Release year \n3. Recommend by Genres \n4. Recommend by User's frequent genres \n5. Rate a movie \n6. Exit");
			int type = sc.nextInt();
			rating = -1;
			year = 1900;
			genres = "";
			email = "";
			
			switch(type) {				
			case 1:
				System.out.println("Enter a rating threshold between 0 and 5 (both inclusive):");
				rating = sc.nextFloat();
				evaluateQuery(collection, rating, year, genres, email, type);
				break;
				
			case 2:
				System.out.println("Enter a release year greater than 1900 (results are capped at the entered year):");
				year = sc.nextInt();
				evaluateQuery(collection, rating, year, genres, email, type);
				break;
				
			case 3:
				System.out.println("Enter comma separated genres from the following:");
				System.out.println("Action, Adventure, Animation, Children, Comedy, Crime, Documentary, Drama, Fantasy, Film-Noir, Horror, Musical, Mystery, Romance, Sci-Fi, Thriller, War, Western");
				sc.nextLine();
				genres = sc.nextLine();
				evaluateQuery(collection, rating, year, genres, email, type);
				break;
				
			case 4:
				System.out.println("Enter email address:");
				sc.nextLine();
				email = sc.nextLine();
				if(!validateEmail(collection, email))
					break;
				evaluateQuery(collection, rating, year, genres, email, type);
				break;
				
			case 5:
				sc.nextLine();
				System.out.println("Enter email address:");
				email = sc.nextLine();
				if(!validateEmail(collection, email))
					break;
				System.out.println("Enter movie name:");
				String movie = sc.nextLine();
				System.out.println("Enter rating:");
				float newRating = sc.nextFloat();
				rateMovie(collection, movie, newRating, email);
				break;
				
			case 6:
				option= false;
				break;
			}
		}
		
	}
	
	public void evaluateQuery(MongoCollection<Document> collection, float rating, int year, String genres, String email, int type) {
		FindIterable<Document> cursor = null;
		switch(type) {
		case 1:
			
			//Recommend based on movie ratings
			if(rating < 0 || rating > 5) {
				System.out.println("Please enter a value between 0-5 (both inclusive).");
				return;
			}
			
			BasicDBObject ratingQuery = new BasicDBObject();
			ratingQuery.put("rating", new BasicDBObject("$gte", rating));
			cursor = collection.find(ratingQuery).sort(new BasicDBObject("rating", -1)).limit(20);
			System.out.println("\n");
			System.out.println("Displaying top 20 results as per ratings:");
			for (Document doc: cursor) {
				System.out.println("Movie Title: " + doc.get("movieTitle") + "\t Rating: " + doc.get("rating"));
			}
			break;
			
		case 2:
			
			//Recommend based on release year
			if(year < 1900) {
				System.out.println("Please enter a year greater than 1900.");
				return;
			} else if (year > Calendar.getInstance().get(Calendar.YEAR)) {
				System.out.println("Please enter a year less than " + Calendar.getInstance().get(Calendar.YEAR));
				return;
			}
			
			BasicDBObject yearQuery = new BasicDBObject();
			yearQuery.put("movieYear", new BasicDBObject("$lte", year));
			cursor = collection.find(yearQuery).sort(new BasicDBObject("movieYear", -1)).limit(20);
			System.out.println("\n");
			System.out.println("Displaying top 20 results as per releasing year:");
			for (Document doc: cursor) {
				System.out.println("Movie Title: " + doc.get("movieTitle") + "\t Release Year: " + doc.get("movieYear"));
			}
			break;
			
		case 3:
			
			//Perform Map Reduce of movie ratings based on user's input genres
			if(genres == "" || genres == null || genres.trim().length() == 0) {
				System.out.println("Please enter a valid list of genres.");
				break;
			}
			if(!genres.matches("[a-zA-Z, ]*")) {
				System.out.println("Please enter a valid list of comma separated genres.");
				break;
			}
			
			String str = "";
			String[] genreArray = genres.split(",");
	        for(int i = 0; i < (genreArray.length-1); i++) {
	        	str += "(?=.*" + genreArray[i].trim() + ")";
	        	str += "|";
	        }
	        str += "(?=.*" + genreArray[genreArray.length-1].trim() + ")";
	        Pattern regex = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
	        
	        Bson filter = Filters.eq("genres", regex);
			
			MapReduce mapReduce = new MapReduce();
			mapReduce.performMapReduce(collection, filter);
			break;
			
		case 4:
			
			//Perform Map Reduce of movie ratings for user's most frequently reviewed genres
			if(email == "" || email == null) {
				System.out.println("Please enter a valid list of genres.");
				break;
			}
			
			evaluateOnFrequentGenres(collection, email);
			break;
			
		case 5:
			
			//Recommend using similarity matrix
			if(email == "" || email == null) {
				System.out.println("Please enter a valid list of genres.");
				break;
			}
			
			break;
			
		default:
			System.out.println("Please enter a valid input.");
			break;
		}	
	}
	
	public void evaluateOnFrequentGenres(MongoCollection collection, String email) {
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
        
        Bson filter = Filters.eq("genres", regex);
		
		MapReduce mapReduce = new MapReduce();
		mapReduce.performMapReduce(collection, filter);
	}
	
	public void rateMovie(MongoCollection collection, String movie, Float rating, String email) {
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
			} else {
				// User has previously rated the movie
				collection.updateOne(cursor.first(), new Document("$set", new Document("rating", rating)));
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
		}
	}
	
	public boolean validateEmail(MongoCollection collection, String email) {
		Document cursor = null;
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

}