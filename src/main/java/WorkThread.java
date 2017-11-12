import org.junit.runner.Result;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class WorkThread implements Runnable {
    private MutationTester executer;

    /**
     * Creates new WorkThread which contains a MutaitonTester to be ran
     * */
    public WorkThread(MutationTester newExecutter)
    {
        executer = newExecutter;
    }

    /**
     * Runs a job by first modifying the bytecode of different classes as specified by the MutationTester executer, then runs
     * the unit tests on the modified bytecode
     * */
    public void run(){
        executer.modifyBytes();
        List<Result> resultList = executer.runTest();
        try {
            FileWriter fileWriter = new FileWriter(executer.getName() +".txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for(Result res : resultList) {
                printWriter.println(res.getFailureCount());
            }
            printWriter.close();
        } catch (IOException e)
        {
            System.out.println("Couldn't write base test : " + e);
        }
    }
}
