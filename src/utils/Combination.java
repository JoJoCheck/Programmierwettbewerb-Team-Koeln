package utils;

public class Combination {
    static void printArr(int[] a, int n) {
        for (int i = 0; i < n; i++)
            System.out.print(a[i] + " ");
        System.out.println();
    }

    static void heapPermutation(int[] a, int size, int n) {
        if (size == 1)
            printArr(a, n);

        for (int i = 0; i < size; i++) {
            heapPermutation(a, size - 1, n);

            int temp;
            if (size % 2 == 1) {
                temp = a[0];
                a[0] = a[size - 1];
            } else {
                temp = a[i];
                a[i] = a[size - 1];
            }
            a[size - 1] = temp;
        }
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 3};
        heapPermutation(a, a.length, a.length);
    }
}
