package org.acme.song.indexer.app;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@Path("/events")
public class SongResource {
    
    @Inject
    @Channel("songs")
    Flowable<KafkaRecord<Integer, String>> songs;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> getEvents() {
    
        return Flowable.merge(
            songs
            .map(k -> k.getPayload()),
            // Trick routers, resetting idle connections
            Flowable.interval(10, TimeUnit.SECONDS).map(x -> "{}")
            );

    }

}