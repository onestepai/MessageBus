package com.onestep.os.messagebusservice.model.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onestep.os.model.BaseRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendMessageRequestMessage extends BaseRequest {
  @JsonProperty("messageContent")
  private String messageContent;

  @JsonProperty("topicId")
  private String topicId;

  @JsonProperty("customerId")
  private String customerId;

  @JsonProperty("secureKey")
  private String secureKey;

  @JsonProperty("timestamp")
  private Long timestamp;
}
