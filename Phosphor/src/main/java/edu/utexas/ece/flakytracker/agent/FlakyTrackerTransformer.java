package edu.utexas.ece.flakytracker.agent;

import edu.columbia.cs.psl.phosphor.PhosphorBaseTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.security.ProtectionDomain;

public class FlakyTrackerTransformer extends PhosphorBaseTransformer {

    public static boolean blackListContains(String s) {
        final String[] blackList = new String[]{"java.", "javax.", "sun.", "com.sun.", "jdk.", "org.xml.sax.", "net.bytebuddy.", "org.mockito.", "org.omg.CORBA.", "org.omg.CORBA_2_3.", "org.omg.CosNaming.", "org.omg.SendingContext.", "org.omg.stub.java.rmi.", "org.objenesis.", "org.mockito.", "com.google.", "org.apache.maven.", "org.apache.logging.", "org.eclipse.jetty.", "org.slf4j.", "org.junit.", "org.hamcrest.", "junit.", "sunw.io.", "sunw.util.", "com.intellij.", "edu.columbia.cs.psl.phosphor."};

        for (int i = 0; i < blackList.length; ++i) {
            String libPackage = blackList[i];
            if (s.startsWith(libPackage)) {
                return true;
            }
        }
        return false;
    }

    public static boolean whiteListContains(String s) {
        final String[] whiteList = new String[]{"java.util.concurrent.ThreadLocalRandom","java.util.Random"};
        for (int i = 0; i < whiteList.length; ++i) {
            String libPackage = whiteList[i];
            if (s.startsWith(libPackage)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        className = className.replaceAll("[/]", ".");
        if (!blackListContains(className) ) {
            final ClassReader reader = new ClassReader(classfileBuffer);
            final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            FlakyClassTracer visitor = new FlakyClassTracer(writer);
            reader.accept(visitor, 0);
            return writer.toByteArray();
        }
        return null;
    }
}