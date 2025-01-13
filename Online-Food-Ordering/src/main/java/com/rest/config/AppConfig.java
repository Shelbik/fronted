package com.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class AppConfig {

    // Конфигурация безопасности
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Безсессионный режим
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/admin/**")
                                .hasAnyRole("RESTAURANT_OWNER", "ADMIN") // Доступ для админов и владельцев
                                .requestMatchers("/api/**")
                                .authenticated() // Требуется аутентификация для других запросов
                                .anyRequest().permitAll()) // Для всех остальных запросов доступ открыт
                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class) // Добавляем проверку JWT перед фильтром базовой аутентификации
                .csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF-защиту
                .cors(cors -> cors.configurationSource(corsConfigurationsSource())); // Настроим CORS

        return http.build();
    }

    // Конфигурация CORS
    private CorsConfigurationSource corsConfigurationsSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();

            // Разрешаем доступ с фронтенда
            cfg.setAllowedOrigins(List.of("http://localhost:3000")); // Измените на актуальный URL вашего фронтенда

            // Разрешаем методы
            cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

            // Разрешаем заголовки
            cfg.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

            // Разрешаем отправку куки и заголовков авторизации
            cfg.setAllowCredentials(true);

            // Заголовки, которые будут возвращаться
            cfg.setExposedHeaders(List.of("Authorization"));

            // Максимальное время кэширования
            cfg.setMaxAge(3600L); // 1 час

            return cfg;
        };
    }

    // Бин для кодирования паролей
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
