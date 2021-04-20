package com.SmartCity.smartcity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



@Entity
public class Location{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private double latitude; 
	private double longitude;

	public Location(){

	}
	public Location(double latitude, double longitude) { 
		this.latitude = latitude; 
		this.longitude = longitude; 
	}
  

	@Override
	public String toString(){
		return "Location{" +
				"latitude=" + latitude +
				", longitude=" + longitude +
				'}';
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}

