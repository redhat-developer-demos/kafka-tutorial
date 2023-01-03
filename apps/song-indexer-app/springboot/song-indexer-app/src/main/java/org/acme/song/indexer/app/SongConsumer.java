package org.acme.song.indexer.app;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class SongConsumer {

    Sinks.Many<ServerSentEvent<String>> hotSource = Sinks.many().multicast().directBestEffort();
    Flux<ServerSentEvent<String>> sseFlux = hotSource.asFlux();
    
    public SongConsumer() {
        sseFlux.log().subscribe();    
    }

    public Flux<ServerSentEvent<String>> get() {
        return sseFlux;
    }

    @KafkaListener(topics = "${kafka.topic}")
    public void receive(@Payload Song song) throws JsonMappingException, JsonProcessingException {
        System.out.println(song + " indexed.");
        hotSource.tryEmitNext(
            ServerSentEvent.builder("Song " + song.id() + " processed")
                .id(UUID.randomUUID().toString()).build()
        ).orThrow();
    }
}