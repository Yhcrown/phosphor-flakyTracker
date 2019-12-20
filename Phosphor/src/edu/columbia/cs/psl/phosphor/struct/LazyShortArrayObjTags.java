package edu.columbia.cs.psl.phosphor.struct;

import edu.columbia.cs.psl.phosphor.Configuration;
import edu.columbia.cs.psl.phosphor.runtime.Taint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class LazyShortArrayObjTags extends LazyArrayObjTags {

    private static final long serialVersionUID = -4189650314277328488L;

    public short[] val;

    public LazyShortArrayObjTags(int len) {
        val = new short[len];
    }

    public LazyShortArrayObjTags(short[] array, Taint[] taints) {
        this.taints = taints;
        this.val = array;
    }

    public LazyShortArrayObjTags(short[] array) {
        this.val = array;
    }

    public LazyShortArrayObjTags(Taint lenTaint, short[] array) {
        this.val = array;
        this.lengthTaint = lenTaint;
    }

    public static LazyShortArrayObjTags factory(short[] array) {
        if (array == null) {
            return null;
        }
        return new LazyShortArrayObjTags(array);
    }

    @Override
    public Object clone() {
        return new LazyShortArrayObjTags(val.clone(), (taints != null) ? taints.clone() : null);
    }

    public void set(Taint idxTag, int idx, short val) {
        set(idxTag, idx, null, val);
    }

    public void set(Taint idxTag, int idx, Taint tag, short val) {
        if (Configuration.derivedTaintListener != null) {
            set(idx, Configuration.derivedTaintListener.arraySet(this, idxTag, idx, tag, val, null), val);
        } else if (idxTag == null) {
            set(idx, tag, val);
        } else if (tag == null) {
            set(idx, idxTag, val);
        } else {
            set(idx, tag.union(idxTag), val);
        }
    }

    public void set(int idx, Taint tag, short val) {
        this.val[idx] = val;
        if (taints == null && tag != null) {
            taints = new Taint[this.val.length];
        }
        if (taints != null) {
            taints[idx] = tag;
        }
    }

    public void set(Taint idxTag, int idx, Taint tag, short val, ControlTaintTagStack ctrl) {
        checkAIOOB(idxTag, idx, ctrl);
        set(idx, Configuration.derivedTaintListener.arraySet(this, idxTag, idx, tag, val, ctrl), val, ctrl);
    }

    public void set(int idx, Taint tag, short val, ControlTaintTagStack ctrl) {
        checkAIOOB(null, idx, ctrl);
        set(idx, Taint.combineTags(tag, ctrl), val);
    }

    public TaintedShortWithObjTag get(Taint idxTaint, int idx, TaintedShortWithObjTag ret) {
        return Configuration.derivedTaintListener.arrayGet(this, idxTaint, idx, ret, null);
    }

    public TaintedShortWithObjTag get(Taint idxTaint, int idx, TaintedShortWithObjTag ret, ControlTaintTagStack ctrl) {
        checkAIOOB(idxTaint, idx, ctrl);
        return Configuration.derivedTaintListener.arrayGet(this, idxTaint, idx, ret, ctrl);
    }

    public TaintedShortWithObjTag get(int idx, TaintedShortWithObjTag ret) {
        ret.val = val[idx];
        ret.taint = (taints == null) ? Taint.emptyTaint() : taints[idx];
        return ret;
    }

    public TaintedShortWithObjTag get(int idx, TaintedShortWithObjTag ret, ControlTaintTagStack ctrl) {
        checkAIOOB(null, idx, ctrl);
        get(idx, ret);
        ret.taint = Taint.combineTags(ret.taint, ctrl);
        return ret;
    }

    public int getLength() {
        return val.length;
    }

    @Override
    public Object getVal() {
        return val;
    }

    public void ensureVal(short[] v) {
        if (v != val) {
            val = v;
        }
    }

    public static short[] unwrap(LazyShortArrayObjTags obj) {
        if (obj != null) {
            return obj.val;
        }
        return null;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        if (val == null) {
            stream.writeInt(-1);
        } else {
            stream.writeInt(val.length);
            for (short el : val) {
                stream.writeShort(el);
            }
        }
        stream.writeObject(taints);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int len = stream.readInt();
        if (len == -1) {
            val = null;
        } else {
            val = new short[len];
            for (int i = 0; i < len; i++) {
                val[i] = stream.readShort();
            }
        }
        taints = (Taint[]) stream.readObject();
    }
}
