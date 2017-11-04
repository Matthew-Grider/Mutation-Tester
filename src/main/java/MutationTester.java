import org.junit.runner.JUnitCore;
        import org.junit.runner.Result;
        import java.lang.reflect.Method;
        import java.util.ArrayList;
        import java.util.List;

public class MutationTester extends JUnitCore {
    @Override
    public Result run(java.lang.Class<?>... classes)
    {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        for(int i = 0 ; i < classes.length; i++)
        {
            classList.add(classes[i]);
        }
        List<Method> tests = Utilities.findTests(classList);
        for(Method test : tests)
        {
            System.out.println(test.getName());
        }
        return super.run(classes);
    }
}
