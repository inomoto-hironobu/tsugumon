package site.saishin.tsugumon;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import site.saishin.tsugumon.util.ByteBufferPoolFactory;

public class ByteBufferPoolTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		ByteBufferPoolFactory bbpool = new ByteBufferPoolFactory();

		ObjectPool<ByteBuffer> bufferPool = new GenericObjectPool<>(new ByteBufferPoolFactory());
		try {
			;
			System.out.println(bufferPool.getNumIdle());
			System.out.println(bufferPool.getNumActive());
			bufferPool.addObject();
			bufferPool.addObject();
			bufferPool.addObject();
			ByteBuffer bb = bufferPool.borrowObject();
			System.out.println(bb.get());
			ByteBuffer bb2 = bufferPool.borrowObject();
			System.out.println(bb2.get());
			System.out.println(bufferPool.getNumIdle());
			System.out.println(bufferPool.getNumActive());
			long start = System.nanoTime();
			for(int i = 0; i < 100_000; i++) {
				ByteBuffer bb3 = bufferPool.borrowObject();
				bufferPool.returnObject(bb3);
			}
			long end = System.nanoTime();
			
			System.out.println("end:"+(end-start));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		
	}

}
