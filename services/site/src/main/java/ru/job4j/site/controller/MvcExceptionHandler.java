package ru.job4j.site.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.job4j.site.exception.IdNotFoundException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public String handleHttpNotFound(HttpClientErrorException.NotFound e, Model model, HttpServletRequest request) {
        log.error("HTTP 404 Not Found: {}", e.getMessage());
        model.addAttribute("error", "Запрашиваемый ресурс не найден");
        model.addAttribute("path", request.getRequestURI());
        return "errors/404";
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientError(HttpClientErrorException e, Model model, HttpServletRequest request) {
        log.error("HTTP Client Error {}: {}", e.getStatusCode(), e.getMessage());
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            model.addAttribute("error", "Ресурс не найден");
            return "errors/404";
        }
        model.addAttribute("error", "Ошибка клиента: " + e.getStatusCode());
        model.addAttribute("path", request.getRequestURI());
        return "errors/500";
    }

    @ExceptionHandler(IdNotFoundException.class)
    public String handleIdNotFoundException(IdNotFoundException e, Model model, HttpServletRequest request) {
        log.error("ID not found exception: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "errors/404";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException e, Model model, HttpServletRequest request) {
        log.error("Page not found: {}", e.getMessage());
        model.addAttribute("error", "Страница не найдена");
        model.addAttribute("path", request.getRequestURI());
        return "errors/404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model, HttpServletRequest request) {
        log.error("Illegal argument: {}", e.getMessage());
        if (e.getMessage() != null && e.getMessage().contains("content is null")) {
            model.addAttribute("error", "Данные не найдены");
            return "errors/404";
        }
        model.addAttribute("error", "Ошибка обработки данных: " + e.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "errors/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model, HttpServletRequest request) {
        log.error("Unexpected error: ", e);
        model.addAttribute("error", "Произошла ошибка на сервере");
        model.addAttribute("path", request.getRequestURI());
        return "errors/500";
    }
}