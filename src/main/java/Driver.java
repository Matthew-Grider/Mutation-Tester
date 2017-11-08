
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javassist.*;

import javassist.bytecode.ClassFile;
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

            ClassPool cp = ClassPool.getDefault();
            cp.insertClassPath("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\");
            CtClass cc = cp.get("io.reactivex.BackpressureEnumTest");

            System.out.println(cc.isFrozen());


            /*BufferedInputStream fin
                    = new BufferedInputStream(new FileInputStream("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\io\\reactivex\\BackpressureEnumTest.class"));
            ClassFile cf = new ClassFile(new DataInputStream(fin));*/

            MutationTester testRunner1 = new MutationTester();

            testRunner1.modifyBytes(cc, cc.getClassFile(), cl);

            /*List<Class<?>> classList = new ArrayList<Class<?>>();
            classList.add(cl);

            List<Method> testmethods = Utilities.findTests(classList);

            for(Method meth : testmethods)
            {
                System.out.println(meth.getName());
            }*/


        } catch (Exception e)
        {
            System.out.println(e + " : couldn't find the class dumby");
        } /*catch(Exception e)
        {
            System.out.println(e);
        }*/
    }

    private static Class<?> parseSource(File source)
    {
        //takes source code and returns the clsses to be used with the mutationTester
        return null;
    }
}
