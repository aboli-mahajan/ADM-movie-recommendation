package edu.tamu.adm.controller;

import java.util.Calendar;
import java.util.Scanner;
import com.mongodb.client.MongoCollection;
import edu.tamu.adm.data.DBConnect;

public class RecommendMovies {
	
	@SuppressWarnings("rawtypes")
	public void getInputs (DBConnect dbDataLoad, MongoCollection collection) {
		//Take user input for recommendation criteria
		Scanner sc = new Scanner(System.in);
		Service service = new Service();
		float rating;
		int year;
		String genres;
		Boolean option = true;
		String email;
		
		while(option == true) {
			System.out.println("\n\nMENU");
			System.out.println("Enter option:");
			System.out.println("1. Recommend based on Rating \n2. Recommend based on Release year \n3. Recommend based on Genres \n4. Recommend using user's frequent genres \n5. Rate a movie \n6. Update a rating \n7. Exit");
			int type = sc.nextInt();
			rating = -1;
			int count = 0;
			year = 1900;
			genres = "";
			email = "";
			
			loop: switch(type) {				
			case 1:
				System.out.println("Enter a rating threshold greater than 0:");
				try {
					rating = sc.nextFloat();
				}catch (Exception e) {
	                System.out.println("Input Mismatch Error! Could not proceed the request!!!");
	            }
				
				while(rating <= 0 || rating > 5) {
					count+=1;
					if(count>2) {
						count =0;
						System.out.println("Max limit reached!! Could not proceed the request");
						break loop;
					}
					
					try {
						System.out.println("Enter a valid rating between 0 and 5");
						rating = sc.nextFloat();
					}catch (Exception e) {
						System.out.println("Input Mismatch Error! Could not proceed the request!!!");
					}
					
				}
				service.evaluateQuery(dbDataLoad, collection, rating, year, genres, email, type);
				break;
				
			case 2:
				System.out.println("Please enter a year between 1900 AND " + Calendar.getInstance().get(Calendar.YEAR) + " (results are capped at the entered year)");
				try {
					year = sc.nextInt();
				}catch (Exception e) {
	                System.out.println("Input Mismatch Error! Could not proceed the request!!!");
	            }
					
				while(year <= 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) {
					count+=1;
					if(count>2) {
						count =0;
						System.out.println("Max limit reached!! Could not proceed the request");
						break loop;
					}
					System.out.println("Please enter a valid year between 1900 AND " + Calendar.getInstance().get(Calendar.YEAR));
					sc.nextLine();
					try {
						year = sc.nextInt();
					}catch (Exception e) {
		                System.out.println("Input Mismatch Error! Could not proceed the request!!!");
		            }
				} 
				service.evaluateQuery(dbDataLoad, collection, rating, year, genres, email, type);
				break;
				
			case 3:
				System.out.println("Enter comma separated genres from the following:");
				System.out.println("Action, Adventure, Animation, Children, Comedy, Crime, Documentary, Drama, Fantasy, Film-Noir, Horror, Musical, Mystery, Romance, Sci-Fi, Thriller, War, Western");
				sc.nextLine();
				genres = sc.nextLine();
				while(genres == "" || genres == null || genres.trim().length() == 0) {
					count+=1;
					if(count>2) {
						count =0;
						System.out.println("Max limit reached!! Could not proceed the request");
						break loop;
					}
					System.out.println("Please enter a valid list of genres.");
					genres = sc.nextLine();
				}
				while(!genres.matches("[a-zA-Z, ]*")) {
					count+=1;
					if(count>2) {
						count =0;
						System.out.println("Max limit reached!! Could not proceed the request");
						break loop;
					}
					System.out.println("Please enter a valid list of comma separated genres.");
					genres = sc.nextLine();
				}
				service.evaluateQuery(dbDataLoad,collection, rating, year, genres, email, type);
				break;
				
			case 4:
				System.out.println("Enter email address:");
				sc.nextLine();
				email = sc.nextLine();
				while(!service.validateEmail(collection, email)) {
					count+=1;
					if(count>2) {
						count =0;
						System.out.println("Max limit reached!! Could not proceed the request");
						break loop;
					}
					email = sc.nextLine();
				}
				service.evaluateQuery(dbDataLoad,collection, rating, year, genres, email, type);
				break;
				
			case 5:
				float newRating = 0;
				System.out.println("Enter email address:");
				sc.nextLine();
				email = sc.nextLine();
				while(!service.validateEmail(collection, email)) {
					count+=1;
					if(count>2) {
						count =0;
						System.out.println("Max limit reached!! Could not proceed the request");
						break loop;
					}
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
				
				service.rateMovie(collection, movie, newRating, email, 1);
				break;
				
			case 6:
				float updateRating = 0;
				System.out.println("Enter email address:");
				sc.nextLine();
				email = sc.nextLine();
				while(!service.validateEmail(collection, email)) {
					count+=1;
					if(count>2) {
						count =0;
						System.out.println("Max limit reached!! Could not proceed the request");
						break loop;
					}
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
				
				service.rateMovie(collection, movie, updateRating, email, 2);
				break;
				
			case 7:
				System.out.println("Thank you for using our recommendation engine!!!!");
				option= false;
				break;
				
			default:
				System.out.println("Please enter a valid input.");
				break;	
			}
		}
		
	}
	
	
	
	
}