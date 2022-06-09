package com.onestep.os.messagebusservice.netty.client;

import com.onestep.os.messagebusservice.netty.ChannelHandlerPool;
import com.onestep.os.utils.LoggerUtils;
import io.netty.channel.Channel;

public class ClientChannelHandlerPool {

  public static final ChannelHandlerPool channelHandlerPool = new ChannelHandlerPool();

  public static void put(String key, Channel channel) {
    // only one server channel
    channelHandlerPool.clear();
    channelHandlerPool.put(key, channel);
  }

  public static boolean remove(Channel channel) {
    return channelHandlerPool.remove(channel);
  }

  public static boolean remove(String key) {
    return channelHandlerPool.remove(key);
  }

  public static void sendMessage(Object message) {
    channelHandlerPool.sendMessage(message);
  }

  public static boolean sendMessage(String host, Object message) {
    if (channelHandlerPool.channelGroup.size() == 1) {
      channelHandlerPool.sendMessage(message);
      return true;
    } else {
      LoggerUtils.info("sendMessage error channelHandlerPool size is not 1");
      return false;
    }
  }
}
