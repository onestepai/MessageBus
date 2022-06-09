package com.onestep.os.messagebusservice.netty.server;

import com.onestep.os.messagebusservice.netty.ChannelHandlerPool;
import io.netty.channel.Channel;


public class ServerChannelHandlerPool {

    public static final ChannelHandlerPool channelHandlerPool = new ChannelHandlerPool();

    public static void put(String key, Channel channel) {
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
        return channelHandlerPool.sendMessage(host, message);
    }
}

