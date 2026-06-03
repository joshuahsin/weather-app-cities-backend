package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.SavedCity;

import jakarta.transaction.Transactional;

@Repository
public interface SavedCityRepository extends JpaRepository<SavedCity, Integer> {
	List<SavedCity> findByUser_Id(Long userId);

	@Transactional
	long deleteByUser_IdAndCity_Id(Long userId, int cityId);
}
