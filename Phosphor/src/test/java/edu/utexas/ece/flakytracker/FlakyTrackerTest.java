package edu.utexas.ece.flakytracker;

import com.google.common.base.Preconditions;
import com.mifmif.common.regex.Generex;
import edu.columbia.cs.psl.phosphor.instrumenter.ClinitRetransformClassVisitor;
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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

            com.github.javafaker.Faker faker5 = new com.github.javafaker.Faker(locale0, randomService2);

            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException; message: locale is required");

        } catch (java.lang.IllegalArgumentException e) {

            // Expected exception.

        }

        org.junit.Assert.assertTrue("'" + long4 + "' != '" + 62L + "'", long4 == 62L);

    }


//    @Test
//    public void parseClassFile() throws IOException {
////        API.getParamTypes("Ljava/io/PrintStream;");
//        String className = "edu.utexas.ece.flakytracker.FlakyTrackerTest";
////        Generex
//        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
//        boolean asmCode = true;
//
////                Printer printer = asmCode ? new ASMifier() : new Textifier();
////                PrintWriter printWriter = new PrintWriter(System.out, true);
////                TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
////                new ClassReader(className).accept(traceClassVisitor, parsingOptions);
//
//        // 读取.class文件
////        File classFile = new File("C:\\Users\\yhcro\\IdeaProjects\\FlakyTracker\\DiUS-java-faker\\target\\test-classes\\flaky\\FlakyTest.class");
////        InputStream inputStream = new FileInputStream(classFile);
//
//        // 创建ClassReader
////        ClassReader reader = new ClassReader(inputStream);
//        ClassReader reader = new ClassReader(className);
//
//        //        final ClassReader reader = new ClassReader(bytes);
//        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
////        FlakyClassTracer visitor = new FlakyClassTracer(writer);
////        ClassNode visitor = new ClassNode(writer);
//        ClinitRetransformClassVisitor visitor = new ClinitRetransformClassVisitor(writer);
//        reader.accept(visitor, 0);
//        System.out.println(visitor);
//
//        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/AfterTracker.class"),writer.toByteArray());
//
//    }


//    @Test
//    public void parseClassFile() throws IOException {
//        String className = "net.landzero.xlog.mybatis.TrackEventBuilder";
//        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
//        boolean asmCode = true;
//        ClassReader reader = new ClassReader(className);
//        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
//        FlakyClassTracer visitor = new FlakyClassTracer(writer);
//        reader.accept(visitor, 0);
//        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/AfterTracker.class"),writer.toByteArray());
//
//    }

    @Test
    public void parseASMFile() throws IOException{
        String className = "com.github.edgar615.util.base.Randoms";
        className = "edu.utexas.ece.flakytracker.FlakyTrackerTest";
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

    @Test
    public void howtoSwap(){
        Random a = ThreadLocalRandom.current();
        if (a instanceof ThreadLocalRandom)
            System.out.println("haha");
        else
            System.out.println("hagepi");

    }

    @Test
    public void test078_1() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "TestGroup100Case0.test078");
        System.out.println(System.currentTimeMillis());
        java.lang.String str0 = net.landzero.xlog.XLog.crid();
        org.junit.Assert.assertEquals("'" + str0 + "' != '" + "-" + "'", str0, "-");
    }
    @Test
    public void test075_1() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "TestGroup100Case0.test075");
        net.landzero.xlog.mybatis.TrackEventBuilder trackEventBuilder0 = new net.landzero.xlog.mybatis.TrackEventBuilder();
        trackEventBuilder0.commit();
        net.landzero.xlog.mybatis.TrackEvent trackEvent2 = trackEventBuilder0.build();
        trackEvent2.setError("DIGEST");
        long long5 = trackEvent2.getDuration();
        java.lang.String str6 = trackEvent2.getMethod();
        org.junit.Assert.assertNotNull(trackEvent2);
        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 1L + "'", long5 == 1L);
        org.junit.Assert.assertNull(str6);
    }
}
