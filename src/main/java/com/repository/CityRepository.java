package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.entity.City;

import jakarta.transaction.Transactional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer>{
	List<City> findByCityAndState(String city, String state);
	@Transactional
	long deleteByCityAndState(String city, String state);
}
