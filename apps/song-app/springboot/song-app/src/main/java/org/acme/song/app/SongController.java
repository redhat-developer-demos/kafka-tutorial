package org.acme.song.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Value(value = "${kafka.topic}")
    private String kafkaTopic;

    @Autowired
    private KafkaTemplate<Integer, Song> kafkaTemplate;

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public DeferredResult<ResponseEntity<?>> createSong(@RequestBody Song song) throws JsonProcessingException {
        
        DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();
        
        CompletableFuture<SendResult<Integer, Song>> cf = 
            kafkaTemplate.send(kafkaTopic,song.id(),song);

        cf.whenComplete((result, exc) -> {
            if (exc == null) {
                System.out.println("Sent message=[" + result + 
                  "] with offset=[" + result.getRecordMetadata().offset() + "]");
                response.setResult(ResponseEntity.status(HttpStatus.CREATED).build());
            }
            else {
                System.out.println("Unable to send message=["
                  + song + "] due to : " + exc.getMessage());
                response.setErrorResult(ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(exc.getMessage())
                );
            }
        });

        return response;
    }

}