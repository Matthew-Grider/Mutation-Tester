
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.util.Pair;
import javassist.*;

import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.io.FileUtils;
import org.junit.runner.Request;
import io.reactivex.*;

public class Driver {

    public static Pair<String, List<String>> readConfig(String configPath)
    {
        List<String> classes = new ArrayList<>();
        String dir = "";

        try {
            FileReader freader = new FileReader(configPath);
            BufferedReader br = new BufferedReader(freader);
            String s;
            int counter = 1;
            while ((s = br.readLine()) != null) {
                if(counter == 2)
                {
                    dir = s;
                    System.out.println("|" + dir + "|");
                }
                else if(counter > 3)
                {
                    classes.add(s);
                    System.out.println("|" + s + "|");
                }
                counter++;
            }
            freader.close();
        } catch (Exception e) {
            System.out.println("Couldn't read directory : " + e);
        } finally {
            return new Pair<String, List<String>>(dir, classes);
        }
    }

    public static List<String> copyMutationDocumentation(String dir)
    {
        String oldDir = (dir.split("out"))[0];
        String classPath = (dir.split("out"))[1];
        String newDir = "mutation";
        List<String> mutatedClassPath = new ArrayList<>();

        oldDir = oldDir + "out";
        File oldFile = new File(oldDir);
        System.out.println(oldDir);
        for(int i = 1; i < 7; i++)
        {
            String temp = newDir + i;
            File newFile = new File(temp);
            try {
                FileUtils.copyDirectory(oldFile, newFile);
                mutatedClassPath.add(temp + classPath);
                System.out.println(temp + classPath);
            } catch(java.io.IOException e)
            {
                System.out.println("Couldn't copy to new folder : " + e);
            }
        }
        return mutatedClassPath;
    }

    public static void main(String[] args)
    {
        Pair<String, List<String>> fileInput = Driver.readConfig("config.txt");
        List<String> mutationList = Driver.copyMutationDocumentation(fileInput.getKey());
        //File f = new File("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\");
        //File f = new File("E:\\cs hw stuff\\cs474-hw2\\out\\production\\classes\\");


        //System.out.println(f.getPath());

        try {
            /*URL[] classpath = {f.toURI().toURL()};
            URLClassLoader urlcl = new URLClassLoader(classpath);
            //Class c = urlcl.loadClass("io.reactivex.BackpressureEnumTest");
            Class c = urlcl.loadClass("io.reactivex.observers.SafeObserver");
            Class<?> cl = c;

            System.out.println(c);*/

            /*ClassPool cp = ClassPool.getDefault();
            //cp.insertClassPath("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\");
            //CtClass cc = cp.get("io.reactivex.BackpressureEnumTest");
            cp.insertClassPath("E:\\cs hw stuff\\cs474-hw2\\out\\production\\classes\\");
            CtClass cc = cp.get("io.reactivex.observers.SafeObserver");

            System.out.println(cc.isFrozen());*/


            /*BufferedInputStream fin
                    = new BufferedInputStream(new FileInputStream("E:\\cs hw stuff\\cs474-hw2\\out\\test\\classes\\io\\reactivex\\BackpressureEnumTest.class"));
            ClassFile cf = new ClassFile(new DataInputStream(fin));*/
            List<String> ctList = new ArrayList<String>();
            for(String name : fileInput.getValue())
            {
                ctList.add(name);
            }

            System.out.println("add stuff to list : " + ctList.size());


            //sets constructor body to return right away
            Mutator mutation1 = new Mutator() {
                @Override
                public void mutate(CtClass mutation) {
                    mutation.defrost();
                    CtConstructor[] constructors = mutation.getConstructors();
                    try {
                        for (int i = 0; i < constructors.length; i++) {
                            mutation.removeConstructor(constructors[i]);
                        }
                        mutation.writeFile();
                        System.out.println("Class has been mutated");
                    } catch (javassist.CannotCompileException e)
                    {
                        System.out.println("bruh, this change cant be compiled mutation1 " + e);
                    } catch (Exception e)
                    {
                        System.out.println("Somethings up in mutation1 : " + e);
                    }
                }
            };

            System.out.println("add new mutation1 rules");

            //removes every other method
            Mutator mutation2 = new Mutator() {
                @Override
                public void mutate(CtClass mutation) {
                    mutation.defrost();
                    CtMethod[] methods = mutation.getDeclaredMethods();
                    try {
                        for (int i = 0; i < methods.length; i += 2) {
                            mutation.removeMethod(methods[i]);
                        }
                        mutation.writeFile();
                        System.out.println("Class has been mutated");
                    } catch (javassist.CannotCompileException e)
                    {
                        System.out.println("bruh, this change cant be compiled mutation2 " + e);
                    } catch (Exception e)
                    {
                        System.out.println("Somethings up in mutation2 : " + e);
                    }
                }
            };

            System.out.println("add new mutation2 rules");

            //sets everyother method to static
            Mutator mutation3 = new Mutator() {
                @Override
                public void mutate(CtClass mutation) {
                    mutation.defrost();
                    CtMethod[] methods = mutation.getMethods();
                    try {
                        for (int i = 0; i < methods.length; i += 2) {
                            methods[i].setModifiers(Modifier.STATIC);
                        }
                        mutation.writeFile();
                        System.out.println("Class has been mutated");
                    } catch (javassist.CannotCompileException e)
                    {
                        System.out.println("bruh, this change cant be compiled mutation3 " + e);
                    } catch (Exception e)
                    {
                        System.out.println("Somethings up in mutation3 : " + e);
                    }
                }
            };

            System.out.println("add new mutation3 rules");

            //removes fields
            Mutator mutation4 = new Mutator() {
                @Override
                public void mutate(CtClass mutation) {
                    mutation.defrost();
                    CtField[] fields = mutation.getFields();
                    try {
                        for (int i = 0; i < fields.length; i++) {
                            mutation.removeField(fields[i]);
                        }
                        mutation.writeFile();
                        System.out.println("Class has been mutated");
                    } catch (javassist.CannotCompileException e)
                    {
                        System.out.println("bruh, this change cant be compiled mutation4 " + e);
                    } catch (Exception e)
                    {
                        System.out.println("Somethings up in mutation4 : " + e);
                    }
                }
            };

            System.out.println("add new mutation4 rules");

            //changers fields to final
            Mutator mutation5 = new Mutator() {
                @Override
                public void mutate(CtClass mutation) {
                    mutation.defrost();
                    CtField[] fields = mutation.getFields();
                    try {
                        for (int i = 0; i < fields.length; i++) {
                            fields[i].setModifiers(Modifier.FINAL);
                        }
                        mutation.writeFile();
                        System.out.println("Class has been mutated");
                    } catch (javassist.CannotCompileException e)
                    {
                        System.out.println("bruh, this change cant be compiled mutation5 " + e);
                    } catch (Exception e)
                    {
                        System.out.println("Somethings up in mutation5 : " + e);
                    }
                }
            };

            System.out.println("add new mutation5 rules");

            //changes fields to static
            Mutator mutation6 = new Mutator() {
                @Override
                public void mutate(CtClass mutation) {
                    mutation.defrost();
                    CtField[] fields = mutation.getFields();
                    try {
                        for (int i = 0; i < fields.length; i++) {
                            fields[i].setModifiers(Modifier.STATIC);
                        }
                        mutation.writeFile();
                        System.out.println("Class has been mutated");
                    } catch (javassist.CannotCompileException e)
                    {
                        System.out.println("bruh, this change cant be compiled mutation6 " + e);
                    } catch (Exception e)
                    {
                        System.out.println("Somethings up in mutation6 : " + e);
                    }
                }
            };

            System.out.println("add new mutation6 rules");

            Mutator[] allMutations = {mutation1, mutation2, mutation3, mutation4, mutation5, mutation6};

            List<MutationTester> runnables = new ArrayList<>();

            int i = 0;
            for(String name : mutationList) {
                runnables.add(new MutationTester(("Mutation" + (i + 1)), allMutations[i], name, ctList));
                System.out.println("created new mutation");
                i++;
            }

            ExecutorService executor = Executors.newFixedThreadPool(3);

            for(MutationTester running : runnables)
            {
                Runnable worker = new WorkThread(running);
                executor.execute(worker);
            }

            executor.shutdown();
            while (!executor.isTerminated()) {   }

            File deleting;
            for(int j = 1; i < 7; i++) {
                deleting = new File(("mutation" + j));
                FileUtils.deleteDirectory(deleting);
            }


        //testRunner1.modifyBytes();

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
