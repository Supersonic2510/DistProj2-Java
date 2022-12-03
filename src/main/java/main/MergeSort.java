package main;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MergeSort {

    // Custom Thread class with constructors
    private static class MergeThread extends Thread {
        
        private List<Integer> list;

        private Integer[] integers;

        public MergeThread(List<Integer> list) {
            this.list = list;
        }

        public List<Integer> getList() {
            return list;
        }

        @Override
        public void run() {
            integers = Arrays.copyOf(list.toArray(), list.size(), Integer[].class);
            mergeSort(integers, 0, list.size() - 1);
            IntStream.range(0, list.size()).forEachOrdered(i -> list.set(i, integers[i]));
        }
    }

    private static void mergeSort(Integer[] array, int begin, int end){
        if (begin < end){
            int middle = (begin + end) / 2;
            mergeSort(array, begin, middle);
            mergeSort(array, middle + 1, end);
            merge(array, begin, middle, end);
        }
    }

    private static void merge(Integer[] array, int begin, int middle, int end){
        Integer[] temp = new Integer[(end - begin) + 1];

        int i = begin, j = middle + 1;
        int k = 0;

        while(i <= middle && j <= end){
            if (array[i] <= array[j]){
                temp[k] = array[i];
                i++;
            }else{
                temp[k] = array[j];
                j++;
            }
            k++;
        }

        while(i <= middle){
            temp[k] = array[i];
            i++;
            k++;
        }

        while(j <= end){
            temp[k] = array[j];
            j++;
            k++;
        }

        for( i = begin, k = 0; i <= end; i++, k++){
            array[i] = temp[k];
        }
    }

    public static int[] splitIntoParts(int whole, int parts) {
        int[] arr = new int[parts];
        for (int i = 0; i < arr.length; i++)
            whole -= arr[i] = (whole + parts - i - 1) / (parts - i);
        return arr;
    }


    // Perform Threaded merge sort
    public static void threadedSort(List<Integer> array, int numThreads){

        Instant start = null;
        Instant finish = null;

        // Creating a reference
        List<List<Integer>> partitionedList = new ArrayList<>();
        int[] chunkArray = splitIntoParts(array.size(), numThreads);

        for (int i = 0, l = 0; i < array.size(); l++) {
            List<Integer> list = new ArrayList<>();
            for (int k = 0; i < array.size() && k < chunkArray[l]; k++, i++) {
                list.add(array.get(i));
            }
            partitionedList.add(list);
        }

        array.clear();
        MergeThread[] mergeThreads = new MergeThread[partitionedList.size()];

        for (int i = 0; i < partitionedList.size(); i++) {
            mergeThreads[i] = new MergeThread(partitionedList.get(i));
            if (i == 0) {
                start = Instant.now();
            }
            mergeThreads[i].start();
        }

        Integer[] integers;
        for (int i = 0; i < partitionedList.size(); i++) {
            try {
                mergeThreads[i].join();
                array.addAll(partitionedList.get(i));
            } catch (InterruptedException e) {
                System.err.println("Couldn't join thread");
            }
            if (i != 0) {
                integers = Arrays.copyOf(array.toArray(), array.size(), Integer[].class);
                merge(integers, 0, array.size() - chunkArray[i] - 1, array.size() - 1);
                Integer[] finalIntegers = integers;
                IntStream.range(0, array.size()).forEachOrdered(n -> array.set(n, finalIntegers[n]));
            }
        }

        finish = Instant.now();


        Duration duration;
        if (start == null || finish == null) {
            duration = Duration.ZERO;
        }else {
            duration = Duration.between(start, finish);
        }

        System.out.println("List sorted with "+ numThreads + " threads: " + CheckSorted.arraySortedOrNot(array, array.size())
                + " and duration: " + duration.toNanos() + "ns");
    }

    public static void singleThreadSort(List<Integer> array){
        MergeThread mergeThread = new MergeThread(array);

        Instant start = Instant.now();
        Instant finish;

        mergeThread.start();


        try {
            mergeThread.join();
            finish = Instant.now();
        } catch (InterruptedException e) {
            System.err.println("Couldn't join thread");
            finish = Instant.now();
        }

        System.out.println("List sorted with single thread: " + CheckSorted.arraySortedOrNot(array, array.size())
                + " and duration: " + Duration.between(start, finish).toNanos() + "ns");
    }
}
