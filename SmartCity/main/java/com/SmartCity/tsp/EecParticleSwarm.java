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

public class EecParticleSwarm {

	public final static int POPULATION_SIZE = 20;
	public final static int RANDOM_SEED = 1234;
	public final static int ALFA = 1;
	public final static int BETA = 2;
	
	public final static double C1 = 2.0;
	public final static double C2 = 2.0;
	public final static double W_UPPERBOUND = 1.0;
	public final static double W_LOWERBOUND = 0.0;

	public static Random random = new Random(RANDOM_SEED);
	public static Random realRandom;

	private UtilityFunction utilityFunction;
	private List<List<EecParticle>> particles;
	private MyFileWriter myFileWriter = new MyFileWriter();

	private HashMap<Integer, SmartCity> map;
	private double[][] costs;
	private int numNodes;
	private List<List<String>> permute;
	private List<List<Integer>> gBest;
	private List<Double> gBFitness;
	private EecParticle gBParticle;
	
	public static MappingUnits mapping;

	public EecParticleSwarm(SmartCity start, List<SmartCity> list, SmartCity finish, String[] compositeServices) {
		// Four list of particles for four permutations
		permute = new LinkedList<List<String>>();
		gBest = new LinkedList<List<Integer>>();
		gBFitness = new LinkedList<Double>();

		particles = new LinkedList<List<EecParticle>>();
		for (int i = 0; i < 4; i++){
			particles.add(new LinkedList<EecParticle>());
			gBFitness.add(0.0);							//Initializing global fitness
		}
		mapping = new MappingUnits(compositeServices);
		mapping.createMap(start, list, finish);
		mapping.setCompositeServices(compositeServices);
		mapping.createMapAssist();
		utilityFunction = new UtilityFunction();
		
		realRandom = new Random(RANDOM_SEED);
		numNodes = compositeServices.length + 2;
		for (int i = 0; i < 4; i++)
			gBest.add(new LinkedList<Integer>());
		// Creating four permutations of the list in order to create particles
		// based on these
		List<String> compositeList = new LinkedList<String>();
		for (int i = 0; i < compositeServices.length; i++)
			compositeList.add(compositeServices[i]);
		for (int i = 0; i < 4; i++) {
			Collections.shuffle(compositeList);
			List<String> tempList = new LinkedList<String>(compositeList);
			permute.add(i, tempList);
		}
		
		

	}

	void initParticles() throws Exception{
		//Calculate cache for QoS
		if(Utils.isOld(mapping.getLastUpdate()) || mapping.isFirstRequest())
			calculateStartFinishDist();
		
		for (int j = 0; j < 4; j++) {
			List<EecParticle> tempParticles = new LinkedList<EecParticle>();
			for (int i = 0; i < POPULATION_SIZE; i++) {
				// Create a new particle with this specific sequence of services
				EecParticle particle = new EecParticle(getPermute().get(j));
				tempParticles.add(particle);
			}
			particles.set(j, tempParticles); // Add particles to the jth list
		}
		gBParticle = new EecParticle(permute.get(0));				//For holding the best particle
	}

	void moveParticles(int t) throws Exception{
		Random random = new Random();
		int index = 0;
		double w = W_UPPERBOUND - (((double) t) / 100) * (W_UPPERBOUND - W_LOWERBOUND);	//Inertia
		
		for (List<EecParticle> particleList : particles) {

			for (EecParticle particle : particleList) {
				List<Double> tempVelocity = new LinkedList<Double>(particle.getVelocity());
				List<Integer> solutiongBest = new LinkedList<Integer>(gBest.get(index)); // Get
																							// gBest
				List<Integer> solutionpBest = new LinkedList<Integer>(particle.getpBest()); // Get
																							// pBest
				List<Integer> solutionParticle = new LinkedList<Integer>(particle.getSolution()); // Get
																									// current
																									// solution
				int numbNodes = permute.get(0).size() + 2; // Total number of
															// nodes
				// Find the service type of the ith node
				// generates all swap operators to calculate (pbest - x(t-1))
				
				double r1 = random.nextDouble();
				double r2 = random.nextDouble();
				
				for (int i = 1; i < numbNodes - 1; i++) {
					//if (solutionpBest.get(i).equals(particle.getSolution().get(i)))
					//	continue;
					// Make the operator
					Double tempVelo = w * tempVelocity.get(i) +
					(r1 * C1) * (solutionpBest.get(i) - solutionParticle.get(i)) +
					(r2 * C2) * (solutiongBest.get(i) - solutionParticle.get(i));
					
					tempVelocity.set(i, tempVelo);
					//particle.getVelocity().set(i, element)
					// Append it to the list


				}
				particle.setVelocity(tempVelocity); // Set particle velocity
				
				for (int i = 1; i < numbNodes - 1; i++) {
				double tempLoc = solutionParticle.get(i) + tempVelocity.get(i);
				solutionParticle.set(i, (int) round(tempLoc,0));
				if(solutionParticle.get(i) > particle.getvMax().get(i))	//Upper bound
					solutionParticle.set(i, particle.getvMax().get(i));
				if(solutionParticle.get(i) < particle.getvMin().get(i))	//Lower Bound
					solutionParticle.set(i, particle.getvMin().get(i));
				}
				
				//Collections.copy(particle.getSolution(), solutionParticle); // Update
				particle.setSolution(solutionParticle);														// current
				particle.calculateFitness(); // Calculate fitness
				if (particle.getFitness() > particle.getpBFitness()) {
					particle.setpBFitness(particle.getFitness()); // Set pbest
					//Collections.copy(particle.getpBest(), particle.getSolution()); // Change
					particle.setpBest(particle.getSolution());															// pbest
				}
			}
			index++; // Keeping the index of the list
		}

	}

	void updategBest() throws Exception{
		int index = 0;
		for (List<EecParticle> particleList : particles) {
			EecParticle tempBest = particleList.get(0);
			tempBest.calculateFitness();
			for (EecParticle particle : particleList) {
				if(particle.getFitness() > tempBest.getFitness()){
					tempBest = particle;
				}
				
			}
			gBest.set(index, tempBest.getSolution());			//Add the solution to the gBest
			gBFitness.set(index, tempBest.getFitness());		//Update fitness
			gBParticle = new EecParticle(tempBest);
			index++; // Keeping the index of the list
			if(index == 4)
				break;
		}
	}

	void calculateStartFinishDist() throws Exception{
		//Calculate distance from start and finish once and store it in cache
		int totalSize = mapping.getMap().size();
		
		//Start's index is 0 in map and finish's is last(size)
		int startIdx = 0;
		int finishIdx = totalSize-1;

		for(Entry<Integer, SmartCity> entry : mapping.getMap().entrySet()){
			if(entry.getKey()!=startIdx && entry.getKey()!=finishIdx){
				//Distance from start
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
	
	public UtilityFunction getUtilityFunction() {
		return utilityFunction;
	}

	public void setUtilityFunction(UtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	public List<List<EecParticle>> getParticles() {
		return particles;
	}

	public void setParticles(List<List<EecParticle>> particles) {
		this.particles = particles;
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

	public List<List<String>> getPermute() {
		return permute;
	}

	public void setPermute(List<List<String>> permute) {
		this.permute = permute;
	}

	public List<List<Integer>> getgBest() {
		return gBest;
	}

	public void setgBest(List<List<Integer>> gBest) {
		this.gBest = gBest;
	}

	public List<Double> getgBFitness() {
		return gBFitness;
	}

	public void setgBFitness(List<Double> gBFitness) {
		this.gBFitness = gBFitness;
	}

	public EecParticle getgBParticle() {
		return gBParticle;
	}

	public void setgBParticle(EecParticle gBParticle) {
		this.gBParticle = gBParticle;
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

}
