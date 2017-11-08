import javassist.CtClass;

public interface Mutator {
    public void mutate(CtClass mutation);
}
