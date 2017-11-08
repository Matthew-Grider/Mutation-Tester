import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.*;

public class Utilities {

    public static List<Method> findAllTestMethods(final Class<?> type)
    {
        List<Method> tests = new ArrayList<Method>();
        Class<?> walker = type;

            final List<Method> objectsMethods = new ArrayList<Method>(Arrays.asList(walker.getDeclaredMethods()));
            for(int i = 0; i < objectsMethods.size(); i++)
            {
                if(objectsMethods.get(i).isAnnotationPresent(Test.class))
                {
                    tests.add(objectsMethods.get(i));
                }
            }
        return tests;
    }

    public static List<Method> findTests(List<Class<?>> classes)
    {
        List<Method> tests = new ArrayList<Method>();
        for(int i = 0 ; i < classes.size(); i++)
        {
            tests.addAll(Utilities.findAllTestMethods(classes.get(i)));
        }
        return tests;
    }


}
