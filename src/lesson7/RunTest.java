package lesson7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;

public class RunTest {

    public static void main(String[] args) {
        ClassTest test = new ClassTest();
        try {
            start(test.getClass());
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void start(Class testClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Method[] methods = testClass.getMethods();
        int beforeCount = 0;
        int afterCount = 0;
        Method beforeMethod = null;
        Method afterMethod = null;
        ArrayList<Method> arrayList = new ArrayList<>();

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                beforeMethod = method;
                beforeCount++;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                afterMethod = method;
                afterCount++;
            }
            if (method.isAnnotationPresent(Test.class)) {
                arrayList.add(method);
            }
        }

        if (beforeCount > 1 && afterCount > 1) {
            throw new RuntimeException("Exceeded number of methods with annotations \"@BeforeSuite\" and \"@AfterSuite\"!");
        } else if (beforeCount > 1) {
            throw new RuntimeException("Exceeded number of methods with annotation \"@BeforeSuite\"!");
        } else if (afterCount > 1) {
            throw new RuntimeException("Exceeded number of methods with annotation \"@AfterSuite\"!");
        }

        if (arrayList.size() == 0) throw new RuntimeException("Missing methods with annotation \"@Test\"!");

        arrayList.sort(Comparator.comparingInt(o -> o.getAnnotation(Test.class).priority()));

        Object object = testClass.newInstance();
        if (beforeMethod != null) beforeMethod.invoke(object);
        for (Method method : arrayList) method.invoke(object);
        if (afterMethod != null) afterMethod.invoke(object);
    }
}
