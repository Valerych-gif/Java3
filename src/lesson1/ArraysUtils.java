package lesson1;

import java.util.ArrayList;
import java.util.Arrays;

public class ArraysUtils<T> {
    private T[] elements;

    public ArraysUtils(T[] elements) {
        this.elements = elements;
    }

    public void change(int element1Number, int element2Number){
        if (element1Number>0&&element1Number<elements.length&&element2Number>0&&element2Number<elements.length){
            T tmp;
            tmp = elements[element1Number];
            elements[element1Number] = elements[element2Number];
            elements[element2Number] = tmp;
        } else {
            System.out.println("Wrong indexes of elements");
        }
    }

    public ArrayList<T> changeArrayToArrayList(){
        return new ArrayList<T>(Arrays.asList(elements));
    }

    @Override
    public String toString() {
        return "arrayElementChanger{" +
                "elements=" + Arrays.toString(elements) +
                '}';
    }
}
