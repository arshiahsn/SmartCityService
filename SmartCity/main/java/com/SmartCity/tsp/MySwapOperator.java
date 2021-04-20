package com.SmartCity.tsp;




public class MySwapOperator {
	private int node1;
	private int node2;
	private int nodeNum1;
	private int nodeNum2;
	private double probability;


	MySwapOperator(int node1, int node2, int nodeNum1, int nodeNum2, double probability){
		this.node1 = node1;
		this.node2 = node2;
		this.nodeNum1 = nodeNum1;
		this.nodeNum2 = nodeNum2;
		this.probability = probability;
		//this.opProb = opProb;
	}


	public int getNode1() {
		return node1;
	}


	public void setNode1(int node1) {
		this.node1 = node1;
	}


	public int getNode2() {
		return node2;
	}


	public void setNode2(int node2) {
		this.node2 = node2;
	}


	public int getNodeNum1() {
		return nodeNum1;
	}


	public void setNodeNum1(int nodeNum1) {
		this.nodeNum1 = nodeNum1;
	}

	public int getNodeNum2() {
		return nodeNum2;
	}


	public void setNodeNum2(int nodeNum2) {
		this.nodeNum2 = nodeNum2;
	}


	public double getProbability() {
		return probability;
	}


	public void setProbability(double probability) {
		this.probability = probability;
	}
	


}