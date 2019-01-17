package site.saishin.tsugumon.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.google.inject.Inject;

import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.logic.TsugumonLogic;
import site.saishin.tsugumon.model.EnqueteModel;
import site.saishin.tsugumon.model.HomeModel;
import site.saishin.tsugumon.model.UserModel;
import site.saishin.tsugumon.util.AccessManager;
import site.saishin.tsugumon.util.AccessManager.Strategy;
import site.saishin.tsugumon.util.ByteBufferPool;

@Singleton
@Path("/")
public class TsugumonResource {

	private static final Logger logger = LoggerFactory.getLogger(TsugumonResource.class);

	@Context
	private ServletConfig sconfig;
	@Inject
	private TsugumonLogic logic;
	@Inject
	private AccessManager accessManager;
	private ObjectPool<ByteBuffer> bufferPool = new GenericObjectPool<>(new ByteBufferPool());
	public TsugumonResource() {
		logger.info("construct");
	}

	@PostConstruct
	public void pc() throws IOException, Exception {
		logger.info("post construct");
		ServletContext context = sconfig.getServletContext();
		logic = (TsugumonLogic) context.getAttribute(TsugumonConstants.LOGIC_NAME);
		accessManager = (AccessManager) context.getAttribute(TsugumonConstants.ACCESS_MANAGER_NAME);
		bufferPool.addObject();
	}

	@GET
	@Path("ipaddress")
	@Produces(MediaType.TEXT_PLAIN)
	public String ipaddress(@Context final HttpServletRequest req) {
		return req.getRemoteAddr();
	}

	@GET
	@Path("systeminfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSystemInfo(@Context final HttpServletRequest req) {
		if (accessManager.access(req.getRemoteAddr(), Strategy.LONG)) {
			return Response.ok(sconfig.getServletContext().getAttribute(TsugumonConstants.BASE_DATA_INFO_NAME)).build();
		} else {
			return TsugumonConstants.FORBIDDEN_RESPONSE;
		}
	}

	@GET
	@Path("dealtEnquete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDealtEnquete(@Context final HttpServletRequest req) {
		return access(req, Strategy.LONG,()->{
			Optional<EnqueteModel> dealt = logic.getDealtEnqueteModelAtTransaction();
			if(dealt.isPresent()) {
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
		return access(req, Strategy.LONG,()-> {
			Optional<UserModel> ret = logic.getUserAtTransaction(req.getRemoteAddr());
			if(ret.isPresent()) {
				UserModel model = ret.get();
				model.setAccessed(accessManager.count(req.getRemoteAddr()));
				return Response.ok(ret.get()).build();
			} else {
				return TsugumonConstants.NOT_FOUND_RESPONSE;
			}
		});
	}
	
	@GET
	@Path("enquete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnquete(@Context final HttpServletRequest req, @PathParam("id") final Long enqueteId) {
		if(accessManager.access(req.getRemoteAddr(), Strategy.MIDDLE)) {
			Optional<EnqueteModel> emo = logic.getEnqueteWithResultAtTransaction(enqueteId, req.getRemoteAddr());
			if(emo.isPresent()) {
				return Response.ok(emo.get()).build();
			} else {
				return TsugumonConstants.NOT_FOUND_RESPONSE;
			}
		} else {
			return TsugumonConstants.FORBIDDEN_RESPONSE;
		}
	}

	@GET
	@Path("home")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHome(@Context final HttpServletRequest req) {
		if(accessManager.access(req.getRemoteAddr(), Strategy.MIDDLE)) {
			return Response.ok(logic.getHomeAtTransaction(req.getRemoteAddr()).orElseGet(() -> {
				return new HomeModel();
			})).build();
		} else {
			return TsugumonConstants.FORBIDDEN_RESPONSE;
		}
	}

	@GET
	@Path("search/{keyword}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context final HttpServletRequest req, @PathParam("keyword") final String keyword, @PathParam("page") final int page) {
		if(accessManager.access(req.getRemoteAddr(), Strategy.MIDDLE)) {
			if (page < 0) {
				return TsugumonConstants.FORBIDDEN_RESPONSE;
			}
			return Response.ok(logic.searchWithTransaction(keyword, page)).build();
		} else {
			return TsugumonConstants.FORBIDDEN_RESPONSE;
		}
	}

	@GET
	@Path("ranking/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRanking(@Context final HttpServletRequest req, @PathParam("page") final int page) {
		if (accessManager.access(req.getRemoteAddr(), Strategy.MIDDLE)) {
			if (page < 0) {
				return TsugumonConstants.NOT_FOUND_RESPONSE;
			}
			return Response.ok(logic.rankWithTransaction(page)).build();
		} else {
			return TsugumonConstants.FORBIDDEN_RESPONSE;
		}

	}

	@POST
	@Path("enquete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEnquete(@Context final HttpServletRequest req, final InputStream in) {
		return atChange(req, ()->{
			try {
				ByteBuffer buffer = bufferPool.borrowObject();
				int read;
				if ((read = in.read(buffer.array())) > 0 && in.read() < 0) {
					buffer.position(read - 1);
				} else {
					return TsugumonConstants.BAD_REQUEST_RESPONSE;
				}
				bufferPool.returnObject(buffer);
				return logic.createEnquete(req.getRemoteAddr(), buffer).orElse(TsugumonConstants.OK_RESPONSE);
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
	public Response deleteEnquete(@Context final HttpServletRequest req) {
		return atChange(req,()-> {
			return logic.deleteEnqueteAtTransaction(req.getRemoteAddr()).orElse(TsugumonConstants.OK_RESPONSE);
		});
	}

	@PUT
	@Path("answer/{enqueteId}/{entry:[1-4]}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putAnswer(@Context final HttpServletRequest req, @PathParam("enqueteId") long enqueteId,
			@PathParam("entry") int entry) {
		return atChange(req, ()-> {
			logger.debug("enquete:" + enqueteId + "; entry:" + entry);
			return logic.putAnswer(req.getRemoteAddr(), enqueteId, entry).orElse(TsugumonConstants.OK_RESPONSE);
		});
	}

	@DELETE
	@Path("answer/{enqueteId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAnswer(@Context final HttpServletRequest req, @PathParam("enqueteId") final Long enqueteId) {
		return atChange(req,()-> {
			return logic.deleteAnswerAtTransaction(req.getRemoteAddr(), enqueteId).orElse(TsugumonConstants.OK_RESPONSE);
		});
	}

	private Response access(HttpServletRequest req, Strategy strategy, Supplier<Response> func) {
		if(accessManager.access(req.getRemoteAddr(), strategy)) {
			return func.get();
		} else {
			return TsugumonConstants.FORBIDDEN_RESPONSE;
		}
	}
	private Response atChange(HttpServletRequest req, Supplier<Response> func) {
		if(accessManager.access(req.getRemoteAddr(), Strategy.SHORT)) {
			return func.get();
		} else {
			return TsugumonConstants.FORBIDDEN_RESPONSE;
		}
	}
}
