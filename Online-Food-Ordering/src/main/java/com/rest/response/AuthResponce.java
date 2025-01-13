package com.rest.response;

import com.rest.model.USER_ROLE;
import lombok.Data;

@Data
public class AuthResponce {

    private String jwt;         // JSON Web Token для авторизованных пользователей
    private String message;     // Сообщение для клиента (например, "Login success" или ошибка)
    private USER_ROLE role;     // Роль пользователя (например, ROLE_CUSTOMER или ROLE_ADMIN)
    private String error;       // Опциональное поле для хранения сообщения об ошибке (например, "Invalid email or password")

    // Можно добавить конструктор, чтобы удобно создавать успешный или ошибочный ответ
    public AuthResponce(String message) {
        this.message = message;
    }

    public AuthResponce(String message, USER_ROLE role, String jwt) {
        this.message = message;
        this.role = role;
        this.jwt = jwt;
    }
}
