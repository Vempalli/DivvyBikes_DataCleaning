package com.CS424.Alpha;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.csvreader.CsvReader;

public class Bikes_HourOfDay_Stations {
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
				Date dt1 = new Date();
				try {
						//System.out.println(++lineNum);
						//++lineNum;
						
						dt1 = getDateFromString(startTime,"MM/dd/yyyy HH:mm");
						valList = finalMap.get(fromStnId);
						valList.add(new Integer(dt1.getHours()));
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
		   int hours[] = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		   Map.Entry<String, ArrayList<Integer>> entry = iterator.next();
		   ArrayList<Integer> valueList =  entry.getValue();
		   if(valueList.size() > 0){
			   for(Integer ind = 0; ind < 24 ; ind++){
				   hours[ind] = getOccurencesOfDays(valueList,ind);
				   mainMapValue = dayCountForStnMap.get(entry.getKey());
				   mainMapValue.put(new Integer(ind),new Integer(hours[ind]));
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
				FileWriter file = new FileWriter("C:\\Users\\ThejaSwarup\\Desktop\\Bikes_HourOfDay_Stations.json");
				while(iterator.hasNext()){
					Map.Entry<String, HashMap<Integer,Integer>> entry = iterator.next();
					HashMap<Integer,Integer> valueMap =  entry.getValue();
					//Build JSON
					for(int hour = 0; hour < 24; hour++){
						JSONObject obj = new JSONObject();
						obj.put("StationName", stationIdNameMap.get(Integer.parseInt(entry.getKey())));
						obj.put("CommunityName", stationCommMap.get(Integer.parseInt(entry.getKey())));
						obj.put("Hour of Day", "["+hour+" to "+hour+":59]");
						obj.put("No.Of.Bikes", valueMap.get(hour));
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
	public static Date getDateFromString(String date, String format) throws Exception{
		Date utilDate = null;
		SimpleDateFormat sdf = null;
		if(date == null || date.length() == 0) {
			return utilDate;
		}
		try { 
			sdf = new SimpleDateFormat(format);
			utilDate = sdf.parse(date);
		}
		catch(Exception e) {
			throw new Exception("Exception in getDateFromString method" + e.getMessage());
		}
		return utilDate;
	}
}
