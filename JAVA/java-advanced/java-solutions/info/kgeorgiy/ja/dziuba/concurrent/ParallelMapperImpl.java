package info.kgeorgiy.ja.dziuba.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

/**
 * Implementation of {@link ParallelMapper} interface.
 * Parallelism is used for applying function.
 *
 * @author Dziuba Maria
 * @version 1.0
 **/
public class ParallelMapperImpl implements ParallelMapper {

    /**
     * True if method close() has been invoked
     */
    private boolean closed = false;

    /**
     * Queue of tasks
     */
    private final SynchronizedQueue queue;

    /**
     * List of threads that are executing tasks
     */
    private final List<Thread> threadsList;

    /**
     * @param threads number of {@link Thread}s to use.
     * @throws InterruptedException throws in case some thread is interrupted.
     */
    public ParallelMapperImpl(final int threads) throws InterruptedException {
        queue = new SynchronizedQueue();
        threadsList = new ArrayList<>();
        InterruptedException exceptions = new InterruptedException();
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(
                    () -> {
                        try {
                            while (!Thread.interrupted()) {
                                runTask();
                            }
                        } catch (InterruptedException e) {
                            exceptions.addSuppressed(e);
                        } finally {
                            Thread.currentThread().interrupt();
                        }
                    });
            threadsList.add(thread);
            thread.start();
        }
        if (exceptions.getSuppressed().length != 0) {
            throw exceptions;
        }
    }

    /**
     * Runs one task, polling in from the queue
     * @throws InterruptedException in case some thread is interrupted
     */
    private void runTask() throws InterruptedException {
        Runnable task;
        synchronized (queue) {
            queue.isEmpty();
            while (queue.isEmpty()) {
                queue.wait();
            }
            task = queue.poll();
            queue.notify();
        }
        task.run();
    }

    /**
     * Synchronized variant of queue for tasks
     */
    public static class SynchronizedQueue {
        private final LinkedList<Runnable> tasks = new LinkedList<>();

        /**
         * Inserts an element in tail of queue
         * @param task to add
         */
        public synchronized void add(Runnable task) {
            tasks.add(task);
            notify();
        }

        /**
         * Removes an element from head and deletes it
         * @return one task
         */
        public synchronized Runnable poll() {
            return tasks.poll();
        }

        /**
         * @return true if queue is empty
         */
        public synchronized boolean isEmpty() {
            return tasks.isEmpty();
        }
    }

    /**
     * Synchronized variant of list of completed tasks
     * @param <T> given type of values
     */
    public static class SynchronizedDataList<T> {
        /**
         * List of evaluated tasks
         */
        private final List<T> dataList;
        /**
         * Number of computed tasks
         */
        private int completedTasks = 0;

        public SynchronizedDataList(final int size) {
            this.dataList = new ArrayList<>(Collections.nCopies(size, null));
        }

        /**
         * Inserts a value and notifies if all tasks are done
         * @param value to insert
         * @param index where value should be put
         */
        synchronized void set(final T value, final int index) {
            dataList.set(index, value);
            completedTasks++;
            if (completedTasks == dataList.size()) {
                notify();
            }
        }

        /**
         * Returns evaluated tasks and waits if they are not completed yet
         * @return {@link List} of completed tasks
         * @throws InterruptedException if some thread is interrupted
         */
        synchronized List<T> getAllData() throws InterruptedException {
            while (completedTasks < dataList.size()) {
                wait();
            }
            return dataList;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, R> List<R> map(final Function<? super T, ? extends R> function,
                              final List<? extends T> values) throws InterruptedException {
        SynchronizedDataList<R> tasksList = new SynchronizedDataList<>(values.size());
        synchronized (this) {
            if (closed) {
                return null;
            }
        }
        for (int idx = 0; idx < values.size(); idx++) {
            final int curIdx = idx;
            queue.add(() -> tasksList.set(function.apply(values.get(curIdx)), curIdx));
        }
        return tasksList.getAllData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        synchronized (this) {
            closed = true;
        }
        for (Thread thread : threadsList) {
            thread.interrupt();
        }
        for (Thread thread : threadsList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // :NOTE: printStackTrace
                e.printStackTrace();
            }
        }
    }
}