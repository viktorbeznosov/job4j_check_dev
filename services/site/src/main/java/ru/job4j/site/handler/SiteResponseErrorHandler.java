package ru.job4j.site.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import ru.job4j.site.exception.IdNotFoundException;
import java.io.IOException;

@Slf4j
@Component
public class SiteResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        HttpStatus.Series series = httpResponse.getStatusCode().series();
        return (series == HttpStatus.Series.CLIENT_ERROR
                || series == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        HttpStatus statusCode = httpResponse.getStatusCode();

        if (statusCode.series() == HttpStatus.Series.SERVER_ERROR) {
            throw new IdNotFoundException("Сервер временно недоступен");
        }
        if (statusCode == HttpStatus.NOT_FOUND) {
            throw new IdNotFoundException("Ресурс не найден (404)");
        }
        if (statusCode.series() == HttpStatus.Series.CLIENT_ERROR) {
            throw new IdNotFoundException("Ошибка клиента: " + statusCode.value());
        }
    }
}