package com.avijeet.sprout.services;

import com.avijeet.sprout.dto.AddressRequestDto;
import com.avijeet.sprout.dto.UserRequestDto;
import com.avijeet.sprout.dto.UserResponseDto;
import com.avijeet.sprout.dto.mappers.UserMapper;
import com.avijeet.sprout.entities.Address;
import com.avijeet.sprout.entities.User;
import com.avijeet.sprout.enums.Role;
import com.avijeet.sprout.exceptions.UserAlreadyExistsException;
import com.avijeet.sprout.exceptions.UserDoesNotExists;
import com.avijeet.sprout.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserResponseDto addUser(UserRequestDto userRequestDto) {
        if(userRepository.findByEmail(userRequestDto.email()) != null) {
            log.error("User with email {}, already exists", userRequestDto.email());
            throw new UserAlreadyExistsException("User already exists, request denied");
        }

        User createdUser = new User();
        createdUser.setEmail(userRequestDto.email());
        createdUser.setRole(Role.CUSTOMER);
        createdUser.setAccountNonLocked(true);

        // encrypt the password
        String encodedPassword = passwordEncoder.encode(userRequestDto.password());
        createdUser.setPassword(encodedPassword);

        log.info("User with email {}, created successfully", createdUser.getEmail());

        return userMapper.toDto(userRepository.save(createdUser));
    }

    public UserResponseDto getUserDetailsByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserDoesNotExists("User with email" + email + "does not exist");
        }

        return userMapper.toDto(user);
    }

    public boolean blockUserAccount(String email) {
        User user  = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserDoesNotExists("User with email " + email + " does not exists");
        }

        if (!user.isAccountNonLocked()) {
            log.info("User {} is already blocked", email);
            return true;
        }

        user.setAccountNonLocked(false);
        userRepository.save(user);

        log.info("User account for {} has been blocked", email);

        return true;
    }

    @Transactional
    public void addAddress(Long userId, AddressRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExists("User not found"));

        Address address = Address.builder()
                .street(dto.street())
                .city(dto.city())
                .zipCode(dto.zipCode())
                .country(dto.country())
                .addressType(dto.addressType())
                .user(user)
                .build();

        user.getAddresses().add(address);
        userRepository.save(user);
    }
}
