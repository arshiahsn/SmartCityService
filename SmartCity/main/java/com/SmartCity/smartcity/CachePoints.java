package com.SmartCity.smartcity;

import java.util.Arrays;

public class CachePoints {
//A class to cache start, finish and their distance to other nodes

	private Location sourceLocation;
	private Location destinationLocation;
	


	public CachePoints(){
	}
	public CachePoints(Location sourceLocation,
			Location destinationLocation){
		this.sourceLocation = sourceLocation;
		this.destinationLocation = destinationLocation;		
	}




	@Override
	public String toString(){
		return "CachePoints{" +
				"sourceLocation=" + sourceLocation +
				", destinationLocation=" + destinationLocation +
				'}';
	}
	public Location getSourceLocation() {
		return sourceLocation;
	}
	public void setSourceLocation(Location sourceLocation) {
		this.sourceLocation = sourceLocation;
	}
	public Location getDestinationLocation() {
		return destinationLocation;
	}
	public void setDestinationLocation(Location destinationLocation) {
		this.destinationLocation = destinationLocation;
	}
}
