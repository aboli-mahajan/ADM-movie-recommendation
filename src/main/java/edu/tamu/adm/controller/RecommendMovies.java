package edu.tamu.adm.controller;

import java.util.Scanner;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class RecommendMovies {
	
	@SuppressWarnings("rawtypes")

	public void getInputs(MongoCollection collection) {
		//Take user input for recommendation criteria
		Scanner sc = new Scanner(System.in);
		System.out.println("MENU");
		System.out.println("Enter recommendation field:");
		System.out.println("1. Rating \n2. Release year \n");
		int type = sc.nextInt();
		float rating = -1;
		int year = 1900;
		
		switch(type) {				
			case 1:
				System.out.println("Enter rating threshold greater than 0");
				rating = sc.nextFloat();
				break;
				
			case 2:
				System.out.println("Enter movie release year threshold greater than 1990");
				year = sc.nextInt();
				break;
		}
		
		FindIterable<Document> cursor = null;
		
		if(rating != -1) {	
			//Recommend based on movie ratings
			BasicDBObject ratingQuery = new BasicDBObject();
			ratingQuery.put("rating", new BasicDBObject("$gte", rating));
			cursor = collection.find(ratingQuery).sort(new BasicDBObject("rating", -1)).limit(20);
			System.out.println("\n");
			System.out.println("Displaying top 20 results:");
			for (Document doc: cursor) {
				System.out.println("Movie Title: " + doc.get("movieTitle") + "\t Rating: " + doc.get("rating"));
			}
		}else if(year >= 1990) {
			//Recommend based on release year
			BasicDBObject yearQuery = new BasicDBObject();
			yearQuery.put("movieYear", new BasicDBObject("$lte", year));
			cursor = collection.find(yearQuery).sort(new BasicDBObject("movieYear", -1)).limit(20);
			System.out.println("\n");
			System.out.println("Displaying top 20 results:");
			for (Document doc: cursor) {
				System.out.println("Movie Title: " + doc.get("movieTitle") + "\t Release Year: " + doc.get("movieYear"));
			}
		}else {
			System.out.println("Please enter a valid input.");
		}
		
	}

}
