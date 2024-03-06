package edu.utexas.ece.flakytracker.agent;


import edu.columbia.cs.psl.phosphor.runtime.Taint;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class FlakyTrackerAgent {


    public static boolean blackListContains(String s) {
        final String[] blackList = new String[]{"java.", "javax.", "sun.", "com.sun.", "jdk.", "org.xml.sax.", "net.bytebuddy.", "org.mockito.", "org.omg.CORBA.", "org.omg.CORBA_2_3.", "org.omg.CosNaming.", "org.omg.SendingContext.", "org.omg.stub.java.rmi.", "org.objenesis.", "org.mockito.", "com.google.", "org.apache.maven.", "org.apache.logging.", "org.eclipse.jetty.", "org.slf4j.", "org.junit.", "org.hamcrest.", "junit.", "sunw.io.", "sunw.util.", "com.intellij.","edu.columbia.cs.psl.phosphor."};

        for (int i = 0; i < blackList.length; ++i) {
            String libPackage = blackList[i];
            if (s.startsWith(libPackage)) {
                return true;
            }
        }
        return false;
    }

    public static void premain$$PHOSPHORTAGGED(String agentArgs, Taint a, Instrumentation inst, Taint b) {
        premain(agentArgs, inst);
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass,
                                    ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
                s = s.replaceAll("[/]", ".");
                if (!blackListContains(s)) {
                    System.out.println(s);
                    final ClassReader reader = new ClassReader(bytes);
                    final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                    FlakyClassTracer visitor = new FlakyClassTracer(writer);
                    reader.accept(visitor, 0);
                    return writer.toByteArray();
                }
                // else{
                //     System.out.println("---blacklist:"+s);
                // }
                return null;
            }
        });

    }


}
