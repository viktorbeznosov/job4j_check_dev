package ru.checkdev.notification.telegram.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Profile;
import ru.checkdev.notification.service.CircuitBreaker;

/**
 * Класс реализует методы get и post для отправки сообщений через WebClient
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@org.springframework.context.annotation.Profile("default")
@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TgAuthCallWebClient implements TgCall {
    private final CircuitBreaker circuitBreaker = new CircuitBreaker(3);

    @Value("${server.auth}")
    private String urlServiceAuth;

    /**
     * Метод get
     *
     * @param url URL http
     * @return Mono<Person>
     */
    @Override
    @Retry(name = "tgAuthRetry")
    @CircuitBreaker(name = "tgAuthCircuitBreaker", fallbackMethod = "fallbackGet")
    public Mono<Profile> doGet(String url) {
        return WebClient.create(urlServiceAuth)
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Profile.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    /**
     * Метод POST
     *
     * @param url     URL http
     * @param profile Body PersonDTO.class
     * @return Mono<Person>
     */
    @Override
    @Retry(name = "tgAuthRetry")
    @CircuitBreaker(name = "tgAuthCircuitBreaker", fallbackMethod = "fallbackPost")
    public Mono<Object> doPost(String url, Profile profile) {

        return Mono.defer(() -> {
            try {
                Object result = circuitBreaker.exec(
                    () -> {
                        return WebClient.create(urlServiceAuth)
                            .post()
                            .uri(url)
                            .bodyValue(profile)
                            .retrieve()
                            .bodyToMono(Object.class)
                            .block();
                    },
                    new Object()
                );
                return Mono.just(result);
            } catch (CircuitBreaker.CircuitBreakerOpenException e) {
                log.error("Circuit Breaker is OPEN, request rejected: {}", url);
                return Mono.error(e);
            }
        });
    }

    @Override
    @Retry(name = "tgAuthRetry")
    @CircuitBreaker(name = "tgAuthCircuitBreaker", fallbackMethod = "fallbackPost")
    public Mono<Object> doPost(String url) {
        return WebClient.create(urlServiceAuth)
                .post()
                .uri(url)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    public Mono<Profile> fallbackGet(String url, Throwable throwable) {
        log.error("GET request failed, fallback triggered: {}", throwable.getMessage());
        return Mono.empty();
    }

    public Mono<Object> fallbackPost(String url, Profile profile, Throwable throwable) {
        log.error("POST request failed, fallback triggered: {}", throwable.getMessage());
        return Mono.empty();
    }
}
