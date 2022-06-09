package com.onestep.os.messagebusservice.netty.server;

import com.onestep.os.messagebusservice.model.message.request.TransmitMessageRequestMessage;
import com.onestep.os.utils.JsonUtils;
import com.onestep.os.utils.LoggerUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
public class NettyServer {



    public void start(int port) {
        new Thread(() -> {
            //boss 线程组用于处理连接工作
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            //work 线程组用于数据处理
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.SO_BACKLOG, 1024 * 1024 * 10)
                        .localAddress(port)
                        .childHandler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                                ch.pipeline().addLast(new HttpServerCodec());
                                //以块的方式来写的处理器
                                ch.pipeline().addLast(new ChunkedWriteHandler());
                                //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
                                ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024 * 1024));
//                                 websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
//                                ch.pipeline().addLast(new WebSocketServerProtocolHandler("/host", null, true, 65536 * 10));
                                //自定义的路由
                                ch.pipeline().addLast(new WebSocketServerHandler());
                            }
                        });
                // 服务器异步创建绑定
                ChannelFuture cf = bootstrap.bind().sync();
                LoggerUtils.info(NettyServer.class + "server:  start websocker and listen for： " + cf.channel().remoteAddress());
                // 关闭服务器通道
                cf.channel().closeFuture().sync();
            } catch (Exception e) {
                LoggerUtils.info(Arrays.toString(e.getStackTrace()));
            } finally {
                LoggerUtils.info("server: close websocket");
                // 释放线程池资源
                try {
                    workerGroup.shutdownGracefully().sync();
                    bossGroup.shutdownGracefully().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public boolean sendMessage(String host, TransmitMessageRequestMessage msg) {
        LoggerUtils.info(String.format("send to host: %s, msg: %s", host, msg));
        return ServerChannelHandlerPool.sendMessage(host, new TextWebSocketFrame(JsonUtils.writeJson(msg)));
    }
}

