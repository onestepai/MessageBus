package com.onestep.os.messagebusservice.config;

import io.swagger.client.ApiClient;
import io.swagger.client.api.OsmessagebusroutingApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {
  @Autowired @Lazy private RestTemplate restTemplate;

  @Value("${client.os_message_bus_routing_service_address}")
  private String osMessageBusRoutingServiceAddress;

  @Bean
  public RestTemplate restTemplate() {
    int timeout = 60000 * 6;
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setReadTimeout(timeout);
    factory.setConnectTimeout(timeout);
    factory.setConnectionRequestTimeout(timeout);
    return new RestTemplate(factory);
  }

  @Bean
  public OsmessagebusroutingApi osmessagebusroutingApi() {
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(osMessageBusRoutingServiceAddress);
    return new OsmessagebusroutingApi(apiClient);
  }
}
