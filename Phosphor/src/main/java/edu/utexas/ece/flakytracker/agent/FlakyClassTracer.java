package edu.utexas.ece.flakytracker.agent;

import edu.columbia.cs.psl.phosphor.Configuration;
import org.objectweb.asm.*;

import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class FlakyClassTracer extends ClassVisitor {


    public static List<API> nonDeterministicAPI;

    public static List<API> trackAPI;

    public static List<API> nonDeterministicClass;

    public static List<API> allAPINeedToBeTainted;


    public static String trackerProxyClass = "edu/utexas/ece/flakytracker/agent/FlakyUtil";

    public static String taintClassLabel = "edu/utexas/ece/flakytracker/agent/FlakyTaintLabel";

    public static String trackerFunction = "checkTainted";

    public static String addWhiteListFunction = "addWhiteList";

    int lineNumber;

    String currentTestName;

    String className;

    boolean haveClinit = false;

    List<String[]> globalFields = new ArrayList<>();

    List<String[]> staticVaribles = new ArrayList<>();

    List<String[]> publicMethods = new ArrayList<>();

    static int labelIndex = 0;

    public static int getLabelIndex() {
        return labelIndex++;
    }

    static {
        nonDeterministicAPI = new ArrayList<>();
        trackAPI = new ArrayList<>();
        nonDeterministicClass = new ArrayList<>();
        allAPINeedToBeTainted = new ArrayList<>();
//        API nextInt = new API("java/util/Random", "nextInt", "()I");
//        API nextIntI = new API("java/util/Random", "nextInt", "(I)I");
        API ThreadLocalCurrent = new API("java/util/concurrent/ThreadLocalRandom", "current", "()Ljava/util/concurrent/ThreadLocalRandom;",FlakyTaintLabel.RANDOM);
        API hashCode = new API("ANY","hashCode", "()I",FlakyTaintLabel.HASHCODE);
        API CalenderInstance = new API("java/util/Calendar","getInstance","()Ljava/util/Calendar;",FlakyTaintLabel.TIME);
        API LocalDateTimeNow = new API("java/time/LocalDateTime","now","()Ljava/time/LocalDataTime;",FlakyTaintLabel.TIME);
        API LocalDateNow = new API("java/time/LocalDate","now","()Ljava/time/LocalDate;",FlakyTaintLabel.TIME);
        API LocalTimeNow = new API("java/time/LocalTime","now","()Ljava/time/LocalTime;",FlakyTaintLabel.TIME);
        API LocalDateTimeNowZone = new API("java/time/LocalDateTime", "now", "(Ljava/time/ZoneId;)Ljava/time/LocalDataTime;",FlakyTaintLabel.TIME);
        API LocalDateNowZone = new API("java/time/LocalDate","now","(Ljava/time/ZoneId;)Ljava/time/LocalDate;",FlakyTaintLabel.TIME);
        API LocalTimeNowZone = new API("java/time/LocalTime","now","(Ljava/time/ZoneId;)Ljava/time/LocalTime;",FlakyTaintLabel.TIME);
        API ClockSystemUTC = new API("java/time/Clock","systemUTC","()Ljava/time/Clock;",FlakyTaintLabel.TIME);
        API ClockSystemDefaultZone = new API("java/time/Clock","systemDefaultZone","()Ljava/time/Clock;",FlakyTaintLabel.TIME);
        API ClockMillis = new API("java/time/Clock","millis","()J",FlakyTaintLabel.TIME);
        API ClockInstant = new API("java/time/Clock","instant","()Ljava/time/Instant;",FlakyTaintLabel.TIME);
        API ClockSystem = new API("java/time/Clock","system","(Ljava/time/ZoneId)Ljava/time/Clock;",FlakyTaintLabel.TIME);
        //TODO: need refine the logic of Clock here;



        API InstantNow = new API("java/time/Instant","now","()Ljava/time/Instant;",FlakyTaintLabel.TIME);
        API ZonedDataTimeNow = new API("java/time/ZonedDateTime","now","()Ljava/time/ZonedDateTime;",FlakyTaintLabel.TIME);
        API ZonedDataTimeNowZone = new API("java/time/ZonedDateTime","now","(Ljava/time/ZoneId;)Ljava/time/ZonedDateTime;",FlakyTaintLabel.TIME);
        API OffsetDateTimeNow = new API("java/time/OffsetDateTime","now","()Ljava/time/OffsetDateTime;",FlakyTaintLabel.TIME);
        API OffsetDateTimeNowZone = new API("java/time/OffsetDateTime","now","(Ljava/time/ZoneId;)Ljava/time/OffsetDateTime;",FlakyTaintLabel.TIME);
        API YearMonthNow = new API("java/time/YearMonth","now","()Ljava/time/YearMonth;",FlakyTaintLabel.TIME);
        API YearMonthNowZone = new API("java/time/YearMonth","now","(Ljava/time/ZoneId;)Ljava/time/YearMonth;",FlakyTaintLabel.TIME);
        API MonthDayNow = new API("java/time/MonthDayNow","now","()Ljava/time/MonthDay;",FlakyTaintLabel.TIME);
        API MonthDayNowZone = new API("java/time/MonthDayNow","now","(Ljava/time/ZoneId;)Ljava/time/MonthDay;",FlakyTaintLabel.TIME);
        API YearNow = new API("java/time/Year","now","()Ljava/time/Year;",FlakyTaintLabel.TIME);
        API YearNowZone = new API("java/time/Year","now","(Ljava/time/ZoneId;)Ljava/time/Year;",FlakyTaintLabel.TIME);

        API SystemNanoTime = new API("java/lang/System","nanoTime","()J",FlakyTaintLabel.TIME);
        API SystemCurrentTimeMillis = new API("java/lang/System","currentTimeMillis","()J",FlakyTaintLabel.TIME);
        API RandomUUID = new API("java/util/UUID","randomUUID","()Ljava/util/UUID;",FlakyTaintLabel.RANDOM);
        //TODO: note this class will be replaced by Phosphor

        API SystemGetEnv = new API("java/lang/System","getenv","(Ljava/lang/String;)Ljava/lang/String;",FlakyTaintLabel.ENVIRONMENT);
        API SystemGetEnvMap = new API("java/lang/System","getenv","()Ljava/util/Map;",FlakyTaintLabel.ENVIRONMENT);
        API SystemGetProperty = new API("java/lang/System","getProperty","()Ljava/lang/String;",FlakyTaintLabel.ENVIRONMENT);

        API TotalMemory = new API("java/lang/Runtime","totalMemory","()J",FlakyTaintLabel.RESOURCE);
        API FreeMemory = new API("java/lang/Runtime","freeMemory","()J",FlakyTaintLabel.RESOURCE);
        API MaxMemory = new API("java/lang/Runtime","maxMemory","()J",FlakyTaintLabel.RESOURCE);
        API AvailableProcessors = new API("java/lang/Runtime","availableProcessors","()I",FlakyTaintLabel.RESOURCE);

        API InetAddressGetLocalHost = new API("java/net/InetAddress","getLocalHost","()Ljava/net/InetAddress;",FlakyTaintLabel.ENVIRONMENT);
        API InetAddressGetByName = new API("java/net/InetAddress", "getByName", "(Ljava/lang/String;)Ljava/net/InetAddress;", FlakyTaintLabel.NETWORK);
        API InetAddressGetAllByName = new API("java/net/InetAddress", "getAllByName", "(Ljava/lang/String;)[Ljava/net/InetAddress;", FlakyTaintLabel.NETWORK);
        API SocketConnect = new API("java/net/Socket", "connect", "(Ljava/net/SocketAddress;I)V", FlakyTaintLabel.NETWORK);
        API HttpURLConnectionConnect = new API("java/net/HttpURLConnection", "connect", "()V", FlakyTaintLabel.NETWORK);
        API SocketChannelConnect = new API("java/nio/channels/SocketChannel", "connect", "(Ljava/net/SocketAddress;)Z", FlakyTaintLabel.NETWORK);
        API AsynchronousSocketChannelGetLocalAddress = new API("java/nio/channels/AsynchronousSocketChannel", "getLocalAddress", "()Ljava/net/SocketAddress;", FlakyTaintLabel.ENVIRONMENT);
        API SocketGetLocalAddress = new API("java/net/Socket", "getLocalAddress", "()Ljava/net/InetAddress;", FlakyTaintLabel.ENVIRONMENT);
        API NetworkInterfaceGetByInetAddress = new API("java/net/NetworkInterface", "getByInetAddress", "(Ljava/net/InetAddress;)Ljava/net/NetworkInterface;", FlakyTaintLabel.ENVIRONMENT);
        API NetworkInterfaceIsUp = new API("java/net/NetworkInterface", "isUp", "()Z", FlakyTaintLabel.ENVIRONMENT);
        API ServerSocketBind = new API("java/net/ServerSocket", "bind", "(Ljava/net/SocketAddress;)V", FlakyTaintLabel.ENVIRONMENT);
        API DatagramSocketBind = new API("java/net/DatagramSocket", "bind", "(Ljava/net/SocketAddress;)V", FlakyTaintLabel.ENVIRONMENT);
        API ServerSocketAccept = new API("java/net/ServerSocket", "accept", "()Ljava/net/Socket;", FlakyTaintLabel.NETWORK);
        API AsynchronousServerSocketChannelAccept = new API("java/nio/channels/AsynchronousServerSocketChannel", "accept", "()Ljava/util/concurrent/Future;", FlakyTaintLabel.NETWORK);
        API NetworkInterfaceGetNetworkInterfaces = new API("java/net/NetworkInterface", "getNetworkInterfaces", "()Ljava/util/Enumeration;", FlakyTaintLabel.ENVIRONMENT);

        API Inet4AddressGetLocalHost = new API("java/net/Inet4Address","getLocalHost","()Ljava/net/InetAddress;",FlakyTaintLabel.ENVIRONMENT);
        API Inet6AddressGetLocalHost = new API("java/net/Inet6Address","getLocalHost","()Ljava/net/InetAddress;",FlakyTaintLabel.ENVIRONMENT);

        API Inet4AddressGetByName = new API("java/net/Inet4Address", "getByName", "(Ljava/lang/String;)Ljava/net/InetAddress;", FlakyTaintLabel.NETWORK);
        API Inet6AddressGetByName = new API("java/net/Inet6Address", "getByName", "(Ljava/lang/String;)Ljava/net/InetAddress;", FlakyTaintLabel.NETWORK);


        API SSLSocketConnect = new API("javax/net/ssl/SSLSocket", "connect", "(Ljava/net/SocketAddress;I)V", FlakyTaintLabel.NETWORK);


        API DatagramChannelConnect = new API("java/nio/channels/DatagramChannel", "connect", "(Ljava/net/SocketAddress;)Z", FlakyTaintLabel.NETWORK);



        API SSLServerSocketBind = new API("javax/net/ssl/SSLServerSocket", "bind", "(Ljava/net/SocketAddress;)V", FlakyTaintLabel.ENVIRONMENT);


        API SSLServerSocketAccept = new API("javax/net/ssl/SSLServerSocket", "accept", "()Ljava/net/Socket;", FlakyTaintLabel.NETWORK);


        
//        Socket
//System.currentTimeMillis()

//        API
        //        System.getenv()

        nonDeterministicAPI.addAll(Arrays.asList(
                ThreadLocalCurrent,
//                hashCode,
                CalenderInstance,
                LocalDateTimeNow,
                LocalDateNow,
                LocalTimeNow,
                LocalDateTimeNowZone,
                LocalDateNowZone,
                LocalTimeNowZone,
                ClockSystemUTC,
                ClockSystemDefaultZone,
                ClockMillis,
                ClockInstant,
                ClockSystem,
                InstantNow,
                ZonedDataTimeNow,
                ZonedDataTimeNowZone,
                OffsetDateTimeNow,
                OffsetDateTimeNowZone,
                YearMonthNow,
                YearMonthNowZone,
                MonthDayNow,
                MonthDayNowZone,
                YearNow,
                YearNowZone,
                SystemNanoTime,
                SystemCurrentTimeMillis,
                RandomUUID,
                SystemGetEnv,
                SystemGetEnvMap,
                SystemGetProperty,
                TotalMemory,
                FreeMemory,
                MaxMemory,
                AvailableProcessors,
                InetAddressGetLocalHost,
                InetAddressGetByName,
                InetAddressGetAllByName,
                SocketConnect,
                HttpURLConnectionConnect,
                SocketChannelConnect,
                AsynchronousSocketChannelGetLocalAddress,
                SocketGetLocalAddress,
                NetworkInterfaceGetByInetAddress,
                NetworkInterfaceIsUp,
                ServerSocketBind,
                DatagramSocketBind,
                ServerSocketAccept,
                AsynchronousServerSocketChannelAccept,
                NetworkInterfaceGetNetworkInterfaces,
                Inet4AddressGetLocalHost,
                Inet6AddressGetLocalHost,
                Inet4AddressGetByName,
                Inet6AddressGetByName,
                SSLSocketConnect,
                DatagramChannelConnect,
                SSLServerSocketBind,
                SSLServerSocketAccept
        ));

        API DateClass = new API("java/util/Date","","()V",FlakyTaintLabel.TIME);
        API RandomClass = new API("java/util/Random", "", "()V",FlakyTaintLabel.RANDOM);
        API ThreadLocalRandomClass = new API("java/util/concurrent/ThreadLocalRandom", "java/util/Random", "()V", FlakyTaintLabel.RANDOM);
        nonDeterministicClass.addAll(Arrays.asList(RandomClass, ThreadLocalRandomClass,DateClass));

        allAPINeedToBeTainted.addAll(Arrays.asList(ThreadLocalRandomClass));


        API assertEquals = new API("org/junit/Assert", "assertEquals", "()V");
        API assertNotEquals = new API("org/junit/Assert", "assertNotEquals", "()V");
        API assertNotNull = new API("org/junit/Assert", "assertNotNull", "()V");
        API assertNull = new API("org/junit/Assert", "assertNull", "()V");
        API assertSame = new API("org/junit/Assert", "assertSame", "()V");
        API assertNotSame = new API("org/junit/Assert", "assertNotSame", "()V");
        API assertThat = new API("org/junit/Assert", "assertThat", "()V");
        API assertTrue = new API("org/junit/Assert", "assertTrue", "()V");
        API assertFalse = new API("org/junit/Assert", "assertFalse", "()V");
        trackAPI.addAll(Arrays.asList(assertEquals, assertNotEquals, assertNotNull, assertNull, assertNotEquals, assertSame, assertNotSame, assertThat, assertTrue, assertFalse));
    }

    public FlakyClassTracer(ClassVisitor cv) {
        super(Configuration.ASM_VERSION, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {

//        if (!haveClinit) {
//            MethodVisitor methodVisitor = visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
//            StaticVisitor staticVisitor = (StaticVisitor) methodVisitor;
//            staticVisitor.visitCode();
//            // Insert taint code for each static field
//            staticVisitor.taintAllStatic();
//
//            staticVisitor.visitInsn(Opcodes.RETURN);
//            staticVisitor.visitMaxs(-1, -1); // Auto-computed
//            staticVisitor.visitEnd();
//        }
        super.visitEnd();
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
            mv = new FlakyMethodVisitor(api, mv);
        }

        if (access == ACC_PUBLIC)
            publicMethods.add(new String[]{name, descriptor});

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


//    private class GlobalFieldVistor extends FlakyTrackerBaseVistor {
//
//        public GlobalFieldVistor(int api) {
//            super(api);
//        }
//
//        public GlobalFieldVistor(int api, MethodVisitor methodVisitor) {
//            super(api, methodVisitor);
//        }
//
//        @Override
//        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
//            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
//            // check if call super()
//            if (opcode == INVOKESPECIAL && name.equals("<init>")) {
//                // must instrument after super()
//                taintAllGlobal();
//            }
//        }
//
//        public void taintAllGlobal() {
//            for (String[] globalField : globalFields) {
//                String fieldName = globalField[0];
//                String fieldDescriptor = globalField[1];
//
//                super.visitVarInsn(ALOAD, 0); // load this reference
//                super.visitFieldInsn(GETFIELD, className, fieldName, fieldDescriptor);
//
//                super.visitTypeInsn(NEW, taintClassLabel);
//                super.visitInsn(DUP);
//
//                super.visitLdcInsn(FlakyTaintLabel.FIELD);
//
//                super.visitLdcInsn(fieldName);
//                super.visitLdcInsn(className);
//                super.visitLdcInsn(lineNumber);
//                super.visitLdcInsn(getLabelIndex()); //label
//                super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
//
//                callTaintedMethod(fieldDescriptor);
//            }
//        }
//    }

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
                super.visitFieldInsn(GETSTATIC, className, fieldName, fieldDescriptor);

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

//        @Override
//        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
//
//
//            super.visitFieldInsn(opcode, owner, name, descriptor);
//
//
//            if (opcode == PUTSTATIC) {
//                super.visitFieldInsn(GETSTATIC, owner, name, descriptor);
//
//                super.visitTypeInsn(NEW, taintClassLabel);
//                super.visitInsn(DUP);
//                super.visitLdcInsn(FlakyTaintLabel.STATIC);
//                super.visitLdcInsn(name);
//                super.visitLdcInsn(className);
//                super.visitLdcInsn(-1);
//                super.visitLdcInsn(getLabelIndex()); //label
//                super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
//                callTaintedMethod(descriptor);
//
//
//                String type = API.getType(descriptor);
//                if (!API.isPrimitiveType(type))
//                    super.visitTypeInsn(CHECKCAST, API.processClassType(descriptor));
//
//                for (API clazz : nonDeterministicClass) {
//                    if (("L" + clazz.getOwner() + ";").equals(descriptor)) {
//                        super.visitTypeInsn(NEW, taintClassLabel);
//                        super.visitInsn(DUP);
//                        super.visitLdcInsn(clazz.getFlakyType());
//                        super.visitLdcInsn(owner + "." + name);
//                        super.visitLdcInsn(className);
//                        super.visitLdcInsn(lineNumber);
//                        super.visitLdcInsn(getLabelIndex()); //label
//                        super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
//                        super.visitMethodInsn(Opcodes.INVOKESTATIC, tainterClass, "taintedReference", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
//
//                        super.visitTypeInsn(CHECKCAST, clazz.getOwner());
//                    }
//
//
//                }
//
//
//                super.visitFieldInsn(opcode, owner, name, descriptor);
//
//            }
//
//        }


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


            super.visitEnd();
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            lineNumber = line;
            super.visitLineNumber(line, start);
        }

//        @Override
//        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
//
//
//            super.visitFieldInsn(opcode, owner, name, descriptor);
//
//
//            if (opcode == PUTSTATIC) {
//                super.visitFieldInsn(GETSTATIC, owner, name, descriptor);
//
//                super.visitTypeInsn(NEW, taintClassLabel);
//                super.visitInsn(DUP);
//                super.visitLdcInsn(FlakyTaintLabel.STATIC);
//                super.visitLdcInsn(name);
//                super.visitLdcInsn(className);
//                super.visitLdcInsn(lineNumber);
//                super.visitLdcInsn(getLabelIndex()); //label
//                super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
//                callTaintedMethod(descriptor);
//
//
//                String type = API.getType(descriptor);
//                if (!API.isPrimitiveType(type))
//                    super.visitTypeInsn(CHECKCAST, API.processClassType(descriptor));
//
//                super.visitFieldInsn(opcode, owner, name, descriptor);
//
//                //add whiteList
//
//                super.visitFieldInsn(GETSTATIC, owner, name, descriptor);
//
//
//                if (API.isPrimitiveType(type))
//                    callBoxingMethod(type);
//
//                super.visitLdcInsn(currentTestName);
//
//                super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, addWhiteListFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);
//
//            }
//        }


        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            String[] paramTypes = API.getParamTypes(descriptor);
            for (API api : trackAPI) {
                if (opcode == INVOKESTATIC && api.getOwner().equals(owner) && api.getName().equals(name)) {
                    if (paramTypes.length >= 2 && !"java/lang/String".equals(paramTypes[paramTypes.length - 2])) {   // message, actual, expected
                        String assertType = API.getAssertType(descriptor);

                        if (API.isDoubleSlot(assertType)) {
                            super.visitInsn(Opcodes.DUP2_X2);
                        } else {
                            super.visitInsn(DUP_X1);
                        }

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

                        if (("java/lang/String").equals(paramTypes[paramTypes.length - 2]))
                            break;

                        if (API.isDoubleSlot(assertType)) {
                            super.visitInsn(DUP2_X2);
                        } else {
                            super.visitInsn(DUP_X1);
                        }

                        if (API.isPrimitiveType(assertType))
                            callBoxingMethod(assertType);

                        super.visitLdcInsn(currentTestName);

                        super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, trackerFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);

                        break;
                    }
//                    else if (paramTypes.length == 2 && paramTypes[0].equals("java/lang/String")) { //not null, message
//                        String assertType = API.getAssertType(descriptor);
//
//                        if (API.isDoubleSlot(assertType)) {
//                            super.visitInsn(DUP2_X2);
//                        } else {
//                            super.visitInsn(DUP_X1);
//                        }
//
//                        if (API.isPrimitiveType(assertType))
//                            callBoxingMethod(assertType);
//
//                        super.visitLdcInsn(currentTestName);
//
//                        super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, trackerFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);
//                    } else if (paramTypes.length == 2) { //expected, actual
//                        String assertType = API.getAssertType(descriptor);
//
//                        if (API.isDoubleSlot(assertType)) {
//                            super.visitInsn(DUP2_X2);
//                        } else {
//                            super.visitInsn(DUP_X1);
//                        }
//
//                        if (API.isPrimitiveType(assertType))
//                            callBoxingMethod(assertType);
//
//
//                        super.visitLdcInsn(currentTestName);
//
//
//                        super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, trackerFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);
//
//                    }
                    else if (paramTypes.length == 1 || (paramTypes.length == 2 && "java/lang/String".equals(paramTypes[0]))) {
                        String assertType = API.getAssertType(descriptor);

                        if (API.isDoubleSlot(assertType)) {
                            super.visitInsn(Opcodes.DUP2);
                        } else {
                            super.visitInsn(DUP);
                        }

                        if (API.isPrimitiveType(assertType))
                            callBoxingMethod(assertType);


                        super.visitLdcInsn(currentTestName);


                        super.visitMethodInsn(INVOKESTATIC, trackerProxyClass, trackerFunction, "(Ljava/lang/Object;Ljava/lang/String;)V", false);

                        break;
                    }

                }
            }


            Label notThreadLocalRandom = new Label();
            Label isThreadLocalRandom = new Label();
            Label end= new Label();

            for (API api : allAPINeedToBeTainted) {
                if (opcode == INVOKEVIRTUAL && (api.owner.equals(owner) || api.name.equals(owner)) && !API.getReturnType(descriptor).equals("void")) {

                    if (paramTypes.length == 0) {
                        super.visitInsn(DUP);
                    } else if (paramTypes.length == 1 && !API.isDoubleSlot(paramTypes[0])) {  // lo1 target, -->dup_x1 lo1 target lo1 -->  target target lo1
                        super.visitInsn(DUP_X1);
                        super.visitInsn(POP);
                        super.visitInsn(DUP_X1);
                    } else if (paramTypes.length == 1 && API.isDoubleSlot(paramTypes[0])) { // lo1 lo2 target -->     target lo1 lo2 target
                        super.visitInsn(DUP2_X1);
                        super.visitInsn(POP2);
                        super.visitInsn(DUP_X2);
                    } else if (paramTypes.length == 2 && !API.isDoubleSlot(paramTypes[0]) && !API.isPrimitiveType(paramTypes[1])) {
                        super.visitInsn(DUP2_X1);
                        super.visitInsn(POP2);
                        super.visitInsn(DUP_X2);

                    } else {
                        break; //Do not support
                    }

                    // la1, la2 , target


                    // la1,  la2, lb, lc1, lc2, target
                    super.visitTypeInsn(Opcodes.INSTANCEOF, api.owner);
                    super.visitJumpInsn(IFEQ, notThreadLocalRandom); ///
                    super.visitLabel(isThreadLocalRandom);


                }
            }

            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
//            super.visitLabel(notThreadLocalRandom);
            for (API api : allAPINeedToBeTainted) {
                if (opcode == INVOKEVIRTUAL && (api.owner.equals(owner) || api.name.equals(owner)) && !API.getReturnType(descriptor).equals("void")) {


                    super.visitTypeInsn(NEW, taintClassLabel);
                    super.visitInsn(DUP);
                    super.visitLdcInsn(FlakyTaintLabel.RANDOM);
                    super.visitLdcInsn(api.owner + "." + name);
                    super.visitLdcInsn(className);
                    super.visitLdcInsn(lineNumber);
                    super.visitLdcInsn(getLabelIndex()); //label
                    super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
                    callTaintedMethod(descriptor);


                    super.visitJumpInsn(GOTO,end);

                    super.visitLabel(notThreadLocalRandom);



                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);


                    super.visitLabel(end);


                }
            }

//            super.visitLabel(notThreadLocalRandom);


            for (API api : nonDeterministicAPI) {
                if ((opcode == INVOKEVIRTUAL || opcode == INVOKESTATIC) && (api.getOwner().equals(owner) || api.getOwner().equals("ANY"))&& api.getName().equals(name) && api.getDescriptor().equals(descriptor)) {


                    super.visitTypeInsn(NEW, taintClassLabel);
                    super.visitInsn(DUP);
                    super.visitLdcInsn(api.flakyType);
                    super.visitLdcInsn(owner + "." + name);
                    super.visitLdcInsn(className);
                    super.visitLdcInsn(lineNumber);
                    super.visitLdcInsn(getLabelIndex()); //label
                    super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
                    callTaintedMethod(descriptor);
                    if (!API.isPrimitiveType(api.returnType))
                        super.visitTypeInsn(CHECKCAST, api.ASMreturType);
                    return;
                }
            }

            for (API clazz : nonDeterministicClass) {
                if (opcode == INVOKESPECIAL && clazz.getOwner().equals(owner) && "<init>".equals(name)) {
                    super.visitTypeInsn(NEW, taintClassLabel);
                    super.visitInsn(DUP);
                    super.visitLdcInsn(clazz.getFlakyType());
                    super.visitLdcInsn(owner + "." + name);
                    super.visitLdcInsn(className);
                    super.visitLdcInsn(lineNumber);
                    super.visitLdcInsn(getLabelIndex()); //label
                    super.visitMethodInsn(INVOKESPECIAL, taintClassLabel, "<init>", "(ILjava/lang/String;Ljava/lang/String;II)V", false);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, tainterClass, "taintedReference", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);

                    super.visitTypeInsn(CHECKCAST, clazz.getOwner());
                    return;
                }


            }


        }
    }
 }
