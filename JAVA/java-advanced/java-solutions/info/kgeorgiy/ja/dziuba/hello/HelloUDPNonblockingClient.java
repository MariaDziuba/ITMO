package info.kgeorgiy.ja.dziuba.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import static info.kgeorgiy.ja.dziuba.hello.HelloUDPUtils.*;

/**
 * UDP Client that sends requests to {@link HelloUDPNonblockingServer}, waits for response,
 * and prints it to console. Differs from {@link HelloUDPServer} in using non-blocking IO
 */
public class HelloUDPNonblockingClient implements HelloClient {

    private static final int MAX_UDP_MESSAGE_SIZE = 1 << 16;
    private int openedChannels;

    private static final String USAGE =
            "Usage: <HelloUDPClient> [host] [port] [prefix] [threads amount] [query amount for each thread]";

    /**
     * Auxiliary class for datagram attachment of a channel
     */
    public static class DatagramAttachment {

        private final int threadIndex;
        private int requestIndex;

        public void incrementRequestIndex() {
            ++requestIndex;
        }

        public int getRequestIndex() {
            return requestIndex;
        }

        public int getThreadIndex() {
            return threadIndex;
        }

        public DatagramAttachment(int threadIndex) {
            this.threadIndex = threadIndex;
            this.requestIndex = 0;
        }
    }
    /**
     * @param prefix  - {@link String} representation of prefix of request
     * @param thread  - serial number of current thread
     * @param request - serial number of request in current thread
     * @return {@link String} representation of request
     */

    private static String makeRequest(String prefix, int thread, int request) {
        return prefix + thread + "_" + request;
    }

    /**
     * Changes mode of given {@link DatagramChannel}
     *
     * @param channel - {@link DatagramChannel} to check on being open
     * @param key - {@link SelectionKey} to set interest operations on
     * @param mode - mode to set: either OP_WRITE or OP_READ
     */
    private static void changeMode(DatagramChannel channel, SelectionKey key, int mode) {
        if (channel.isOpen()) {
            key.interestOps(mode);
        }
    }

    /**
     * Configures the {@link HelloUDPNonblockingClient}
     *
     * @param host - server host
     * @param port - server port
     * @param threads - number of request threads
     * @param selector - {@link Selector} of this {@link HelloUDPNonblockingClient}
     */
    private void configure(String host, int port, int threads, Selector selector) {
        InetSocketAddress serverAddress = new InetSocketAddress(host, port);
        for (int i = 0; i < threads; ++i) {
            try {
                DatagramChannel channel = DatagramChannel.open();
                channel.configureBlocking(false);
                channel.connect(serverAddress);
                channel.register(selector, SelectionKey.OP_WRITE, new DatagramAttachment(i));
            } catch (IOException e) {
                System.err.println("Unable to open Datagram channel");
            }
        }
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try (Selector selector = Selector.open()) {

            configure(host, port, threads, selector);

            openedChannels = threads;
            ByteBuffer buf = ByteBuffer.allocate(MAX_UDP_MESSAGE_SIZE);
            while (!Thread.interrupted() && openedChannels > 0) {
                selector.select(100);

                if (selector.selectedKeys().isEmpty()) {
                    selector.keys().forEach(key -> key.interestOps(SelectionKey.OP_WRITE));
                    continue;
                }

                for (final Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final SelectionKey key = i.next();
                    final DatagramChannel channel = (DatagramChannel) key.channel();
                    final DatagramAttachment attachment = (DatagramAttachment) key.attachment();
                    final String requestText = makeRequest(
                            prefix,
                            attachment.getThreadIndex(),
                            attachment.getRequestIndex());

                    buf.clear();

                    if (key.isReadable()) {
                        read(channel, attachment, buf, requests, requestText);
                        changeMode(channel, key, SelectionKey.OP_WRITE);
                    } else if (key.isWritable()) {
                        write(channel, buf, requestText);
                        changeMode(channel, key, SelectionKey.OP_READ);
                    }
                    i.remove();
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to open selector");
        }
    }

    /**
     * Reads from given {@link DatagramChannel}
     *
     * @param channel - {@link DatagramChannel} to read from
     * @param attachment - {@link DatagramAttachment}
     * @param buffer - {@link ByteBuffer} to write in
     * @param requests - number of requests per thread
     * @param requestText - {@link String} representation of a request
     * @throws IOException -  in case of IO error in channel
     */
    private void read(DatagramChannel channel, DatagramAttachment attachment, ByteBuffer buffer,
                      int requests, String requestText) throws IOException {
        try {
            channel.read(buffer);
            String response = extractMessageFromBuffer(buffer);
            if (response.equals("Hello, " + requestText)) {
                attachment.incrementRequestIndex();
            }
            if (attachment.getRequestIndex() == requests) {
                channel.close();
                --openedChannels;
            }
        } catch (IOException e) {
            channel.close();
        }
    }

    /**
     * Writes to given {@link DatagramChannel}
     *
     * @param channel - {@link DatagramChannel} to write to
     * @param buffer - {@link ByteBuffer} to write in
     * @param requestText - {@link String} representation of a request
     * @throws IOException - in case of IO error in channel
     */
    private void write(DatagramChannel channel, ByteBuffer buffer, String requestText) throws IOException {
        fillBuffer(buffer, requestText);
        try {
            channel.write(buffer);
        } catch (IOException e) {
            channel.close();
        }
    }

    /**
     * Runs the client:
     * <p>
     * Usage: <HelloUDPClient> [host] [port] [prefix] [threads amount] [query amount for each thread]
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
        new HelloUDPNonblockingClient().run(host, port, prefix, threads, requests);
    }
}
