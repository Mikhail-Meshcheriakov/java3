package lesson7;

public class ClassTest {

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("beforeSuite");
    }

    @BeforeSuite
    public static void beforeSuite2() {
        System.out.println("beforeSuite2");
    }

    @Test(priority = 3)
    public void calculation1() {
        System.out.println("calculation1");
    }

    @Test(priority = 4)
    public void calculation2() {
        System.out.println("calculation2");
    }

    @Test(priority = 1)
    public void calculation3() {
        System.out.println("calculation3");
    }

    @Test(priority = 2)
    public void calculation4() {
        System.out.println("calculation4");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("afterSuite");
    }
}
