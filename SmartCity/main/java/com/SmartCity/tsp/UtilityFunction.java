package com.SmartCity.tsp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.SmartCity.smartcity.Cache;
import com.SmartCity.smartcity.RealDistance;
import com.SmartCity.smartcity.RealDistanceRepository;
import com.SmartCity.smartcity.SmartCity;
import com.SmartCity.smartcity.Utils;

public class UtilityFunction {
	/*
	 * Max value for Quality is 10 Min value for Quality is 0
	 * 
	 * Max value for Distance is 50 KM Min value for Distance is 0.1 KM
	 * 
	 */

	private double maxDist = 24;
	private double minDist = 0.1;
	private double xMDist = 12.05;

	private double maxQual = 10;
	private double minQual = 0;
	private double xMQual = 5;

	// TODO:Make these values close to real
	// Distance in meter
	private double maxRealDist = 100000;
	private double minRealDist = 100;
	private double xMRealDist = 50050;
	// Duration in seconds
	private double maxDuration = 10800;
	private double minDuration = 300;
	private double xMDuration = 5550;

	private double alpha = 1;

	private double wDist = 0;
	private double wQual = 0.2;
	private double wRealDist = 0.4;
	private double wDuration = 0.4;

	private Attributes attributes;

	// List<RealDistance> realDistanceList;

	// Calculate distance

	public double calculateCost(SmartCity i, SmartCity j) {
		double dx = i.getLocation().getLatitude() - j.getLocation().getLatitude();
		double dy = i.getLocation().getLongitude() - j.getLocation().getLongitude();
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	}

	public double sigmoidFunction(double min, double xM, double max, double x, boolean isDownward) {
		double exp, util = 0;
		double beta = alpha * (max - xM) / (xM - min);
		if (x <= min)
			return 0;
		if (x > min && x <= xM) {
			exp = (alpha * (xM - x)) / (x - min);
			util = 1 / (1 + Math.pow(Math.E, exp));
		}
		if (x > xM && x < max) {
			exp = (beta * (x - xM)) / (max - x);
			util = 1 - (1 / (1 + Math.pow(Math.E, exp)));
		}
		if (x >= max)
			return 1;

		if (isDownward)
			return (1 - util);
		else
			return util;
	}

	public double simpleUtility(double min, double max, double x, boolean isDownward) {
		if (isDownward)
			return (max - x) / (max - min);
		else
			return (x - min) / (max - min);
	}

	public double calculatePartialUtility(int nodeNumber, double realDist, double dur, int qual) {
		int nodeNum = nodeNumber - 1;
		double singleUtil1 = sigmoidFunction(nodeNum * minRealDist, nodeNum * xMRealDist, nodeNum * maxRealDist,
				realDist, true);
		double singleUtil2 = sigmoidFunction(nodeNum * minDuration, nodeNum * xMDuration, nodeNum * maxDuration, dur,
				true);
		double singleUtil3 = sigmoidFunction(nodeNum * minQual, nodeNum * xMQual, nodeNum * maxQual, qual, false);
		double aggUtil = wRealDist * singleUtil1 + wDuration * singleUtil2 + wQual * singleUtil3;

		return aggUtil;
	}

	public double calculateSimpleUtility(int nodeNumber, double realDist, double dur, int qual) {
		int nodeNum = nodeNumber - 1;
		double singleUtil1 = simpleUtility(nodeNum * minRealDist, nodeNum * maxRealDist, realDist, true);
		double singleUtil2 = simpleUtility(nodeNum * minDuration, nodeNum * maxDuration, dur, true);
		double singleUtil3 = simpleUtility(nodeNum * minQual, nodeNum * maxQual, qual, false);

		double aggUtil = wRealDist * singleUtil1 + wDuration * singleUtil2 + wQual * singleUtil3;

		return aggUtil;
	}

	public Attributes calculateUtility(SmartCity i, SmartCity j) {
		// TODO: If it's old, replace
		// Calculate euclidian distance
		attributes = new Attributes();
		double dist = calculateCost(i, j);
		double realDist, duration;
		RealDistance rDist;
		Cache cache = null;
		// Get destination Quality
		int quality = j.getQuality();
		// If one node is start/finish

		if (i.getServiceType().equals("start") || i.getServiceType().equals("finish")
				|| j.getServiceType().equals("finish") || j.getServiceType().equals("start")) {

			if (i.getServiceType().equals("start") || i.getServiceType().equals("finish")) {
				cache = Utils.findByRowAndColumn(i.getServiceType(), j.getId().toString());
			}
			if (j.getServiceType().equals("finish") || j.getServiceType().equals("start")) {
				cache = Utils.findByRowAndColumn(i.getId().toString(), j.getServiceType());
			}
			if (cache == null) {
				GoogleWebService gService = new GoogleWebService();
				try {
					gService.getAttributes(i.getLocation(), j.getLocation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				realDist = gService.getDistList().get(0);
				duration = gService.getDurList().get(0);

			} else {
				if (!Utils.isOld(cache.getLastUpdate())) {
					realDist = cache.getDistValue();
					duration = cache.getDurValue();
				} else {
					// If it's older than the period fetch from google
					GoogleWebService gService = new GoogleWebService();
					try {
						gService.getAttributes(i.getLocation(), j.getLocation());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					realDist = gService.getDistList().get(0);
					duration = gService.getDurList().get(0);
					// TODO: Save to DB
				}
			}
		} else {
			// Get real distance and duration from database previously fetched
			// from
			// google
			if ((rDist = Utils.findByRowAndColumn(i.getId(), j.getId())) == null) {
				// If one node is origin or finish just fetch from google
				// If node does not exist in real distance DB fetch from google
				GoogleWebService gService = new GoogleWebService();
				try {
					gService.getAttributes(i.getLocation(), j.getLocation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				realDist = gService.getDistList().get(0);
				duration = gService.getDurList().get(0);
			} else {

				// If a node exist in real distance DB
				// And it is not older than a period (30min)
				if (!Utils.isOld(rDist.getLastUpdate())) {
					realDist = rDist.getDistValue();
					duration = rDist.getDurValue();
				} else {
					// If it's older than the period fetch from google
					GoogleWebService gService = new GoogleWebService();
					try {
						gService.getAttributes(i.getLocation(), j.getLocation());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					realDist = gService.getDistList().get(0);
					duration = gService.getDurList().get(0);
					// TODO: Save to DB

				}

			}
		}
		//
		// Only one request, list has one element

		// Calculate utility

		// Sigmoid formula for distance
		// Distance is downward
		double singleUtil1 = sigmoidFunction(minDist, xMDist, maxDist, dist, true);
		double singleUtil2 = sigmoidFunction(minQual, xMQual, maxQual, quality, false);

		double singleUtil3 = sigmoidFunction(minRealDist, xMRealDist, maxRealDist, realDist, true);
		double singleUtil4 = sigmoidFunction(minDuration, xMDuration, maxDuration, duration, true);

		getAttributes().setRealDistUtil(singleUtil3);
		getAttributes().setDurationUtil(singleUtil4);
		getAttributes().setQualityUtil(singleUtil2);
		/*
		 * Aggregator: Weight for distance is wDist Weight for quality is wQual
		 * Simple sigma aggregator
		 */
		double aggUtil = wDist * singleUtil1 + wQual * singleUtil2 + wRealDist * singleUtil3 + wDuration * singleUtil4;
		// double aggUtil = Math.pow(singleUtil1, wDist)*Math.pow(singleUtil2,
		// wQual);
		// Set attribtues

		getAttributes().setDist(dist);
		getAttributes().setRealDist(realDist);
		getAttributes().setDuration(duration);
		getAttributes().setQual(quality);
		getAttributes().setUtility(aggUtil);

		attributes.computeNominalAtts();

		return attributes;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

}