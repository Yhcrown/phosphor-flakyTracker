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

    public static String tainterClass = "edu/columbia/cs/psl/phosphor/runtime/MultiTainter";

    public static String trackerProxyClass = "edu/utexas/ece/flakytracker/agent/FlakyUtil";

    public static String taintClassLabel = "edu/utexas/ece/flakytracker/agent/FlakyTaintLabel";

    public static String trackerFunction = "checkTainted";

    static int lineNumber;

    static String currentTestName;

    static String className;

    static List<String> globalFields;

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
        if (access == (ACC_PUBLIC | ACC_SUPER)){
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
        }else if("<clinit>".equals(name)){
            mv = new StaticFieldVisitor(api, mv);
        }else if ("<init>".equals(name)){

        }
        return mv;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        boolean isStatic = (access & ACC_STATIC) == 0;
        boolean isFinal = (access & ACC_FINAL) == 0;

        //TODO: track static fields
        if (!isFinal){
            globalFields.add(name);
        }


        return super.visitField(access, name, descriptor, signature, value);
    }


    private class StaticFieldVisitor extends MethodVisitor{

        public StaticFieldVisitor(int api) {
            super(api);
        }

        public StaticFieldVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

    }
    private class FlakyMethodVisitor extends MethodVisitor {

        boolean isTestcase;
        public FlakyMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        public void callTaintedMethod(String oriDescriptor){
            String returnType = API.getReturnType(oriDescriptor);
            String descriptor;
            String methodName;

            switch (returnType) {
                case "boolean":
                    methodName = "taintedBoolean";
                    descriptor = "(ZLjava/lang/Object;)Z";
                    break;
                case "byte":
                    methodName = "taintedByte";
                    descriptor = "(BLjava/lang/Object;)B";
                    break;
                case "char":
                    methodName = "taintedChar";
                    descriptor = "(CLjava/lang/Object;)C";
                    break;
                case "double":
                    methodName = "taintedDouble";
                    descriptor = "(DLjava/lang/Object;)D";
                    break;
                case "float":
                    methodName = "taintedFloat";
                    descriptor = "(FLjava/lang/Object;)F";
                    break;
                case "long":
                    methodName = "taintedLong";
                    descriptor = "(JLjava/lang/Object;)J";
                    break;
                case "int":
                    methodName = "taintedInt";
                    descriptor = "(ILjava/lang/Object;)I";
                    break;
                case "short":
                    methodName = "taintedShort";
                    descriptor = "(SLjava/lang/Object;)S";
                    break;
                case "boolean[]":
                    methodName = "taintedBooleanArray";
                    descriptor = "([ZLjava/lang/Object;)[Z";
                    break;
                case "byte[]":
                    methodName = "taintedByteArray";
                    descriptor = "([BLjava/lang/Object;)[B";
                    break;
                case "char[]":
                    methodName = "taintedCharArray";
                    descriptor = "([CLjava/lang/Object;)[C";
                    break;
                case "double[]":
                    methodName = "taintedDoubleArray";
                    descriptor = "([DLjava/lang/Object;)[D";
                    break;
                case "float[]":
                    methodName = "taintedFloatArray";
                    descriptor = "([FLjava/lang/Object;)[F";
                    break;
                case "long[]":
                    methodName = "taintedLongArray";
                    descriptor = "([JLjava/lang/Object;)[J";
                    break;
                case "int[]":
                    methodName = "taintedIntArray";
                    descriptor = "([ILjava/lang/Object;)[I";
                    break;
                case "short[]":
                    methodName = "taintedShortArray";
                    descriptor = "([SLjava/lang/Object;)[S";
                    break;
                default:
                    // 对于不支持的类型，可以抛出异常或者选择忽略
                    throw new IllegalArgumentException("Unsupported return type for tainting: " + returnType);
            }
            super.visitMethodInsn(Opcodes.INVOKESTATIC, tainterClass, methodName, descriptor, false);
        }


        public void callBoxingMethod(String primitiveType) {
            String wrapperClass;
            String descriptor;

            switch (primitiveType) {
                case "boolean":
                    wrapperClass = "java/lang/Boolean";
                    descriptor = "(Z)Ljava/lang/Boolean;";
                    break;
                case "byte":
                    wrapperClass = "java/lang/Byte";
                    descriptor = "(B)Ljava/lang/Byte;";
                    break;
                case "char":
                    wrapperClass = "java/lang/Character";
                    descriptor = "(C)Ljava/lang/Character;";
                    break;
                case "double":
                    wrapperClass = "java/lang/Double";
                    descriptor = "(D)Ljava/lang/Double;";
                    break;
                case "float":
                    wrapperClass = "java/lang/Float";
                    descriptor = "(F)Ljava/lang/Float;";
                    break;
                case "long":
                    wrapperClass = "java/lang/Long";
                    descriptor = "(J)Ljava/lang/Long;";
                    break;
                case "int":
                    wrapperClass = "java/lang/Integer";
                    descriptor = "(I)Ljava/lang/Integer;";
                    break;
                case "short":
                    wrapperClass = "java/lang/Short";
                    descriptor = "(S)Ljava/lang/Short;";
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported primitive type for boxing: " + primitiveType);
            }


            mv.visitMethodInsn(Opcodes.INVOKESTATIC, wrapperClass, "valueOf", descriptor, false);
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
            super.visitEnd();
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            lineNumber = line;
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode, owner, name, descriptor);

            if (opcode == PUTSTATIC){
                super.visitTypeInsn(NEW, taintClassLabel);
                super.visitInsn(DUP);
                super.visitLdcInsn(FlakyTaintLabel.FIELD);
                super.visitLdcInsn(owner+"."+name);
                super.visitLdcInsn(className);
                super.visitLdcInsn(lineNumber);
                super.visitLdcInsn(FlakyTaintLabel.getUniqueIndex()); //label
                super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
                callTaintedMethod(descriptor);
            }
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
                    super.visitLdcInsn(owner+"."+name);
                    super.visitLdcInsn(className);
                    super.visitLdcInsn(lineNumber);
                    super.visitLdcInsn(FlakyTaintLabel.getUniqueIndex()); //label
                    super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
                    callTaintedMethod(descriptor);
                }
            }


        }
    }
}
