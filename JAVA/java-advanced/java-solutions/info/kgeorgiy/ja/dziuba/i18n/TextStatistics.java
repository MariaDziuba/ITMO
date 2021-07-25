package info.kgeorgiy.ja.dziuba.i18n;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;

public class TextStatistics {

    private static Locale inputLocale;
    private static Locale outputLocale;

    private static boolean validateArgs(final String[] args) {
        if (args.length < 1 || args.length > 4) {
            System.err.println("USAGE: <TextStatistics> [inputLocale]" +
                    " [outputLocale] [inputFileName] [outputFileName]");
            return false;
        }

        if (Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Arguments must be non-null");
            return false;
        }

        inputLocale = new Locale.Builder().setLanguageTag(args[0]).build();
        outputLocale = new Locale.Builder().setLanguageTag(args[1]).build();
        if (!(outputLocale.getLanguage().equals("ru") || outputLocale.getLanguage().equals("en"))) {
            System.err.println("Unsupported output locale provided");
            return false;
        }

        return true;
    }

    public static void main(final String[] args) {
        if (!validateArgs(args)) {
            return;
        }

        final String text;
        try {
            text = FileUtils.readFile(Path.of(args[2]));
        } catch (final IOException | InvalidPathException e) {
            // :NOTE: Язык
            System.err.println("Failed to read input file, " + e.getMessage());
            return;
        }
        final Map<StatisticsType, StatisticsData<?>> statistics =
                new StatisticsManager().getStatistics(text, inputLocale);

        final String report;
        try {
            report = ReportGenerator.generateReport(inputLocale, outputLocale, statistics, args[2]);
        } catch (final IOException e) {
            System.err.println("Failed to generate report, " + e.getMessage());
            return;
        }

        try {
            FileUtils.writeFile(args[3], report);
        } catch (final IOException | InvalidPathException e) {
            System.err.println("Failed to write a report file, " + e.getMessage());
        }
    }
}