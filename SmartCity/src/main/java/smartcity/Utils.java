package smartcity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;


public class Utils {

	public static long idCounter = 0;
	//Check if a node has registered already
	public static boolean nodeNotExists(SmartCityRepository scRepo,
			LocationRepository locRepo,
			SmartCity node){
		List<SmartCity> temp1 = scRepo.findByServiceType(node.getServiceType());
		List<Location> temp2 = locRepo.findByLatitudeAndLongitude(node.getLocation().getLatitude(), 
				node.getLocation().getLongitude());
		boolean flag = false;
		if(temp1.isEmpty() || temp2.isEmpty())
			return !flag;			
		else{
			for(int j = 0; j < temp2.size(); j++)
				for(int i = 0; i < temp1.size(); i++){
					if(temp1.get(i).getLocation() == temp2.get(j)){
						return flag;
					}

				}

		}
		return !flag;
	}
	//Using shuffle for now
	// Implementing Fisher–Yates shuffle
	public static void shuffleArray(String[] ar)
	{
		// If running on Java 6 or older, use `new Random()` on RHS here
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			String a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
	
	//Calculating difference of a Date with current time
	//Returns true if it's older than one minute
	public static boolean isOld(Date oldDate){
        Date currentDate = new Date();
        long duration = currentDate.getTime() - oldDate.getTime();
        
        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        
        if(diffInMinutes > 1)
        	return true;
        else
        	return false;
}
	
	//Update node information in database if it's old (older than 1min - for test)
	//TODO:Change to 1h later
	public static void updateFromNodes(List<SmartCity> nodeList, SmartCityRepository scRepo){
		RestTemplate restTemplate = new RestTemplate();
		for(SmartCity node : nodeList){
			if(Utils.isOld(node.getLastUpdate()))
			{
				SmartCity updateNode = restTemplate.getForObject(
						"http://"+ node.getUrl() + ":9090" +"/getinfo", SmartCity.class);
				node.setQuality(updateNode.getQuality());
				//Commit update to database
				scRepo.save(node);			}
		}
	}
}
