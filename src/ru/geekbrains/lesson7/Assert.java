package ru.geekbrains.lesson7;

public class Assert {

    private Assert(){

    }

    public static void assertBoolean(boolean expected, boolean condition){
        if (expected==condition) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail!");
        }
    }

    public static void assertEquals(Object expected, Object condition){
        if (expected.equals(condition)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail!");
        }
    }
}
