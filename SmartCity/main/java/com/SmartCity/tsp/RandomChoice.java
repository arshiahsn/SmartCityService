package com.SmartCity.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.SmartCity;
import com.SmartCity.tsp.Environment.CustomComparatorCh;

public class RandomChoice implements Runnable{
//Implementing a random choice algorithm based on genetic algorithm with one chromosome
	public final static int RANDOM_SEED = 2345;
	public static Random realRandom;
	public static MappingUnits mapping;
	
	
	private Chromosome chromosome;
	
	private MyFileWriter myFileWriter;

	public RandomChoice(SmartCity start, 
			List<SmartCity> list, 
			SmartCity finish,
			String[] compositeServices){

		//map = new HashMap<Integer,SmartCity>();
		//mapAssist = new HashMap<String,List<Integer>>();
		mapping = new MappingUnits(compositeServices);
		mapping.createMap(start,list,finish);	
		//this.compositeServices = compositeServices;
		mapping.setCompositeServices(compositeServices);
		mapping.createMapAssist();
		myFileWriter = new MyFileWriter();
		int realRandSeed = randomGenerator();
		realRandom = new Random(realRandSeed);
	}
	
	public int randomGenerator(){
		List<Integer> list = new ArrayList<Integer>();
		for(int i=1000; i<2000; i++)
			list.add(i);
		Collections.shuffle(list);
		return list.get(0);
	}
	
	public void init() throws Exception{		
		Chromosome chromosome = new Chromosome();
		//Initializing genes and then sorting them based on their random-key
		chromosome.initGenes();
		chromosome.sort();
		chromosome.calculateFitness();
		
		setChromosome(chromosome);
	}
	
	public List<SmartCity> getMinRouteList(){
		List<SmartCity> maxRoute = new LinkedList<SmartCity>();
		//First chromosome with highest fitness
		for(Gene gene : chromosome.getGenes()){
			maxRoute.add(gene.getGeneNode());
		}
		return maxRoute;
	}
	
	Chromosome getChromosome(){
		return chromosome;
	}
	void setChromosome(Chromosome chromosome){
		this.chromosome = chromosome;
	}
	
	@Override
	public void run() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Random choice: utility: "+getChromosome().getFitness()+"\n");
		double aggUtil = getChromosome().getFitness();
		double aggDist = getChromosome().getDist();
		double aggRealDist = getChromosome().getRealDist();
		double aggDuration = getChromosome().getDuration();
		double aggQuality = getChromosome().getQuality();
		double aggServTime = getChromosome().getServiceTime();
		int reqCount = getChromosome().getGeneCount()-2;
		myFileWriter.write("util-ra"+String.valueOf(reqCount)+"req",String.valueOf(aggUtil));
		myFileWriter.write("dist-ra"+String.valueOf(reqCount)+"req",String.valueOf(aggDist));
		myFileWriter.write("realDist-ra"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDist));
		myFileWriter.write("qual-ra"+String.valueOf(reqCount)+"req",String.valueOf(aggQuality));
		myFileWriter.write("dur-ra"+String.valueOf(reqCount)+"req",String.valueOf(aggDuration));
		myFileWriter.write("servTime-ra"+String.valueOf(reqCount)+"req",String.valueOf(aggServTime));
		myFileWriter.write("totalTime-ra"+String.valueOf(reqCount)+"req",String.valueOf(aggServTime+aggDuration));
	}

}
