package org.acme.song.indexer.app;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

@Component
public class SongConsumer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EmitterProcessor<ServerSentEvent<String>> emitter = EmitterProcessor.create();

    public Flux<ServerSentEvent<String>> get() {
        return emitter.log();
    }

    @KafkaListener(topics = "${kafka.topic}")
    public void receive(@Payload String data) throws JsonMappingException, JsonProcessingException {

        Song song = OBJECT_MAPPER.readValue(data, Song.class);

        System.out.println(song + " indexed.");

        emitter.onNext(ServerSentEvent.builder("Song " + song.id + " processed")
            .id(UUID.randomUUID().toString())
        .build());
    }
}