package site.saishin.tsugumon;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AccessManagerTest.class,
	ByteBufferPoolTest.class,
	LogicTest.class,
	TsugumonResourceTest.class
})
public class AllTests {

}
