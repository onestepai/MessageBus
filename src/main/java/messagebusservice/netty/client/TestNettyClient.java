package com.onestep.os.messagebusservice.netty.client;

public class TestNettyClient {
  public static void main(String[] args) {
    new NettyClient().start("test.domain", "111.231.212.239", 30001);
  }
}
