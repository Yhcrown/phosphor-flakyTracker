package edu.utexas.ece.flakytracker.agent;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.columbia.cs.psl.phosphor.runtime.Taint;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;

public class FlakyUtil {

    public static <T> void checkTainted(T a){
        System.out.println("come in");
        Taint taint = MultiTainter.getTaint(a);
        for (Object label : taint.getLabels()) {
            if (label instanceof FlakyTaintLabel) {
                FlakyTaintLabel taintLabel = (FlakyTaintLabel) label;
                System.out.println(taintLabel);
            }
        }

    }

     public static void main(String[] args) throws IOException {
         String className = "flaky.FlakyDemo";
         int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
         boolean asmCode = true;

 //        Printer printer = asmCode ? new ASMifier() : new Textifier();
 //        PrintWriter printWriter = new PrintWriter(System.out, true);
 //        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
 //        new ClassReader(className).accept(traceClassVisitor, parsingOptions);

         ClassReader reader = new ClassReader(className);

 //        final ClassReader reader = new ClassReader(bytes);
         final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS );
         FlakyClassTracer visitor = new FlakyClassTracer(writer);
         reader.accept(visitor, 0);

         FileUtils.writeByteArrayToFile(new File("target/classes/flaky/FlakyDemo7.class"),writer.toByteArray());

//         System.out.println(Arrays.toString(API.getArgumentsByDescriptor("(Ljava/lang/String;JJ)V")));
//         System.out.println(API.getReturnTypeByDescriptor("(Ljava/lang/String;JJ)V"));

     }






}