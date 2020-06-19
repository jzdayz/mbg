package io.github.jzdayz.mbg.util;

import java.net.ServerSocket;

public class PortUtils {

  public static int port() {
    for (int i = 10000; i < 60000; i++) {
      try {
        ServerSocket serverSocket = new ServerSocket(i);
        serverSocket.close();
        return i;
      } catch (Exception e) {
      }
    }
    throw new RuntimeException("no port");
  }
}
