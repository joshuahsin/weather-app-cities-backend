package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dao.AllCityDAO;
import com.dao.CityDAO;
import com.entity.AllCity;
import com.entity.City;

@RestController
@RequestMapping("/weatherCities")
@CrossOrigin(origins = "http://localhost:3000")
public class AppController {
	@Autowired
	private CityDAO cityDAO;
	
	@GetMapping("/AllCities")
	public List<City> getAllCities() {
		return cityDAO.getAllCities();
	}
	
	@GetMapping("/getCityByID/{id}")
	public City getCityByID(@PathVariable int id) {
		return cityDAO.getCitybyID(id);
	}
	
	@GetMapping("/getCityByCityState")
	public boolean getCityByCityState(@RequestParam String city, @RequestParam String state) {
		return cityDAO.getCitybyCityState(city, state);
	}
	
	@PostMapping("/addCity")
	public City addCity(@RequestBody City city) {
		return cityDAO.addCity(city);
	}
	
	@PostMapping("/addCityList")
	public List<City> addCityList(@RequestBody List<City> city_list) {
		return cityDAO.addCityList(city_list);
	}
	
	@PutMapping("/updateCity/{id}")
	public City updateCity(@PathVariable int id, @RequestBody City city) {
		return cityDAO.updateCity(id, city);
	}
	
	@DeleteMapping("/deleteCity/{id}")
	public List<City> deleteCity(@PathVariable int id) {
		return cityDAO.deleteCityByID(id);
	}
	
	@DeleteMapping("/deleteCityByCityState")
	public boolean deleteCityByCityState(@RequestParam String city, @RequestParam String state) {
		System.out.println(city + ", " + state);
		return cityDAO.deleteCityByCityState(city, state);
	}
	
	@Autowired
	private AllCityDAO allCityDAO;
	
	@GetMapping("/AllAllCities")
	public List<AllCity> getAllAllCities() {
		return allCityDAO.getAllCities();
	}
	
	@GetMapping("/getAllCityByID/{id}")
	public AllCity getAllCityByID(@PathVariable int id) {
		return allCityDAO.getCitybyID(id);
	}
	
	@PostMapping("/addAllCity")
	public AllCity addAllCity(@RequestBody AllCity city) {
		return allCityDAO.addCity(city);
	}
	
	@PostMapping("/addAllCityList")
	public List<AllCity> addAllCityList(@RequestBody List<AllCity> city_list) {
		return allCityDAO.addCityList(city_list);
	}
	
	@PutMapping("/updateAllCity/{id}")
	public AllCity updateAllCity(@PathVariable int id, @RequestBody AllCity city) {
		return allCityDAO.updateCity(id, city);
	}
	
	@DeleteMapping("/deleteAllCity/{id}")
	public boolean deleteAllCity(@PathVariable int id) {
		return allCityDAO.deleteCityByID(id);
	}
}
