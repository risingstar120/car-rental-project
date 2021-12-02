package com.example1.carrental.repo;

import com.example1.carrental.constant.FuelType;
import com.example1.carrental.constant.GearBoxType;
import com.example1.carrental.domain.Car;
import com.example1.carrental.domain.CarPackage;
import com.example1.carrental.domain.CarParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarRepoTest {

        @Autowired
        private CarRepo underTest;

        @BeforeEach
        void setUp() {
                Car car = new Car(null, "RSA45362", "Audi", "S6", true, new CarPackage(null, "SPORTY", 300),
                        new CarParameters(null, FuelType.PETROL, GearBoxType.AUTOMATIC, 5, 5, true));
                underTest.save(car);
        }

        @AfterEach
        void tearDown() {
                underTest.deleteAll();
        }

        @Test
        void itShouldReturnAvailableCars() {

                List<Car> cars = underTest.findCars(PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "id")));

                assertThat(underTest.findAvailableCars(PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "id"))))
                        .isEqualTo(cars.stream()
                                .filter(car -> car.getIsAvailable().equals(true))
                                .collect(Collectors.toList()));

        }

}