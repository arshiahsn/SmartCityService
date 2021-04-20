package com.SmartCity.tsp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.SmartCity;


public class MyParticleSwarmOpt implements Runnable{
	
	public final static int ITERATION_SIZE = 100;
	private MyParticleSwarm myParticleSwarm;
	private List<SmartCity> minRouteList;
	MyFileWriter myFileWriter;
	
	public MyParticleSwarmOpt(MyParticleSwarm myParticleSwarm){
		this.myParticleSwarm = myParticleSwarm;
		 myFileWriter = new MyFileWriter();
	}
	
	@Override
	public void run() {
		int iteration = 0;
		int maxIndex = 0;
		try
		{
			myParticleSwarm.initParticles();
			while(iteration <= ITERATION_SIZE){
				//TODO:Gbest 
				myParticleSwarm.updategBest();			//Update global Best
				myParticleSwarm.moveParticles();			//Calculate velocity and Move particles
				iteration++;
				if(iteration == ITERATION_SIZE){
					//maxIndex = findMaxIndex();
					System.out.println("Itertion: "+iteration+" My PSO Fitness: "+
							myParticleSwarm.getgBFitness().toString()+
							"My PSO Solution: "+ myParticleSwarm.getgBest()
							);
				}

			}
			
			//Write quality into file
			//It's normalized according to number of nodes(genes)
			//n genes indicate n-1 utilities
			
			double aggUtil = myParticleSwarm.getgBFitness();
			double aggRealDist = myParticleSwarm.getgBParticle().getRealDist();
			double aggDur = myParticleSwarm.getgBParticle().getDuration();
			double aggQual = myParticleSwarm.getgBParticle().getQuality();
			double aggDist = myParticleSwarm.getgBParticle().getDist();
			double aggServTime = myParticleSwarm.getgBParticle().getServiceTime();
			double aggTotalTime = aggServTime + aggDur;
			int reqCount = (int) myParticleSwarm.getgBParticle().getSolution().size()-2;
			double aggRealDistUtil = myParticleSwarm.getgBParticle().getRealDistUtil();
			double aggDurationUtil = myParticleSwarm.getgBParticle().getDurationUtil();
			double aggQualityUtil = myParticleSwarm.getgBParticle().getQualityUtil();
			
			myFileWriter.write("util-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggUtil));
			myFileWriter.write("realDist-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDist));
			myFileWriter.write("dur-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggDur));
			myFileWriter.write("qual-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggQual));
			myFileWriter.write("dist-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggDist));
			myFileWriter.write("servTime-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggServTime));
			myFileWriter.write("totalTime-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggTotalTime));
			myFileWriter.write("realDistUtil-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggRealDistUtil));
			myFileWriter.write("realDurUtil-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggDurationUtil));
			myFileWriter.write("qualUtil-mypso"+String.valueOf(reqCount)+"req",String.valueOf(aggQualityUtil));
		
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public List<SmartCity> getMinRouteList() {
		// TODO Auto-generated method stub
		List<SmartCity> maxRoute = new LinkedList<SmartCity>();
		//First chromosome with highest fitness
		//int maxIndex = findMaxIndex();
		List<Integer> desigNodes = myParticleSwarm.getgBest();
		for(Integer integer : desigNodes){
			maxRoute.add(myParticleSwarm.mapping.getNode(integer));
		}
		return maxRoute;
		
	}
	/*public int findMaxIndex(){
		//A function to find the best global solution among 4 solutions
		double max = myParticleSwarm.getgBFitness();
		int maxIndex = 0;
		int index = 0;
		for(Double fitness : myParticleSwarm.getgBFitness()){
			if(fitness > max){
				maxIndex = index;
				max = fitness;
			}
	
			index++;
		}
		return maxIndex;
	}*/

}
