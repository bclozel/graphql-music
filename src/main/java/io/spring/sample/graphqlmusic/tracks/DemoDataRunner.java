package io.spring.sample.graphqlmusic.tracks;

import io.spring.sample.graphqlmusic.faker.DemoDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DemoDataRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoDataRunner.class);

    private final ReactiveMongoTemplate mongoTemplate;

    private final DemoDataGenerator demoDataGenerator;

    public DemoDataRunner(ReactiveMongoTemplate mongoTemplate, DemoDataGenerator demoDataGenerator) {
        this.mongoTemplate = mongoTemplate;
        this.demoDataGenerator = demoDataGenerator;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Flux.merge(this.mongoTemplate.dropCollection(Track.class), this.mongoTemplate.dropCollection(Artist.class),
                this.mongoTemplate.dropCollection(Album.class),
                this.mongoTemplate.dropCollection(Playlist.class)).blockLast();
        createMongoDbIndexFor(Track.class);

        int artistCount = 5;
        int albumsPerArtist = 3;
        int playlistCount = 5;
        List<Album> albums = new ArrayList<>();
        for (int i = 0; i < artistCount; i++) {
            Artist newArtist = this.mongoTemplate.save(this.demoDataGenerator.createArtist()).block();
            for (int j = 0; j < albumsPerArtist; j++) {
                Album newAlbum = this.mongoTemplate.insert(this.demoDataGenerator.createAlbum(Set.of(newArtist))).block();
                Iterable<Track> newTracks = this.mongoTemplate.insertAll(this.demoDataGenerator.createTracks(newAlbum)).toIterable();
                newAlbum.addTracks(newTracks);
                albums.add(this.mongoTemplate.save(newAlbum).block());
            }
        }

        List<Playlist> playlists = new ArrayList<>();
        for (int i = 0; i < playlistCount; i++) {
            playlists.add(this.demoDataGenerator.createPlaylist("rstoyanchev", albums));
            playlists.add(this.demoDataGenerator.createPlaylist("bclozel", albums));
        }
        this.mongoTemplate.insertAll(playlists)
                .doOnNext(playlist -> logger.info(playlist.toString()))
                .blockLast();
    }

    private void createMongoDbIndexFor(Class<?> entityClass) {
        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = this.mongoTemplate
                .getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
        ReactiveIndexOperations indexOperations = mongoTemplate.indexOps(entityClass);
        Flux.fromIterable(resolver.resolveIndexFor(entityClass))
                .flatMap(indexOperations::ensureIndex)
                .blockLast();
    }
}
