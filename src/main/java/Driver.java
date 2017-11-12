
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
import org.junit.runner.Result;

public class Driver {

    /**
     * Reads config file and returns a pair containing the directory and classes to be mutated
     * param: path to config file
     * return <directory name, list of names of classes to be mutated>
    * */
    public static Pair<List<String>, List<String>> readConfig(String configPath)
    {
        List<String> classes = new ArrayList<>();
        List<String> dir = new ArrayList<>();

        try {
            FileReader freader = new FileReader(configPath);
            BufferedReader br = new BufferedReader(freader);
            String s;
            int counter = 1;
            while ((s = br.readLine()) != null) {
                if(counter == 2)
                {
                    dir.add(s);
                    System.out.println("|" + dir + "|");
                }
                else if(counter == 4)
                {
                    dir.add(s);
                }
                else if(counter > 5)
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
            return new Pair<List<String>, List<String>>(dir, classes);
        }
    }

    /**
     * Makes six copies of all class files from the /out directory and then returns a list of thier paths
     * param: path to /out
     * return list of new copies paths
     * */
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

    /**
     * Read results from the different output files
     * param: path to file
     * return list of returned misses
     * */
    public static List<Integer> printResults(String fileName)
    {
        List<Integer> dir = new ArrayList<>();
        try {
            FileReader freader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(freader);
            String s;
            int counter = 1;
            while ((s = br.readLine()) != null) {
                dir.add(Integer.parseInt(s));
            }
            freader.close();
        } catch (Exception e) {
            System.out.println("Couldn't read directory : " + e);
        }
        return dir;
    }

    /**
     * Processes the results of all tests and prints the consensus
     * param: list of master and the tests results
     * */
    public static void finalPrinter(List<List<Integer>> mutations, List<Integer> master)
    {
        int missMatches = 0;
        int totalTestClass = 0;
        int numMisses = 0;

        System.out.println("--------------------------------------------");
        System.out.println("|              Mutation Results            |");
        System.out.println("|                Listed Below              |");
        System.out.println("--------------------------------------------");
        System.out.println("\n");

        int counter = 1;
        for(List<Integer> list : mutations)
        {
            for(int i = 0; i < master.size() && i < list.size(); i++) {
                if (master.get(i) != list.get(i))
                {
                    missMatches++;
                }
                totalTestClass++;
                numMisses += list.get(i);
            }
            System.out.println("Mutation " + counter + " was " + ((1 - (missMatches / totalTestClass)) * 100) + "% the same as the original tests");
            System.out.println("with " + missMatches + " different test detections from original tests and a total of " + numMisses);
            counter++;
        }
    }

    /**
     * Starts the program and manages the thread pool
     * */
    public static void main(String[] args)
    {
        //prepares data to be mutated
        Pair<List<String>, List<String>> fileInput = Driver.readConfig("config.txt");
        String testPath = fileInput.getKey().get(1);
        //runs basline tests
        List<Result> resultList = Utilities.runTest(testPath);
        try {
            FileWriter fileWriter = new FileWriter("BaseTest.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for(Result res : resultList) {
                printWriter.println(res.getFailureCount());
            }
            printWriter.close();
        } catch (IOException e)
        {
            System.out.println("Couldn't write base test : " + e);
        }
        System.out.println("Test ran");
        List<String> mutationList = Driver.copyMutationDocumentation(fileInput.getKey().get(0));

        //adds names of classes to be edited to a list
        try {
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

            //array of all above created mutations
            Mutator[] allMutations = {mutation1, mutation2, mutation3, mutation4, mutation5, mutation6};

            List<MutationTester> runnables = new ArrayList<>();

            //creates list of MutationTesters to be executed by threads
            int i = 0;
            for(String name : mutationList) {
                runnables.add(new MutationTester(("Mutation" + (i + 1)), allMutations[i], name, ctList));
                System.out.println("created new mutation");
                i++;
            }

            //creates thread pool with 3 threads
            ExecutorService executor = Executors.newFixedThreadPool(3);

            //runs the 6 MutationTest with the 3 threads in the pool
            for(MutationTester running : runnables)
            {
                Runnable worker = new WorkThread(running);
                executor.execute(worker);
            }

            executor.shutdown();
            while (!executor.isTerminated()) {   }

            //creates list from test output files
            List<Integer> master = printResults("BaseTest.txt");
            List<List<Integer>> mutations = new ArrayList<>();
            for(int j = 1; j < 7; j++) {
                mutations.add(printResults("Mutation" + j + ".txt"));
            }

            //processes and prints the final outputs
            finalPrinter(mutations, master);

            //removes the copied directories
            File deleting;
            for(int j = 1; i < 7; j++) {
                deleting = new File(("mutation" + j));
                FileUtils.deleteDirectory(deleting);
            }
        } catch (Exception e)
        {
            System.out.println(e + " : something went wrong in the Driver");
        }
    }
}
