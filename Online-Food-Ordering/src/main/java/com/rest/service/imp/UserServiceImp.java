package com.rest.service.imp;

import com.rest.config.JwtProvider;
import com.rest.model.*;
import com.rest.repository.*;
import com.rest.request.UpdateProfileRequest;
import com.rest.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // Метод для извлечения email из JWT
    private String extractEmailFromToken(String jwt) {
        return jwtProvider.getEmailFromJwtToken(jwt);
    }

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        String email = extractEmailFromToken(jwt);
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
    }

    @Override
    public User findUserById(long id) throws Exception {
        return usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + id));
    }

    @Override
    public User updateUserProfile(String jwt, UpdateProfileRequest updateRequest) throws Exception {
        User user = findUserByJwtToken(jwt);

        // Обновляем только указанные поля
        if (updateRequest.getFullname() != null) {
            user.setFullname(updateRequest.getFullname());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }

        // Сохраняем изменения
        return usersRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUserByJwtToken(String jwt) throws Exception {
        // Извлекаем email
        String email = extractEmailFromToken(jwt);




        // Находим пользователя
        User user = usersRepository.findByEmail(email.toLowerCase()) // Сравнение email без учета регистра
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));


        // Логирование начала удаления
        System.out.println("Начинается удаление пользователя с email: " + email);

        // Удаляем связанные данные
       deleteRelatedData(user);

        // Удаляем пользователя
        usersRepository.delete(user);
        System.out.println("Пользователь с email " + email + " успешно удалён.");
    }

    private void deleteRelatedData(User user) {
        // Удаление заказов
        List<Order> orders = orderRepository.findByUserId(user.getId());
        if (!orders.isEmpty()) {
            System.out.println("Удаляем заказы пользователя: " + orders.size());
            orderRepository.deleteAll(orders);
        }

        // Удаление адресов
        List<Address> addresses = addressRepository.findByUserId(user.getId());
        if (!addresses.isEmpty()) {
            System.out.println("Удаляем адреса пользователя: " + addresses.size());
            addressRepository.deleteAll(addresses);
        }

        // Удаление корзины
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart != null) {
            System.out.println("Удаляем корзину пользователя");
            cartRepository.delete(cart);
        }

        // Удаление ресторана
        Restaurant restaurant = restaurantRepository.findAllByOwnerId(user.getId());
        if (restaurant != null) {
            System.out.println("Удаляем ресторан пользователя");
            restaurantRepository.delete(restaurant);
        }
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) throws Exception {


        // Находим пользователя по id
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));

        System.out.println(user.getId());
        System.out.println(user.getFullname());
        System.out.println(user.getClass());
        System.out.println(user.getEmail());

        // Логирование начала удаления
        System.out.println("Начинается удаление пользователя с id: " + id);

        // Удаляем связанные данные
        deleteRelatedData(user);

        // Удаляем пользователя
        usersRepository.delete(user);
        System.out.println("Пользователь с id " + id + " успешно удалён.");
    }

}
