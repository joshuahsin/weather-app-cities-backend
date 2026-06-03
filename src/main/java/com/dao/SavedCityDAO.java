package com.dao;

import java.util.List;

import com.entity.SavedCity;

public interface SavedCityDAO {
	List<SavedCity> getSavedCitiesByUserId(Long userId);
	SavedCity addSavedCity(Long userId, int cityId);
	boolean deleteSavedCity(int id);
	boolean deleteSavedCityByUserAndCity(Long userId, int cityId);
}
