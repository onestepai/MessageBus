package com.onestep.os.messagebusservice.model.message.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RoutingHeartbeatResponseMessage {
  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("timestamp")
  private Long timestamp;

  @JsonProperty("routesResult")
  private List<RouteResult> routeResults;

  @JsonProperty("errorMessage")
  private String errorMessage;

  @JsonProperty("errorCode")
  private Long errorCode;
}
