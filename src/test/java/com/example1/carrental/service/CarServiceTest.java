package com.example1.carrental.service;

import com.example1.carrental.domain.Car;
import com.example1.carrental.domain.CarPackage;
import com.example1.carrental.repo.CarPackageRepo;
import com.example1.carrental.repo.CarRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

        @Mock
        CarRepo carRepo;

        @Mock
        CarPackageRepo carPackageRepo;

        @InjectMocks
        CarService carService;


        @Test()
        void itShouldReturnAnEmptyInstanceOfCar() {
                when(carRepo.findById(anyLong())).thenReturn(Optional.of(new Car()));

                assertThat(carService.getCar(1L)).hasAllNullFieldsOrProperties();
        }

        @Test
        void itShouldSaveCar() {
                Car car = Car.builder().brand("Audi").model("A4").build();

                when(carRepo.save(car)).thenReturn(car);

                Car saved = carService.saveCar(car);

                assertThat(car.getModel()).isEqualTo(saved.getModel());
                assertThat(car.getBrand()).isEqualTo(saved.getBrand());
        }

        @Test
        void itShouldCheckIfCarIsEdited() {
                Car car = Car.builder().id(2L).build();

                when(carRepo.findById(2L)).thenReturn(Optional.of(car));
                when(carRepo.save(car)).thenReturn(car);

                assertThat(carService.editCar(car)).isEqualTo(car);

        }

        @Test
        void itShouldDeleteCar() {
                Car car = Car.builder().id(4L).build();
                doNothing().when(carRepo).deleteById(4L);

                carService.deleteCar(4L);

                verify(carRepo, times(1)).deleteById(4L);
        }

        @Test
        void itShouldReturnAllCars() {
                Car car = Car.builder().registrationNr("GHF88493").brand("Bentley").model("Continental").isAvailable(true).build();
                Car car2 = Car.builder().registrationNr("HGF78493").brand("Lamborghini").model("Huracan").isAvailable(true).build();
                Car car3 = Car.builder().registrationNr("KMN74837").brand("Volkswagen").model("Golf").isAvailable(false).build();
                ArrayList<Car> cars = new ArrayList<>();
                cars.add(car);
                cars.add(car2);
                cars.add(car3);

                when(carRepo.findCars(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id"))))
                .thenReturn(cars);

                assertThat(carService.getAllCars(1, Sort.Direction.ASC)).hasSize(3);
        }

        @Test
        void itShouldReturnAvailableCars() {
                Car available1 = Car.builder().registrationNr("OPE74639").brand("Audi").model("80").isAvailable(true).build();
                Car notAvailable1 = Car.builder().registrationNr("JKD94839").brand("Rolls-Royce").model("Phantom").isAvailable(false).build();
                Car available2 = Car.builder().registrationNr("HJD85743").brand("Fiat").model("Stilo").isAvailable(true).build();
                Car available3 = Car.builder().registrationNr("ASD84754").brand("Toyota").model("Yaris").isAvailable(true).build();
                Car notAvailable2 = Car.builder().registrationNr("OIU95840").brand("Opel").model("Insignia").isAvailable(false).build();
                ArrayList<Car> cars = new ArrayList<>();
                cars.add(available1);
                cars.add(notAvailable1);
                cars.add(available2);
                cars.add(available3);
                cars.add(notAvailable2);

                when(carRepo.findAvailableCars(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id"))))
                        .thenReturn(cars.stream()
                        .filter(car -> car.getIsAvailable()).collect(Collectors.toList()));

                assertThat(carService.getAvailableCars(1, Sort.Direction.ASC)).hasSize(3);
        }

        @Test
        void itShouldReturnCarPackages() {
                CarPackage carPackage1 = new CarPackage();
                CarPackage carPackage2 = new CarPackage();
                List<CarPackage> list = Arrays.asList(carPackage1, carPackage2);

                when(carPackageRepo.findAll()).thenReturn(list);

                assertThat(carService.getCarPackages()).isEqualTo(list);

        }

        @Test
        void itShouldSaveCarPackage() {
                CarPackage luxury = CarPackage.builder().packageName("Luxury").pricePerHour(500).build();

                when(carPackageRepo.save(luxury)).thenReturn(luxury);

                CarPackage saved = carService.saveCarPackage(luxury);

                assertThat(saved).isEqualTo(luxury);
        }

        @Test
        void itShouldCheckIfPackageIsEdited() {
                CarPackage sporty = CarPackage.builder().id(5L).packageName("Sporty").pricePerHour(300).build();

                when(carPackageRepo.findById(5L)).thenReturn(Optional.of(sporty));
                when(carPackageRepo.save(sporty)).thenReturn(sporty);

                assertThat(carService.editCarPackage(sporty)).isEqualTo(sporty);
        }

        @Test
        void itShouldDeleteCarPackage() {
                CarPackage ordinary = CarPackage.builder().id(3L).packageName("Ordinary").pricePerHour(100).build();
                CarPackage awesome = CarPackage.builder().id(4L).packageName("Awesome").pricePerHour(800).build();

                doNothing().when(carPackageRepo).deleteById(3L);
                doNothing().when(carPackageRepo).deleteById(4L);

                carService.deleteCarPackage(3L);
                carService.deleteCarPackage(4L);

                verify(carPackageRepo, times(1)).deleteById(3L);
                verify(carPackageRepo, times(1)).deleteById(4L);
        }

}