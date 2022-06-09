package com.onestep.os.messagebusservice.controller;

import com.onestep.os.messagebusservice.model.message.request.*;
import com.onestep.os.messagebusservice.model.message.response.*;
import com.onestep.os.messagebusservice.service.MessageBusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@Api(value = "osmessagebus")
public class MessageBusController {
  @Autowired private MessageBusService messageBusService;

  @PostMapping("/sendMessage")
  @ApiOperation(value = "Send Message", notes = "Send Message")
  @ApiImplicitParam(
      name = "request",
      value = "send message request",
      required = true,
      dataType = "SendMessageRequestMessage")
  public SendMessageResponseMessage sendMessage(
      @RequestBody @Validated SendMessageRequestMessage request) {
    return messageBusService.sendMessage(request);
  }

  @PostMapping("/transmitMessage")
  @ApiOperation(value = "Transmit Message", notes = "Transmit Message")
  @ApiImplicitParam(
      name = "request",
      value = "Transmit message request",
      required = true,
      dataType = "TransmitMessageRequestMessage")
  public TransmitMessageResponseMessage transmitMessage(
      @RequestBody @Validated TransmitMessageRequestMessage request) {
    return messageBusService.transmitMessage(request);
  }

  @PostMapping("/getConsumerConfig")
  @ApiOperation(value = "Get Consumer Config", notes = "Get Consumer Config Message")
  @ApiImplicitParam(
      name = "request",
      value = "get consumer config request",
      required = true,
      dataType = "GetConsumerConfigRequestMessage")
  public GetConsumerConfigResponseMessage getConsumerConfig(
      @RequestBody @Validated GetConsumerConfigRequestMessage request) {
    return messageBusService.getConsumerConfig(request);
  }

  @PostMapping("/messageBusHeartbeat")
  @ApiOperation(value = "messageBusHeartbeat", notes = "messageBusHeartbeat")
  @ApiImplicitParam(
      name = "request",
      value = "messageBusHeartbeat",
      required = true,
      dataType = "MessageBusHeartbeatRequestMessage")
  public MessageBusHeartbeatResponseMessage messageBusHeartBeat(
      @RequestBody @Validated MessageBusHeartbeatRequestMessage request) {
    return messageBusService.messageBusHeartBeat(request);
  }

  @PostMapping("/routingHeartbeat")
  @ApiOperation(value = "routingHeartbeat", notes = "routingHeartbeat")
  @ApiImplicitParam(
      name = "request",
      value = "routingHeartbeat",
      required = true,
      dataType = "RoutingHeartbeatRequestMessage")
  public RoutingHeartbeatResponseMessage routingHeartBeat(
      @RequestBody @Validated RoutingHeartbeatRequestMessage request) {
    return messageBusService.routingHeartBeat(request);
  }
}
