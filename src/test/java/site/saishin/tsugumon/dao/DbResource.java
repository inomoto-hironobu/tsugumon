package site.saishin.tsugumon.dao;

import org.junit.rules.ExternalResource;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.TransactionManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mysql.jdbc.Driver;

import site.saishin.tsugumon.dao.setting.AppConfig;
import site.saishin.tsugumon.dao.setting.TsugumonDao;
import site.saishin.tsugumon.dao.setting.TsugumonDaoImpl;

public class DbResource extends ExternalResource {

	private AppConfig app;
	private TsugumonDao dao;

	@Override
	protected void before() throws Throwable {
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(AppConfig.class).in(Singleton.class);
			}

			@Provides
			private LocalTransactionDataSource getDataSource() {
				return new LocalTransactionDataSource("jdbc:mysql://localhost/tsugumon-test?useUnicode=true&characterEncoding=utf8", "root", "7)UxXkQT");
			}
			@Provides
			private Dialect getDialect() {
				return new MysqlDialect();
			}
		});
		
		app = injector.getInstance(AppConfig.class);
		System.out.println(app.getDataSource());
		try {
			Driver driver = (Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println(driver);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		dao = new TsugumonDaoImpl(app);
		TransactionManager tx = app.getTransactionManager();
		tx.required(() -> {
			dao.create();
			dao.input();
		});
	}
	public AppConfig getAppConfig() {
		return app;
	}
	
	@Override
	protected void after() {
		TransactionManager tx = app.getTransactionManager();
		tx.required(() -> {
			dao.drop();
		});
	}
}
