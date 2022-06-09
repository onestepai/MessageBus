package com.onestep.os.messagebusservice.model.message.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class RabbitMQConsumerConfigMessage extends ConsumerConfigMessage {
  @JsonProperty("exchangeName")
  private String exchangeName;

  @JsonProperty("address")
  private String address;

  @JsonProperty("port")
  private String port;

  @JsonProperty("username")
  private String username;

  @JsonProperty("password")
  private String password;

  @JsonProperty("concurrency")
  private String concurrency;

  @JsonProperty("acknowledgeMode")
  private String acknowledgeMode;

  @JsonProperty("maxConcurrency")
  private String maxConcurrency;

  @JsonProperty("prefetch")
  private String prefetch;
}
