package org.acme.song.app;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
