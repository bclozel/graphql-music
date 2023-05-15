package io.spring.sample.graphqlmusic.tracks;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PlaylistRepository extends ReactiveCrudRepository<Playlist, String> {

    Flux<Playlist> findByAuthor(String author);
}
