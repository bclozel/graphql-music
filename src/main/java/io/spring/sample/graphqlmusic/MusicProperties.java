package io.spring.sample.graphqlmusic;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("music")
public class MusicProperties {

    private Lyrics lyrics = new Lyrics();

    public Lyrics getLyrics() {
        return this.lyrics;
    }

    public static class Lyrics {

        private Duration delay = Duration.ofSeconds(2);

        public Duration getDelay() {
            return this.delay;
        }

        public void setDelay(Duration delay) {
            this.delay = delay;
        }
    }
}