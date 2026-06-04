package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dao.CityDAO;
import com.dao.SavedCityDAO;
import com.dao.UserDAO;
import com.dto.ChangePasswordRequest;
import com.service.RegistrationService;
import com.dto.LoginRequest;
import com.entity.City;
import com.entity.SavedCity;
import com.entity.User;
import com.entity.Role;
import com.service.EmailService;

@RestController
@RequestMapping("/weatherCities")
@CrossOrigin(origins = "http://localhost:3000")
public class AppController {
	@GetMapping("/health")
	public String health() {
		return "healthy";
	}

	@Autowired
	private CityDAO cityDAO;

	@GetMapping("/cities")
	public List<City> getAllCities() {
		return cityDAO.getAllCities();
	}

	@GetMapping("/city/{id}")
	public City getCityById(@PathVariable int id) {
		return cityDAO.getCitybyID(id);
	}

	@GetMapping("/cityExists")
	public boolean getCityByCityState(@RequestParam String city, @RequestParam String state) {
		return cityDAO.getCitybyCityState(city, state);
	}

	@PostMapping("/city")
	public City addCity(@RequestBody City city) {
		return cityDAO.addCity(city);
	}

	@PostMapping("/cities")
	public List<City> addCityList(@RequestBody List<City> city_list) {
		return cityDAO.addCityList(city_list);
	}

	@PutMapping("/city/{id}")
	public City updateCity(@PathVariable int id, @RequestBody City city) {
		return cityDAO.updateCity(id, city);
	}

	@DeleteMapping("/city/{id}")
	public boolean deleteCity(@PathVariable int id) {
		return cityDAO.deleteCityByID(id);
	}

	@DeleteMapping("/cityByCityState")
	public boolean deleteCityByCityState(@RequestParam String city, @RequestParam String state) {
		return cityDAO.deleteCityByCityState(city, state);
	}

	@Autowired
	private SavedCityDAO savedCityDAO;

	@GetMapping("/savedCities/{userId}")
	public List<SavedCity> getSavedCitiesByUserId(@PathVariable Long userId) {
		return savedCityDAO.getSavedCitiesByUserId(userId);
	}

	@PostMapping("/savedCity")
	public SavedCity addSavedCity(@RequestParam Long userId, @RequestParam int cityId) {
		return savedCityDAO.addSavedCity(userId, cityId);
	}

	@DeleteMapping("/savedCity/{id}")
	public boolean deleteSavedCity(@PathVariable int id) {
		return savedCityDAO.deleteSavedCity(id);
	}

	@DeleteMapping("/savedCityByUserAndCity")
	public boolean deleteSavedCityByUserAndCity(@RequestParam Long userId, @RequestParam int cityId) {
		return savedCityDAO.deleteSavedCityByUserAndCity(userId, cityId);
	}

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private EmailService emailService;

	@GetMapping("/AllUsers")
	public List<User> getAllUsers() {
		return userDAO.getAllUsers();
	}

	@GetMapping("/getUserById/{id}")
	public User getUserById(@PathVariable Long id) {
		return userDAO.getUserById(id);
	}

	@GetMapping("/getUserByUsername")
	public User getUserByUsername(@RequestParam String username) {
		return userDAO.getUserByUsername(username);
	}

	@GetMapping("/getUserByEmail")
	public User getUserByEmail(@RequestParam String email) {
		return userDAO.getUserByEmail(email);
	}

	@GetMapping("/getUsersByRole")
	public List<User> getUsersByRole(@RequestParam Role role) {
		return userDAO.getUsersByRole(role);
	}

	@GetMapping("/userExistsByUsername")
	public boolean userExistsByUsername(@RequestParam String username) {
		return userDAO.userExistsByUsername(username);
	}

	@GetMapping("/userExistsByEmail")
	public boolean userExistsByEmail(@RequestParam String email) {
		return userDAO.userExistsByEmail(email);
	}

	@PostMapping("/login")
	public boolean login(@RequestBody LoginRequest loginRequest) {
		return userDAO.userExistsByUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword());
	}

	@PostMapping("/register")
	public String register(@Valid @RequestBody User user) {
		return registrationService.initiateRegistration(user);
	}

	@PostMapping("/verifyEmail")
	public User verifyEmail(@RequestParam String email, @RequestParam String code) {
		return registrationService.completeRegistration(email, code);
	}

	@PostMapping("/addUserList")
	public List<User> addUserList(@RequestBody List<User> userList) {
		return userDAO.addUserList(userList);
	}

	@PutMapping("/updateUser/{id}")
	public User updateUser(@PathVariable Long id, @RequestBody User user) {
		return userDAO.updateUser(id, user);
	}

	@PatchMapping("/changePassword/{id}")
	public User changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
		return userDAO.updateUserPassword(id, request.getNewPassword());
	}

	@DeleteMapping("/deleteUser/{id}")
	public List<User> deleteUser(@PathVariable Long id) {
		return userDAO.deleteUserById(id);
	}

	@DeleteMapping("/deleteUserByUsername")
	public boolean deleteUserByUsername(@RequestParam String username) {
		return userDAO.deleteUserByUsername(username);
	}

	@PostMapping("/forgotPassword")
	public String forgotPassword(@RequestParam String email) {
		try {
			User user = userDAO.getUserByEmail(email);
			String temporaryPassword = generateTemporaryPassword();
			userDAO.updateUserPasswordByUsername(user.getUsername(), temporaryPassword);
			emailService.sendTemporaryPassword(user.getEmail(), user.getUsername(), temporaryPassword);
			return "Temporary password sent to your email address.";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();

	private String generateTemporaryPassword() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder tempPassword = new StringBuilder();
		for (int i = 0; i < 12; i++) {
			tempPassword.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
		}
		return tempPassword.toString();
	}
}
