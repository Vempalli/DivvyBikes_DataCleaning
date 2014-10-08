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

public class Bikes_DayOfWeek_Community {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		HashMap<String, ArrayList<Integer>> finalMap = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, HashMap<Integer,Integer>> dayCountForStnMap = new HashMap<String, HashMap<Integer,Integer>>();
		HashMap<Integer, String> stationIdNameMap = new HashMap<Integer, String>();
		//HashMap<Integer, String> stationCommMap = new HashMap<Integer, String>();
		try {
			Integer lineCount = 0;
			CsvReader trips = new CsvReader("C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\InputFiles\\trips_updated.csv");
			//CsvReader trips = new CsvReader("C:\\Users\\ThejaSwarup\\Desktop\\Test.csv");
			//CsvReader stations = new CsvReader("C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\InputFiles\\stations_final.csv");
			CsvReader communities = new CsvReader("C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\InputFiles\\Communities.csv");
			communities.readHeaders();
			
			//Map: stationName, dayList
			
			while (communities.readRecord()){
				++lineCount;
				stationIdNameMap.put(new Integer(communities.get("id")), communities.get("name"));
				//stationCommMap.put(new Integer(stations.get("id")), stations.get("CommunityArea"));
				finalMap.put(communities.get("name"), new ArrayList<Integer>());
				dayCountForStnMap.put(communities.get("name"), new HashMap<Integer,Integer>());
			}
			communities.close();
			//read headers from input file
			trips.readHeaders();
			int lineNum = 0;
			while (trips.readRecord())
			{
				
				ArrayList<Integer> valList = new ArrayList<Integer>();
				String startTime = trips.get("starttime");
				String fromCommName = trips.get("from_community");
				Date dt1 = new Date();
				try {
						System.out.println(++lineNum);
						//++lineNum;
						
						dt1 = getDateFromString(startTime,"MM/dd/yyyy HH:mm");
						valList = finalMap.get(fromCommName);
						valList.add(new Integer(dt1.getDay()));
						finalMap.put(fromCommName, valList);
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
		   int days[] = new int[]{0,0,0,0,0,0,0};
		   Map.Entry<String, ArrayList<Integer>> entry = iterator.next();
		   ArrayList<Integer> valueList =  entry.getValue();
		   if(valueList.size() > 0){
			   for(Integer ind = 0; ind < 7 ; ind++){
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
		createJSON(dayCountForStnMap);
		System.out.println("Program Executed");
	}
	
	private static void createJSON(
			HashMap<String, HashMap<Integer, Integer>> dayCountForStnMap) {
			HashMap<Integer,String> dayMap = new HashMap<Integer, String>();
			dayMap.put(0, "Sunday");
			dayMap.put(1, "Monday");
			dayMap.put(2, "Tuesday");
			dayMap.put(3, "Wednesday");
			dayMap.put(4, "Thursday");
			dayMap.put(5, "Friday");
			dayMap.put(6, "Saturday");
			int line = 0;
			try {
				Iterator<Map.Entry<String, HashMap<Integer,Integer>>> iterator = dayCountForStnMap.entrySet().iterator();
				FileWriter file = new FileWriter("C:\\Users\\ThejaSwarup\\Desktop\\Bikes_DayOfWeek_Communities.json");
				while(iterator.hasNext()){
					Map.Entry<String, HashMap<Integer,Integer>> entry = iterator.next();
					HashMap<Integer,Integer> valueMap =  entry.getValue();
					//Build JSON
					for(int day = 0; day < 7; day++){
						JSONObject obj = new JSONObject();
						obj.put("CommunityName", entry.getKey());
						obj.put("Day", dayMap.get(new Integer(day)));
						if(valueMap.size() > 0){
							obj.put("No.Of.Bikes", valueMap.get(day));
						}
						else{
							obj.put("No.Of.Bikes", 0);
						}
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
