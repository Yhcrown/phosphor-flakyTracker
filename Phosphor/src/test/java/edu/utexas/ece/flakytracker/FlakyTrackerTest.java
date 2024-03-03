package edu.utexas.ece.flakytracker;

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

    @Test
    public void test01(){
        System.out.println("test01");
        Random r = new Random();
        System.out.println("ha");
        int x = r.nextInt();
        System.out.println("hei");
//        FlakyUtil.checkTainted(x);
        org.junit.Assert.assertEquals("not equal", 1, x); //flaky
    }
}
