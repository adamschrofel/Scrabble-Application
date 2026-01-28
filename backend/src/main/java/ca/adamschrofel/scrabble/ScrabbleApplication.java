package ca.adamschrofel.scrabble;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Scrabble Word Finder Spring Boot application.
 * This class initializes the Spring context and starts the embedded Tomcat server.
 * Provides REST endpoints for word solving and definitions.
 */
@SpringBootApplication
public class ScrabbleApplication {

	/**
	 * Main method to launch application.
	 * Starts the embedded web server on port 8080
	 */
	public static void main(String[] args) {
		SpringApplication.run(ScrabbleApplication.class, args);
	}

}
