package edu.tamu.adm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

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
			System.out.println("Enter recommendation field:");
			System.out.println("1. Rating \n2. Release year \n3. Genres \n4. User's frequent genres \n5. Exit");
			int type = sc.nextInt();
			rating = -1;
			year = 1900;
			genres = "";
			email = "";
			
			switch(type) {				
			case 1:
				System.out.println("Enter a rating threshold greater than 0:");
				rating = sc.nextFloat();
				evaluateQuery(collection, rating, year, genres, email);
				break;
				
			case 2:
				System.out.println("Enter a release year greater than 1900 (results are capped at the entered year):");
				year = sc.nextInt();
				evaluateQuery(collection, rating, year, genres, email);
				break;
				
			case 3:
				System.out.println("Enter comma separated genres from the following:");
				System.out.println("Action, Adventure, Animation, Children, Comedy, Crime, Documentary, Drama, Fantasy, Film-Noir, Horror, Musical, Mystery, Romance, Sci-Fi, Thriller, War, Western");
				sc.nextLine();
				genres = sc.nextLine();
				evaluateQuery(collection, rating, year, genres, email);
				break;
				
			case 4:
				System.out.println("Enter email address:");
				// TBD: verify existence of email
				sc.nextLine();
				email = sc.nextLine();
				evaluateQuery(collection, rating, year, genres, email);
				break;
				
			case 5:
				option = false;
				break;
			}
		}
		
	}
	
	public void evaluateQuery(MongoCollection<Document> collection, float rating, int year, String genres, String email) {
		FindIterable<Document> cursor = null;
		if(rating != -1) {
			
			//Recommend based on movie ratings
			BasicDBObject ratingQuery = new BasicDBObject();
			ratingQuery.put("rating", new BasicDBObject("$gte", rating));
			cursor = collection.find(ratingQuery).sort(new BasicDBObject("rating", -1)).limit(20);
			System.out.println("\n");
			System.out.println("Displaying top 20 results as per ratings:");
			for (Document doc: cursor) {
				System.out.println("Movie Title: " + doc.get("movieTitle") + "\t Rating: " + doc.get("rating"));
			}
			
		}else if(year > 1900) {
			
			//Recommend based on release year
			BasicDBObject yearQuery = new BasicDBObject();
			yearQuery.put("movieYear", new BasicDBObject("$lte", year));
			cursor = collection.find(yearQuery).sort(new BasicDBObject("movieYear", -1)).limit(20);
			System.out.println("\n");
			System.out.println("Displaying top 20 results as per releasing year:");
			for (Document doc: cursor) {
				System.out.println("Movie Title: " + doc.get("movieTitle") + "\t Release Year: " + doc.get("movieYear"));
			}
			
		}else if(genres != "") {
			
			//Perform Map Reduce of movie ratings based on user's input genres
			MapReduce mapReduce = new MapReduce();
			mapReduce.performMapReduce(collection, genres.split(","));
			
		}else if(email != ""){
			
			//Perform Map Reduce of movie ratings for user's most frequently reviewed genres
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
			
			// Sort the map of genres and their frequencies in descending order
			Map<String, Double> commonGenres = Utility.sortByComparator(genresMap);
			moviegenres = "";
			
			Iterator it = commonGenres.entrySet().iterator();
			int cnt = 0;
			// Get the top 3 genres by frequency of reviewing
		    while (it.hasNext() && cnt < 3) {
		        Map.Entry entry = (Map.Entry)it.next();
		        moviegenres += entry.getKey();
		        moviegenres += ",";
		        it.remove();
		        cnt ++;
		    }
		    
		    moviegenres = moviegenres.substring(0, moviegenres.length()-1);
			
			System.out.println("The top 3 frequent genres are: " + moviegenres);
			
			// Send the 3 frequent genres to Map-Reduce function
			MapReduce mapReduce = new MapReduce();
			mapReduce.performMapReduce(collection, moviegenres.split(","));
			
		} else {
			
			System.out.println("Please enter a valid input.");
		}
	}

}