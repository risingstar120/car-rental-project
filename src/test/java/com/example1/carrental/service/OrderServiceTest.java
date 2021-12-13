package com.example1.carrental.service;

import com.example1.carrental.domain.AccessKey;
import com.example1.carrental.domain.CarPackage;
import com.example1.carrental.domain.CreditCard;
import com.example1.carrental.domain.User;
import com.example1.carrental.exception.InsufficientFundsException;
import com.example1.carrental.exception.NoCreditCardException;
import com.example1.carrental.repo.CarPackageRepo;
import com.example1.carrental.security.LoggedInUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

        @Mock
        CarPackageRepo carPackageRepo;

        @Mock
        LoggedInUser loggedInUser;

        @InjectMocks
        OrderService orderService;


        @Test
        void itShouldReturnAccessKey() {
                CreditCard card = CreditCard.builder().cardNumber(7755443334559900L)
                        .month(4).year(2023).CVV(278).accountBalance(1200L).build();
                User user = User.builder().creditCard(card).build();

                CarPackage build = CarPackage.builder().packageName("Luxury").pricePerHour(500).build();

                when(loggedInUser.getUser()).thenReturn(user);
                when(carPackageRepo.findByPackageName("Luxury")).thenReturn(Optional.of(build));

                AccessKey accessKey = orderService.submitOrder("Luxury", 2);

                assertThat(accessKey.getCarPackage()).isEqualTo("Luxury");
                assertThat(accessKey.getHours()).isEqualTo(2);
                assertThat(user.getCreditCard().getAccountBalance()).isEqualTo(200L);
        }

        @Test
        void itShouldThrowEntityNotFoundException() {
                CreditCard card = CreditCard.builder().accountBalance(0L).build();
                User user = User.builder().username("Radoslaw").creditCard(card).build();

                when(loggedInUser.getUser()).thenReturn(user);
                when(carPackageRepo.findByPackageName(anyString())).thenThrow(EntityNotFoundException.class);

                assertThrows(EntityNotFoundException.class, () -> orderService.submitOrder("BigCar", 3));
        }

        @Test
        void itShouldThrowNoCreditCardException() {
                User user = User.builder().username("Tomasz").creditCard(null).build();

                when(loggedInUser.getUser()).thenReturn(user);

                assertThrows(NoCreditCardException.class, () -> orderService.submitOrder("Ordinary", 2));
        }

        @Test
        void itShouldThrowInsufficientFundsException() {
                CreditCard card = CreditCard.builder().accountBalance(600L).build();
                User user = User.builder().username("Radoslaw").creditCard(card).build();
                CarPackage luxury = CarPackage.builder().packageName("Luxury").pricePerHour(500).build();

                when(loggedInUser.getUser()).thenReturn(user);
                when(carPackageRepo.findByPackageName("Luxury")).thenReturn(Optional.of(luxury));

                assertThrows(InsufficientFundsException.class, () -> orderService.submitOrder("Luxury", 2));
        }

}