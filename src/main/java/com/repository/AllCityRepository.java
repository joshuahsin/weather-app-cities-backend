package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.AllCity;
import com.entity.City;

@Repository
public interface AllCityRepository extends JpaRepository<AllCity, Integer>{

}
