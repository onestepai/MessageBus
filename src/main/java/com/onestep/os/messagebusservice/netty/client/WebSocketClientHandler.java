package com.onestep.os.messagebusservice.netty.client;

import com.onestep.os.messagebusservice.model.message.event.SocketMessageEvent;
import com.onestep.os.messagebusservice.util.ApplicationContextProvider;
import com.onestep.os.utils.LoggerUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import lombok.val;

import java.util.function.Consumer;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

  private final WebSocketClientHandshaker webSocketClientHandshaker;

  private ChannelPromise handshakeFuture;

  private final Consumer<ChannelHandlerContext> inactiveCallback;

  public WebSocketClientHandler(
      WebSocketClientHandshaker webSocketClientHandshaker,
      Consumer<ChannelHandlerContext> inactiveCallback) {
    this.webSocketClientHandshaker = webSocketClientHandshaker;
    this.inactiveCallback = inactiveCallback;
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    handshakeFuture = ctx.newPromise();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause)
      throws Exception {
    cause.printStackTrace();
    LoggerUtils.info("exception: " + cause.getMessage());
    ClientChannelHandlerPool.remove(channelHandlerContext.channel());
    channelHandlerContext.close();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    LoggerUtils.info("client:channelActive " + channel.remoteAddress());

    webSocketClientHandshaker.handshake(channel);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();

    LoggerUtils.info("client:disconnect：client" + channel.remoteAddress());
    inactiveCallback.accept(ctx);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
    channelHandlerContext.flush();
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    LoggerUtils.info("client: receive message from server ：" + msg);
    Channel channel = ctx.channel();
    // handshake first to register channel and host
    if (!webSocketClientHandshaker.isHandshakeComplete()) {
      webSocketClientHandshaker.finishHandshake(channel, (FullHttpResponse) msg);
      handshakeFuture.setSuccess();
      ClientChannelHandlerPool.put(getHostValueInPath(), channel);
      return;
    }
    // receive text message
    if (msg instanceof TextWebSocketFrame) {
      TextWebSocketFrame frame = (TextWebSocketFrame) msg;
      final val text = frame.text();
      handleMessage(text);
    }
  }

  private void handleMessage(String text) {
    final val appContext = ApplicationContextProvider.getApplicationContext();
    if (appContext != null) {
      appContext.publishEvent(new SocketMessageEvent(this, text));
    }
  }

  private String getHostValueInPath() {
    String path = webSocketClientHandshaker.uri().getPath();
    int i = path.lastIndexOf("/");
    return path.substring(i + 1);
  }
}
