package com.rest.service;

import com.rest.model.User;
import com.rest.request.UpdateProfileRequest;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    // Найти пользователя по JWT токену
    public User findUserByJwtToken(String jwt) throws Exception;

    // Найти пользователя по email
    public User findUserByEmail(String email) throws Exception;

    User findUserById(long id) throws Exception;

    // Обновить профиль пользователя
    public User updateUserProfile(String jwt, UpdateProfileRequest updateRequest) throws Exception;

    // Удалить пользователя
    public void deleteUserByJwtToken(String jwt) throws Exception;

    @Transactional
    void deleteUserById(Long id) throws Exception;
}
