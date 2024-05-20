package edu.utexas.ece.flakytracker;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.utexas.ece.flakytracker.agent.FlakyClassTracer;
import edu.utexas.ece.flakytracker.agent.FlakyTaintLabel;
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
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class FlakyTrackerTest {

    int a = 1;

    static int b =2;

    static final int c = 3;

    static Random e = new Random();
    static final Random f = new Random();
    private static final Random SHARED_RANDOM = new Random();
    @Test
    public void test02(){
//        b = 1;
        Random r = new Random();
        int x = r.nextInt();
        int y = b;
        y = SHARED_RANDOM.nextInt();
        FlakyUtil.checkTainted(e,"aa");
        org.junit.Assert.assertEquals("equal",y,2);  // static but not flaky
        org.junit.Assert.assertEquals("equal",c,3);  // possible flaky
        org.junit.Assert.assertEquals("not equal", x, 3); //random
    }

    @Test

    public void test071() throws Throwable {

        java.util.Locale locale0 = null;

        java.util.Random random1 = null;

        com.github.javafaker.service.RandomService randomService2 = new com.github.javafaker.service.RandomService(random1);

        long long4 = randomService2.nextLong((long) 'a');

        // The following exception was thrown during execution in test generation

        try {

//            com.github.javafaker.Faker faker5 = new com.github.javafaker.Faker(locale0, randomService2);

            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException; message: locale is required");

        } catch (java.lang.IllegalArgumentException e) {

            // Expected exception.

        }

// flaky:         org.junit.Assert.assertTrue("'" + long4 + "' != '" + 62L + "'", long4 == 62L);

    }


    @Test
    public void parseClassFile() throws IOException {
//        API.getParamTypes("Ljava/io/PrintStream;");
        String className = "edu.utexas.ece.flakytracker.FlakyTrackerTest";
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        boolean asmCode = true;

//                Printer printer = asmCode ? new ASMifier() : new Textifier();
//                PrintWriter printWriter = new PrintWriter(System.out, true);
//                TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
//                new ClassReader(className).accept(traceClassVisitor, parsingOptions);

        // 读取.class文件
        File classFile = new File("C:\\Users\\yhcro\\IdeaProjects\\FlakyTracker\\DiUS-java-faker\\target\\test-classes\\com\\github\\javafaker\\AddressTest.class");
        InputStream inputStream = new FileInputStream(classFile);

        // 创建ClassReader
        ClassReader reader = new ClassReader(inputStream);
//        ClassReader reader = new ClassReader(className);

        //        final ClassReader reader = new ClassReader(bytes);
        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
        FlakyClassTracer visitor = new FlakyClassTracer(writer);
        reader.accept(visitor, 0);

        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/AfterTracker.class"),writer.toByteArray());

//////         System.out.println
    }

    @Test
    public void parseASMFile() throws IOException{
        String className = "edu.utexas.ece.flakytracker.FlakyTrackerTest";
//        className = "org.objectweb.asm.ClassReader";
//        FlakyDemo.noninitial = 1;
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        boolean asmCode = true;
        System.out.println(Arrays.toString(className.split("\\$")));

        // (2) 打印结果
        Printer printer = asmCode ? new ASMifier() : new Textifier();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
//        new ClassReader(className).accept(traceClassVisitor, parsingOptions);
        ClassReader classReader = new ClassReader(className);

        ClassWriter classWriter = new ClassWriter(classReader,parsingOptions);
//        new Class(classWriter);



        classReader.accept(traceClassVisitor,0);
    }

}
