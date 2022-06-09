package com.onestep.os.messagebusservice.netty;

import com.onestep.os.utils.LoggerUtils;
import io.netty.channel.Channel;
import io.netty.util.internal.PlatformDependent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class ChannelHandlerPool {

  public ConcurrentMap<String, Channel> channelGroup = PlatformDependent.newConcurrentHashMap();

  public void clear() {
    channelGroup.clear();
  }

  public void put(String key, Channel channel) {
    channelGroup.put(key, channel);
  }

  public boolean remove(Channel channel) {
    String key = null;
    boolean b = channelGroup.containsValue(channel);
    if (b) {
      Set<Map.Entry<String, Channel>> entries = channelGroup.entrySet();
      for (Map.Entry<String, Channel> entry : entries) {
        Channel value = entry.getValue();
        if (value.equals(channel)) {
          key = entry.getKey();
          break;
        }
      }
    } else {
      return true;
    }
    return remove(key);
  }

  public boolean remove(String key) {
    Channel remove = channelGroup.remove(key);
    if (remove != null) {
      return channelGroup.containsValue(remove);
    } else {
      return true;
    }
  }

  public void sendMessage(Object message) {
    channelGroup.forEach((s, channel) -> channel.writeAndFlush(message));
  }

  public boolean sendMessage(String host, Object message) {
    Channel channel = channelGroup.get(host);
    if (channel != null) {
      LoggerUtils.info("found channel for host : " + host);
      channel.writeAndFlush(message);
      return true;
    } else {
      LoggerUtils.info("not found channel for host : " + host);
      return false;
    }
  }
}
