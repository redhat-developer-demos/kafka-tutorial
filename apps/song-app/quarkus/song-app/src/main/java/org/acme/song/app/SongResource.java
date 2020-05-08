package org.acme.song.app;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.reactive.messaging.kafka.KafkaRecord;


@Path("/songs")
public class SongResource {
    
    @Inject
    @Channel("songs")
    Emitter<String> songs;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public CompletionStage<Void> createSong(Song song) {
        song.setOp(Operation.ADD);
        System.out.println(song);
        KafkaRecord<Integer, String> msg = KafkaRecord.of(song.id, JsonbBuilder.create().toJson(song));
        songs.send(msg);
        return CompletableFuture.completedFuture(null);
    }

}