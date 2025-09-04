package com.dao;
import java.util.List;

import com.entity.City;

public interface CityDAO {
	public List<City> getAllCities();
	public City getCitybyID(int id);
	public boolean getCitybyCityState(String city, String state);
	public City addCity(City city);
	public List<City> addCityList(List<City> city_list);
	public City updateCity(int id, City city);
	public List<City> deleteCityByID(int id);
	public boolean deleteCityByCityState(String city, String state);
}
