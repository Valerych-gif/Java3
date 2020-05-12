package lesson5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainClass {
    public static final int CARS_COUNT = 4;
    final static Semaphore tunnelSemaphore = new Semaphore(CARS_COUNT/2);
    final static CountDownLatch raceStartLocker = new CountDownLatch(CARS_COUNT);
    final static CountDownLatch raceFinishLocker = new CountDownLatch(CARS_COUNT);
    static AtomicBoolean isWinner = new AtomicBoolean(true);

    public static CountDownLatch getRaceStartLocker() {
        return raceStartLocker;
    }

    public static CountDownLatch getRaceFinishLocker() {
        return raceFinishLocker;
    }

    public static Semaphore getTunnelSemaphore() {
        return tunnelSemaphore;
    }

    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];

        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        try {
            raceStartLocker.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        try {
            raceFinishLocker.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }

}