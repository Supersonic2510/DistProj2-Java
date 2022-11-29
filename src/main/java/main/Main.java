package main;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import thread.BeginThread;
import thread.EndThread;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        LinkedList<Integer> linkedList = new LinkedList<>();
        Random random = new Random();

        while (random.nextInt(0, 1000) % 1000 != 0) {
            linkedList.add(random.nextInt(0, 100));
        }


        // LinkedList Parallel Search
        //parallelSearch(random.nextInt(0, 100), linkedList);

        // Array split search
        //parallelSearch(random.nextInt(0, 100), linkedList, random.nextInt(0, 100));

        MergeSort.threadedSort(linkedList, random.nextInt(1, 100));
    }

    public static void parallelSearch(int toSearch, List<Integer> linkedList) {
        BeginThread beginThread = new BeginThread(linkedList, toSearch);
        EndThread endThread = new EndThread(linkedList, toSearch);
        int value = -1;

        beginThread.start();
        endThread.start();

        try {
            beginThread.join();
            endThread.join();

            Triplet<Boolean, Instant, Instant> beginTuple = beginThread.getResult();
            Triplet<Boolean, Instant, Instant> endTuple = endThread.getResult();
            Duration beginDuration = Duration.between(beginTuple.getValue1(), beginTuple.getValue2());
            Duration endDuration = Duration.between(endTuple.getValue1(), endTuple.getValue2());

            if (beginTuple.getValue0() && endTuple.getValue0()) {
                if (beginDuration.compareTo(endDuration) >= 0) {
                    System.out.println("Find " + toSearch + " from start and needed: " + beginDuration.toNanosPart() + "ns");
                }else {
                    System.out.println("Find " + toSearch + " from end and needed: " + endDuration.toNanosPart() + "ns");
                }
            }else {
                System.out.println("No value found");
            }

        } catch (InterruptedException e) {
            System.err.println("Couldn't join thread");
        }
    }

    public static int parallelSearch(int toSearch, List<Integer> array, int numThreads) {

        AtomicInteger counter = new AtomicInteger();
        List<List<Integer>> partitionedList = array.stream().collect(Collectors.groupingBy(o -> counter.getAndIncrement() / numThreads)).values().stream().toList();
        Pair<BeginThread[], Integer[]> resultListTuple = new Pair<>(new BeginThread[partitionedList.size()], new Integer[partitionedList.size()]);

        for (int i = 0; i < partitionedList.size(); i++) {
            resultListTuple.getValue0()[i] = new BeginThread(partitionedList.get(i), toSearch);
            resultListTuple.getValue0()[i].start();
        }

        for (int i = 0; i < partitionedList.size(); i++) {
            try {
                resultListTuple.getValue0()[i].join();
                resultListTuple.getValue1()[i] = resultListTuple.getValue0()[i].getPosition();
            } catch (InterruptedException e) {
                System.err.println("Couldn't join thread");
            }
        }

        for (int i = 0; i < partitionedList.size(); i++) {
            System.out.println("The row #" + (i + 1) + " has value " + toSearch + " in position: " + resultListTuple.getValue1()[i]);
        }

        return 0;
    }


}