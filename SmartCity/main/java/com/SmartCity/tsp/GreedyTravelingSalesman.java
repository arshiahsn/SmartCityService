package com.SmartCity.tsp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Stack;
import java.util.Map.Entry;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.SmartCity;

public class GreedyTravelingSalesman
{
	private int numberOfNodes;
	private Stack<Integer> stack;
	private double adjacencyMatrix[][];
	private int route[];
	private HashMap<Integer,SmartCity> map;
	private MyFileWriter myFileWriter = new MyFileWriter();
	
	public GreedyTravelingSalesman(AdjacencyMatrix adjacencyMatrix,
			HashMap<Integer,SmartCity> map)
	{
		stack = new Stack<Integer>();
		this.adjacencyMatrix = adjacencyMatrix.getMatrix();
		this.map = map;
		//Make route array equal to number of columns in matrix(number of nodes)
		route = new int[this.adjacencyMatrix[0].length];
	}
	HashMap<Integer,SmartCity> getMap(){
		return this.map;
	}
	
	List<SmartCity> getMinRoute(){
		return getRoute(route);
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
	
	public void printRouteAndCostPrime(List<SmartCity> route){
		//A better function to calculate cost(Utility of a given route)
		UtilityFunction utilityFunction = new UtilityFunction();
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
			cumuUtility += utilityFunction.calculateUtility(listIdx, listNextIdx).getUtility();
			cumuDist += utilityFunction.getAttributes().getDist();
			cumuDur += utilityFunction.getAttributes().getDuration();
			cumuRealDist += utilityFunction.getAttributes().getRealDist();
			cumuQual += utilityFunction.getAttributes().getQual();
			cumuServTime += utilityFunction.getAttributes().getServiceTime();
			
			listIdx = listNextIdx;
		}
		System.out.print("Greedy Utility: "+cumuUtility+"\n");

		
		//Write quality into file
		//It's normalized according to number of nodes(genes)
		//n genes indicate n-1 utilities
		int reqCount = (int) nodeCount-2;
		myFileWriter.write("util-gr"+String.valueOf(reqCount)+"req",String.valueOf(cumuUtility));
		myFileWriter.write("dist-gr"+String.valueOf(reqCount)+"req",String.valueOf(cumuDist));
		myFileWriter.write("realDist-gr"+String.valueOf(reqCount)+"req",String.valueOf(cumuRealDist));
		myFileWriter.write("qual-gr"+String.valueOf(reqCount)+"req",String.valueOf(cumuQual));
		myFileWriter.write("dur-gr"+String.valueOf(reqCount)+"req",String.valueOf(cumuDur));
		myFileWriter.write("servTime-gr"+String.valueOf(reqCount)+"req",String.valueOf(cumuServTime));
		myFileWriter.write("totalTime-gr"+String.valueOf(reqCount)+"req",String.valueOf(cumuServTime+cumuDur));
	}
	
	public Integer getKeyByValue(SmartCity value) {
		for (Entry<Integer, SmartCity> entry : getMap().entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
		
	public void tsp()
	{
		int nodeCounter = 0;
		numberOfNodes = adjacencyMatrix[1].length - 1;
		int[] visited = new int[numberOfNodes + 1];
		visited[1] = 1;
		stack.push(1);
		int element, dst = 0, i;
		double min = Integer.MAX_VALUE;
		boolean minFlag = false;
		System.out.print(1 + "\t");
		route[0] = 1;
		while (!stack.isEmpty())
		{
			element = stack.peek();
			i = 1;
			min = Integer.MAX_VALUE;
			while (i <= numberOfNodes)
			{
				if (adjacencyMatrix[element][i] > 0 && visited[i] == 0)
				{
					if (min > adjacencyMatrix[element][i])
					{
						min = adjacencyMatrix[element][i];
						dst = i;
						minFlag = true;
					}
				}
				i++;
			}
			if (minFlag)
			{
				visited[dst] = 1;
				stack.push(dst);
				nodeCounter++;
				System.out.print(dst + "\t");
				route[nodeCounter] = dst;
				minFlag = false;
				continue;
			}
			stack.pop();
		}
	}
}
