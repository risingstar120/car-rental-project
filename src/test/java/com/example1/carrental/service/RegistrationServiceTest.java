package com.example1.carrental.service;

import com.example1.carrental.domain.CreditCard;
import com.example1.carrental.domain.User;
import com.example1.carrental.dto.CreditCardDto;
import com.example1.carrental.dto.UserSaveDto;
import com.example1.carrental.repo.UserRepo;
import com.example1.carrental.security.LoggedInUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

        @Mock
        UserRepo userRepo;

        @Mock
        LoggedInUser loggedInUser;

        @InjectMocks
        RegistrationService registrationService;


        @Test
        void itShouldCheckIfPasswordIsIncorrect() {
                UserSaveDto userSaveDto = UserSaveDto.builder().username("JohnBDP685").password("johnapple56").build();

                when(userRepo.findByUsername(userSaveDto.getUsername())).thenReturn(Optional.empty());


                try (MockedStatic<PasswordValidator> mockedStatic = Mockito.mockStatic(PasswordValidator.class)) {

                        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(userSaveDto.getPassword());

                        when(PasswordValidator.matcher(userSaveDto.getPassword())).thenReturn(m);
                        registrationService.registerUser(userSaveDto);
                        assertFalse(m.find());
                }
        }

        @Test
        void itShouldAddCreditCardToUser() {
                User user = User.builder().firstName("Mickey").lastName("Rourke").build();
                CreditCardDto creditCardDto = CreditCardDto.builder().cardNumber(8888943300781111L).build();

                when(loggedInUser.getUser()).thenReturn(user);

                registrationService.addCreditCard(creditCardDto);

                assertThat(user.getCreditCard()).isNotNull().hasFieldOrPropertyWithValue("cardNumber", 8888943300781111L);
        }

        @Test
        void itShouldMakeMoneyTransfer() {
                User user = User.builder().firstName("Richard").lastName("Hammond").build();
                CreditCard creditCard = CreditCard.builder().accountBalance(0L).build();

                user.setCreditCard(creditCard);

                when(loggedInUser.getUser()).thenReturn(user);

                registrationService.moneyTransfer(700L);

                assertThat(user.getCreditCard().getAccountBalance()).isEqualTo(700L);
        }

}