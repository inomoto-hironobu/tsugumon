package site.saishin.tsugumon;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import site.saishin.tsugumon.entity.Enquete;
import site.saishin.tsugumon.entity.Language;

public class EntityTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		EntityManager em = Persistence.createEntityManagerFactory("test").createEntityManager();
		int i = em.createNamedQuery(Language.ALL).getResultList().size();
		System.out.println(i);
		assertEquals(1L, i);
		Long r = (Long) em.createNamedQuery(Enquete.COUNT_ALL).getSingleResult();
		System.out.println(r);
	}
}
