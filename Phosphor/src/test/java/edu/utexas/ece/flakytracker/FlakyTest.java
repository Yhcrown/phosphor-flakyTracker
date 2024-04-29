package edu.utexas.ece.flakytracker;
import edu.utexas.ece.flakytracker.agent.FlakyClassTracer;
import org.apache.commons.io.FileUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.columbia.cs.psl.phosphor.runtime.Taint;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
// import flaky.FlakyUtil;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlakyTest {

    public static boolean debug = false;

    @Test
    public void test021() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TestGroup100Case0.test021");
        java.util.Random random0 = null;
        com.github.javafaker.Faker faker1 = new com.github.javafaker.Faker(random0);
        com.github.javafaker.Photography photography2 = faker1.photography();
        java.lang.String str3 = photography2.brand();
        // FlakyUtil.checkTainted(str3);
        System.out.println(MultiTainter.getTaint(str3));
        // org.junit.Assert.assertNotNull(photography2);
        org.junit.Assert.assertEquals("'" + str3 + "' != '" + "Tokina" + "'", str3, "Tokina"); //flaky
    }

    @Test
    public void test045() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TestGroup100Case0.test045");
        java.util.Random random0 = null;
        com.github.javafaker.Faker faker1 = new com.github.javafaker.Faker(random0);
        com.github.javafaker.Photography photography2 = faker1.photography();
        com.github.javafaker.IdNumber idNumber3 = faker1.idNumber();
        com.github.javafaker.App app4 = faker1.app();
        com.github.javafaker.Hacker hacker5 = faker1.hacker();
        java.lang.String str7 = faker1.letterify("Mr Peanutbutter");
        org.junit.Assert.assertNotNull(photography2);
        org.junit.Assert.assertNotNull(idNumber3);
        org.junit.Assert.assertNotNull(app4);
        org.junit.Assert.assertNotNull(hacker5);
        // System.out.println(MultiTainter.getTaint(str7));
        org.junit.Assert.assertEquals("'" + str7 + "' != '" + "Mr Peanutbutter" + "'", str7, "Mr Peanutbutter");
    }

//    @Test
//    public void test() throws IOException {
////        API.getParamTypes("Ljava/io/PrintStream;");
//        String className = "edu.utexas.ece.flakytracker.FlakyTest";
//        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
//        boolean asmCode = true;
//
//        //        Printer printer = asmCode ? new ASMifier() : new Textifier();
//        //        PrintWriter printWriter = new PrintWriter(System.out, true);
//        //        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
//        //        new ClassReader(className).accept(traceClassVisitor, parsingOptions);
//
//        ClassReader reader = new ClassReader(className);
//
//        //        final ClassReader reader = new ClassReader(bytes);
//        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
//        FlakyClassTracer visitor = new FlakyClassTracer(writer);
//        reader.accept(visitor, 0);
//
//        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/FlakyTest.class"),writer.toByteArray());
//
////////         System.out.println
//    }
}

