package io.spring.sample.graphqlmusic.faker;

import io.spring.sample.graphqlmusic.lyrics.LyricsData;
import io.spring.sample.graphqlmusic.tracks.Album;
import io.spring.sample.graphqlmusic.tracks.Artist;
import io.spring.sample.graphqlmusic.tracks.Playlist;
import io.spring.sample.graphqlmusic.tracks.Track;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

/**
 * Generate demo data for the GraphQL Music application.
 * @see DataFaker
 */
@Component
public class DemoDataGenerator {

    private final DataFaker faker = new DataFaker();

    public Artist createArtist() {
        return new Artist(this.faker.playlist().artistName());
    }

    public Album createAlbum(Set<Artist> artists) {
        Album album = new Album(this.faker.playlist().albumName(), Set.of(this.faker.playlist().musicGenre()),
                this.faker.playlist().releaseDate(), this.faker.code().ean13());
        album.setArtists(artists);
        return album;
    }

    public List<Track> createTracks(Album album) {
        List<Track> tracks = new ArrayList<>();
        for (int i= 1 ;i < this.faker.random().nextInt(8, 13); i++) {
            tracks.add(Track.of(i, this.faker.playlist().trackTitle(), this.faker.playlist().trackDuration(),
                    album.getArtists(), album, this.faker.playlist().trackRating()));
        }
        return tracks;
    }

    public LyricsData createLyrics(String songId) {
        try {
            return new LyricsData(songId, URI.create("https://lyrics.example.org/file/" + songId + ".txt").toURL());
        } catch (MalformedURLException exc) {
            throw new IllegalStateException("could not create lyrics URL", exc);
        }
    }

    public Playlist createPlaylist(String authorName, List<Album> albums) {
        Set<String> trackIds = new HashSet<>();
        Integer trackCount = this.faker.random().nextInt(15, 30);
        int albumCount = albums.size();
        Playlist playList = new Playlist(this.faker.playlist().playlistName(), authorName);
        for (int i = 0; i< trackCount; i++) {
            int randomAlbumIndex = this.faker.random().nextInt(albumCount);
            Album randomAlbum = albums.get(randomAlbumIndex);
            int tracksSkipped = this.faker.random().nextInt(randomAlbum.getTrackIds().size() - 1);
            randomAlbum.getTrackIds().stream().skip(tracksSkipped).findFirst().ifPresent(trackIds::add);
        }
        playList.setTrackIds(trackIds);
        return playList;
    }

}
