package edu.utexas.ece.flakytracker;
import edu.utexas.ece.flakytracker.agent.API;
import edu.utexas.ece.flakytracker.agent.FlakyClassTracer;
import edu.utexas.ece.flakytracker.agent.FlakyUtil;
import org.apache.commons.io.FileUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
// import flaky.FlakyUtil;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlakyTest {

    public static boolean debug = false;

    ThreadLocalRandom current2 = ThreadLocalRandom.current();
    @Test
    public void test031_1() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TestGroup100Case0.test031");
        ThreadLocalRandom current = ThreadLocalRandom.current();

        java.util.Random random0 = null;
        com.github.javafaker.Faker faker1 = new com.github.javafaker.Faker(random0);
        com.github.javafaker.Avatar avatar2 = faker1.avatar();
        com.github.javafaker.SlackEmoji slackEmoji3 = faker1.slackEmoji();
        com.github.javafaker.Medical medical4 = faker1.medical();
        com.github.javafaker.University university6 = faker1.university();
        java.lang.String str7 = university6.prefix();
        org.junit.Assert.assertNotNull(avatar2);
        org.junit.Assert.assertNotNull(slackEmoji3);
        org.junit.Assert.assertNotNull(medical4);
        org.junit.Assert.assertNotNull(university6);
        org.junit.Assert.assertEquals("'" + str7 + "' != '" + "Eastern" + "'", str7, "Eastern");

    }

//    @Test
//    public void test068_1() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "TestGroup100Case0.test068");
//        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        int len = '4';
//        java.lang.String str1 = Randoms.randomAlphabet((int) '4');
//        FlakyUtil.checkTainted(str1.equals(str1),"hahaniubi");
//        Preconditions.checkNotNull(base);
//        StringBuilder sb = new StringBuilder(len);
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        int y = random.nextInt(len);
//        final Random temp = new Random();
//        double z = temp.nextGaussian();
//        FlakyUtil.checkTainted(temp,"random");
//        FlakyUtil.checkTainted(random,"Threadrandom");
//        FlakyUtil.checkTainted(y,"nextinty");
//        FlakyUtil.checkTainted(z,"nextintz");
//        char c = base.charAt(y);
//        System.out.println(c);
//        sb.append("nothing");
//
//        StringBuilder append = sb.append("a");
////
//        FlakyUtil.checkTainted(sb,"append");
//        char[] values = new char[10];
//        values[1] = c;
//        FlakyUtil.checkTainted(c,"charat");
//        char[] newValues = Arrays.copyOf(values,100);
//        FlakyUtil.checkTainted(sb.charAt(0),"values");
//        FlakyUtil.checkTainted(sb.toString().charAt(0),"toString");
//        boolean b = sb.toString().equals("b");
//        FlakyUtil.checkTainted(b,"boolean");
//        sb.toString();
//        char cor = 'c';
//        char cccc = MultiTainter.taintedChar(cor,"c");
//        FlakyUtil.checkTainted( MultiTainter.taintedChar(cor,"c"),"cccc");
//        StringBuilder buf = new StringBuilder();
//        buf.append(cccc);
//        FlakyUtil.checkTainted(sb,"buf");
//
//
////        FlakyUtil.checkTainted(new FactoryTest().getvalue(),"getvalue2");
//
//
//        System.out.println(MultiTainter.getTaint(y));
//
//
//
//        int range = base.length();
//        FlakyUtil.checkTainted(random.nextInt(range),"nextintrange");
////        FlakyUtil.checkTainted(base.charAt(random.nextInt(range)),"basecharat");
//        for(int i = 0; i < len; ++i) {
//            sb.append(base.charAt(random.nextInt(range)));
//        }
////        FlakyUtil.checkTainted(sb.charAt(1),"charat0");
//        str1 = sb.toString();
//        System.out.println("kFTkErtzJWZnsEgqxfNtJKPQjwPUVInUodtzvFAVDXzsdzxnhHm".length());
//        System.out.println(str1.length());
//        FlakyUtil.checkTainted(sb,"sb");
//        new StringBuilder(str1.length());
////        FlakyUtil.checkTainted(str1.equals(new String()),"ss");
////        org.junit.Assert.assertEquals("'" + str1 + "' != '" + "kFTkErtzJWZnsEgqxfNtJKPQjwPUVInUodtzvFAVDXzsdzxnhHhm" + "'", str1, "kFTkErtzJWZnsEgqxfNtJKPQjwPUVInUodtzvFAVDXzsdzxnhHhm");
//    }


//    @Test
//    public void test045() throws Throwable {
////        if (debug)
////            System.out.format("%n%s%n", "TestGroup100Case0.test045");
////        java.util.Random random0 = null;
////        com.github.javafaker.Faker faker1 = new com.github.javafaker.Faker(random0);
////        com.github.javafaker.Photography photography2 = faker1.photography();
////        com.github.javafaker.IdNumber idNumber3 = faker1.idNumber();
////        com.github.javafaker.App app4 = faker1.app();
////        com.github.javafaker.Hacker hacker5 = faker1.hacker();
////        java.lang.String str7 = faker1.letterify("Mr Peanutbutter");
////        org.junit.Assert.assertNotNull(photography2);
////        org.junit.Assert.assertNotNull(idNumber3);
////        org.junit.Assert.assertNotNull(app4);
////        org.junit.Assert.assertNotNull(hacker5);
////        // System.out.println(MultiTainter.getTaint(str7));
////        org.junit.Assert.assertEquals("'" + str7 + "' != '" + "Mr Peanutbutter" + "'", str7, "Mr Peanutbutter");
//    }
//
//    @Test
//    public void test() throws IOException {
////        API.getParamTypes("Ljava/io/PrintStream;");
//        String className = "edu.utexas.ece.flakytracker.FlakyTest";
////        String className = "org.junit.Assert";
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
//
//    @Test
//    public void APITest(){
//        System.out.println(Arrays.toString(API.getParamTypes("(Ljava/lang/String;JJ)V")));
//    }
//
//    @Test
//    public void testTracker() throws IOException {
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
//        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/AfterTracker.class"),writer.toByteArray());
//
////         System.out.println
//    }
//
//    @Test
//    public void test054_1() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "TestGroup100Case0.test054");
//        java.util.Random random0 = null;
//        com.github.javafaker.service.RandomService randomService1 = new com.github.javafaker.service.RandomService(random0);
//        long long3 = randomService1.nextLong((long) 'a');
//        java.lang.String str5 = randomService1.hex((int) (short) 10);
//        // The following exception was thrown during execution in test generation
//        try {
//            java.lang.Integer int8 = randomService1.nextInt(42598, (int) (byte) -1);
//            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException; message: bound must be positive");
//        } catch (java.lang.IllegalArgumentException e) {
//            // Expected exception.
//        }
//        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 10L + "'", long3 == 10L);
//// flaky:         org.junit.Assert.assertEquals("'" + str5 + "' != '" + "F3A67DA8B8" + "'", str5, "F3A67DA8B8");
//    }
//
//    @Test
//    public void test071_1() throws Throwable {
////         if (debug)
////             System.out.format("%n%s%n", "TestGroup100Case0.test071");
//        java.util.Locale locale0 = null;
//        java.util.Random random1 = null;
//        com.github.javafaker.service.RandomService randomService2 = new RandomService(random1);
//        long long4 = randomService2.nextLong((long) 'a');
////         // The following exception was thrown during execution in test generation
//        try {
////            com.github.javafaker.service.FakeValuesService faker5 = new com.github.javafaker.service.FakeValuesService(locale0, randomService2);
//
////                 throw new IllegalArgumentException();
////             org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException; message: locale is required");
//        } catch (java.lang.IllegalArgumentException e) {
////             // Expected exception.
//        }
//        System.out.println("test071");
//        org.junit.Assert.assertTrue("'" + long4 + "' != '" + 62L + "'", long4 == 62L);
//    }
//
    @Test
    public void tested(){
        System.out.println(API.getReturnType("();"));
    }
//

}

