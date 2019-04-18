package site.saishin.tsugumon.resources;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.TsugumonUtil;

@Singleton
@Path("/sys")
public class SystemResource {
	@Context
	private ServletConfig sconfig;
	private Set<String> proxys;
	@PostConstruct
	public void pc() {
		proxys = (Set<String>) sconfig.getServletContext().getAttribute(TsugumonConstants.PROXIES_NAME);
	}
	@GET
	@Path("ipaddress")
	@Produces(MediaType.TEXT_PLAIN)
	public String ipaddress(@Context final HttpServletRequest req) {
		return TsugumonUtil.getAddr(req, proxys);
	}

	@GET
	@Path("systeminfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSystemInfo(@Context final HttpServletRequest req) {
		return (Response) sconfig.getServletContext().getAttribute(TsugumonConstants.BASE_DATA_INFO);
	}

}
