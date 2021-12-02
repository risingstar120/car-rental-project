package com.example1.carrental.mapper;

import com.example1.carrental.domain.User;
import com.example1.carrental.dto.UserEditDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserEditDtoMapper {

        public static List<UserEditDto> mapUserToUserDto(List<User> users) {
                return users.stream()
                        .map(user -> new UserEditDto(user.getId(), user.getFirstName(),
                                user.getLastName(), user.getUsername(), user.getPassword(),
                                user.getEmail(), user.getPhone())).collect(Collectors.toList());
        }

}