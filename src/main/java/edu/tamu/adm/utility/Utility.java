package edu.tamu.adm.utility;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Utility {
	
	//Method to sort the Map in descending order based on rating
	public static Map<String, Double> sortByComparator(Map<String, Double> unsortMap)
    {
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

	//Display Map with limit set to 20
    public static void printMovieList(Map<String, Double> map)
    {
	    int count = 0, max = 20;
	    System.out.println("Top 20 recommended movies based on your genres:");
        for (Entry<String, Double> entry : map.entrySet())
        {
        	if(count >= max) break;
            System.out.println("Movie Name: " + entry.getKey() + " \t\t\t Rating : "+ String.format("%.2f", entry.getValue()));
            count+=1;
        }
    }

}