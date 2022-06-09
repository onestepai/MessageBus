package com.onestep.os.messagebusservice.model.message.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class SocketMessageEvent extends ApplicationEvent {
  private String message;

  public SocketMessageEvent(Object source, String message) {
    super(source);
    this.message = message;
  }
}
