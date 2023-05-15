package io.spring.sample.graphqlmusic.tracks;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document
public class Playlist {

    private String id;

    private final String name;

    private final String author;

    private Set<String> trackIds;

    public Playlist(String name, String author) {
        this.name = name;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getTrackIds() {
        return trackIds;
    }

    public void setTrackIds(Set<String> trackIds) {
        this.trackIds = trackIds;
    }

    public void addTrack(Track track) {
        if (this.trackIds == null) {
            this.trackIds = new HashSet<>();
        }
        this.trackIds.add(track.getId());
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
