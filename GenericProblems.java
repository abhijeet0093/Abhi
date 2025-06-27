
public class GenericProblems {

    // 1. Count Occurrence of Element
    public static <T> int countOccurrences(T[] arr, T target) {
        int count = 0;
        for (T item : arr) {
            if (item.equals(target)) {
                count++;

            }
        }
        return count;
    }

    // 2. Linear Search
    public static <T> int linearSearch(T[] arr, T target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    // 3. Find Min and Max
    public static <T extends Comparable<T>> T findMin(T[] arr) {
        T min = arr[0];
        for (T item : arr) {
            if (item.compareTo(min) < 0) {
                min = item;
            }
        }
        return min;
    }

    public static <T extends Comparable<T>> T findMax(T[] arr) {
        T max = arr[0];
        for (T item : arr) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }

    // 4. Check if Sorted (non-descending)
    public static <T extends Comparable<T>> boolean isSorted(T[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i].compareTo(arr[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static <T> T findMiddle(T[] arr) {
        int mid = arr.length / 2;
        return arr[mid];
    }

    public static <T> void checkTwoElements(T[] arr, T a, T b) {
        int indexA = -1, indexB = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(a)) {
                indexA = i;
            }
            if (arr[i].equals(b)) {
                indexB = i;
            }
        }

        if (indexA != -1 && indexB != -1) {
            System.out.println(a + " found at index " + indexA);
            System.out.println(b + " found at index " + indexB);
        } else {
            System.out.println("One or both elements not found.");
        }
    }

    public static void findSubstring(String str, String subStr) {
        int count = 0;
        int start = 0;
        while ((start = str.indexOf(subStr, start)) != -1) {
            System.out.println("Found at index: " + start + " to " + (start + subStr.length() - 1));
            count++;
            start += subStr.length();
        }
        System.out.println("Total occurrences: " + count);
    }

    public static void main(String[] args) {
        Integer[] nums = {1, 2, 3, 4, 5, 6};
        String[] words = {"a", "b", "a", "c"};

        System.out.println("Count: " + countOccurrences(nums, 2));
        System.out.println("Index: " + linearSearch(nums, 3));
        System.out.println("Min: " + findMin(nums));
        System.out.println("Max: " + findMax(nums));
        System.out.println("Is Sorted: " + isSorted(nums));
        System.out.println("Middle: " + findMiddle(words));
        checkTwoElements(words, "a", "c");

        String sentence = "Solapur Education Society Solapur Maharastra";
        findSubstring(sentence, "Solapur");
    }
}
