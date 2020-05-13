import java.util.Arrays;

public class ArrayCutterClass {
    public static void main(String[] args) {

        int[] arr1 = {1, 2, 3, 4, 5};
        int[] newArr1 = arrayCutter(arr1);
        System.out.println(Arrays.toString(newArr1));

        int[] arr2 = {1, 2, 4, 3, 4, 3, 5, 7};
        int[] newArr2 = arrayCutter(arr2);
        System.out.println(Arrays.toString(newArr2));

        int[] arr3 = {1, 2, 3, 5, 6};
        int[] newArr3 = arrayCutter(arr3);
        System.out.println(Arrays.toString(newArr3));
    }

    public static int[] arrayCutter(int[] arr) {
        int index = -1;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 4) {
                index = i;
            }
        }

        if (index == -1) {
            throw new RuntimeException();
        } else if (index != arr.length) {
            return Arrays.copyOfRange(arr, index + 1, arr.length);
        } else {
            return new int[0];
        }
    }
}
