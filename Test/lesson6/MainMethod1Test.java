package lesson6;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MainMethod1Test {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new int[]{8, 6},       new int[]{1, 3, 4, 8, 6}},
                {new int[]{},           new int[]{1, 1, 2, 5, 4}},
                {new int[]{9},          new int[]{3, 0, 4, 4, 9}},
                {new int[]{6, 8, 0, 1}, new int[]{4, 6, 8, 0, 1}}
        });
    }

    private static Main main;
    private int[] expected, x;

    public MainMethod1Test(int[] expected, int[] x) {
        this.expected = expected;
        this.x = x;
    }

    @BeforeClass
    public static void globalInit() {
        main = new Main();
    }

    @Test
    public void arrayProcessing1Test() {
        Assert.assertArrayEquals(expected, main.arrayProcessing1(x));
    }
}
