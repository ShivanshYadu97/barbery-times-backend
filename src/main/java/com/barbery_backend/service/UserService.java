package com.barbery_backend.service;

import com.barbery_backend.entity.User;

import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> getCustomerByPhone(String phone);

}