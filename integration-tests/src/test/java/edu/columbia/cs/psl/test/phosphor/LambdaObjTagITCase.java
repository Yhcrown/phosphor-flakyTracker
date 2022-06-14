package edu.columbia.cs.psl.test.phosphor;

import edu.columbia.cs.psl.phosphor.runtime.MultiTainter;
import org.junit.Assert;
import org.junit.Test;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.security.AccessController.doPrivileged;
import static org.junit.Assert.assertEquals;

public class LambdaObjTagITCase extends BaseMultiTaintClass {

	@Test
	public void testDoubleConsumer() {
		ObjDoubleConsumer<double[]> consumer = (ll, l) -> {
			ll[0] += l;
		};
		consumer.accept(new double[1], 1.0);
		BiConsumer<double[], double[]> consumer2 = (ll, rr) -> ll[0] += rr[0];
		consumer2.accept(new double[1], new double[1]);
	}

	float getFloat() {
		return 0f;
	}

	@Test
	public void testFloat2DoubleSupplier() throws Exception {
		DoubleSupplier supplier = ((DoubleSupplier) this::getFloat);
		double d = supplier.getAsDouble();
	}

	@Test
	public void testCollectors() {
		List<String> givenList = Arrays.asList("a", "bb", "ccc", "dd");
		givenList.stream().collect(Collectors.toList());
	}

	@Test
	public void testIntStreamsDoNotCrash() {
		int sum = IntStream.of(1, 2, 3, 4, 5).sum(); // creates a bunch of lambdas
	}

	//This test is interesting because Character::toLowerCase is a wrapped (not instrumented) method
	@Test
	public void testLambdaCallsMethodIgnoredFromInstrumentation(){
		String name = "a";
		name.chars().map(Character::toLowerCase).forEach((c)->{});
	}

	@Test
	public void testEmptyLambda() {
		Runnable r = () -> {
		};
	}

	@Test
	public void testZeroArgVoidWrappedLambda() {
		int[] i = new int[1];
		Runnable r = () -> {
			i[0] += 10;
		};
		r.run();
		assertEquals("Expected runnable constructed using lambda to run.", 10, i[0]);
	}

	@Test
	public void testLambdaIntArg() {
		intArg(new int[10]);
	}

	@Test
	public void testLocalDateTimeDoesntCrash() {
		LocalDateTime.now();
		//Just testing to make sure no exception
	}

	void initStreams(int[] arg) {
	}

	void intArg(int[] arg) {
		try {
			doPrivileged((PrivilegedExceptionAction<Void>) () -> {
				initStreams(arg);
				return null;
			});
		} catch(PrivilegedActionException ex) {
			//
		}
	}

	@Test
	public void testSupplier() {
		Supplier<double[]> supplier = () -> new double[3];
		double[] d = supplier.get();
	}

	@Test
	public void testUnaryOperator() {
		UnaryOperator<String> f = s -> s;
		String in = MultiTainter.taintedReference("hello", 0);
		String out = f.apply(in);
		assertEquals(in, out);
	}

	@Test
	public void testBoxingLambda() {
		BinaryOperator<Integer> sum = Integer::sum;
		Integer result = sum.apply(0, 5);
	}

	static void tryRunner(Runnable runnable) {
		runnable.run();
	}

	static void print() {
		System.out.println("Success!");
		staticRunnableMethodRan = true;
	}

	static boolean staticRunnableMethodRan = false;
	@Test
    public void testStaticRunnableMethod(){
		staticRunnableMethodRan = false;
		tryRunner(LambdaObjTagITCase::print);
		Assert.assertTrue(staticRunnableMethodRan);
	}

	@Test
	public void directConstructorReference() throws Throwable {
		Constructor<MethodHandles.Lookup> constructor =
				MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
		constructor.setAccessible(true);
		MethodHandles.Lookup lookup = constructor.newInstance(
				String.class,
				-1 // Lookup.TRUSTED
		);
		MethodHandles.Lookup caller = lookup.in(String.class);
		MethodHandle handle = caller.findConstructor(
				String.class, MethodType.methodType(void.class, char[].class, boolean.class)
		);
		CallSite callSite = LambdaMetafactory.metafactory(
				caller,
				"apply",
				MethodType.methodType(BiFunction.class),
				handle.type().generic(),
				handle,
				handle.type()
		);
		@SuppressWarnings("unchecked")
		BiFunction<char[], Boolean, String> f =
				(BiFunction<char[], Boolean, String>) callSite.getTarget().invokeExact();
		char[] values = new char[]{'h', 'e', 'l', 'l', 'o'};
		String result = f.apply(values, true);
		Assert.assertEquals("hello", result);
	}
}
