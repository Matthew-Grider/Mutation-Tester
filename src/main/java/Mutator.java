import javassist.CtClass;

public interface Mutator {
    /**
     * Mutates the bytecode
     * */
    public void mutate(CtClass mutation);
}
