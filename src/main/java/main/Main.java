package main;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import thread.BeginTheadByCopy;
import thread.BeginThread;
import thread.EndThread;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        LinkedList<Integer> linkedList = new LinkedList<>();
        Random random = new Random();

        while (linkedList.size() < 100000) {
            linkedList.add(random.nextInt(0, 100));
        }


        // LinkedList Parallel Search
        //parallelSearch(random.nextInt(0, 100), linkedList);

        // Array split search
        //parallelSearch(random.nextInt(0, 100), linkedList, 5);
        //parallelSearchCopy(random.nextInt(0, 100), linkedList, 5);

        //MergeSort.threadedSort(linkedList, 5);
        //MergeSort.singleThreadSort(linkedList);
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
        Instant start, end;
        List<List<Integer>> partitionedList = new ArrayList<>();
        int[] chunkArray = MergeSort.splitIntoParts(array.size(), numThreads);

        for (int i = 0, l = 0; i < array.size(); l++) {
            List<Integer> list = new ArrayList<>();
            for (int k = 0; i < array.size() && k < chunkArray[l]; k++, i++) {
                list.add(array.get(i));
            }
            partitionedList.add(list);
        }

        Pair<BeginThread[], Integer[]> resultListTuple = new Pair<>(new BeginThread[partitionedList.size()], new Integer[partitionedList.size()]);

        for (int i = 0; i < partitionedList.size(); i++) {
            resultListTuple.getValue0()[i] = new BeginThread(partitionedList.get(i), toSearch);
        }

        start = Instant.now();
        for (int i = 0; i < partitionedList.size(); i++) {
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

        end = Instant.now();

        for (int i = 0; i < partitionedList.size(); i++) {
            System.out.println("The row #" + (i + 1) + " has value " + toSearch + " in position: " + resultListTuple.getValue1()[i]);
        }

        System.out.println("Duration reference: " + Duration.between(start, end).toNanos() + "ns");

        return 0;
    }

    public static int parallelSearchCopy(int toSearch, List<Integer> array, int numThreads) {
        Instant start, end;
        List<List<Integer>> partitionedList = new ArrayList<>();
        int[] chunkArray = MergeSort.splitIntoParts(array.size(), numThreads);

        for (int i = 0, l = 0; i < array.size(); l++) {
            List<Integer> list = new ArrayList<>();
            for (int k = 0; i < array.size() && k < chunkArray[l]; k++, i++) {
                list.add(array.get(i));
            }
            partitionedList.add(list);
        }
        Pair<BeginTheadByCopy[], Integer[]> resultListTuple = new Pair<>(new BeginTheadByCopy[partitionedList.size()], new Integer[partitionedList.size()]);

        for (int i = 0; i < partitionedList.size(); i++) {
            resultListTuple.getValue0()[i] = new BeginTheadByCopy(partitionedList.get(i), toSearch);
        }

        start = Instant.now();
        for (int i = 0; i < partitionedList.size(); i++) {
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

        end = Instant.now();

        for (int i = 0; i < partitionedList.size(); i++) {
            System.out.println("The row #" + (i + 1) + " has value " + toSearch + " in position: " + resultListTuple.getValue1()[i]);
        }

        System.out.println("Duration copy: " + Duration.between(start, end).toNanos() + "ns");

        return 0;
    }


}