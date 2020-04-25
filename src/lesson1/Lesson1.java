package lesson1;

import java.util.*;

public class Lesson1 {
    private static <T> void swapElements(T[] array, int pos1, int pos2) {
        try {
            T element = array[pos1 - 1];
            array[pos1 - 1] = array[pos2 - 1];
            array[pos2 - 1] = element;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Отсутствует элемент массива в позиции: " + (Integer.parseInt(e.getMessage()) + 1));
        }
    }

    private static <T> ArrayList<T> arrayToArrayList(T[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    public static void main(String[] args) {
        Integer[] intArray = {1, 2, 3};
        String[] stringArray = {"one", "two", "three"};

        swapElements(intArray, 1, 2);
        System.out.println(Arrays.toString(intArray));

        swapElements(stringArray, 2, 3);
        System.out.println(Arrays.toString(stringArray));

        ArrayList<Integer> intList = arrayToArrayList(intArray);
        System.out.println(intList);

        swapElements(stringArray, 2, 30);
        System.out.println(Arrays.toString(stringArray));
    }
}
