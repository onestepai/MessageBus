package com.onestep.os.messagebusservice.service;

import com.onestep.os.error.OsError;
import com.onestep.os.messagebusservice.config.TerminalConfig;
import com.onestep.os.messagebusservice.config.OsMessageBusServiceApi;
import com.onestep.os.messagebusservice.model.message.consumer.KafkaConsumerConfigMessage;
import com.onestep.os.messagebusservice.model.message.consumer.RabbitMQConsumerConfigMessage;
import com.onestep.os.messagebusservice.model.message.event.SocketMessageEvent;
import com.onestep.os.messagebusservice.model.message.mongodb.MessageRecord;
import com.onestep.os.messagebusservice.model.message.request.*;
import com.onestep.os.messagebusservice.model.message.response.*;
import com.onestep.os.messagebusservice.netty.client.NettyClient;
import com.onestep.os.messagebusservice.netty.server.NettyServer;
import com.onestep.os.messagebusservice.producer.kafka.KafkaSender;
import com.onestep.os.messagebusservice.producer.rabbit.RabbitSender;
import com.onestep.os.messagebusservice.util.LoggerHelper;
import com.onestep.os.messagebusservice.util.SecurityUtils;
import com.onestep.os.utils.JsonUtils;
import com.onestep.os.utils.LoggerUtils;
import io.swagger.client.api.OsmessagebusroutingApi;
import io.swagger.client.model.*;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class MessageBusServiceImpl implements MessageBusService {

  private final static String KAFKA_CONSUMER_TYPE = "kafka";

  private final static String RABBIT_MQ_CONSUMER_TYPE = "rabbitMQ";

  private final static String RUN_MODE_SERVER = "server";

  private final static String RUN_MODE_CLIENT = "client";

  @Autowired private RabbitSender rabbitSender;

  @Autowired private KafkaSender kafkaSender;

  @Autowired private NettyServer nettyServer;

  @Autowired private NettyClient nettyClient;

  @Autowired private TerminalConfig terminalConfig;

  @Autowired @Lazy private RestTemplate restTemplate;

  @Autowired private OsmessagebusroutingApi osmessagebusroutingApi;


  @PostConstruct
  public void startWebsocket() {
    if (RUN_MODE_SERVER.equals(terminalConfig.getRunMode())) {
      nettyServer.start(terminalConfig.getRunServerPort());
      // if run server first,return method
      UpdateTerminalStatusRequestMessage request = new UpdateTerminalStatusRequestMessage();
      request.setRequestId(UUID.randomUUID().toString());
      request.setTimestamp(new Date().getTime());
      request.setMbCustomerId(terminalConfig.getCustomerId());
      request.setTerminalId(terminalConfig.getTerminalId());
      request.setStatus(0);
      UpdateTerminalStatusResponseMessage response =
          osmessagebusroutingApi.updateTerminalStatus(request);
      LoggerHelper.error(
          OsError.OS_SUCCESS,
          String.format("update server status in routing service: %s", response.getErrorCode()));
    }
    if (RUN_MODE_CLIENT.equals(terminalConfig.getRunMode())) {
      final GetAvailableHostsRequestMessage getAvailableHostsRequestMessage =
          new GetAvailableHostsRequestMessage();
      getAvailableHostsRequestMessage.setRequestId(UUID.randomUUID().toString());
      getAvailableHostsRequestMessage.setTimestamp(new Date().getTime());
      getAvailableHostsRequestMessage.setCustomerId(terminalConfig.getCustomerId());
      final GetAvailableHostsResponseMessage getAvailableHostsResponseMessage =
          osmessagebusroutingApi.getAvailableHosts(getAvailableHostsRequestMessage);
      if (getAvailableHostsResponseMessage.getErrorCode() != OsError.OS_SUCCESS.getErrorCode()) {
        LoggerUtils.error(
            OsError.OS_FAILURE_DEPENDENCY, getAvailableHostsResponseMessage.getErrorMessage());
      } else {
        if (CollectionUtils.isEmpty(getAvailableHostsResponseMessage.getHosts())) {
          LoggerUtils.error(
              OsError.OS_FAILURE_DOES_NOT_EXIST_DATA,
              OsError.OS_FAILURE_DOES_NOT_EXIST_DATA.getError());
        } else {
          for (Map.Entry<String, String> host :
              getAvailableHostsResponseMessage.getHosts().entrySet()) {

            LoggerHelper.error(
                OsError.OS_SUCCESS, String.format("Successfully get host: %s", host.getValue()));
            nettyClient.start(
                terminalConfig.getTerminalId(), host.getValue(), terminalConfig.getRunClientPort());
            final AddTerminalToHostRequestMessage addTerminalToHostRequestMessage =
                new AddTerminalToHostRequestMessage();
            addTerminalToHostRequestMessage.setHostId(host.getKey());
            addTerminalToHostRequestMessage.setTerminalId(terminalConfig.getTerminalId());
            addTerminalToHostRequestMessage.setRequestId(UUID.randomUUID().toString());
            addTerminalToHostRequestMessage.setTimestamp(new Date().getTime());
            osmessagebusroutingApi.addTerminalToHost(addTerminalToHostRequestMessage);
            break;
          }
        }
      }
    }
  }

  @EventListener
  public void onSocketMessageEvent(SocketMessageEvent messageEvent) {
    LoggerUtils.info("onSocketMessageEvent: " + messageEvent.getMessage());
    final val request =
        JsonUtils.readJson(messageEvent.getMessage(), TransmitMessageRequestMessage.class);
    if (request != null) {
      LoggerUtils.info("onSocketMessageEvent request is NOT null!");
      transmitMessage(request);
    } else {
      LoggerUtils.info("onSocketMessageEvent request is null!");
    }
  }

  private SendMessageResponseMessage sendMessage(
      final MessageBusTerminal messageBusTerminal,
      final SendMessageRequestMessage request,
      final List<MessageBusTerminal> messageBusTerminals,
      final String encryptKey,
      final String tempKey) {
    try {
      if (messageBusTerminal.getErrorCode() != OsError.OS_SUCCESS.getErrorCode()) {
        return SendMessageResponseMessage.builder()
            .requestId(request.getRequestId())
            .timestamp(System.currentTimeMillis())
            .errorCode(messageBusTerminal.getErrorCode())
            .errorMessage(messageBusTerminal.getErrorMessage())
            .build();
      }
      LoggerUtils.info("sendMessage" + messageBusTerminal.getTerminalId());
      final TransmitMessageRequestMessage transmitMessageRequestMessage =
          new TransmitMessageRequestMessage();
      String encryptedContent = request.getMessageContent();
      if (!StringUtils.isEmpty(encryptKey)) {
        LoggerUtils.info("in encrypt mode.");
        encryptedContent = SecurityUtils.encrypt(request.getMessageContent(), encryptKey);
      }
      transmitMessageRequestMessage.setTempKey(tempKey);
      transmitMessageRequestMessage.setMessageContent(encryptedContent);
      transmitMessageRequestMessage.setMessageId(request.getRequestId());
      transmitMessageRequestMessage.setTopicId(request.getTopicId());
      transmitMessageRequestMessage.setTimestamp(new Date().getTime());
      transmitMessageRequestMessage.setMessageRoute(messageBusTerminals);
      if (messageBusTerminal.getTerminalType() == 0) {
        if (messageBusTerminal.getTerminalUrl() != null
            && !messageBusTerminal.getTerminalUrl().equals(terminalConfig.getServerUrl())) {

          final OsMessageBusServiceApi osMessageBusServiceApi = new OsMessageBusServiceApi(messageBusTerminal.getTerminalUrl());
          osMessageBusServiceApi.transmitMessage(transmitMessageRequestMessage);
          return SendMessageResponseMessage.builder()
              .requestId(request.getRequestId())
              .timestamp(System.currentTimeMillis())
              .errorCode(OsError.OS_SUCCESS.getErrorCode())
              .errorMessage(OsError.OS_SUCCESS.getError())
              .build();
        }
      } else {
        if (!ObjectUtils.isEmpty(messageBusTerminal.getTerminalId())) {
          if (sendMessageToWebsocket(
              messageBusTerminal.getTerminalId(), transmitMessageRequestMessage)) {
            return SendMessageResponseMessage.builder()
                .requestId(request.getRequestId())
                .timestamp(System.currentTimeMillis())
                .errorCode(OsError.OS_SUCCESS.getErrorCode())
                .errorMessage(OsError.OS_SUCCESS.getError())
                .build();
          } else {
            return SendMessageResponseMessage.builder()
                .requestId(request.getRequestId())
                .timestamp(System.currentTimeMillis())
                .errorCode(OsError.OS_FAILURE_DOES_NOT_EXIST_DATA.getErrorCode())
                .errorMessage(OsError.OS_FAILURE_DOES_NOT_EXIST_DATA.getError())
                .build();
          }
        }
      }

      Long timestamp = System.currentTimeMillis();

      MessageRecord messageRecord = new MessageRecord();
      messageRecord.setTopicKey(request.getTopicId());
      messageRecord.setClientId(request.getCustomerId());
      messageRecord.setMessageContent(request.getMessageContent());
      messageRecord.setTryCount(0);
      messageRecord.setStatus(1);
      messageRecord.setMessageId(request.getRequestId());
      messageRecord.setCreateTime(timestamp);
      messageRecord.setUpdateTime(timestamp);

      if (rabbitSender.sendMessage(messageRecord)) {
        SendMessageResponseMessage responseMessage =
            SendMessageResponseMessage.builder()
                .requestId(request.getRequestId())
                .timestamp(System.currentTimeMillis())
                .errorCode(OsError.OS_SUCCESS.getErrorCode())
                .errorMessage(OsError.OS_SUCCESS.getError())
                .build();

        return responseMessage;
      } else {
        SendMessageResponseMessage responseMessage =
            SendMessageResponseMessage.builder()
                .requestId(request.getRequestId())
                .timestamp(System.currentTimeMillis())
                .errorCode(OsError.OS_FAILURE_INTERNAL.getErrorCode())
                .errorMessage(OsError.OS_FAILURE_INTERNAL.getError())
                .build();

        return responseMessage;
      }
    } catch (Exception e) {
      SendMessageResponseMessage responseMessage =
          SendMessageResponseMessage.builder()
              .requestId(request.getRequestId())
              .timestamp(System.currentTimeMillis())
              .errorCode(OsError.OS_FAILURE_INTERNAL.getErrorCode())
              .errorMessage(OsError.OS_FAILURE_INTERNAL.getError())
              .build();

      return responseMessage;
    }
  }

  @Override
  public SendMessageResponseMessage sendMessage(SendMessageRequestMessage request) {
    final GetGroupRoutingRequestMessage getGroupRoutingRequestMessage =
        new GetGroupRoutingRequestMessage();
    getGroupRoutingRequestMessage.setRequestId(request.getRequestId());
    getGroupRoutingRequestMessage.setTopicId(request.getTopicId());
    getGroupRoutingRequestMessage.setCustomerId(request.getCustomerId());
    getGroupRoutingRequestMessage.setSecureKey(request.getSecureKey());
    getGroupRoutingRequestMessage.setTimestamp(request.getTimestamp());
    getGroupRoutingRequestMessage.setSourceTerminal(terminalConfig.getTerminalId());
    final GetGroupRoutingResponseMessage getGroupRoutingResponseMessage =
        osmessagebusroutingApi.getGroupRoutes(getGroupRoutingRequestMessage);
    Long err = getGroupRoutingResponseMessage.getErrorCode();
    if (OsError.OS_SUCCESS.getErrorCode() != err) {
      LoggerUtils.error(
              OsError.OS_FAILURE_DEPENDENCY, getGroupRoutingResponseMessage.getErrorMessage());
      return SendMessageResponseMessage.builder()
              .errorCode(err)
              .errorMessage(getGroupRoutingResponseMessage.getErrorMessage())
              .requestId(request.getRequestId())
              .timestamp(new Date().getTime())
              .build();
    }
    final List<GroupMessageBusRoute> groupMessageBusRoutes =
        getGroupRoutingResponseMessage.getGroupMessageBusRoutes();
    if (CollectionUtils.isEmpty(groupMessageBusRoutes)) {
      LoggerUtils.error(OsError.OS_FAILURE_DOES_NOT_EXIST_DATA, "no any route");
      return SendMessageResponseMessage.builder()
              .errorCode(OsError.OS_FAILURE_DOES_NOT_EXIST_DATA.getErrorCode())
              .errorMessage("no any route")
              .requestId(request.getRequestId())
              .timestamp(new Date().getTime())
              .build();
    }
    for (final GroupMessageBusRoute groupMessageBusRoute : groupMessageBusRoutes) {
      for (final MessageBusRoute messageBusRoute : groupMessageBusRoute.getMessageBusRoutes()) {
        if (OsError.OS_SUCCESS.getErrorCode() == messageBusRoute.getErrorCode()) {
          final List<MessageBusTerminal> messageBusTerminals =
              messageBusRoute.getMessageBusTerminals();
          LoggerUtils.info(messageBusTerminals.get(0).getTerminalId());
          if (messageBusTerminals.size() > 0) {
            MessageBusTerminal messageBusTerminal = messageBusTerminals.get(0);

            if (RUN_MODE_SERVER.equals(terminalConfig.getRunMode())
                && terminalConfig.getServerUrl().equals(messageBusTerminal.getTerminalUrl())
                && messageBusTerminals.size() > 1) {
              messageBusTerminal = messageBusTerminals.get(1);
            }
            List<MessageBusTerminal> nextMessageBusTerminals =
                messageBusTerminals.subList(1, messageBusTerminals.size());
            sendMessage(
                messageBusTerminal,
                request,
                nextMessageBusTerminals,
                getGroupRoutingResponseMessage.getEncryptedKey(),
                getGroupRoutingResponseMessage.getTempKey());
          }
        }
      }
    }
    return SendMessageResponseMessage.builder()
        .errorCode(err)
        .errorMessage(OsError.OS_SUCCESS.getError())
        .requestId(request.getRequestId())
        .timestamp(new Date().getTime())
        .build();
  }

  private boolean sendMessageToWebsocket(String clientHost, TransmitMessageRequestMessage request) {
    boolean sendSuccess;
    if (RUN_MODE_SERVER.equals(terminalConfig.getRunMode())) {
      sendSuccess = nettyServer.sendMessage(clientHost, request);
    } else {
      sendSuccess = nettyClient.sendMessage(clientHost, request);
    }
    return sendSuccess;
  }

  @Override
  public GetConsumerConfigResponseMessage getConsumerConfig(
      GetConsumerConfigRequestMessage request) {
    try {
      if (KAFKA_CONSUMER_TYPE.equals(terminalConfig.getConsumerType())){
        KafkaConsumerConfigMessage consumerConfig = new KafkaConsumerConfigMessage();
        consumerConfig.setExchangeName(terminalConfig.getExchangeName());
        consumerConfig.setAcknowledgeMode(terminalConfig.getAcknowledgeMode());
        consumerConfig.setAddress(terminalConfig.getAddress());
        consumerConfig.setConcurrency(terminalConfig.getConcurrency());
        consumerConfig.setMaxConcurrency(terminalConfig.getMaxConcurrency());
        consumerConfig.setPassword(terminalConfig.getPassword());
        consumerConfig.setPort(terminalConfig.getPort());
        consumerConfig.setUsername(terminalConfig.getUsername());
        return GetConsumerConfigResponseMessage.builder()
                .requestId(request.getRequestId())
                .timestamp(System.currentTimeMillis())
                .consumerConfigMessage(consumerConfig)
                .errorCode(OsError.OS_SUCCESS.getErrorCode())
                .errorMessage(OsError.OS_SUCCESS.getError())
                .build();
      } else if(RABBIT_MQ_CONSUMER_TYPE.equals(
              terminalConfig.getConsumerType())){
        RabbitMQConsumerConfigMessage consumerConfig = new RabbitMQConsumerConfigMessage();
        consumerConfig.setExchangeName(terminalConfig.getExchangeName());
        consumerConfig.setAcknowledgeMode(terminalConfig.getAcknowledgeMode());
        consumerConfig.setAddress(terminalConfig.getAddress());
        consumerConfig.setConcurrency(terminalConfig.getConcurrency());
        consumerConfig.setMaxConcurrency(terminalConfig.getMaxConcurrency());
        consumerConfig.setPassword(terminalConfig.getPassword());
        consumerConfig.setPort(terminalConfig.getPort());
        consumerConfig.setUsername(terminalConfig.getUsername());
        consumerConfig.setPrefetch(terminalConfig.getPrefetch());

        return GetConsumerConfigResponseMessage.builder()
                .requestId(request.getRequestId())
                .timestamp(System.currentTimeMillis())
                .consumerConfigMessage(consumerConfig)
                .errorCode(OsError.OS_SUCCESS.getErrorCode())
                .errorMessage(OsError.OS_SUCCESS.getError())
                .build();
      } else {
        GetConsumerConfigResponseMessage responseMessage =
            GetConsumerConfigResponseMessage.builder()
                .requestId(request.getRequestId())
                .timestamp(System.currentTimeMillis())
                .errorCode(OsError.OS_FAILURE_INTERNAL.getErrorCode())
                .errorMessage("invalid consumer config")
                .build();

        return responseMessage;
      }
    } catch (Exception e) {
      GetConsumerConfigResponseMessage responseMessage =
          GetConsumerConfigResponseMessage.builder()
              .requestId(request.getRequestId())
              .timestamp(System.currentTimeMillis())
              .errorCode(OsError.OS_FAILURE_INTERNAL.getErrorCode())
              .errorMessage(OsError.OS_FAILURE_INTERNAL.getError())
              .build();
      return responseMessage;
    }
  }

  @Override
  public TransmitMessageResponseMessage transmitMessage(TransmitMessageRequestMessage request) {
    final List<MessageBusTerminal> messageBusTerminals = request.getMessageRoute();
    boolean partialFailed = false;
    if (messageBusTerminals.size() == 0) {
      return TransmitMessageResponseMessage.builder()
          .requestId(request.getRequestId())
          .timestamp(System.currentTimeMillis())
          .errorCode(OsError.OS_FAILURE_DOES_NOT_EXIST_DATA.getErrorCode())
          .errorMessage(OsError.OS_FAILURE_DOES_NOT_EXIST_DATA.getError())
          .build();
    }
    final MessageBusTerminal messageBusTerminal = messageBusTerminals.get(0);
    try {
      if (messageBusTerminal.getTerminalType() == 0) {
        if (messageBusTerminal.getTerminalUrl() != null
            && !messageBusTerminal.getTerminalUrl().equals(terminalConfig.getServerUrl())) {
          final TransmitMessageRequestMessage transmitMessageRequestMessage =
              new TransmitMessageRequestMessage();
          transmitMessageRequestMessage.setMessageContent(request.getMessageContent());
          transmitMessageRequestMessage.setMessageId(request.getRequestId());

          transmitMessageRequestMessage.setTopicId(request.getTopicId());
          transmitMessageRequestMessage.setTimestamp(new Date().getTime());
          transmitMessageRequestMessage.setMessageRoute(messageBusTerminals);

          final OsMessageBusServiceApi osMessageBusServiceApi = new OsMessageBusServiceApi(messageBusTerminal.getTerminalUrl());
          osMessageBusServiceApi.transmitMessage(transmitMessageRequestMessage);
        }
      } else if (!ObjectUtils.isEmpty(messageBusTerminal.getTerminalId())
          && !messageBusTerminal.getTerminalId().equals(terminalConfig.getTerminalId())) {
        request.setMessageRoute(
            request.getMessageRoute().subList(1, request.getMessageRoute().size()));
          partialFailed = sendMessageToWebsocket(messageBusTerminal.getTerminalId(), request);
      }
      Long timestamp = System.currentTimeMillis();
      final String secureKey = terminalConfig.getSecureKey();
      final GetTopicAccessRequestMessage getTopicAccessRequestMessage =
          new GetTopicAccessRequestMessage();
      getTopicAccessRequestMessage.setRequestId(request.getRequestId());
      getTopicAccessRequestMessage.setTempKey(request.getTempKey());
      getTopicAccessRequestMessage.setTerminalSecureKey(secureKey);
      final GetTopicAccessResponseMessage getTopicAccessResponseMessage =
          osmessagebusroutingApi.getTopicAccess(getTopicAccessRequestMessage);
      String content = request.getMessageContent();
      if (OsError.OS_SUCCESS.getErrorCode() == getTopicAccessResponseMessage.getErrorCode()
          && !StringUtils.isEmpty(getTopicAccessResponseMessage.getDecodeKey())) {
        content = SecurityUtils.decrypt(content, getTopicAccessResponseMessage.getDecodeKey());
      }
      MessageRecord messageRecord = new MessageRecord();
      messageRecord.setTopicKey(request.getTopicId());
      messageRecord.setClientId(request.getCustomerId());
      messageRecord.setMessageContent(content);
      messageRecord.setTryCount(0);
      messageRecord.setStatus(1);
      messageRecord.setMessageId(request.getMessageId());
      messageRecord.setCreateTime(timestamp);
      messageRecord.setUpdateTime(timestamp);

      if (KAFKA_CONSUMER_TYPE.equals(terminalConfig.getConsumerType())) {
        partialFailed = !kafkaSender.sendMessage(messageRecord);
      } else if (RABBIT_MQ_CONSUMER_TYPE.equals(
              terminalConfig.getConsumerType())) {
        partialFailed = !rabbitSender.sendMessage(messageRecord);
      } else {
        LoggerUtils.error(OsError.OS_FAILURE_INVALID_ARG,
                "Only support kafka or kafka. " +
                "Please check your consumer_type under terminal");
      }
      LoggerUtils.info(String.format("decrypted content: %s", content));
    } catch (Exception e) {
      partialFailed = true;
    }

    if (partialFailed) {
      return TransmitMessageResponseMessage.builder()
          .requestId(request.getRequestId())
          .timestamp(System.currentTimeMillis())
          .errorCode(OsError.OS_FAILURE_ERROR_IN_PARTIAL.getErrorCode())
          .errorMessage(OsError.OS_FAILURE_ERROR_IN_PARTIAL.getError())
          .build();
    } else {
      return TransmitMessageResponseMessage.builder()
          .requestId(request.getRequestId())
          .timestamp(System.currentTimeMillis())
          .errorCode(OsError.OS_SUCCESS.getErrorCode())
          .errorMessage(OsError.OS_SUCCESS.getError())
          .build();
    }
  }

  @Override
  public MessageBusHeartbeatResponseMessage messageBusHeartBeat(
      MessageBusHeartbeatRequestMessage request) {
    return MessageBusHeartbeatResponseMessage.builder()
        .errorCode(OsError.OS_SUCCESS.getErrorCode())
        .errorMessage(OsError.OS_SUCCESS.getError())
        .requestId(request.getRequestId())
        .timestamp(new Date().getTime())
        .build();
  }

  @Override
  public RoutingHeartbeatResponseMessage routingHeartBeat(RoutingHeartbeatRequestMessage request) {
    final List<RouteResult> routeResults = new ArrayList<>();
    OsError osError = OsError.OS_SUCCESS;
    for (Map.Entry<String, String> entry : request.getTargets().entrySet()) {
      final MessageBusHeartbeatRequestMessage messageBusHeartbeatRequestMessage =
          new MessageBusHeartbeatRequestMessage();

      final OsMessageBusServiceApi osMessageBusServiceApi = new OsMessageBusServiceApi(entry.getValue());
      final Long currentTimestamp = new Date().getTime();
      final MessageBusHeartbeatResponseMessage messageBusHeartbeatResponseMessage =
          osMessageBusServiceApi.messageBusHeartbeat(messageBusHeartbeatRequestMessage);
      final RouteResult result =
          RouteResult.builder()
              .errorCode(messageBusHeartbeatResponseMessage.getErrorCode())
              .errorMessage(messageBusHeartbeatResponseMessage.getErrorMessage())
              .latency(messageBusHeartbeatResponseMessage.getTimestamp() - currentTimestamp)
              .target(entry.getKey())
              .timestamp(new Date().getTime())
              .build();
      routeResults.add(result);
    }
    return RoutingHeartbeatResponseMessage.builder()
        .errorCode(osError.getErrorCode())
        .errorMessage(osError.getError())
        .requestId(request.getRequestId())
        .routeResults(routeResults)
        .timestamp(new Date().getTime())
        .build();
  }
}
