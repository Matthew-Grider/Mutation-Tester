public class WorkThread implements Runnable {
    private MutationTester executer;

    public WorkThread(MutationTester newExecutter)
    {
        executer = newExecutter;;
    }

    public void run(){
        executer.modifyBytes();
    }
}
