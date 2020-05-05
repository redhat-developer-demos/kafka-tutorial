package org.acme.song.indexer.app;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class SongController {

    private SongConsumer songConsumer;

    public SongController(SongConsumer songConsumer) {
        this.songConsumer = songConsumer;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> getEvents() {
        return songConsumer.get();
    }

}