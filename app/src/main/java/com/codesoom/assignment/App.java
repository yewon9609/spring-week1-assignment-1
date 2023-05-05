package com.codesoom.assignment;

import com.codesoom.assignment.handler.DemoHttpHandler;
import com.codesoom.assignment.repositroy.TaskRepository;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

  public static void main(String[] args) {
    InetSocketAddress address = new InetSocketAddress("localhost", 8000);
    try {
      HttpServer httpServer = HttpServer.create(address, 0);
      HttpHandler handler = new DemoHttpHandler(new TaskRepository());
      httpServer.createContext("/", handler);
      httpServer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
