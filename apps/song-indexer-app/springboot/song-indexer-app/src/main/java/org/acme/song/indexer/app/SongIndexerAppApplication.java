package org.acme.song.indexer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SongIndexerAppApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SongIndexerAppApplication.class, args);
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		  configurer.setDefaultTimeout(600_000);
	}

}
