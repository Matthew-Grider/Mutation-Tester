
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Request;
import io.reactivex.*;

public class Driver {

    public static void main(String[] args)
    {
        File f = new File("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\");


        System.out.println(f.getPath());

        try {
            URL[] classpath = {f.toURI().toURL()};
            URLClassLoader urlcl = new URLClassLoader(classpath);
            Class c = urlcl.loadClass("io.reactivex.BackpressureEnumTest");
            Class<?> cl = c;

            System.out.println(c);

            /*MutationTester testRunner1 = new MutationTester();

            testRunner1.run(cl);*/

            List<Class<?>> classList = new ArrayList<Class<?>>();
            classList.add(cl);

            List<Method> testmethods = Utilities.findTests(classList);

            for(Method meth : testmethods)
            {
                System.out.println(meth.getName());
            }


        } catch (ClassNotFoundException e)
        {
            System.out.println(e.getException() + " : couldn't find the class dumby");
            System.out.println(e);
        } catch(Exception e)
        {
            System.out.println(e);
        }
    }

    private static Class<?> parseSource(File source)
    {
        //takes source code and returns the clsses to be used with the mutationTester
        return null;
    }
}
