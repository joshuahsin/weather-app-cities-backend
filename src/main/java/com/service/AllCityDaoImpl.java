package com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.AllCityDAO;
import com.entity.AllCity;
import com.exception.CityNotFoundException;
import com.repository.AllCityRepository;

@Service
public class AllCityDaoImpl implements AllCityDAO{
	@Autowired
	private AllCityRepository cityRepo;
	
	@Override
	public List<AllCity> getAllCities() {
		return cityRepo.findAll();
	}

	@Override
	public AllCity getCitybyID(int id) {
		Optional<AllCity> city = cityRepo.findById(id);
		if(city.isPresent()) {
			return city.get();
		}
		throw new CityNotFoundException(id);
	}

	@Override
	public AllCity addCity(AllCity city) {
		return cityRepo.save(city);
	}
	
	@Override
	public List<AllCity> addCityList(List<AllCity> city_list) {
		// TODO Auto-generated method stub
		return cityRepo.saveAll(city_list);
	}

	@Override
	public AllCity updateCity(int id, AllCity city) {
		if(cityRepo.findById(id).isPresent()) {
			return cityRepo.save(city);
		}
		throw new CityNotFoundException(id);
	}

	@Override
	public boolean deleteCityByID(int id) {
		if(cityRepo.findById(id).isPresent()) {
			cityRepo.deleteById(id);
			return true;
		}
		throw new CityNotFoundException(id);
	}
}
