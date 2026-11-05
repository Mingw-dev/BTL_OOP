package com.example.ilms.service;

import com.example.ilms.dto.user.*;
import com.example.ilms.entity.Role;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.RoleRepository;
import com.example.ilms.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserCreateRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .role(role)
                .build();
        User saveUser = userRepository.save(user);
        return modelMapper.map(saveUser, UserResponseDTO.class);
    }

    public UserResponseDTO getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserResponseDTO responseDTO = modelMapper.map(user, UserResponseDTO.class);

        if (responseDTO.getRoleName() == null) {
            responseDTO.setRoleName(user.getRole().getName());
        }

        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO updateUser(
            UUID id,
            UserUpdateRequestDTO updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getMiddleName() != null) {
            user.setMiddleName(updateRequest.getMiddleName());
        }
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    public UserResponseDTO upDateRole(UUID id, UserUpdateRoleRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByCode(request.getRole())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

}