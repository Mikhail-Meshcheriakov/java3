package lesson6;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MainMethod2Test {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true,  new int[]{1, 1, 4, 1, 4}},
                {false, new int[]{1, 1, 1, 1, 1}},
                {false, new int[]{1, 4, 1, 4, 5}},
                {false, new int[]{4, 4, 4, 4, 4}}
        });
    }

    private static Main main;
    private boolean expected;
    private int[] x;

    public MainMethod2Test(boolean expected, int[] x) {
        this.expected = expected;
        this.x = x;
    }

    @BeforeClass
    public static void globalInit() {
        main = new Main();
    }

    @Test
    public void arrayProcessing1Test() {
        Assert.assertEquals(expected, main.arrayProcessing2(x));
    }
}
