package slcsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class slcsp {
	
	private static HashMap<String, String> slscpHash = new HashMap<String, String>();
	private static HashMap<String, ArrayList<ArrayList<String>>> zipsHash = new HashMap<String, ArrayList<ArrayList<String>>>();
	private static HashMap<ArrayList<String>, ArrayList<ArrayList<String>>> plansHash = new HashMap<ArrayList<String>, ArrayList<ArrayList<String>>>();
	
	public static void main(String[] args) {
		/*
		 * Things to do:
		 * 1) Need to retrieve and unpackage data from .csv files into 3 separate hash tables
		 * 		How to Map:
		 * 			slscp: Map with Key = zipcode, Value = rate
		 * 			zips: Map with Key = zipcode, Value = state, county_code, name, rate_area 
		 * 			plans: Map with Key = state, rate_area, metal_level, Value = plan_id, rate
		 * 2) go through the main slcsp.csv hash table one by one and search through other hashes to populate the hash.
		 *      Requires Algorithm (The aim is to find the 2nd lowest SILVER PLAN)
		 *           Algorithm goes as follows:
		 *                take a zipcode from the slcspHash
		 *                use that zipcode to find a list of zips from zipsHash
		 *                for every list of zip take the state and rate_area to find a list of plans
		 *                	for every list of plan, search for a list that has SILVER metal_level,
		 *                		for every SILVER metal_level, save the rate onto a list
		 *                sort the list in ascending order
		 *                find the 2nd least expensive value in the list
		 * 3) create loop that prints out the slcsp values taken from hash
		 */
		loadData();
	}
	
	// Function that unpackages the .csv files within the data folder and populates their hash map or lists
	private static void loadData() {
		// Initialize Variables
		File slscpFilePath = new File("./src/data/slcsp.csv");
		File zipsFilePath = new File("./src/data/zips.csv");
		File plansFilePath = new File("./src/data/plans.csv");
		
		String row = "";
		
		try {
			// Reads the values of the slcsp.csv file and inserts them into a Hash Map,
			// if value in key-value pair is undetermined, use an empty string as a placeholder
			// rows are made up of: zipcode, rate
			BufferedReader slscpCsvReader = new BufferedReader(new FileReader(slscpFilePath));
			row = slscpCsvReader.readLine();
			while ((row = slscpCsvReader.readLine()) != null) {
			    String[] data = row.split(",");
		    	slscpHash.put(data[0], "");
			}
			slscpCsvReader.close();
			
			// Reads the values of the zips.csv file and inserts them into a Multi Key Hash Map
			// rows are made up of: zipcode, state, county_code, name, rate_area
			BufferedReader zipsCsvReader = new BufferedReader(new FileReader(zipsFilePath));
			while ((row = zipsCsvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    
			    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
			    ArrayList<String> list2 = new ArrayList<String>();
			    if (zipsHash.containsKey(data[0])) {
			    	list = zipsHash.get(data[0]);
			    }
			    list2.add(data[1]); // state
		    	list2.add(data[2]); // county_code
		    	list2.add(data[3]); // name
		    	list2.add(data[4]); // rate_area
		    	list.add(list2);
			    
			    zipsHash.put(data[0], list);
			}
			zipsCsvReader.close();
			
			// Reads the values of the plans.csv file and inserts them into a Hash Map
			// rows are made up of: plan_id, state, metal_level, rate, rate_area
			BufferedReader plansCsvReader = new BufferedReader(new FileReader(plansFilePath));
			while ((row = plansCsvReader.readLine()) != null) {
				String[] data = row.split(",");
				
				ArrayList<String> key = new ArrayList<String>();
				key.add(data[1]); // state
				key.add(data[4]); // rate_area
				key.add(data[2]); // metal_level
				ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
				ArrayList<String> list2 = new ArrayList<String>();
				if (plansHash.containsKey(key)) {
					list = plansHash.get(key);
				}
				list2.add(data[0]); // plan_id
				list2.add(data[3]); // rate
				list.add(list2);
				
				plansHash.put(key, list); // plan_id, list of plan info
			}
			plansCsvReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

// Key class that provides a pair of values to be used in the Multi Key Hash Map
class Key {
	
	public String key1;
	public String key2;
	public String key3;
 
	public Key(String key1, String key2, String key3) {
			this.key1 = key1;
			this.key2 = key2;
			this.key3 = key3;
	}
}