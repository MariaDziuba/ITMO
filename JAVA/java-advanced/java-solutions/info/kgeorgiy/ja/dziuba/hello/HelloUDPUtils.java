package info.kgeorgiy.ja.dziuba.hello;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class HelloUDPUtils {
    /**
     * Validates command line arguments
     *
     * @see Arrays
     * @param args - command line arguments
     */
    public static boolean validateArgs(String[] args, int size, String usage) {
        if (args.length < 1 || args.length > size) {
            System.err.println("Wrong arguments. " + usage);
            return false;
        }

        if (Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Arguments must be non-null");
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            try {
                Integer.parseInt(args[i]);
            } catch (final NumberFormatException ignored) {
                System.err.println("Argument №" + i + " is not an integer value");
                return false;
            }
        }
        return true;
    }

    /**
     * Extracts message from {@link DatagramPacket}
     *
     * @see StandardCharsets
     * @param packet - {@link DatagramPacket} to extract message from
     * @return {@link String} representation of message
     */
    public static String extractMessage(final DatagramPacket packet) {
        return new String(packet.getData(),
                packet.getOffset(),
                packet.getLength(),
                StandardCharsets.UTF_8);
    }

    /**
     * @see StandardCharsets
     * Extract message from {@link ByteBuffer}
     *
     * @param buffer - {@link ByteBuffer} to extract message from
     * @return {@link String} representation of message
     */
    public static String extractMessageFromBuffer(final ByteBuffer buffer) {
        buffer.flip();
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    /**
     * @see StandardCharsets
     * Fill given {@link ByteBuffer} with given string
     *
     * @param buffer - {@link ByteBuffer} to fill
     * @param string - {@link String} for filling
     */
    public static void fillBuffer(ByteBuffer buffer, final String string) {
        buffer.put(string.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
    }

    /**
     * Invokes method shutdown() on given {@link ExecutorService}, but if all tasks have not been done
     * in given number of seconds or InterruptedException occurred, invokes shutdownNow()
     *
     * @param executorService - {@link ExecutorService} to close
     * @param timeout         - number of seconds to wait
     */
    public static void closeExecutorService(final ExecutorService executorService, final int timeout) {
        executorService.shutdown();
        try {
            boolean closed = executorService.awaitTermination(timeout, TimeUnit.SECONDS);
            System.out.println(closed ? "All tasks done" : "Timeout");
        } catch (final InterruptedException e) {
            System.err.println("Can't terminate");
        }
    }
}

