package com.CS424.Alpha;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.csvreader.CsvReader;

public class Bikes_DayOfYear_Stations {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		HashMap<String, ArrayList<Integer>> finalMap = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, HashMap<Integer,Integer>> dayCountForStnMap = new HashMap<String, HashMap<Integer,Integer>>();
		HashMap<Integer, String> stationIdNameMap = new HashMap<Integer, String>();
		HashMap<Integer, String> stationCommMap = new HashMap<Integer, String>();
		try {
			Integer lineCount = 0;
			CsvReader trips = new CsvReader("C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\InputFiles\\trips_updated.csv");
			//CsvReader trips = new CsvReader("C:\\Users\\ThejaSwarup\\Desktop\\Test.csv");
			CsvReader stations = new CsvReader("C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\InputFiles\\stations_final.csv");
			stations.readHeaders();
			
			//Map: stationName, dayList
			
			while (stations.readRecord()){
				++lineCount;
				stationIdNameMap.put(new Integer(stations.get("id")), stations.get("stationName"));
				stationCommMap.put(new Integer(stations.get("id")), stations.get("CommunityArea"));
				finalMap.put(stations.get("id"), new ArrayList<Integer>());
				dayCountForStnMap.put(stations.get("id"), new HashMap<Integer,Integer>());
			}
			stations.close();
			//read headers from input file
			trips.readHeaders();
			while (trips.readRecord())
			{
				
				ArrayList<Integer> valList = new ArrayList<Integer>();
				String startTime = trips.get("starttime");
				String fromStnId = trips.get("from_station_id");
				Calendar cal = Calendar.getInstance();
			    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
				try {
						cal.setTime(sdf.parse(startTime));
						valList = finalMap.get(fromStnId);
						valList.add(new Integer(cal.get(Calendar.DAY_OF_YEAR)));
						finalMap.put(fromStnId, valList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
			trips.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Now from your final map count no.of occurrences of days for each station
		Iterator<Map.Entry<String, ArrayList<Integer>>> iterator = finalMap.entrySet().iterator();
		while(iterator.hasNext()){
		   HashMap<Integer, Integer> mainMapValue = new HashMap<Integer, Integer>();
		   int days[] = new int[365]; 
		   for(int i = 0; i < days.length; i++) {
			   days[i] = 0;
	       }
		   Map.Entry<String, ArrayList<Integer>> entry = iterator.next();
		   ArrayList<Integer> valueList =  entry.getValue();
		   if(valueList.size() > 0){
			   for(Integer ind = 0; ind < days.length ; ind++){
				   days[ind] = getOccurencesOfDays(valueList,ind);
				   mainMapValue = dayCountForStnMap.get(entry.getKey());
				   mainMapValue.put(new Integer(ind),new Integer(days[ind]));
			   }
		   }
		   dayCountForStnMap.put(entry.getKey(), mainMapValue);
		   iterator.remove(); 
		}
		
		//create JSON Object
		System.out.println(dayCountForStnMap);
		createJSON(dayCountForStnMap,stationIdNameMap,stationCommMap);
		System.out.println("Program Executed");
	}
	
	private static void createJSON(
			HashMap<String, HashMap<Integer, Integer>> dayCountForStnMap, HashMap<Integer, String> stationIdNameMap, HashMap<Integer, String> stationCommMap) {
			int line = 0;
			try {
				Iterator<Map.Entry<String, HashMap<Integer,Integer>>> iterator = dayCountForStnMap.entrySet().iterator();
				FileWriter file = new FileWriter("C:\\Users\\ThejaSwarup\\Desktop\\Bikes_DayOfYear_Stations.json");
				while(iterator.hasNext()){
					Map.Entry<String, HashMap<Integer,Integer>> entry = iterator.next();
					HashMap<Integer,Integer> valueMap =  entry.getValue();
					//Build JSON
					for(int day = 1; day < 366; day++){
						JSONObject obj = new JSONObject();
						obj.put("StationName", stationIdNameMap.get(Integer.parseInt(entry.getKey())));
						obj.put("CommunityName", stationCommMap.get(Integer.parseInt(entry.getKey())));
						obj.put("Day of Year", day);
						obj.put("No.Of.Bikes", valueMap.get(day));
						System.out.println(++line);
						file.write(obj.toString());
						
					}
					iterator.remove();
				}
				
				file.flush();
				file.close();
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}

	private static int getOccurencesOfDays(ArrayList<Integer> valueList,
			Integer day) {
		return Collections.frequency(valueList, day);
		
	}
}
