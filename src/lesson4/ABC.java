package lesson4;

public class ABC {
    private static final int LETTER_COUNT = 5;
    private volatile char currentLetter = 'A';
    private static final ABC abc = new ABC();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            abc.printA();
        });
        Thread t2 = new Thread(() -> {
            abc.printB();
        });
        Thread t3 = new Thread(() -> {
            abc.printC();
        });
        t1.start();
        t2.start();
        t3.start();
    }

    public void printA() {
        synchronized (abc) {
            try {
                for (int i = 0; i < LETTER_COUNT; i++) {
                    while (currentLetter != 'A') {
                        abc.wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    abc.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printB() {
        synchronized (abc) {
            try {
                for (int i = 0; i < LETTER_COUNT; i++) {
                    while (currentLetter != 'B') {
                        abc.wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    abc.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printC() {
        synchronized (abc) {
            try {
                for (int i = 0; i < LETTER_COUNT; i++) {
                    while (currentLetter != 'C') {
                        abc.wait();
                    }
                    System.out.print("C");
                    currentLetter = 'A';
                    abc.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
