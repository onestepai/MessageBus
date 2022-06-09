package com.onestep.os.messagebusservice.model.message.consumer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = RabbitMQConsumerConfigMessage.class, name = "1"),
  @JsonSubTypes.Type(value = KafkaConsumerConfigMessage.class, name = "2")
})
public class ConsumerConfigMessage {}
