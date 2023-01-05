package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import io.quarkus.kafka.client.serialization.JsonbSerde;
import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;

@ApplicationScoped
public class MusicChartTopologyProducer {
    
    private static final String SONGS_STORE = "songs-store";
    private static final String SONGS_TOPIC = "songs";
    private static final String PLAYED_SONGS_TOPIC = "played-songs";

    @RegisterForReflection
    public static record Song(int id, String name, String author) {};

    @Produces
    public Topology calculateMusicCharts() {

        final StreamsBuilder builder = new StreamsBuilder();
        final JsonbSerde<Song> songSerde = new JsonbSerde<>(Song.class);
        final JsonbSerde<SongStats> playedSongSerde = new JsonbSerde<>(SongStats.class);

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(SONGS_STORE);

        final GlobalKTable<Integer, Song> songs = builder.globalTable(
                SONGS_TOPIC,
                Consumed.with(Serdes.Integer(), songSerde)
        );

        builder.stream(
            PLAYED_SONGS_TOPIC,
            Consumed.with(Serdes.Integer(), Serdes.String())
        )
        .join(
            songs,
            (songId, userId) -> songId,
            (userId, song) -> song.name
        )
        .groupByKey()
        .aggregate(SongStats::new, 
                (songId, songName, songStats) -> songStats.aggregate(songName),
                Materialized.<Integer, SongStats> as(storeSupplier)
                    .withKeySerde(Serdes.Integer())
                    .withValueSerde(playedSongSerde)
        )
        .toStream()
        .peek((songId, songStats) -> Log.infof("music chart updated for song id %d -> %s", songId, songStats));

        return builder.build();

    }

}