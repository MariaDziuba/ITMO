package info.kgeorgiy.ja.dziuba.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static info.kgeorgiy.ja.dziuba.hello.HelloUDPUtils.*;

/**
 * UDP server that accepts requests from {@link HelloUDPNonblockingClient} and responds
 * to them. Differs from {@link HelloUDPClient} in using non-blocking IO
 */
public class HelloUDPNonblockingServer implements HelloServer {

    private static final String USAGE = "USAGE: <HelloUDPServer> [port] [threads]";
    private DatagramChannel datagramChannel;
    private Selector selector;
    private ExecutorService listener;
    private InetSocketAddress serverAddress;

    /**
     * Auxiliary class for client attachment of a channel
     */
    public static class ClientAttachment {

        private static final int MAX_MESSAGE_SIZE = 1 << 16;
        ByteBuffer buf;
        SocketAddress address;

        public ClientAttachment() {
            buf = ByteBuffer.allocate(MAX_MESSAGE_SIZE);
        }
    }

    @Override
    public void start(int port, int threads) {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            System.err.println("Unable to open selector");
            return;
        }
        try {
            datagramChannel = DatagramChannel.open();
        } catch (IOException e) {
            System.err.println("Unable to open Datagram channel");
            return;
        }
        serverAddress = new InetSocketAddress(port);
        listener = Executors.newSingleThreadExecutor();
        listener.submit(this::listen);
    }

    /**
     * Listens for incoming requests
     */
    private void listen() {
        try {
            datagramChannel.bind(serverAddress);
            datagramChannel.configureBlocking(false);
            datagramChannel.register(selector, SelectionKey.OP_READ, new ClientAttachment());
            while (!Thread.interrupted() && selector.isOpen() && datagramChannel.isOpen()) {
                selector.select();
                if (selector.selectedKeys().isEmpty()) {
                    continue;
                }
                for (final Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                    final SelectionKey key = it.next();
                    ClientAttachment attachment = (ClientAttachment) key.attachment();
                    if (key.isReadable()) {
                        read(attachment);
                        key.interestOps(SelectionKey.OP_WRITE);
                    } else if (key.isWritable()) {
                        write(attachment);
                        key.interestOps(SelectionKey.OP_READ);
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            close();
        }
    }

    /**
     * Reads from given {@link DatagramChannel}
     *
     * @param attachment - {@link ClientAttachment}
     * @throws IOException - in case of IO error in channel
     */
    private void read(ClientAttachment attachment) throws IOException {
        attachment.buf.clear();
        attachment.address = datagramChannel.receive(attachment.buf);
        String requestText = extractMessageFromBuffer(attachment.buf);
        attachment.buf.clear();
        fillBuffer(attachment.buf, "Hello, " + requestText);
    }

    /**
     * Writes to given {@link DatagramChannel}
     *
     * @param attachment - {@link ClientAttachment}
     * @throws IOException - in case of IO error in channel
     */
    private void write(ClientAttachment attachment) throws IOException {
        datagramChannel.send(attachment.buf, attachment.address);
    }

    @Override
    public void close() {
        try {
            if (datagramChannel != null) {
                datagramChannel.close();
            }
            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
            System.err.println("IO error occurred when closing resources");
        }
        closeExecutorService(listener, 30);
    }

    /**
     * Runs a server:
     * <p>
     * USAGE: <HelloUDPServer> [port] [threads]
     */
    public static void main(String[] args) {
        if (!validateArgs(args, 2, USAGE)) {
            return;
        }

        int port = Integer.parseInt(args[0]);
        int threads = Integer.parseInt(args[1]);
        HelloUDPNonblockingServer server = new HelloUDPNonblockingServer();
        server.start(port, threads);
        server.close();
    }
}