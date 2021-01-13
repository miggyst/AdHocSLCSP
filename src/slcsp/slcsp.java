package slcsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class slcsp {
	
	public static void main(String[] args) {
		/*
		 * Things to do:
		 * 1) Need to retrieve and unpackage data from .csv files into 3 separate hash tables
		 * 2) go through the main slcsp.csv hash table one by one and search through other hashes to populate the hash.
		 * 3) create loop that prints out the slcsp values taken from hash
		 */
		loadData();
	}
	
	// Function that unpackages the .csv files within the data folder and populates their hash map or lists
	public static void loadData() {
		// Initialize Variables
		File slscpFilePath = new File("./src/data/slcsp.csv");
		File zipsFilePath = new File("./src/data/zips.csv");
		File plansFilePath = new File("./src/data/plans.csv");
		
		HashMap<String, String> slscpHash = new HashMap<String, String>();
		HashMap<Key, List<String>> zipsHash = new HashMap<Key, List<String>>();
		HashMap<String, List<String>> plansHash = new HashMap<String, List<String>>();
		
		String row = "";
		
		try {
			// Reads the values of the slcsp.csv file and inserts them into a Hash Map,
			// if value in key-value pair is undetermined, use an empty string as a placeholder
			// rows are made up of: zipcode, rate
			BufferedReader slscpCsvReader = new BufferedReader(new FileReader(slscpFilePath));
			while ((row = slscpCsvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    if (data.length == 2) {
			    	slscpHash.put(data[0], data[1]);
			    }
			    else {
			    	slscpHash.put(data[0], "");
			    }
			}
			slscpCsvReader.close();
			
			// Reads the values of the zips.csv file and inserts them into a Multi Key Hash Map
			// rows are made up of: zipcode, state, county_code, name, rate_area
			BufferedReader zipsCsvReader = new BufferedReader(new FileReader(zipsFilePath));
			while ((row = zipsCsvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    Key key = new Key(data[0], data[2]); // zipcode, county_code
			    
			    List<String> list = new ArrayList<String>();
			    list.add(data[1]); // state
			    list.add(data[3]); // name
			    list.add(data[4]); // rate_area
			    
			    zipsHash.put(key, list);
			}
			zipsCsvReader.close();
			
			// Reads the values of the plans.csv file and inserts them into a Hash Map
			// rows are made up of: plan_id, state, metal_level, rate, rate_area
			BufferedReader plansCsvReader = new BufferedReader(new FileReader(plansFilePath));
			while ((row = plansCsvReader.readLine()) != null) {
				String[] data = row.split(",");
				List<String> list = new ArrayList<String>();
				
				list.add(data[1]); // state
				list.add(data[2]); // metal_level
				list.add(data[3]); // rate
				list.add(data[4]); // rate_area
				
				plansHash.put(data[0], list); // plan_id, list of plan info
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
 
	public Key(String key1, String key2) {
			this.key1 = key1;
			this.key2 = key2;
	}
}