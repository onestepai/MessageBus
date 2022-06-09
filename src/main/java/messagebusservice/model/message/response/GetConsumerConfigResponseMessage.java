package com.onestep.os.messagebusservice.model.message.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onestep.os.messagebusservice.model.message.consumer.ConsumerConfigMessage;
import lombok.Builder;

@Builder
public class GetConsumerConfigResponseMessage {
  @JsonProperty("consumerConfig")
  private ConsumerConfigMessage consumerConfigMessage;

  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("timestamp")
  private Long timestamp;

  @JsonProperty("errorMessage")
  private String errorMessage;

  @JsonProperty("errorCode")
  private Long errorCode;
}
