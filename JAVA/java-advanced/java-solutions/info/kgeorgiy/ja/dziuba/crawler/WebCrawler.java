package info.kgeorgiy.ja.dziuba.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Crawls websites recursively, downloading pages and extracting links from them.
 *
 * @author Dziuba Maria
 * @version 1.1
 */
public class WebCrawler implements Crawler {

    private final ExecutorService downloaderService;
    private final ExecutorService extractorService;
    private final Downloader downloader;

    private static final String USAGE = "Usage : WebCrawler url [depth [downloads [extractors [perHost]]]]";

    private static final int DEFAULT_ARG = 1;

    /**
     * @param downloader - {@link Downloader}, which downloads websites
     * @param downloaders - number of given downloader threads
     * @param extractors - number of given link extractors threads
     * @param perHost - ---------------------------------------
     */
    public WebCrawler(final Downloader downloader, final int downloaders, final int extractors, final int perHost) {
        this.downloaderService = Executors.newFixedThreadPool(downloaders);
        this.extractorService = Executors.newFixedThreadPool(extractors);
        this.downloader = downloader;
    }

    @Override
    public Result download(final String url, final int depth) {
        final Set<String> usedURL = ConcurrentHashMap.newKeySet();
        final Map<String, IOException> exceptions = new ConcurrentHashMap<>();
        final Phaser phaser = new Phaser();

        phaser.register();
        usedURL.add(url);
        recursiveCrawl(url, depth, usedURL, exceptions, phaser);
        phaser.arriveAndAwaitAdvance();
        usedURL.removeAll(exceptions.keySet());

        return new Result(List.copyOf(usedURL), exceptions);
    }

    /**
     * Crawls websites recursively
     * @param url - URL to start crawling from (in {@link String} representation)
     * @param depth - needed depth of a crawl
     * @param usedUrl - {@link Set} of urls that have already been crawled
     * @param exceptions - {@link Map} of exceptions occurred during the crawl
     * @param phaser - {@link Phaser}, a reusable synchronization barrier
     */
    private void recursiveCrawl(final String url,
                                final int depth,
                                final Set<String> usedUrl,
                                final Map<String, IOException> exceptions,
                                final Phaser phaser
    ) {
        phaser.register();
        downloaderService.submit(() -> {
            try {
                final Document doc = downloader.download(url);
                if (depth != 1) {
                    extractLinks(url, depth, usedUrl, exceptions, phaser, doc);
                }
            } catch (final IOException e) {
                exceptions.put(url, e);
            } finally {
                phaser.arrive();
            }
        });
    }

    private void extractLinks(final String url, final int depth, final Set<String> usedUrl, final Map<String, IOException> exceptions, final Phaser phaser, final Document doc) {
        phaser.register();
        extractorService.submit(() -> {
            try {
                doc.extractLinks().stream()
                        .filter(usedUrl::add)
                        .forEach(link -> recursiveCrawl(link, depth - 1, usedUrl, exceptions, phaser));
            } catch (final IOException e) {
                exceptions.put(url, e);
            } finally {
                phaser.arrive();
            }
        });
    }

    /**
     * Prints, which links were successfully downloaded, and which were not downloaded due to error
     * @param result - result to print
     */
    private static void printResult(final Result result) {
        System.out.println("Successfully downloaded " + result.getDownloaded().size() + " pages: ");
        result.getDownloaded().forEach(System.out::println);

        System.out.println("Not downloaded due to error: " + result.getErrors().size());
        result.getErrors().forEach((msg, e) -> {
            System.out.println("URL: " + msg);
            System.out.println("Error: " + e.getMessage());
        });
    }

    private static int getArg(final String[] args, final int idx) {
        return idx > args.length ? DEFAULT_ARG : Integer.parseInt(args[idx]);
    }

    /**
     * Validates command line arguments
     * @param args - command line arguments
     * @throws IllegalArgumentException if arguments are wrong
     */
    private static void validateArgs(final String[] args) {
        if (args.length < 1 || args.length > 5 || args[0] == null) {
            System.err.println("Wrong arguments. " + USAGE);
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < args.length; i++) {
            try {
                Integer.parseInt(args[i]);
            } catch (final NumberFormatException ignored) {
                throw new IllegalArgumentException("Argument №" + i + " is not an integer value");
            }
        }
    }

    /**
     * @param args needed to be provided: "Usage : WebCrawler url [depth [downloads [extractors [perHost]]]]"
     * @throws IOException in case if error occurred
     */
    public static void main(final String[] args) throws IOException {
        validateArgs(args);
        final String url = args[0];

        final int depth = getArg(args, 1);
        final int downloaders = getArg(args, 2);
        final int extractors = getArg(args, 3);
        final int perHost = getArg(args, 4);

        final info.kgeorgiy.ja.dziuba.crawler.WebCrawler webCrawler = new info.kgeorgiy.ja.dziuba.crawler.WebCrawler(new CachingDownloader(), downloaders, extractors, perHost);
        final Result result = webCrawler.download(url, depth);
        // :NOTE: Утечка ресурсов
        webCrawler.close();
        printResult(result);
    }

    /**
     * Invokes method shutdown() on given {@link ExecutorService}, but if all tasks have not been done in 30 s
     * or InterruptedException occurred, invokes shutdownNow()
     *
     * @param executorService - {@link ExecutorService} to close
     */
    private static void close(final ExecutorService executorService) {
        executorService.shutdown();
        try {
            // :NOTE: Очень мало
            // :NOTE: Не дожидается окончания
            final boolean closed = executorService.awaitTermination(30, TimeUnit.SECONDS);
            System.out.println(closed ? "All tasks done" : "Timeout");
        } catch (final InterruptedException e) {
            System.err.println("Can't terminate");
        }
    }

    @Override
    public void close() {
        close(downloaderService);
        close(extractorService);
    }
}
