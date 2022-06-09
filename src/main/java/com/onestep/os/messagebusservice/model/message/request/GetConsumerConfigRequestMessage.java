package com.onestep.os.messagebusservice.model.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onestep.os.model.BaseRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetConsumerConfigRequestMessage extends BaseRequest {
  @JsonProperty("topicKey")
  private String topicKey;

  @JsonProperty("clientId")
  private String clientId;

  @JsonProperty("timestamp")
  private Long timestamp;
}
