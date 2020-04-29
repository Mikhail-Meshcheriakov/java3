package lesson1;

import java.util.ArrayList;

public class ExampleBox {
    public static void main(String[] args) {
        Box<Apple> appleBox1 = new Box<>();
        System.out.println(appleBox1.getWeight());

        appleBox1.add(new Apple());
        System.out.println(appleBox1.getWeight());

        ArrayList<Orange> orangeList = new ArrayList<>();
        orangeList.add(new Orange());
        Box<Orange> orangeBox1 = new Box<>(orangeList);
        System.out.println(orangeBox1.getWeight());

        System.out.println(appleBox1.compare(orangeBox1));

        appleBox1.add(new Apple());
        appleBox1.add(new Apple());
        orangeBox1.add(new Orange());

        System.out.println(appleBox1.compare(orangeBox1));

        Box<Fruit> fruitBox = new Box<>();
        fruitBox.add(new Fruit(20));
        fruitBox.add(new Orange());
        fruitBox.add(new Apple());
        System.out.println(fruitBox.getWeight());

        Box<Apple> appleBox2 = new Box<>();
        appleBox2.add(new Apple());
        System.out.println(appleBox1.getWeight() + " | " + appleBox2.getWeight());
        appleBox1.moveToAnotherBox(appleBox2);
        System.out.println(appleBox1.getWeight() + " | " + appleBox2.getWeight());
    }
}
