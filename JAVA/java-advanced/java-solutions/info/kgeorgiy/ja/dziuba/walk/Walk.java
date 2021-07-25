package info.kgeorgiy.ja.dziuba.walk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Walk {

    void process(String inputFile, String outputFile) throws WalkException {
        Path inputFilePath, outputFilePath;
        try {
            inputFilePath = Path.of(inputFile);
            outputFilePath = Path.of(outputFile);
        } catch (InvalidPathException e) {
            throw new WalkException("Invalid path");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
            try (Stream<String> inputFiles = Files.lines(inputFilePath, StandardCharsets.UTF_8)) {
                inputFiles.forEach(filePathName -> {
                    long hash;
                    try {
                        hash = PJWHasher.calculateHash(Path.of(filePathName));
                    } catch (InvalidPathException e) {
                        hash = 0;
                    }
                    try {
                        writer.write(String.format("%016x %s%n", hash, filePathName));
                    } catch (IOException e) {
                        System.err.println("Unable to write into output file" + outputFilePath.toString());
                    }
                });
            } catch (UnsupportedEncodingException e) {
                throw new WalkException("Wrong encoding of input file" + inputFilePath.toString());
            } catch (IOException e) {
                throw new WalkException("Unable to read input file " + inputFilePath.toString());
            }
        } catch (UnsupportedEncodingException e) {
            throw new WalkException("Wrong encoding of output file" + outputFilePath.toString());
        } catch (InvalidPathException | IOException e) {
            throw new WalkException("Unable to open output file " + outputFilePath.toString());
        }
    }

    public static void main(String[] args) {
        try {
            if (args == null || args.length != 2) {
                throw new WalkException("Usage java Walk <input file name> <output file name>");
            }
            if (args[0] == null || args[1] == null) {
                throw new WalkException("Non-null files expected");
            }
            Walk walk = new Walk();
            walk.process(args[0], args[1]);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}