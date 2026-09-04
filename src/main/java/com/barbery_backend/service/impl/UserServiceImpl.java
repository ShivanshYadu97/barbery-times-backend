package com.barbery_backend.service.impl;

import com.barbery_backend.entity.User;
import com.barbery_backend.repository.UserRepository;
import com.barbery_backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }
}