package site.saishin.tsugumon;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import site.saishin.tsugumon.logic.LogicTest;
import site.saishin.tsugumon.resource.TsugumonResourceTest;
import site.saishin.tsugumon.util.AccessManagerTest;
import site.saishin.tsugumon.util.ByteBufferPoolTest;

@RunWith(Suite.class)
@SuiteClasses({
	AccessManagerTest.class,
	ByteBufferPoolTest.class,
	LogicTest.class,
	TsugumonResourceTest.class
})
public class AllTests {

}
