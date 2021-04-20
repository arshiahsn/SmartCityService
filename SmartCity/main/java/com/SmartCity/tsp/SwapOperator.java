package com.SmartCity.tsp;



public class SwapOperator {
	private int x;
	private int y;
	private double probability;
	private int opProb = 1; 

	SwapOperator(int x, int y, double probability){
		this.x = x;
		this.y = y;
		this.probability = probability;
		//this.opProb = opProb;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
	/*
	public int getOpProb() {
		return opProb;
	}
	public void setOpProb(int opProb) {
		this.opProb = opProb;
	}
	*/
	
	
}
