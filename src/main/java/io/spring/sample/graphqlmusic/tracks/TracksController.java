package io.spring.sample.graphqlmusic.tracks;

import io.spring.sample.graphqlmusic.lyrics.LyricsData;
import io.spring.sample.graphqlmusic.lyrics.LyricsService;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Controller
public class TracksController {

    private final ReactiveMongoTemplate mongoTemplate;

    private final LyricsService lyricsService;

    public TracksController(ReactiveMongoTemplate mongoTemplate, LyricsService lyricsService) {
        this.mongoTemplate = mongoTemplate;
        this.lyricsService = lyricsService;
    }

    @QueryMapping
    public Mono<Window<Track>> searchForTracks(@Argument String keyword, ScrollSubrange subrange) {
        Query query = TextQuery.queryText(new TextCriteria().matchingAny(keyword.split(" ")))
                .sortByScore()
                .includeScore()
                .limit(subrange.count().orElse(10))
                .with(subrange.position().orElse(ScrollPosition.offset()));
        return this.mongoTemplate.scroll(query, Track.class);
    }

    @BatchMapping
    public Mono<Map<Track, Album>> album(List<Track> tracks) {
        Set<String> albumIds = tracks.stream().map(Track::getAlbumId).collect(Collectors.toSet());
        return this.mongoTemplate.query(Album.class)
                .matching(query(where("id").in(albumIds)))
                .all()
                .collectMap(Album::getId, Function.identity())
                .map(albumMap -> {
                    Map<Track, Album> map = new HashMap<>();
                    tracks.forEach(track -> map.put(track, albumMap.get(track.getAlbumId())));
                    return map;
                });
    }

    @BatchMapping
    public Mono<Map<Track, List<Artist>>> artists(List<Track> tracks) {
        Set<String> artistIds = tracks.stream()
                .flatMap(track -> track.getArtistIds().stream())
                .collect(Collectors.toSet());
        return this.mongoTemplate.query(Artist.class)
                .matching(query(where("id").in(artistIds)))
                .all()
                .collectMap(Artist::getId, Function.identity())
                .map(artistMap -> {
                    MultiValueMap<Track, Artist> map = new LinkedMultiValueMap<>();
                    tracks.forEach(track -> track.getArtistIds()
                            .forEach(artistId -> map.add(track, artistMap.get(artistId))));
                    return map;
                });
    }

    @SchemaMapping
    public Flux<Track> tracks(Album album) {
        return this.mongoTemplate.query(Track.class).matching(query(where("id").in(album.getTrackIds()))).all();
    }

    @BatchMapping
    public Mono<Map<Track, LyricsData>> lyrics(List<Track> tracks) {
        Map<String, Track> trackIds = tracks.stream().collect(Collectors.toMap(Track::getId, track -> track));
        return this.lyricsService.fetchLyrics(trackIds.keySet())
                .collectMap(lyricsData -> trackIds.get(lyricsData.songId()));
    }

}
