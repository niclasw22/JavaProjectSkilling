package com.Project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

class Dispatcher {
    //The below arrays are to store all the data
    //From this, we will create threads for two different drones
    //That both work with the same dispatcher
    //So that we can pump data into them in parallel
    public static double[] FirstLat = new double[632];
    public static double[] FirstLong = new double[632];
    public static String[] FirstTime = new String[632];
    public static double[] SecondLat = new double[1096];
    public static double[] SecondLong = new double[1096];
    public static String[] SecondTime = new String[1096];
    public static String[] StationName = new String[309];
    public static double[] StationLat = new double[309];
    public static double[] StationLong = new double[309];
    public static String[] Names = new String[2];

    void readData() {
        String FirstDrone = "src/5937.csv";
        String SecondDrone = "src/6043.csv";
        String StationLocation = "src/tube.csv";
        String row = "";


        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(FirstDrone));
            int i = 0;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                if (i == 0) {
                    getNames()[0] = data[0];
                }
                double lat = Double.parseDouble(data[1].replaceAll("\"", ""));
                //System.out.println(lat);
                getFirstLat()[i] = lat;
                double lon = Double.parseDouble(data[2].replaceAll("\"", ""));
                getFirstLong()[i] = lon;
                //System.out.println(data[3]);
                String[] time = data[3].split(" ");
                //System.out.println(time[0]+" "+time[1]);
                getFirstTime()[i] = time[1].replaceAll("\"", "");
//                System.out.println("The stored variable in time is "+getFirstTime()[i]);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String second = "";
        try {
            BufferedReader secondReader = new BufferedReader(new FileReader(SecondDrone));
            int j = 0;
            while ((second = secondReader.readLine()) != null) {
                String[] two = second.split(",");
                if (j == 0) {
                    getNames()[1] = two[0];
                }
                double seclat = Double.parseDouble(two[1].replaceAll("\"", ""));
                getSecondLat()[j] = seclat;
                double seclon = Double.parseDouble(two[2].replaceAll("\"", ""));
                getSecondLong()[j] = seclon;
                String[] time = two[3].split(" ");
                getSecondTime()[j] = time[1].replaceAll("\"", "");
//                System.out.println(getSecondTime()[j]);
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String stat = "";
        try {
            BufferedReader thirdReader = new BufferedReader(new FileReader(StationLocation));
            int k = 0;
            while ((stat = thirdReader.readLine()) != null) {
                String[] three = stat.split(",");
                double statlat = Double.parseDouble(three[1]);
                getStationLat()[k] = statlat;
                double statlon = Double.parseDouble(three[2]);
                getStationLong()[k] = statlon;
                getStationName()[k] = three[0];
                k++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendData(Drone drone, int index) {
    	if (drone.ID==Names[0]) {
    		for (int j = 0; j < drone.memory.length; j++) {
                drone.memory[j][0] = getFirstLat()[index];
                drone.memory[j][1] = getFirstLong()[index];
                index++;

            }
    	}
    	else {
    		for (int j = 0; j < drone.memory.length; j++) {
                drone.memory[j][0] = getSecondLat()[index];
                drone.memory[j][1] = getSecondLong()[index];
                index++;

            }
    	}
        drone.isAvailable=false;
    }

    public double[] getFirstLat() {
        return FirstLat;
    }

    public void setFirstLat(double[] firstLat) {
        FirstLat = firstLat;
    }

    public double[] getFirstLong() {
        return FirstLong;
    }

    public void setFirstLong(double[] firstLong) {
        FirstLong = firstLong;
    }

    public String[] getFirstTime() {
        return FirstTime;
    }

    public void setFirstTime(String[] firstTime) {
        FirstTime = firstTime;
    }

    public double[] getSecondLat() {
        return SecondLat;
    }

    public void setSecondLat(double[] secondLat) {
        SecondLat = secondLat;
    }

    public double[] getSecondLong() {
        return SecondLong;
    }

    public void setSecondLong(double[] secondLong) {
        SecondLong = secondLong;
    }

    public String[] getSecondTime() {
        return SecondTime;
    }

    public void setSecondTime(String[] secondTime) {
        SecondTime = secondTime;
    }

    public String[] getStationName() {
        return StationName;
    }

    public void setStationName(String[] stationName) {
        StationName = stationName;
    }

    public double[] getStationLat() {
        return StationLat;
    }

    public void setStationLat(double[] stationLat) {
        StationLat = stationLat;
    }

    public double[] getStationLong() {
        return StationLong;
    }

    public void setStationLong(double[] stationLong) {
        StationLong = stationLong;
    }

    public String[] getNames() {
        return Names;
    }

    public void setNames(String[] names) {
        Names = names;
    }
}

class Drone{
    double memory[][]= new double[10][2];
    boolean isAvailable= true;
    String ID;
    int index=0;
    double speed;
    void reset(){
        double memory[][]= new double[10][2];
        isAvailable=true;
    }
    void inc(){
        index+=10;

    }
}


class path implements Runnable{
	Dispatcher ds;
	Drone drone;
	private volatile boolean stop= false;
	double [] Stat_1;
	double [] Stat_2;
	String [] StatName;
	String [] Time;
	String [][] Time_array;

	public path(Dispatcher dispatcher, Drone dr,double [] Station_1,double [] Station_2,String [] StationName, String [] times, String[][] Time_a) {
		ds=dispatcher;
		drone=dr;
		Stat_1=Station_1;
		Stat_2=Station_2;
		StatName=StationName;
		Time=times;
		Time_array=Time_a;
	}
	public static String checkNearby(double lat,double lon,double [] Stat_1, double [] Stat_2, String [] StatName){

        String a="NaN";
        Double dist;
        for(int i=0;i<StatName.length;i++){
        	dist=DistanceCalculator(lat,lon,Stat_1[i],Stat_2[i]);
            if(dist<350){
                a= StatName[i];

            }
        }
        return a;


    }
    private static double DistanceCalculator(double lat1,double lon1, double lat2, double lon2){
        //Here we use the formula for distance between two latitude and longitude points;
        //The most popular one seems to be the Haversine Formula
        double φ1 = lat1 * Math.PI/180, φ2 = lat2 * Math.PI/180, Δλ = (lon2-lon1) * Math.PI/180, R = 6371e3;
        double distance = Math.acos( Math.sin(φ1)*Math.sin(φ2) + Math.cos(φ1)*Math.cos(φ2) * Math.cos(Δλ) ) * R;

        return distance;
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int j=0;
		String[] list= {"Light","Moderate","Heavy"};
		Random r= new Random();
		String previous="";
		while(stop==false) {
			if(drone.isAvailable) {
					ds.sendData(drone,drone.index);
					System.out.println("Sending "+drone.ID+" next points!");
			}
			
			for(int i=0;i<drone.memory.length;i++) {
	               String a= checkNearby(drone.memory[i][0],drone.memory[i][1] , Stat_1,Stat_2, StatName);
	               if(Time_array[j][0].equals("08") && Time_array[j][1].equals("09")&& Time_array[j+1][0].equals("08") && Time_array[j+1][1].equals("10")) {
	            	   
	            	   System.out.println("The stoptime has been reached! Quitting program...");
	            	   stop=true;
	            	   break;
	               }
	               if (a!="NaN"&& a!=previous){
	                   //Here we report on the traffic, and only do it once by checking if the last point is close to the same station
	                   System.out.println("=== Traffic report: "+a+" ===");
	                   System.out.println("Drone ID : "+drone.ID);
	                   System.out.println("Time: "+Time[j]);
	                   System.out.println("Speed : constant");
	                   System.out.println("Traffic conditions: "+list[r.nextInt(list.length)]);
	               }
	               else {
	            	   System.out.println("Drone "+drone.ID +" cannot find anything, moving to next coordinate...");
	               }
	                
					j++;
					previous=a;
			}
			if(stop) {
				break;
			}
			drone.inc();
			drone.reset();
			
			}
		
		
		
		
	}
	
}

public class App {
    public static void main(String[] args) {
        Dispatcher dispatcher= new Dispatcher();
        dispatcher.readData();
        Drone drone_one= new Drone();
        Drone drone_two= new Drone();
        drone_one.ID=dispatcher.getNames()[0];
        drone_two.ID=dispatcher.getNames()[1];
        

        String [] FirstTime=dispatcher.getFirstTime();
        String [] SecondTime= dispatcher.getSecondTime();
        String [][] first_time_array= new String[FirstTime.length][3];
        String [][] second_time_array= new String[SecondTime.length][3];

        for (int i=0; i<FirstTime.length;i++){
            String[] temp =FirstTime[i].split(":");
            first_time_array[i][0]=temp[0];
            first_time_array[i][1]=temp[1];
            first_time_array[i][2]=temp[2];
            

        }

        for (int i=0; i<SecondTime.length;i++){
            String[] temp =SecondTime[i].split(":");
            second_time_array[i][0]=temp[0];
            second_time_array[i][1]=temp[1];
            second_time_array[i][2]=temp[2];

        }




        double[] Stat_1= Arrays.copyOf(dispatcher.getStationLat(),dispatcher.getStationLat().length);
        double[] Stat_2= Arrays.copyOf(dispatcher.getStationLong(),dispatcher.getStationLong().length);
        String [] StatName= dispatcher.getStationName();
        
        Thread t1= new Thread(new path(dispatcher, drone_one,Stat_1,Stat_2,StatName,FirstTime,first_time_array));
        Thread t2= new Thread(new path(dispatcher, drone_two, Stat_1, Stat_2, StatName,SecondTime,second_time_array));
        
        t1.start();
        t2.start();
        
        




    }
}


