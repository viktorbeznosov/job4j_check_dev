package ru.job4j.site.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * CheckDev пробное собеседование
 * Создание DI объекта WebClient для сервиса auth
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 19.09.2023
 */
@Configuration
@RequiredArgsConstructor
public class WebClientAuthConfig {

    private static final String SERVICE_ID = "auth";
}
