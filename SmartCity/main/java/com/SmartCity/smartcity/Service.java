package com.SmartCity.smartcity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SmartCity.tsp.AdjacencyMatrix;
import com.SmartCity.tsp.AntTsp;
import com.SmartCity.tsp.AntTspSolver;
import com.SmartCity.tsp.EecParticleSwarm;
import com.SmartCity.tsp.EecParticleSwarmOpt;
import com.SmartCity.tsp.Environment;
import com.SmartCity.tsp.GreedyTSPSolver;
import com.SmartCity.tsp.GreedyTravelingSalesman;
import com.SmartCity.tsp.MyParticleSwarm;
import com.SmartCity.tsp.MyParticleSwarmOpt;
import com.SmartCity.tsp.ParticleSwarm;
import com.SmartCity.tsp.ParticleSwarmOpt;
import com.SmartCity.tsp.RandomChoice;
import com.SmartCity.tsp.TravelingSalesman;
import com.SmartCity.tsp.TravelingSalesmanBruteForce;


public class Service {

	private Location sourceLocation;
	private String[] compositeServices;
	private Location destinationLocation;
	


	public Service(){
	}
	public Service(Location sourceLocation,
			String[] compositeServices,
			Location destinationLocation){
		this.sourceLocation = sourceLocation;
		this.compositeServices = compositeServices;
		this.destinationLocation = destinationLocation;		
	}




	@Override
	public String toString(){
		return "Service{" +
				"sourceLocation=" + sourceLocation +
				", compositeServices=" + Arrays.toString(compositeServices) +
				", destinationLocation=" + destinationLocation +
				'}';
	}
	public List<SmartCity> scheduleServices() throws Exception{
		//TODO:Google API
		//TODO:Schedule services based on Google Cost and Quality
		//A HashMap of ID and Service Type to hold found nodes with defined services

		List<SmartCity> foundNodes = new ArrayList<SmartCity>();
		//TODO:Fix this
		foundNodes = Utils.iterativeSearch(sourceLocation, 
				compositeServices, 
				destinationLocation);

		/*for(String serviceString : compositeServices)
			for (SmartCity node : repository.findAll()) 
				if(serviceString.equals(node.getServiceType()))
				{
					foundNodes.add(node);
				}*/

		try {
			Utils.updateFromNodes(foundNodes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Run TSP Algorithm on nodes
		//Bruteforce
		/*
		AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(
				new SmartCity(0,sourceLocation,"start"),
				foundNodes,
				new SmartCity(0,destinationLocation,"finish"));

		adjacencyMatrix.calculateAdjacencyMatrix();
		
		TravelingSalesman salesman = new TravelingSalesman(adjacencyMatrix.getMatrix(),
				adjacencyMatrix.getMap());		
		salesman.printCosts();
		//Bruteforce or Genetic
		TravelingSalesmanBruteForce bruteForce = new TravelingSalesmanBruteForce(salesman);
		//Thread threadOne = new Thread(bruteForce);
		bruteForce.run();
		//threadOne.start();
		//threadOne.join();
		*/
		//Initializing the environment for Genetic Algorithm
		
		
		/*
		//Ant Colony Algorithm
		AdjacencyMatrix antAdjacencyMatrix = new AdjacencyMatrix(
				new SmartCity(0,sourceLocation,"start"),
				foundNodes,
				new SmartCity(0,destinationLocation,"finish"));

		antAdjacencyMatrix.calculateAdjacencyMatrix();		
		AntTsp antTsp = new AntTsp(antAdjacencyMatrix, 
				antAdjacencyMatrix.getMap());
		AntTspSolver antTspSolver = new AntTspSolver(antTsp);
		antTspSolver.run();
		*/
		
		//Genetic algorithm
		Environment environment = new Environment(
				new SmartCity(0,sourceLocation,"start"),
				foundNodes,
				new SmartCity(0,destinationLocation,"finish"),
				compositeServices);
		//Thread threadOne = new Thread(environment);
		environment.run();
		//threadOne.start();
		//threadTwo.join();
		
		
		
		//Random choice	
		/*
		RandomChoice randomChoice = new RandomChoice(				
				new SmartCity(0,sourceLocation,"start"),
				foundNodes,
				new SmartCity(0,destinationLocation,"finish"),
				compositeServices);
		randomChoice.run();
		 */
		
		/*
		//Greedy algorithm
		AdjacencyMatrix greedyAdjacencyMatrix = new AdjacencyMatrix(
				new SmartCity(0,sourceLocation,"start"),
				foundNodes,
				new SmartCity(0,destinationLocation,"finish"),
				true);

		greedyAdjacencyMatrix.calculateAdjacencyMatrix();		
		GreedyTravelingSalesman greedyTravelingSalesman = new GreedyTravelingSalesman(greedyAdjacencyMatrix, 
				greedyAdjacencyMatrix.getMap());
		GreedyTSPSolver greedyTSPSolver = new GreedyTSPSolver(greedyTravelingSalesman);
		greedyTSPSolver.run();
		*/
		//Particle Swarm Optimization
		/*
		ParticleSwarm particleSwarm = new ParticleSwarm(
				new SmartCity(0,sourceLocation,"start"),
				foundNodes,
				new SmartCity(0,destinationLocation,"finish"),
				compositeServices
				);
		ParticleSwarmOpt particleSwarmOpt= new ParticleSwarmOpt(particleSwarm);
		particleSwarmOpt.run();
		//Thread threadTwo = new Thread(particleSwarmOpt);
		//threadTwo.start();
		
		
		//My Particle Swarm Optimization
		MyParticleSwarm myParticleSwarm = new MyParticleSwarm(
				new SmartCity(0,sourceLocation,"start"),
				foundNodes,
				new SmartCity(0,destinationLocation,"finish"),
				compositeServices
				);
		MyParticleSwarmOpt myParticleSwarmOpt= new MyParticleSwarmOpt(myParticleSwarm);
		myParticleSwarmOpt.run();
		//Thread threadThree = new Thread(myParticleSwarmOpt);
		//threadThree.start();
		
		
		//EecParticle Swarm
		EecParticleSwarm eecParticleSwarm = new EecParticleSwarm(
				new SmartCity(0,sourceLocation,"start"),
				foundNodes,
				new SmartCity(0,destinationLocation,"finish"),
				compositeServices
				);
		EecParticleSwarmOpt eecParticleSwarmOpt = new EecParticleSwarmOpt(eecParticleSwarm);
		eecParticleSwarmOpt.run();
		*/
		
		//randomChoice.getMinRouteList();
		//return bruteForce.getMinRouteList();
		//return greedyTSPSolver.getMinRouteList();
		//return antTspSolver.getMinRouteList();
		return environment.getMinRouteList();
		//return particleSwarmOpt.getMinRouteList()
		//return eecParticleSwarmOpt.getMinRouteList();
		//return myParticleSwarmOpt.getMinRouteList();

	}





	public Location getSourceLocation() {
		return sourceLocation;
	}
	public void setSourceLocation(Location sourceLocation) {
		this.sourceLocation = sourceLocation;
	}
	public String[] getCompositeServices() {
		return compositeServices;
	}
	public void setCompositeServices(String[] compositeServices) {
		this.compositeServices = compositeServices;
	}
	public Location getDestinationLocation() {
		return destinationLocation;
	}
	public void setDestinationLocation(Location destinationLocation) {
		this.destinationLocation = destinationLocation;
	}



}
