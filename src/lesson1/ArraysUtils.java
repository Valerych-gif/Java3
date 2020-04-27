package lesson1;

import java.util.ArrayList;
import java.util.Arrays;

public class ArraysUtils {

    public static void change(Object[] elements, int element1Number, int element2Number){
        if (element1Number>=0&&element1Number<elements.length&&element2Number>=0&&element2Number<elements.length){
            Object tmp = elements[element1Number];
            elements[element1Number] = elements[element2Number];
            elements[element2Number] = tmp;
        } else {
            System.out.println("Wrong indexes of elements");
        }
    }

    public static <T> ArrayList<T> changeArrayToArrayList(T[] elements){
        return new ArrayList<T>(Arrays.asList(elements));
    }

}
