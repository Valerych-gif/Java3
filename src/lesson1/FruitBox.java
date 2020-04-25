package lesson1;

import java.util.ArrayList;
import java.util.Collection;

public class FruitBox<T> {
    private ArrayList<Fruit> fruits;

    public FruitBox(ArrayList<T> fruits) {
        this.fruits = (ArrayList<Fruit>) fruits;
    }

    public FruitBox() {
        this.fruits = new ArrayList<>();
    }

    public void add (T fruit){
        fruits.add((Fruit) fruit);
    }

    public float getWeight(){
        float boxWeight=0;
        for (Fruit fruit : fruits) {
            boxWeight += fruit.getWeight();
        }
        return boxWeight;
    }

    public void getAllFruits (FruitBox<T> anotherBox){
        fruits.addAll(new ArrayList<>(anotherBox.fruits));
        anotherBox.fruits.clear();
    }

    public boolean compare(FruitBox<? extends Fruit> anotherBox){
        return (Math.abs(getWeight()-anotherBox.getWeight())<0.0001f);
    }
}
