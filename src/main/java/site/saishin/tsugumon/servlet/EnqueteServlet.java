package site.saishin.tsugumon.servlet;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.TsugumonUtil;
import site.saishin.tsugumon.entity.Enquete;
import site.saishin.tsugumon.entity.User;
import site.saishin.tsugumon.logic.TsugumonLogic;
import site.saishin.tsugumon.model.EnqueteModel;

/**
 * Enquete
 */
public class EnqueteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(EnqueteServlet.class);
	@Inject
	private TsugumonLogic logic;
	private Set<String> proxys;
	private Set<String> availableUsers;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EnqueteServlet() throws ClassNotFoundException {
		super();
		logger.info("EnqueteServlet constructed");
	}
	/**
	 * @see HttpServlet#init(ServletConfig config)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("EnqueteServlete init");
		ServletContext context = config.getServletContext();
		availableUsers = (Set<String>) context.getAttribute(TsugumonConstants.ACCESS_USERS);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idp = request.getParameter("id");
		if(idp == null) {
			response.sendError(Status.BAD_REQUEST.getStatusCode());
		} else {
			try {
				Long id = Long.parseLong(idp);
				Optional<Enquete> ret = logic.getEnqueteById(id);
				if (ret.isPresent()) {
					EnqueteModel model = new EnqueteModel(ret.get());
					String addr = TsugumonUtil.getAddr(request, proxys);
					Optional<User> useropt = logic.getUserByIpAddress(addr);
					boolean avail = false;
					if(useropt.isPresent()) {
						avail = TsugumonUtil.checkAvailable(addr, logic.countAnswers(useropt.get()), availableUsers);
						if(avail) {
							model.getEntries().forEach(e -> {
								e.setQuantity(logic.countAnswersByEntry(e.getId()));
							});
						}
					}
					request.setAttribute("enquete", new EnqueteModel(ret.get()));
					request.getRequestDispatcher("/WEB-INF/enquete.jsp").forward(request, response);
				} else {
					response.sendError(Status.NOT_FOUND.getStatusCode());
				}
			} catch (NumberFormatException e) {
				logger.debug(request.getServletPath());
				response.sendError(Status.BAD_REQUEST.getStatusCode());
			}
		}
	}
}
