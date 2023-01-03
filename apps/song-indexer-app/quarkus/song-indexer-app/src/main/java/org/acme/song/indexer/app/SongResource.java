package org.acme.song.indexer.app;

import java.util.UUID;

import javax.annotation.PostConstruct;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;

import org.eclipse.microprofile.reactive.messaging.Channel;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;

@Startup
@Path("/events")
public class SongResource {
    
    @Context
    Sse sse;

    @Inject
    @Channel("songs")
    Multi<Record<Integer, Song>> songs;

    Multi<OutboundSseEvent> broadcastSseStream;

    @PostConstruct
    public void setupBroadcastingSseStream() {
        this.broadcastSseStream = 
            songs
                .onItem().invoke(kr -> Log.info("execute business logic -> "+kr.value()+" indexed."))    
                .map(
                    kr -> sse.newEventBuilder()
                        .id(UUID.randomUUID().toString())
                        .data("Song "+kr.value().id()+" processed")
                    .build()
                ).broadcast().toAllSubscribers();
        this.broadcastSseStream.subscribe().with(evt -> {});
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<OutboundSseEvent> getEvents() {    
        return broadcastSseStream
                .log()
                .onItem()
                .invoke(evt -> Log.info("sending sse event with [id: "+evt.getId()+", data: "+evt.getData()+"] to connected client"));
    }

}