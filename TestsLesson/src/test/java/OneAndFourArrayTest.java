import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class OneAndFourArrayTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true, new int[]{1, 1, 1, 4, 4, 1, 4, 4}},
                {false, new int[]{1, 1, 1, 1, 1, 1}},
                {false, new int[]{4, 4, 4, 4}},
                {false, new int[]{1, 4, 4, 1, 1, 4, 3}},
        });
    }
    private boolean result;
    private int[] arr;

    public OneAndFourArrayTest (boolean result, int[] arr){
        this.result=result;
        this.arr=arr;
    }

    @Test
    public void containOnlyOneAndFourTest(){
        Assert.assertEquals(result, OneAndFourArrayClass.containOnlyOneAndFour(arr));
    }
}
