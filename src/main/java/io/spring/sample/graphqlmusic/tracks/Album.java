package io.spring.sample.graphqlmusic.tracks;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document
public class Album {

    @Id
    private String id;

    private final String title;

    private final Set<String> genres;

    @DocumentReference
    private Set<Artist> artists;

    private final LocalDate releaseDate;

    private final String ean;

    private Set<String> trackIds;

    public Album(String title, Set<String> genres, LocalDate releaseDate, String ean) {
        Assert.hasText(title, "album title should not be empty");
        Assert.notEmpty(genres, "album genres should not be empty");
        Assert.notNull(releaseDate, "release date should not be null");
        Assert.hasText(ean, "album EAN should not be empty");
        this.title = title;
        this.genres = genres;
        this.releaseDate = releaseDate;
        this.ean = ean;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Set<String> getGenres() {
        return this.genres;
    }

    public Set<Artist> getArtists() {
        return this.artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    public LocalDate getReleaseDate() {
        return this.releaseDate;
    }

    public String getEan() {
        return this.ean;
    }

    public Set<String> getTrackIds() {
        return trackIds;
    }

    public void setTrackIds(Set<String> trackIds) {
        this.trackIds = trackIds;
    }

    public void addTrack(Track track) {
        Assert.notNull(track.getId(), "track id should not be null");
        if (this.trackIds == null) {
            this.trackIds = new HashSet<>();
        }
        this.trackIds.add(track.getId());
    }

    public void addTracks(Iterable<Track> tracks) {
        Assert.notNull(tracks, "tracks should not be null");
        tracks.forEach(this::addTrack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(id, album.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", genres=" + genres +
                ", artists=" + artists +
                ", releaseDate=" + releaseDate +
                ", ean='" + ean + '\'' +
                ", trackIds=" + trackIds +
                '}';
    }

}
