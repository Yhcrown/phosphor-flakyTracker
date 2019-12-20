package edu.columbia.cs.psl.phosphor.runtime;

import edu.columbia.cs.psl.phosphor.struct.*;
import edu.columbia.cs.psl.phosphor.struct.multid.MultiDTaintedArrayWithObjTag;
import org.objectweb.asm.Type;

import java.lang.reflect.Array;

public class ArrayReflectionMasker {

    private ArrayReflectionMasker() {
        // Prevents this class from being instantiated
    }

    public static int getLength(Object obj) {
        if(obj.getClass().isArray()) {
            return Array.getLength(obj);
        } else if(obj instanceof LazyArrayObjTags) {
            return Array.getLength(((LazyArrayObjTags) obj).getVal());
        }
        throw new ArrayStoreException("Uknown array type: " + obj.getClass());
    }

    public static TaintedIntWithObjTag getLength$$PHOSPHORTAGGED(Object obj, TaintedIntWithObjTag ret) {
        if(obj.getClass().isArray()) {
            ret.taint = null;
            ret.val = Array.getLength(obj);
            return ret;
        } else if(obj instanceof LazyArrayObjTags) {
            ret.taint = null;
            ret.val = Array.getLength(((LazyArrayObjTags) obj).getVal());
            return ret;
        }
        throw new IllegalArgumentException("Not an array type: " + obj.getClass());
    }

    public static TaintedIntWithObjTag getLength$$PHOSPHORTAGGED(Object obj, ControlTaintTagStack ctrl, TaintedIntWithObjTag ret) {
        return getLength$$PHOSPHORTAGGED(obj, ret);
    }

    public static Object newInstance$$PHOSPHORTAGGED(Class clazz, Taint lenTaint, int len, ControlTaintTagStack zz) {
        return newInstance$$PHOSPHORTAGGED(clazz, lenTaint, len);
    }

    public static Object newInstance$$PHOSPHORTAGGED(Class clazz, Object lenTaint, int len, ControlTaintTagStack zz) {
        return newInstance$$PHOSPHORTAGGED(clazz, (Taint) lenTaint, len);
    }

    public static Object newInstance$$PHOSPHORTAGGED(Class clazz, Taint lenTaint, int len) {
        Class tmp = clazz;
        int dims = 0;
        while(tmp.isArray()) {
            tmp = tmp.getComponentType();
            dims++;
        }
        if(tmp.isPrimitive()) {
            if(dims == 0) {
                if(tmp == Double.TYPE) {
                    return new LazyDoubleArrayObjTags(new double[len]);
                }
                if(tmp == Float.TYPE) {
                    return new LazyFloatArrayObjTags(new float[len]);
                }
                if(tmp == Integer.TYPE) {
                    return new LazyIntArrayObjTags(new int[len]);
                }
                if(tmp == Long.TYPE) {
                    return new LazyLongArrayObjTags(new long[len]);
                }
                if(tmp == Short.TYPE) {
                    return new LazyShortArrayObjTags(new short[len]);
                }
                if(tmp == Boolean.TYPE) {
                    return new LazyBooleanArrayObjTags(new boolean[len]);
                }
                if(tmp == Byte.TYPE) {
                    return new LazyByteArrayObjTags(new byte[len]);
                }
                if(tmp == Character.TYPE) {
                    return new LazyCharArrayObjTags(new char[len]);
                }
            } else {
                clazz = MultiDTaintedArrayWithObjTag.getUnderlyingBoxClassForUnderlyingClass(clazz);
            }
        }
        return Array.newInstance(clazz, len);
    }

    public static Object newInstance$$PHOSPHORTAGGED(Class clazz, LazyIntArrayObjTags dimsTaint, ControlTaintTagStack ctrl) {
        return newInstance$$PHOSPHORTAGGED(clazz, dimsTaint);
    }

    public static Object newInstance$$PHOSPHORTAGGED(Class clazz, LazyIntArrayObjTags dimstaint) {
        int[] dims = dimstaint.val;
        Type t = Type.getType(clazz);
        if(t.getSort() == Type.ARRAY && t.getElementType().getSort() != Type.OBJECT) {
            // Component type is multi-dimensional primitive array
            try {
                clazz = Class.forName(MultiDTaintedArrayWithObjTag.getTypeForType(t).getInternalName().replace("/", "."));
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
            return Array.newInstance(clazz, dims);
        } else if(t.getSort() != Type.OBJECT) {
            clazz = MultiDTaintedArrayWithObjTag.getClassForComponentType(t.getSort());
            if(clazz.isArray()) {
                int lastDim = dims[dims.length - 1];
                int[] newDims = new int[dims.length - 1];
                System.arraycopy(dims, 0, newDims, 0, dims.length - 1);
                Object[] ret = (Object[]) Array.newInstance(clazz, newDims);
                MultiDTaintedArrayWithObjTag.initLastDim(ret, lastDim, t.getSort());
                return ret;

            } else if(t.getSort() != Type.ARRAY && dims.length == 1) {
                int lastDimSize = dims[dims.length - 1];
                switch(t.getSort()) {
                    case Type.BOOLEAN:
                        return new LazyBooleanArrayObjTags(new boolean[lastDimSize]);
                    case Type.BYTE:
                        return new LazyByteArrayObjTags(new byte[lastDimSize]);
                    case Type.CHAR:
                        return new LazyCharArrayObjTags(new char[lastDimSize]);
                    case Type.DOUBLE:
                        return new LazyDoubleArrayObjTags(new double[lastDimSize]);
                    case Type.FLOAT:
                        return new LazyFloatArrayObjTags(new float[lastDimSize]);
                    case Type.INT:
                        return new LazyIntArrayObjTags(new int[lastDimSize]);
                    case Type.LONG:
                        return new LazyLongArrayObjTags(new long[lastDimSize]);
                    case Type.SHORT:
                        return new LazyShortArrayObjTags(new short[lastDimSize]);
                    default:
                        throw new IllegalArgumentException();
                }
            } else {
                int lastDim = dims[dims.length - 1];
                int[] newDims = new int[dims.length - 1];
                System.arraycopy(dims, 0, newDims, 0, dims.length - 1);
                Object[] ret = (Object[]) Array.newInstance(clazz, newDims);
                MultiDTaintedArrayWithObjTag.initLastDim(ret, lastDim, t.getSort());
                return ret;
            }
        } else if(clazz.isPrimitive()) {
            clazz = MultiDTaintedArrayWithObjTag.getClassForComponentType(t.getSort());
            int lastDim = dims[dims.length - 1];
            int[] newDims = new int[dims.length - 1];
            System.arraycopy(dims, 0, newDims, 0, dims.length - 1);
            Object[] ret = (Object[]) Array.newInstance(clazz, newDims);
            MultiDTaintedArrayWithObjTag.initLastDim(ret, lastDim, t.getSort());
            return ret;
        } else {
            return Array.newInstance(clazz, dims);
        }
    }

    public static Object get$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl) {
        return get$$PHOSPHORTAGGED(obj, idxTaint, idx);
    }

    public static Object get$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx) {
        if(obj instanceof LazyBooleanArrayObjTags) {
            return getBoolean$$PHOSPHORTAGGED(obj, idxTaint, idx, new TaintedBooleanWithObjTag()).toPrimitiveType();
        } else if(obj instanceof LazyByteArrayObjTags) {
            return getByte$$PHOSPHORTAGGED(obj, idxTaint, idx, new TaintedByteWithObjTag()).toPrimitiveType();
        } else if(obj instanceof LazyCharArrayObjTags) {
            return getChar$$PHOSPHORTAGGED(obj, idxTaint, idx, new TaintedCharWithObjTag()).toPrimitiveType();
        } else if(obj instanceof LazyDoubleArrayObjTags) {
            return getDouble$$PHOSPHORTAGGED(obj, idxTaint, idx, new TaintedDoubleWithObjTag()).toPrimitiveType();
        } else if(obj instanceof LazyFloatArrayObjTags) {
            return getFloat$$PHOSPHORTAGGED(obj, idxTaint, idx, new TaintedFloatWithObjTag()).toPrimitiveType();
        } else if(obj instanceof LazyIntArrayObjTags) {
            return getInt$$PHOSPHORTAGGED(obj, idxTaint, idx, new TaintedIntWithObjTag()).toPrimitiveType();
        } else if(obj instanceof LazyLongArrayObjTags) {
            return getLong$$PHOSPHORTAGGED(obj, idxTaint, idx, new TaintedLongWithObjTag()).toPrimitiveType();
        } else if(obj instanceof LazyShortArrayObjTags) {
            return getShort$$PHOSPHORTAGGED(obj, idxTaint, idx, new TaintedShortWithObjTag()).toPrimitiveType();
        }
        return Array.get(obj, idx);
    }

    public static Taint tryToGetTaintObj(Object val) {
        return MultiTainter.getTaint(val);
    }

    public static TaintedByteWithObjTag getByte$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, TaintedByteWithObjTag ret) {
        if(obj instanceof LazyByteArrayObjTags) {
            return ((LazyByteArrayObjTags) obj).get(idx, ret);
        }
        throw new ArrayStoreException("Called getX, but don't have tainted X array!");
    }

    public static TaintedBooleanWithObjTag getBoolean$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl, TaintedBooleanWithObjTag ret) {
        return getBoolean$$PHOSPHORTAGGED(obj, idxTaint, idx, ret);
    }

    public static TaintedIntWithObjTag getInt$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl, TaintedIntWithObjTag ret) {
        return getInt$$PHOSPHORTAGGED(obj, idxTaint, idx, ret);
    }

    public static TaintedCharWithObjTag getChar$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl, TaintedCharWithObjTag ret) {
        return getChar$$PHOSPHORTAGGED(obj, idxTaint, idx, ret);
    }

    public static TaintedDoubleWithObjTag getDouble$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl, TaintedDoubleWithObjTag ret) {
        return getDouble$$PHOSPHORTAGGED(obj, idxTaint, idx, ret);
    }

    public static TaintedFloatWithObjTag getFloat$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl, TaintedFloatWithObjTag ret) {
        return getFloat$$PHOSPHORTAGGED(obj, idxTaint, idx, ret);
    }

    public static TaintedShortWithObjTag getShort$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl, TaintedShortWithObjTag ret) {
        return getShort$$PHOSPHORTAGGED(obj, idxTaint, idx, ret);
    }

    public static TaintedLongWithObjTag getLong$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl, TaintedLongWithObjTag ret) {
        return getLong$$PHOSPHORTAGGED(obj, idxTaint, idx, ret);
    }

    public static TaintedByteWithObjTag getByte$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, ControlTaintTagStack ctrl, TaintedByteWithObjTag ret) {
        return getByte$$PHOSPHORTAGGED(obj, idxTaint, idx, ret);
    }

    public static TaintedBooleanWithObjTag getBoolean$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, TaintedBooleanWithObjTag ret) {
        if(obj instanceof LazyBooleanArrayObjTags) {
            return ((LazyBooleanArrayObjTags) obj).get(idx, ret);
        }
        throw new ArrayStoreException("Called getX, but don't have tainted X array!");
    }

    public static TaintedCharWithObjTag getChar$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, TaintedCharWithObjTag ret) {
        if(obj instanceof LazyCharArrayObjTags) {
            return ((LazyCharArrayObjTags) obj).get(idx, ret);
        }
        throw new ArrayStoreException("Called getX, but don't have tainted X array!");
    }

    public static TaintedDoubleWithObjTag getDouble$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, TaintedDoubleWithObjTag ret) {
        if(obj instanceof LazyDoubleArrayObjTags) {
            return ((LazyDoubleArrayObjTags) obj).get(idx, ret);
        }
        throw new ArrayStoreException("Called getX, but don't have tainted X array!");
    }

    public static TaintedIntWithObjTag getInt$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, TaintedIntWithObjTag ret) {
        if(obj instanceof LazyIntArrayObjTags) {
            return ((LazyIntArrayObjTags) obj).get(idx, ret);
        }
        throw new ArrayStoreException("Called getX, but don't have tainted X array!");
    }

    public static TaintedLongWithObjTag getLong$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, TaintedLongWithObjTag ret) {
        if(obj instanceof LazyLongArrayObjTags) {
            return ((LazyLongArrayObjTags) obj).get(idx, ret);
        }
        throw new ArrayStoreException("Called getX, but don't have tainted X array!");
    }

    public static TaintedShortWithObjTag getShort$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, TaintedShortWithObjTag ret) {
        if(obj instanceof LazyShortArrayObjTags) {
            return ((LazyShortArrayObjTags) obj).get(idx, ret);
        }
        throw new ArrayStoreException("Called getX, but don't have tainted X array!");
    }

    public static TaintedFloatWithObjTag getFloat$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, TaintedFloatWithObjTag ret) {
        if(obj instanceof LazyFloatArrayObjTags) {
            return ((LazyFloatArrayObjTags) obj).get(idx, ret);
        }
        throw new ArrayStoreException("Called getX, but don't have tainted X array!");
    }

    public static void set$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Object val) {
        if(obj != null && !obj.getClass().isArray()) {
            //in this case obj will be boxed, and we need to pull the taint out of val when we unbox it
            if(obj instanceof LazyBooleanArrayObjTags) {
                setBoolean$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Boolean) val);
            } else if(obj instanceof LazyByteArrayObjTags) {
                setByte$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Byte) val);
            } else if(obj instanceof LazyCharArrayObjTags) {
                setChar$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Character) val);
            } else if(obj instanceof LazyDoubleArrayObjTags) {
                setDouble$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Double) val);
            } else if(obj instanceof LazyFloatArrayObjTags) {
                setFloat$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Float) val);
            } else if(obj instanceof LazyIntArrayObjTags) {
                setInt$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Integer) val);
            } else if(obj instanceof LazyLongArrayObjTags) {
                setLong$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Long) val);
            } else if(obj instanceof LazyShortArrayObjTags) {
                setShort$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Short) val);
            } else {
                throw new ArrayStoreException("Got passed an obj of type " + obj + " to store to");
            }
        } else {
            Array.set(obj, idx, val);
        }
    }

    public static void set$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Object val, ControlTaintTagStack ctrl) {
        if(obj != null && !obj.getClass().isArray()) {
            //in this case obj will be boxed, and we need to pull the taint out of val when we unbox it
            if(obj instanceof LazyBooleanArrayObjTags) {
                setBoolean$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Boolean) val, ctrl);
            } else if(obj instanceof LazyByteArrayObjTags) {
                setByte$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Byte) val, ctrl);
            } else if(obj instanceof LazyCharArrayObjTags) {
                setChar$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Character) val, ctrl);
            } else if(obj instanceof LazyDoubleArrayObjTags) {
                setDouble$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Double) val, ctrl);
            } else if(obj instanceof LazyFloatArrayObjTags) {
                setFloat$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Float) val, ctrl);
            } else if(obj instanceof LazyIntArrayObjTags) {
                setInt$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Integer) val, ctrl);
            } else if(obj instanceof LazyLongArrayObjTags) {
                setLong$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Long) val, ctrl);
            } else if(obj instanceof LazyShortArrayObjTags) {
                setShort$$PHOSPHORTAGGED(obj, idxTaint, idx, tryToGetTaintObj(val), (Short) val, ctrl);
            } else {
                throw new ArrayStoreException("Got passed an obj of type " + obj + " to store to");
            }
        } else {
            Array.set(obj, idx, val);
        }
    }

    public static void setBoolean$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, boolean val, ControlTaintTagStack ctrl) {
        taint = Taint.combineTags(taint, ctrl);
        setBoolean$$PHOSPHORTAGGED(obj, idxTaint, idx, taint, val);
    }

    public static void setByte$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, byte val, ControlTaintTagStack ctrl) {
        taint = Taint.combineTags(taint, ctrl);
        setByte$$PHOSPHORTAGGED(obj, idxTaint, idx, taint, val);
    }

    public static void setChar$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, char val, ControlTaintTagStack ctrl) {
        taint = Taint.combineTags(taint, ctrl);
        setChar$$PHOSPHORTAGGED(obj, idxTaint, idx, taint, val);
    }

    public static void setDouble$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, double val, ControlTaintTagStack ctrl) {
        taint = Taint.combineTags(taint, ctrl);
        setDouble$$PHOSPHORTAGGED(obj, idxTaint, idx, taint, val);
    }

    public static void setFloat$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, float val, ControlTaintTagStack ctrl) {
        taint = Taint.combineTags(taint, ctrl);
        setFloat$$PHOSPHORTAGGED(obj, idxTaint, idx, taint, val);
    }

    public static void setInt$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, int val, ControlTaintTagStack ctrl) {
        taint = Taint.combineTags(taint, ctrl);
        setInt$$PHOSPHORTAGGED(obj, idxTaint, idx, taint, val);
    }

    public static void setLong$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, long val, ControlTaintTagStack ctrl) {
        taint = Taint.combineTags(taint, ctrl);
        setLong$$PHOSPHORTAGGED(obj, idxTaint, idx, taint, val);
    }

    public static void setShort$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, short val, ControlTaintTagStack ctrl) {
        taint = Taint.combineTags(taint, ctrl);
        setShort$$PHOSPHORTAGGED(obj, idxTaint, idx, taint, val);
    }

    public static void setBoolean$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, boolean val) {
        if(obj instanceof LazyBooleanArrayObjTags) {
            LazyBooleanArrayObjTags a = (LazyBooleanArrayObjTags) obj;
            a.set(idx, taint, val);
        } else {
            throw new ArrayStoreException("Called setX, but don't have tainted X array!");
        }
    }

    public static void setByte$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, byte val) {
        if(obj instanceof LazyByteArrayObjTags) {
            LazyByteArrayObjTags a = (LazyByteArrayObjTags) obj;
            a.set(idx, taint, val);
        } else {
            throw new ArrayStoreException("Called setX, but don't have tainted X array!, got " + obj.getClass());
        }
    }

    public static void setChar$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, char val) {
        if(obj instanceof LazyCharArrayObjTags) {
            LazyCharArrayObjTags a = (LazyCharArrayObjTags) obj;
            a.set(idx, taint, val);
        } else {
            throw new ArrayStoreException("Called setX, but don't have tainted X array!");
        }
    }

    public static void setDouble$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, double val) {
        if(obj instanceof LazyDoubleArrayObjTags) {
            LazyDoubleArrayObjTags a = (LazyDoubleArrayObjTags) obj;
            a.set(idx, taint, val);
        } else {
            throw new ArrayStoreException("Called setX, but don't have tainted X array!");
        }
    }

    public static void setFloat$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, float val) {
        if(obj instanceof LazyFloatArrayObjTags) {
            LazyFloatArrayObjTags a = (LazyFloatArrayObjTags) obj;
            a.set(idx, taint, val);
        } else {
            throw new ArrayStoreException("Called setX, but don't have tainted X array!");
        }
    }

    public static void setInt$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, int val) {
        if(obj instanceof LazyIntArrayObjTags) {
            LazyIntArrayObjTags a = (LazyIntArrayObjTags) obj;
            a.set(idx, taint, val);
        } else {
            throw new ArrayStoreException("Called setX, but don't have tainted X array!");
        }
    }

    public static void setLong$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, long val) {
        if(obj instanceof LazyLongArrayObjTags) {
            LazyLongArrayObjTags a = (LazyLongArrayObjTags) obj;
            a.set(idx, taint, val);
        } else {
            throw new ArrayStoreException("Called setX, but don't have tainted X array!");
        }
    }

    public static void setShort$$PHOSPHORTAGGED(Object obj, Taint idxTaint, int idx, Taint taint, short val) {
        if(obj instanceof LazyShortArrayObjTags) {
            LazyShortArrayObjTags a = (LazyShortArrayObjTags) obj;
            a.set(idx, taint, val);
        } else {
            throw new ArrayStoreException("Called setX, but don't have tainted X array!");
        }
    }
}
