package org.acme;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PlaySongsGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaySongsGenerator.class);

    private final List<String> users = Collections.unmodifiableList(
        Arrays.asList("Alex", "Edson", "Burr", "Sebi", "Kamesh"));

    private final List<Song> songs = Collections.unmodifiableList(
        Arrays.asList(
                new Song(1, "The Good The Bad And The Ugly", "Ennio Morricone"),
                new Song(2, "Believe", "Cher"),
                new Song(3, "Still Loving You", "Scorpions"),
                new Song(4, "Bohemian Rhapsody", "Queen"),
                new Song(5, "Sometimes", "James"),
                new Song(6, "Into The Unknown", "Frozen II"),
                new Song(7, "Fox On The Run", "Sweet"),
                new Song(8, "Perfect", "Ed Sheeran")
        ));
    
    // Register songs to kafka topic. Executed once.
    @Outgoing("songs")
    public Flowable<KafkaRecord<Integer, String>> songs() {
        final List<KafkaRecord<Integer, String>> songsAsJson = songs.stream()
            .map(s -> KafkaRecord.of(
                    s.id,
                    JsonbBuilder.create().toJson(s)))
            .peek(km -> logger.info("Registered: {}", km.getPayload()))
            .collect(Collectors.toList());

        return Flowable.fromIterable(songsAsJson);
    }

    @Outgoing("played-songs")
    public Flowable<KafkaRecord<Integer, String>> generatePlays() {
        return Flowable.interval(5, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .map(tick ->{
                        final Song song = songs.get(random.nextInt(songs.size()));
                        logger.info("song {}: {} played.", song.id, song.name);
                        
                        return KafkaRecord.of(song.id, Instant.now() + ";" + users.get(random.nextInt(users.size())));
                });
    }

    public static class Song {

        int id;
        String name;
        String author;
        
        public Song(int id, String name, String author) {
            this.id = id;
            this.name = name;
            this.author = author;
        }
    
        public int getId() {
            return id;
        }
    
        public String getName() {
            return name;
        }
    
        public String getAuthor() {
            return author;
        }
    
    }

    private Random random = new Random();

}