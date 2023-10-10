package io.spring.sample.graphqlmusic.lyrics;

import java.net.URL;

public record LyricsData(String trackId, URL file) {
}
