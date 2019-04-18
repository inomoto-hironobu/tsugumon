package site.saishin.tsugumon;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.logic.TsugumonLogic;
import site.saishin.tsugumon.model.EnqueteModel;
import site.saishin.tsugumon.model.UserModel;
import site.saishin.tsugumon.model.Message;
import site.saishin.tsugumon.model.AttributeModel;
import site.saishin.tsugumon.resources.TsugumonResource;
import site.saishin.tsugumon.util.AccessManager;

public class TsugumonResourceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() {

		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				
			}
			@Provides
			private Set<String> getAccessUsers() {
				return accessUsers;
			}
		});
		tsugumonResource = injector.getInstance(TsugumonResource.class);
	}
	private AccessManager accessManager = new AccessManager();
	TsugumonResource tsugumonResource;
	Set<String> accessUsers = new HashSet<>();

	@After
	public void tearDown() {

	}

	@Test
	public void test() {

		HttpServletRequest existreq = Mockito.mock(HttpServletRequest.class);
		Mockito.when(existreq.getRemoteAddr()).thenReturn(Constants.existIpAddr);
		
		HttpServletRequest newreq = Mockito.mock(HttpServletRequest.class);
		Mockito.when(newreq.getRemoteAddr()).thenReturn("127.0.0.1");
		
		Response resp = tsugumonResource.getUser(existreq);
		assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
		Object entity = resp.getEntity();
		if(entity instanceof UserModel) {
			UserModel existres = (UserModel) resp.getEntity(); 
			assertThat(existres.getOwnEnquete().getId(), is(1L));
		} else {
			fail();
		}
		entity = resp.getEntity();
		if(entity instanceof List) {
			@SuppressWarnings("unchecked")
			List<EnqueteModel> emlist = (List<EnqueteModel>) entity;
			assertThat(emlist, notNullValue());
			assertThat(emlist.size(), is(not(0)));
			EnqueteModel em = emlist.get(0);
			assertThat(em.getDescription(), notNullValue());
		} else {
			fail();
		}
		
		String source = "{\"description\":\"test\",\"entries\":[{\"string\":\"test\",\"number\":1},{\"string\":\"test\",\"number\":2}]}";
		resp = tsugumonResource.createEnquete(existreq, IOUtils.toInputStream(source, Charset.forName("UTF-8")));
		assertThat(resp.getStatus(), is(Response.Status.CONFLICT.getStatusCode()));
		resp = tsugumonResource.deleteEnquete(existreq);
		assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
		StringBuilder sbuffer = new StringBuilder();
		sbuffer.append("{\"description\":\"test\",\"entries\":[");
		for(int i = 0; i < 1000; i++) {
			sbuffer.append("{\"string\":\"test\"},");
		}
		sbuffer.append("{\"string\":\"test\"}]}");

		resp = tsugumonResource.createEnquete(existreq, IOUtils.toInputStream(sbuffer.toString(), Charset.forName("UTF-8")));
		assertThat(resp.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
		resp = tsugumonResource.deleteAnswer(existreq, 2L);
		assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
		Mockito.when(existreq.getRemoteAddr()).thenReturn("192.168.10.5");
		assertFalse(tsugumonResource.deleteAnswer(existreq, 1L).getStatus() == Response.Status.OK.getStatusCode());


	}
}
