package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.SavedCityDAO;
import com.entity.City;
import com.entity.SavedCity;
import com.entity.User;
import com.exception.CityNotFoundException;
import com.exception.UserNotFoundException;
import com.repository.CityRepository;
import com.repository.SavedCityRepository;
import com.repository.UserRepository;

@Service
public class SavedCityDaoImpl implements SavedCityDAO {
	@Autowired
	private SavedCityRepository savedCityRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CityRepository cityRepo;

	@Override
	public List<SavedCity> getSavedCitiesByUserId(Long userId) {
		return savedCityRepo.findByUser_Id(userId);
	}

	@Override
	public SavedCity addSavedCity(Long userId, int cityId) {
		User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
		City city = cityRepo.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
		return savedCityRepo.save(new SavedCity(user, city));
	}

	@Override
	public boolean deleteSavedCity(int id) {
		if (savedCityRepo.findById(id).isPresent()) {
			savedCityRepo.deleteById(id);
			return true;
		}
		throw new CityNotFoundException(id);
	}

	@Override
	public boolean deleteSavedCityByUserAndCity(Long userId, int cityId) {
		return savedCityRepo.deleteByUser_IdAndCity_Id(userId, cityId) > 0;
	}
}
