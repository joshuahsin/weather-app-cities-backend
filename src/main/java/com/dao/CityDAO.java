package com.dao;

import java.util.List;

import com.entity.City;

public interface CityDAO {
	List<City> getAllCities();
	City getCitybyID(int id);
	boolean getCitybyCityState(String city, String state);
	City addCity(City city);
	List<City> addCityList(List<City> city_list);
	City updateCity(int id, City city);
	boolean deleteCityByID(int id);
	boolean deleteCityByCityState(String city, String state);
}
