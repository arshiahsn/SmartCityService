package com.SmartCity.tsp;

import java.util.List;

import com.SmartCity.smartcity.SmartCity;

public class GreedyTSPSolver implements Runnable{
    

	private List<SmartCity> minRouteList;
	GreedyTravelingSalesman greedyTravelingSalesman;
	
	public void setMinRouteList(List<SmartCity> minRouteList){
		this.minRouteList = minRouteList;
	}
	
	public List<SmartCity> getMinRouteList(){
		return minRouteList;
	}

	public GreedyTSPSolver(GreedyTravelingSalesman greedyTravelingSalesman){
		this.greedyTravelingSalesman = greedyTravelingSalesman;
	}

	@Override
	public void run() {
	    greedyTravelingSalesman.tsp();
	    setMinRouteList(greedyTravelingSalesman.getMinRoute());
	}
}
