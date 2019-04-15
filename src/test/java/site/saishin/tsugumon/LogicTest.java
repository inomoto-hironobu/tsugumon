package site.saishin.tsugumon;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
import site.saishin.tsugumon.model.EnqueteModel;
import site.saishin.tsugumon.model.HomeModel;
import site.saishin.tsugumon.model.UserModel;

public class LogicTest {

	TsugumonLogic logic;
	Set<String> availableUsers;
	MemcachedClient mclient;
	EntityManager em;

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
	public void testGet() {
		String addr = "127.0.0.1";
		Optional<HomeModel> homeOpt = logic.getHome(addr);
		assertThat(homeOpt.isPresent(), is(true));
		HomeModel home = homeOpt.get();
		assertThat(home.getAnswers().size(), is(5));
		assertThat(home.getOwnEnquete().getId(), is(1L));
		//
		Optional<UserModel> umo = logic.getUserModel(addr);
		assertEquals(addr, umo.get().getIpAddress());
		assertThat(umo.get().getIpAddress(), is(addr));
		//
		Optional<EnqueteModel> deo = logic.getDealtEnqueteModel();
		assertThat(deo.isPresent(), is(true));
		assertThat(deo.get().getId(), is(11L));
		// Memcachedの影響を見るため２回同じ動作を行う
		final Optional<HomeModel> homeOpt2 = logic.getHome(addr);
		assertThat(homeOpt2.isPresent(), is(true));
		home = homeOpt2.get();
		assertThat(home.getAnswers().size(), is(5));
		//
		List<EnqueteModel> list = logic.getRank(0);
		assertThat(list.size(), is(11));
		list = logic.getRank(1);
		assertThat(list.size(), is(0));
		User user = new User(addr);
		user.ipAddress = "127.0.0.1";
		Optional<UserModel> optuser = logic.getUserModel(addr);
		assertTrue(optuser.isPresent());
	}

	@Test
	public void test2() {
	
		// assertTrue(result.isPresent());
	}

	@Test
	public void testTransaction() {
		String addr = "127.0.0.1";
		//
		Optional<Response> ret = logic.deleteAnswerAtTransaction(addr, 100L);
		assertThat(ret.isPresent(), is(true));
		assertThat(ret.get().getStatus(), is(Status.NOT_FOUND.getStatusCode()));
		// 投票の削除
		ret = logic.deleteAnswerAtTransaction(addr, 1L);
		assertThat(ret.isPresent(), is(false));
		Optional<HomeModel> homeOpt = logic.getHome(addr);
		HomeModel home = homeOpt.get();
		assertThat(home.getAnswers().size(), is(4));
		logic.getEnqueteModel(1L, addr);
		//
		logic.changeAnswer(addr, 1L, 1);
		// すでにアンケートが存在する状態でアンケートを送る
		PipedOutputStream out = new PipedOutputStream();
		try (OutputStreamWriter o = new OutputStreamWriter(out)) {
			PipedInputStream in = new PipedInputStream();
			out.connect(in);
			ExecutorService eservice = Executors.newSingleThreadExecutor();
			eservice.execute(() -> {
				try {
					o.write("{\"description\":\"test\"," + "\"entries\":" + "[{\"number\":1,\"string\":\"test\"},"
							+ "{\"number\":2,\"string\":\"test\"}]" + "}");
					o.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
			ByteBuffer buffer = ByteBuffer.allocate(5000);
			in.read(buffer.array());
			ret = logic.createEnquete(addr, buffer);
			assertThat(ret.isPresent(), is(true));
			assertThat(ret.get().getStatus(), is(Status.CONFLICT.getStatusCode()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// アンケートを削除して
		logic.deleteEnquete(addr);
		out = new PipedOutputStream();
		try (OutputStreamWriter o = new OutputStreamWriter(out)) {
			PipedInputStream in = new PipedInputStream();
			out.connect(in);
			ExecutorService eservice = Executors.newSingleThreadExecutor();
			eservice.execute(() -> {
				try {
					o.write("{\"description\":\"test\"," + "\"entries\":" + "[{\"number\":1,\"string\":\"test\"},"
							+ "{\"number\":2,\"string\":\"test\"}]" + "}");
					o.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
			ByteBuffer buffer = ByteBuffer.allocate(5000);
			in.read(buffer.array());
			ret = logic.createEnquete(addr, buffer);
			assertThat(ret.isPresent(), is(false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSendEnquete() {

	}
}
