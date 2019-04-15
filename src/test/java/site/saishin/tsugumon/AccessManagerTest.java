package site.saishin.tsugumon;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import site.saishin.tsugumon.util.AccessManager;
import site.saishin.tsugumon.util.AccessManager.Strategy;

public class AccessManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	AccessManager accessManager = new AccessManager();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAccess() {
		String addr = "test";
		assertThat(accessManager.access(addr, Strategy.SHORT), is(true));
	}
}
