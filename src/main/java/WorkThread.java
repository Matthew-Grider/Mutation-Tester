import org.junit.runner.Result;

public class WorkThread implements Runnable {
    private MutationTester executer;

    public WorkThread(MutationTester newExecutter)
    {
        executer = newExecutter;
    }

    public void run(){
        executer.modifyBytes();
        for(Result result : executer.runTest())
        {
            System.out.println(result.wasSuccessful());
        }
    }
}
