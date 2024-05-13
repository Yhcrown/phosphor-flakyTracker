package edu.utexas.ece.flakytracker.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FlakyTrackerBaseVistor extends MethodVisitor {


    public static String tainterClass = "edu/columbia/cs/psl/phosphor/runtime/MultiTainter";

    public FlakyTrackerBaseVistor(int api) {
        super(api);
    }

    public FlakyTrackerBaseVistor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    public void callTaintedMethod(String oriDescriptor) {


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
                methodName = "taintedReference";
                descriptor = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
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
}
