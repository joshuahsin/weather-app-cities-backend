package com.app.WeatherCities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.entity.AllCity;
import com.entity.City;
import com.exception.CityNotFoundException;
import com.repository.AllCityRepository;
import com.repository.CityRepository;
import com.repository.UserRepository;
import com.service.AllCityDaoImpl;
import com.service.CityDaoImpl;
import com.service.UserDaoImpl;

@ExtendWith(MockitoExtension.class)
public class WeatherCitiesServiceTests {

    @Mock
    private AllCityRepository allCityRepository;

    @InjectMocks
    private AllCityDaoImpl allCityService;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityDaoImpl cityService;

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserDaoImpl userService;

    @Test
    void testFindAllAllCities() {
        when(allCityRepository.findAll()).thenReturn(List.of(new AllCity(1, "New York", "NY", "USA"),
                                                    new AllCity(2, "Los Angeles", "CA", "USA")));
        
        List<AllCity> allCities = allCityService.getAllCities();
        assertThat(allCities).isNotEmpty();
        assertThat(allCities.size()).isEqualTo(2);
        assertThat(allCities.get(0).getCity()).isEqualTo("New York");
        assertThat(allCities.get(0).getState()).isEqualTo("NY");
        assertThat(allCities.get(0).getCountry()).isEqualTo("USA");
        assertThat(allCities.get(1).getCity()).isEqualTo("Los Angeles");
        assertThat(allCities.get(1).getState()).isEqualTo("CA");
        assertThat(allCities.get(1).getCountry()).isEqualTo("USA");
    }

    @Test
    void testFindAllCityById() {
        when(allCityRepository.findById(1)).thenReturn(Optional.of(new AllCity(1, "New York", "NY", "USA")));
        AllCity allCity = allCityService.getCitybyID(1);
        assertThat(allCity).isNotNull();
        assertThat(allCity.getCity()).isEqualTo("New York");
        assertThat(allCity.getState()).isEqualTo("NY");
        assertThat(allCity.getCountry()).isEqualTo("USA");
    }

    @Test
    void testFindAllCityByIdNotFound() {
        when(allCityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> allCityService.getCitybyID(1)).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testAddAllCity() {
        AllCity allCity = new AllCity(1, "New York", "NY", "USA");
        when(allCityRepository.save(allCity)).thenReturn(allCity);
        AllCity addedCity = allCityService.addCity(allCity);
        assertThat(addedCity).isNotNull();
        assertThat(addedCity.getCity()).isEqualTo("New York");
        assertThat(addedCity.getState()).isEqualTo("NY");
        assertThat(addedCity.getCountry()).isEqualTo("USA");
    }

    @Test
    void testAddAllCityList() {
        List<AllCity> allCities = List.of(new AllCity(1, "New York", "NY", "USA"),
                                            new AllCity(2, "Los Angeles", "CA", "USA"));
        when(allCityRepository.saveAll(allCities)).thenReturn(allCities);
        List<AllCity> addedCities = allCityService.addCityList(allCities);
        assertThat(addedCities).isNotNull();
        assertThat(addedCities.size()).isEqualTo(2);
        assertThat(addedCities.get(0).getCity()).isEqualTo("New York");
        assertThat(addedCities.get(0).getState()).isEqualTo("NY");
        assertThat(addedCities.get(0).getCountry()).isEqualTo("USA");
        assertThat(addedCities.get(1).getCity()).isEqualTo("Los Angeles");
        assertThat(addedCities.get(1).getState()).isEqualTo("CA");
        assertThat(addedCities.get(1).getCountry()).isEqualTo("USA");
    }

    @Test
    void testUpdateAllCity() {
        AllCity allCity = new AllCity(1, "New York", "NY", "USA");
        AllCity updatedCity = new AllCity(1, "Los Angeles", "CA", "USA");
        when(allCityRepository.findById(1)).thenReturn(Optional.of(allCity));
        when(allCityRepository.save(updatedCity)).thenReturn(updatedCity);
        AllCity updatedCityReturn = allCityService.updateCity(1, updatedCity);
        assertThat(updatedCityReturn).isNotNull();
        assertThat(updatedCityReturn.getCity()).isEqualTo("Los Angeles");
        assertThat(updatedCityReturn.getState()).isEqualTo("CA");
        assertThat(updatedCityReturn.getCountry()).isEqualTo("USA");
    }

    @Test
    void testUpdateAllCityNotFound() {
        when(allCityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> allCityService.updateCity(1, new AllCity(1, "Los Angeles", "CA", "USA"))).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testDeleteAllCity() {
        when(allCityRepository.findById(1)).thenReturn(Optional.of(new AllCity(1, "New York", "NY", "USA")));
        //when(allCityRepository.deleteById(1)).thenReturn(void);
        boolean deleted = allCityService.deleteCityByID(1);
        assertThat(deleted).isTrue();
    }

    @Test
    void testDeleteAllCityNotFound() {
        when(allCityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> allCityService.deleteCityByID(1)).isInstanceOf(CityNotFoundException.class);
    }

    
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
        boolean found = cityService.getCitybyCityState("New York", "NY");
        assertThat(found).isTrue();
    }

    @Test
    void testFindCityByCityStateNotFound() {
        when(cityRepository.findByCityAndState("New York", "NY")).thenReturn(List.of());
        boolean found = cityService.getCitybyCityState("New York", "NY");
        assertThat(found).isFalse();
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
        List<City> addedCities = cityService.addCityList(cities);
        assertThat(addedCities).isNotNull();
    }

    @Test
    void testUpdateCity() {
        City city = new City(1, "New York", "NY", "USA");
        City updatedCity = new City(1, "Los Angeles", "CA", "USA");
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(cityRepository.save(updatedCity)).thenReturn(updatedCity);
        City updatedCityReturn = cityService.updateCity(1, updatedCity);
        assertThat(updatedCityReturn).isNotNull();
        assertThat(updatedCityReturn.getCity()).isEqualTo("Los Angeles");
        assertThat(updatedCityReturn.getState()).isEqualTo("CA");
        assertThat(updatedCityReturn.getCountry()).isEqualTo("USA");
    }

    @Test
    void testUpdateCityNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cityService.updateCity(1, new City(1, "Los Angeles", "CA", "USA"))).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testDeleteCity() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(new City(1, "New York", "NY", "USA")));
        boolean deleted = cityService.deleteCityByID(1);
        assertThat(deleted).isTrue();
    }
    
    @Test
    void testDeleteCityNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cityService.deleteCityByID(1)).isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void testDeleteCityByCityState() {
        when(cityRepository.deleteByCityAndState("New York", "NY")).thenReturn(1L);
        boolean deleted = cityService.deleteCityByCityState("New York", "NY");
        assertThat(deleted).isTrue();
    }
}
