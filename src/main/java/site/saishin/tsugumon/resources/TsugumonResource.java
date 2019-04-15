package site.saishin.tsugumon.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.logic.TransactionLogic;
import site.saishin.tsugumon.logic.TsugumonLogic;
import site.saishin.tsugumon.model.EnqueteModel;
import site.saishin.tsugumon.model.HomeModel;
import site.saishin.tsugumon.model.UserModel;
import site.saishin.tsugumon.util.AccessManager;
import site.saishin.tsugumon.util.AccessManager.Strategy;
import site.saishin.tsugumon.util.ByteBufferPoolFactory;

@Singleton
@Path("/")
public class TsugumonResource {

	private static final Logger logger = LoggerFactory.getLogger(TsugumonResource.class);

	@Context
	private ServletConfig sconfig;
	private TsugumonLogic logic;
	private TransactionLogic transactionLogic;
	private AccessManager accessManager;
	private Set<String> proxys;
	private ObjectPool<ByteBuffer> bufferPool = new GenericObjectPool<>(new ByteBufferPoolFactory());

	public TsugumonResource() {
		logger.info("construct");
	}

	@PostConstruct
	public void pc() throws IOException, Exception {
		logger.info("post construct");
		ServletContext context = sconfig.getServletContext();
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				
			}

			@Provides
			EntityManager provides() {
				return Persistence.createEntityManagerFactory("tsugumon").createEntityManager();
			}
		});
		logic = injector.getInstance(TsugumonLogic.class);
		accessManager = (AccessManager) context.getAttribute(TsugumonConstants.ACCESS_MANAGER_NAME);
		bufferPool.addObject();
	}

	@PreDestroy
	public void pd() {

	}

	@GET
	@Path("ipaddress")
	@Produces(MediaType.TEXT_PLAIN)
	public String ipaddress(@Context final HttpServletRequest req) {
		return getAddr(req);
	}

	@GET
	@Path("systeminfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSystemInfo(@Context final HttpServletRequest req) {
		return accessOnManagement(req, Strategy.LONG, addr ->{
			return Response.ok(sconfig.getServletContext().getAttribute(TsugumonConstants.BASE_DATA_INFO_NAME)).build();
		});
	}

	@GET
	@Path("dealtEnquete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDealtEnquete(@Context final HttpServletRequest req) {
		return accessOnManagement(req, Strategy.LONG, (addr) -> {
			Optional<EnqueteModel> dealt = logic.getDealtEnqueteModel();
			if (dealt.isPresent()) {
				return Response.ok(dealt.get()).build();
			} else {
				return TsugumonConstants.NOT_FOUND_RESPONSE;
			}
		});
	}

	@GET
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@Context final HttpServletRequest req) {
		return accessOnManagement(req, Strategy.LONG, (addr) -> {
			Optional<UserModel> ret = logic.getUserModel(addr);
			if (ret.isPresent()) {
				UserModel model = ret.get();
				model.getAvailable();
				model.setAccessed(accessManager.count(req.getRemoteAddr()));
				return Response.ok(model).build();
			} else {
				return TsugumonConstants.NOT_FOUND_RESPONSE;
			}
		});
	}

	@GET
	@Path("enquete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnquete(@Context final HttpServletRequest req, @PathParam("id") final Long enqueteId) {
		return accessOnManagement(req, Strategy.MIDDLE, addr -> {
			Optional<EnqueteModel> emo = logic.getEnqueteModel(enqueteId, addr);
			if (emo.isPresent()) {
				return Response.ok(emo.get()).build();
			} else {
				return TsugumonConstants.NOT_FOUND_RESPONSE;
			}
		}); 
	}

	@GET
	@Path("home")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHome(@Context final HttpServletRequest req) {
		return accessOnManagement(req, Strategy.MIDDLE, (addr) -> {
			return Response
					.ok(logic.getHome(req.getRemoteAddr())
					.orElseGet(() -> {
						return new HomeModel();
					})).build();
			});
	}

	@GET
	@Path("search/{keyword}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context final HttpServletRequest req, @PathParam("keyword") final String keyword,
			@PathParam("page") final int page) {
		return accessOnManagement(req, Strategy.MIDDLE, (addr)->{
			if (page < 0) {
				return TsugumonConstants.FORBIDDEN_RESPONSE;
			}
			return Response.ok(logic.search(keyword, page)).build();
		});
	}

	@GET
	@Path("ranking/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRanking(@Context final HttpServletRequest req, @PathParam("page") final int page) {
		return accessOnManagement(req, Strategy.MIDDLE, (addr) -> {
			if (page < 0) {
				return TsugumonConstants.NOT_FOUND_RESPONSE;
			}
			return Response.ok(logic.getRank(page)).build();
		}); 
	}

	@POST
	@Path("enquete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEnquete(@Context final HttpServletRequest req, final InputStream in) {
		return accessOnManagement(req, Strategy.SHORT, (addr) -> {
			try {
				ByteBuffer buffer = bufferPool.borrowObject();
				int read;
				if ((read = in.read(buffer.array())) > 0 && in.read() < 0) {
					buffer.position(read - 1);
				} else {
					return TsugumonConstants.BAD_REQUEST_RESPONSE;
				}
				bufferPool.returnObject(buffer);
				return transactionLogic.createEnquete(addr, buffer).orElse(TsugumonConstants.OK_RESPONSE);
			} catch (IOException | NoSuchElementException | IllegalStateException e) {
				logger.error(e.getMessage(), e);
				return TsugumonConstants.SERVER_ERROR_RESPONSE;
			} catch (Exception e) {
				return TsugumonConstants.SERVER_ERROR_RESPONSE;
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					return TsugumonConstants.SERVER_ERROR_RESPONSE;
				}
			}
		});
	}

	@DELETE
	@Path("enquete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEnquete(@Context final HttpServletRequest req) {
		return accessOnManagement(req, Strategy.SHORT, (addr) -> {
			return transactionLogic.deleteEnquete(addr).orElse(TsugumonConstants.OK_RESPONSE);
		});
	}

	@PUT
	@Path("answer")
	@Produces(MediaType.APPLICATION_JSON)
	public Response putAnswer(@Context final HttpServletRequest req, @FormParam("enqueteId") final long enqueteId,
			@FormParam("entry") int entry) {
		return accessOnManagement(req, Strategy.SHORT, (addr) -> {
			logger.debug("enquete:" + enqueteId + "; entry:" + entry);

			return transactionLogic.changeAnswer(addr, enqueteId, entry).orElse(TsugumonConstants.OK_RESPONSE);
		});
	}

	@DELETE
	@Path("answer/{enqueteId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAnswer(@Context final HttpServletRequest req, @PathParam("enqueteId") final Long enqueteId) {
		return accessOnManagement(req, Strategy.SHORT, (addr) -> {
			return transactionLogic.deleteAnswer(addr, enqueteId)
					.orElse(TsugumonConstants.OK_RESPONSE);
		});
	}

	private Response accessOnManagement(HttpServletRequest req, Strategy strategy, Function<String, Response> func) {
		if (accessManager.access(getAddr(req), strategy)) {
			return func.apply(getAddr(req));
		} else {
			return TsugumonConstants.FORBIDDEN_RESPONSE;
		}
	}
	public String getAddr(HttpServletRequest req) {
		String xff = req.getHeader("X-Forwarded-For");
		if(xff != null) {
			logger.info(xff);
			proxys.add(req.getRemoteAddr());
			return xff;
		}
		return req.getRemoteAddr();
	}
}
