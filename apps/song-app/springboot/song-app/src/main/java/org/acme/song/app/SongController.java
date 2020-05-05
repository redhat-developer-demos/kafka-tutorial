package org.acme.song.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/songs")
public class SongController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value(value = "${kafka.topic}")
    private String kafkaTopic;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public DeferredResult<ResponseEntity<?>> createSong(@RequestBody Song song) throws JsonProcessingException {
        song.setOp(Operation.ADD);
        
        final DeferredResult<ResponseEntity<?>> response = new DeferredResult<>();
        String jsonSong = OBJECT_MAPPER.writeValueAsString(song); 
        final ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(kafkaTopic, song.getId(), jsonSong);
        future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
 
            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                System.out.println("Sent message=[" + result + 
                  "] with offset=[" + result.getRecordMetadata().offset() + "]");

                response.setResult(ResponseEntity.status(HttpStatus.CREATED).build());
            }
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                  + song + "] due to : " + ex.getMessage());

                response.setErrorResult(
                    ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage()));
            }
        });

        return response;
    }

}