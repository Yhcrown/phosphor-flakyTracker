package edu.utexas.ece.flakytracker;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.utexas.ece.flakytracker.agent.FlakyClassTracer;
import edu.utexas.ece.flakytracker.agent.FlakyUtil;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FlakyTrackerTest {

    int a = 1;

    static int b =2;

    static int c = 3;
    @Test
    public void test02(){
        b = 1;
        Random r = new Random();
        int x = r.nextInt();
        int y = b;
        org.junit.Assert.assertEquals("equal",y,1);  // static but not flaky
        org.junit.Assert.assertEquals("equal",c,3);  // possible flaky
        org.junit.Assert.assertEquals("not equal", x, 3); //random
    }




    @Test
    public void test() throws IOException {
//        API.getParamTypes("Ljava/io/PrintStream;");
        String className = "edu.utexas.ece.flakytracker.FlakyTrackerTest";
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        boolean asmCode = true;

        //        Printer printer = asmCode ? new ASMifier() : new Textifier();
        //        PrintWriter printWriter = new PrintWriter(System.out, true);
        //        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        //        new ClassReader(className).accept(traceClassVisitor, parsingOptions);
//
//        ClassReader reader = new ClassReader(className);
//
//        //        final ClassReader reader = new ClassReader(bytes);
//        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
//        FlakyClassTracer visitor = new FlakyClassTracer(writer);
//        reader.accept(visitor, 0);
//
//        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/AfterTracker.class"),writer.toByteArray());

//////         System.out.println
    }

}
