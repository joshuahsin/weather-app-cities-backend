package com.dao;

import java.util.List;

import com.entity.AllCity;

public interface AllCityDAO {
	public List<AllCity> getAllCities();
	public AllCity getCitybyID(int id);
	public AllCity addCity(AllCity city);
	public List<AllCity> addCityList(List<AllCity> city_list);
	public AllCity updateCity(int id, AllCity city);
	public boolean deleteCityByID(int id);
}
