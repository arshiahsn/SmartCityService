package com.SmartCity.smartcity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;



public interface CacheRepository extends JpaRepository<Cache, Long> {
    Cache findByRowAndCol(String row, String col);

}
