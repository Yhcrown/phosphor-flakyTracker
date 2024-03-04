package edu.utexas.ece.flakytracker.agent;

import edu.columbia.cs.psl.phosphor.Configuration;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class FlakyClassTracer extends ClassVisitor {


    public static List<API> nonDeterministicAPI;

    public static List<API> trackAPI;


    public static String trackerProxyClass = "edu/utexas/ece/flakytracker/agent/FlakyUtil";

    public static String taintClassLabel = "edu/utexas/ece/flakytracker/agent/FlakyTaintLabel";

    public static String trackerFunction = "checkTainted";

    public static String addWhiteListFunction = "addWhiteList";

    static int lineNumber;

    static String currentTestName;

    static String className;

    static boolean haveClinit;

    static List<String[]> globalFields;

    static List<String[]> staticVaribles;

    static int labelIndex = 0;

    public static int getLabelIndex() {
        return labelIndex++;
    }

    static {
        nonDeterministicAPI = new ArrayList<>();
        trackAPI = new ArrayList<>();
        globalFields = new ArrayList<>();


        API nextInt = new API("java/util/Random", "nextInt", "()I");
        // API nextLong = new API("java/util/Random", "nextLong", "()L");
        nonDeterministicAPI.addAll(Arrays.asList(nextInt));
        API assertEquals = new API("org/junit/Assert", "assertEquals", "()V");
        new API("org/junit/Assert", "assertNotEquals", "()V");
        trackAPI.addAll(Arrays.asList(assertEquals));
    }

    public FlakyClassTracer(ClassVisitor cv) {
        super(Configuration.ASM_VERSION, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (access == (ACC_PUBLIC | ACC_SUPER)) {
            className = name;
        }
        super.visit(version, access, name, signature, superName, interfaces);
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
        } else if ("<clinit>".equals(name)) {
            mv = new StaticVisitor(api, mv);
            haveClinit = true;
        } else if ("<init>".equals(name)) {
            mv = new GlobalFieldVistor(api, mv);
        }
        return mv;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        boolean isStatic = (access & ACC_STATIC) == 0;
        boolean isFinal = (access & ACC_FINAL) == 0;

        //TODO: track static fields
        if (!isFinal && !isStatic) {
            globalFields.add(new String[]{name, descriptor});
        }

        if (!isFinal && isStatic) {
            staticVaribles.add(new String[]{name, descriptor});
        }


        return super.visitField(access, name, descriptor, signature, value);
    }


    private class GlobalFieldVistor extends FlakyTrackerBaseVistor{

        public GlobalFieldVistor(int api) {
            super(api);
        }

        public GlobalFieldVistor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            taintAllGlobal();
        }

        public void taintAllGlobal(){
            for (String[] globalField : globalFields) {
                String fieldName = globalField[0];
                String fieldDescriptor = globalField[1];
                visitFieldInsn(GETFIELD, className, fieldName, fieldDescriptor);

                super.visitTypeInsn(NEW, taintClassLabel);
                super.visitInsn(DUP);

                super.visitLdcInsn(FlakyTaintLabel.FIELD);

                super.visitLdcInsn(fieldName);
                super.visitLdcInsn(className);
                super.visitLdcInsn(lineNumber);
                super.visitLdcInsn(getLabelIndex()); //label
                super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);

                callTaintedMethod(fieldDescriptor);
            }
        }
    }

    private class StaticVisitor extends FlakyTrackerBaseVistor {

        public StaticVisitor(int api) {
            super(api);
        }

        public StaticVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }

        public void taintAllStatic() {
            for (String[] staticVarible : staticVaribles) {
                String fieldName = staticVarible[0];
                String fieldDescriptor = staticVarible[1];
                visitFieldInsn(GETSTATIC, className, fieldName, fieldDescriptor);

                super.visitTypeInsn(NEW, taintClassLabel);
                super.visitInsn(DUP);

                super.visitLdcInsn(FlakyTaintLabel.STATIC);

                super.visitLdcInsn(fieldName);
                super.visitLdcInsn(className);
                super.visitLdcInsn(lineNumber);
                super.visitLdcInsn(getLabelIndex()); //label
                super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);

                callTaintedMethod(fieldDescriptor);

            }

        }


    }

    private class FlakyMethodVisitor extends FlakyTrackerBaseVistor {

        boolean isTestcase;

        public FlakyMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }


        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals("Lorg/junit/Test") && visible) {
                isTestcase = true;
            }
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public void visitEnd() {
            if (isTestcase) {
                isTestcase = false;
            }
            if (!haveClinit){
                MethodVisitor methodVisitor = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
                StaticVisitor staticVisitor = (StaticVisitor)methodVisitor;
                staticVisitor.visitCode();
                // Insert taint code for each static field
                staticVisitor.taintAllStatic();

                staticVisitor.visitInsn(Opcodes.RETURN);
                staticVisitor.visitMaxs(-1, -1); // Auto-computed
                staticVisitor.visitEnd();
            }

            super.visitEnd();
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            lineNumber = line;
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {


            if (opcode == GETFIELD) {
                if (className.equals(owner)) {
                    if (API.isDoubleSlot(descriptor))
                        super.visitInsn(DUP2);
                    else
                        super.visitInsn(DUP);
                    super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, addWhiteListFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);
                }
            }

            if (opcode == GETSTATIC) {
                if (API.isDoubleSlot(descriptor))
                    super.visitInsn(DUP2);
                else
                    super.visitInsn(DUP);
                super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, addWhiteListFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);
            }

            super.visitFieldInsn(opcode, owner, name, descriptor);
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

                    String assertType = API.getAssertType(descriptor);
                    if (API.isPrimitiveType(assertType))
                        callBoxingMethod(assertType);


                    super.visitLdcInsn(currentTestName);


                    // lo11
                    // lo12
                    // lo21
                    // lo22
                    // lo11
                    // lo12


                    super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, trackerFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);

                    if (API.isDoubleSlot(descriptor)) {
                        super.visitInsn(DUP2_X2);
                    } else {
                        super.visitInsn(DUP_X1);
                    }

                    if (API.isPrimitiveType(assertType))
                        callBoxingMethod(assertType);

                    super.visitLdcInsn(currentTestName);

                    super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, trackerFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);
                }
            }


            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            for (API api : nonDeterministicAPI) {
                if (opcode == INVOKEVIRTUAL && api.getOwner().equals(owner) && api.getName().equals(name) && api.getDescriptor().equals(descriptor)) {


                    super.visitTypeInsn(NEW, taintClassLabel);
                    super.visitInsn(DUP);
                    super.visitLdcInsn(FlakyTaintLabel.RANDOM);
                    super.visitLdcInsn(owner + "." + name);
                    super.visitLdcInsn(className);
                    super.visitLdcInsn(lineNumber);
                    super.visitLdcInsn(getLabelIndex()); //label
                    super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
                    callTaintedMethod(descriptor);
                }
            }


        }
    }
}
