package com.SmartCity.tsp;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.SmartCity;
import com.SmartCity.smartcity.Utils;

public class MyParticleSwarm {

	public final static int POPULATION_SIZE = 5;
	public final static int RANDOM_SEED = 1234;
	public final static int ALFA = 1;
	public final static int BETA = 2;

	public static Random random = new Random(RANDOM_SEED);
	public static Random realRandom;

	private UtilityFunction utilityFunction;
	private List<MyParticle> myParticles;
	private MyFileWriter myFileWriter = new MyFileWriter();

	private HashMap<Integer, SmartCity> map;
	private double[][] costs;
	private int numNodes;
	// private List<List<String>> permute;
	private List<Integer> gBest;
	private Double gBFitness;
	private MyParticle gBParticle;

	public static MappingUnits mapping;

	public MyParticleSwarm(SmartCity start, List<SmartCity> list, SmartCity finish, String[] compositeServices) {
		// Four list of particles for four permutations
		// permute = new LinkedList<List<String>>();
		gBest = new LinkedList<Integer>();
		// gBFitness = new LinkedList<Double>();

		myParticles = new LinkedList<MyParticle>();

		// myParticles.add(new LinkedList<MyParticle>());
		gBFitness = 0.0; // Initializing global fitness

		mapping = new MappingUnits(compositeServices);
		mapping.createMap(start, list, finish);
		mapping.setCompositeServices(compositeServices);
		mapping.createMapAssist();
		utilityFunction = new UtilityFunction();

		realRandom = new Random(RANDOM_SEED);
		numNodes = compositeServices.length + 2;

		// gBest.add(new LinkedList<Integer>());
		// Creating four permutations of the list in order to create particles
		// based on these
		List<String> compositeList = new LinkedList<String>();
		for (int i = 0; i < compositeServices.length; i++)
			compositeList.add(compositeServices[i]);
		/*
		 * for (int i = 0; i < 4; i++) { Collections.shuffle(compositeList);
		 * List<String> tempList = new LinkedList<String>(compositeList);
		 * permute.add(i, tempList); }
		 */

	}

	void initParticles() throws Exception {
		// Calculate cache for QoS
		if (Utils.isOld(mapping.getLastUpdate()) || mapping.isFirstRequest())
			calculateStartFinishDist();

		List<MyParticle> tempParticles = new LinkedList<MyParticle>();
		for (int i = 0; i < POPULATION_SIZE; i++) {
			// Create a new particle with this specific sequence of services
			MyParticle particle = new MyParticle();
			myParticles.add(particle);
		}
		// myParticles.add(tempParticles); // Add particles to the jth list

		gBParticle = new MyParticle(); // For holding the best particle
	}

	void moveParticles() throws Exception {
		Random random = new Random();
		int index = 0;
		for (MyParticle particle : myParticles) {

			List<MySwapOperator> tempVelocity = new LinkedList<MySwapOperator>();
			List<Integer> solutiongBest = new LinkedList<Integer>(gBest); // Get																					// gBest
			List<Integer> solutionpBest = new LinkedList<Integer>(particle.getpBest()); // Get																					// pBest
			List<Integer> solutionParticle = new LinkedList<Integer>(particle.getSolution()); // Get																								// current																					// solution
			int numbNodes = mapping.getCompositeServices().length + 2; // Total number of nodes
			// Find the service type of the ith node
			// generates all swap operators to calculate (pbest - x(t-1))
			for (int i=1; i<numbNodes-1; i++) {
				int solNode = solutionParticle.get(i);
				String service = mapping.getMap().get(solNode).getServiceType();	//Fetch node service
				if (solutionEquals(solutionParticle, solutionpBest, service))
					continue;
				// Make the operator
				int nodeNumber = getNodeNum(solutionpBest, service);
				int srcNodeNumber = getNodeNum(solutionParticle, service);
				int nodeIndex = solutionpBest.indexOf(nodeNumber);
				MySwapOperator MySwapOperator = new MySwapOperator(i, nodeIndex, srcNodeNumber, nodeNumber, 1);
				//Operator to swap i with j(nodeIndex)
				// Append it to the list
				tempVelocity.add(MySwapOperator);
				
				int temp = solutionpBest.get(MySwapOperator.getNode1());
				solutionpBest.set(MySwapOperator.getNode1(), MySwapOperator.getNodeNum1());
				solutionpBest.set(MySwapOperator.getNode2(), temp);
			}
			// generates all swap operators to calculate (gbest - x(t-1))
			for (int i=1; i<numbNodes-1; i++) {
				int solNode = solutionParticle.get(i);
				String service = mapping.getMap().get(solNode).getServiceType();	//Fetch node service
				if (solutionEquals(solutionParticle, solutiongBest, service))
					continue;
				// Make the operator
				int nodeNumber = getNodeNum(solutiongBest, service);
				int srcNodeNumber = getNodeNum(solutionParticle, service);
				int nodeIndex = solutiongBest.indexOf(nodeNumber);
				MySwapOperator MySwapOperator = new MySwapOperator(i, nodeIndex, srcNodeNumber, nodeNumber, 1);
				//Operator to swap i with j(nodeIndex)
				// Append it to the list
				tempVelocity.add(MySwapOperator);
				
				int temp = solutiongBest.get(MySwapOperator.getNode1());
				solutiongBest.set(MySwapOperator.getNode1(), MySwapOperator.getNodeNum1());
				solutiongBest.set(MySwapOperator.getNode2(), temp);

			}

			particle.setVelocity(tempVelocity); // Set particle velocity
			for (MySwapOperator MySwapOperator : tempVelocity) { // Now add the
																// velocity
				// TODO:Make probability right, it's always zero
				double probability = random.nextInt(100) / 100;
				if (probability <= MySwapOperator.getProbability()) {
					// If its less than probability, set
					int temp = solutionParticle.get(MySwapOperator.getNode2());
					solutionParticle.set(MySwapOperator.getNode1(), MySwapOperator.getNodeNum2());
					if(MySwapOperator.getNode1() != MySwapOperator.getNode2())
						solutionParticle.set(MySwapOperator.getNode2(), temp);
				}

			}
			Collections.copy(particle.getSolution(), solutionParticle); // Update
																		// current
																		// solution
			particle.calculateFitness(); // Calculate fitness
			if (particle.getFitness() > particle.getpBFitness()) {
				particle.setpBFitness(particle.getFitness()); // Set pbest
																// fitness		 
				particle.setpBest(particle.getSolution());		// Change														// pbest
			}

			index++; // Keeping the index of the list
		}

	}

	void updategBest() throws Exception {
		int index = 0;
			MyParticle tempBest = new MyParticle();
			tempBest = new MyParticle(myParticles.get(0));
			for (MyParticle particle : myParticles) {
				if (particle.getFitness() > tempBest.getFitness()) {
					tempBest = particle;
				}

			}
			gBest = tempBest.getSolution(); // Add the solution to the
														// gBest
			gBFitness = tempBest.getFitness(); // Update fitness
			gBParticle = new MyParticle(tempBest);
			index++; // Keeping the index of the list

		
	}

	void calculateStartFinishDist() throws Exception {
		// Calculate distance from start and finish once and store it in cache
		int totalSize = mapping.getMap().size();

		// Start's index is 0 in map and finish's is last(size)
		int startIdx = 0;
		int finishIdx = totalSize - 1;

		for (Entry<Integer, SmartCity> entry : mapping.getMap().entrySet()) {
			if (entry.getKey() != startIdx && entry.getKey() != finishIdx) {
				// Distance from start
				//Distance from start
				utilityFunction.calculateUtility(mapping.getMap().get(startIdx), entry.getValue());
				mapping.setRealUtilEle(0,entry.getKey(),utilityFunction.getAttributes().getUtility());
				mapping.setRealDistEle(0,entry.getKey(),utilityFunction.getAttributes().getRealDist());
				mapping.setRealDurEle(0,entry.getKey(),utilityFunction.getAttributes().getDuration());
				mapping.setDistEle(0,entry.getKey(),utilityFunction.getAttributes().getDist());
				mapping.setServiceTimeEle(0,entry.getKey(),utilityFunction.getAttributes().getServiceTime());
				//Util
				mapping.setRealDistUtilEle(0,entry.getKey(),utilityFunction.getAttributes().getRealDistUtil());
				mapping.setRealDurUtilEle(0,entry.getKey(),utilityFunction.getAttributes().getDurationUtil());
				mapping.setRealQualUtilEle(0,entry.getKey(),utilityFunction.getAttributes().getQualityUtil());

				//Distance to finish
				utilityFunction.calculateUtility(entry.getValue(),mapping.getMap().get(finishIdx));
				mapping.setRealUtilEle(1,entry.getKey(),utilityFunction.getAttributes().getUtility());
				mapping.setRealDistEle(1,entry.getKey(),utilityFunction.getAttributes().getRealDist());
				mapping.setRealDurEle(1,entry.getKey(),utilityFunction.getAttributes().getDuration());
				mapping.setDistEle(1,entry.getKey(),utilityFunction.getAttributes().getDist());
				mapping.setServiceTimeEle(1,entry.getKey(),utilityFunction.getAttributes().getServiceTime());
				//Util
				mapping.setRealDistUtilEle(1,entry.getKey(),utilityFunction.getAttributes().getRealDistUtil());
				mapping.setRealDurUtilEle(1,entry.getKey(),utilityFunction.getAttributes().getDurationUtil());
				mapping.setRealQualUtilEle(1,entry.getKey(),utilityFunction.getAttributes().getQualityUtil());
			}
		}
		mapping.setFirstRequest(false);

	}
	
	boolean solutionEquals(List<Integer> list1, List<Integer> list2, String service){
		//Determines if a service is complete equal in both lists with regard to the concrete service
		//And its location
		int nodeNum1 = 0, nodeNum2 = 0;
		for(Integer nodeNum : list1){
			if(mapping.getMap().get(nodeNum).getServiceType().equals(service) )
				nodeNum1 = nodeNum;
		}
		for(Integer nodeNum : list2){
			if(mapping.getMap().get(nodeNum).getServiceType().equals(service) )
				nodeNum2 = nodeNum;
		}
		if((nodeNum1 == nodeNum2) && (list1.indexOf(nodeNum1) == list2.indexOf(nodeNum2)))
			return true;
		else
			return false;
	}
	
	int getNodeNum(List<Integer> list, String service){
		int tempNodeNum = 0;
		for(Integer nodeNum : list){
			if(mapping.getMap().get(nodeNum).getServiceType().equals(service))
				tempNodeNum = nodeNum;
		}
		return tempNodeNum;
	}
	
	public UtilityFunction getUtilityFunction() {
		return utilityFunction;
	}

	public void setUtilityFunction(UtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	public List<MyParticle> getParticles() {
		return myParticles;
	}

	public void setParticles(List<List<Particle>> particles) {
		this.myParticles = myParticles;
	}

	public MyFileWriter getMyFileWriter() {
		return myFileWriter;
	}

	public void setMyFileWriter(MyFileWriter myFileWriter) {
		this.myFileWriter = myFileWriter;
	}

	public HashMap<Integer, SmartCity> getMap() {
		return map;
	}

	public void setMap(HashMap<Integer, SmartCity> map) {
		this.map = map;
	}

	public double[][] getCosts() {
		return costs;
	}

	public void setCosts(double[][] costs) {
		this.costs = costs;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}



	public List<Integer> getgBest() {
		return gBest;
	}

	public void setgBest(List<Integer> gBest) {
		this.gBest = gBest;
	}

	public Double getgBFitness() {
		return gBFitness;
	}

	public void setgBFitness(Double gBFitness) {
		this.gBFitness = gBFitness;
	}

	public MyParticle getgBParticle() {
		return gBParticle;
	}

	public void setgBParticle(MyParticle gBParticle) {
		this.gBParticle = gBParticle;
	}

}
