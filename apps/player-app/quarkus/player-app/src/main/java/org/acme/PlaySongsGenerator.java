package org.acme;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@ApplicationScoped
public class PlaySongsGenerator {

    public static record Song(int id, String name, String author) {};

    private static final Random RANDOM = new Random();
    
    private static final List<String> USERS = List.of("Alex", "Edson", "Burr", "Sebi", "Kamesh");

    @ConfigProperty(name = "song.play.interval.millis")
    public Optional<Integer> songPlayIntervalMillis;

    private static final List<Song> SONGS = List.of(
                new Song(1, "The Good The Bad And The Ugly", "Ennio Morricone"),
                new Song(2, "Believe", "Cher"),
                new Song(3, "Still Loving You", "Scorpions"),
                new Song(4, "Bohemian Rhapsody", "Queen"),
                new Song(5, "Sometimes", "James"),
                new Song(6, "Into The Unknown", "Frozen II"),
                new Song(7, "Fox On The Run", "Sweet"),
                new Song(8, "Perfect", "Ed Sheeran")
            );
    
    // Register songs to kafka topic. Executed once.
    @Outgoing("songs")
    public Multi<KafkaRecord<Integer, Song>> produceSongReferenceData() {
        Log.info("creating song reference data");
        return Multi.createFrom().items(
            SONGS.stream()
                .peek(song -> Log.infof("producing -> %s", song))
                .map(song -> KafkaRecord.of(song.id,song))
        );
    }

    // Continuously simulate random songs being played at certain intervals by users
    @Outgoing("played-songs")
    public Multi<KafkaRecord<Integer, String>> generateSongPlays() {
        int interval = songPlayIntervalMillis.orElse(1000);
        Log.info("starting to play random songs every "+interval+ " ms");
        return Multi.createFrom().ticks().every(Duration.ofMillis(interval))
                .onOverflow().drop()
                .map(tick -> Tuple2.of(
                            SONGS.get(RANDOM.nextInt(SONGS.size())),
                            USERS.get(RANDOM.nextInt(USERS.size()))
                        )
                )
                .invoke(t -> Log.infof("Played song id %d (%s) for user %s", t.getItem1().id, t.getItem1().name, t.getItem2()))
                .map(t -> KafkaRecord.of(t.getItem1().id,t.getItem2()));
    }

}