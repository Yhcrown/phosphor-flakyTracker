package edu.utexas.ece.flakytracker;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import edu.utexas.ece.flakytracker.agent.FlakyUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.Random;

public class FlakyTrackerTest {

    int a = 1;

    @Test
    public void test01(){
        a = 2;
    }
    @Test
    public void test02(){
        System.out.println("test01");
        Random r = new Random();
        System.out.println("ha");
        int x = r.nextInt();
        System.out.println("hei");
//        FlakyUtil.checkTainted(x);
        org.junit.Assert.assertEquals(a,1);
//        org.junit.Assert.assertEquals("not equal", 1, x); //flakytracker
    }

    @Test
    public void test03(){
        int x = 1;
        MultiTainter.taintedInt(x,10);
        System.out.println(MultiTainter.getTaint(x));
        x = 3;
        System.out.println(MultiTainter.getTaint(x));

    }


}
