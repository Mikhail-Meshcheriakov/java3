package lesson1;

import java.util.ArrayList;
import java.util.Arrays;

public class Box<T extends Fruit> {
    private ArrayList<T> fruitsInBox = new ArrayList<>();

    public Box(T... fruits) {
        this.fruitsInBox.addAll(Arrays.asList(fruits));
    }

    public Box(ArrayList<T> fruits) {
        this.fruitsInBox = fruits;
    }

    public float getWeight() {
        float result = 0.0f;
        for (T fruit : fruitsInBox) {
            result += fruit.getWeight();
        }
        return result;
    }

    public void add(T fruits) {
        this.fruitsInBox.add(fruits);
    }

    public void moveToAnotherBox(Box<T> box) {
        if (box.fruitsInBox.isEmpty()) {
            return;
        }
        if (box.equals(this)) {
            return;
        }
        box.fruitsInBox.addAll(this.fruitsInBox);
        fruitsInBox.clear();
    }

    public boolean compare(Box<?> box) {
        return Math.abs(this.getWeight() - box.getWeight()) < 0.0001;
    }
}
