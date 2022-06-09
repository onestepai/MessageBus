package com.onestep.os.messagebusservice.config;

import com.onestep.os.client.RestfulClient;
import com.onestep.os.messagebusservice.model.message.request.MessageBusHeartbeatRequestMessage;
import com.onestep.os.messagebusservice.model.message.request.TransmitMessageRequestMessage;
import com.onestep.os.messagebusservice.model.message.response.MessageBusHeartbeatResponseMessage;
import com.onestep.os.messagebusservice.model.message.response.TransmitMessageResponseMessage;
import org.springframework.web.client.RestClientException;

public class OsMessageBusServiceApi  extends RestfulClient {
  private String url;
  private final static String TRANSMIT_MESSAGE_URL = "/message/transmitMessage";

  private final static String HEART_BEAT_MESSAGE_URL = "/message/messageBusHeartbeat";
  public OsMessageBusServiceApi(final String url){
    this.url = url;
  }

  private String getUrl(String api) {
    if (!url.endsWith("/")) {
      url += "/";
    }
    url += api;
    return url;
  }

  public TransmitMessageResponseMessage transmitMessage(TransmitMessageRequestMessage request)
      throws RestClientException {
    String path = getUrl(TRANSMIT_MESSAGE_URL);
    return post(path, request, TransmitMessageResponseMessage.class);
  }

  public MessageBusHeartbeatResponseMessage messageBusHeartbeat(
      MessageBusHeartbeatRequestMessage request) {
    String path = getUrl(HEART_BEAT_MESSAGE_URL);
    return post(path, request, MessageBusHeartbeatResponseMessage.class);
  }
}
