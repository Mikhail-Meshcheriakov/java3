package lesson6;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MainMethod1ExceptionTest {
    @Parameterized.Parameters
    public static Collection<Object> data() {
        return Arrays.asList(
                new int[]{1, 3, 9, 8, 6},
                new int[]{1, 1, 2, 5, 0},
                new int[]{3, 0, 8, 2, 9},
                new int[]{9, 6, 8, 0, 1}
        );
    }


    private static Main main;
    private int[] x;

    public MainMethod1ExceptionTest(int[] expected) {
        this.x = expected;
    }

    @BeforeClass
    public static void globalInit() {
        main = new Main();
    }

    @Test(expected = RuntimeException.class)
    public void arrayProcessing1Test() {
        main.arrayProcessing1(x);
    }
}