package edu.columbia.cs.psl.phosphor.instrumenter;

import edu.columbia.cs.psl.phosphor.runtime.TaintSentinel;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class JasperCompilerGeneratorCV extends ClassVisitor {

    // The name of the class being visited
    private String className;
    // The name of the field added to a generated class to indicate that phosphor should make it concrete upon loading it
    public static String makeConcreteSentinel = "$$PHOSPHOR_MAKE_CONCRETE";
    // Whether or not this is a class that needs to be made concrete
    private boolean makeConcrete;

    public JasperCompilerGeneratorCV(ClassVisitor cv, boolean makeConcrete) {
        super(Opcodes.ASM5, cv);
        this.makeConcrete = makeConcrete;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        if(makeConcrete) {
            access &= ~Opcodes.ACC_ABSTRACT;
            access |= Opcodes.ACC_FINAL;
            super.visit(version, access, name, signature, superName, interfaces);
        } else {
            super.visit(version, access, name, signature, superName, interfaces);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if(isJasperCompilerGeneratorClass(className)) {
            mv = new MethodVisitor(Opcodes.ASM5, mv) {
                @Override
                public void visitLdcInsn(Object cst) {
                    if(cst instanceof String) {
                        String s = (String)cst;
                        if(s.equals("public final class ")) {
                            super.visitLdcInsn("public abstract class ");
                            return;
                        }
                    }
                    super.visitLdcInsn(cst);
                }
            };
            if(name.equals("genPreambleStaticInitializers")) {
                mv = new MethodVisitor(Opcodes.ASM5, mv) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        writeStaticSentenielField(mv);
                    }
                };
            }
        }
        return mv;
    }

    private void writeStaticSentenielField(MethodVisitor mv) {
        // Load this onto the stack
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        // Load this instance's writer field onto the stack
        mv.visitFieldInsn(Opcodes.GETFIELD, className, "out", "Lorg/apache/jasper/compiler/ServletWriter;");
        // Load another copy of the writer field onto the stack
        mv.visitInsn(Opcodes.DUP);
        // Write the sentinel field
        mv.visitLdcInsn(String.format("private static final %s %s = null;", TaintSentinel.class.getName(), makeConcreteSentinel));
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/apache/jasper/compiler/ServletWriter", "printil", "(Ljava/lang/String;)V", false);
        // Write the newline
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/apache/jasper/compiler/ServletWriter", "println", "()V", false);

    }

    /* Returns whether the class with the specified name is a generator class that needs to create abstract classes instead
     * concrete ones. */
    public static boolean isJasperCompilerGeneratorClass(String className) {
        return className != null && (className.equals("org/apache/struts2/jasper/compiler/Generator") ||
                className.equals("org/apache/jasper/compiler/Generator"));
    }

    /* Returns whether the class with the specified name was generated by a Generator and needs to be made concrete. */
    public static boolean isJasperCompilerGeneratedClass(List<FieldNode> fields) {
        for(FieldNode field : fields) {
            if(field.name.equals(makeConcreteSentinel)) {
                return true;
            }
        }
        return false;
    }
}
