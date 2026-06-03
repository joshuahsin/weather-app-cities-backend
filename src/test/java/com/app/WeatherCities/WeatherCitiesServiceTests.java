package com.app.WeatherCities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.entity.City;
import com.entity.Role;
import com.entity.SavedCity;
import com.entity.User;
import com.exception.CityNotFoundException;
import com.exception.UserNotFoundException;
import com.repository.CityRepository;
import com.repository.SavedCityRepository;
import com.repository.UserRepository;
import com.service.CityDaoImpl;
import com.service.SavedCityDaoImpl;
import com.service.UserDaoImpl;

@ExtendWith(MockitoExtension.class)
public class WeatherCitiesServiceTests {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityDaoImpl cityService;

    @Mock
    private SavedCityRepository savedCityRepository;

    @InjectMocks
    private SavedCityDaoImpl savedCityService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDaoImpl userService;

    // --- City (catalog) tests ---

    @Test
    void testFindAllCities() {
        when(cityRepository.findAll()).thenReturn(List.of(new City(1, "New York", "NY", "USA"),
                                                          new City(2, "Los Angeles", "CA", "USA")));
        List<City> cities = cityService.getAllCities();
        assertThat(cities).isNotEmpty();
        assertThat(cities.size()).isEqualTo(2);
        assertThat(cities.get(0).getCity()).isEqualTo("New York");
        assertThat(cities.get(0).getState()).isEqualTo("NY");
        assertThat(cities.get(0).getCountry()).isEqualTo("USA");
        assertThat(cities.get(1).getCity()).isEqualTo("Los Angeles");
        assertThat(cities.get(1).getState()).isEqualTo("CA");
        assertThat(cities.get(1).getCountry()).isEqualTo("USA");
    }

    @Test
    void testFindCityById() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(new City(1, "New York", "NY", "USA")));
        City city = cityService.getCitybyID(1);
        assertThat(city).isNotNull();
        assertThat(city.getCity()).isEqualTo("New York");
        assertThat(city.getState()).isEqualTo("NY");
        assertThat(city.getCountry()).isEqualTo("USA");
    }

    @Test
    void testFindCityByIdNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cityService.getCitybyID(1)).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testFindCityByCityState() {
        when(cityRepository.findByCityAndState("New York", "NY")).thenReturn(List.of(new City(1, "New York", "NY", "USA")));
        assertThat(cityService.getCitybyCityState("New York", "NY")).isTrue();
    }

    @Test
    void testFindCityByCityStateNotFound() {
        when(cityRepository.findByCityAndState("New York", "NY")).thenReturn(List.of());
        assertThat(cityService.getCitybyCityState("New York", "NY")).isFalse();
    }

    @Test
    void testAddCity() {
        City city = new City(1, "New York", "NY", "USA");
        when(cityRepository.save(city)).thenReturn(city);
        City addedCity = cityService.addCity(city);
        assertThat(addedCity).isNotNull();
        assertThat(addedCity.getCity()).isEqualTo("New York");
        assertThat(addedCity.getState()).isEqualTo("NY");
        assertThat(addedCity.getCountry()).isEqualTo("USA");
    }

    @Test
    void testAddCityList() {
        List<City> cities = List.of(new City(1, "New York", "NY", "USA"),
                                    new City(2, "Los Angeles", "CA", "USA"));
        when(cityRepository.saveAll(cities)).thenReturn(cities);
        assertThat(cityService.addCityList(cities)).isNotNull();
    }

    @Test
    void testUpdateCity() {
        City city = new City(1, "New York", "NY", "USA");
        City updatedCity = new City(1, "Los Angeles", "CA", "USA");
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(cityRepository.save(updatedCity)).thenReturn(updatedCity);
        City result = cityService.updateCity(1, updatedCity);
        assertThat(result).isNotNull();
        assertThat(result.getCity()).isEqualTo("Los Angeles");
        assertThat(result.getState()).isEqualTo("CA");
        assertThat(result.getCountry()).isEqualTo("USA");
    }

    @Test
    void testUpdateCityNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cityService.updateCity(1, new City(1, "Los Angeles", "CA", "USA"))).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testDeleteCity() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(new City(1, "New York", "NY", "USA")));
        assertThat(cityService.deleteCityByID(1)).isTrue();
    }

    @Test
    void testDeleteCityNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cityService.deleteCityByID(1)).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testDeleteCityByCityState() {
        when(cityRepository.deleteByCityAndState("New York", "NY")).thenReturn(1L);
        assertThat(cityService.deleteCityByCityState("New York", "NY")).isTrue();
    }

    @Test
    void testDeleteCityByCityStateNotFound() {
        when(cityRepository.deleteByCityAndState("New York", "NY")).thenReturn(0L);
        assertThat(cityService.deleteCityByCityState("New York", "NY")).isFalse();
    }

    // --- SavedCity (join table) tests ---

    @Test
    void testGetSavedCitiesByUserId() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        City city = new City(1, "New York", "NY", "USA");
        when(savedCityRepository.findByUser_Id(1L)).thenReturn(List.of(new SavedCity(user, city)));
        List<SavedCity> results = savedCityService.getSavedCitiesByUserId(1L);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getUser().getUsername()).isEqualTo("John Doe");
        assertThat(results.get(0).getCity().getCity()).isEqualTo("New York");
    }

    @Test
    void testAddSavedCity() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        City city = new City(1, "New York", "NY", "USA");
        SavedCity savedCity = new SavedCity(user, city);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(savedCityRepository.save(any(SavedCity.class))).thenReturn(savedCity);
        SavedCity result = savedCityService.addSavedCity(1L, 1);
        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("John Doe");
        assertThat(result.getCity().getCity()).isEqualTo("New York");
    }

    @Test
    void testAddSavedCityUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> savedCityService.addSavedCity(1L, 1)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testAddSavedCityCityNotFound() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> savedCityService.addSavedCity(1L, 1)).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testDeleteSavedCity() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        City city = new City(1, "New York", "NY", "USA");
        SavedCity savedCity = new SavedCity(user, city);
        when(savedCityRepository.findById(1)).thenReturn(Optional.of(savedCity));
        assertThat(savedCityService.deleteSavedCity(1)).isTrue();
    }

    @Test
    void testDeleteSavedCityNotFound() {
        when(savedCityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> savedCityService.deleteSavedCity(1)).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testDeleteSavedCityByUserAndCity() {
        when(savedCityRepository.deleteByUser_IdAndCity_Id(1L, 1)).thenReturn(1L);
        assertThat(savedCityService.deleteSavedCityByUserAndCity(1L, 1)).isTrue();
    }

    @Test
    void testDeleteSavedCityByUserAndCityNotFound() {
        when(savedCityRepository.deleteByUser_IdAndCity_Id(1L, 1)).thenReturn(0L);
        assertThat(savedCityService.deleteSavedCityByUserAndCity(1L, 1)).isFalse();
    }

    // --- User tests ---

    @Test
    void testFindAllUsers() {
        User user1 = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user1.setId(1L);
        User user2 = new User("Jane Doe", "jane.doe@example.com", "password", Role.USER);
        user2.setId(2L);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<User> users = userService.getAllUsers();
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0).getUsername()).isEqualTo("John Doe");
        assertThat(users.get(0).getEmail()).isEqualTo("john.doe@example.com");
        assertThat(users.get(0).getRole()).isEqualTo(Role.USER);
        assertThat(users.get(1).getUsername()).isEqualTo("Jane Doe");
        assertThat(users.get(1).getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(users.get(1).getRole()).isEqualTo(Role.USER);
    }

    @Test
    void testFindUserById() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User foundUser = userService.getUserById(1L);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("John Doe");
        assertThat(foundUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void testFindUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(1L)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testFindUserByUsername() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        when(userRepository.findByUsername("John Doe")).thenReturn(Optional.of(user));
        User foundUser = userService.getUserByUsername("John Doe");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("John Doe");
        assertThat(foundUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void testFindUserByUsernameNotFound() {
        when(userRepository.findByUsername("John Doe")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserByUsername("John Doe")).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testFindUserByEmail() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        User foundUser = userService.getUserByEmail("john.doe@example.com");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("John Doe");
        assertThat(foundUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void testFindUserByEmailNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserByEmail("john.doe@example.com")).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testFindUsersByRole() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        when(userRepository.findByRole(Role.USER)).thenReturn(List.of(user));
        List<User> users = userService.getUsersByRole(Role.USER);
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo("John Doe");
        assertThat(users.get(0).getEmail()).isEqualTo("john.doe@example.com");
        assertThat(users.get(0).getRole()).isEqualTo(Role.USER);
    }

    @Test
    void testFindUsersByRoleNotFound() {
        when(userRepository.findByRole(Role.USER)).thenReturn(List.of());
        assertThat(userService.getUsersByRole(Role.USER)).isEmpty();
    }

    @Test
    void testUserExistsByUsername() {
        when(userRepository.existsByUsername("John Doe")).thenReturn(true);
        assertThat(userService.userExistsByUsername("John Doe")).isTrue();
    }

    @Test
    void testUserExistsByUsernameNotFound() {
        when(userRepository.existsByUsername("John Doe")).thenReturn(false);
        assertThat(userService.userExistsByUsername("John Doe")).isFalse();
    }

    @Test
    void testUserExistsByEmail() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);
        assertThat(userService.userExistsByEmail("john.doe@example.com")).isTrue();
    }

    @Test
    void testUserExistsByEmailNotFound() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        assertThat(userService.userExistsByEmail("john.doe@example.com")).isFalse();
    }

    @Test
    void testUserExistsByUsernameAndPassword() {
        when(userRepository.existsByUsernameAndPassword("John Doe", "password")).thenReturn(true);
        assertThat(userService.userExistsByUsernameAndPassword("John Doe", "password")).isTrue();
    }

    @Test
    void testUserExistsByUsernameAndPasswordNotFound() {
        when(userRepository.existsByUsernameAndPassword("John Doe", "password")).thenReturn(false);
        assertThat(userService.userExistsByUsernameAndPassword("John Doe", "password")).isFalse();
    }

    @Test
    void testAddUser() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        when(userRepository.save(user)).thenReturn(user);
        User addedUser = userService.addUser(user);
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getUsername()).isEqualTo("John Doe");
        assertThat(addedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(addedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void testAddUserList() {
        List<User> users = List.of(new User("John Doe", "john.doe@example.com", "password", Role.USER),
                                    new User("Jane Doe", "jane.doe@example.com", "password", Role.USER));
        when(userRepository.saveAll(users)).thenReturn(users);
        List<User> addedUsers = userService.addUserList(users);
        assertThat(addedUsers).isNotNull();
        assertThat(addedUsers.size()).isEqualTo(2);
    }

    @Test
    void testUpdateUser() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        User updatedUser = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        updatedUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        userService.updateUser(1L, updatedUser);
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(1L, new User("John Doe", "john.doe@example.com", "password", Role.USER))).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testDeleteUserById() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of());
        List<User> deletedUsers = userService.deleteUserById(1L);
        assertThat(deletedUsers).isEmpty();
    }

    @Test
    void testDeleteUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deleteUserById(1L)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testDeleteUserByUsername() {
        when(userRepository.deleteByUsername("John Doe")).thenReturn(1L);
        assertThat(userService.deleteUserByUsername("John Doe")).isTrue();
    }

    @Test
    void testDeleteUserByUsernameNotFound() {
        when(userRepository.deleteByUsername("John Doe")).thenReturn(0L);
        assertThat(userService.deleteUserByUsername("John Doe")).isFalse();
    }

    @Test
    void testUpdateUserPassword() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setPassword("newpassword");
        when(userRepository.save(user)).thenReturn(user);
        User updatedUser = userService.updateUserPassword(1L, "newpassword");
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPassword()).isEqualTo("newpassword");
    }

    @Test
    void testUpdateUserPasswordNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUserPassword(1L, "newpassword")).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testUpdateUserPasswordByUsername() {
        User user = new User("John Doe", "john.doe@example.com", "password", Role.USER);
        user.setId(1L);
        when(userRepository.findByUsername("John Doe")).thenReturn(Optional.of(user));
        user.setPassword("newpassword");
        when(userRepository.save(user)).thenReturn(user);
        User updatedUser = userService.updateUserPasswordByUsername("John Doe", "newpassword");
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPassword()).isEqualTo("newpassword");
    }

    @Test
    void testUpdateUserPasswordByUsernameNotFound() {
        when(userRepository.findByUsername("John Doe")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUserPasswordByUsername("John Doe", "newpassword")).isInstanceOf(UserNotFoundException.class);
    }
}
