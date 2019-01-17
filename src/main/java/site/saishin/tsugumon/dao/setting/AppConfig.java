package site.saishin.tsugumon.dao.setting;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.seasar.doma.jdbc.tx.TransactionManager;

public class AppConfig implements Config {

	@Inject
	private Dialect dialect;

	@Inject
	private LocalTransactionDataSource dataSource;

	private TransactionManager transactionManager;

	@Override
	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(LocalTransactionDataSource dataSource) {
		this.dataSource = dataSource;
	}
	@Override
	public TransactionManager getTransactionManager() {
		if(transactionManager == null) {
			transactionManager = new LocalTransactionManager(dataSource.getLocalTransaction(getJdbcLogger()));
		}
		return transactionManager;
	}
}
