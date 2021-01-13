package slcsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

class slcsp {
	// private global variables
	private static HashMap<String, String> slcspHash = new HashMap<String, String>();
	private static HashMap<String, ArrayList<ArrayList<String>>> zipsHash = new HashMap<String, ArrayList<ArrayList<String>>>();
	private static HashMap<ArrayList<String>, ArrayList<ArrayList<String>>> plansHash = new HashMap<ArrayList<String>, ArrayList<ArrayList<String>>>();
	
	// Main function that loads, calculates, and prints slcsp functionality
	public static void main(String[] args) {
		loadData();
		calculateSLCSP();
		printSLCSP();
	}
	
	// Function that unpackages the .csv files within the data folder and populates their hash map or lists
	private static void loadData() {
		// Initialize Variables
		File slcspFilePath = new File("./src/data/slcsp.csv");
		File zipsFilePath = new File("./src/data/zips.csv");
		File plansFilePath = new File("./src/data/plans.csv");
		
		String row = "";
		
		try {
			// Reads the values of the slcsp.csv file and inserts them into a Hash Map,
			// if value in key-value pair is undetermined, use an empty string as a placeholder
			// rows are made up of: zipcode, rate
			BufferedReader slcspCsvReader = new BufferedReader(new FileReader(slcspFilePath));
			row = slcspCsvReader.readLine(); // skips headers or the first line that acts as headers
			while ((row = slcspCsvReader.readLine()) != null) {
			    String[] data = row.split(",");
		    	slcspHash.put(data[0].trim().replaceFirst("^0+(?!$)", "").toLowerCase(), "");
			}
			slcspCsvReader.close();
			
			// Reads the values of the zips.csv file and inserts them into a Multi Key Hash Map
			// rows are made up of: zipcode, state, county_code, name, rate_area
			BufferedReader zipsCsvReader = new BufferedReader(new FileReader(zipsFilePath));
			row = zipsCsvReader.readLine(); // skips headers or the first line that acts as headers
			while ((row = zipsCsvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    
			    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
			    ArrayList<String> list2 = new ArrayList<String>();
			    if (zipsHash.containsKey(data[0])) {
			    	list = zipsHash.get(data[0]);
			    }
			    list2.add(data[1].toLowerCase().trim()); // state
		    	list2.add(data[2].toLowerCase().trim()); // county_code
		    	list2.add(data[3].toLowerCase().trim()); // name
		    	list2.add(data[4].toLowerCase().trim()); // rate_area
		    	list.add(list2);
			    
			    zipsHash.put(data[0].trim().replaceFirst("^0+(?!$)", "").toLowerCase().trim(), list);
			}
			zipsCsvReader.close();
			
			// Reads the values of the plans.csv file and inserts them into a Hash Map
			// rows are made up of: plan_id, state, metal_level, rate, rate_area
			BufferedReader plansCsvReader = new BufferedReader(new FileReader(plansFilePath));
			row = plansCsvReader.readLine(); // skips headers or the first line that acts as headers
			while ((row = plansCsvReader.readLine()) != null) {
				String[] data = row.split(",");
				
				ArrayList<String> key = new ArrayList<String>();
				key.add(data[1].toLowerCase().trim()); // state
				key.add(data[4].toLowerCase().trim()); // rate_area
				key.add(data[2].toLowerCase().trim()); // metal_level
				ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
				ArrayList<String> list2 = new ArrayList<String>();
				if (plansHash.containsKey(key)) {
					list = plansHash.get(key);
				}
				list2.add(data[0].toLowerCase().trim()); // plan_id
				list2.add(data[3].toLowerCase().trim()); // rate
				list.add(list2);
				
				plansHash.put(key, list); // plan_id, list of plan info
			}
			plansCsvReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Function that calculates the 2nd cheapest rate per zip code
	private static void calculateSLCSP() {
		// Loops through every zipcode value within slcspHash
		for (String slcspKey : slcspHash.keySet()) {
			// Variable initialization, also resets the variables on loop
			ArrayList<String> rateList = new ArrayList<String>();
			ArrayList<String> zipcodeList = new ArrayList<String>();
			
			double secondLowestRate = Double.MAX_VALUE;
			double lowestRate = Double.MAX_VALUE;
			
			Boolean isAmbiguous = false;
			
			// Checks to see if the given zipcode is within the zipsHash
			if (zipsHash.containsKey(slcspKey)) {
				// Creating a list of keys to use to search plansHash
				ArrayList<ArrayList<String>> zipsList = zipsHash.get(slcspKey);
				ArrayList<ArrayList<String>> plansHashKeyList = new ArrayList<ArrayList<String>>();
				for (ArrayList<String> zips : zipsList) {
					ArrayList<String> plansHashKey = new ArrayList<String>();
					plansHashKey.add(zips.get(0));
					plansHashKey.add(zips.get(3));
					plansHashKey.add("silver");
					plansHashKeyList.add(plansHashKey);
				}
				
				// Searches the plansHash Map for all Values that correspond to Keys from the plansHashKeyList
				for (ArrayList<String> plansHashKey : plansHashKeyList) {
					// Checks to see if the given Key is within the plansHash
					if (plansHash.containsKey(plansHashKey)) {
						
						// Checks for ambiguous zipcode to rate_area relationship.
						// If there are multiple rate_areas corresponding to a particular zipcode, consider that zipcode ambiguous and set rate to blank ""
						zipcodeList.add(plansHashKey.get(1));
						if (zipcodeList.size() > 1 && !zipcodeList.get(zipcodeList.size()-2).equals(zipcodeList.get(zipcodeList.size()-1))) {
							isAmbiguous = true;
							break;
						}
						
						// All Values associated with the key will be stripped down to just rates to be stored in a list
						for (ArrayList<String> plansHashValue : plansHash.get(plansHashKey)) {
							rateList.add(plansHashValue.get(1));
						}
						
						// Sifts through the list of rates to find and store the secondLowestRate
						for (int i = 0; i < rateList.size(); i++) {
							if (Double.parseDouble(rateList.get(i)) < lowestRate) {
								secondLowestRate = lowestRate;
								lowestRate = Double.parseDouble(rateList.get(i));
							}
							else if (Double.parseDouble(rateList.get(i)) < secondLowestRate && Double.parseDouble(rateList.get(i)) > lowestRate) {
								secondLowestRate = Double.parseDouble(rateList.get(i));
							}
						}
						rateList.clear();
					}
				}
				
				// sets the value in the Key-Value pair to be the secondLowest or blank ""
				// if the value is ambiguous due to conflicting corresponding rate_area or there isn't a second lowest rate, set to blank ""
				if (isAmbiguous || secondLowestRate == Double.MAX_VALUE) {
					slcspHash.put(slcspKey, "");
				}
				else {
					slcspHash.put(slcspKey, String.valueOf(secondLowestRate));	
				}
			}
		}
	}
	
	// Function that prints the zipcode and the accompanying calculated second lowest rate
	private static void printSLCSP() {
		System.out.println("zipcode,rate");
		for (Entry<String, String> slcspHash : slcspHash.entrySet()) {
			System.out.println(slcspHash.getKey() + "," + slcspHash.getValue());
		}
	}
}