package lesson6;

public class Main {

    public int[] arrayProcessing1(int[] array) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == 4) {
                int[] newArray = new int[array.length - 1 - i];
                System.arraycopy(array, i + 1, newArray, 0, array.length - 1 - i);
                return newArray;
            }
        }
        throw new RuntimeException("Incorrect values: the number 4 is missing in the array.");
    }

    public boolean arrayProcessing2(int[] array) {
        int countOne = 0;
        int countFour = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) {
                countOne++;
            } else if (array[i] == 4) {
                countFour++;
            } else {
                return false;
            }
        }
        if (countOne == 0 || countFour == 0) return false;
        return true;
    }

//    public static void main(String[] args) {
//        System.out.println(Arrays.toString(arrayProcessing1(new int[]{})));
//
//        System.out.println(arrayProcessing2(new int[]{4, 4, 4, 5}));
//    }
}
