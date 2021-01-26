package org.example;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Implements something that is like a site crawler except it crawls the number
 * line randomly. Each "visit" to an integer results in the discovery of five
 * additional integers that should also be visited if they haven't already.
 *
 * Integers should only be visited once, this is accomplished with checks
 * against the alreadyVisited ConcurrentHashMap.
 *
 * To simulate the expense of crawling a web site, each "visit" to an integer
 * on the number line takes 100 ms.
 *
 * So, single-threaded, visiting 100,000 integers should take 10,000 sec. But
 * with 100 worker threads, it only actually takes about 100 sec.
 *
 */
public class ExecutorDemo {

    private static final int INTEGERS_TO_VISIT = 100_000; // some will be missed due to randomness
    private static final int QUEUE_CAPACITY = 200_000;

    // This data structure contains state that is read and modified by many
    // threads but ConcurrentHashMap is written specifically to be thread-safe,
    // so that is ok.
    private static final ConcurrentHashMap<Integer, Boolean> alreadyVisited = new ConcurrentHashMap<>();

    // This data structure contains state that is read and modified by many
    // threads, but blocking queue implementations must be thread-safe, so that
    // is ok.
    private static final BlockingQueue<Integer> toDoQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY, false);

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService executorService = new ThreadPoolExecutor(100, 100,
                5L, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY, false),
                new ThreadFactoryBuilder().setNameFormat("worker-%d").setDaemon(true).build());

        long start = System.currentTimeMillis();
        // Seed the to do queue with the first integer to visit
        toDoQueue.add(ThreadLocalRandom.current().nextInt(INTEGERS_TO_VISIT));
        for (;;) {
            Integer i = toDoQueue.poll(10, TimeUnit.SECONDS);
            if (i == null) {
                long elapsedSec = (System.currentTimeMillis() - start) / 1000;
                System.out.println("queue timed out empty, visited " + alreadyVisited.size() +
                        " in " + elapsedSec + " sec");
                continue;
            }
            executorService.execute(() -> visitAndEnqueue(i));
        }
    }

    // This method is intended to be executed asynchronously by an
    // ExecutorService
    private static void visitAndEnqueue(Integer i) {
        try {
            if (!alreadyVisited.containsKey(i)) {
                List<Integer> children = expensiveVisitAndGenerate(i);
                System.out.println("[" + Thread.currentThread().getName() + "] visited " + i);
                alreadyVisited.putIfAbsent(i, true);
                for (Integer child : children) {
                    if (!alreadyVisited.containsKey(child)) {
                        toDoQueue.add(child);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    // Takes an integer that it is "visiting" and returns a random list of
    // integers that need to be "visited" if they haven't already
    private static List<Integer> expensiveVisitAndGenerate(Integer i) throws InterruptedException {

        // This is the "expensive" part of expensiveVisitAndGenerate. Imagine
        // this is a remote call to a very expensive service for example.
        Thread.sleep(100L);

        if (alreadyVisited.containsKey(i)) {
            // I should be the only thread that "visits" this integer
            System.err.println("warning, duplicate work detected");
        }
        int resultSize = 5;
        List<Integer> result = new ArrayList<>(resultSize);
        for (int j = 0; j < resultSize; j++) {
            result.add(ThreadLocalRandom.current().nextInt(INTEGERS_TO_VISIT));
        }
        return result;
    }

}
