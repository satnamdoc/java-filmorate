package ru.yandex.practicum.filmorate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.yandex.practicum.filmorate.controller.FilmController;

@SpringBootApplication
public class FilmorateApplication {
	private static final Logger log = LoggerFactory.getLogger(FilmController.class);

	public static void main(String[] args) {
		log.info("Запуск приложения");
		SpringApplication.run(FilmorateApplication.class, args);
	}

}
