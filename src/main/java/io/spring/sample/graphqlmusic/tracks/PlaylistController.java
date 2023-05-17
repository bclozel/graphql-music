package io.spring.sample.graphqlmusic.tracks;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class PlaylistController {

    private final ReactiveMongoTemplate mongoTemplate;

    private final PlaylistRepository playlistRepository;

    public PlaylistController(ReactiveMongoTemplate mongoTemplate, PlaylistRepository playlistRepository) {
        this.mongoTemplate = mongoTemplate;
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

    @SchemaMapping
    public Mono<Window<Track>> tracks(Playlist playList, ScrollSubrange subrange) {
        Query query = Query.query(Criteria.where("id").in(playList.getTrackIds()))
                .limit(subrange.count().orElse(10))
                .with(subrange.position().orElse(ScrollPosition.offset()))
                .with(Sort.by("title"));
        return this.mongoTemplate.scroll(query, Track.class);
    }

}
