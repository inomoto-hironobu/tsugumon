package site.saishin.tsugumon;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import site.saishin.tsugumon.logic.TransactionLogic;
import site.saishin.tsugumon.model.UserModel;

public class TransactionLogicTest {

	TransactionLogic logic;
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
		
	}
	@Test
	public void testTransaction() {
		String addr = "127.0.0.1";
		//
		Optional<Response> ret = logic.deleteAnswer(addr, 100L);
		assertThat(ret.isPresent(), is(true));
		assertThat(ret.get().getStatus(), is(Status.NOT_FOUND.getStatusCode()));
		// 投票の削除
		ret = logic.deleteAnswer(addr, 1L);
		assertThat(ret.isPresent(), is(false));

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
}
