package com.SmartCity.smartcity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;



public interface RealDistanceRepository extends JpaRepository<RealDistance, Long> {
    RealDistance findByRowAndCol(Long row, Long col);

}