package com.SmartCity.tsp;

import java.io.IOException;
import java.util.List;

import com.SmartCity.smartcity.SmartCity;


public class AntTspSolver implements Runnable{
	
    AntTsp antTsp;
	private List<SmartCity> minRouteList;
	
	public AntTspSolver(AntTsp antTsp){
		this.antTsp = antTsp;
	}
    
    @Override
    public void run() {
        // Load in TSP data file.

            antTsp.solve();
        	setMinRouteList(antTsp.getMinRoute());
       // }

    }

	public AntTsp getAnttsp() {
		return antTsp;
	}

	public void setAnttsp(AntTsp anttsp) {
		this.antTsp = anttsp;
	}

	public List<SmartCity> getMinRouteList() {
		return minRouteList;
	}

	public void setMinRouteList(List<SmartCity> minRouteList) {
		this.minRouteList = minRouteList;
	}
}
