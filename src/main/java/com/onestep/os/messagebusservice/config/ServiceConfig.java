package com.onestep.os.messagebusservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ServiceConfig {
  @Value("${debug.log_on}")
  private Boolean debugLogOn;
}
