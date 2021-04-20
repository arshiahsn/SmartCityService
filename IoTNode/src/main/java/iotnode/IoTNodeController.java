package iotnode;
import java.util.HashMap;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class IoTNodeController {

	@RequestMapping(value="/getquality",method=RequestMethod.GET)
	public int getQuality(){
		return Application.node.getQuality();
	}

	@RequestMapping(value="/getinfo",method=RequestMethod.GET)
	public IoTNode getInfo(){
		IoTNode newNode = Application.node;
		return newNode;
	}

	@RequestMapping(value="/updateinfo",method=RequestMethod.PUT)
	public IoTNode updateInfo(@RequestParam(value="quality") int newQuality) throws Exception{
		RestTemplate restTemplate = new RestTemplate();
		Application.node.setQuality(newQuality);
		//It's better to have lazy updates
		//Why send update when it's not needed in the server?
	/*	restTemplate.put("http://"+ Application.serverAddress +"/updatenode?" +
				 "id=" + Application.node.getId() + "&"+
				 "quality="+Application.node.getQuality(), null); */
		return Application.node;
	}
	
	@RequestMapping(value="/initnode",method=RequestMethod.PUT)
	public IoTNode setId(@RequestParam(value="id") long id) throws Exception{
		Application.node.setId(id);
		return Application.node;
	}
}
