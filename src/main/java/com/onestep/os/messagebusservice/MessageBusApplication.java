package com.onestep.os.messagebusservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.onestep")
@ComponentScan("io.swagger")
public class MessageBusApplication {

  public static void main(String[] args) {

    SpringApplication.run(MessageBusApplication.class, args);
  }
}
