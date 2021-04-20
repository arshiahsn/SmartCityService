package com.SmartCity.smartcity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
	List<Location> findByLatitudeAndLongitude(double latitude, double longitude);
}