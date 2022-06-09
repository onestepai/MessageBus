package com.onestep.os.messagebusservice.netty.client;

import com.onestep.os.messagebusservice.model.message.request.TransmitMessageRequestMessage;
import com.onestep.os.utils.JsonUtils;
import com.onestep.os.utils.LoggerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.val;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Component
public class NettyClient {

  public void start(String clientHost, String serverHost, int port) {

    new Thread(
            () -> {
              // 配置客户端的NIO线程组
              EventLoopGroup clientGroup = new NioEventLoopGroup();
              try {
                WebSocketClientHandler webSocketClientHandler =
                    new WebSocketClientHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                            new URI("ws://" + serverHost + ":" + port + "/" + clientHost),
                            WebSocketVersion.V13,
                            null,
                            false,
                            new DefaultHttpHeaders()),
                        ctx -> {
                          LoggerUtils.info("client: disconnected from sever,try to reconnect");
                          // 如果运行过程中服务端挂了,执行重连机制
                          EventLoop eventLoop = ctx.channel().eventLoop();
                          eventLoop.schedule(
                              () -> start(clientHost, serverHost, port), 10L, TimeUnit.SECONDS);
                        });

                // 配置bootstrap
                EventLoopGroup worker = new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                bootstrap
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(
                        new ChannelInitializer<SocketChannel>() {

                          @Override
                          protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 将请求与应答消息编码或者解码为HTTP消息
                            pipeline.addLast(new HttpClientCodec());
                            // 将http消息的多个部分组合成一条完整的HTTP消息
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            // 客户端Handler
                            pipeline.addLast("handler", webSocketClientHandler);
                          }
                        });
                // 发起异步连接操作  同步方法待成功
                final val connect = bootstrap.connect(serverHost, port);
                connect.addListener(
                    (ChannelFutureListener)
                        future -> {
                          if (future.isSuccess()) {
                            LoggerUtils.info("client: connect to socket server success");
                          } else {
                            LoggerUtils.info("client: connect from sever failed ,try to reconnect");
                            future
                                .channel()
                                .eventLoop()
                                .schedule(
                                    () -> start(clientHost, serverHost, port),
                                    20,
                                    TimeUnit.SECONDS);
                          }
                        });
                Channel channel = connect.sync().channel();
                // 等待客户端链路关闭
                channel.closeFuture().sync();
              } catch (InterruptedException | URISyntaxException e) {
                e.printStackTrace();
              } finally {
                LoggerUtils.info("client: close websocket client");
                clientGroup.shutdownGracefully();
              }
            })
        .start();
  }

  public boolean sendMessage(String clientHost, TransmitMessageRequestMessage request) {
    return ClientChannelHandlerPool.sendMessage(
        clientHost, new TextWebSocketFrame(JsonUtils.writeJson(request)));
  }
}
