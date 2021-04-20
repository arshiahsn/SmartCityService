package com.SmartCity.tsp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.SmartCity.smartcity.Location;
import com.SmartCity.smartcity.SmartCity;





public class AdjacencyMatrix {
	private double matrix[][];
	private HashMap<Integer, SmartCity> map;
	private int size;
	private UtilityFunction utilityFunction;
	private boolean notReal;
	//When doing reverse tsp INFI has no value, despite ZERO which is of significant value
	private double INFI = 0;
	private double ZERO = 1;
	public int numberOfNodes;



	public AdjacencyMatrix(SmartCity start, 
			List<SmartCity> list, 
			SmartCity finish){
		size = 3 + list.size();
		matrix = new double[size][size];
		numberOfNodes = matrix[1].length;
		map = this.createMap(start, list, finish);
		utilityFunction = new UtilityFunction();
	}
	public AdjacencyMatrix(SmartCity start, 
			List<SmartCity> list, 
			SmartCity finish,
			boolean notReal){
		this(start, list, finish);
		this.notReal = notReal;
		INFI = 10;
		ZERO = 0;
	}

	public double[][] getMatrix(){
		return matrix;
	}
	public double getElement(int i, int j){
		return matrix[i][j];
	}
	public void setElement(int i, int j, double tempMatrix){
		matrix[i][j] = tempMatrix;
	}
	public SmartCity getNode(int i){
		return map.get(i);
	}
	public HashMap<Integer, SmartCity> getMap(){
		return this.map;
	}


	public boolean startFinishDummy(int i, int j){
		if(
				//Start to finish //and Start to Dummy is zero
				(i == 0 && j == size-1)
				|| (i == 0 && j == size-2)
				//Dummy to Start and Dummy to finish is zero
				|| (i == size-1 && j == 0)
				|| (i == size-1 && j == size-2)
				//Finish to start and finish to dummy is zero
				|| (i == size-2 && j == 0)
				|| (i == size-2 && j == size-1))
			return true;
		else
			return false;

	}

	public double calculateCost(int i, int j){
		double dx = (this.getNode(i).getLocation().getLatitude() - 
				this.getNode(j).getLocation().getLatitude());
		double dy = (this.getNode(i).getLocation().getLongitude() - 
				this.getNode(j).getLocation().getLongitude());
		return Math.sqrt(dx*dx+dy*dy);
	}


	public HashMap<Integer,SmartCity> createMap(SmartCity start, List<SmartCity> list, 
			SmartCity finish){
		HashMap<Integer,SmartCity> map = new HashMap<Integer,SmartCity>();
		//Start+Finish+List+Dummy node
		//Dummy node is n-1
		int nodeCount = 3 + list.size();
		map.put(0, start);
		map.put(nodeCount-1, finish);
		//Dummy node
		map.put(nodeCount-2, new SmartCity(0, new Location(1e10,1e10), "dummy"));
		for(int i = 1; i < nodeCount-2; i++){
			map.put(i, list.get(i-1));
		}

		return map;
	}

	public void calculateAdjacencyMatrix(){

		int pred[] = new int [numberOfNodes];
		int suc[] = new int[numberOfNodes];
		//Instantiating successors and predecessors array
		for(int i = 0; i < numberOfNodes; i++){
			suc[i] = i;
			pred[i] = i;
		}

		//Holding number of nodes in a cluster
		HashMap<String,Integer> serviceCount = new HashMap<String,Integer>();
		//Holding number of nodes in a cluster up to now (Important for the last node)
		HashMap<String,Integer> counter = new HashMap<String,Integer>();

		//Initializing costs and set counters
		for(int i = 0; i < numberOfNodes; i++){
			if(!serviceCount.containsKey(this.getNode(i).getServiceType())){
				serviceCount.put(this.getNode(i).getServiceType(), 1);
			}
			else{
				int newVal = serviceCount.get(this.getNode(i).getServiceType())+1;
				serviceCount.put(this.getNode(i).getServiceType(), newVal);
			}
			for(int j = 0; j < numberOfNodes; j++)
				this.setElement(i, j, -1);
		}
		//Calculating costs
		for(int i = 0; i < numberOfNodes; i++){
			if(counter.containsKey(this.getNode(i).getServiceType())){
				int incVal = counter.get(this.getNode(i).getServiceType())+1;
				counter.put(this.getNode(i).getServiceType(), incVal);
			}
			else{
				counter.put(this.getNode(i).getServiceType(), 1);
			}
			for(int j = 0; j < numberOfNodes; j++){
				//All nodes are unreachable for dummy except for start and finish
				if(this.getNode(i).getServiceType().equals("dummy")
						&& !this.getNode(j).getServiceType().equals("start")
						&& !this.getNode(j).getServiceType().equals("finish")){
					this.setElement(i, j, INFI);
					this.setElement(j, i, INFI);
					continue;
				}
				//Dummy is unreachable from all nodes except for start and finish
				if(this.getNode(j).getServiceType().equals("dummy")
						&& !this.getNode(i).getServiceType().equals("start")
						&& !this.getNode(i).getServiceType().equals("finish")){
					this.setElement(i, j, INFI);
					this.setElement(j, i, INFI);
					continue;
				}
				if(this.getElement(i, j)!=-1)
					continue;
				//Cost of a node to itself is zero
				//If both nodes are of the same service type, they're in a cluster
				//Dummy node has 0 distance from start and beginning and infinite from other nodes

				if(		i==j 
						|| startFinishDummy(i, j)){
					this.setElement(i,j,ZERO);	
				}
				else{
					if(this.getNode(i).getServiceType().equals(
							this.getNode(j).getServiceType())){
						this.setElement(i,j,ZERO);
						//There's only on edge for each node to another node inside the cluster
						int nodeCounter = 2;
						for(int k = 1; k < numberOfNodes-2; k++){
							if(k == i || k == j)
								continue;
							else
								if(this.getNode(i).getServiceType().equals(
										this.getNode(k).getServiceType())){
									nodeCounter++;
									//There's only one edge and it's with the successor
									if(this.getElement(i, k) == -1)
										this.setElement(i,k,INFI);
									if((counter.get(this.getNode(i).getServiceType()) == 1) &&
											(nodeCounter == serviceCount.get(this.getNode(i).getServiceType())))
										continue;
									if(this.getElement(k, i) == -1)
										this.setElement(k,i,INFI);
								}


						}
						// i == j's predecessor
						pred[j] = i;
						suc[i] = j;
						//If there are only two nodes in a cluster it's a two-way edge
						//Else it's a oneway cycle
						if(serviceCount.get(this.getNode(i).getServiceType()) > 2)
							this.setElement(j,i,INFI);
					}
					else{
						//double dist = calculateCost(i, j);
						//In order to use Eucledean distance only
						if(notReal)
							this.setElement(i, j, calculateCost(i,j));
						else{
							double utilValue = utilityFunction
									.calculateUtility(this.getNode(i), this.getNode(j)).getUtility();
							this.setElement(i, j, utilValue);
						}
					}
				}


			}
		}
		//Now let's change the matrix to fit the GTSP
		//Copying the matrix
		double tempMatrix[][] = new double[numberOfNodes][];
		for(int i = 0; i < numberOfNodes; i++){
			tempMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
		}

		for(int i = 0; i < numberOfNodes; i++)
			for(int j = 0; j < numberOfNodes; j++){
				if(			i == j
						|| this.getNode(i).getServiceType().equals(
								this.getNode(j).getServiceType()) ){
					continue;
				}
				//Change the value to its successor's
				this.setElement(i, j, tempMatrix[suc[i]][j]);

			}
	}

}
