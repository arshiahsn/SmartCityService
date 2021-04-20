package com.SmartCity.tsp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.SmartCity;


public class ParticleSwarmOpt implements Runnable{
	
	public final static int ITERATION_SIZE = 100;
	private ParticleSwarm particleSwarm;
	private List<SmartCity> minRouteList;
	MyFileWriter myFileWriter;
	
	public ParticleSwarmOpt(ParticleSwarm particleSwarm){
		this.particleSwarm = particleSwarm;
		 myFileWriter = new MyFileWriter();
	}
	
	@Override
	public void run() {
		int iteration = 0;
		int maxIndex = 0;
		try
		{
			particleSwarm.initParticles();
			while(iteration <= ITERATION_SIZE){
				//TODO:Gbest 
				particleSwarm.updategBest();			//Update global Best
				particleSwarm.moveParticles();			//Calculate velocity and Move particles
				iteration++;
				if(iteration == ITERATION_SIZE){
					maxIndex = findMaxIndex();
					System.out.println("Itertion: "+iteration+" Fitness: "+
							particleSwarm.getgBFitness().get(maxIndex).toString()+
							"Solution: "+ particleSwarm.getgBest().get(maxIndex)
							);
				}

			}
			
			//Write quality into file
			//It's normalized according to number of nodes(genes)
			//n genes indicate n-1 utilities
			
			double aggUtil = particleSwarm.getgBFitness().get(maxIndex);
			double aggRealDist = particleSwarm.getgBParticle().getRealDist();
			double aggDur = particleSwarm.getgBParticle().getDuration();
			double aggQual = particleSwarm.getgBParticle().getQuality();
			double aggDist = particleSwarm.getgBParticle().getDist();
			double aggServTime = particleSwarm.getgBParticle().getServiceTime();
			double aggTotalTime = aggServTime + aggDur;
			int reqCount = (int) particleSwarm.getgBParticle().getSolution().size()-2;
			double aggRealDistUtil = particleSwarm.getgBParticle().getRealDistUtil();
			double aggDurationUtil = particleSwarm.getgBParticle().getDurationUtil();
			double aggQualityUtil = particleSwarm.getgBParticle().getQualityUtil();
			
			myFileWriter.write("util-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggUtil));
			myFileWriter.write("realDist-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDist));
			myFileWriter.write("dur-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggDur));
			myFileWriter.write("qual-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggQual));
			myFileWriter.write("dist-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggDist));
			myFileWriter.write("servTime-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggServTime));
			myFileWriter.write("totalTime-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggTotalTime));
			myFileWriter.write("realDistUtil-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDistUtil));
			myFileWriter.write("realDurUtil-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggDurationUtil));
			myFileWriter.write("qualUtil-pso"+String.valueOf(reqCount)+"req",String.valueOf(aggQualityUtil));
		
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public List<SmartCity> getMinRouteList() {
		// TODO Auto-generated method stub
		List<SmartCity> maxRoute = new LinkedList<SmartCity>();
		//First chromosome with highest fitness
		int maxIndex = findMaxIndex();
		List<Integer> desigNodes = particleSwarm.getgBest().get(maxIndex);
		for(Integer integer : desigNodes){
			maxRoute.add(particleSwarm.mapping.getNode(integer));
		}
		return maxRoute;
		
	}
	public int findMaxIndex(){
		//A function to find the best global solution among 4 solutions
		double max = particleSwarm.getgBFitness().get(0);
		int maxIndex = 0;
		int index = 0;
		for(Double fitness : particleSwarm.getgBFitness()){
			if(fitness > max){
				maxIndex = index;
				max = fitness;
			}
	
			index++;
		}
		return maxIndex;
	}

}
