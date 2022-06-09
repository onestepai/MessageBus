package com.onestep.os.messagebusservice.producer.kafka;

import com.onestep.os.error.OsError;
import com.onestep.os.messagebusservice.model.message.mongodb.MessageRecord;
import com.onestep.os.messagebusservice.producer.MessageSender;
import com.onestep.os.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

@Component
public class KafkaSender implements MessageSender {
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public boolean sendMessage(MessageRecord messageRecord) {
    final ListenableFuture<SendResult<String, String>> future =
            this.kafkaTemplate.send(messageRecord.getTopicKey(),
                    messageRecord.getMessageContent());
    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
      @Override
      public void onSuccess(SendResult<String, String> result) {
        LoggerUtils.info("Successfully sent message");
      }
      @Override
      public void onFailure(Throwable ex) {
        LoggerUtils.error(OsError.OS_FAILURE_DEPENDENCY,"Failed sent message");
      }
    });
    return true;
  }
}
