package com.onestep.os.messagebusservice.model.message.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRecord {
  private String messageId;
  private String secretKey;
  private String topicKey;
  private String clientId;
  private String messageContent;
  private Integer tryCount;
  private Integer status; // 1: tosend, 2: sended, 3: successful
  private Long nextRetry;
  private Long createTime;
  private Long updateTime;
}
