/* 
 * Created on Dec 17, 2005
 *
 * This source code is released under the GNU Lesser General Public License Version 3, 29 June 2007
 * see http://www.gnu.org/licenses/lgpl.html or the plain text version of the LGPL included with this project
 *
 * It comes with no warranty whatsoever
 */
package com.SmartCity.tsp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.SmartCity;

/**
 * This class encodes a traveling salesman problem in the form of n integer coordinates. 
 * 
 * @author Bjoern Guenzel - http://blog.blinker.net
 */
public class TravelingSalesman {

	public final static int MAP_SIZE = 200;
	private int[][] coordinates;
	private double[][] costs;
	public int n;
	boolean[] visited;
	private HashMap<Integer,SmartCity> map;
	private MyFileWriter myFileWriter = new MyFileWriter();
	
	private static int[][] createRandomCoordinates(int n, Random random) {
		int[][] coordinates = new int[n][2];

		for (int i = 0; i < coordinates.length; i++) {

			//we ignore the case that two cities have the same coordinates - it should work anyway?

			coordinates[i][0] = Math.abs(random.nextInt()%MAP_SIZE); 
			coordinates[i][1] = Math.abs(random.nextInt()%MAP_SIZE); 
		}

		return coordinates;
	}

	public TravelingSalesman(int[][] coordinates){
		this(coordinates, coordinates.length);
	}
	
	public TravelingSalesman(){
		
	}

	public TravelingSalesman(double[][] costs, HashMap<Integer,SmartCity> map){
		//this(coordinates, coordinates.length);
		this.n = costs[1].length;
		visited = new boolean[n];
		this.costs = costs;
		this.map = map;
	}

	public TravelingSalesman(int n, Random random){
		this(createRandomCoordinates(n, random), n);
	}

	public TravelingSalesman(int[][] coordinates, int n){
		this.n = n;

		visited = new boolean[n];
		costs = new double[n][n];

		this.coordinates = coordinates;

		initCostsByCoordinates();
	}


	/*	
	 * create costs matrix by creating coordinates for cities and using flight distance as cost
	 * however, the algorithm is more general than that, arbitrary cost matrices should work, even unsymmetric ones
	 */
	private void initCostsByCoordinates() {
		for(int i = 0;i<coordinates.length;i++){
			for(int j = 0;j<coordinates.length;j++){
				costs[i][j] = calculateTravelCostsBetweenCities(i,j);
			}
		}
	}

	private double calculateTravelCostsBetweenCities(int i, int j){
		int dx = coordinates[i][0]-coordinates[j][0];
		int dy = coordinates[i][1]-coordinates[j][1];

		return Math.sqrt(dx*dx+dy*dy); //pythagoras
	}

	public double calculateCosts(int[] route){
		return calculateCosts(route, false);
	}

	public double calculateCosts(int[] route, boolean isVerbose){

		double travelCosts = 0;
		for (int i = 1; i < route.length; i++) {
			travelCosts += costs[route[i-1]][route[i]]; 

			if(isVerbose){
				System.out.println("costs from "+route[i-1]+" to "+route[i]+": "+costs[route[i-1]][route[i]]);
			}

		}

		//return to starting city
		travelCosts += costs[route[n-1]][route[0]];
		if(isVerbose){
			System.out.println("costs from "+route[n-1]+" to "+route[0]+": "+costs[route[n-1]][route[0]]);
		}
		return travelCosts;
	}

	public HashMap<Integer,SmartCity> getMap(){
		return this.map;
	}

	public List<SmartCity> getRoute(int[] route){
		List<SmartCity> list = new ArrayList<SmartCity>();
		for(int i = 0; i<route.length; i++){
			list.add(this.getMap().get(route[i]));
		}

		return this.correctMinRouteList(list);
	}

	public List<SmartCity> correctMinRouteList(List<SmartCity> minRouteList){
		String tempServiceType = new String();

		ListIterator<SmartCity> liter = minRouteList.listIterator();
		while(liter.hasNext()){
			SmartCity listIdx = liter.next();
			if(listIdx.getServiceType().equals("start")
					|| listIdx.getServiceType().equals("finish"))
				continue;
			else
				if(listIdx.getServiceType().equals("dummy"))
					liter.remove();
			//					minRouteList.remove(listIdx);
				else{
					if(listIdx.getServiceType().equals(tempServiceType))
						//						minRouteList.remove(listIdx);
						liter.remove();
					else
						tempServiceType = listIdx.getServiceType();
				}											

		}

		//Reverse if needed
		List<SmartCity> correctList = new ArrayList<SmartCity>();
		liter = minRouteList.listIterator();
		SmartCity listIdx = liter.next();
		SmartCity listIdxNext = liter.next();
		if(listIdx.getServiceType().equals("start") &&
				listIdxNext.getServiceType().equals("finish"))
		{
			correctList.add(listIdx);
			while(liter.hasNext())
				liter.next();
			while(liter.hasPrevious()){
				listIdx = liter.previous();
				if(!listIdx.getServiceType().equals("start") && 
						!listIdx.getServiceType().equals("finish"))
					correctList.add(listIdx);
			}
			correctList.add(listIdxNext);
			//printRouteAndCost(correctList);
			printRouteAndCostPrime(correctList);
			return correctList;
		}
		else{
			//printRouteAndCost(minRouteList);
			printRouteAndCostPrime(minRouteList);
			return minRouteList;
		}


	}
	
	public Integer getKeyByValue(SmartCity value) {
		for (Entry<Integer, SmartCity> entry : getMap().entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
	public void printRouteAndCost(List<SmartCity> route){
		
		//A better function to calculate cost(Utility of a given route)

		double nodeCount = 0;
		System.out.print("Final route: ");
		for(SmartCity node : route){
			System.out.print(getKeyByValue(node)+" ");
			nodeCount++;
		}
		System.out.print("\n");
		
		
		double cumulativeUtil = 0;
		double cumulativeDist = 0;
		double cumulativeRealDist = 0;
		double cumulativeDuration = 0;
		double cumulativeServTime = 0;
		int cumulativeQual = 0;
		
		double cumulativeRealDistUtil = 0;
		double cumulativeDurationUtil = 0;
		double cumulativeQualityUtil = 0;

		
		UtilityFunction utilityFunction = new UtilityFunction();
		ListIterator<SmartCity> liter = route.listIterator();
		double utility, realDist, dur, qual, dist, serviceTime, realDistUtil, realDurUtil, qualUtil;
		SmartCity listIdx = liter.next();
		while (liter.hasNext()) {
			SmartCity listNextIdx = liter.next();
			if (listIdx.getServiceType().equals("start")) {
				// Casting from long to int as ID
				utility = Environment.mapping.getRealUtilEle(0, getKeyByValue(listNextIdx));
				realDist = Environment.mapping.getRealDistEle(0, getKeyByValue(listNextIdx));
				dur = Environment.mapping.getRealDurEle(0, getKeyByValue(listNextIdx));
				qual = listNextIdx.getQuality();
				dist = Environment.mapping.getDistEle(0, getKeyByValue(listNextIdx));
				serviceTime = Environment.mapping.getServiceTimeEle(0, getKeyByValue(listNextIdx));
				realDistUtil = Environment.mapping.getRealDistUtilEle(0, getKeyByValue(listNextIdx));
				realDurUtil = Environment.mapping.getRealDurUtilEle(0, getKeyByValue(listNextIdx));
				qualUtil = Environment.mapping.getRealQualUtilEle(0, getKeyByValue(listNextIdx));

			} else if (listNextIdx.getServiceType().equals("finish")) {
				utility = Environment.mapping.getRealUtilEle(1, getKeyByValue(listIdx));
				realDist = Environment.mapping.getRealDistEle(1, getKeyByValue(listIdx));
				dur = Environment.mapping.getRealDurEle(1, getKeyByValue(listIdx));
				dist = Environment.mapping.getDistEle(1, getKeyByValue(listIdx));
				realDistUtil = Environment.mapping.getRealDistUtilEle(1, getKeyByValue(listIdx));
				realDurUtil = Environment.mapping.getRealDurUtilEle(1, getKeyByValue(listIdx));
				qualUtil = 0;
				serviceTime = 0;
				qual = 0;
			} else {
				utility = utilityFunction.calculateUtility(listIdx, listNextIdx).getUtility();
				realDist = utilityFunction.getAttributes().getRealDist();
				dur = utilityFunction.getAttributes().getDuration();
				qual = listNextIdx.getQuality();
				dist = utilityFunction.getAttributes().getDist();
				serviceTime = utilityFunction.getAttributes().getServiceTime();
				realDistUtil = utilityFunction.getAttributes().getRealDistUtil();
				realDurUtil = utilityFunction.getAttributes().getDurationUtil();
				qualUtil = utilityFunction.getAttributes().getQualityUtil();
			}

			cumulativeRealDist += realDist;
			cumulativeDuration += dur;
			cumulativeUtil += utility;
			cumulativeQual += qual;
			cumulativeDist += dist;
			cumulativeServTime += serviceTime;
			cumulativeRealDistUtil += realDistUtil;
			cumulativeDurationUtil += realDurUtil;
			cumulativeQualityUtil += qualUtil;
			listIdx = listNextIdx;
		}
		
		System.out.print("Utility: "+cumulativeUtil+"\n");
		
		//Write quality into file
		//It's normalized according to number of nodes(genes)
		//n genes indicate n-1 utilities
		int reqCount = (int) (nodeCount-2);
		myFileWriter.write("util-br"+String.valueOf(reqCount)+"req",String.valueOf(cumulativeUtil));
		myFileWriter.write("dist-br"+String.valueOf(reqCount)+"req",String.valueOf(cumulativeDist));
		myFileWriter.write("realDist-br"+String.valueOf(reqCount)+"req",String.valueOf(cumulativeRealDist));
		myFileWriter.write("dur-br"+String.valueOf(reqCount)+"req",String.valueOf(cumulativeDuration));
		myFileWriter.write("qual-br"+String.valueOf(reqCount)+"req",String.valueOf(cumulativeQual));
		myFileWriter.write("servTime-br"+String.valueOf(reqCount)+"req",String.valueOf(cumulativeServTime));
		myFileWriter.write("totalTime-br"+String.valueOf(reqCount)+"req",String.valueOf(cumulativeServTime+cumulativeDuration));
		
	}
	public void printRouteAndCostPrime(List<SmartCity> route){
		//A better function to calculate cost(Utility of a given route)

		double nodeCount = 0;
		System.out.print("Final route: ");
		for(SmartCity node : route){
			System.out.print(getKeyByValue(node)+" ");
			nodeCount++;
		}
		System.out.print("\n");
		
		ListIterator<SmartCity> liter = route.listIterator();
		double cumuUtility = 0;
		double cumuDist = 0;
		double cumuDur = 0;
		double cumuRealDist = 0;
		int cumuQual = 0;
		double cumuServTime = 0;

		SmartCity listIdx = liter.next();
		while(liter.hasNext()){
			SmartCity listNextIdx = liter.next();
			//cumuUtility += costs[getKeyByValue(listIdx)][getKeyByValue(listNextIdx)];
			UtilityFunction utilityFunction = new UtilityFunction();
			cumuUtility += utilityFunction.calculateUtility(listIdx, listNextIdx).getUtility();
			cumuDist += utilityFunction.getAttributes().getDist();
			cumuDur += utilityFunction.getAttributes().getDuration();
			cumuRealDist += utilityFunction.getAttributes().getRealDist();
			cumuQual += utilityFunction.getAttributes().getQual();
			cumuServTime += utilityFunction.getAttributes().getServiceTime();

			listIdx = listNextIdx;

		}
		System.out.print("Utility: "+cumuUtility+"\n");
		
		//Write quality into file
		//It's normalized according to number of nodes(genes)
		//n genes indicate n-1 utilities
		int reqCount = (int) (nodeCount-2);
		myFileWriter.write("util-br"+String.valueOf(reqCount)+"req",String.valueOf(cumuUtility));
		myFileWriter.write("dist-br"+String.valueOf(reqCount)+"req",String.valueOf(cumuDist));
		myFileWriter.write("realDist-br"+String.valueOf(reqCount)+"req",String.valueOf(cumuRealDist));
		myFileWriter.write("dur-br"+String.valueOf(reqCount)+"req",String.valueOf(cumuDur));
		myFileWriter.write("qual-br"+String.valueOf(reqCount)+"req",String.valueOf(cumuQual));
		myFileWriter.write("servTime-br"+String.valueOf(reqCount)+"req",String.valueOf(cumuServTime));
		myFileWriter.write("totalTime-br"+String.valueOf(reqCount)+"req",String.valueOf(cumuServTime+cumuDur));
		
	}
	public void printRoute(int[] route){
		for(int i = 0;i<route.length;i++){
			System.out.print(route[i]+" ");
			//System.out.print(this.map.get(i)+" ");
		}
	}

	public void printCosts(){
		System.out.println("costs matrix for the traveling salesman problem:");
		for(int i = 0;i<costs.length;i++){
			for(int j = 0;j<costs[i].length;j++){
				System.out.print(costs[i][j]+" ");
			}
			System.out.print("\n");
		}
	}

}
