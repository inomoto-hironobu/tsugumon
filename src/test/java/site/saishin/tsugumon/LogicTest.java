package site.saishin.tsugumon;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.entity.User;
import site.saishin.tsugumon.logic.TsugumonLogic;

public class LogicTest {

	TsugumonLogic logic;
	Set<String> availableUsers;
	MemcachedClient mclient;
	EntityManager em;

	public LogicTest() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Before
	public void setUp() throws IOException {
		availableUsers = new HashSet<String>();
		mclient = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
		em = Persistence.createEntityManagerFactory("test").createEntityManager();
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {

			}

			@Provides
			EntityManager provides() {
				return em;
			}
			@Provides
			Set<String> available() {
				return availableUsers;
			}
			@Provides
			MemcachedClient mem() {
				return mclient;
			}
		});
		logic = injector.getInstance(TsugumonLogic.class);
		mclient.flush();
	}

	@After
	public void tearDown() {
		em.close();
		mclient.shutdown();
		availableUsers.clear();
	}

	@Test
	public void testGetEntity() {
		logic.getUserByIpAddress("127.0.0.1").ifPresent(u -> {
			assertEquals(u.id.longValue(), 1L);
			logic.getEnqueteByUser(u).ifPresent(e -> {
				assertEquals(e.id.longValue(), 1L);
			});;
		});
		
	}
	@Test
	public void testGet() {
		String addr = "127.0.0.1";
	}

	@Test
	public void test2() {
	
		// assertTrue(result.isPresent());
	}

	@Test
	public void testSendEnquete() {

	}
}
