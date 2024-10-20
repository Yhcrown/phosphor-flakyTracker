package edu.utexas.ece.flakytracker;


import com.mifmif.common.regex.Generex;
import dk.brics.automaton.*;
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
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FlakyTrackerTest {

    int a = 1;

    static int b =2;

    static final int c = 3;

    static Random e = new Random();
    static final Random f = new Random();
    private static final Random SHARED_RANDOM = new Random();
//    @Test
//    public void test02(){
////        b = 1;
//        Random r = new Random();
//        int x = r.nextInt();
//        int y = b;
//        y = SHARED_RANDOM.nextInt();
//        FlakyUtil.checkTainted(e,"aa");
//        org.junit.Assert.assertEquals("equal",y,2);  // static but not flaky
//        org.junit.Assert.assertEquals("equal",c,3);  // possible flaky
//        org.junit.Assert.assertEquals("not equal", x, 3); //random
//    }

//    @Test
//
//    public void test071() throws Throwable {
//
//        java.util.Locale locale0 = null;
//
//        java.util.Random random1 = null;
//
//        com.github.javafaker.service.RandomService randomService2 = new com.github.javafaker.service.RandomService(random1);
//
//        long long4 = randomService2.nextLong((long) 'a');
//
//        // The following exception was thrown during execution in test generation
//
//        try {
//
//            com.github.javafaker.Faker faker5 = new com.github.javafaker.Faker(locale0, randomService2);
//
//            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException; message: locale is required");
//
//        } catch (java.lang.IllegalArgumentException e) {
//
//            // Expected exception.
//
//        }
//        FlakyUtil.checkTainted(long4,"a");
//        FlakyUtil.checkTainted("'" + long4 + "' != '" + 62L + "'","string");
//
//
//
//        org.junit.Assert.assertTrue("'" + long4 + "' != '" + 62L + "'", long4 == 62L);
//
//    }

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
//        FlakyClassTracer visitor = new FlakyClassTracer(writer);
////        ClassNode visitor = new ClassNode(writer);
////        ClinitRetransformClassVisitor visitor = new ClinitRetransformClassVisitor(writer);
//        reader.accept(visitor, 0);
//        System.out.println(visitor);
//
//        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/AfterTracker.class"),writer.toByteArray());
//
//    }


    @Test
    public void parseClassFile() throws IOException {
        String className = "org/springframework/jdbc/core/JdbcTemplate";
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        boolean asmCode = true;
        ClassReader reader = new ClassReader(className);
        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
        FlakyClassTracer visitor = new FlakyClassTracer(writer);
        reader.accept(visitor, 0);
        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/AfterTracker.class"),writer.toByteArray());

    }


@Test
public void test078_1() throws Throwable {
    com.kestreldigital.conjuror.Conjuror conjuror0 = new com.kestreldigital.conjuror.Conjuror();
    java.lang.String str1 = conjuror0.conjureFirstName();
    java.util.Date date4 = conjuror0.conjureBirthDate((int) (short) 0, (int) ' ');
    java.lang.String str5 = conjuror0.conjureLastName();
    java.lang.String str7 = conjuror0.conjureString("Hurst");
    java.util.Date date10 = conjuror0.conjureBirthDate((int) (short) 100, (int) (short) 1);
//    FlakyUtil.checkTainted(str1,"roger");
    org.junit.Assert.assertEquals("'" + str1 + "' != '" + "Roger" + "'", str1, "Roger");
    org.junit.Assert.assertNotNull(date4);
//    FlakyUtil.checkTainted(str7,"hurst");
// flaky "11) test015(TestGroup100Case0)":         org.junit.Assert.assertEquals(date4.toString(), "Thu Jan 07 15:38:01 GMT 1993");
// flaky "8) test015(TestGroup100Case0)":         org.junit.Assert.assertEquals("'" + str5 + "' != '" + "Parry" + "'", str5, "Parry");
    org.junit.Assert.assertEquals("'" + str7 + "' != '" + "Hurst" + "'", str7, "Hurst");
    org.junit.Assert.assertNotNull(date10);//    org.junit.Assert.assertNotNull(date10);
// flaky "7) test015(TestGroup100Case0)":         org.junit.Assert.assertEquals(date10.toString(), "Sat Jun 27 01:25:49 GMT 1936");
}
    @Test
    public void testAutomaton() throws Throwable {
        RegExp regExp = new RegExp("string");
        boolean a = true;
        MultiTainter.taintedBoolean(a,100);
        regExp.setAllowMutate(a);
        regExp = MultiTainter.taintedReference(regExp, "ha");
        regExp.toAutomaton();
    }
    @Test
    public void test291_1() throws Throwable {
        java.util.Comparator<org.apache.commons.csv.CSVRecord> cSVRecordComparator0 = null;
        com.google.code.externalsorting.csv.CsvSortOptions.Builder builder3 = new com.google.code.externalsorting.csv.CsvSortOptions.Builder(cSVRecordComparator0, (int) (byte) 1, (long) (byte) -1);
       java.util.Comparator<org.apache.commons.csv.CSVRecord> cSVRecordComparator16 = null;
        com.google.code.externalsorting.csv.CsvSortOptions.Builder builder19 = new com.google.code.externalsorting.csv.CsvSortOptions.Builder(cSVRecordComparator16, (int) (byte) 1, (long) (byte) -1);
        com.google.code.externalsorting.csv.CsvSortOptions.Builder builder21 = builder19.skipHeader(true);
        com.google.code.externalsorting.csv.CsvSortOptions csvSortOptions22 = builder21.build();
        org.apache.commons.csv.CSVFormat cSVFormat24 = csvSortOptions22.getFormat();
        com.google.code.externalsorting.csv.CsvSortOptions.Builder builder26 = builder3.format(cSVFormat24);
        long long27 = com.google.code.externalsorting.csv.SizeEstimator.estimatedSizeOf((java.lang.Object) builder26);
        org.junit.Assert.assertTrue("'" + long27 + "' != '" + 194L + "'", long27 == 194L);
    }


    @Test
    public void parseASMFile() throws IOException{
        String className = "com.github.edgar615.util.base.Randoms";
        className = "dk.brics.automaton.RegExp";
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
    public void example(){
        int i = Runtime.getRuntime().availableProcessors();
        Assert.assertEquals(i,20);
    }

//    @Test
//    public void test078_1() throws Throwable {
////        if (debug)
////            System.out.format("%n%s%n", "TestGroup100Case0.test078");
//        System.out.println(System.currentTimeMillis());
//        java.lang.String str0 = net.landzero.xlog.XLog.crid();
//        org.junit.Assert.assertEquals("'" + str0 + "' != '" + "-" + "'", str0, "-");
//    }
//    @Test
//    public void test075_1() throws Throwable {
////        if (debug)
////            System.out.format("%n%s%n", "TestGroup100Case0.test075");
//        net.landzero.xlog.mybatis.TrackEventBuilder trackEventBuilder0 = new net.landzero.xlog.mybatis.TrackEventBuilder();
//        trackEventBuilder0.commit();
//        net.landzero.xlog.mybatis.TrackEvent trackEvent2 = trackEventBuilder0.build();
//        trackEvent2.setError("DIGEST");
//        long long5 = trackEvent2.getDuration();
//        java.lang.String str6 = trackEvent2.getMethod();
//        org.junit.Assert.assertNotNull(trackEvent2);
//        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 1L + "'", long5 == 1L);
//        org.junit.Assert.assertNull(str6);
//    }

    @Test
    public void test014_1() throws Throwable {

        com.kestreldigital.conjuror.Conjuror conjuror0 = new com.kestreldigital.conjuror.Conjuror();
        java.lang.String str1 = conjuror0.conjureFirstName();
        java.lang.String str2 = conjuror0.conjureLastName();
        java.lang.String str3 = conjuror0.conjureFirstName();
        java.util.Date date6 = conjuror0.conjureBirthDate((int) (byte) 100, 100);
        java.lang.Class<?> wildcardClass7 = conjuror0.getClass();
        org.junit.Assert.assertEquals("'" + str1 + "' != '" + "Paul" + "'", str1, "Paul");
// flaky "10) test014(TestGroup100Case0)":         org.junit.Assert.assertEquals("'" + str2 + "' != '" + "Sayer" + "'", str2, "Sayer");
// flaky "7) test014(TestGroup100Case0)":         org.junit.Assert.assertEquals("'" + str3 + "' != '" + "Kamran" + "'", str3, "Kamran");
        org.junit.Assert.assertNotNull(date6);
// flaky "6) test014(TestGroup100Case0)":         org.junit.Assert.assertEquals(date6.toString(), "Tue Aug 26 08:12:14 GMT 1924");
        org.junit.Assert.assertNotNull(wildcardClass7);
    }

    @Test
    public void test086_1() throws Throwable {
        hu.chengming.util.client.InMemoryCounterPersistClient inMemoryCounterPersistClient0 = null;
        hu.chengming.util.InMemoryCounter<java.lang.CharSequence> charSequenceInMemoryCounter4 = new hu.chengming.util.InMemoryCounter<java.lang.CharSequence>(inMemoryCounterPersistClient0, (long) '#', 1L, (long) (byte) -1);
        long long6 = charSequenceInMemoryCounter4.increment((java.lang.CharSequence) "");
        long long9 = charSequenceInMemoryCounter4.increment((java.lang.CharSequence) "", (long) '4');
        long long11 = charSequenceInMemoryCounter4.increment((java.lang.CharSequence) "");
        long long13 = charSequenceInMemoryCounter4.increment((java.lang.CharSequence) "hi!");
        long long15 = charSequenceInMemoryCounter4.increment((java.lang.CharSequence) "hi!");
        long long17 = charSequenceInMemoryCounter4.increment((java.lang.CharSequence) "hi!");
        long long19 = charSequenceInMemoryCounter4.increment((java.lang.CharSequence) "");
        org.junit.Assert.assertTrue("'" + long6 + "' != '" + 1L + "'", long6 == 1L);
        org.junit.Assert.assertTrue("'" + long9 + "' != '" + 53L + "'", long9 == 53L);
        org.junit.Assert.assertTrue("'" + long11 + "' != '" + 54L + "'", long11 == 54L);
        org.junit.Assert.assertTrue("'" + long13 + "' != '" + 1L + "'", long13 == 1L);
        org.junit.Assert.assertTrue("'" + long15 + "' != '" + 2L + "'", long15 == 2L);
        org.junit.Assert.assertTrue("'" + long17 + "' != '" + 3L + "'", long17 == 3L);
        org.junit.Assert.assertTrue("'" + long19 + "' != '" + 55L + "'", long19 == 55L);
    }

    @Test
    public void test024_1() throws Throwable {
        com.apifortress.apiffaker.F f0 = new com.apifortress.apiffaker.F();
        MultiTainter.taintedReference(f0,"haha");
        java.lang.String str1 = f0.country();
        java.lang.String str2 = f0.cityPrefix();
        java.lang.String str3 = f0.mobile();
        org.junit.Assert.assertEquals("'" + str1 + "' != '" + "Kazakhstan" + "'", str1, "Kazakhstan");
// flaky "1) test024(TestGroup100Case0)":         org.junit.Assert.assertEquals("'" + str2 + "' != '" + "Lake" + "'", str2, "Lake");
// flaky "1) test024(TestGroup100Case0)":         org.junit.Assert.assertEquals("'" + str3 + "' != '" + "192-874-9079" + "'", str3, "192-874-9079");
    }
   @Test
    public void test092_1() throws Throwable {
        org.dataone.cn.dao.ReplicationDaoMetacatImpl replicationDaoMetacatImpl0 = MultiTainter.taintedReference(new org.dataone.cn.dao.ReplicationDaoMetacatImpl(), "a");
        org.dataone.cn.log.MetricEvent metricEvent1 = org.dataone.cn.log.MetricEvent.LOG_AGGREGATION_HARVEST_RETRIEVED;
        org.dataone.service.types.v1.NodeReference nodeReference2 = null;
        org.dataone.service.types.v1.Identifier identifier3 = null;
        org.dataone.cn.log.MetricLogEntry metricLogEntry5 = new org.dataone.cn.log.MetricLogEntry(metricEvent1, nodeReference2, identifier3, "smreplicationpolicy");
        org.dataone.cn.log.MetricEvent metricEvent6 = metricLogEntry5.getEvent();
        java.util.Date date7 = metricLogEntry5.getDateLogged();
        // The following exception was thrown during execution in test generation
        try {
//            int a = MultiTainter.taintedInt(0,"a");
            java.util.List<org.dataone.service.types.v1.Identifier> identifierList10 = replicationDaoMetacatImpl0.getCompletedCoordinatingNodeReplicasByDate(date7,a, (int) 'a');
            org.junit.Assert.fail("Expected exception of type org.springframework.jdbc.BadSqlGrammarException; message: PreparedStatementCallback; bad SQL grammar []; nested exception is org.h2.jdbc.JdbcSQLException: Table \"SMREPLICATIONSTATUS\" not found; SQL statement:?SELECT DISTINCT guid, date_verified  FROM  smreplicationstatus  WHERE date_verified <= ?   AND status = 'COMPLETED'   AND member_node = 'cnDev'  ORDER BY date_verified ASC; [42102-163]");
        } catch (org.springframework.jdbc.BadSqlGrammarException e) {
            // Expected exception.
        }
        org.junit.Assert.assertTrue("'" + metricEvent1 + "' != '" + org.dataone.cn.log.MetricEvent.LOG_AGGREGATION_HARVEST_RETRIEVED + "'", metricEvent1.equals(org.dataone.cn.log.MetricEvent.LOG_AGGREGATION_HARVEST_RETRIEVED));
        org.junit.Assert.assertTrue("'" + metricEvent6 + "' != '" + org.dataone.cn.log.MetricEvent.LOG_AGGREGATION_HARVEST_RETRIEVED + "'", metricEvent6.equals(org.dataone.cn.log.MetricEvent.LOG_AGGREGATION_HARVEST_RETRIEVED));
        org.junit.Assert.assertNotNull(date7);
        org.junit.Assert.assertEquals(date7.toString(), "Mon Aug 26 08:13:57 GMT 2024");
    }
}
