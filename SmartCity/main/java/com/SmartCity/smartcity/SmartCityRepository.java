package com.SmartCity.smartcity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;



public interface SmartCityRepository extends JpaRepository<SmartCity, Long> {
    List<SmartCity> findByServiceType(String serviceType);
    //There's only one instance of each unique service provider in a specific location
    SmartCity findByServiceTypeAndLocation(String ServiceType, Location location);


}



