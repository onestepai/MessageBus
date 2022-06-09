package com.onestep.os.messagebusservice.netty.server;

import com.onestep.os.messagebusservice.model.message.event.SocketMessageEvent;
import com.onestep.os.messagebusservice.util.ApplicationContextProvider;
import com.onestep.os.utils.LoggerUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import lombok.val;


public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        LoggerUtils.info("ServerWebSocketHandler exceptionCaught " + cause.getMessage());
        channelHandlerContext.close();
        super.exceptionCaught(channelHandlerContext, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LoggerUtils.info("connect to client " + ctx.channel().remoteAddress().toString());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final val channel = ctx.channel();
        LoggerUtils.info("disconnect to client " + channel.remoteAddress().toString());
        ServerChannelHandlerPool.remove(channel);
        channel.close();
    }


    private WebSocketServerHandshaker socketServerHandShaker;

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        LoggerUtils.info("server: receive client http message：" + msg.uri());

        WebSocketServerHandshakerFactory wsFactory
                = new WebSocketServerHandshakerFactory("ws://127.0.0.1:12345/ws/{uri}", null, false);

        String uri = msg.uri();
        Channel connectChannel = ctx.channel();
        ServerChannelHandlerPool.put(getHostValueInPath(uri), connectChannel);
        socketServerHandShaker = wsFactory.newHandshaker(msg);
        if (socketServerHandShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(connectChannel);
        } else {
            socketServerHandShaker.handshake(connectChannel, msg);
        }
    }

    private String getHostValueInPath(String uri) {
        int i = uri.lastIndexOf("/");
        return uri.substring(i + 1);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LoggerUtils.info("server: receive message from client ：" + msg);

        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        Channel channel = ctx.channel();

        if (frame instanceof CloseWebSocketFrame) {
            LoggerUtils.info("server: 关闭与客户端链接 " + channel.remoteAddress());
            socketServerHandShaker.close(channel, (CloseWebSocketFrame) frame.retain());
            return;
        }


        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            return;
        }

        String request = ((TextWebSocketFrame) frame).text();
        handleMessage(ctx, request);
    }

    private void handleMessage(ChannelHandlerContext ctx, String message) {
        LoggerUtils.info(String.format("server: received %s from client  %s", message, ctx.channel()));

        final val appContext = ApplicationContextProvider.getApplicationContext();
        if (appContext != null) {
            appContext.publishEvent(new SocketMessageEvent(this, message));
        }
    }

}