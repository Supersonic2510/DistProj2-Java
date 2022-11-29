package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
        
        private static void mergeSort(Integer[] array, int begin, int end){
            if (begin < end){
                int middle = (begin + end) / 2;
                mergeSort(array, begin, middle);
                mergeSort(array, middle + 1, end);
                merge(array, begin, middle, end);
            }
        }
        
        public static void merge(Integer[] array, int begin, int middle, int end){
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
    }

    // Perform Threaded merge sort
    public static void threadedSort(List<Integer> array, int numThreads){

        // Creating a reference
        for (int j = numThreads; j > 0; j--) {
            Integer[] integerArray;
            AtomicInteger counter = new AtomicInteger();
            int chunk = array.size() / j;
            List<List<Integer>> partitionedList = array.stream().collect(Collectors.groupingBy(o -> counter.getAndIncrement() / chunk)).values().stream().toList();

            array.clear();
            MergeThread[] mergeThreads = new MergeThread[partitionedList.size()];

            for (int i = 0; i < partitionedList.size(); i++) {
                mergeThreads[i] = new MergeThread(partitionedList.get(i));
                mergeThreads[i].start();
            }

            for (int i = 0; i < partitionedList.size(); i++) {
                try {
                    mergeThreads[i].join();
                    array.addAll(partitionedList.get(i));
                } catch (InterruptedException e) {
                    System.err.println("Couldn't join thread");
                }
            }
        }

        System.out.println(CheckSorted.arraySortedOrNot(array, array.size()));
    }
}
