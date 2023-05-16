package io.spring.sample.graphqlmusic.tracks;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Controller
public class PlaylistController {

    private final PlaylistRepository playlistRepository;

    public PlaylistController(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @QueryMapping
    public Mono<Playlist> playlist(@Argument String id) {
        return this.playlistRepository.findById(id);
    }

    @QueryMapping
    public Flux<Playlist> userPlaylists(@Argument String authorName) {
        return this.playlistRepository.findByAuthor(authorName);
    }
    
}
