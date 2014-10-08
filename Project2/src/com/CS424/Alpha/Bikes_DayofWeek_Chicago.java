package com.CS424.Alpha;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class Bikes_DayofWeek_Chicago {
	static int days[] = new int[]{0,0,0,0,0,0,0};
	public static void main(String[] args) {
		try {
			
			CsvReader trips = new CsvReader("C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\InputFiles\\trips_updated.csv");
			String allDates = "C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\TempFiles\\Temp1.csv";
			String execptionCase = "C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\TempFiles\\Temp2.csv";
			
			
			CsvWriter daysOutput = new CsvWriter(new FileWriter(allDates, true), ',');
			CsvWriter exception = new CsvWriter(new FileWriter(execptionCase, true), ',');
			
			//set headers to output file
			daysOutput.write("TripStartDay");
			daysOutput.write("TripEndDay");
			daysOutput.endRecord();
			
			exception.write("TripStartDay");
			exception.write("TripEndDay");
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
						
						daysOutput.write(Integer.toString(dt1.getDay()));
						daysOutput.write(Integer.toString(dt2.getDay()));
						daysOutput.endRecord();
						if(dt1.getDay() != dt2.getDay()){
							exception.write(Integer.toString(dt1.getDay()));
							exception.write(Integer.toString(dt2.getDay()));
							exception.endRecord();
						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
			trips.close();
			daysOutput.close();
			exception.close();
			
			
			//Write actual output to CSV in FinalFiles folder
			String finalOutput = "C:\\Users\\ThejaSwarup\\Box Sync\\Fall 2014\\CS 424\\Project2\\FinalFiles\\BikesOutByDayOfWeek_Chicago.csv";
			CsvWriter daysCount = new CsvWriter(new FileWriter(finalOutput, true), ',');
			daysCount.write("Day");
			daysCount.write("No_Of_Bikes");
			daysCount.endRecord();
			
			CsvReader temp1 = new CsvReader(allDates);
			CsvReader temp2 = new CsvReader(execptionCase);
			temp1.readHeaders();
			temp2.readHeaders();
			while (temp1.readRecord()){
				days[Integer.parseInt(temp1.get("TripStartDay"))] += 1;
			}
			while (temp2.readRecord()){
				days[Integer.parseInt(temp2.get("TripEndDay"))] += 1;
			}
			//write all counts
			for(int index = 0;index< 7; index++){
				daysCount.write(Integer.toString(index));
				daysCount.write(Integer.toString(days[index]));
				daysCount.endRecord();
			}
			daysCount.close();
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
