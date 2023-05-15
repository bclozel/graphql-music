package io.spring.sample.graphqlmusic.faker;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

class Playlist extends AbstractProvider<BaseProviders> {

    public Playlist(BaseProviders faker) {
        super(faker);
        ClassPathResource resource = new ClassPathResource("faker/playlist.yml");
        try {
            faker.addUrl(Locale.ENGLISH, resource.getURL());
        }
        catch (IOException exc) {
            throw new IllegalStateException("Could not load custom fake data file", exc);
        }
    }

    public String musicGenre() {
        return resolve("playlist.genres");
    }

    public String artistName() {
        return resolve("playlist.artists");
    }

    public String albumName() {
        return resolve("playlist.albums");
    }

    public String trackTitle() {
        return resolve("playlist.titles");
    }

    public String playlistName() {
        return resolve("playlist.names");
    }

    public LocalDate releaseDate() {
        return LocalDate.parse(this.faker.date().past(365 * 20, TimeUnit.DAYS, "YYYY-MM-dd"));
    }

    public Duration trackDuration() {
        return this.faker.date().duration(2*60, 4*60, ChronoUnit.SECONDS);
    }

    public int trackRating() {
        return this.faker.random().nextInt(100);
    }

}
