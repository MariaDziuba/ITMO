package info.kgeorgiy.ja.dziuba.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static info.kgeorgiy.ja.dziuba.hello.HelloUDPUtils.*;

/**
 * UDP Client that sends requests to {@link HelloUDPServer}, waits for response,
 * and prints it to console
 */
public class HelloUDPClient implements HelloClient {
    private static final String USAGE =
            "Usage: <HelloUDPClient> [host] [port] [prefix] [threads amount] [query amount for each thread]";

    /**
     * @param prefix  - {@link String} representation of prefix of request
     * @param thread  - serial number of current thread
     * @param request - serial number of request in current thread
     * @return {@link String} representation of request
     */
    private static String makeRequest(String prefix, int thread, int request) {
        return prefix + thread + "_" + request;
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        final InetAddress ipAddress;
        try {
            ipAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.err.println("Can't resolve host's name");
            return;
        }

        final ExecutorService threadPool = Executors.newFixedThreadPool(threads);

        final SocketAddress socketAddress = new InetSocketAddress(ipAddress, port);

        for (int i = 0; i < threads; i++) {
            threadPool.submit(new RequestSender(socketAddress, prefix, i, requests));
        }

        closeExecutorService(threadPool, 30);
    }

    /**
     * Runs the client:
     * <p>
     * Usage: <HelloUDPClient> [host] [port] [prefix] [threads amount] [query amount for each thread]
     * @param args - command line arguments
     */
    public static void main(String[] args) {
        if (!validateArgs(args, 5, USAGE)) {
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String prefix = args[2];
        int threads = Integer.parseInt(args[3]);
        int requests = Integer.parseInt(args[4]);
        HelloUDPClient helloUDPClient = new HelloUDPClient();
        helloUDPClient.run(host, port, prefix, threads, requests);
    }

    /**
     * Auxiliary class for sending requests
     */
    private static class RequestSender implements Runnable {

        SocketAddress serverAddress;
        String prefix;
        int threadIndex;
        int requests;

        public RequestSender(final SocketAddress serverAddress,
                             final String prefix,
                             final int threadIndex,
                             final int requests) {
            this.serverAddress = serverAddress;
            this.prefix = prefix;
            this.threadIndex = threadIndex;
            this.requests = requests;
        }

        /**
         * Sends request
         */
        @Override
        public void run() {
            try (final DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(500);

                final DatagramPacket requestPacket = new DatagramPacket(new byte[0], 0, serverAddress);
                final int bufferSize = clientSocket.getReceiveBufferSize();
                final DatagramPacket responsePacket = new DatagramPacket(new byte[bufferSize], bufferSize);

                for (int requestIndex = 0; requestIndex < requests; requestIndex++) {

                    final String requestText = makeRequest(prefix, threadIndex, requestIndex);
                    requestPacket.setData(requestText.getBytes(StandardCharsets.UTF_8));

                    while (!clientSocket.isClosed()) {
                        try {
                            clientSocket.send(requestPacket);
                            System.out.println("Request: " + requestText);
                            clientSocket.receive(responsePacket);
                        } catch (IOException e) {
                            System.err.println("Failed to perform a request / get a response: " + e.getMessage());
                            continue;
                        }

                        final String responseText = extractMessage(responsePacket);
                        System.out.println("Response: " + responseText);
                        if (responseText.equals("Hello, " + requestText)) {
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                System.err.println("Timeout or socket error");
            }
        }
    }
}
