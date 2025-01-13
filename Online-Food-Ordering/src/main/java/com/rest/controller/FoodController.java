package com.rest.controller;

import com.rest.model.Food;
import com.rest.model.User;
import com.rest.service.FoodService;
import com.rest.service.RestaurantService;
import com.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private FoodService foodService;
    @Autowired
    private UserService userService;
    @Autowired
    private RestaurantService restaurantService;

    // Класс для обработки ошибок
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        // Геттер и сеттер
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFood(@RequestParam String keyword,
                                        @RequestHeader("Authorization") String jwt) {
        try {
            // Попытка найти пользователя по JWT
            User user = userService.findUserByJwtToken(jwt);

            // Поиск еды
            List<Food> foods = foodService.searchFood(keyword);

            // Возвращаем список еды
            return new ResponseEntity<>(foods, HttpStatus.OK);
        } catch (Exception e) {
            // В случае ошибки возвращаем объект с ошибкой
            ErrorResponse errorResponse = new ErrorResponse("Произошла ошибка при поиске еды.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<?> getRestaurantFood(@PathVariable Long restaurantId,
                                               @RequestParam boolean vegeterian,
                                               @RequestParam boolean seasonal,
                                               @RequestParam boolean nonveg,
                                               @RequestParam(required = false) String food_category,
                                               @RequestHeader("Authorization") String jwt) {
        try {
            // Попытка найти пользователя по JWT
            User user = userService.findUserByJwtToken(jwt);

            // Получение еды ресторана
            List<Food> foods = foodService.getRestaurantsFood(restaurantId, vegeterian, nonveg, seasonal, food_category);

            // Возвращаем список еды
            return new ResponseEntity<>(foods, HttpStatus.OK);
        } catch (Exception e) {
            // В случае ошибки возвращаем объект с ошибкой
            ErrorResponse errorResponse = new ErrorResponse("Произошла ошибка при получении еды из ресторана.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
