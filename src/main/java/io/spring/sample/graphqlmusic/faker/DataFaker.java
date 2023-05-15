package io.spring.sample.graphqlmusic.faker;

import net.datafaker.Faker;

/**
 * Custom {@link Faker} extension that adds {@link Playlist} data generation.
 */
class DataFaker extends Faker {

    public Playlist playlist() {
        return getProvider(Playlist.class, Playlist::new, this);
    }
}
