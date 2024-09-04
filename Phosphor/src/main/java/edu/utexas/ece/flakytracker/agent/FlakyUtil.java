package edu.utexas.ece.flakytracker.agent;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.columbia.cs.psl.phosphor.runtime.Taint;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class FlakyUtil {

    static Set<String> logHistory = new HashSet<>();


    public static <T> void checkTainted(T input, String testName) {
        if (input == null)
            return;

        Set<Object> labels = getTaintLabels(input);

        if (labels.isEmpty()) {
            return;
        }
        for (Object label : labels) {
            if (label instanceof FlakyTaintLabel) {
                FlakyTaintLabel taintLabel = (FlakyTaintLabel) label;
                if (!taintLabel.isInWhiteList(testName)) {
                    String log = "FlakyTracker Log: " + testName + " may be flaky: " + taintLabel.toString();
                    if (!logHistory.contains(log)) {
                        if (taintLabel.getType() != FlakyTaintLabel.STATIC) //TODO: too many false negative on static
                            System.out.println(log);
                        logHistory.add(log);
                    }
                }
            } else {
                System.out.println(label);
            }
        }
    }

    static <T> Set<Object>  getTaintLabels(T input){

        Object[] objectArray = null;
        if (input.getClass().isArray()) {
            int length = Array.getLength(input);
            objectArray = new Object[length];
            for (int i = 0; i < length; i++) {
                objectArray[i] = Array.get(input, i);
            }
        }
//        objectArray.
        Set<Object> labels = new HashSet<>();
        if (objectArray != null) {
            for (Object taint : objectArray) {
                if (taint instanceof String)
                    getTaintLabelsOnString(labels, (String) taint);
                else if (taint instanceof StringBuilder)
                    getTaintLabelsOnStringBuilder(labels, (StringBuilder) taint);
                else
                    labels.addAll(Arrays.asList(MultiTainter.getTaint(taint).getLabels()));
            }
        } else {
            if (input instanceof String)
                getTaintLabelsOnString(labels, (String) input);
            else if (input instanceof StringBuilder)
                getTaintLabelsOnStringBuilder(labels, (StringBuilder) input);
            labels.addAll(Arrays.asList(MultiTainter.getTaint(input).getLabels()));
        }
        return labels;
    }

    static void getTaintLabelsOnString(Set<Object> labels, String s) {
        for (int i = 0; i < s.length(); i++) {
            labels.addAll(Arrays.asList(MultiTainter.getTaint(s.charAt(i)).getLabels()));
        }
    }

    static void getTaintLabelsOnStringBuilder(Set<Object> labels, StringBuilder s) {
        for (int i = 0; i < s.length(); i++) {
            labels.addAll(Arrays.asList(MultiTainter.getTaint(s.charAt(i)).getLabels()));
        }
    }


    public static <T> void addWhiteList(T input, String testName) {
        if (input == null)
            return;
       Object[] labels = MultiTainter.getTaint(input).getLabels();
        for (Object label : labels) {
            if (label instanceof FlakyTaintLabel) {
                FlakyTaintLabel taintLabel = (FlakyTaintLabel) label;
                if ("STATIC".equals(taintLabel.getStringType()) || "FIELD".equals(taintLabel.getStringType())) {
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