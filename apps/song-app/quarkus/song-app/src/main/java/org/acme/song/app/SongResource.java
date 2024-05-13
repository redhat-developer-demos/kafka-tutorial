package org.acme.song.app;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.Record;

@Path("/songs")
public class SongResource {
    
    @Inject
    @Channel("songs")
    Emitter<Record<Integer,Song>> songs;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSong(Song song) {    
        Log.info(song);
        var completionStage = songs.send(Record.of(song.id(),song));
        completionStage.toCompletableFuture().join();
        return Response.status(Response.Status.CREATED).build();
    }

}
