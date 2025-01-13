package com.rest.controller.gigaAdmin;

import com.rest.model.User;
import com.rest.repository.UsersRepository;
import com.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gigadmin/users")
public class GigaAdminController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserService userService;
    // Требуется роль "ADMIN" для доступа к этому маршруту
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = usersRepository.findAll();  // Получаем всех пользователей из базы данных
        return ResponseEntity.ok(users);  // Отправляем список пользователей в ответ
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/edit/{id}")
    @GetMapping
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        try {
            Optional<User> user = usersRepository.findById(id);
            return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
