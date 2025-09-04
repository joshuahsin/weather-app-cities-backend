package com.exception;

public class CityNotFoundException extends RuntimeException {
	public CityNotFoundException(int id) {
		super("Coud not find city with the id " + id);
	}
}
