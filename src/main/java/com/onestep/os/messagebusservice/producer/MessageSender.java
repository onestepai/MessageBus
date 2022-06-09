package com.onestep.os.messagebusservice.producer;

import com.onestep.os.messagebusservice.model.message.mongodb.MessageRecord;

public interface MessageSender {
  boolean sendMessage(MessageRecord messageRecord);
}
