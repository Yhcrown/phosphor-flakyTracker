package edu.utexas.ece.flakytracker.agent;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.columbia.cs.psl.phosphor.runtime.Taint;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FlakyUtil {

    static Set<String> logHistory = new HashSet<>();

    public static <T> void checkTainted(T a, String testName) {
//        System.out.println("come in "+testName);
//        System.out.println("");
        Taint taint = MultiTainter.getTaint(a);
        for (Object label : taint.getLabels()) {
            if (label instanceof FlakyTaintLabel) {
                FlakyTaintLabel taintLabel = (FlakyTaintLabel) label;

                if (!taintLabel.isInWhiteList(testName)) {
                    String log = testName + " may be flaky: " + taintLabel.toString();
                    if (!logHistory.contains(log)) {
                        System.out.println(log);
                        logHistory.add(log);
                    }
                }
            }
        }
        if (taint.getLabels().length == 0) {
            System.out.println(testName + " is not flaky");
        }
    }


    public static <T> void addWhiteList(T a, String testName) {
//        System.out.println("come in white");
        Taint taint = MultiTainter.getTaint(a);
        for (Object label : taint.getLabels()) {
            if (label instanceof FlakyTaintLabel) {
                FlakyTaintLabel taintLabel = (FlakyTaintLabel) label;
                if ("STATIC".equals(taintLabel.getStringType()) || "FIELD".equals(taintLabel.getStringType())){
                    taintLabel.addWhiteList(testName);
                }
            }
        }
    }

//    public static void main(String[] args) throws IOException {
//        String className = "flaky.FlakyDemo";
//        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
//        boolean asmCode = true;
//
//
//        ClassReader reader = new ClassReader(className);
//
//
//        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
//        FlakyClassTracer visitor = new FlakyClassTracer(writer);
//        reader.accept(visitor, 0);
//
//        FileUtils.writeByteArrayToFile(new File("target/classes/flaky/FlakyDemo7.class"), writer.toByteArray());
//
//
//    }


}