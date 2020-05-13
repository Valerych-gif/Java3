import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ArrayCutterTest {

    static ArrayCutterClass arrayCutter;
    @BeforeClass
    public static void globalInit() {
        arrayCutter = new ArrayCutterClass();
    }

    @Test (expected = RuntimeException.class)
    public void arrayCutterExceptionTest(){
        arrayCutter.arrayCutter(new int[]{1, 2, 3, 5});
    }

    @Test
    public void arrayCutterSuccessRunTest(){
        Assert.assertArrayEquals(new int[] {3, 5, 7}, arrayCutter.arrayCutter(new int[]{1, 2, 4, 3, 4, 3, 5, 7}));
    }

    @Test
    public void arrayCutterFourIsLastTest(){
        Assert.assertArrayEquals(new int[] {}, arrayCutter.arrayCutter(new int[]{1, 2, 4, 3, 4}));
    }

}
