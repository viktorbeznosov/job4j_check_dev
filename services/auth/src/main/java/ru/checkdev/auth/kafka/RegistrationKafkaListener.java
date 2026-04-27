package ru.checkdev.auth.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.ProfileTgDTO;
import ru.checkdev.auth.service.PersonService;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationKafkaListener {
    private final PersonService persons;

    @KafkaListener(topics = "job4j_checkdev")
    public void registerProfile(Profile profile) {
        try {
            log.debug(profile.toString());

            Optional<Profile> result = this.persons.reg(profile);
            if (result.isPresent()) {
                log.info("Profile registered {}", result);
            } else {
                log.warn("Пользователь с почтой %s уже существует {}", profile.getEmail());
            }
        } catch (KafkaException exception) {
            log.error("Error kafka listen", exception);
        }
    }
}
