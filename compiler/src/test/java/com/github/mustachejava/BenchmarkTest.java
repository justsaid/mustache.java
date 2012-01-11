package com.github.mustachejava;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.concurrent.Executors;

import com.google.common.io.CharStreams;

import com.github.mustachejava.impl.DefaultCodeFactory;
import junit.framework.TestCase;

/**
 * Compare compilation with interpreter.
 * <p/>
 * User: sam
 * Date: 5/14/11
 * Time: 9:28 PM
 */
public class BenchmarkTest extends TestCase {
  private static final int TIME = 2000;
  private File root;

  protected void setUp() throws Exception {
    super.setUp();
    File file = new File("src/test/resources");
    root = new File(file, "simple.html").exists() ? file : new File("../src/test/resources");
  }

  public void testComplex() throws MustacheException, IOException {
    System.out.println("complex.html evaluations per millisecond:");
    for (int i = 0; i < 3; i++) {
      {
        DefaultCodeFactory cf = new DefaultCodeFactory();
        Mustache m = cf.compile("complex.html");
        assertEquals(CharStreams.toString(
                        new InputStreamReader(
                                BenchmarkTest.class.getResourceAsStream("/complex.txt"))),
                complextest(m, new InterpreterTest.ComplexObject()).toString());
        long start = System.currentTimeMillis();
        int total = 0;
        while (true) {
          complextest(m, new InterpreterTest.ComplexObject());
          total++;
          if (System.currentTimeMillis() - start > TIME) break;
        }
        System.out.println("Serial: " + total/TIME);
      }
    }
  }

  public void testParallelComplex() throws MustacheException, IOException {
    System.out.println("complex.html evaluations per millisecond:");
    for (int i = 0; i < 3; i++) {
      {
        DefaultCodeFactory cf = new DefaultCodeFactory();
        cf.setExecutorService(Executors.newCachedThreadPool());
        Mustache m = cf.compile("complex.html");
        assertEquals(CharStreams.toString(
                new InputStreamReader(
                        BenchmarkTest.class.getResourceAsStream("/complex.txt"))),
                complextest(m, new InterpreterTest.ParallelComplexObject()).toString());
        long start = System.currentTimeMillis();
        int total = 0;
        while (true) {
          complextest(m, new InterpreterTest.ParallelComplexObject());
          total++;
          if (System.currentTimeMillis() - start > TIME) break;
        }
        System.out.println("Parallel: " + total/TIME);
      }
    }
  }

  private StringWriter complextest(Mustache m, Object complexObject) throws MustacheException, IOException {
    StringWriter sw = new StringWriter();
    m.execute(sw, complexObject);
    return sw;
  }

}
