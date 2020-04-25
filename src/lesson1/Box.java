package lesson1;

import java.util.ArrayList;

public class Box<T extends Fruit> {
    private ArrayList<T> fruitsInBox = new ArrayList<>();

    public Box(T... fruits) {
        for (T fruit : fruits) {
            this.add(fruit);
        }
    }

    public Box(ArrayList<T> fruits) {
        fruitsInBox = fruits;
    }

    public float getWeight() {
        float result = 0.0f;
        for (T fruit : fruitsInBox) {
            result += fruit.getWeight();
        }
        return result;
    }

    public void add(T fruits) {
        fruitsInBox.add(fruits);
    }

    public void moveToAnotherBox(Box<T> box) {
        for (T fruit : fruitsInBox) {
            box.add(fruit);
        }
        fruitsInBox.clear();
    }

    public boolean compare(Box<?> box) {
        return this.getWeight() == box.getWeight();
    }
}
