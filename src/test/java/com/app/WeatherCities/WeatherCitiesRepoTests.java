package com.app.WeatherCities;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import com.entity.City;
import com.entity.Role;
import com.entity.SavedCity;
import com.entity.User;
import com.exception.CityNotFoundException;
import com.exception.UserNotFoundException;
import com.repository.CityRepository;
import com.repository.SavedCityRepository;
import com.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WeatherCitiesRepoTests {

	@MockBean
	private JavaMailSender mailSender;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private SavedCityRepository savedCityRepository;

	@Autowired
	private UserRepository userRepository;

	// --- City (catalog) tests ---

	@Test
	void testFindCityByID() {
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);
		int id = city.getId();
		assertThat(cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id)).getCity()).isEqualTo("New York");
		assertThat(cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id)).getState()).isEqualTo("NY");
		assertThat(cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id)).getCountry()).isEqualTo("USA");
	}

	@Test
	void testFindAllCities() {
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);
		List<City> cities = cityRepository.findAll();
		assertThat(cities).isNotEmpty();
	}

	@Test
	void testAddCity() {
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);
		assertThat(cityRepository.findById(city.getId())).isPresent();
	}

	@Test
	void testUpdateCity() {
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		city = cityRepository.save(city);
		int id = city.getId();

		assertThat(cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id)).getCity()).isEqualTo("New York");
		assertThat(cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id)).getState()).isEqualTo("NY");

		city.setCity("Portland");
		city.setState("OR");
		cityRepository.save(city);

		assertThat(cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id)).getCity()).isEqualTo("Portland");
		assertThat(cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException(id)).getState()).isEqualTo("OR");
	}

	@Test
	void testDeleteCity() {
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);
		int id = city.getId();
		cityRepository.deleteById(id);
		assertThat(cityRepository.findById(id)).isNotPresent();
	}

	@Test
	void testFindCityByCityState() {
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);
		assertThat(cityRepository.findByCityAndState("New York", "NY")).isNotEmpty();
	}

	@Test
	void testDeleteCityByCityState() {
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);
		assertThat(cityRepository.deleteByCityAndState("New York", "NY")).isEqualTo(1);
		assertThat(cityRepository.findByCityAndState("New York", "NY")).isEmpty();
	}

	// --- SavedCity (join table) tests ---

	@Test
	void testAddSavedCity() {
		User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
		userRepository.save(user);
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);

		SavedCity savedCity = new SavedCity(user, city);
		savedCityRepository.save(savedCity);
		assertThat(savedCityRepository.findById(savedCity.getId())).isPresent();
	}

	@Test
	void testFindSavedCitiesByUserId() {
		User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
		userRepository.save(user);
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);

		savedCityRepository.save(new SavedCity(user, city));
		List<SavedCity> results = savedCityRepository.findByUser_Id(user.getId());
		assertThat(results).isNotEmpty();
		assertThat(results.get(0).getCity().getCity()).isEqualTo("New York");
	}

	@Test
	void testDeleteSavedCity() {
		User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
		userRepository.save(user);
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);

		SavedCity savedCity = savedCityRepository.save(new SavedCity(user, city));
		int id = savedCity.getId();
		savedCityRepository.deleteById(id);
		assertThat(savedCityRepository.findById(id)).isNotPresent();
	}

	@Test
	void testDeleteSavedCityByUserAndCity() {
		User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
		userRepository.save(user);
		City city = new City();
		city.setCity("New York");
		city.setState("NY");
		city.setCountry("USA");
		cityRepository.save(city);

		savedCityRepository.save(new SavedCity(user, city));
		assertThat(savedCityRepository.deleteByUser_IdAndCity_Id(user.getId(), city.getId())).isEqualTo(1);
		assertThat(savedCityRepository.findByUser_Id(user.getId())).isEmpty();
	}

	// --- User tests ---

	@Test
	void testFindUserByID() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);

		long id = user.getId();
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getUsername()).isEqualTo("John Doe");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getEmail()).isEqualTo("john.doe@example.com");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getPassword()).isEqualTo("password");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getRole()).isEqualTo(Role.USER);
	}

	@Test
	void testFindAllUsers() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		List<User> users = userRepository.findAll();
		assertThat(users).isNotEmpty();
	}

	@Test
	void testAddUser() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		assertThat(userRepository.findById(user.getId())).isPresent();
	}

	@Test
	void testUpdateUser() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);

		long id = user.getId();
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getUsername()).isEqualTo("John Doe");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getEmail()).isEqualTo("john.doe@example.com");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getPassword()).isEqualTo("password");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getRole()).isEqualTo(Role.USER);

		user.setUsername("Jane Doe");
		user.setEmail("jane.doe@example.com");
		userRepository.save(user);
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getUsername()).isEqualTo("Jane Doe");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getEmail()).isEqualTo("jane.doe@example.com");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getPassword()).isEqualTo("password");
		assertThat(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)).getRole()).isEqualTo(Role.USER);
	}

	@Test
	void testDeleteUser() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		long id = user.getId();
		userRepository.deleteById(id);
		assertThat(userRepository.findById(id)).isNotPresent();
	}

	@Test
	void testFindUserByUsername() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		assertThat(userRepository.findByUsername("John Doe")).isPresent();
	}

	@Test
	void testFindUserByEmail() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		assertThat(userRepository.findByEmail("john.doe@example.com")).isPresent();
	}

	@Test
	void testFindUsersByRole() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);

		User user2 = new User();
		user2.setUsername("Jane Doe");
		user2.setEmail("jane.doe@example.com");
		user2.setPassword("password");
		user2.setRole(Role.ADMIN);
		userRepository.save(user2);

		assertThat(userRepository.findByRole(Role.USER)).size().isEqualTo(1);
		assertThat(userRepository.findByRole(Role.ADMIN)).size().isEqualTo(1);
	}

	@Test
	void testUserExistsByUsername() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		assertThat(userRepository.existsByUsername("John Doe")).isTrue();
	}

	@Test
	void testUserExistsByEmail() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		assertThat(userRepository.existsByEmail("john.doe@example.com")).isTrue();
	}

	@Test
	void testUserExistsByUsernameAndPassword() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		assertThat(userRepository.existsByUsernameAndPassword("John Doe", "password")).isTrue();
	}

	@Test
	void testDeleteUserByUsername() {
		User user = new User();
		user.setUsername("John Doe");
		user.setEmail("john.doe@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);
		userRepository.save(user);
		assertThat(userRepository.deleteByUsername("John Doe")).isEqualTo(1);
	}
}
