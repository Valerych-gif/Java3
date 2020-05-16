package ru.geekbrains.lesson7;

import ru.geekbrains.lesson7.annotations.AfterSuite;
import ru.geekbrains.lesson7.annotations.BeforeSuite;
import ru.geekbrains.lesson7.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class TestsEngine {
    static MyTests myTests;
    static Method[] methods;
    static int beforeSuiteCounter=0;
    static int afterSuiteCounter=0;

    public static void main(String[] args) {
        Class myTestsClass = MyTests.class;
        start(myTestsClass);
    }

    private static void start(Class myTestsClass) {

        methods = myTestsClass.getDeclaredMethods();
        try {
            myTests = (MyTests)myTestsClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (checkBeforeAndAfterAnnotationsCount()){
            runBeforeSuiteMethod();
            runTestMethods();
            runAfterSuiteMethod();
        }
    }

    private static void runBeforeSuiteMethod() {
        for (Method method : methods) {
            runTestByAnnotation(method, BeforeSuite.class);
        }
    }

    private static void runTestMethods() {
        Method[] testMethods = new Method[methods.length-beforeSuiteCounter-afterSuiteCounter];
        int i=0;
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)){
                testMethods[i]=method;
                i++;
            }
        }
        Arrays.sort(testMethods, Comparator.comparingInt(o -> -(o.getDeclaredAnnotation(Test.class).priority())));
        for (Method method : testMethods) {
            runTestByAnnotation(method, Test.class);
        }
    }

    private static void runAfterSuiteMethod() {
        for (Method method : methods) {
            runTestByAnnotation(method, AfterSuite.class);
        }
    }

    private static void runTestByAnnotation(Method method, Class annotation) {
        if (method.isAnnotationPresent(annotation)){
            try {
                method.invoke(myTests);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkBeforeAndAfterAnnotationsCount() {

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)){
                beforeSuiteCounter++;
            }
            if (method.isAnnotationPresent(AfterSuite.class)){
                afterSuiteCounter++;
            }
            if (beforeSuiteCounter>1){
                try {
                    throw new RuntimeException("More then one @BeforeSuite annotation");
                }catch (RuntimeException ex){
                    ex.printStackTrace();
                    return false;
                }
            }
            if (afterSuiteCounter>1){
                try {
                    throw new RuntimeException("More then one @AfterSuite annotation");
                }catch (RuntimeException ex){
                    ex.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

}
