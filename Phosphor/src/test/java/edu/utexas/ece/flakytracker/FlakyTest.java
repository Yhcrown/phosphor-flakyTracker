package edu.utexas.ece.flakytracker;
import edu.columbia.cs.psl.phosphor.struct.harmony.util.Arrays;
import edu.utexas.ece.flakytracker.agent.API;
import edu.utexas.ece.flakytracker.agent.FlakyClassTracer;
import org.apache.commons.io.FileUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.columbia.cs.psl.phosphor.runtime.Taint;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import com.github.javafaker.*;
import java.io.File;
import java.io.IOException;
// import flaky.FlakyUtil;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlakyTest {

    public static boolean debug = false;

    @Test
    public void test031_1() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TestGroup100Case0.test031");
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

    @Test
    public void test045() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "TestGroup100Case0.test045");
//        java.util.Random random0 = null;
//        com.github.javafaker.Faker faker1 = new com.github.javafaker.Faker(random0);
//        com.github.javafaker.Photography photography2 = faker1.photography();
//        com.github.javafaker.IdNumber idNumber3 = faker1.idNumber();
//        com.github.javafaker.App app4 = faker1.app();
//        com.github.javafaker.Hacker hacker5 = faker1.hacker();
//        java.lang.String str7 = faker1.letterify("Mr Peanutbutter");
//        org.junit.Assert.assertNotNull(photography2);
//        org.junit.Assert.assertNotNull(idNumber3);
//        org.junit.Assert.assertNotNull(app4);
//        org.junit.Assert.assertNotNull(hacker5);
//        // System.out.println(MultiTainter.getTaint(str7));
//        org.junit.Assert.assertEquals("'" + str7 + "' != '" + "Mr Peanutbutter" + "'", str7, "Mr Peanutbutter");
    }

    @Test
    public void test() throws IOException {
//        API.getParamTypes("Ljava/io/PrintStream;");
//        String className = "edu.utexas.ece.flakytracker.FlakyTest";
        String className = "org.junit.Assert";
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        boolean asmCode = true;

        //        Printer printer = asmCode ? new ASMifier() : new Textifier();
        //        PrintWriter printWriter = new PrintWriter(System.out, true);
        //        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        //        new ClassReader(className).accept(traceClassVisitor, parsingOptions);

        ClassReader reader = new ClassReader(className);

        //        final ClassReader reader = new ClassReader(bytes);
        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
        FlakyClassTracer visitor = new FlakyClassTracer(writer);
        reader.accept(visitor, 0);

        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/FlakyTest.class"),writer.toByteArray());

//////         System.out.println
    }

    @Test
    public void APITest(){
        System.out.println(Arrays.toString(API.getParamTypes("(Ljava/lang/String;JJ)V")));
    }

    @Test
    public void testTracker() throws IOException {
//        API.getParamTypes("Ljava/io/PrintStream;");
        String className = "flaky.FlakyTest";
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        boolean asmCode = true;

        //        Printer printer = asmCode ? new ASMifier() : new Textifier();
        //        PrintWriter printWriter = new PrintWriter(System.out, true);
        //        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        //        new ClassReader(className).accept(traceClassVisitor, parsingOptions);

        ClassReader reader = new ClassReader(className);

        //        final ClassReader reader = new ClassReader(bytes);
        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
        FlakyClassTracer visitor = new FlakyClassTracer(writer);
        reader.accept(visitor, 0);

        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/AfterTracker.class"),writer.toByteArray());

//         System.out.println
    }

}

