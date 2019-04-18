package site.saishin.tsugumon.listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.entity.Answer;
import site.saishin.tsugumon.entity.Enquete;
import site.saishin.tsugumon.entity.User;
import site.saishin.tsugumon.model.BaseDataInfo;
import site.saishin.tsugumon.model.BaseDataInfo.Builder;
import site.saishin.tsugumon.util.AccessManager;
import site.saishin.tsugumon.util.Timer;

public class TsugumonContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(TsugumonContextListener.class);
	ScheduledExecutorService longCycleScheduler;
	ScheduledExecutorService middleCycleScheduler;
	ScheduledExecutorService shortCycleScheduler;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("inited");
		ServletContext context = event.getServletContext();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		
		MemcachedClient mclient;
		try {
			mclient = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		Set<String> proxies =  Collections.synchronizedSet(new HashSet<>());
		AccessManager accessManager = new AccessManager();
		Timer timer = new Timer();
		Set<String> availableUsers = new HashSet<>();
		//時間がくるごとに処理
		//長い時間
		longCycleScheduler = Executors.newSingleThreadScheduledExecutor();
		longCycleScheduler.scheduleAtFixedRate(()->{
			timer.setLongCycle(Instant.now());
			proxies.clear();
			EntityManager em = Persistence.createEntityManagerFactory("tsugumon").createEntityManager();
			Builder builder = new BaseDataInfo.Builder();
			builder.totalUser((Long) em.createNamedQuery(User.COUNT).getSingleResult());
			builder.totalEnquete((Long) em.createNamedQuery(Enquete.COUNT_ALL).getSingleResult());
			builder.totalAnswer((Long) em.createNamedQuery(Answer.COUNT_ALL).getSingleResult());
			context.setAttribute(TsugumonConstants.BASE_DATA_INFO, Response.ok(builder.build()).build());
			accessManager.clearLong();
			
		}, 0, TsugumonConstants.LONG_CYCLE_MINUTES, TimeUnit.MINUTES);
		//
		middleCycleScheduler = Executors.newSingleThreadScheduledExecutor();
		middleCycleScheduler.scheduleAtFixedRate(()->{
			timer.setMiddleCycle(Instant.now());
			accessManager.clearMiddle();
		}, 0, TsugumonConstants.MIDDLE_CYCLE_MINUTES, TimeUnit.MINUTES);
		//
		shortCycleScheduler = Executors.newSingleThreadScheduledExecutor();
		shortCycleScheduler.scheduleAtFixedRate(() -> {
			timer.setShortCycle(Instant.now());
			accessManager.clearShort();
			synchronized (availableUsers) {
				availableUsers.clear();
			}
		}, TsugumonConstants.SHORT_CYCLE_MINUTES, TsugumonConstants.SHORT_CYCLE_MINUTES, TimeUnit.MINUTES);

		context.setAttribute("timer", timer);
		context.setAttribute(TsugumonConstants.PROXIES_NAME, proxies);
		context.setAttribute(TsugumonConstants.ACCESS_MANAGER_NAME, accessManager);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		shortCycleScheduler.shutdown();
		middleCycleScheduler.shutdown();
		longCycleScheduler.shutdown();
	}
}