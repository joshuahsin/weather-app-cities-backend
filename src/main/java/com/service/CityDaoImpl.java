package com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.CityDAO;
import com.entity.City;
import com.exception.CityNotFoundException;
import com.repository.CityRepository;

@Service
public class CityDaoImpl implements CityDAO {
	@Autowired
	private CityRepository cityRepo;

	@Override
	public List<City> getAllCities() {
		return cityRepo.findAll();
	}

	@Override
	public City getCitybyID(int id) {
		Optional<City> city = cityRepo.findById(id);
		if (city.isPresent()) {
			return city.get();
		}
		throw new CityNotFoundException(id);
	}

	@Override
	public boolean getCitybyCityState(String city, String state) {
		return !cityRepo.findByCityAndState(city, state).isEmpty();
	}

	@Override
	public City addCity(City city) {
		return cityRepo.save(city);
	}

	@Override
	public List<City> addCityList(List<City> city_list) {
		return cityRepo.saveAll(city_list);
	}

	@Override
	public City updateCity(int id, City city) {
		if (cityRepo.findById(id).isPresent()) {
			return cityRepo.save(city);
		}
		throw new CityNotFoundException(id);
	}

	@Override
	public boolean deleteCityByID(int id) {
		if (cityRepo.findById(id).isPresent()) {
			cityRepo.deleteById(id);
			return true;
		}
		throw new CityNotFoundException(id);
	}

	@Override
	public boolean deleteCityByCityState(String city, String state) {
		return cityRepo.deleteByCityAndState(city, state) > 0;
	}
}
