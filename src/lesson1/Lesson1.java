package lesson1;

import java.util.Arrays;

public class Lesson1 {
    public static void main(String[] args) {
        task1();
        System.out.println("-------------");
        task2();
        System.out.println("-------------");
        task3();
        System.out.println("-------------");
    }

    private static void task1() {
        String[] array1= {"Огонь", "Вода", "Воздух", "Земля"};
        ArraysUtils<String> changer = new ArraysUtils<>(array1);
        System.out.println(changer);
        changer.change(1,2);
        System.out.println(changer);
    }

    private static void task2() {
        Character[] array2 = {'A', 'B', 'C', 'D', 'E'};
        ArraysUtils<Character> changer = new ArraysUtils<>(array2);
        System.out.println(changer);
        System.out.println("4th element of ArrayList is " + changer.changeArrayToArrayList().get(3));
    }

    private static void task3() {
        FruitBox<Apple> box1 = new FruitBox<>();
        box1.add(new Apple(2.0f));
        box1.add(new Apple());
        box1.add(new Apple(1.2f));
        box1.add(new Apple());
        System.out.println("Weight of box1 is " + box1.getWeight());
        FruitBox<Orange> box2 = new FruitBox<>();
        box2.add(new Orange());
        box2.add(new Orange(1.4f));
        box2.add(new Orange());
        box2.add(new Orange(1.6f));
        System.out.println("Weight of box2 is " + box2.getWeight());
        FruitBox<Orange> box3 = new FruitBox<>();
        box3.add(new Orange());
        box3.add(new Orange());
        box3.add(new Orange(2.2f));
        System.out.println("Weight of box3 is " + box3.getWeight());
        System.out.println("Is box1 and box2 equal? " + box1.compare(box2));
        System.out.println("Is box1 and box3 equal? " + box1.compare(box3));
        System.out.println("We get all fruits from box3 and put its into box2");
        box2.getAllFruits(box3);
        System.out.println("Weight of box2 is " + box2.getWeight());
        System.out.println("Weight of box3 is " + box3.getWeight());
    }

}
