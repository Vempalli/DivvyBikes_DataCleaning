package com.CS424.Alpha;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class Bikes_HourOfDay_Chicago {
	static int hours[] = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		try {
			
			CsvReader trips = new CsvReader("C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\InputFiles\\trips_updated.csv");
			String allHours = "C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\TempFiles\\Temp1Hour.csv";
			String execptionCase = "C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\TempFiles\\Temp2Hour.csv";
			
			
			CsvWriter hoursOutput = new CsvWriter(new FileWriter(allHours, true), ',');
			CsvWriter exception = new CsvWriter(new FileWriter(execptionCase, true), ',');
			
			//set headers to output file
			hoursOutput.write("TripStartHour");
			hoursOutput.write("TripEndHour");
			hoursOutput.endRecord();
			
			exception.write("TripStartHour");
			exception.write("TripEndHour");
			exception.endRecord();
			
			//read headers from input file
			trips.readHeaders();
			while (trips.readRecord())
			{
				String startTime = trips.get("starttime");
				String endTime = trips.get("stoptime");
				Date dt1 = new Date();
				Date dt2 = new Date();
				try {
						dt1 = getDateFromString(startTime,"MM/dd/yyyy HH:mm");
						dt2 = getDateFromString(endTime,"MM/dd/yyyy HH:mm");
						
						hoursOutput.write(Integer.toString(dt1.getHours()));
						hoursOutput.write(Integer.toString(dt2.getHours()));
						hoursOutput.endRecord();
						if(dt1.getHours() == dt2.getHours()){
							exception.write(Integer.toString(dt1.getHours()));
							exception.write(Integer.toString(dt2.getHours()));
							exception.endRecord();
						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
			trips.close();
			hoursOutput.close();
			exception.close();
			
			
			//Write actual output to CSV in FinalFiles folder
			String finalOutput = "C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\FinalFiles\\BikesOutByHourOfDay_Chicago.csv";
			CsvWriter hoursCount = new CsvWriter(new FileWriter(finalOutput, true), ',');
			hoursCount.write("Hour");
			hoursCount.write("No_Of_Bikes");
			hoursCount.endRecord();
			
			CsvReader temp1 = new CsvReader(allHours);
			CsvReader temp2 = new CsvReader(execptionCase);
			temp1.readHeaders();
			temp2.readHeaders();
			while (temp1.readRecord()){
				hours[Integer.parseInt(temp1.get("TripStartHour"))] += 1;
				hours[Integer.parseInt(temp1.get("TripEndHour"))] += 1;
			}
			while (temp2.readRecord()){
				hours[Integer.parseInt(temp2.get("TripEndHour"))] -= 1;
			}
			//write all counts
			for(int index = 0;index < 24; index++){
				hoursCount.write(Integer.toString(index));
				hoursCount.write(Integer.toString(hours[index]));
				hoursCount.endRecord();
			}
			hoursCount.close();
			temp1.close();
			temp2.close();
			System.out.println("Program Executed");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
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
