public class OneAndFourArrayClass {
    public static void main(String[] args) {
        int[] arr1 = {1,1,4,1,4,4,1};
        System.out.println(containOnlyOneAndFour(arr1));
        int[] arr2 = {1,1,4,1,4,3,4,1};
        System.out.println(containOnlyOneAndFour(arr2));
        int[] arr3 = {4,4,4,4};
        System.out.println(containOnlyOneAndFour(arr3));
    }

    public static boolean containOnlyOneAndFour(int[] arr){
        boolean hasOne = false, hasFour = false;
        for (int i = 0; i < arr.length; i++) {
            if ((arr[i]!=1)&(arr[i]!=4)) return false;
            if (arr[i]==1) hasOne=true;
            if (arr[i]==4) hasFour=true;
        }
        return hasOne&hasFour;
    }
}
