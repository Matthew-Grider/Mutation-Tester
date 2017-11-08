
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
        //File f = new File("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\");
        File f = new File("E:\\cs hw stuff\\cs474-hw2\\out\\production\\classes\\");


        System.out.println(f.getPath());

        try {
            URL[] classpath = {f.toURI().toURL()};
            URLClassLoader urlcl = new URLClassLoader(classpath);
            //Class c = urlcl.loadClass("io.reactivex.BackpressureEnumTest");
            Class c = urlcl.loadClass("io.reactivex.observers.SafeObserver");
            Class<?> cl = c;

            System.out.println(c);

            ClassPool cp = ClassPool.getDefault();
            //cp.insertClassPath("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\");
            //CtClass cc = cp.get("io.reactivex.BackpressureEnumTest");
            cp.insertClassPath("E:\\cs hw stuff\\cs474-hw2\\out\\production\\classes\\");
            CtClass cc = cp.get("io.reactivex.observers.SafeObserver");

            System.out.println(cc.isFrozen());


            /*BufferedInputStream fin
                    = new BufferedInputStream(new FileInputStream("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\io\\reactivex\\BackpressureEnumTest.class"));
            ClassFile cf = new ClassFile(new DataInputStream(fin));*/

            MutationTester testRunner1 = new MutationTester();

            Mutator mutation = new Mutator() {
                @Override
                public void mutate(CtClass mutation) {
                    mutation.defrost();
                    CtConstructor[] constructors = mutation.getConstructors();
                    try {
                        for (int i = 0; i < constructors.length; i++) {
                            constructors[i].setBody("{return;}");
                        }
                        mutation.writeFile("C:\\Users\\Matt\\Documents");
                        System.out.println("Class has been mutated");
                    } catch (javassist.CannotCompileException e)
                    {
                        System.out.println("bruh, this change cant be compiled " + e);
                    } catch (Exception e)
                    {
                        System.out.println("Somethings up : " + e);
                    }
                }
            };

            testRunner1.modifyBytes(cc, mutation, cl);

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
