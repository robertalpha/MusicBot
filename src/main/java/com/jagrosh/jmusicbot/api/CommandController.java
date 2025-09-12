package com.jagrosh.jmusicbot.api;

import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

public class CommandController {
    public CommandController(HttpServer server, ApiCommandHandler handler) {
        server.createContext("/api/command", (exchange -> {
            //GET
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "hello world";

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(response.getBytes());
                output.flush();
            } else if ("POST".equals(exchange.getRequestMethod())) {
                StringBuilder sb = new StringBuilder();
                InputStream ios = exchange.getRequestBody();
                int i;
                while ((i = ios.read()) != -1) {
                    sb.append((char) i);
                }

                // send command
                handler.handlePayload(sb.toString());

                String response = "received";

                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

}
