package iotnode;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {
	public static String configFileName = "src/main/resources/node.conf";
	public static String absolutePath = new File("src/main/resources/node.conf").getAbsolutePath();
	public static String serviceType = null;
	static Location location = null;
	static int quality = 0;
	static String[] lines = null;
	public static String serverAddress = null;
	public static IoTNode node = null;

	//A function to determine value of parameters in configuration file
	public static void setParamString(String[] lines){
		int length = lines.length;
		for(int i=0;i<length;i++)
			lines[i] = lines[i].substring(lines[i].indexOf("=")+2);

	}
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		//Reading from the configuration file "node.conf"

		try{
			ReadFile file = new ReadFile(absolutePath);
			lines = file.OpenFile();
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}
		if(lines!=null){
			setParamString(lines);
			serviceType = lines[1];
			location  = new Location (Double.parseDouble(
					lines[2].substring(
							lines[2].indexOf("[")+1, 
							lines[2].indexOf(","))),

					Double.parseDouble(
							lines[2].substring(
									lines[2].indexOf(",")+1, 
									lines[2].indexOf("]"))));
			quality = Integer.parseInt(lines[3]);
			serverAddress = lines[4];
		}
		//Instantiating a new IoT Node with configuration parameters
		node = new IoTNode(quality,location,serviceType);
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			IoTNode nodeBack = restTemplate.postForObject(
					"http://"+ serverAddress +"/registernode", node, IoTNode.class);
			log.info(nodeBack.toString());
			node.setId(nodeBack.getId());
		};
	}

}
