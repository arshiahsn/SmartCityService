package com.SmartCity.tsp;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import com.SmartCity.smartcity.Location;
import com.SmartCity.smartcity.SmartCity;

public class GoogleWebService {
	private String apiKey;
	private RestTemplate restTemplate;
	private List<Double> distList;
	private List<Double> durList;
	
	
	public GoogleWebService(){
		restTemplate = new RestTemplate();
		apiKey = "AIzaSyDLj5xdbamw9ZGQg2fijVRPw05SycwUMDc";
		distList = new LinkedList<Double>();
		durList = new LinkedList<Double>();
	}
	
	public void putInDistList(double element){
		distList.add(element);
	}
	public void putInDurList(double element){
		durList.add(element);
	}
	

	public void getAttributes(Location origin, Location destination) throws Exception{
		try{

			String jsonStr = restTemplate.getForObject(
					"https://maps.googleapis.com/maps/api/distancematrix/json?units=metric"
					+ "&origins="+origin.getLatitude()+","+origin.getLongitude()
					+ "&destinations="+destination.getLatitude()+","+destination.getLongitude()
					+ "&departure_time=now&traffic_model=best_guess"
					+ "&key=" + apiKey, String.class);
			
			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONArray rows = jsonObj.getJSONArray("rows");
			
			//List<Double> distList = new LinkedList<Double>();
			//List<Double> durList = new LinkedList<Double>();
			
			for (int i = 0; i < rows.length(); i++) {
				JSONObject row = rows.getJSONObject(i);
				JSONArray elements = row.getJSONArray("elements");
				
				JSONObject attributes = elements.getJSONObject(0);
				//Get distance in meters
				JSONObject distance = attributes.getJSONObject("distance");
				double distVal = distance.getDouble("value");
				putInDistList(distVal);
				//Get duration in seconds
				JSONObject duration = attributes.getJSONObject("duration_in_traffic");
				double durVal = duration.getDouble("value");
				putInDurList(durVal);

			}

		}
		catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}
	public List<Double> getDistList() {
		return distList;
	}
	public void setDistList(List<Double> distList) {
		this.distList = distList;
	}
	public List<Double> getDurList() {
		return durList;
	}
	public void setDurList(List<Double> durList) {
		this.durList = durList;
	}


	
	
}
