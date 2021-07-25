package info.kgeorgiy.ja.dziuba.walk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

final public class PJWHasher {

    final static int BUFFER_SIZE = 256;

    public static long calculateHash(Path filePath) {
        try (BufferedInputStream stream = new BufferedInputStream(Files.newInputStream(filePath))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            long hashValue = 0;

            int charNumber;
            while ((charNumber = stream.read(buffer)) >= 0) {
                for (int i = 0; i < charNumber; ++i) {
                    hashValue =  (hashValue << 8) + (buffer[i] & 0xff);
                    long high = hashValue & 0xFF00000000000000L;
                    if (high != 0) {
                        hashValue ^= high >> 48;
                        hashValue &= ~high;
                    }
                }
            }
            return hashValue;

        } catch (IOException e) {
            // :NOTE: add message about the exception
            return 0L;
        }
    }
}