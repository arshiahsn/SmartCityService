package smartcity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;



public interface SmartCityRepository extends CrudRepository<SmartCity, Long> {
    List<SmartCity> findByServiceType(String serviceType);
    //There's only one instance of each unique service provider in a specific location
    SmartCity findByServiceTypeAndLocation(String ServiceType, Location location);


}



