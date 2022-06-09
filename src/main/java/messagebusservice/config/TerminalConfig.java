package com.onestep.os.messagebusservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TerminalConfig {


  @Value("${terminal.customer_id}")
  private String customerId;

  @Value("${terminal.run_mode}")
  private String runMode;

  @Value("${terminal.server.url}")
  private String serverUrl;

  @Value("${terminal.client.port}")
  private Integer runClientPort;


  @Value("${terminal.terminal_id}")
  private String terminalId;

  @Value("${terminal.server.port}")
  private Integer runServerPort;

  @Value("${terminal.secure_key}")
  private String secureKey;

  @Value("${terminal.consumer.consumer_type}")
  private String consumerType;

  @Value("${terminal.consumer.exchange_name}")
  private String exchangeName;


  @Value("${terminal.consumer.address")
  private String address;

  @Value("${terminal.consumer.port")
  private String port;

  @Value("${terminal.consumer.username")
  private String username;

  @Value("${terminal.consumer.password")
  private String password;

  @Value("${terminal.consumer.concurrency")
  private String concurrency;

  @Value("${terminal.consumer.acknowledgeMode")
  private String acknowledgeMode;

  @Value("${terminal.consumer.maxConcurrency")
  private String maxConcurrency;

  @Value("${terminal.consumer.prefetch")
  private String prefetch;
}
