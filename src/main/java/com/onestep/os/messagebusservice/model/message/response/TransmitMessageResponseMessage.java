package com.onestep.os.messagebusservice.model.message.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class TransmitMessageResponseMessage {
  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("timestamp")
  private Long timestamp;

  @JsonProperty("errorMessage")
  private String errorMessage;

  @JsonProperty("errorCode")
  private Long errorCode;
}
