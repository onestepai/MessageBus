package com.onestep.os.messagebusservice.model.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onestep.os.model.BaseRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class RoutingHeartbeatRequestMessage extends BaseRequest {

  @JsonProperty("targets")
  private Map<String, String> targets;

  @JsonProperty("secureKey")
  private String secureKey;
}
