package smartcity;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class SmartCityController {


	private final AtomicLong nodeCounter = new AtomicLong();
	@Autowired 
	SmartCityRepository repository;

	@Autowired
	LocationRepository locRepository;
	//public SmartCity setNodeId(){}


	@RequestMapping(value="/registernode",method=RequestMethod.POST)
	public SmartCity registerNode(@RequestBody SmartCity newNode,
			HttpServletRequest request) throws Exception{
		RestTemplate restTemplate = new RestTemplate();
		//Instance to access db
		SmartCity node = newNode;
		//Extracting address:port of IoT Node
		String senderIp = request.getRemoteAddr();
		node.setUrl(senderIp);
		//Trying to see if the node exists already
		if(Utils.nodeNotExists(repository, locRepository, node)){
			//Save and then fetch the node from DB and set it's ID to the returning object
			node = repository.save(node);
		}
		else
			throw new Exception("Node registered already!");
		return node;
	}

	@RequestMapping(value="/scheduleservices",method=RequestMethod.POST)
	public Service scheduleServices(@RequestBody Service requestedCompositeService) throws Exception{
		Service scheduledService = requestedCompositeService;
		scheduledService.scheduleServices(repository);
		return scheduledService;
	}

	@RequestMapping(value="/getallnodes",method=RequestMethod.GET)
	public List<SmartCity> getAllNodes(){
		List<SmartCity> nodeList = null;
		for(SmartCity smartCity : repository.findAll())
			nodeList.add(smartCity);
		return nodeList;
	}

	@RequestMapping(value="/updatenode",method=RequestMethod.PUT)
	public SmartCity updateNode(@RequestParam(value="id") long id,
			@RequestParam(value="quality") int newQuality) throws Exception{
		if(repository.exists(id)){
			SmartCity modifiedNode = repository.findOne(id);
			modifiedNode.setQuality(newQuality);
			repository.save(modifiedNode);
		}
		else{
			throw new Exception("Node "+id+" does not exist!");
		}
		return repository.findOne(id);
	}

	@RequestMapping(value="/deletenode/{id}",method=RequestMethod.DELETE)
	public void deleteNode(@PathVariable long id) throws Exception{
		//SmartCity tempNode = null;
		if(repository.exists(id)){
			repository.delete(id); ;
		}
		else{
			throw new Exception("Node "+id+" does not exist!");
		}
		//return tempNode;
	}

	@RequestMapping(value="/getnode/{id}",method=RequestMethod.GET)
	public SmartCity getNode(@PathVariable long id) throws Exception{
		return repository.findOne(id);
	}
	//TODO;fix this
	/*	@RequestMapping(value="/getnodesbytype/{serviceType}",method=RequestMethod.GET)
	public HashMap<Long,SmartCity> getNodesByType(@PathVariable char serviceType) throws Exception{
		HashMap<Long,SmartCity> foundNodes = new HashMap<Long,SmartCity>();
		Application.smartCity.forEach((k,v) -> {
			if(serviceType == v.getServiceType())
				foundNodes.put(k, v);
		});
		if(foundNodes.isEmpty())
			throw new Exception("No nodes with service type "+serviceType+" was found!");
		return foundNodes;
	}*/
}
