package com.SmartCity.tsp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.SmartCity;


public class EecParticleSwarmOpt implements Runnable{
	
	public final static int ITERATION_SIZE = 100;
	private EecParticleSwarm eecParticleSwarm;
	private List<SmartCity> minRouteList;
	MyFileWriter myFileWriter;
	
	public EecParticleSwarmOpt(EecParticleSwarm eecParticleSwarm){
		this.eecParticleSwarm = eecParticleSwarm;
		 myFileWriter = new MyFileWriter();
	}
	
	@Override
	public void run() {
		int iteration = 0;
		int maxIndex = 0;
		try
		{
			eecParticleSwarm.initParticles();
			while(iteration <= ITERATION_SIZE){
				//TODO:Gbest 
				eecParticleSwarm.updategBest();			//Update global Best
				eecParticleSwarm.moveParticles(iteration);			//Calculate velocity and Move particles
				iteration++;
				if(iteration == ITERATION_SIZE){
					maxIndex = findMaxIndex();
					System.out.println("Itertion: "+iteration+"Eec Fitness: "+
							eecParticleSwarm.getgBFitness().get(maxIndex).toString()+
							"Eec Solution: "+ eecParticleSwarm.getgBest().get(maxIndex)
							);
				}

			}
			
			//Write quality into file
			//It's normalized according to number of nodes(genes)
			//n genes indicate n-1 utilities
			
			double aggUtil = eecParticleSwarm.getgBFitness().get(maxIndex);
			double aggRealDist = eecParticleSwarm.getgBParticle().getRealDist();
			double aggDur = eecParticleSwarm.getgBParticle().getDuration();
			double aggQual = eecParticleSwarm.getgBParticle().getQuality();
			double aggDist = eecParticleSwarm.getgBParticle().getDist();
			double aggServTime = eecParticleSwarm.getgBParticle().getServiceTime();
			double aggTotalTime = aggServTime + aggDur;
			int reqCount = (int) eecParticleSwarm.getgBParticle().getSolution().size()-2;
			double aggRealDistUtil = eecParticleSwarm.getgBParticle().getRealDistUtil();
			double aggDurationUtil = eecParticleSwarm.getgBParticle().getDurationUtil();
			double aggQualityUtil = eecParticleSwarm.getgBParticle().getQualityUtil();
			
			myFileWriter.write("util-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggUtil));
			myFileWriter.write("realDist-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDist));
			myFileWriter.write("dur-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggDur));
			myFileWriter.write("qual-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggQual));
			myFileWriter.write("dist-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggDist));
			myFileWriter.write("servTime-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggServTime));
			myFileWriter.write("totalTime-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggTotalTime));
			myFileWriter.write("realDistUtil-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDistUtil));
			myFileWriter.write("realDurUtil-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggDurationUtil));
			myFileWriter.write("qualUtil-eecpso"+String.valueOf(reqCount)+"req",String.valueOf(aggQualityUtil));
		
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
		List<Integer> desigNodes = eecParticleSwarm.getgBest().get(maxIndex);
		for(Integer integer : desigNodes){
			maxRoute.add(eecParticleSwarm.mapping.getNode(integer));
		}
		return maxRoute;
		
	}
	public int findMaxIndex(){
		//A function to find the best global solution among 4 solutions
		double max = eecParticleSwarm.getgBFitness().get(0);
		int maxIndex = 0;
		int index = 0;
		for(Double fitness : eecParticleSwarm.getgBFitness()){
			if(fitness > max){
				maxIndex = index;
				max = fitness;
			}
	
			index++;
		}
		return maxIndex;
	}

}
