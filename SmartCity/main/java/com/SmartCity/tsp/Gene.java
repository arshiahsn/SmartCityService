package com.SmartCity.tsp;

import org.springframework.stereotype.Component;

import com.SmartCity.smartcity.SmartCity;

@Component
public class Gene {
	
	SmartCity geneNode;	
	double randomKey;	
	Integer fraction;
	
	Gene(SmartCity geneNode, double randomKey, int fraction){
		this.geneNode = geneNode;
		this.randomKey = randomKey;
		this.fraction = fraction;
	}
	
	
	public SmartCity getGeneNode() {
		return geneNode;
	}
	public void setGeneNode(SmartCity geneNode) {
		this.geneNode = geneNode;
	}
	public double getRandomKey() {
		return randomKey;
	}
	public void setRandomKey(double randomKey) {
		this.randomKey = randomKey;
	}


	public Integer getFraction() {
		return fraction;
	}


	public void setFraction(Integer fraction) {
		this.fraction = fraction;
	}
	
	
}
