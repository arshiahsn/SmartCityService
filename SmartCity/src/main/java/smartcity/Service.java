package smartcity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

public class Service {
	private Location sourceLocation;
	private String[] compositeServices;
	private Location destinationLocation;


	public Service(){
	}
	public Service(Location sourceLocation,
			String[] compositeServices,
			Location destinationLocation){
		this.sourceLocation = sourceLocation;
		this.compositeServices = compositeServices;
		this.destinationLocation = destinationLocation;		
	}


	

	@Override
	public String toString(){
		return "Service{" +
				"sourceLocation=" + sourceLocation +
				", compositeServices=" + Arrays.toString(compositeServices) +
				", destinationLocation=" + destinationLocation +
				'}';
	}
	public void scheduleServices(SmartCityRepository repository){
		//TODO:Google API
		//TODO:Schedule services based on Google Cost and Quality
		//A HashMap of ID and Service Type to hold found nodes with defined services

		List<SmartCity> foundNodes = new ArrayList<SmartCity>();
		//TODO:Fix this
		for(String serviceString : compositeServices)
			for (SmartCity node : repository.findAll()) 
				if(serviceString.equals(node.getServiceType()))
				{
					foundNodes.add(node);
				}

		Utils.updateFromNodes(foundNodes,repository);
		//TODO:Extract information from found nodes
		//Run CSP Algorithm on nodes
		Utils.shuffleArray(this.compositeServices);
	}
	
	

	
	
	public Location getSourceLocation() {
		return sourceLocation;
	}
	public void setSourceLocation(Location sourceLocation) {
		this.sourceLocation = sourceLocation;
	}
	public String[] getCompositeServices() {
		return compositeServices;
	}
	public void setCompositeServices(String[] compositeServices) {
		this.compositeServices = compositeServices;
	}
	public Location getDestinationLocation() {
		return destinationLocation;
	}
	public void setDestinationLocation(Location destinationLocation) {
		this.destinationLocation = destinationLocation;
	}



}
