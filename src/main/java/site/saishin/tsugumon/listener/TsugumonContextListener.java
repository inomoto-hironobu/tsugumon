package site.saishin.tsugumon.listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import net.spy.memcached.MemcachedClient;
import site.saishin.tsugumon.TsugumonConstants;
import site.saishin.tsugumon.util.AccessManager;
import site.saishin.tsugumon.util.BaseDataInfo;
import site.saishin.tsugumon.util.BaseDataInfo.Builder;
import site.saishin.tsugumon.util.Timer;

public class TsugumonContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(TsugumonContextListener.class);
	ScheduledExecutorService longCycleScheduler;
	ScheduledExecutorService middleCycleScheduler;
	ScheduledExecutorService shortCycleScheduler;
	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}	
	public void contextInitialized(ServletContextEvent event) {
		logger.info("inited");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				
			}
		});
		MemcachedClient mclient = null;
		try {
			mclient = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		AccessManager accessManager = new AccessManager();
		Timer timer = new Timer();
		Set<String> availableUsers = new HashSet<>();
		//時間がくるごとに処理
		//長い時間
		longCycleScheduler = Executors.newSingleThreadScheduledExecutor();
		longCycleScheduler.scheduleAtFixedRate(()->{
			timer.setLongCycle(Instant.now());
			Builder builder = new BaseDataInfo.Builder();
			
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

		event.getServletContext().setAttribute("timer", timer);

		event.getServletContext().setAttribute(TsugumonConstants.ACCESS_MANAGER_NAME, accessManager);
	}

	public void contextDestroyed(ServletContextEvent event) {
		shortCycleScheduler.shutdown();
		middleCycleScheduler.shutdown();
		longCycleScheduler.shutdown();
	}
}