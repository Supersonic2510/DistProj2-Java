package main;

import java.util.List;

class CheckSorted {
    // *** Retrieved from GeekToGeeks to test data

    // Function that returns 0 if a pair
    // is found unsorted
    public static boolean arraySortedOrNot(List<Integer> arr, int n) {

        // Array has one or no element
        if (n == 0 || n == 1)
            return true;

        for (int i = 1; i < n; i++)

            // Unsorted pair found
            if (arr.get(i - 1) > arr.get(i))
                return false;

        // No unsorted pair found
        return true;
    }
}
