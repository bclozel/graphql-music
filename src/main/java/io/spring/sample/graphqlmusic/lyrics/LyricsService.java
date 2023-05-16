package io.spring.sample.graphqlmusic.lyrics;

import io.spring.sample.graphqlmusic.MusicProperties;
import io.spring.sample.graphqlmusic.faker.DemoDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Simulate a component that makes remote calls to fetch lyrics data for songs.
 * <p>This service introduces artificial latency with the {@code "music.lyrics.delay"}
 * configuration property.
 */
@Service
public class LyricsService {

    private static final Logger logger = LoggerFactory.getLogger(LyricsService.class);

    private final DemoDataGenerator dataGenerator;

    private final MusicProperties properties;

    public LyricsService(DemoDataGenerator dataGenerator, MusicProperties properties) {
        this.dataGenerator = dataGenerator;
        this.properties = properties;
    }

    public Mono<LyricsData> fetchLyrics(String songId) {
        return Mono.just(this.dataGenerator.createLyrics(songId))
                .delayElement(properties.getLyrics().getDelay())
                .doOnSubscribe(subscription ->  logger.info("Starting to fetch lyrics for song {}", songId))
                .doOnNext(lyrics -> logger.info("Fetched lyrics for song {}", songId));
    }

    public Flux<LyricsData> fetchLyrics(Collection<String> songIds) {
        return Flux.fromIterable(songIds)
                .map(this.dataGenerator::createLyrics)
                .delaySubscription(properties.getLyrics().getDelay())
                .doOnComplete(() -> logger.info("Fetched lyrics for {} songs", songIds.size()));
    }

}
