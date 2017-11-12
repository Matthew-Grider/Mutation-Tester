import javassist.bytecode.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javassist.*;

import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;
import static java.lang.System.exit;
import static javassist.bytecode.Mnemonic.OPCODE;

public class MutationTester {
    private String mutationName;
    private Mutator mutation;
    private ClassPool cp;
    private List<CtClass> ctList;
    private String dirPath;
    private List<String> classList;
    private String testPath;

    /**
     * Creates new MutationsTester which takes the defined mutation interface and mutates the byte code
     * */
    public MutationTester(String name, Mutator newMutation, String newDirPath, List<String> newPackageList)
    {
        mutationName = name;
        mutation = newMutation;
        dirPath = newDirPath;
        classList = newPackageList;
        ctList = new ArrayList<>();
        //testPath = pathTest;

        System.out.println("copied all variables");

        try {
            cp = new ClassPool();
            cp.insertClassPath(newDirPath);
        } catch (javassist.NotFoundException e){
            System.out.println("couldn't find the correct directory path : " + e);
        }
        System.out.println("made class pool " + cp.toString());
        for(int i = 0; i < newPackageList.size(); i++) {
            String clazz = newPackageList.get(i);
            System.out.println(clazz);
            try {
                ctList.add(cp.get(clazz));
                System.out.println("Loaded " + clazz + " into " + mutationName);
            } catch (javassist.NotFoundException e) {
                System.out.println("Couldn't find class " + clazz + " : " + e);
            }
        }


    }

    /**
     * Takes the mutation rules and mutates the given classes bytecode
     * */
    public void modifyBytes()
    {
        for( CtClass ct : ctList)
        {
            try {
                mutation.mutate(ct);
            } catch (Exception e) {
                System.out.println("couldn't run mutation correctly : " + e);
            }
        }
    }

    /**
     * Runs tests on loaded test classes and then returns the results
     * return list of Results from tests
     * */
    public List<Result> runTest()
    {
        String[] temp = dirPath.split("production");
        String testPath = temp[0] + "test" + temp[1];
        List<Class<?>> run = new ArrayList<>();

        File testDir = new File(testPath);

        List<File> tests = new ArrayList<>(FileUtils.listFiles(testDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));

        try {
            URL[] classpath = {testDir.toURI().toURL()};
            URLClassLoader urlcl = new URLClassLoader(classpath);

            for(File test : tests) {
                String holder = test.getAbsolutePath();
                if(holder.contains("\\")) {
                    temp = test.getAbsolutePath().split("classes\\\\");
                    temp = (temp[1].replaceAll("\\\\", ".")).split(".class");
                }
                else if(holder.contains("/"))
                {
                    temp = test.getAbsolutePath().split("classes/");
                    temp = (temp[1].replaceAll("/", ".")).split(".class");
                }
                else
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

    public String getName()
    {
        return mutationName;
    }
}
