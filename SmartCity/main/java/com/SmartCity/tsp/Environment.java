package com.SmartCity.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import static java.lang.Math.toIntExact;

/*
 * Created on 06-Jan-2006
 * 
 * This source code is released under the GNU Lesser General Public License Version 3, 29 June 2007
 * see http://www.gnu.org/licenses/lgpl.html or the plain text version of the LGPL included with this project
 * 
 * It comes with no warranty whatsoever
 */


import java.util.Random;

import org.apache.catalina.servlet4preview.http.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Component;

import java.util.Map.Entry;
import java.util.Objects;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.Location;
import com.SmartCity.smartcity.SmartCity;
import com.SmartCity.smartcity.Utils;
import com.SmartCity.tsp.TravelingSalesman;

@Component
public class Environment implements Runnable{


	public final static int RANDOM_SEED = 1234;

	public final static int POPULATION_SIZE = 100;//must be even number	
	public final static int SUPERIOR_SIZE = 20;	
	public final static int CROSSOVER_SIZE = 70;	
	public final static int IMMIGRATION_SIZE = 10;


	public static Random random = new Random(RANDOM_SEED);
	private Random realRandom;
	
	private UtilityFunction utilityFunction;	
	public List<Chromosome> chromosomes;
	private MyFileWriter myFileWriter = new MyFileWriter();


	/*
	@Autowired
	public HashMap<Integer, SmartCity> map;
	@Autowired
	public HashMap<String,List<Integer>> mapAssist;
	@Autowired
	public String[] compositeServices;
	 */

	public static MappingUnits mapping;

	public Environment(SmartCity start, 
			List<SmartCity> list, 
			SmartCity finish,
			String[] compositeServices){

		//map = new HashMap<Integer,SmartCity>();
		//mapAssist = new HashMap<String,List<Integer>>();
		chromosomes = new LinkedList<Chromosome>();
		mapping = new MappingUnits(compositeServices);
		mapping.createMap(start,list,finish);	
		utilityFunction = new UtilityFunction();
		//this.compositeServices = compositeServices;
		mapping.setCompositeServices(compositeServices);
		mapping.createMapAssist();
		realRandom = new Random();
		
	}
	
	public static int randomGenerator(){
		List<Integer> list = new ArrayList<Integer>();
		for(int i=1000; i<2000; i++)
			list.add(i);
		Collections.shuffle(list);
		return list.get(0);
	}

	//Return node from key

	//Return key from node
	public Integer getKeyByValue(SmartCity value) {
		for (Entry<Integer, SmartCity> entry : mapping.getMap().entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}


	public void run(){

		int generation = 0;
		//main loop
		//Initializing chromosomes
		try
		{
			initChromosomes();
			while(generation<=10){
				//Generating new offsprings
				generateOffSprings();
				generation++;
				if(generation % 10 == 0){
					System.out.println("\nGeneration: "+generation+", utility: "+getBestChromosome().getFitness());
					System.out.print("\nBest chromosome: ");
					printBestChromosome();

				}
			}
			//Write quality into file
			//It's normalized according to number of nodes(genes)
			//n genes indicate n-1 utilities
			//double aggUtil = getBestChromosome().getFitness();
			double aggUtil = getBestChromosome().getFitness();
			double aggRealDist = getBestChromosome().getRealDist();
			double aggDur = getBestChromosome().getDuration();
			double aggQual = getBestChromosome().getQuality();
			double aggDist = getBestChromosome().getDist();
			double aggServTime = getBestChromosome().getServiceTime();
			double aggTotalTime = aggServTime + aggDur;
			int reqCount = (int) getBestChromosome().getGeneCount()-2;
			double aggRealDistUtil = getBestChromosome().getRealDistUtil();
			double aggDurationUtil = getBestChromosome().getDurationUtil();
			double aggQualityUtil = getBestChromosome().getQualityUtil();

			
			myFileWriter.write("util-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggUtil));
			myFileWriter.write("realDist-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDist));
			myFileWriter.write("dur-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggDur));
			myFileWriter.write("qual-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggQual));
			myFileWriter.write("dist-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggDist));
			myFileWriter.write("servTime-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggServTime));
			myFileWriter.write("totalTime-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggTotalTime));
			myFileWriter.write("realDistUtil-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDistUtil));
			myFileWriter.write("realDurUtil-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggDurationUtil));
			myFileWriter.write("qualUtil-ga"+String.valueOf(reqCount)+"req",String.valueOf(aggQualityUtil));


		}
		catch (Exception e){
			e.printStackTrace();
		}

	}
	void printBestChromosome(){
		List<Gene> genes= chromosomes.get(0).getGenes();
		for(Gene gene : genes){
			System.out.print(this.getKeyByValue(gene.geneNode)+" ");
		}
		System.out.print("\n");
	}
	Chromosome getBestChromosome(){
		return chromosomes.get(0);
	}
	public List<SmartCity> getMinRouteList(){
		List<SmartCity> maxRoute = new LinkedList<SmartCity>();
		Collections.sort(chromosomes, new CustomComparatorCh());
		//First chromosome with highest fitness
		List<Gene> desigGenes = chromosomes.get(0).getGenes();
		for(Gene gene : desigGenes){
			maxRoute.add(gene.getGeneNode());
		}
		return maxRoute;
	}

	void initChromosomes() throws Exception{
		//		List<Chromosome> chromosomes = new LinkedList<Chromosome>();
		//Calculating real distance for start and finish points
		if(Utils.isOld(mapping.getLastUpdate()) || mapping.isFirstRequest())
			calculateStartFinishDist();

		for(int i=0; i<POPULATION_SIZE; i++){			
			Chromosome chromosome = new Chromosome();
			//Initializing genes and then sorting them based on their random-key
			chromosome.initGenes();
			chromosome.sort();
			//chromosome.swap();
			//chromosome.twoOpt();
			chromosome.calculateFitness();
			//Add the chromosome to the list
			this.chromosomes.add(chromosome);
		}

		//calculateFitness();
	}



	void generateOffSprings() throws Exception{
		/*20 percent are chosen from the old chromosomes
		  70 percent are made from cross-over
		  10 percent are completely new
		 * */
		List<Chromosome> offSprings = new LinkedList<Chromosome>();
		Collections.sort(chromosomes, new CustomComparatorCh().reversed());
		//Add first twenty percent from the old generation
		for(int i=0; i<SUPERIOR_SIZE; i++){
			offSprings.add(chromosomes.get(i));
		}
		for(int i=0; i<CROSSOVER_SIZE; i++){
			int size = chromosomes.size()-1;
			int firstParentIdx = realRandom.nextInt(size);
			int secondParentIdx = realRandom.nextInt(size);

			Chromosome firstParent = chromosomes.get(firstParentIdx);
			Chromosome secondParent = chromosomes.get(secondParentIdx);	

			Chromosome offSpring = new Chromosome();

			offSpring.crossOver(firstParent, secondParent);
			offSpring.sort();
			//offSpring.swap();
			//offSpring.twoOpt();
			offSpring.calculateFitness();
			offSprings.add(offSpring);
		}
		for(int i=0; i<IMMIGRATION_SIZE; i++){
			Chromosome chromosome = new Chromosome();
			//Initializing genes and then sorting them based on their random-key
			chromosome.initGenes();
			chromosome.sort();
			//chromosome.swap();
			//chromosome.twoOpt();
			chromosome.calculateFitness();
			offSprings.add(chromosome);
		}
		chromosomes = new LinkedList<Chromosome>(offSprings);
		//calculateFitness();
	}


	public List<Chromosome> getChromosomes() {
		return chromosomes;
	}
	public void setChromosomes(List<Chromosome> chromosomes) {
		this.chromosomes = chromosomes;
	}


	public class CustomComparatorCh implements Comparator<Chromosome> {
		@Override
		public int compare(Chromosome o1, Chromosome o2) {
			return o1.getFitness().compareTo(o2.getFitness());
		}
	}
	void calculateStartFinishDist(){
		//Calculate distance from start and finish once and store it in a Database
		int totalSize = mapping.getMap().size();
		
		//Start's index is 0 in map and finish's is last(size)
		int startIdx = 0;
		int finishIdx = totalSize-1;

		for(Entry<Integer, SmartCity> entry : mapping.getMap().entrySet()){
			if(entry.getKey()!=startIdx && entry.getKey()!=finishIdx){
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
	//A function to calculate final route's utility
	//Not needed here
	/*
	public void printRouteAndCostPrime(List<SmartCity> route){
		UtilityFunction utilityFunction = new UtilityFunction();
		System.out.print("Final route prime: ");
		for(SmartCity node : route){
			System.out.print(getKeyByValue(node)+" ");
		}
		System.out.print("\n");
		
		ListIterator<SmartCity> liter = route.listIterator();
		double cumuUtility = 0;
		SmartCity listIdx = liter.next();
		while(liter.hasNext()){
			SmartCity listNextIdx = liter.next();
			//cumuUtility += costs[getKeyByValue(listIdx)][getKeyByValue(listNextIdx)];
			cumuUtility += utilityFunction.calculateUtility(listIdx, listNextIdx);
			listIdx = listNextIdx;
		}
		System.out.print("Cost: "+cumuUtility+"\n");
	}

	 */




}

