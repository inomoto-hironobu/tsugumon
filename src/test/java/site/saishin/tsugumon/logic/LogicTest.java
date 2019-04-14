package site.saishin.tsugumon.logic;

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

import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.entity.User;
import site.saishin.tsugumon.model.EnqueteModel;
import site.saishin.tsugumon.model.HomeModel;
import site.saishin.tsugumon.model.UserModel;

public class LogicTest {
	
	TsugumonLogic logic;
	Set<String> availableUsers;
	MemcachedClient mclient;
	@Before
	public void setUp() throws IOException {
		availableUsers = new HashSet<String>();
		mclient = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
		logic = new TsugumonLogic(availableUsers, mclient);
		logic.em = Persistence.createEntityManagerFactory("test").createEntityManager();
		mclient.flush();
	}

	@After
	public void tearDown() {
		logic.em.close();
		mclient.shutdown();
		availableUsers.clear();
	}

	@Test
	public void test2() {
		
		Optional<Response> result = logic.asValidUser("test", (s) -> {
			s.id;
			
			return null;
		});
		assertTrue(result.isPresent());
	}
	void assertEnquete(EnqueteModel em) {
		assertTrue(em.getDescription() != null);
	}
	@SuppressWarnings("resource")
	@Test
	public void test() {
		
		String addr = "127.0.0.1";
		Optional<HomeModel> homeOpt = logic.getHomeAtTransaction(addr);
		assertThat(homeOpt.isPresent(), is(true));
		HomeModel home = homeOpt.get();
		assertThat(home.getAnswers().size(), is(5));
		assertThat(home.getOwnEnquete().getId(), is(1L));
		//
		Optional<UserModel> umo = logic.getUserAtTransaction(addr);
		assertEquals(addr, umo.get().getIpAddress());
		assertThat(umo.get().getIpAddress(), is(addr));
		//
		Optional<EnqueteModel> deo = logic.getDealtEnqueteModelAtTransaction();
		assertThat(deo.isPresent(), is(true));
		assertThat(deo.get().getId(), is(11L));
		//Memcachedの影響を見るため２回同じ動作を行う
		final Optional<HomeModel> homeOpt2 = logic.getHomeAtTransaction(addr);
		assertThat(homeOpt2.isPresent(), is(true));
		home = homeOpt2.get();
		assertThat(home.getAnswers().size(), is(5));
		//
		List<EnqueteModel> list = logic.rankWithTransaction(0);
		assertThat(list.size(), is(11));
		list = logic.rankWithTransaction(1);
		assertThat(list.size(), is(0));
		User user = new User(addr);
		user.ipAddress = "127.0.0.1";
		Optional<UserModel> optuser = logic.getUserAtTransaction(addr);

		//
		Optional<Response> ret = logic.deleteAnswerAtTransaction(addr, 100L);
		assertThat(ret.isPresent(), is(true));
		assertThat(ret.get().getStatus(), is(Status.NOT_FOUND.getStatusCode()));
		//投票の削除
		ret = logic.deleteAnswerAtTransaction(addr, 1L);
		assertThat(ret.isPresent(), is(false));
		homeOpt = logic.getHomeAtTransaction(addr);
		home = homeOpt.get();
		assertThat(home.getAnswers().size(), is(4));
		logic.getEnqueteWithResultAtTransaction(1L, addr);
		//
		logic.putAnswer(addr, 1L, 1);
		//すでにアンケートが存在する状態でアンケートを送る
		PipedOutputStream out = new PipedOutputStream();
		try (OutputStreamWriter o = new OutputStreamWriter(out)) {
			PipedInputStream in = new PipedInputStream();
			out.connect(in);
			ExecutorService eservice = Executors.newSingleThreadExecutor();
			eservice.execute(()->{		
				try {
					o.write("{\"description\":\"test\","
							+ "\"entries\":"
							+ "[{\"number\":1,\"string\":\"test\"},"
							+ "{\"number\":2,\"string\":\"test\"}]"
							+ "}");
					o.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			});
			ByteBuffer buffer = ByteBuffer.allocate(5000);
			in.read(buffer.array());
			ret = logic.createEnquete(addr, buffer);
			assertThat(ret.isPresent(),is(true));
			assertThat(ret.get().getStatus(), is(Status.CONFLICT.getStatusCode()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//アンケートを削除して
		logic.deleteEnqueteAtTransaction(addr);
		out = new PipedOutputStream();
		try (OutputStreamWriter o = new OutputStreamWriter(out)) {
			PipedInputStream in = new PipedInputStream();
			out.connect(in);
			ExecutorService eservice = Executors.newSingleThreadExecutor();
			eservice.execute(()->{		
				try {
					o.write("{\"description\":\"test\","
							+ "\"entries\":"
							+ "[{\"number\":1,\"string\":\"test\"},"
							+ "{\"number\":2,\"string\":\"test\"}]"
							+ "}");
					o.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			});
			ByteBuffer buffer = ByteBuffer.allocate(5000);
			in.read(buffer.array());
			ret = logic.createEnquete(addr, buffer);
			assertThat(ret.isPresent(),is(false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testSendEnquete() {
		
	}
}
