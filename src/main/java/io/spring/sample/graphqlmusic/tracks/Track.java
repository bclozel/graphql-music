package io.spring.sample.graphqlmusic.tracks;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Document
public class Track {

    @Id
    private String id;

    private final int number;

    @TextIndexed
    private final String title;

    private final Duration duration;

    private final Set<String> artistIds;

    private final String albumId;

    private final Integer rating;

    @TextScore
    private  Float searchScore;


    public Track(int number, String title, Duration duration, Set<String> artistIds, String albumId, Integer rating) {
        Assert.hasText(title, "track title should not be empty");
        Assert.notNull(duration, "track duration should not be null");
        Assert.notEmpty(artistIds, "track artists should not be empty");
        Assert.notNull(albumId, "track album should not be null");
        Assert.isTrue(rating < 101, "track rating should be <= 100");
        this.number = number;
        this.title = title;
        this.duration = duration;
        this.artistIds = artistIds;
        this.albumId = albumId;
        this.rating = rating;
    }

    public static Track of(int number, String title, Duration duration, Set<Artist> artists, Album album, Integer rating) {
        Set<String> artistIds = artists.stream().map(Artist::getId).collect(Collectors.toSet());
        return new Track(number, title, duration, artistIds, album.getId(), rating);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return this.title;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public Set<String> getArtistIds() {
        return this.artistIds;
    }

    public String getAlbumId() {
        return this.albumId;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Float getSearchScore() {
        return this.searchScore;
    }

    public void setSearchScore(Float searchScore) {
        this.searchScore = searchScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
