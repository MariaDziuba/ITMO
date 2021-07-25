package info.kgeorgiy.ja.dziuba.mapper;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Implementation of {@link ScalarIP} interface using iterative parallelism.
 *
 * @author Dziuba Maria
 * @version 1.0
 */
public class IterativeParallelism implements ScalarIP {

    /**
     * Object, that applies function to given values in parallel way
     */
    private final ParallelMapper mapper;

    /**
     * @param mapper Class instance implementing ParallelMapper interface
     */
    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    public IterativeParallelism() {
        this(null);
    }

    /**
     * @param threadsList {@link List} of {@link Thread}s to join.
     * @throws InterruptedException throws in case some thread didn't join.
     */
    private static void joinTasks(final List<Thread> threadsList) throws InterruptedException {
        InterruptedException exceptions = new InterruptedException();
        for (Thread thread : threadsList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                exceptions.addSuppressed(e);
            }
        }
        if (exceptions.getSuppressed().length != 0) {
            throw exceptions;
        }
    }

    /**
     * Creates an almost fair distribution of tasks performed on a given number of {@link Thread}s
     *
     * @param <T>     given type of values
     * @param threads number of {@link Thread}s to use.
     * @param values  {@link List} of tasks to be completed.
     * @return {@link List} of tasks distributed on given number of {@link Thread}s
     */
    private <T> List<Stream<T>> distribute(int threads, List<T> values) {
        threads = Math.max(1, Math.min(threads, values.size()));
        final List<Stream<T>> distribution = new ArrayList<>();
        final int groupSize = values.size() / threads;
        int extraTasks = values.size() % threads;
        for (int i = 0, end = 0; i < threads; i++) {
            int begin = end;
            end = begin + groupSize + (extraTasks > 0 ? 1 : 0);
            extraTasks--;
            distribution.add(values.subList(begin, end).stream());
        }

        return distribution;
    }

    /**
     * @param threads       number of {@link Thread}s to use
     * @param values        {@link List} of tasks to be completed.
     * @param tasksFunction {@link Function} to be applied to each element from tasksDistribution
     * @param gatherer      {@link Function} to get the final result from tasksResults
     * @param <T>           given type of values
     * @param <R>           result
     * @return final result of a task
     * @throws InterruptedException throws in case some thread is interrupted.
     */
    private <T, R> R abstractTask(int threads, List<T> values,
                                  Function<Stream<T>, R> tasksFunction,
                                  Function<Stream<R>, R> gatherer) throws InterruptedException {

        validateArgs(threads, values, tasksFunction, gatherer);
        List<Stream<T>> tasksDistributions = distribute(threads, values);
        threads = tasksDistributions.size();
        List<R> tasksResults;
        List<Thread> threadsList = new ArrayList<>();
        if (mapper != null) {
            tasksResults = mapper.map(tasksFunction, tasksDistributions);
        } else {
            tasksResults = new ArrayList<>(Collections.nCopies(threads, null));
            for (int idx = 0; idx < threads; idx++) {
                final int curIdx = idx;
                Thread thread = new Thread(
                        () -> tasksResults.set(curIdx, tasksFunction.apply(tasksDistributions.get(curIdx))));
                threadsList.add(thread);
                thread.start();
            }
        }
        joinTasks(threadsList);
        return gatherer.apply(tasksResults.stream());
    }

    /**
     * Validates input arguments
     *
     * @param threads       {@link List} of threads to join.
     * @param values        {@link List} of tasks to be completed.
     * @param tasksFunction {@link Function} to be applied to each element from tasksDistribution
     * @param gatherer      {@link Function} to get the final result from tasksResults
     **/
    private <T, R> void validateArgs(int threads, List<? extends T> values,
                                     Function<Stream<T>, R> tasksFunction,
                                     Function<Stream<R>, R> gatherer) throws InterruptedException {
        if (threads <= 0) {
            throw new InterruptedException("Number of threads must be > 0");
        }
        if (values == null) {
            throw new InterruptedException("Values list must be non-null");
        }
        if (tasksFunction == null) {
            throw new InterruptedException("Task function must be non-null");
        }
        if (gatherer == null) {
            throw new InterruptedException("Gathering function must be non-null");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values,
                         Comparator<? super T> cmp) throws InterruptedException {
        return abstractTask(threads, values,
                stream -> stream.max(cmp).orElseThrow(),
                stream -> stream.max(cmp).orElseThrow());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values,
                         Comparator<? super T> cmp) throws InterruptedException {
        return maximum(threads, values, cmp.reversed());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values,
                           Predicate<? super T> predicate) throws InterruptedException {
        return abstractTask(threads, values,
                stream -> stream.allMatch(predicate),
                stream -> stream.allMatch(Boolean::booleanValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values,
                           Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, predicate.negate());
    }
}
