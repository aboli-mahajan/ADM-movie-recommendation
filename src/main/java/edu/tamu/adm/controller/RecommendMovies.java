package edu.tamu.adm.controller;

import java.util.Calendar;
import java.util.Scanner;
import com.mongodb.client.MongoCollection;
import edu.tamu.adm.data.DBConnect;
import edu.tamu.adm.utility.Utility;

public class RecommendMovies {
	
	@SuppressWarnings("rawtypes")
	public void getInputs (DBConnect dbDataLoad, MongoCollection collection) {
		//Take user input for recommendation criteria
		Scanner sc = new Scanner(System.in);
		Utility ut = new Utility();
		float rating;
		int year;
		String genres;
		Boolean option = true;
		String email;
		
		while(option == true) {
			System.out.println("\n\nMENU");
			System.out.println("Enter recommendation field:");
			System.out.println("1. Rating \n2. Release year \n3. Genres \n4. User's frequent genres \n5. Rate a movie \n6. Update a rating \n7. Exit");
			int type = sc.nextInt();
			rating = -1;
			year = 1900;
			genres = "";
			email = "";
			
			switch(type) {				
			case 1:
				System.out.println("Enter a rating threshold greater than 0:");
				try {
					rating = sc.nextFloat();
				}catch (Exception e) {
	                System.out.println("Input Mismatch Error! Could not proceed the request!!!");
	            }
				
				while(rating < 0 || rating > 5) {
					System.out.println("Please enter a value between 0-5 (both inclusive).");
					rating = sc.nextFloat();
				}
				ut.evaluateQuery(dbDataLoad, collection, rating, year, genres, email, type);
				break;
				
			case 2:
				System.out.println("Please enter a year between 1900 AND " + Calendar.getInstance().get(Calendar.YEAR) + " (results are capped at the entered year)");
				try {
					year = sc.nextInt();
				}catch (Exception e) {
	                System.out.println("Input Mismatch Error! Could not proceed the request!!!");
	            }
					
				while(year <= 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) {
					System.out.println("Please enter a valid year between 1900 AND " + Calendar.getInstance().get(Calendar.YEAR));
					sc.nextLine();
					try {
						year = sc.nextInt();
					}catch (Exception e) {
		                System.out.println("Input Mismatch Error! Could not proceed the request!!!");
		            }
				} 
				ut.evaluateQuery(dbDataLoad, collection, rating, year, genres, email, type);
				break;
				
			case 3:
				System.out.println("Enter comma separated genres from the following:");
				System.out.println("Action, Adventure, Animation, Children, Comedy, Crime, Documentary, Drama, Fantasy, Film-Noir, Horror, Musical, Mystery, Romance, Sci-Fi, Thriller, War, Western");
				sc.nextLine();
				genres = sc.nextLine();
				while(genres == "" || genres == null || genres.trim().length() == 0) {
					System.out.println("Please enter a valid list of genres.");
					genres = sc.nextLine();
				}
				while(!genres.matches("[a-zA-Z, ]*")) {
					System.out.println("Please enter a valid list of comma separated genres.");
					genres = sc.nextLine();
				}
				ut.evaluateQuery(dbDataLoad,collection, rating, year, genres, email, type);
				break;
				
			case 4:
				System.out.println("Enter email address:");
				// TBD: verify existence of email
				sc.nextLine();
				email = sc.nextLine();
				while(!ut.validateEmail(collection, email)) {
					email = sc.nextLine();
				}
				ut.evaluateQuery(dbDataLoad,collection, rating, year, genres, email, type);
				break;
				
			case 5:
				float newRating = 0;
				System.out.println("Enter email address:");
				sc.nextLine();
				email = sc.nextLine();
				while(!ut.validateEmail(collection, email)) {
					email = sc.nextLine();
				}
				System.out.println("Enter movie name:");
				String movie = sc.nextLine();
				System.out.println("Enter rating:");
				try {
					newRating = sc.nextFloat();
				}catch (Exception e) {
	                System.out.println("Input Mismatch Error! Could not proceed the request!!!");
	            }
				
				ut.rateMovie(collection, movie, newRating, email, 1);
				break;
				
			case 6:
				float updateRating = 0;
				System.out.println("Enter email address:");
				sc.nextLine();
				email = sc.nextLine();
				while(!ut.validateEmail(collection, email)) {
					email = sc.nextLine();
				}
				System.out.println("Enter movie name:");
				movie = sc.nextLine();
				System.out.println("Enter rating:");
				try {
					updateRating = sc.nextFloat();
				}catch (Exception e) {
	                System.out.println("Input Mismatch Error! Could not proceed the request!!!");
	            }
				
				ut.rateMovie(collection, movie, updateRating, email, 2);
				break;
				
			case 7:
				option= false;
				break;
				
			default:
				System.out.println("Please enter a valid input.");
				break;	
			}
		}
		
	}
	
	
	
	
}