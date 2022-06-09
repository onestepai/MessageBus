package com.onestep.os.messagebusservice.model.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onestep.os.model.BaseRequest;
import io.swagger.client.model.MessageBusTerminal;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TransmitMessageRequestMessage extends BaseRequest {
  @JsonProperty("messageContent")
  private String messageContent;

  @JsonProperty("messageId")
  private String messageId;

  @JsonProperty("customerId")
  private String customerId;

  @JsonProperty("messageRoute")
  private List<MessageBusTerminal> messageRoute;

  @JsonProperty("topicId")
  private String topicId;

  @JsonProperty("tempKey")
  private String tempKey;

  @JsonProperty("timestamp")
  private Long timestamp;
}
