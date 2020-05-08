package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class MusicChartTopologyProducer {
    static final String SONGS_STORE = "songs-store";
    static final String SONGS_TOPIC = "songs";
    static final String PLAYED_SONGS_TOPIC = "played-songs";

    @Produces
    public Topology getTopCharts() {

        final StreamsBuilder builder = new StreamsBuilder();
        final JsonbSerde<Song> songSerde = new JsonbSerde<>(Song.class);
        final JsonbSerde<PlayedSong> playedSongSerde = new JsonbSerde<>(PlayedSong.class);

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(SONGS_STORE);

        final GlobalKTable<Integer, Song> songs = builder.globalTable(
                SONGS_TOPIC,
                Consumed.with(Serdes.Integer(), songSerde));

        builder.stream(
            PLAYED_SONGS_TOPIC,
            Consumed.with(Serdes.Integer(), Serdes.String())
        )
        .join(songs, 
                (songId, timestampAndUserId) -> songId, 
                (timestampAndUserId, song) -> {
                    return song.getName();
                }
                
        )
        .groupByKey()
        .aggregate(PlayedSong::new, 
                    (songId, value, playedSong) -> playedSong.aggregate(value),
                    Materialized.<Integer, PlayedSong> as(storeSupplier)
                    .withKeySerde(Serdes.Integer())
                    .withValueSerde(playedSongSerde)
        )
        .toStream()
        .print(Printed.toSysOut());

        return builder.build();

    }

}