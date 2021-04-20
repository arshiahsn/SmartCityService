package com.SmartCity.tsp;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;


import com.SmartCity.smartcity.SmartCity;


public class MappingUnits {

	

	public MappingUnits(String[] compositeServices) {
		this.map = new HashMap<Integer,SmartCity>();
		this.mapAssist = new HashMap<String,List<Integer>>();
		this.compositeServices = compositeServices;
		this.realDist = new Double[2][100];
		this.realUtil = new Double[2][100];
		this.realDur= new Double[2][100];
		this.dist= new Double[2][100];
		this.realDistUtil = new Double[2][100];
		this.realDurUtil = new Double[2][100];
		this.realQualUtil = new Double[2][100];
		this.setServiceTime(new Double[2][100]);
		this.firstRequest = true;
		this.lastUpdate = new Date();

	}
	private HashMap<Integer,SmartCity> map;	
	private HashMap<String,List<Integer>> mapAssist;
	private String[] compositeServices;
	private Double[][] realDist;
	private Double[][] realDur;
	private Double[][] realUtil;
	private Double[][] dist;
	private Double[][] serviceTime;
	
	private Double[][] realDistUtil;
	private Double[][] realDurUtil;
	private Double[][] realQualUtil;
	
	private boolean firstRequest;
	private Date lastUpdate;

	
	public void createMap(SmartCity start, List<SmartCity> list, 
			SmartCity finish){
		//Start+Finish+List
		int nodeCount = 2 + list.size();
		getMap().put(0, start);
		getMap().put(nodeCount-1, finish);
		for(int i = 1; i < nodeCount-1; i++){
			getMap().put(i, list.get(i-1));
		}

	}
	public void createMapAssist(){

		for(Entry<Integer, SmartCity> entry : getMap().entrySet()){
			//Traversing service array in order to make a new array consisting of all
			//Service providers of a kind in an element
			//Add start and finish point once
			if(entry.getValue().getServiceType().equals("start") ||
					entry.getValue().getServiceType().equals("finish")){
				List<Integer> tempList = new LinkedList<Integer>();
				tempList.add(entry.getKey());
				getMapAssist().put(entry.getValue().getServiceType(), tempList);
			}

			else	
				for(String service: getCompositeServices()){
					if(entry.getValue().getServiceType().equals(service))
						//serviceIndexArr.add(entry.getKey());
						if(getMapAssist().containsKey(service))
							getMapAssist().get(service).add(entry.getKey());
						else{
							List<Integer> tempList = new LinkedList<Integer>();
							tempList.add(entry.getKey());
							getMapAssist().put(service, tempList);
						}
				}

		}

	}

	
	public SmartCity getNode(int i){
		return getMap().get(i);
	}
	
	public HashMap<Integer, SmartCity> getMap() {
		return map;
	}
	public void setMap(HashMap<Integer, SmartCity> map) {
		this.map = map;
	}
	public HashMap<String, List<Integer>> getMapAssist() {
		return mapAssist;
	}
	public void setMapAssist(HashMap<String, List<Integer>> mapAssist) {
		this.mapAssist = mapAssist;
	}
	public String[] getCompositeServices() {
		return compositeServices;
	}
	public void setCompositeServices(String[] compositeServices) {
		this.compositeServices = compositeServices;
	}
	public void setRealDist(Double[][] realDist){
		this.realDist = realDist;
	}
	public Double[][] getRealDist(){
		return this.realDist;
	}
	public void setRealUtilEle(int i, int j, Double element){
		realUtil[i][j] = element;
	}
	public Double getRealUtilEle(int i, int j){
		return realUtil[i][j];
	}
	public void setRealDurEle(int i, int j, Double element){
		realDur[i][j] = element;
	}
	public Double getRealDurEle(int i, int j){
		return realDur[i][j];
	}
	public void setRealDistEle(int i, int j, Double element){
		realDist[i][j] = element;
	}
	public Double getRealDistEle(int i, int j){
		return realDist[i][j];
	}
	public void setDistEle(int i, int j, Double element){
		dist[i][j] = element;
	}
	public Double getDistEle(int i, int j){
		return dist[i][j];
	}	
	public void setRealDistUtilEle(int i, int j, Double element){
		realDistUtil[i][j] = element;
	}
	public Double getRealDistUtilEle(int i, int j){
		return realDistUtil[i][j];
	}
	public void setRealDurUtilEle(int i, int j, Double element){
		realDurUtil[i][j] = element;
	}
	public Double getRealDurUtilEle(int i, int j){
		return realDurUtil[i][j];
	}
	public void setRealQualUtilEle(int i, int j, Double element){
		realQualUtil[i][j] = element;
	}
	public Double getRealQualUtilEle(int i, int j){
		return realQualUtil[i][j];
	}
	
	public Double[][] getRealDur() {
		return realDur;
	}
	public void setRealDur(Double[][] realDur) {
		this.realDur = realDur;
	}
	public Double[][] getRealUtil() {
		return realUtil;
	}
	public void setRealUtil(Double[][] realUtil) {
		this.realUtil = realUtil;
	}
	public Double[][] getDist() {
		return dist;
	}
	public void setDist(Double[][] dist) {
		this.dist = dist;
	}
	public Double[][] getServiceTime() {
		return serviceTime;
	}
	public void setServiceTime(Double[][] serviceTime) {
		this.serviceTime = serviceTime;
	}
	public void setServiceTimeEle(int i, int j, Double element){
		serviceTime[i][j] = element;
	}
	public Double getServiceTimeEle(int i, int j){
		return serviceTime[i][j];
	}
	public Date getLastUpdate(){
		return this.lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate){
		this.lastUpdate = lastUpdate;
	}
	public boolean isFirstRequest() {
		return firstRequest;
	}
	public void setFirstRequest(boolean firstRequest) {
		this.firstRequest = firstRequest;
	}
	public Double[][] getRealDistUtil() {
		return realDistUtil;
	}
	public void setRealDistUtil(Double[][] realDistUtil) {
		this.realDistUtil = realDistUtil;
	}
	public Double[][] getRealDurUtil() {
		return realDurUtil;
	}
	public void setRealDurUtil(Double[][] realDurUtil) {
		this.realDurUtil = realDurUtil;
	}
	public Double[][] getRealQualUtil() {
		return realQualUtil;
	}
	public void setRealQualUtil(Double[][] realQualUtil) {
		this.realQualUtil = realQualUtil;
	}
}
