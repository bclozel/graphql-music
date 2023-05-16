package io.spring.sample.graphqlmusic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MusicProperties.class)
public class GraphqlMusicApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlMusicApplication.class, args);
	}

}
