package com.SmartCity.smartcity;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.SmartCity.tsp.GoogleWebService;

@Component
public class Utils {

	private static SmartCityRepository scRepo;
	private static LocationRepository locRepo;
	private static RealDistanceRepository rDistRepo;
	private static CacheRepository cRepo;

	@Autowired
	public Utils(SmartCityRepository scRepo, LocationRepository locRepo, RealDistanceRepository rDistRepo,
			CacheRepository cRepo) {
		Utils.scRepo = scRepo;
		Utils.locRepo = locRepo;
		Utils.rDistRepo = rDistRepo;
		Utils.cRepo = cRepo;

	}

	public static long idCounter = 0;

	public static double calculateDistance(Location sourceLocation, Location destinationLocation) {
		double dx = sourceLocation.getLatitude() - destinationLocation.getLatitude();
		double dy = sourceLocation.getLongitude() - destinationLocation.getLongitude();
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static boolean isInCircle(Location center, double radius, Location location) {
		double dx = center.getLatitude() - location.getLatitude();
		double dy = center.getLongitude() - location.getLongitude();
		return ((dx * dx + dy * dy) <= radius * radius);
	}

	// Iterative search for nodes, starts with a square around source and
	// destination
	// Search area grows exponentially if not all required nodes are not found
	public static List<SmartCity> iterativeSearch(Location sourceLocation, String[] compositeServices,
			Location destinationLocation) {
		// To see if circle has all required services
		List<String> foundServices = new ArrayList<String>();
		List<SmartCity> foundNodes = new ArrayList<SmartCity>();

		double circleDia = calculateDistance(sourceLocation, destinationLocation);
		double circleRad = (circleDia * 3) / 4;
		// Getting center of start and end locations
		double circCenterX = Math.abs((sourceLocation.getLatitude() + destinationLocation.getLatitude()) / 2);
		double circCenterY = Math.abs((sourceLocation.getLongitude() + destinationLocation.getLongitude()) / 2);
		Location center = new Location(circCenterX, circCenterY);
		List<SmartCity> allNodes = scRepo.findAll();

		int iteration = 0;
		// Search and iterate until all required services are found
		while (foundServices.size() != compositeServices.length && iteration <= 10) {
			for (String serviceString : compositeServices) {
				ListIterator<SmartCity> liter = allNodes.listIterator();
				while (liter.hasNext()) {
					SmartCity node = liter.next();
					if (serviceString.equals(node.getServiceType())) {
						// If it's in the circle add it
						if (isInCircle(center, circleRad, node.getLocation())) {
							foundNodes.add(node);
							// Remove it from list so it's not searched again
							liter.remove();
							// Add it to the hashmap as well
							if (!foundServices.contains(node.getServiceType()))
								foundServices.add(node.getServiceType());
						}
					}
				}

			}
			// Multiply radius by 2 if nodes not found
			circleRad = circleRad * 2;
			iteration++;
		}
		return foundNodes;
	}

	// Check if a node has registered already
	public static boolean nodeNotExists(SmartCity node) {
		List<SmartCity> temp1 = scRepo.findByServiceType(node.getServiceType());
		List<Location> temp2 = locRepo.findByLatitudeAndLongitude(node.getLocation().getLatitude(),
				node.getLocation().getLongitude());
		boolean flag = false;
		if (temp1.isEmpty() || temp2.isEmpty())
			return !flag;
		else {
			for (int j = 0; j < temp2.size(); j++)
				for (int i = 0; i < temp1.size(); i++) {
					if (temp1.get(i).getLocation() == temp2.get(j)) {
						return flag;
					}

				}

		}
		return !flag;
	}

	// Using shuffle for now
	// Implementing Fisher–Yates shuffle
	public static void shuffleArray(String[] ar) {
		// If running on Java 6 or older, use `new Random()` on RHS here
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			String a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	// Calculating difference of a Date with current time
	// Returns true if it's older than thirty minutes
	public static boolean isOld(Date oldDate) {
		Date currentDate = new Date();
		long duration = currentDate.getTime() - oldDate.getTime();

		long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

		if (diffInHours > 10000)
			return true;
		else
			return false;
	}

	// Update node information in database if it's old (older than 1min - for
	// test)
	// TODO:Change to 1h later
	public static void updateFromNodes(List<SmartCity> nodeList) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		for (SmartCity node : nodeList) {
			if (Utils.isOld(node.getLastUpdate())) {
				try {
					SmartCity updateNode = restTemplate.getForObject("http://" + node.getUrl() + ":9090" + "/getinfo",
							SmartCity.class);
					node.setQuality(updateNode.getQuality());
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
				// Commit update to database
				scRepo.save(node);
			}
		}
	}

	public static List<RealDistance> saveRealDistance(List<SmartCity> allNodes) throws Exception {
		// Get real distance and duration values from google and save them in DB
		List<RealDistance> addedElements = new LinkedList<RealDistance>();
		for (SmartCity i : allNodes)
			for (SmartCity j : allNodes) {
				// Set default value of -1 for all nodes
				double realDist = -1, duration = -1;
				if (i == j)
					continue;
				// Add entry to DB if not already in it or it is older than the
				// period (30min)
				RealDistance realDistance = rDistRepo.findByRowAndCol(i.getId(), j.getId());
				if (realDistance == null || Utils.isOld(realDistance.getLastUpdate()) ||
						realDistance.getDistValue() == -1 || realDistance.getDurValue() == -1) {
					try {
						GoogleWebService gService = new GoogleWebService();
						gService.getAttributes(i.getLocation(), j.getLocation());
						realDist = gService.getDistList().get(0);
						duration = gService.getDurList().get(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// If it's null then set new entry
					if (realDistance == null)
						realDistance = new RealDistance(i.getId(), j.getId(), realDist, duration);
					else {
						// If already exists update duration and distance values
						// and add it to the array
						realDistance.setDistValue(realDist);
						realDistance.setDurValue(duration);
					}

					addedElements.add(realDistance);
					rDistRepo.save(realDistance);
				}


			}
		// return rDistRepo.findAll();
		if (addedElements.isEmpty())
			throw new Exception("Nothing to update!");
		else
			return addedElements;
	}

	public static List<Cache> saveCache(CachePoints cachePoints, List<SmartCity> allNodes) throws Exception {
		// Get real distance and duration values for the start and finish from
		// google and save them in DB
		//TODO: Both are the same, so delete one part, node to sf or sf to node
		List<Cache> addedElements = new LinkedList<Cache>();
		List<SmartCity> tempNodes = new LinkedList<SmartCity>();

		Location destination = cachePoints.getDestinationLocation();
		Location source = cachePoints.getSourceLocation();
		// Create nodes
		SmartCity start = new SmartCity(0, source, "start");
		SmartCity finish = new SmartCity(0, destination, "finish");

		tempNodes.add(start);
		tempNodes.add(finish);
		
		for (SmartCity sfNode : tempNodes)
			for (SmartCity node : allNodes) {
				// Set default value of -1 for all nodes
				double realDist = -1, duration = -1, realDistC = -1, durationC = -1;
				if (node == start || node == finish)
					continue;
				// Add entry to DB if not already in it or it is older than the
				// period (30min)
				Cache cache = cRepo.findByRowAndCol(node.getId().toString(), sfNode.getServiceType());
				//Converese, for start/finish to node
				Cache cacheC = cRepo.findByRowAndCol(sfNode.getServiceType(), node.getId().toString());


				if (cache == null || cacheC == null
						|| Utils.isOld(cache.getLastUpdate()) 
						|| Utils.isOld(cacheC.getLastUpdate())
						|| cache.getDistValue() == -1
						|| cache.getDurValue() == -1
						|| cacheC.getDistValue() == -1
						|| cacheC.getDurValue() == -1){
					try {
						GoogleWebService gService = new GoogleWebService();

						gService.getAttributes(node.getLocation(), sfNode.getLocation());
						realDist = gService.getDistList().get(0);
						duration = gService.getDurList().get(0);

						gService.getAttributes(sfNode.getLocation(), node.getLocation());
						realDistC = gService.getDistList().get(0);
						durationC = gService.getDurList().get(0);

					} catch (Exception e) {
						e.printStackTrace();
					}
					// If it's null then set new entry
					if (cacheC == null || cacheC == null) {
						if (cache == null)
							cache = new Cache(node.getId().toString(), sfNode.getServiceType(), realDist, duration);
						if (cacheC == null)
							cacheC = new Cache(sfNode.getServiceType(), node.getId().toString(), realDistC, durationC);
					} else {
						// If already exists update duration and distance values
						// and
						// add it to the array
						cache.setDistValue(realDist);
						cache.setDurValue(duration);
						cacheC.setDistValue(realDistC);
						cacheC.setDurValue(durationC);
					}

					addedElements.add(cache);
					addedElements.add(cacheC);
					
					cRepo.save(cache);
					cRepo.save(cacheC);
				}

			}
		// return rDistRepo.findAll();
		if (addedElements.isEmpty())
			throw new Exception("Nothing to update!");
		else
			return addedElements;
	}

	public static List<RealDistance> saveRealDistance(SmartCity node, List<SmartCity> allNodes) throws Exception {
		// Get real distance and duration values from google and save them in DB
		List<RealDistance> addedElements = new LinkedList<RealDistance>();
		for (SmartCity i : allNodes) {
			// Set default value of -1 for all nodes
			if (i == node)
				continue;

			double realDist = -1, duration = -1, realDistRev = -1, durationRev = -1;
			try {
				GoogleWebService gService = new GoogleWebService();
				GoogleWebService gService_ = new GoogleWebService();

				gService.getAttributes(i.getLocation(), node.getLocation());
				realDist = gService.getDistList().get(0);
				duration = gService.getDurList().get(0);

				gService_.getAttributes(node.getLocation(), i.getLocation());
				realDistRev = gService_.getDistList().get(0);
				durationRev = gService_.getDurList().get(0);

			} catch (Exception e) {
				e.printStackTrace();
			}
			RealDistance dist1 = new RealDistance(i.getId(), node.getId(), realDist, duration);
			RealDistance dist2 = new RealDistance(node.getId(), i.getId(), realDistRev, durationRev);
			addedElements.add(dist1);
			addedElements.add(dist2);
			rDistRepo.save(dist1);
			rDistRepo.save(dist2);

		}
		// return rDistRepo.findAll();
		return addedElements;
	}
	
	public static void updateCache(Service compositeService) throws Exception{
		//TODO: Update cache function, add new cache for new locations
		//TODO: Delete old cache
		CachePoints cachePoints = new CachePoints(compositeService.getSourceLocation(),
										compositeService.getDestinationLocation());
		saveCache(cachePoints, scRepo.findAll());
	}

	public static RealDistance findByRowAndColumn(Long row, Long column) {
		return rDistRepo.findByRowAndCol(row, column);
	}

	public static Cache findByRowAndColumn(String row, String column) {
		return cRepo.findByRowAndCol(row, column);
	}

}
