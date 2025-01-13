package com.rest.controller;

import com.rest.model.User;
import com.rest.request.UpdateProfileRequest;
import com.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Получение профиля пользователя по JWT
    @GetMapping("/profile")
    public ResponseEntity<User> findUserByJwtToken(@RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Обновление информации профиля пользователя
    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(
            @RequestHeader("Authorization") String jwt,
            @RequestBody UpdateProfileRequest updateRequest) {
        try {
            User updatedUser = userService.updateUserProfile(jwt, updateRequest);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Удаление пользователя
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteUserProfile(@RequestHeader("Authorization") String jwt) {
        try {
            String token = jwt.startsWith("Bearer ") ? jwt.substring(7) : jwt;
            System.out.println(token);
            userService.deleteUserByJwtToken(token);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("User not found or an error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Удаление пользователя по ID
    @DeleteMapping("/profile/{id}")
    public ResponseEntity<String> deleteUserProfileById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("User not found or an error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
