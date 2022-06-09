package com.onestep.os.messagebusservice.service;

import com.onestep.os.messagebusservice.model.message.request.*;
import com.onestep.os.messagebusservice.model.message.response.*;

public interface MessageBusService {
  SendMessageResponseMessage sendMessage(SendMessageRequestMessage request);

  GetConsumerConfigResponseMessage getConsumerConfig(GetConsumerConfigRequestMessage request);

  // new design
  TransmitMessageResponseMessage transmitMessage(TransmitMessageRequestMessage request);

  MessageBusHeartbeatResponseMessage messageBusHeartBeat(MessageBusHeartbeatRequestMessage request);

  RoutingHeartbeatResponseMessage routingHeartBeat(RoutingHeartbeatRequestMessage request);
}
