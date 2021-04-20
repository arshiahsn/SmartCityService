package com.SmartCity.tsp;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SmartCity.smartcity.SmartCity;

@Component
public class Chromosome {

	private LinkedList<Gene> genes;
	private int size;
	private UtilityFunction utilityFunction;

	private Double fitness;
	public final static int RANDOM_SEED = 1234;
	public final static Random rand = new Random(RANDOM_SEED);
	private Random realRandom;

	private Double dist;
	private Double realDist;
	private Double duration;
	private Integer quality;
	private Double serviceTime;
	
	private Double realDistUtil;
	private Double durationUtil;
	private Double qualityUtil;
	
	private Double util;

	public Chromosome() {
		this.genes = new LinkedList<Gene>();
		utilityFunction = new UtilityFunction();
		realRandom = new Random();
	}

	// Return node from key

	public Double getFitness() {
		return this.fitness;
	}

	public void setFitness(Double fitness) {
		this.fitness = fitness;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public LinkedList<Gene> getGenes() {
		return genes;
	}

	public void setGenes(LinkedList<Gene> genes) {
		this.genes = genes;
	}

	// Return key from node
	public Integer getKeyByValue(SmartCity value) {
		for (Entry<Integer, SmartCity> entry : Environment.mapping.getMap().entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	void calculateFitness() {
		// Calculate fitness for the chromosomes
		// for(Chromosome chromosome : chromosomes){
		// Cumulative utility of a chromosome as fitness
		// Duration and distance also used for measurements
		double cumulativeUtil = 0;
		double cumulativeDist = 0;
		double cumulativeRealDist = 0;
		double cumulativeDuration = 0;
		double cumulativeServTime = 0;
		int cumulativeQual = 0;
		
		double cumulativeRealDistUtil = 0;
		double cumulativeDurationUtil = 0;
		double cumulativeQualityUtil = 0;

		List<Gene> genes = getGenes();
		ListIterator<Gene> liter = genes.listIterator();
		double utility, realDist, dur, qual, dist, serviceTime, realDistUtil, realDurUtil, qualUtil;
		Gene listIdx = liter.next();
		while (liter.hasNext()) {
			Gene listNextIdx = liter.next();
			if (listIdx.geneNode.getServiceType().equals("start")) {
				// Casting from long to int as ID
				utility = Environment.mapping.getRealUtilEle(0, getKeyByValue(listNextIdx.geneNode));
				realDist = Environment.mapping.getRealDistEle(0, getKeyByValue(listNextIdx.geneNode));
				dur = Environment.mapping.getRealDurEle(0, getKeyByValue(listNextIdx.geneNode));
				qual = listNextIdx.getGeneNode().getQuality();
				dist = Environment.mapping.getDistEle(0, getKeyByValue(listNextIdx.geneNode));
				serviceTime = Environment.mapping.getServiceTimeEle(0, getKeyByValue(listNextIdx.geneNode));
				realDistUtil = Environment.mapping.getRealDistUtilEle(0, getKeyByValue(listNextIdx.geneNode));
				realDurUtil = Environment.mapping.getRealDurUtilEle(0, getKeyByValue(listNextIdx.geneNode));
				qualUtil = Environment.mapping.getRealQualUtilEle(0, getKeyByValue(listNextIdx.geneNode));

			} else if (listNextIdx.geneNode.getServiceType().equals("finish")) {
				utility = Environment.mapping.getRealUtilEle(1, getKeyByValue(listIdx.geneNode));
				realDist = Environment.mapping.getRealDistEle(1, getKeyByValue(listIdx.geneNode));
				dur = Environment.mapping.getRealDurEle(1, getKeyByValue(listIdx.geneNode));
				dist = Environment.mapping.getDistEle(1, getKeyByValue(listIdx.geneNode));
				realDistUtil = Environment.mapping.getRealDistUtilEle(1, getKeyByValue(listIdx.geneNode));
				realDurUtil = Environment.mapping.getRealDurUtilEle(1, getKeyByValue(listIdx.geneNode));
				qualUtil = 0;
				serviceTime = 0;
				qual = 0;
			} else {
				utility = utilityFunction.calculateUtility(listIdx.geneNode, listNextIdx.geneNode).getUtility();
				realDist = utilityFunction.getAttributes().getRealDist();
				dur = utilityFunction.getAttributes().getDuration();
				qual = listNextIdx.getGeneNode().getQuality();
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
		
		this.setFitness(cumulativeUtil);
		this.setRealDist(cumulativeRealDist);
		this.setDuration(cumulativeDuration);
		this.setQuality(cumulativeQual);
		this.setDist(cumulativeDist);
		this.setServiceTime(cumulativeServTime);
		this.setRealDistUtil(cumulativeRealDistUtil);
		this.setDurationUtil(cumulativeDurationUtil);
		this.setQualityUtil(cumulativeQualityUtil);
//
	}

	void crossOver(Chromosome parent1, Chromosome parent2) {
		int chromosomeSize = 0;
		for (Entry<String, List<Integer>> entry : Environment.mapping.getMapAssist().entrySet()) {
			// Simply add finish and start since they're the same in both
			// parents
			if (entry.getKey().equals("start"))
				genes.add(new Gene(Environment.mapping.getNode(entry.getValue().get(0)), 1.0, 0));
			else if (entry.getKey().equals("finish"))
				genes.add(new Gene(Environment.mapping.getNode(entry.getValue().get(0)), 1.99, 99));
			else {
				// Choose a gene from parent with a chance for each service
				int chance = realRandom.nextInt(100) + 1;
				if (chance > 30)
					genes.add(parent1.getGeneByService(entry.getKey()));
				else
					genes.add(parent2.getGeneByService(entry.getKey()));
				
				chromosomeSize++;
			}
		}
		this.setSize(chromosomeSize+2);
	}

	void swap() {
		// The Swap heuristic function
		// Choose a random node, exept the start and finish
		int geneNumber = realRandom.nextInt(this.size - 2) + 1;
		// Determine the random node's service type
		String serv = genes.get(geneNumber).geneNode.getServiceType();
		// Replace the random node with another node of the same servyce type
		int size = Environment.mapping.getMapAssist().get(serv).size();

		// Service provider number in the elements
		// If only one service, then it's 0, no random
		int elementNo;
		if (size == 1)
			elementNo = 0;
		else
			elementNo = realRandom.nextInt(size - 1);
		SmartCity node = Environment.mapping.getMap().get(Environment.mapping.getMapAssist().get(serv).get(elementNo));
		// Remove the old gene
		// Make a new gene and put it in the same place as the old gene
		Gene tempGene = new Gene(node, genes.get(geneNumber).getRandomKey(), genes.get(geneNumber).getFraction());
		//genes.remove(geneNumber);
		// genes.add(geneNumber, new Gene(node,
		// genes.get(geneNumber).getRandomKey(),
		// genes.get(geneNumber).getFraction()));

		LinkedList<Gene> tempGenes = new LinkedList<Gene>();
		tempGenes.addAll(getGenes());
		//Collections.copy(tempGenes, getGenes());
		tempGenes.remove(geneNumber);
		//Collections.copy(tempGenes, getGenes());
		double maxUtil = 0;
		int desiIndex = 0;
		Chromosome fitChromosome = new Chromosome();
		//Best chromosome so far
		fitChromosome.setGenes(this.getGenes());
		for (int i = 0; i < this.size - 2; i++) {
			// Collections.swap(tempGenes, i, geneNumber);
			// Check the best place to locate the node
			/*
			UtilityFunction utilityFunction = new UtilityFunction();
			double utility = utilityFunction.calculateUtility(tempGenes.get(i).getGeneNode(), tempGene.getGeneNode())
					.getUtility()
					+ utilityFunction.calculateUtility(tempGene.getGeneNode(), tempGenes.get(i + 1).getGeneNode())
							.getUtility()
					- utilityFunction
							.calculateUtility(tempGenes.get(i).getGeneNode(), tempGenes.get(i + 1).getGeneNode())
							.getUtility();
			if (utility > maxUtil) {
				maxUtil = utility;
				desiIndex = i;
			}
			*/
			//Put the designated Gene after the i-th node, caculate fitness,
			//Determine whether it is better than other possibilities or not
			tempGenes.add(i + 1, tempGene);
			Chromosome tempChromosome = new Chromosome();
			tempChromosome.setGenes(tempGenes);
			tempChromosome.calculateFitness();
			if(tempChromosome.getFitness() > maxUtil){
				maxUtil = tempChromosome.getFitness();
				//fitChromosome.setGenes(tempChromosome.getGenes());
				//Cope the current chromosome in the best so far, if it is better
				Collections.copy(fitChromosome.genes, tempChromosome.getGenes());
			}
			tempGenes.remove(i + 1);
			
		}
		
		//genes.add(desiIndex + 1, tempGene);
		//Replace the original chromosome the best so far
		copyChromosome(fitChromosome);
		
	}

	void twoOpt() {
		// The 2-opt heuristic function
		//2-opt heuristic attempts to find two edges of the tour that can be removed,
		//and two edges that can be inserted to obtain a chromosome with better utility
		int lastIdx = Environment.mapping.getMap().size()-1;
		boolean loopFlag = true;
		double bestFitness = getFitness();
		int counter = 0;
		while (loopFlag && counter < 5) {
			//Repeat algorithm until no improvements is made
			boolean breakFlag = true;
			if(size - 2 == 1)
				break;
			for (int i = 1; i < size - 2; i++) {
				for (int j = i + 1; j < size - 1; j++) {
					Chromosome newChromosome = new Chromosome();
					newChromosome.getGenes().add(new Gene(Environment.mapping.getMap().get(0), 1.0, 0));
					newChromosome.getGenes().addAll(twoOptSwap(i, j));
					newChromosome.getGenes().add(new Gene(Environment.mapping.getMap().get(lastIdx), 1.99, 99));
					newChromosome.calculateFitness();
					double newFitness = newChromosome.getFitness();
					if (newFitness > bestFitness) {
						copyChromosome(newChromosome);
						breakFlag = false;
					}

				}
				if (i == size - 3 && breakFlag)
					loopFlag = false;
			}
			counter++;
			
		}
	}

	void copyChromosome(Chromosome newChromosome) {
		// A function to replace a chromosome
		//this.setGenes(newChromosome.getGenes());
		Collections.copy(genes, newChromosome.getGenes());
		this.calculateFitness();
	}

	LinkedList<Gene> twoOptSwap(int i, int j) {
		// Perform the 2_opt swap
		LinkedList<Gene> tempGenes = new LinkedList<Gene>();
		LinkedList<Gene> reverseSubGenes = new LinkedList<Gene>();
		// Pick from index 0 to i-1
		tempGenes.addAll(this.getGenes().subList(1, i));	//1 to i-1
		// Pick from index i to j in reverse order
		reverseSubGenes.addAll(this.getGenes().subList(i, j+1));	//i to j in reverse order
		Collections.reverse(reverseSubGenes);
		tempGenes.addAll(reverseSubGenes);
		// Pick from index j+1 to to the end
		tempGenes.addAll(this.getGenes().subList(j+1, size-1));	//j+1 to the end

		return tempGenes;

	}

	void initGenes() {
		// Initializing genes in a chromosome
		int chromosomeSize = 0;
		// Choose a random service provider from each category
		for (Entry<String, List<Integer>> entry : Environment.mapping.getMapAssist().entrySet()) {
			// If its start or finish, they're the only element in the array
			// add with special attributes so they're always the first and last
			// genes in chromosome
			if (entry.getKey().equals("start"))
				genes.add(new Gene(Environment.mapping.getNode(entry.getValue().get(0)), 1.0, 0));
			else if (entry.getKey().equals("finish"))
				genes.add(new Gene(Environment.mapping.getNode(entry.getValue().get(0)), 1.99, 99));

			else {
				// If it is not start nor finish, add with random attributes
				int size = entry.getValue().size();
				// Service
				String serv = entry.getKey();
				// Service provider number in the elements
				// If only one service, then it's 0, no random
				int elementNo;
				if (size == 1)
					elementNo = 0;
				else
					elementNo = realRandom.nextInt(size - 1);
				// Get a random from the service category
				int provNo = Environment.mapping.getMapAssist().get(serv).get(elementNo);
				// Cluster number
				int clustNo = realRandom.nextInt(98) + 1;
				// Creating the random-key
				double randKey = clustNo * 0.01 + provNo;
				// Add the element to the genes
				genes.add(new Gene(Environment.mapping.getMap().get(provNo), randKey, clustNo));
				chromosomeSize++;
			}

		}
		setSize(chromosomeSize + 2);
	}

	void sort() throws Exception {
		try {
			Collections.sort(getGenes(), new CustomComparator());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class CustomComparator implements Comparator<Gene> {
		@Override
		public int compare(Gene o1, Gene o2) {
			return o1.getFraction().compareTo(o2.getFraction());
		}
	}

	Gene getGeneByService(String service) {
		for (Gene gene : getGenes())
			if (gene.getGeneNode().getServiceType().equals(service))
				return gene;
		return null;
	}

	int getGeneCount() {
		return genes.size();
	}

	public Double getDist() {
		return dist;
	}

	public void setDist(Double dist) {
		this.dist = dist;
	}

	public Double getRealDist() {
		return realDist;
	}

	public void setRealDist(Double realDist) {
		this.realDist = realDist;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public Integer getQuality() {
		return quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	public Double getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(Double serviceTime) {
		this.serviceTime = serviceTime;
	}

	public UtilityFunction getUtilityFunction() {
		return utilityFunction;
	}

	public void setUtilityFunction(UtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	public Double getRealDistUtil() {
		return realDistUtil;
	}

	public void setRealDistUtil(Double realDistUtil) {
		this.realDistUtil = realDistUtil;
	}

	public Double getDurationUtil() {
		return durationUtil;
	}

	public void setDurationUtil(Double durationUtil) {
		this.durationUtil = durationUtil;
	}

	public Double getQualityUtil() {
		return qualityUtil;
	}

	public void setQualityUtil(Double qualityUtil) {
		this.qualityUtil = qualityUtil;
	}

	public Double getUtil() {
		return util;
	}

	public void setUtil(Double util) {
		this.util = util;
	}

}