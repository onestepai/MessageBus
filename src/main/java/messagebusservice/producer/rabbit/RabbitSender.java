package com.onestep.os.messagebusservice.producer.Rabbit;

import com.onestep.os.error.OsError;
import com.onestep.os.messagebusservice.config.ServiceConfig;
import com.onestep.os.messagebusservice.model.message.mongodb.MessageRecord;
import com.onestep.os.messagebusservice.producer.MessageSender;
import com.onestep.os.messagebusservice.util.LoggerHelper;
import com.onestep.os.utils.JsonUtils;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RabbitSender implements MessageSender {
  @Autowired private RabbitTemplate rabbitTemplate;

  @Autowired private ServiceConfig serviceConfig;

  @Autowired private DirectExchange direct;

  // 回调函数: confirm确认
  final RabbitTemplate.ConfirmCallback confirmCallback =
      new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
          LoggerHelper.info(
              String.format(
                  "CorrelationData: %s, ack: %b, cause: %s",
                  JsonUtils.writeJson(correlationData), ack, cause),
              serviceConfig.getDebugLogOn());
          String messageId = correlationData.getId();
          if (ack) {
//            messageSuccessful(messageId);
          } else {
            LoggerHelper.error(
                OsError.OS_FAILURE_INTERNAL,
                String.format("Fail to send message to rabbit, messageId: %s", messageId));
          }
        }

//        private void messageSuccessful(String messageId) {
//          Optional<MessageRecord> messageRecord = messageRecordDao.findById(messageId);
//          if (messageRecord.isPresent()) {
//            MessageRecord updateMessageRecord = messageRecord.get();
//            updateMessageRecord.setStatus(3);
//            updateMessageRecord.setUpdateTime(System.currentTimeMillis());
//            messageRecordDao.save(updateMessageRecord);
//          } else {
//            LoggerHelper.error(
//                OsError.OS_FAILURE_DOES_NOT_EXIST_DATA,
//                String.format("Invalid messageId: %s", messageId));
//          }
//        }
      };

  @Override
  public boolean sendMessage(MessageRecord messageRecord) {
    rabbitTemplate.setConfirmCallback(confirmCallback);

    CorrelationData correlationData = new CorrelationData(messageRecord.getMessageId());
    messageRecord.setTryCount(1);
    messageRecord.setStatus(2);
    messageRecord.setUpdateTime(System.currentTimeMillis());
    rabbitTemplate.convertAndSend(
        direct.getName(),
        messageRecord.getTopicKey(),
        messageRecord.getMessageContent(),
        correlationData);

    return true;
  }
}
