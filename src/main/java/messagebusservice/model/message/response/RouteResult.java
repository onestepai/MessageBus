package com.onestep.os.messagebusservice.model.message.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class RouteResult {
  @JsonProperty("target")
  private String target;

  @JsonProperty("timestamp")
  private Long timestamp;

  @JsonProperty("latency")
  private Long latency;

  @JsonProperty("errorMessage")
  private String errorMessage;

  @JsonProperty("errorCode")
  private Long errorCode;
}
