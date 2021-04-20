package tsp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import smartcity.Location;

import smartcity.SmartCity;





public class AdjacencyMatrix {
	private double matrix[][];
	private HashMap<Integer, SmartCity> map;
	private int size;

	public AdjacencyMatrix(SmartCity start, 
			List<SmartCity> list, 
			SmartCity finish){
		size = 3 + list.size();
		matrix = new double[size][size];
		map = this.createMap(start, list, finish);
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


	public HashMap<Integer,SmartCity> createMap(SmartCity start, List<SmartCity> list, 
			SmartCity finish){
		HashMap<Integer,SmartCity> map = new HashMap<Integer,SmartCity>();
		//Start+Finish+List+Dummy node
		//Dummy node is n-1
		int nodeCount = 3 + list.size();
		map.put(0, start);
		map.put(nodeCount-1, finish);
		//Dummy node
		map.put(nodeCount-2, new SmartCity(0, new Location(1000000,1000000), "dummy"));
		for(int i = 1; i < nodeCount-2; i++){
			map.put(i, list.get(i-1));
		}

		return map;
	}
	//TODO:add quality to the cost
	public void calculateAdjacencyMatrix(){
		int pred[] = new int [matrix[1].length];
		int suc[] = new int[matrix[1].length];
		for(int i = 0; i < matrix[1].length; i++){
			suc[i] = i;
			pred[i] = i;
		}

		//Holding number of nodes in a cluster
		HashMap<String,Integer> serviceCount = new HashMap<String,Integer>();
		//Holding number of nodes in a cluster up to now (Important for the last node)
		HashMap<String,Integer> counter = new HashMap<String,Integer>();

		for(int i = 0; i < matrix[1].length; i++){
			if(!serviceCount.containsKey(this.getNode(i).getServiceType())){
				serviceCount.put(this.getNode(i).getServiceType(), 1);
			}
			else{
				int newVal = serviceCount.get(this.getNode(i).getServiceType())+1;
				serviceCount.put(this.getNode(i).getServiceType(), newVal);
			}
			for(int j = 0; j < matrix[1].length; j++)
				this.setElement(i, j, -1);
		}
		for(int i = 0; i < matrix[1].length; i++){
			if(counter.containsKey(this.getNode(i).getServiceType())){
				int incVal = counter.get(this.getNode(i).getServiceType())+1;
				counter.put(this.getNode(i).getServiceType(), incVal);
			}
			else{
				counter.put(this.getNode(i).getServiceType(), 1);
			}
			for(int j = 0; j < matrix[1].length; j++){
				if(this.getElement(i, j)!=-1)
					continue;
				//Cost of a node to itself is zero
				//If both nodes are of the same service type, they're in a cluster
				//Dummy node has 0 distance from start and beginning and infinite from other nodes
				//TODO:fix this

				if(		i==j 
						//Start to finish and Start to Dummy is zero
						|| (i == 0 && j == size-1)
						|| (i == 0 && j == size-2)
						//Dummy to Start and Dummy to finish is zero
						|| (i == size-1 && j == 0)
						|| (i == size-1 && j == size-2)
						//Finish to start and finish to dummy is zero
						|| (i == size-2 && j == 0)
						|| (i == size-2 && j == size-1)

						){
					this.setElement(i,j,0.1);	
				}
				else 
					if(this.getNode(i).getServiceType().equals(
							this.getNode(j).getServiceType())){
						this.setElement(i,j,0.1);
						//There's only on edge for each node to another node inside the cluster
						int nodeCounter = 2;
						for(int k = 1; k < matrix[1].length-2; k++){
							if(k == i || k == j)
								continue;
							else
								if(this.getNode(i).getServiceType().equals(
										this.getNode(k).getServiceType())){
									nodeCounter++;
									//There's only one edge and it's with the successor
									if(this.getElement(i, k) == -1)
										this.setElement(i,k,1000000);
									if((counter.get(this.getNode(i).getServiceType()) == 1) &&
											(nodeCounter == serviceCount.get(this.getNode(i).getServiceType())))
										continue;
									if(this.getElement(k, i) == -1)
										this.setElement(k,i,1000000);
								}


						}
						// i == j's predecessor
						pred[j] = i;
						suc[i] = j;
						//If there are only two nodes in a cluster it's a two-way edge
						//Else it's a oneway cycle
						if(serviceCount.get(this.getNode(i).getServiceType()) > 2)
							this.setElement(j,i,1000000);
					}
					else{
						double dx = (this.getNode(i).getLocation().getLatitude() - 
								this.getNode(j).getLocation().getLatitude());
						double dy = (this.getNode(i).getLocation().getLongitude() - 
								this.getNode(j).getLocation().getLongitude());
						double dist = Math.sqrt(dx*dx+dy*dy);
						this.setElement(i, j, dist);
					}

			}
		}
		//Now let's change the matrix to fit the GTSP
		//Copying the matrix
		double tempMatrix[][] = new double[matrix[1].length][];
		for(int i = 0; i < matrix[1].length; i++){
			tempMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
		}

		for(int i = 0; i < matrix[1].length; i++)
			for(int j = 0; j < matrix[1].length; j++){
				if(			i == j
						|| this.getNode(i).getServiceType().equals(
								this.getNode(j).getServiceType()) ){
					continue;
				}
				//Change the value with its successor's
				this.setElement(i, j, tempMatrix[suc[i]][j]);

			}
	}

}
