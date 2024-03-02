package edu.utexas.ece.flakytracker.agent;

import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class FlakyClassTracer extends ClassVisitor {


    public static List<API> nonDeterministicAPI;

    public static List<API> trackAPI;

    public static String tainterClass = "edu/columbia/cs/psl/phosphor/runtime/MultiTainter";

    public static String trackerProxyClass = "edu/utexas/ece/flakytracker/agent/FlakyUtil";

    public static String taintClassLabel = "edu/utexas/ece/flakytracker/agent/FlakyTaintLabel";

    public static String trackerFunction = "checkTainted";


    static int lineNumber;

    static String currentTestName;

    static {
        nonDeterministicAPI = new ArrayList<>();
        trackAPI = new ArrayList<>();
        API nextInt = new API("java/util/Random", "nextInt", "()I");
        // API nextLong = new API("java/util/Random", "nextLong", "()L");
        nonDeterministicAPI.addAll(Arrays.asList(nextInt));
        API assertEquals = new API("org/junit/Assert", "assertEquals", "()V");
        new API("org/junit/Assert", "assertNotEquals", "()V");
        trackAPI.addAll(Arrays.asList(assertEquals));
    }

    public FlakyClassTracer(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new FlakyMethodVisitor(api, mv);
                currentTestName = name;
            }
        }
        return mv;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        boolean isStatic = (access & ACC_STATIC) == 0;
//        if (isStatic)
        //TODO: track static fields
        return super.visitField(access, name, descriptor, signature, value);
    }

    private class FlakyMethodVisitor extends MethodVisitor {

        boolean isTestcase;
        public FlakyMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }


        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals("Lorg/junit/Test") && visible)
                isTestcase = true;
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public void visitEnd() {
            if (isTestcase)
                isTestcase = false;
            super.visitEnd();
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            lineNumber = line;
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            for (API api : trackAPI) {
                if (opcode == INVOKESTATIC && api.getOwner().equals(owner) && api.getName().equals(name)) {
                    if (API.isDoubleSlot(descriptor)) {
                        super.visitInsn(Opcodes.DUP2_X2);
                    } else {
                        super.visitInsn(DUP_X1);
                    }
                    if (API.getAssertType(descriptor) != null) {
                        switch (API.getAssertType(descriptor)) {
                            case "J":
                                super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                                break;
                            case "D":
                                super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                                break;
                            case "F":
                                super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                                break;
                            case "I":
                                super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                                break;
                        }
                    }
                    super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, trackerFunction, "(Ljava/lang/Object;)V", false);

                    if (API.isDoubleSlot(descriptor)) {
                        super.visitInsn(DUP2_X2);
                    } else {
                        super.visitInsn(DUP_X1);
                    }

                    if (API.getAssertType(descriptor) != null) {
                        switch (API.getAssertType(descriptor)) {
                            case "J":
                                super.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                                break;
                            case "D":
                                super.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                                break;
                            case "F":
                                super.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                                break;
                            case "I":
                                super.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                                break;
                        }
                    }

                    super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, trackerFunction, "(Ljava/lang/Object;)V", false);
                }
            }


            // lo11
            // lo12
            // lo21
            // lo22
            // lo11
            // lo12

             System.out.println(opcode+" "+owner+" "+name+" "+descriptor);
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            for (API api : nonDeterministicAPI) {
                if (opcode == INVOKEVIRTUAL && api.getOwner().equals(owner) && api.getName().equals(name) && api.getDescriptor().equals(descriptor)) {


                    super.visitTypeInsn(NEW, taintClassLabel);
                    super.visitInsn(DUP);
                    super.visitLdcInsn(FlakyTaintLabel.RANDOM);
                    super.visitLdcInsn(owner+"."+name);
                    super.visitLdcInsn(lineNumber);
                    super.visitLdcInsn(114514); //label
                    super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;II)V", false);
                    super.visitMethodInsn(INVOKESTATIC, tainterClass, "taintedInt", "(ILjava/lang/Object;)I", false);

                }
            }


        }
    }
}
