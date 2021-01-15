package smartcity;

import java.util.HashMap;
import java.util.Enumeration;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {

    //public static HashMap<Long,SmartCity> smartCity;

	public static void main(String[] args) {
    	//smartCity = new HashMap<Long,SmartCity>();
        SpringApplication.run(Application.class, args);

    }
}