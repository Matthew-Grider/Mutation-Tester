import org.junit.runner.Result;

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
        for(Result result : executer.runTest())
        {
            System.out.println(result.wasSuccessful());
        }
    }
}
