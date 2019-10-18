package edu.columbia.cs.psl.phosphor.runtime;

import edu.columbia.cs.psl.phosphor.Configuration;
import edu.columbia.cs.psl.phosphor.Instrumenter;
import edu.columbia.cs.psl.phosphor.TaintUtils;
import edu.columbia.cs.psl.phosphor.instrumenter.InvokedViaInstrumentation;
import edu.columbia.cs.psl.phosphor.struct.*;
import edu.columbia.cs.psl.phosphor.struct.harmony.util.ArrayList;
import edu.columbia.cs.psl.phosphor.struct.multid.MultiDTaintedArray;
import edu.columbia.cs.psl.phosphor.struct.multid.MultiDTaintedArrayWithObjTag;
import org.objectweb.asm.Type;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static edu.columbia.cs.psl.phosphor.instrumenter.TaintMethodRecord.*;

public class ReflectionMasker {

    private static final boolean IS_KAFFE = false;
    private static final String multiDDescriptor = "edu.columbia.cs.psl.phosphor.struct.Lazy";
    private static final int multiDDescriptorLength = multiDDescriptor.length();
    private static final char[] SET_TAG_METHOD_CHARS = "setPHOSPHOR_TAG".toCharArray();
    private static final int SET_TAG_METHOD_LEN = SET_TAG_METHOD_CHARS.length;
    private static final char[] METHOD_SUFFIX_CHARS = TaintUtils.METHOD_SUFFIX.toCharArray();
    private static final int METHOD_SUFFIX_LEN = METHOD_SUFFIX_CHARS.length;

    static {
        System.setSecurityManager(null);
    }

    @SuppressWarnings("unused")
    public static Object getObject$$PHOSPHORTAGGED(Unsafe u, Object obj, Taint<?> tag, long offset, ControlTaintTagStack ctrl) {
        return getObject$$PHOSPHORTAGGED(u, obj, null, offset);
    }

    @SuppressWarnings("unused")
    public static Object getObject$$PHOSPHORTAGGED(Unsafe u, Object obj, Taint<?> tag, long offset) {
        return RuntimeUnsafePropagator.get(u, obj, offset, null);
    }

    @SuppressWarnings("unused")
    public static void putObject$$PHOSPHORTAGGED(Unsafe u, Object obj, Taint<?> tag, long fieldOffset, Object val, ControlTaintTagStack ctrl) {
        putObject$$PHOSPHORTAGGED(u, obj, tag, fieldOffset, val);
    }

    @SuppressWarnings("unused")
    public static void putObject$$PHOSPHORTAGGED(Unsafe u, Object obj, Taint<?> tag, long fieldOffset, Object val) {
        if(val instanceof LazyArrayObjTags) {
            try {
                RuntimeUnsafePropagator.OffsetPair pair = RuntimeUnsafePropagator.getOffsetPair(u, obj, fieldOffset);
                if(pair != null) {
                    if(pair.origFieldOffset != Unsafe.INVALID_FIELD_OFFSET) {
                        u.putObject(obj, pair.origFieldOffset, MultiDTaintedArray.unbox1D(val));
                    }
                    if(pair.tagFieldOffset != Unsafe.INVALID_FIELD_OFFSET) {
                        u.putObject(obj, pair.tagFieldOffset, val);
                    }
                    return;
                }
            } catch(Exception e) {
                //
            }
        }
        u.putObject(obj, fieldOffset, val);
    }


    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = IS_INSTANCE)
    public static TaintedBooleanWithObjTag isInstance(Class<?> c1, Object o, TaintedBooleanWithObjTag ret) {
        ret.taint = null;
        if(o instanceof LazyArrayObjTags && !LazyArrayObjTags.class.isAssignableFrom(c1)) {
            ret.val = c1.isInstance(MultiDTaintedArrayWithObjTag.unboxRaw(o));
        } else {
            ret.val = c1.isInstance(o);
        }
        return ret;
    }

    @SuppressWarnings("unused")
    public static String getPropertyHideBootClasspath(String prop) {
        if(prop.equals("sun.boot.class.path")) {
            return null;
        } else if(prop.equals("os.name")) {
            return "linux";
        }
        return System.getProperty(prop);
    }

    private static Method getTaintMethodControlTrack(Method m) {
        if(m.getDeclaringClass().isAnnotation()) {
            return m;
        }
        final char[] chars = m.getName().toCharArray();
        if(chars.length > METHOD_SUFFIX_LEN) {
            boolean isEq = true;
            int x = 0;
            for(int i = chars.length - METHOD_SUFFIX_LEN; i < chars.length; i++) {
                if(chars[i] != METHOD_SUFFIX_CHARS[x]) {
                    isEq = false;
                }
                x++;
            }
            if(isEq) {
                if(!IS_KAFFE) {
                    setMark(m, true);
                }
                return m;
            }
        }
        if(declaredInIgnoredClass(m)) {
            return m;
        }
        ArrayList<Class> newArgs = new ArrayList<>();
        for(final Class c : m.getParameterTypes()) {
            if(c.isArray()) {
                if(c.getComponentType().isPrimitive()) {
                    // 1d primitive array
                    try {
                        newArgs.add(Class.forName(MultiDTaintedArrayWithObjTag.getTypeForType(Type.getType(c)).getInternalName().replace('/', '.')));
                    } catch(ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    newArgs.add(c);
                } else {
                    Class elementType = c.getComponentType();
                    while(elementType.isArray()) {
                        elementType = c.getComponentType();
                    }
                    if(elementType.isPrimitive()) {
                        // 2d primitive array
                        try {
                            newArgs.add(Class.forName(MultiDTaintedArrayWithObjTag.getTypeForType(Type.getType(c)).getInternalName().replace('/', '.')));
                        } catch(ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Reference array
                        newArgs.add(c);
                    }
                }
            } else if(c.isPrimitive()) {
                newArgs.add(Configuration.TAINT_TAG_OBJ_CLASS);
                newArgs.add(c);
            } else {
                // Anything else
                newArgs.add(c);
            }
        }
        newArgs.add(ControlTaintTagStack.class);
        final Class returnType = m.getReturnType();
        if(returnType.isPrimitive() && returnType != Void.TYPE) {
            if(returnType == Integer.TYPE) {
                newArgs.add(TaintedIntWithObjTag.class);
            } else if(returnType == Short.TYPE) {
                newArgs.add(TaintedShortWithObjTag.class);
            } else if(returnType == Float.TYPE) {
                newArgs.add(TaintedFloatWithObjTag.class);
            } else if(returnType == Double.TYPE) {
                newArgs.add(TaintedDoubleWithObjTag.class);
            } else if(returnType == Long.TYPE) {
                newArgs.add(TaintedLongWithObjTag.class);
            } else if(returnType == Character.TYPE) {
                newArgs.add(TaintedCharWithObjTag.class);
            } else if(returnType == Byte.TYPE) {
                newArgs.add(TaintedByteWithObjTag.class);
            } else if(returnType == Boolean.TYPE) {
                newArgs.add(TaintedBooleanWithObjTag.class);
            }
        }
        Class[] args = new Class[newArgs.size()];
        newArgs.toArray(args);
        Method ret = null;
        try {
            ret = m.getDeclaringClass().getDeclaredMethod(m.getName() + TaintUtils.METHOD_SUFFIX, args);
        } catch(NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static Method getTaintMethod(Method m) {
        if(isMarked(m) && getCachedMethod(m) != null) {
            return m;
        } else if(!isMarked(m) && getCachedMethod(m) != null) {
            return getCachedMethod(m);
        }else if(m.getDeclaringClass().isAnnotation()) {
            return m;
        }
        final char[] chars = m.getName().toCharArray();
        if(chars.length > METHOD_SUFFIX_LEN) {
            boolean isEq = true;
            int x = 0;
            for(int i = chars.length - METHOD_SUFFIX_LEN; i < chars.length; i++) {
                if(chars[i] != METHOD_SUFFIX_CHARS[x]) {
                    isEq = false;
                }
                x++;
            }
            if(isEq) {
                if(!IS_KAFFE) {
                    setMark(m, true);
                }
                return m;
            }
        }
        ArrayList<Class> newArgs = new ArrayList<>();
        boolean madeChange = false;
        for(final Class c : m.getParameterTypes()) {
            if(c.isArray()) {
                if(c.getComponentType().isPrimitive()) {
                    // 1d primitive array
                    madeChange = true;
                    newArgs.add(MultiDTaintedArray.getUnderlyingBoxClassForUnderlyingClass(c));
                    newArgs.add(c);
                } else {
                    Class elementType = c.getComponentType();
                    while(elementType.isArray()) {
                        elementType = elementType.getComponentType();
                    }
                    if(elementType.isPrimitive()) {
                        // 2d primitive array
                        madeChange = true;
                        try {
                            newArgs.add(Class.forName(MultiDTaintedArray.getTypeForType(Type.getType(c)).getInternalName()));
                        } catch(ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //reference array
                        newArgs.add(c);
                    }
                }
            } else if(c.isPrimitive()) {
                madeChange = true;
                newArgs.add(Configuration.TAINT_TAG_OBJ_CLASS);
                newArgs.add(c);
            } else {
                //anything else
                newArgs.add(c);
            }
        }
        final Class returnType = m.getReturnType();
        if(!returnType.isArray()) {
            if(returnType.isPrimitive() && returnType != Void.TYPE) {
                if(returnType == Integer.TYPE) {
                    newArgs.add(TaintedIntWithObjTag.class);
                } else if(returnType == Short.TYPE) {
                    newArgs.add(TaintedShortWithObjTag.class);
                } else if(returnType == Float.TYPE) {
                    newArgs.add(TaintedFloatWithObjTag.class);
                } else if(returnType == Double.TYPE) {
                    newArgs.add(TaintedDoubleWithObjTag.class);
                } else if(returnType == Long.TYPE) {
                    newArgs.add(TaintedLongWithObjTag.class);
                } else if(returnType == Character.TYPE) {
                    newArgs.add(TaintedCharWithObjTag.class);
                } else if(returnType == Byte.TYPE) {
                    newArgs.add(TaintedByteWithObjTag.class);
                } else if(returnType == Boolean.TYPE) {
                    newArgs.add(TaintedBooleanWithObjTag.class);
                }
                madeChange = true;
            }
        }
        if(madeChange) {
            Class[] args = new Class[newArgs.size()];
            newArgs.toArray(args);
            Method ret = null;
            try {
                ret = m.getDeclaringClass().getDeclaredMethod(m.getName() + TaintUtils.METHOD_SUFFIX, args);
            } catch(NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
            setMark(ret, true);
            setCachedMethod(m, ret);
            setCachedMethod(ret, m);
            return ret;
        } else {
            setMark(m, false);
            setCachedMethod(m, m);
            return m;
        }
    }

    /* Returns the original list of the parameters for a method that would produce a phosphor-added method with the specified
     * tainted parameters. */
    private static SinglyLinkedList<Class<?>> getOriginalParamTypes(Class<?>[] taintedParamTypes) {
        SinglyLinkedList<Class<?>> originalParamTypes = new SinglyLinkedList<>();
        for(int i = 0; i < taintedParamTypes.length; i++) {
            Class<?> paramType = taintedParamTypes[i];
            if(paramType.equals(Taint.class) || paramType.equals(Integer.TYPE)) {
                // Add the type of the primitive for which the current parameter is the taint tag
                originalParamTypes.enqueue(taintedParamTypes[++i]);
            } else if(LazyArrayObjTags.class.isAssignableFrom(paramType)) {
                // Add the type of the 1D primitive array for which the current parameter is the taint array
                originalParamTypes.enqueue(taintedParamTypes[++i]);
            } else if(paramType.getName().contains("edu.columbia.cs.psl.phosphor.struct.Lazy")) {
                // Add the original multidimensional primitive array for the current LazyArray array
                originalParamTypes.enqueue(TaintUtils.getUnwrappedClass(paramType));
            } else if(!paramType.equals(TaintSentinel.class) && !paramType.equals(ControlTaintTagStack.class) &&
                    !TaintedPrimitiveWithObjTag.class.isAssignableFrom(paramType)) {
                // Add the type as is if it is not TaintSentinel, ControlTaintTagStack or a TaintedPrimitiveWithXTags
                originalParamTypes.enqueue(paramType);
            }
        }
        return originalParamTypes;
    }

    /**
     *  Called for Class.getConstructor and Class.getDeclaredConstructor to remap the parameter types.
     */
    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = ADD_TYPE_PARAMS)
    public static Class<?>[] addTypeParams(Class<?> clazz, Class<?>[] params, boolean implicitTracking) {
        if(isIgnoredClass(clazz) || params == null) {
            return params;
        }
        boolean needsChange = false;
        for(Class<?> c : params) {
            if(c != null && isPrimitiveOrPrimitiveArray(c)) {
                needsChange = true;
            }
        }
        if(implicitTracking) {
            needsChange = true;
        }
        if(needsChange) {
            ArrayList<Class<?>> newParams = new ArrayList<>();
            for(Class<?> c : params) {
                Type t = Type.getType(c);
                if(t.getSort() == Type.ARRAY) {
                    if(t.getElementType().getSort() != Type.OBJECT) {
                        if(t.getDimensions() == 1) {
                            newParams.add(MultiDTaintedArray.getUnderlyingBoxClassForUnderlyingClass(c));
                        } else {
                            Type newType;
                            newType = MultiDTaintedArrayWithObjTag.getTypeForType(t);
                            try {
                                newParams.add(Class.forName(newType.getInternalName().replace("/", ".")));
                                continue;
                            } catch(ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if(t.getSort() != Type.OBJECT) {
                    newParams.add(Configuration.TAINT_TAG_OBJ_CLASS);
                }
                newParams.add(c);
            }
            if(implicitTracking) {
                newParams.add(ControlTaintTagStack.class);
            }
            newParams.add(TaintSentinel.class);
            Class[] ret = new Class[newParams.size()];
            newParams.toArray(ret);
            return ret;
        }
        return params;
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = GET_DECLARED_METHOD)
    public static Method getDeclaredMethod(Class<?> czz, String name, Class<?>[] params) throws NoSuchMethodException {
        return checkForSyntheticObjectMethod(czz.getDeclaredMethod(name, params), true);
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = GET_METHOD)
    public static Method getMethod(Class<?> czz, String name, Class<?>[] params) throws NoSuchMethodException {
        return checkForSyntheticObjectMethod(czz.getMethod(name, params), false);
    }

    /* If the specified method is a synthetic hashCode or equals method added by Phosphor and declaredOnly is true,
     * finds and returns a suitable replacement for the method. If the specified method is a synthetic hashCode or
     * equals method added by Phosphor and declaredOnly is false, throws a NoSuchMethodException. */
    private static Method checkForSyntheticObjectMethod(Method m, boolean declaredOnly) throws NoSuchMethodException {
        if(m.isSynthetic()) {
            if("equals".equals(m.getName())) {
                if(declaredOnly) {
                    throw new NoSuchMethodException();
                } else {
                    return ObjectMethods.EQUALS.method;
                }
            } else if("hashCode".equals(m.getName())) {
                if(declaredOnly) {
                    throw new NoSuchMethodException();
                } else {
                    return ObjectMethods.HASH_CODE.method;
                }
            }
        }
        return m;
    }

    /* Returns true if the specified member was declared in a class ignored by Phosphor. */
    private static boolean declaredInIgnoredClass(Member member) {
        return member != null && member.getDeclaringClass() != null && isIgnoredClass(member.getDeclaringClass());
    }

    /* Returns true if the specified class was ignored by Phosphor. */
    private static boolean isIgnoredClass(Class<?> clazz) {
        return clazz != null && (Instrumenter.isIgnoredClass(clazz.getName().replace('.', '/')) || Object.class.equals(clazz));
    }

    private static boolean isPrimitiveOrPrimitiveArray(Class<?> c) {
        return c.isArray() ? isPrimitiveOrPrimitiveArray(c.getComponentType()) : c.isPrimitive();
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = FIX_ALL_ARGS_CONSTRUCTOR)
    public static Object[] fixAllArgs(Object[] in, Constructor<?> c) {
        return fixAllArgs(in, c, false, null);
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = FIX_ALL_ARGS_CONSTRUCTOR_CONTROL)
    public static Object[] fixAllArgs(Object[] in, Constructor c, ControlTaintTagStack ctrl) {
        return fixAllArgs(in, c, true, ctrl);
    }

    private static Object[] fixAllArgs(Object[] in, Constructor<?> c, boolean implicitTracking, ControlTaintTagStack ctrl) {
        if(declaredInIgnoredClass(c)) {
            return getOriginalParams(c.getParameterTypes(), in);
        }
        if(c == null) {
            return in;
        } else if(in != null && c.getParameterTypes().length != in.length) {
            Object[] ret = new Object[c.getParameterTypes().length];
            fillInParams(ret, in, c.getParameterTypes());
            if(implicitTracking) {
                ret[ret.length - 2] = ctrl;
            }
            return ret;
        } else if(in == null && c.getParameterTypes().length == 1) {
            Object[] ret = new Object[1];
            ret[0] = null;
            return ret;
        } else if(in == null && c.getParameterTypes().length == 2) {
            Object[] ret = new Object[2];
            ret[0] = implicitTracking ? ctrl : new ControlTaintTagStack();
            ret[1] = null;
            return ret;
        }
        return in;
    }

    /**
     * Returns an array of objects derived from the specified array of tainted parameters that match the specified
     * arrayof types.
     */
    private static Object[] getOriginalParams(Class<?>[] types, Object[] taintedParams) {
        Object[] originalParams = new Object[types.length];
        for(int i = 0; i < types.length; i++) {
            if(types[i].isPrimitive()) {
                if(taintedParams[i] instanceof TaintedPrimitiveWithObjTag) {
                    originalParams[i] = ((TaintedPrimitiveWithObjTag) taintedParams[i]).toPrimitiveType();
                } else {
                    originalParams[i] = taintedParams[i];
                }
            } else if(types[i].isArray()) {
                Object obj = MultiDTaintedArray.maybeUnbox(taintedParams[i]);
                originalParams[i] = obj;

            } else {
                originalParams[i] = taintedParams[i];
            }
        }
        return originalParams;
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = FIX_ALL_ARGS_METHOD)
    public static MethodInvoke fixAllArgs(Method m, Object owner, Object[] in) {
        MethodInvoke ret = new MethodInvoke();
        if(m == null || declaredInIgnoredClass(m)) {
            ret.a = in;
            ret.o = owner;
            ret.m = m;
            return ret;
        }
        m.setAccessible(true);
        if((!isMarked(m)) && !"java.lang.Object".equals(m.getDeclaringClass().getName())) {
            m = getTaintMethod(m);
        }
        m.setAccessible(true);
        ret.o = owner;
        ret.m = m;
        if(in != null && m.getParameterTypes().length != in.length) {
            ret.a = new Object[ret.m.getParameterTypes().length];
        } else {
            ret.a = in;
        }
        int j = fillInParams(ret.a, in, ret.m.getParameterTypes());
        if((in == null && m.getParameterTypes().length == 1) || (in != null && j != in.length - 1)) {
            ret.a = (in != null) ? ret.a : new Object[1];
            final Class returnType = m.getReturnType();
            if(TaintedPrimitiveWithObjTag.class.isAssignableFrom(returnType)) {
                try {
                    ret.a[j] = returnType.newInstance();
                } catch(InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * The instrumentation may add calls to this method.
     */
    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = FIX_ALL_ARGS_METHOD_CONTROL)
    public static MethodInvoke fixAllArgs(Method m, Object owner, Object[] in, ControlTaintTagStack ctrl) {
        MethodInvoke ret = new MethodInvoke();
        m.setAccessible(true);
        if(declaredInIgnoredClass(m)) {
            ret.a = in;
            ret.o = owner;
            ret.m = m;
            return ret;
        }
        if(!isMarked(m)) {
            m = getTaintMethodControlTrack(m);
        }
        m.setAccessible(true);
        ret.o = owner;
        ret.m = m;
        if(in != null && m.getParameterTypes().length != in.length) {
            ret.a = new Object[ret.m.getParameterTypes().length];
        } else {
            ret.a = in;
        }
        int j = fillInParams(ret.a, in, ret.m.getParameterTypes());
        if(ret.a != null && ret.a.length > j) {
            ret.a[j++] = ctrl;
        }
        if(in == null && m.getParameterTypes().length == 1) {
            ret.a = new Object[1];
            ret.a[0] = ctrl;
        } else if((in == null && m.getParameterTypes().length == 2)
                || (in != null && j != in.length - 1)) {
            if(in == null) {
                ret.a = new Object[2];
                ret.a[0] = ctrl;
                j++;
            }
            final Class returnType = m.getReturnType();
            if(TaintedPrimitiveWithObjTag.class.isAssignableFrom(returnType)) {
                try {
                    ret.a[j] = returnType.newInstance();
                    if(ret.a[j].getClass().equals(Boolean.class)) {
                        System.exit(-1);
                    }
                } catch(InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Adds arguments to the target argument array from the specified array of provided arguments based on the specified
     * expected parameter types. Returns the number of arguments added.
     */
    private static int fillInParams(Object[] targetArgs, Object[] providedArgs, Class<?>[] paramTypes) {
        int targetParamIndex = 0;
        if(providedArgs != null && paramTypes.length != providedArgs.length) {
            for(Object providedArg : providedArgs) {
                Class<?> targetParamClass = paramTypes[targetParamIndex];
                if(targetParamClass.equals(Configuration.TAINT_TAG_OBJ_CLASS)) {
                    // Add an object taint
                    targetArgs[targetParamIndex++] = MultiTainter.getTaint(providedArg);
                    // Add a boxed primitive to the args
                    targetArgs[targetParamIndex++] = providedArg;
                } else if(LazyArrayObjTags.class.isAssignableFrom(targetParamClass)) {
                    // Add a LazyArray to the args
                    LazyArrayObjTags arr = ((LazyArrayObjTags) providedArg);
                    targetArgs[targetParamIndex++] = arr;
                    // Add a primitive array to the args
                    targetArgs[targetParamIndex++] = (arr == null) ? null : arr.getVal();
                } else {
                    // Add the provided argument as is to the args
                    targetArgs[targetParamIndex++] = providedArg;
                }
            }
        }
        return targetParamIndex;
    }

    /**
     * Masks calls to Object.getClass from ObjectOutputStream.
     */
    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = GET_ORIGINAL_CLASS_OBJECT_OUTPUT_STREAM)
    public static Class<?> getOriginalClassObjectOutputStream(Object obj) {
        if(obj instanceof LazyArrayObjTags && ((LazyArrayObjTags) obj).taints != null) {
            return obj.getClass();
        } else {
            return getOriginalClass(obj.getClass());
        }
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = GET_ORIGINAL_METHOD)
    public static Method getOriginalMethod(Method m) {
        if(getCachedMethod(m) != null && isMarked(m)) {
            return getCachedMethod(m);
        }
        return m;
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = GET_ORIGINAL_CONSTRUCTOR)
    public static Constructor<?> getOriginalConstructor(Constructor<?> cons) {
        if(declaredInIgnoredClass(cons)) {
            return cons;
        }
        boolean hasSentinel = false;
        for(Class<?> clazz : cons.getParameterTypes()) {
            if(clazz.equals(TaintSentinel.class)) {
                hasSentinel = true;
                break;
            }
        }
        if(hasSentinel) {
            Class<?>[] origParams = getOriginalParamTypes(cons.getParameterTypes()).toArray(new Class<?>[0]);
            try {
                return cons.getDeclaringClass().getDeclaredConstructor(origParams);
            } catch(NoSuchMethodException | SecurityException e) {
                return cons;
            }
        } else {
            return cons;
        }
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = GET_ORIGINAL_CLASS)
    public static Class<?> getOriginalClass(Class<?> clazz) {
        if(getCachedClass(clazz) != null) {
            return getCachedClass(clazz);
        } else if(clazz.isArray()) {
            String cmp;
            Class c = clazz.getComponentType();
            while(c.isArray()) {
                c = c.getComponentType();
            }
            cmp = c.getName();
            if(cmp.length() >= multiDDescriptorLength
                    && cmp.subSequence(0, multiDDescriptorLength).equals(multiDDescriptor)) {
                Type t = Type.getType(clazz);
                String innerType = MultiDTaintedArray.getPrimitiveTypeForWrapper(clazz);
                String newName = "[";
                for(int i = 0; i < t.getDimensions(); i++) {
                    newName += "[";
                }
                try {
                    Class ret = Class.forName(newName + innerType);
                    setCachedClass(clazz, ret);
                    setCachedClass(ret, ret);
                    return ret;
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            setCachedClass(clazz, clazz);
            return clazz;
        } else {
            String cmp = clazz.getName();
            if(cmp.length() >= multiDDescriptorLength
                    && cmp.subSequence(0, multiDDescriptorLength).equals(multiDDescriptor)) {
                String innerType = MultiDTaintedArray.getPrimitiveTypeForWrapper(clazz);
                try {
                    Class ret = Class.forName("[" + innerType);
                    setCachedClass(clazz, ret);
                    setCachedClass(ret, ret);
                    return ret;
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            setCachedClass(clazz, clazz);
            return clazz;
        }
    }

    /**
     * Filters the fields returned by Class.getFields and Class.getDeclaredFields.
     */
    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = REMOVE_TAINTED_FIELDS)
    public static Field[] removeTaintedFields(Field[] in) {
        SinglyLinkedList<Field> ret = new SinglyLinkedList<>();
        boolean removeSVUIDField = containsSVUIDSentinelField(in);
        for(Field f : in) {
            if(!f.getName().equals("taint") && !f.getName().endsWith(TaintUtils.TAINT_FIELD) && !f.getName().startsWith(TaintUtils.PHOSPHOR_ADDED_FIELD_PREFIX)
                    && !(removeSVUIDField && f.getName().equals("serialVersionUID"))) {
                ret.enqueue(f);
            }
        }
        return ret.toArray(new Field[ret.size()]);
    }

    /**
     * Returns whether the specified array of fields contains a sentinel field indicating that a SerialVersionUID was
     * added to the class by phosphor.
     */
    private static boolean containsSVUIDSentinelField(Field[] in) {
        for(Field f : in) {
            if(f.getName().equals(TaintUtils.ADDED_SVUID_SENTINEL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filters the methods returns by Class.getDeclaredMethods and Class.getMethods. If declaredOnly is true then
     * synthetic equals and hashCode methods are fully removed from the specified array, otherwise they are replaced with
     * Object.equals and Object.hashCode respectively.
     */
    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = REMOVE_TAINTED_METHODS)
    public static Method[] removeTaintedMethods(Method[] in, boolean declaredOnly) {
        SinglyLinkedList<Method> ret = new SinglyLinkedList<>();
        for(Method f : in) {
            final char[] chars = f.getName().toCharArray();
            boolean match = false;
            if(chars.length == SET_TAG_METHOD_LEN) {
                match = true;
                for(int i = 3; i < SET_TAG_METHOD_LEN; i++) {
                    if(chars[i] != SET_TAG_METHOD_CHARS[i]) {
                        match = false;
                        break;
                    }
                }
            }
            if(!match && chars.length > METHOD_SUFFIX_LEN) {
                int x = 0;
                boolean matched = true;
                for(int i = chars.length - METHOD_SUFFIX_LEN; i < chars.length; i++) {
                    if(chars[i] != METHOD_SUFFIX_CHARS[x]) {
                        matched = false;
                        break;
                    }
                    x++;
                }
                if(!matched) {
                    ret.enqueue(f);
                }
            } else if(!match) {
                // Check for synthetic hashCode and equals methods added by Phosphor
                if(f.isSynthetic()) {
                    if(chars.length == 6 && chars[0] == 'e' && chars[1] == 'q' && chars[2] == 'u' && chars[3] == 'a' &&
                            chars[4] == 'l' && chars[5] == 's') {
                        if(!declaredOnly) {
                            ret.enqueue(ObjectMethods.EQUALS.method);
                        }
                        continue;
                    } else if(chars.length == 8 && chars[0] == 'h' && chars[1] == 'a' && chars[2] == 's' && chars[3] == 'h' &&
                            chars[4] == 'C' && chars[5] == 'o' && chars[6] == 'd' && chars[7] == 'e') {
                        if(!declaredOnly) {
                            ret.enqueue(ObjectMethods.HASH_CODE.method);
                        }
                        continue;
                    }
                }
                ret.enqueue(f);
            }
        }
        return ret.toArray(new Method[ret.size()]);
    }

    @SuppressWarnings("unused")
    @InvokedViaInstrumentation(record = REMOVE_TAINTED_CONSTRUCTORS)
    public static Constructor<?>[] removeTaintedConstructors(Constructor<?>[] in) {
        SinglyLinkedList<Constructor<?>> ret = new SinglyLinkedList<>();
        for(Constructor<?> f : in) {
            Class<?>[] params = f.getParameterTypes();
            if(params.length == 0 || !(TaintUtils.isTaintSentinel(params[params.length - 1]))) {
                ret.enqueue(f);
            }
        }
        return ret.toArray(new Constructor<?>[ret.size()]);
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @InvokedViaInstrumentation(record = REMOVE_TAINTED_INTERFACES)
    public static Class[] removeTaintedInterfaces(Class[] in) {
        if(in == null) {
            return null;
        }
        boolean found = false;
        for(Class aClass : in) {
            if(aClass.equals(TaintedWithObjTag.class)) {
                found = true;
                break;
            }
        }
        if(!found) {
            return in;
        }
        Class[] ret = new Class[in.length - 1];
        int idx = 0;
        for(Class aClass : in) {
            if(!aClass.equals(TaintedWithObjTag.class)) {
                ret[idx] = aClass;
                idx++;
            }
        }
        return ret;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @InvokedViaInstrumentation(record = REMOVE_EXTRA_STACK_TRACE_ELEMENTS)
    public static StackTraceElement[] removeExtraStackTraceElements(StackTraceElement[] in, Class<?> clazz) {
        int depthToCut = 0;
        String toFind = clazz.getName();
        if(in == null) {
            return null;
        }

        for(int i = 0; i < in.length; i++) {
            if(in[i].getClassName().equals(toFind) && !(i + 1 < in.length && in[i + 1].getClassName().equals(toFind))) {
                depthToCut = i + 1;
                break;
            }
        }
        StackTraceElement[] ret = new StackTraceElement[in.length - depthToCut];
        System.arraycopy(in, depthToCut, ret, 0, ret.length);
        return ret;
    }

    private static Method getCachedMethod(Method method) {
        return method.PHOSPHOR_TAGmethod;
    }

    private static void setCachedMethod(Method method, Method valueToCache) {
        method.PHOSPHOR_TAGmethod = valueToCache;
    }

    private static Class<?> getCachedClass(Class<?> clazz) {
        return clazz.PHOSPHOR_TAGclass;
    }

    private static void setCachedClass(Class<?> clazz, Class<?> valueToCache) {
        clazz.PHOSPHOR_TAGclass = valueToCache;
    }

    private static void setMark(Method method, boolean value) {
        method.PHOSPHOR_TAGmarked = value;
    }

    private static boolean isMarked(Method method) {
        return method.PHOSPHOR_TAGmarked;
    }

    /* Used to create singleton references to Methods of the Object class. */
    private enum ObjectMethods {
        EQUALS("equals", Object.class),
        HASH_CODE("hashCode");

        public Method method;

        ObjectMethods(String name, Class<?>... parameterTypes) {
            try {
                this.method = Object.class.getDeclaredMethod(name, parameterTypes);
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
