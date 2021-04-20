package com.SmartCity.tsp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Attributes {
	
	public final static int RANDOM_SEED = 1234;	
	public final static Random rand = new Random(RANDOM_SEED);
	
	private double realDist;
	private double duration;
	private double dist;
	private int qual;
	private double utility;
	private double serviceTime;
	private double totalTime;
	
	private double realDistUtil;
	private double durationUtil;
	private double qualityUtil;
	private double serviceTimeUtil;
	private double totalTimeUtil;
	
	//A funciton to compute service time and total time based on other attributes
	public void computeNominalAtts(){
		this.duration /= 60; //Duration in minutes
		this.realDist /= 1000; //Distance in kilometers
		int randomNum = ThreadLocalRandom.current().nextInt((qual-1)*10, qual*10 + 1);
		this.serviceTime = 100-randomNum;
		
	}
	
	public double getRealDist() {
		return realDist;
	}
	public void setRealDist(double realDist) {
		this.realDist = realDist;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public double getDist() {
		return dist;
	}
	public void setDist(double dist) {
		this.dist = dist;
	}
	public int getQual() {
		return qual;
	}
	public void setQual(int qual) {
		this.qual = qual;
	}
	public double getUtility() {
		return utility;
	}
	public void setUtility(double utility) {
		this.utility = utility;
	}
	public double getServiceTime() {
		return serviceTime;
	}
	public void setServiceTime(double serviceTime) {
		this.serviceTime = serviceTime;
	}
	public double getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public double getRealDistUtil() {
		return realDistUtil;
	}

	public void setRealDistUtil(double realDistUtil) {
		this.realDistUtil = realDistUtil;
	}

	public double getDurationUtil() {
		return durationUtil;
	}

	public void setDurationUtil(double durationUtil) {
		this.durationUtil = durationUtil;
	}

	public double getQualityUtil() {
		return qualityUtil;
	}

	public void setQualityUtil(double qualityUtil) {
		this.qualityUtil = qualityUtil;
	}

	public double getServiceTimeUtil() {
		return serviceTimeUtil;
	}

	public void setServiceTimeUtil(double serviceTimeUtil) {
		this.serviceTimeUtil = serviceTimeUtil;
	}

	public double getTotalTimeUtil() {
		return totalTimeUtil;
	}

	public void setTotalTimeUtil(double totalTimeUtil) {
		this.totalTimeUtil = totalTimeUtil;
	}
	
	
	
	
	

}
