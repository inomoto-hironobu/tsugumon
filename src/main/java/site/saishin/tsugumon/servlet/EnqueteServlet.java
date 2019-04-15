package site.saishin.tsugumon.servlet;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Long id = Long.parseLong(request.getParameter("id"));
			Optional<EnqueteModel> ret = logic.getEnqueteModel(id, request.getRemoteAddr());
			if (ret.isPresent()) {
				request.setAttribute("enquete", ret.get());
				request.getRequestDispatcher("/WEB-INF/enquete.jsp").forward(request, response);
			} else {
				response.sendError(Status.NOT_FOUND.getStatusCode());
			}
		} catch (NumberFormatException e) {
			logger.debug(request.getServletPath());
			response.sendError(Status.NOT_FOUND.getStatusCode());
		}
	}
}
