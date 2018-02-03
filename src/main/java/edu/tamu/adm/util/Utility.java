package edu.tamu.adm.util;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;

public class Utility {

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

	public static void print(AggregateIterable<Document> iterable, int type) {
		switch(type) {				
		case 1:
			System.out.println("\n");
			System.out.println("Displaying top 20 results as per ratings:");
			System.out.println("**************************************************************************************************");
			System.out.println("\t\tMovie Title \t\t\t\tRating");
			System.out.println("**************************************************************************************************");
			for (Document doc: iterable) {
				System.out.format("%32s\t\t\t%.2f\n", doc.get("_id"), doc.get("rating"));
			}
			break;
			
		}
	}

	public static void print(FindIterable<Document> cursor, int type) {
		switch(type) {
		case 1: 
			System.out.println("**************************************************************************************************");
			System.out.println("\t\tMovie Title \t\t\t\tRating");
			System.out.println("**************************************************************************************************");
			for (Document doc : cursor) {
				System.out.format("%32s\t\t\t%.2f\n", doc.get("movieTitle"), doc.get("rating"));
			}
			break;
		case 2:
			System.out.println("\n");
			System.out.println("Displaying top 20 results as per releasing year:");
			System.out.println("**************************************************************************************************");
			System.out.println("\t\tMovie Title \t\t\t\t Year");
			System.out.println("**************************************************************************************************");
			for (Document doc: cursor) {
				System.out.format("%32s\t\t\t%d\n", doc.get("_id"), ((Double) ((Document) doc.get("value")).get("movieYear")).intValue());
			}
			break;
		case 3:
			//Genres
			if(!cursor.iterator().hasNext()) {
				System.out.println("Invalid Genre!!! Couldn't proceed the request!!!");
				break;
			}
			System.out.println("*************************************************************************************************************************************************************************");
			System.out.println("\t\tMovie Title \t\t\t\t\t\t\t\t Genres \t\t\t\t\tYear \t\t\t Rating");
			System.out.println("*************************************************************************************************************************************************************************");
			for (Document doc : cursor) {
				System.out.format( "%40s\t\t\t%48s\t\t\t%d\t\t\t%.2f\n",doc.get("_id"),((Document) doc.get("value")).get("genres"),((Double) ((Document) doc.get("value")).get("movieYear")).intValue(),((Document) doc.get("value")).get("rating"));
			}
			break;
		}
		
		
	}

}