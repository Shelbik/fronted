package com.rest.controller;

import com.rest.config.JwtProvider;
import com.rest.model.Cart;
import com.rest.model.USER_ROLE;
import com.rest.model.User;
import com.rest.repository.CartRepository;
import com.rest.repository.UsersRepository;
import com.rest.request.LoginRequest;
import com.rest.response.AuthResponce;
import com.rest.service.imp.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponce> createUserHandle(@RequestBody User user) {
        if (usersRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already used with another account");
        }

        USER_ROLE role = user.getRole() != null ? user.getRole() : USER_ROLE.ROLE_CUSTOMER;

        User createdUser = new User();
        createdUser.setEmail(user.getEmail());
        createdUser.setFullname(user.getFullname());
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = usersRepository.save(createdUser);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        Authentication authentication = authenticate(user.getEmail(), user.getPassword());
        String jwt = jwtProvider.generateToken(authentication);

        AuthResponce authResponce = new AuthResponce(jwt, savedUser.getRole(), "Register success");
        authResponce.setJwt(jwt);
        authResponce.setMessage("Register success");
        authResponce.setRole(savedUser.getRole());

        return new ResponseEntity<>(authResponce, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponce> signin(@RequestBody LoginRequest req) {
        Authentication authentication = authenticate(req.getEmail(), req.getPassword());

        try {
            // Проверка на специальный логин и пароль для администратора
            if ("admin@gmail.com".equals(req.getEmail()) && "admin123+".equals(req.getPassword())) {
                AuthResponce adminResponse = new AuthResponce("Admin login success", USER_ROLE.ROLE_ADMIN, null);
                adminResponse.setMessage("Welcome, Admin!");
                adminResponse.setRole(USER_ROLE.ROLE_ADMIN);
                String jwt = jwtProvider.generateToken(authentication);
                AuthResponce authResponce = new AuthResponce("Admin login success", USER_ROLE.ROLE_ADMIN, jwt);

                return new ResponseEntity<>(authResponce, HttpStatus.OK);
            }

            // Стандартный процесс аутентификации

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            String jwt = jwtProvider.generateToken(authentication);

            AuthResponce authResponce = new AuthResponce("Login success", USER_ROLE.valueOf(role), jwt);
            return new ResponseEntity<>(authResponce, HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            AuthResponce authResponce = new AuthResponce("Invalid email or password");
            authResponce.setError("Invalid email or password");
            return new ResponseEntity<>(authResponce, HttpStatus.UNAUTHORIZED);
        }
    }

    private Authentication authenticate(String username, String pass) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid Username...");
        }

        if (!passwordEncoder.matches(pass, userDetails.getPassword())) {
            throw new BadCredentialsException(("Invalid Password"));
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
