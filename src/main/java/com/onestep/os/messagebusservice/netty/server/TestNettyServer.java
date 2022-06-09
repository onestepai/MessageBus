package com.onestep.os.messagebusservice.netty.server;

public class TestNettyServer {
    public static void main(String[] args) {
        try {
            new NettyServer().start(12345);
        } catch (Exception e) {
            System.out.println("NettyServerError:" + e.getMessage());
        }
    }
}
