package info.kgeorgiy.ja.dziuba.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

import static info.kgeorgiy.ja.dziuba.hello.HelloUDPUtils.*;

/**
 * UDP server that accepts requests from {@link HelloUDPClient} and responds to them
 */
public class HelloUDPServer implements HelloServer {
    private static final String USAGE = "USAGE: <HelloUDPServer> [port] [threads]";

    private DatagramSocket serverSocket = null;
    private ExecutorService threadPool = null;
    private ExecutorService listener = null;

    @Override
    public void start(int port, int threads) {
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.err.println("Datagram Socket exception appeared: " + e.getMessage());
            return;
        }

        threadPool = Executors.newFixedThreadPool(threads);
        listener = Executors.newSingleThreadExecutor();
        listener.submit(this::listen);
    }

    /**
     * Listens for incoming requests
     */
    private void listen() {
        while (!serverSocket.isClosed()) {
            try {
                final int bufferSize = serverSocket.getReceiveBufferSize();
                final DatagramPacket requestPacket = new DatagramPacket(new byte[bufferSize], bufferSize);
                serverSocket.receive(requestPacket);
                threadPool.submit(new ResponseSender(requestPacket));
            } catch (IOException e) {
                System.err.println("Unable to receive: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        serverSocket.close();
        closeExecutorService(threadPool, 30);
        closeExecutorService(listener, 1);
    }

    /**
     * Runs a server:
     * <p>
     * USAGE: <HelloUDPServer> [port] [threads]
     * @param args - command line arguments
     */
    public static void main(String[] args) {
        if (!validateArgs(args, 2, USAGE)) {
            return;
        }
        int port = Integer.parseInt(args[0]);
        int threads = Integer.parseInt(args[1]);
        HelloUDPServer helloUDPServer = new HelloUDPServer();
        helloUDPServer.start(port, threads);
        helloUDPServer.close();
    }

    /**
     * Auxiliary class for sending response
     */
    private class ResponseSender implements Runnable {

        DatagramPacket request;

        public ResponseSender(final DatagramPacket request) {
            this.request = request;
        }

        /**
         * Sends response
         */
        @Override
        public void run() {
            final DatagramPacket respondPacket = new DatagramPacket(new byte[0], 0, request.getSocketAddress());
            String response = "Hello, " + extractMessage(request);

            respondPacket.setData(response.getBytes(StandardCharsets.UTF_8));
            try {
                serverSocket.send(respondPacket);
            } catch (IOException e) {
                System.err.println("Response exception occurred: " + e.getMessage());
            }
        }
    }
}
