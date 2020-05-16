package ru.geekbrains.lesson7;

import ru.geekbrains.lesson7.annotations.AfterSuite;
import ru.geekbrains.lesson7.annotations.BeforeSuite;
import ru.geekbrains.lesson7.annotations.Test;

public class MyTests {
    ProgramForTesting program;

    @BeforeSuite
    public void initTest(){
        System.out.println("Preparing for testing...");
        program = new ProgramForTesting();
    }

    @Test
    public void addTest(){
        System.out.println("Priority 5 (default). ");
        Assert.assertEquals(4, program.add(2,2));
    }

    @Test (priority = 7)
    public void isAMoreThenBTest(){
        System.out.println("Priority 7. ");
        Assert.assertBoolean(false, program.iaAMoreThenB(5, 6));
    }

    @Test (priority = 3)
    public void isAMoreThenBTestFail(){ // Этот тест провалится
        System.out.println("Priority 3. This test must fail");
        Assert.assertBoolean(true, program.iaAMoreThenB(5, 6));
    }

    @AfterSuite
    public void stopTests(){
        System.out.println("That's all, guys!");
    }

}
