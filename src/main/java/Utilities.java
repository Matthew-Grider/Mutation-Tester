import java.io.File;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import static java.lang.System.exit;

public class Utilities {

    /**
     * Runs tests on loaded test classes and then returns the results
     * return list of Results from tests
     * */
    public static List<Result> runTest(String testPath)
    {
        /*String[] temp = dirPath.split("production");
        String testPath = temp[0] + "test" + temp[1];*/
        List<Class<?>> run = new ArrayList<>();

        File testDir = new File(testPath);

        List<File> tests = new ArrayList<>(FileUtils.listFiles(testDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));

        try {
            URL[] classpath = {testDir.toURI().toURL()};
            URLClassLoader urlcl = new URLClassLoader(classpath);

            //for(File test : tests) {
            for(int i = tests.size() - 1; i >= 0; i--) {
                File test = tests.get(i);
                String[] temp = {};
                String holder = test.getAbsolutePath();
                if(holder.contains("\\")) {
                    temp = test.getAbsolutePath().split("classes\\\\");
                    temp = (temp[1].replaceAll("\\\\", ".")).split(".class");
                } else if(holder.contains("/"))
                {
                    temp = test.getAbsolutePath().split("classes/");
                    temp = (temp[1].replaceAll("/", ".")).split(".class");
                } else
                {
                    System.out.println("THE PATHS ARE MEST UP, DOUBLE CHECK THE SLASHES AND MAKE SURE THEY MATCH");
                    exit(0);
                }


                try {
                    run.add(urlcl.loadClass(temp[0]));
                } catch(IllegalAccessError e) {
                    System.out.println("Class " + temp[0] + " wasn't loaded " + e);
                }
            }
        } catch(MalformedURLException e) {
            System.out.println("Incorrect test path : " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't find class : " + e);
        }

        JUnitCore testRunner = new JUnitCore();
        List<Result> results = new ArrayList<>();

        for(Class<?> runnableTest : run) {
            results.add(testRunner.run(runnableTest));
        }

        return results;
    }

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
