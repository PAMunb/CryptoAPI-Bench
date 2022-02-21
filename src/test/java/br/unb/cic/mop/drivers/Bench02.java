package br.unb.cic.mop.drivers;

import com.google.common.reflect.ClassPath;
import org.junit.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Bench02 {

    public static final String BENCH02_PACKAGE = "br.unb.cic.mop";

    @Test
    public void executeBenchmark()  {
        Set<Class<?>> classes = null;

        try {
            classes = findBench02Classes();
        }
        catch(IOException ex) {
            Assert.fail(ex.getMessage());
        }

        Assert.assertTrue(classes.size() > 0);

        int executions = 0;
        int errors = 0;

        Set<String> classNames = new HashSet<>();

        for (Class<?> c : classes) {
            Method mainMethod = findMainMethod(c);

            classNames.add(c.getName());

            try {
                if (mainMethod != null) {
                    switch (mainMethod.getParameterCount()) {
                        case 0:
                            mainMethod.invoke(null);
                            executions++;
                            break;
                        case 1:
                            mainMethod.invoke(null, (Object) null);
                            executions++;
                            break;
                        default:
                            System.out.println(String.format("Error in class %s. Method main has %d",
                                    c.getName(), mainMethod.getParameterCount()));
                    }
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error on method: " + mainMethod.toString());
                errors++;
            }
            catch (Exception ex) {
                System.out.println(ex.getCause() + " " + ex.getMessage() + " when executing " + c.getName());
                errors++;
            }
        }

        System.out.println(errors);
        Assert.assertTrue(executions > 0);
    }

    /* finds the classes of our second benchmark */
    private Set<Class<?>> findBench02Classes() throws IOException {
        ClassPath cp = ClassPath.from(getClass().getClassLoader());

        Set<Class<?>> classes = new HashSet<>();

        cp.getTopLevelClassesRecursive(BENCH02_PACKAGE).stream().forEach(ci -> classes.add(ci.load()));

        return classes;
    }

    /* finds the main method of a given class */
    private Method findMainMethod(Class<?> c) {
        Method mainMethod = null;
        Method[] methods = c.getDeclaredMethods();

        for(Method m: methods) {
            if(m.getName().equals("main")) {
                return m;
            }
        }
        return null;
    }

    /* We use the following method to "silence" the standard output stream */


}
